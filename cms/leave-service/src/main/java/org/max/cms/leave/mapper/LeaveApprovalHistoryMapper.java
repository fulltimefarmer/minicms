package org.max.cms.leave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.max.cms.leave.entity.LeaveApprovalHistory;

/**
 * 请假审批历史Mapper接口
 */
@Mapper
public interface LeaveApprovalHistoryMapper extends BaseMapper<LeaveApprovalHistory> {
}