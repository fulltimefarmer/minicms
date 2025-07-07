package org.max.cms.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.max.cms.user.entity.JobLevel;
import org.max.cms.user.mapper.JobLevelMapper;
import org.max.cms.user.service.JobLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class JobLevelServiceImpl implements JobLevelService {

    @Autowired
    private JobLevelMapper jobLevelMapper;

    @Override
    public JobLevel createJobLevel(JobLevel jobLevel) {
        jobLevelMapper.insert(jobLevel);
        return jobLevel;
    }

    @Override
    public JobLevel updateJobLevel(JobLevel jobLevel) {
        jobLevelMapper.updateById(jobLevel);
        return jobLevel;
    }

    @Override
    public void deleteJobLevel(Long id) {
        // 软删除
        JobLevel jobLevel = new JobLevel();
        jobLevel.setId(id);
        jobLevel.setDeleted(true);
        jobLevelMapper.updateById(jobLevel);
    }

    @Override
    public JobLevel getJobLevelById(Long id) {
        return jobLevelMapper.selectById(id);
    }

    @Override
    public JobLevel getJobLevelByCode(String code) {
        return jobLevelMapper.findByCode(code);
    }

    @Override
    public List<JobLevel> getAllJobLevels() {
        return jobLevelMapper.selectList(
            new QueryWrapper<JobLevel>()
                .eq("deleted", false)
                .orderByDesc("level")
        );
    }

    @Override
    public List<JobLevel> getAllEnabledJobLevels() {
        return jobLevelMapper.findAllEnabled();
    }

    @Override
    public List<JobLevel> getJobLevelsByLevelRange(Integer minLevel, Integer maxLevel) {
        return jobLevelMapper.findByLevelRange(minLevel, maxLevel);
    }

    @Override
    public void toggleJobLevelStatus(Long id, Boolean enabled) {
        JobLevel jobLevel = new JobLevel();
        jobLevel.setId(id);
        jobLevel.setEnabled(enabled);
        jobLevelMapper.updateById(jobLevel);
    }
}