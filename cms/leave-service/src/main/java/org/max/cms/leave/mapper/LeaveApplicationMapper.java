package org.max.cms.leave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.max.cms.leave.entity.LeaveApplication;

/**
 * 请假申请Mapper接口
 */
@Mapper
public interface LeaveApplicationMapper extends BaseMapper<LeaveApplication> {
}