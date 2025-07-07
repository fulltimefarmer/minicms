package org.max.cms.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.max.cms.user.entity.Contract;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ContractMapper extends BaseMapper<Contract> {
    
    /**
     * 根据员工ID获取所有合同
     */
    @Select("SELECT * FROM contracts WHERE employee_id = #{employeeId} ORDER BY start_date DESC")
    List<Contract> findByEmployeeId(Long employeeId);
    
    /**
     * 获取员工当前有效合同
     */
    @Select("SELECT * FROM contracts WHERE employee_id = #{employeeId} AND status = 'ACTIVE' ORDER BY start_date DESC LIMIT 1")
    Contract getCurrentContractByEmployeeId(Long employeeId);
    
    /**
     * 根据合同编号查找合同
     */
    @Select("SELECT * FROM contracts WHERE contract_number = #{contractNumber}")
    Contract findByContractNumber(String contractNumber);
    
    /**
     * 获取即将到期的合同
     */
    @Select("SELECT * FROM contracts WHERE status = 'ACTIVE' AND end_date BETWEEN #{startDate} AND #{endDate}")
    List<Contract> findExpiringContracts(LocalDate startDate, LocalDate endDate);
    
    /**
     * 根据合同状态获取合同列表
     */
    @Select("SELECT * FROM contracts WHERE status = #{status}")
    List<Contract> findByStatus(String status);
}