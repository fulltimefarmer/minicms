package org.max.cms.leave.enums;

import lombok.Getter;

/**
 * 请假状态枚举
 */
@Getter
public enum LeaveStatus {
    PENDING("待审核", "Pending"),
    APPROVED("已批准", "Approved"),
    REJECTED("已拒绝", "Rejected"),
    CANCELLED("已取消", "Cancelled"),
    IN_PROGRESS("进行中", "In Progress"),
    COMPLETED("已完成", "Completed");

    private final String chineseName;
    private final String englishName;

    LeaveStatus(String chineseName, String englishName) {
        this.chineseName = chineseName;
        this.englishName = englishName;
    }
}