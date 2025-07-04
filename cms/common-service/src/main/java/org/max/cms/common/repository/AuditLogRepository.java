package org.max.cms.common.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.max.cms.common.entity.AuditLog;

@Mapper
public interface AuditLogRepository extends BaseMapper<AuditLog> {
}