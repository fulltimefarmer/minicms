package org.max.cms.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.max.cms.user.entity.Contact;
import org.max.cms.user.entity.Employee;
import org.max.cms.user.mapper.ContactMapper;
import org.max.cms.user.mapper.EmployeeMapper;
import org.max.cms.user.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactMapper contactMapper;
    
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public Contact createContact(Contact contact) {
        contactMapper.insert(contact);
        return contact;
    }

    @Override
    public Contact updateContact(Contact contact) {
        contactMapper.updateById(contact);
        return contact;
    }

    @Override
    public void deleteContact(Long id) {
        // 软删除
        Contact contact = new Contact();
        contact.setId(id);
        contact.setDeleted(true);
        contactMapper.updateById(contact);
    }

    @Override
    public Contact getContactById(Long id) {
        Contact contact = contactMapper.selectById(id);
        if (contact != null) {
            loadRelatedData(contact);
        }
        return contact;
    }

    @Override
    public List<Contact> getContactsByEmployeeId(Long employeeId) {
        return contactMapper.findByEmployeeId(employeeId);
    }

    @Override
    public Contact getPrimaryContactByEmployeeId(Long employeeId) {
        Contact contact = contactMapper.getPrimaryContactByEmployeeId(employeeId);
        if (contact != null) {
            loadRelatedData(contact);
        }
        return contact;
    }

    @Override
    public List<Contact> getContactsByEmployeeIdAndType(Long employeeId, String contactType) {
        return contactMapper.findByEmployeeIdAndType(employeeId, contactType);
    }

    @Override
    public void setPrimaryContact(Long employeeId, Long contactId) {
        // 先将该员工的所有联系方式设置为非主要
        QueryWrapper<Contact> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employee_id", employeeId);
        List<Contact> contacts = contactMapper.selectList(queryWrapper);
        
        for (Contact contact : contacts) {
            contact.setIsPrimary(false);
            contactMapper.updateById(contact);
        }
        
        // 设置指定联系方式为主要
        Contact primaryContact = contactMapper.selectById(contactId);
        if (primaryContact != null && primaryContact.getEmployeeId().equals(employeeId)) {
            primaryContact.setIsPrimary(true);
            contactMapper.updateById(primaryContact);
        }
    }

    @Override
    public int batchCreateContacts(List<Contact> contacts) {
        int count = 0;
        for (Contact contact : contacts) {
            try {
                contactMapper.insert(contact);
                count++;
            } catch (Exception e) {
                // 记录错误日志，继续处理下一个
                // log.error("创建联系方式失败: {}", contact.getEmployeeId(), e);
            }
        }
        return count;
    }

    @Override
    public void deleteContactsByEmployeeId(Long employeeId) {
        QueryWrapper<Contact> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employee_id", employeeId);
        List<Contact> contacts = contactMapper.selectList(queryWrapper);
        
        for (Contact contact : contacts) {
            contact.setDeleted(true);
            contactMapper.updateById(contact);
        }
    }
    
    private void loadRelatedData(Contact contact) {
        // 加载员工信息
        if (contact.getEmployeeId() != null) {
            Employee employee = employeeMapper.selectById(contact.getEmployeeId());
            contact.setEmployee(employee);
        }
    }
}