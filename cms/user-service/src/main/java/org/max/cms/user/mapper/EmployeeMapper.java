package org.max.cms.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.max.cms.user.entity.Employee;

import java.util.List;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    
    /**
     * 根据用户ID查找员工信息
     */
    @Select("SELECT * FROM employees WHERE user_id = #{userId}")
    Employee findByUserId(Long userId);
    
    /**
     * 根据工号查找员工信息
     */
    @Select("SELECT * FROM employees WHERE employee_number = #{employeeNumber}")
    Employee findByEmployeeNumber(String employeeNumber);
    
    /**
     * 根据部门ID查找员工列表
     */
    List<Employee> findByDepartmentId(Long departmentId);
    
    /**
     * 根据状态查找员工列表
     */
    @Select("SELECT * FROM employees WHERE status = #{status}")
    List<Employee> findByStatus(String status);
    
    /**
     * 获取员工完整信息（包含用户、职级、薪资等信息）
     */
    Employee getEmployeeWithDetails(Long employeeId);
}