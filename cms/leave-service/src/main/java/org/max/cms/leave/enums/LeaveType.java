package org.max.cms.leave.enums;

import lombok.Getter;

/**
 * 请假类型枚举
 */
@Getter
public enum LeaveType {
    ANNUAL("年假", "Annual Leave"),
    SICK("病假", "Sick Leave"),
    PERSONAL("事假", "Personal Leave"),
    MATERNITY("产假", "Maternity Leave"),
    PATERNITY("陪产假", "Paternity Leave"),
    MARRIAGE("婚假", "Marriage Leave"),
    BEREAVEMENT("丧假", "Bereavement Leave"),
    COMPENSATORY("调休", "Compensatory Leave"),
    OTHER("其他", "Other");

    private final String chineseName;
    private final String englishName;

    LeaveType(String chineseName, String englishName) {
        this.chineseName = chineseName;
        this.englishName = englishName;
    }
}