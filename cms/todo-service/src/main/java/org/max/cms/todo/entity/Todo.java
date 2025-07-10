package org.max.cms.todo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.max.cms.common.entity.BaseEntity;

import java.time.LocalDateTime;

/**
 * 待办事项实体
 */
@Entity
@Table(name = "t_todo")
public class Todo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 待办事项标题
     */
    @NotBlank(message = "标题不能为空")
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * 待办事项描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 是否已完成
     */
    @NotNull(message = "完成状态不能为空")
    @Column(nullable = false)
    private Boolean completed = false;

    /**
     * 优先级：LOW, MEDIUM, HIGH
     */
    @NotNull(message = "优先级不能为空")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Priority priority = Priority.MEDIUM;

    /**
     * 截止时间
     */
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    /**
     * 创建用户ID
     */
    @NotNull(message = "创建用户不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 标签（JSON格式存储）
     */
    @Column(columnDefinition = "JSON")
    private String tags;

    /**
     * 优先级枚举
     */
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    // 构造函数
    public Todo() {
    }

    public Todo(String title, String description, Priority priority, Long userId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.userId = userId;
        this.completed = false;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * 标记为已完成
     */
    public void markAsCompleted() {
        this.completed = true;
    }

    /**
     * 标记为未完成
     */
    public void markAsIncomplete() {
        this.completed = false;
    }

    /**
     * 检查是否过期
     */
    public boolean isOverdue() {
        return dueDate != null && !completed && LocalDateTime.now().isAfter(dueDate);
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", completed=" + completed +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                ", userId=" + userId +
                ", tags='" + tags + '\'' +
                '}';
    }
}