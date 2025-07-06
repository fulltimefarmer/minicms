package org.max.cms.leave.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.max.cms.leave.dto.LeaveApplicationDTO;
import org.max.cms.leave.dto.LeaveApprovalDTO;
import org.max.cms.leave.entity.LeaveApplication;
import org.max.cms.leave.entity.LeaveApprovalHistory;
import org.max.cms.leave.service.LeaveApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 请假申请控制器
 */
@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "请假管理", description = "员工请假申请和审批管理")
public class LeaveApplicationController {
    
    private final LeaveApplicationService leaveApplicationService;
    
    @PostMapping("/applications")
    @Operation(summary = "创建请假申请", description = "员工创建新的请假申请")
    public ResponseEntity<LeaveApplication> createLeaveApplication(
            @Valid @RequestBody LeaveApplicationDTO dto) {
        try {
            LeaveApplication application = leaveApplicationService.createLeaveApplication(dto);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            log.error("创建请假申请失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/applications/{id}")
    @Operation(summary = "更新请假申请", description = "更新待审核的请假申请")
    public ResponseEntity<LeaveApplication> updateLeaveApplication(
            @PathVariable Long id,
            @Valid @RequestBody LeaveApplicationDTO dto) {
        try {
            LeaveApplication application = leaveApplicationService.updateLeaveApplication(id, dto);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            log.error("更新请假申请失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/applications/{id}")
    @Operation(summary = "删除请假申请", description = "删除待审核的请假申请")
    public ResponseEntity<Void> deleteLeaveApplication(@PathVariable Long id) {
        try {
            boolean deleted = leaveApplicationService.deleteLeaveApplication(id);
            return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("删除请假申请失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/applications/{id}")
    @Operation(summary = "获取请假申请详情", description = "根据ID获取请假申请详情")
    public ResponseEntity<LeaveApplication> getLeaveApplication(@PathVariable Long id) {
        LeaveApplication application = leaveApplicationService.getLeaveApplicationById(id);
        return application != null ? ResponseEntity.ok(application) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/applications")
    @Operation(summary = "分页查询请假申请", description = "分页查询请假申请列表")
    public ResponseEntity<Page<LeaveApplication>> getLeaveApplications(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "申请人ID") @RequestParam(required = false) Long applicantId,
            @Parameter(description = "申请状态") @RequestParam(required = false) String status) {
        
        Page<LeaveApplication> result = leaveApplicationService.getLeaveApplications(page, size, applicantId, status);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/pending-approvals")
    @Operation(summary = "获取待审批申请", description = "获取指定审批人的待审批申请列表")
    public ResponseEntity<Page<LeaveApplication>> getPendingApprovals(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "审批人ID") @RequestParam Long approverId) {
        
        Page<LeaveApplication> result = leaveApplicationService.getPendingApprovals(page, size, approverId);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/approvals")
    @Operation(summary = "审批请假申请", description = "审批通过或拒绝请假申请")
    public ResponseEntity<Void> approveLeaveApplication(@Valid @RequestBody LeaveApprovalDTO dto) {
        try {
            boolean approved = leaveApplicationService.approveLeaveApplication(dto);
            return approved ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("审批请假申请失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/applications/{id}/cancel")
    @Operation(summary = "取消请假申请", description = "申请人取消自己的请假申请")
    public ResponseEntity<Void> cancelLeaveApplication(
            @PathVariable Long id,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            boolean cancelled = leaveApplicationService.cancelLeaveApplication(id, userId);
            return cancelled ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("取消请假申请失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/applications/{id}/history")
    @Operation(summary = "获取审批历史", description = "获取指定请假申请的审批历史记录")
    public ResponseEntity<List<LeaveApprovalHistory>> getApprovalHistory(@PathVariable Long id) {
        List<LeaveApprovalHistory> history = leaveApplicationService.getApprovalHistory(id);
        return ResponseEntity.ok(history);
    }
}