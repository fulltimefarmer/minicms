package org.max.cms.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.max.cms.user.entity.Salary;

import java.util.List;

@Mapper
public interface SalaryMapper extends BaseMapper<Salary> {
    
    /**
     * 获取员工当前薪资信息
     */
    @Select("SELECT * FROM salaries WHERE employee_id = #{employeeId} AND status = 'ACTIVE' ORDER BY effective_date DESC LIMIT 1")
    Salary getCurrentSalaryByEmployeeId(Long employeeId);
    
    /**
     * 获取员工所有薪资历史
     */
    @Select("SELECT * FROM salaries WHERE employee_id = #{employeeId} ORDER BY effective_date DESC")
    List<Salary> getSalaryHistoryByEmployeeId(Long employeeId);
    
    /**
     * 获取职级的薪资统计
     */
    List<Salary> getSalariesByJobLevelId(Long jobLevelId);
    
    /**
     * 根据状态获取薪资列表
     */
    @Select("SELECT * FROM salaries WHERE status = #{status}")
    List<Salary> findByStatus(String status);
}