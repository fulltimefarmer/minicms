package org.max.cms.user.service;

import org.max.cms.user.entity.JobLevel;
import java.util.List;

public interface JobLevelService {
    
    /**
     * 创建职级
     */
    JobLevel createJobLevel(JobLevel jobLevel);
    
    /**
     * 更新职级
     */
    JobLevel updateJobLevel(JobLevel jobLevel);
    
    /**
     * 删除职级
     */
    void deleteJobLevel(Long id);
    
    /**
     * 根据ID获取职级
     */
    JobLevel getJobLevelById(Long id);
    
    /**
     * 根据代码获取职级
     */
    JobLevel getJobLevelByCode(String code);
    
    /**
     * 获取所有职级
     */
    List<JobLevel> getAllJobLevels();
    
    /**
     * 获取所有启用的职级
     */
    List<JobLevel> getAllEnabledJobLevels();
    
    /**
     * 根据等级范围查询职级
     */
    List<JobLevel> getJobLevelsByLevelRange(Integer minLevel, Integer maxLevel);
    
    /**
     * 启用/禁用职级
     */
    void toggleJobLevelStatus(Long id, Boolean enabled);
}