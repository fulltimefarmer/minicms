package org.max.cms.user.service;

import org.max.cms.user.entity.Department;
import java.util.List;

public interface DepartmentService {
    
    /**
     * 获取部门树形结构
     */
    List<Department> getDepartmentTree();
    
    /**
     * 创建部门
     */
    Department createDepartment(Department department);
    
    /**
     * 更新部门
     */
    Department updateDepartment(Department department);
    
    /**
     * 删除部门
     */
    void deleteDepartment(Long id);
    
    /**
     * 根据ID获取部门
     */
    Department getDepartmentById(Long id);
    
    /**
     * 获取所有部门列表
     */
    List<Department> getAllDepartments();
    
    /**
     * 根据父ID获取子部门
     */
    List<Department> getChildDepartments(Long parentId);
    
    /**
     * 检查部门是否存在用户
     */
    boolean hasDepartmentUsers(Long departmentId);
}