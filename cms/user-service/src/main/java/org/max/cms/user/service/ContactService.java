package org.max.cms.user.service;

import org.max.cms.user.entity.Contact;
import java.util.List;

public interface ContactService {
    
    /**
     * 创建联系方式
     */
    Contact createContact(Contact contact);
    
    /**
     * 更新联系方式
     */
    Contact updateContact(Contact contact);
    
    /**
     * 删除联系方式
     */
    void deleteContact(Long id);
    
    /**
     * 根据ID获取联系方式
     */
    Contact getContactById(Long id);
    
    /**
     * 根据员工ID获取联系方式列表
     */
    List<Contact> getContactsByEmployeeId(Long employeeId);
    
    /**
     * 获取员工主要联系方式
     */
    Contact getPrimaryContactByEmployeeId(Long employeeId);
    
    /**
     * 根据员工ID和联系方式类型获取联系信息
     */
    List<Contact> getContactsByEmployeeIdAndType(Long employeeId, String contactType);
    
    /**
     * 设置主要联系方式
     */
    void setPrimaryContact(Long employeeId, Long contactId);
    
    /**
     * 批量创建联系方式
     */
    int batchCreateContacts(List<Contact> contacts);
    
    /**
     * 删除员工所有联系方式
     */
    void deleteContactsByEmployeeId(Long employeeId);
}