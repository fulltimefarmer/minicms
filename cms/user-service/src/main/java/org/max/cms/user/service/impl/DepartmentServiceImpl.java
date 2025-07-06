package org.max.cms.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.max.cms.user.entity.Department;
import org.max.cms.user.entity.User;
import org.max.cms.user.mapper.DepartmentMapper;
import org.max.cms.user.mapper.UserMapper;
import org.max.cms.user.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Department> getDepartmentTree() {
        List<Department> allDepartments = departmentMapper.selectList(
            new QueryWrapper<Department>()
                .eq("enabled", true)
                .eq("deleted", false)
                .orderByAsc("sort", "created_at")
        );
        
        return buildTree(allDepartments, null);
    }

    @Override
    public Department createDepartment(Department department) {
        // 设置部门路径和层级
        if (department.getParentId() != null) {
            Department parent = departmentMapper.selectById(department.getParentId());
            if (parent != null) {
                department.setPath(parent.getPath() + "/" + department.getCode());
                department.setLevel(parent.getLevel() + 1);
            }
        } else {
            department.setPath("/" + department.getCode());
            department.setLevel(1);
        }
        
        // 设置默认排序
        if (department.getSort() == null) {
            department.setSort(0);
        }
        
        departmentMapper.insert(department);
        return department;
    }

    @Override
    public Department updateDepartment(Department department) {
        Department existing = departmentMapper.selectById(department.getId());
        if (existing == null) {
            throw new RuntimeException("部门不存在");
        }
        
        // 更新路径和层级（如果父级发生变化）
        if (!existing.getParentId().equals(department.getParentId())) {
            updateDepartmentPath(department);
        }
        
        departmentMapper.updateById(department);
        return department;
    }

    @Override
    public void deleteDepartment(Long id) {
        // 检查是否有子部门
        List<Department> children = getChildDepartments(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("存在子部门，无法删除");
        }
        
        // 检查是否有用户
        if (hasDepartmentUsers(id)) {
            throw new RuntimeException("部门下存在用户，无法删除");
        }
        
        // 软删除
        Department department = new Department();
        department.setId(id);
        department.setDeleted(true);
        departmentMapper.updateById(department);
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentMapper.selectById(id);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentMapper.selectList(
            new QueryWrapper<Department>()
                .eq("enabled", true)
                .eq("deleted", false)
                .orderByAsc("sort", "created_at")
        );
    }

    @Override
    public List<Department> getChildDepartments(Long parentId) {
        return departmentMapper.selectList(
            new QueryWrapper<Department>()
                .eq("parent_id", parentId)
                .eq("enabled", true)
                .eq("deleted", false)
                .orderByAsc("sort", "created_at")
        );
    }

    @Override
    public boolean hasDepartmentUsers(Long departmentId) {
        Long count = userMapper.selectCount(
            new QueryWrapper<User>()
                .eq("department_id", departmentId)
                .eq("deleted", false)
        );
        return count > 0;
    }

    /**
     * 构建部门树
     */
    private List<Department> buildTree(List<Department> departments, Long parentId) {
        List<Department> result = new ArrayList<>();
        
        for (Department department : departments) {
            if ((parentId == null && department.getParentId() == null) ||
                (parentId != null && parentId.equals(department.getParentId()))) {
                
                List<Department> children = buildTree(departments, department.getId());
                department.setChildren(children);
                result.add(department);
            }
        }
        
        return result;
    }

    /**
     * 更新部门路径
     */
    private void updateDepartmentPath(Department department) {
        if (department.getParentId() != null) {
            Department parent = departmentMapper.selectById(department.getParentId());
            if (parent != null) {
                department.setPath(parent.getPath() + "/" + department.getCode());
                department.setLevel(parent.getLevel() + 1);
            }
        } else {
            department.setPath("/" + department.getCode());
            department.setLevel(1);
        }
    }
}