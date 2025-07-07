package org.max.cms.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.max.cms.user.entity.JobLevel;

import java.util.List;

@Mapper
public interface JobLevelMapper extends BaseMapper<JobLevel> {
    
    /**
     * 根据代码查找职级
     */
    @Select("SELECT * FROM job_levels WHERE code = #{code}")
    JobLevel findByCode(String code);
    
    /**
     * 获取所有启用的职级，按等级排序
     */
    @Select("SELECT * FROM job_levels WHERE enabled = true ORDER BY level DESC")
    List<JobLevel> findAllEnabled();
    
    /**
     * 根据等级范围查询职级
     */
    @Select("SELECT * FROM job_levels WHERE level BETWEEN #{minLevel} AND #{maxLevel} AND enabled = true ORDER BY level")
    List<JobLevel> findByLevelRange(Integer minLevel, Integer maxLevel);
}