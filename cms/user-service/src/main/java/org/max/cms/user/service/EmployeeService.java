package org.max.cms.user.service;

import org.max.cms.user.entity.Employee;
import java.util.List;

public interface EmployeeService {
    
    /**
     * 创建员工信息
     */
    Employee createEmployee(Employee employee);
    
    /**
     * 更新员工信息
     */
    Employee updateEmployee(Employee employee);
    
    /**
     * 删除员工信息
     */
    void deleteEmployee(Long id);
    
    /**
     * 根据ID获取员工信息
     */
    Employee getEmployeeById(Long id);
    
    /**
     * 根据用户ID获取员工信息
     */
    Employee getEmployeeByUserId(Long userId);
    
    /**
     * 根据工号获取员工信息
     */
    Employee getEmployeeByEmployeeNumber(String employeeNumber);
    
    /**
     * 获取所有员工信息
     */
    List<Employee> getAllEmployees();
    
    /**
     * 根据部门ID获取员工列表
     */
    List<Employee> getEmployeesByDepartmentId(Long departmentId);
    
    /**
     * 根据状态获取员工列表
     */
    List<Employee> getEmployeesByStatus(String status);
    
    /**
     * 获取员工完整信息（包含用户、职级、薪资等信息）
     */
    Employee getEmployeeWithDetails(Long employeeId);
    
    /**
     * 分页查询员工信息
     */
    List<Employee> getEmployeesWithPagination(int page, int size);
    
    /**
     * 批量导入员工信息
     */
    int batchImportEmployees(List<Employee> employees);
}