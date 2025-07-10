package org.max.cms.todo.service;

import org.max.cms.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 待办事项服务接口
 */
public interface TodoService {

    /**
     * 创建待办事项
     */
    Todo createTodo(Todo todo);

    /**
     * 根据ID获取待办事项
     */
    Todo getTodoById(Long id, Long userId);

    /**
     * 更新待办事项
     */
    Todo updateTodo(Long id, Todo todo, Long userId);

    /**
     * 删除待办事项
     */
    void deleteTodo(Long id, Long userId);

    /**
     * 获取用户的所有待办事项
     */
    List<Todo> getUserTodos(Long userId);

    /**
     * 分页获取用户的待办事项
     */
    Page<Todo> getUserTodos(Long userId, Pageable pageable);

    /**
     * 根据完成状态获取待办事项
     */
    List<Todo> getTodosByCompleted(Long userId, Boolean completed);

    /**
     * 分页根据完成状态获取待办事项
     */
    Page<Todo> getTodosByCompleted(Long userId, Boolean completed, Pageable pageable);

    /**
     * 根据优先级获取待办事项
     */
    List<Todo> getTodosByPriority(Long userId, Todo.Priority priority);

    /**
     * 搜索待办事项
     */
    List<Todo> searchTodos(Long userId, String keyword);

    /**
     * 分页搜索待办事项
     */
    Page<Todo> searchTodos(Long userId, String keyword, Pageable pageable);

    /**
     * 标记待办事项为已完成
     */
    Todo completeTodo(Long id, Long userId);

    /**
     * 标记待办事项为未完成
     */
    Todo incompleteTodo(Long id, Long userId);

    /**
     * 批量更新待办事项完成状态
     */
    int batchUpdateCompleted(List<Long> ids, Boolean completed, Long userId);

    /**
     * 获取逾期的待办事项
     */
    List<Todo> getOverdueTodos(Long userId);

    /**
     * 获取今天截止的待办事项
     */
    List<Todo> getTodayTodos(Long userId);

    /**
     * 获取指定时间范围内的待办事项
     */
    List<Todo> getTodosByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 获取用户待办事项统计信息
     */
    Map<String, Object> getTodoStats(Long userId);

    /**
     * 删除用户的所有待办事项
     */
    void deleteAllUserTodos(Long userId);
}