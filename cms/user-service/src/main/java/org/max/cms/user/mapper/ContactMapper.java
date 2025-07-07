package org.max.cms.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.max.cms.user.entity.Contact;

import java.util.List;

@Mapper
public interface ContactMapper extends BaseMapper<Contact> {
    
    /**
     * 根据员工ID获取联系方式
     */
    @Select("SELECT * FROM contacts WHERE employee_id = #{employeeId}")
    List<Contact> findByEmployeeId(Long employeeId);
    
    /**
     * 获取员工主要联系方式
     */
    @Select("SELECT * FROM contacts WHERE employee_id = #{employeeId} AND is_primary = true LIMIT 1")
    Contact getPrimaryContactByEmployeeId(Long employeeId);
    
    /**
     * 根据联系方式类型获取联系信息
     */
    @Select("SELECT * FROM contacts WHERE employee_id = #{employeeId} AND contact_type = #{contactType}")
    List<Contact> findByEmployeeIdAndType(Long employeeId, String contactType);
}