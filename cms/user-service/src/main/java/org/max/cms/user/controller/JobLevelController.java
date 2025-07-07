package org.max.cms.user.controller;

import org.max.cms.user.entity.JobLevel;
import org.max.cms.user.service.JobLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-levels")
@CrossOrigin(origins = "*")
public class JobLevelController {

    @Autowired
    private JobLevelService jobLevelService;

    /**
     * 获取所有职级
     */
    @GetMapping
    public ResponseEntity<List<JobLevel>> getAllJobLevels() {
        List<JobLevel> jobLevels = jobLevelService.getAllJobLevels();
        return ResponseEntity.ok(jobLevels);
    }

    /**
     * 获取所有启用的职级
     */
    @GetMapping("/enabled")
    public ResponseEntity<List<JobLevel>> getAllEnabledJobLevels() {
        List<JobLevel> jobLevels = jobLevelService.getAllEnabledJobLevels();
        return ResponseEntity.ok(jobLevels);
    }

    /**
     * 根据ID获取职级
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobLevel> getJobLevelById(@PathVariable Long id) {
        JobLevel jobLevel = jobLevelService.getJobLevelById(id);
        if (jobLevel != null) {
            return ResponseEntity.ok(jobLevel);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 根据代码获取职级
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<JobLevel> getJobLevelByCode(@PathVariable String code) {
        JobLevel jobLevel = jobLevelService.getJobLevelByCode(code);
        if (jobLevel != null) {
            return ResponseEntity.ok(jobLevel);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 创建职级
     */
    @PostMapping
    public ResponseEntity<JobLevel> createJobLevel(@RequestBody JobLevel jobLevel) {
        try {
            JobLevel created = jobLevelService.createJobLevel(jobLevel);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新职级
     */
    @PutMapping("/{id}")
    public ResponseEntity<JobLevel> updateJobLevel(@PathVariable Long id, @RequestBody JobLevel jobLevel) {
        try {
            jobLevel.setId(id);
            JobLevel updated = jobLevelService.updateJobLevel(jobLevel);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除职级
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobLevel(@PathVariable Long id) {
        try {
            jobLevelService.deleteJobLevel(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 根据等级范围查询职级
     */
    @GetMapping("/range")
    public ResponseEntity<List<JobLevel>> getJobLevelsByLevelRange(
            @RequestParam Integer minLevel,
            @RequestParam Integer maxLevel) {
        List<JobLevel> jobLevels = jobLevelService.getJobLevelsByLevelRange(minLevel, maxLevel);
        return ResponseEntity.ok(jobLevels);
    }

    /**
     * 启用/禁用职级
     */
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleJobLevelStatus(@PathVariable Long id, @RequestParam Boolean enabled) {
        try {
            jobLevelService.toggleJobLevelStatus(id, enabled);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}