package org.max.cms.user.controller;

import org.max.cms.user.entity.Department;
import org.max.cms.user.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 获取部门树形结构
     */
    @GetMapping("/tree")
    public ResponseEntity<List<Department>> getDepartmentTree() {
        List<Department> tree = departmentService.getDepartmentTree();
        return ResponseEntity.ok(tree);
    }

    /**
     * 获取所有部门列表
     */
    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    /**
     * 根据ID获取部门
     */
    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        Department department = departmentService.getDepartmentById(id);
        if (department != null) {
            return ResponseEntity.ok(department);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 创建部门
     */
    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        try {
            Department created = departmentService.createDepartment(department);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新部门
     */
    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        try {
            department.setId(id);
            Department updated = departmentService.updateDepartment(department);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取子部门
     */
    @GetMapping("/{id}/children")
    public ResponseEntity<List<Department>> getChildDepartments(@PathVariable Long id) {
        List<Department> children = departmentService.getChildDepartments(id);
        return ResponseEntity.ok(children);
    }
}