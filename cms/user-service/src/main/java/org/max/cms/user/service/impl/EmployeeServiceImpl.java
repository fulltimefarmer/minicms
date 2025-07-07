package org.max.cms.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.max.cms.user.entity.Employee;
import org.max.cms.user.entity.User;
import org.max.cms.user.entity.JobLevel;
import org.max.cms.user.entity.Salary;
import org.max.cms.user.entity.Contact;
import org.max.cms.user.mapper.EmployeeMapper;
import org.max.cms.user.mapper.UserMapper;
import org.max.cms.user.mapper.JobLevelMapper;
import org.max.cms.user.mapper.SalaryMapper;
import org.max.cms.user.mapper.ContactMapper;
import org.max.cms.user.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JobLevelMapper jobLevelMapper;
    
    @Autowired
    private SalaryMapper salaryMapper;
    
    @Autowired
    private ContactMapper contactMapper;

    @Override
    public Employee createEmployee(Employee employee) {
        employeeMapper.insert(employee);
        return employee;
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        employeeMapper.updateById(employee);
        return employee;
    }

    @Override
    public void deleteEmployee(Long id) {
        // 软删除
        Employee employee = new Employee();
        employee.setId(id);
        employee.setDeleted(true);
        employeeMapper.updateById(employee);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeMapper.selectById(id);
    }

    @Override
    public Employee getEmployeeByUserId(Long userId) {
        return employeeMapper.findByUserId(userId);
    }

    @Override
    public Employee getEmployeeByEmployeeNumber(String employeeNumber) {
        return employeeMapper.findByEmployeeNumber(employeeNumber);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeMapper.selectList(
            new QueryWrapper<Employee>()
                .eq("deleted", false)
                .orderByDesc("created_at")
        );
    }

    @Override
    public List<Employee> getEmployeesByDepartmentId(Long departmentId) {
        return employeeMapper.findByDepartmentId(departmentId);
    }

    @Override
    public List<Employee> getEmployeesByStatus(String status) {
        return employeeMapper.findByStatus(status);
    }

    @Override
    public Employee getEmployeeWithDetails(Long employeeId) {
        Employee employee = employeeMapper.selectById(employeeId);
        if (employee != null) {
            // 加载关联的用户信息
            if (employee.getUserId() != null) {
                User user = userMapper.selectById(employee.getUserId());
                employee.setUser(user);
            }
            
            // 加载当前薪资信息
            Salary currentSalary = salaryMapper.getCurrentSalaryByEmployeeId(employeeId);
            employee.setCurrentSalary(currentSalary);
            
            // 加载职级信息
            if (currentSalary != null && currentSalary.getJobLevelId() != null) {
                JobLevel jobLevel = jobLevelMapper.selectById(currentSalary.getJobLevelId());
                employee.setJobLevel(jobLevel);
            }
            
            // 加载主要联系方式
            Contact contact = contactMapper.getPrimaryContactByEmployeeId(employeeId);
            employee.setContact(contact);
        }
        return employee;
    }

    @Override
    public List<Employee> getEmployeesWithPagination(int page, int size) {
        Page<Employee> pageRequest = new Page<>(page, size);
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", false)
                   .orderByDesc("created_at");
        Page<Employee> result = employeeMapper.selectPage(pageRequest, queryWrapper);
        return result.getRecords();
    }

    @Override
    public int batchImportEmployees(List<Employee> employees) {
        int count = 0;
        for (Employee employee : employees) {
            try {
                employeeMapper.insert(employee);
                count++;
            } catch (Exception e) {
                // 记录错误日志，继续处理下一个
                // log.error("导入员工失败: {}", employee.getEmployeeNumber(), e);
            }
        }
        return count;
    }
}