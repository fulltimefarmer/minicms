package org.max.cms.user.controller;

import org.max.cms.user.entity.Contact;
import org.max.cms.user.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "*")
public class ContactController {

    @Autowired
    private ContactService contactService;

    /**
     * 根据ID获取联系方式
     */
    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        Contact contact = contactService.getContactById(id);
        if (contact != null) {
            return ResponseEntity.ok(contact);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 根据员工ID获取联系方式列表
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Contact>> getContactsByEmployeeId(@PathVariable Long employeeId) {
        List<Contact> contacts = contactService.getContactsByEmployeeId(employeeId);
        return ResponseEntity.ok(contacts);
    }

    /**
     * 获取员工主要联系方式
     */
    @GetMapping("/employee/{employeeId}/primary")
    public ResponseEntity<Contact> getPrimaryContactByEmployeeId(@PathVariable Long employeeId) {
        Contact contact = contactService.getPrimaryContactByEmployeeId(employeeId);
        if (contact != null) {
            return ResponseEntity.ok(contact);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 根据员工ID和联系方式类型获取联系信息
     */
    @GetMapping("/employee/{employeeId}/type/{contactType}")
    public ResponseEntity<List<Contact>> getContactsByEmployeeIdAndType(
            @PathVariable Long employeeId, 
            @PathVariable String contactType) {
        List<Contact> contacts = contactService.getContactsByEmployeeIdAndType(employeeId, contactType);
        return ResponseEntity.ok(contacts);
    }

    /**
     * 创建联系方式
     */
    @PostMapping
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        try {
            Contact created = contactService.createContact(contact);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新联系方式
     */
    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable Long id, @RequestBody Contact contact) {
        try {
            contact.setId(id);
            Contact updated = contactService.updateContact(contact);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除联系方式
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        try {
            contactService.deleteContact(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 设置主要联系方式
     */
    @PutMapping("/employee/{employeeId}/primary/{contactId}")
    public ResponseEntity<Void> setPrimaryContact(@PathVariable Long employeeId, @PathVariable Long contactId) {
        try {
            contactService.setPrimaryContact(employeeId, contactId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 批量创建联系方式
     */
    @PostMapping("/batch")
    public ResponseEntity<String> batchCreateContacts(@RequestBody List<Contact> contacts) {
        try {
            int count = contactService.batchCreateContacts(contacts);
            return ResponseEntity.ok("成功创建 " + count + " 个联系方式");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("批量创建失败: " + e.getMessage());
        }
    }

    /**
     * 删除员工所有联系方式
     */
    @DeleteMapping("/employee/{employeeId}")
    public ResponseEntity<Void> deleteContactsByEmployeeId(@PathVariable Long employeeId) {
        try {
            contactService.deleteContactsByEmployeeId(employeeId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}