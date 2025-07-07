package org.max.cms.user.controller;

import org.max.cms.user.entity.Employee;
import org.max.cms.user.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 获取所有员工信息
     */
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    /**
     * 根据ID获取员工信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 获取员工完整信息（包含用户、职级、薪资等信息）
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<Employee> getEmployeeWithDetails(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeWithDetails(id);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 根据用户ID获取员工信息
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Employee> getEmployeeByUserId(@PathVariable Long userId) {
        Employee employee = employeeService.getEmployeeByUserId(userId);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 根据工号获取员工信息
     */
    @GetMapping("/number/{employeeNumber}")
    public ResponseEntity<Employee> getEmployeeByEmployeeNumber(@PathVariable String employeeNumber) {
        Employee employee = employeeService.getEmployeeByEmployeeNumber(employeeNumber);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 创建员工信息
     */
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        try {
            Employee created = employeeService.createEmployee(employee);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新员工信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        try {
            employee.setId(id);
            Employee updated = employeeService.updateEmployee(employee);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除员工信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 根据部门ID获取员工列表
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartmentId(@PathVariable Long departmentId) {
        List<Employee> employees = employeeService.getEmployeesByDepartmentId(departmentId);
        return ResponseEntity.ok(employees);
    }

    /**
     * 根据状态获取员工列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Employee>> getEmployeesByStatus(@PathVariable String status) {
        List<Employee> employees = employeeService.getEmployeesByStatus(status);
        return ResponseEntity.ok(employees);
    }

    /**
     * 分页查询员工信息
     */
    @GetMapping("/page")
    public ResponseEntity<List<Employee>> getEmployeesWithPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Employee> employees = employeeService.getEmployeesWithPagination(page, size);
        return ResponseEntity.ok(employees);
    }

    /**
     * 批量导入员工信息
     */
    @PostMapping("/batch")
    public ResponseEntity<String> batchImportEmployees(@RequestBody List<Employee> employees) {
        try {
            int count = employeeService.batchImportEmployees(employees);
            return ResponseEntity.ok("成功导入 " + count + " 个员工信息");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("批量导入失败: " + e.getMessage());
        }
    }
}