package org.max.cms.todo.controller;

import org.max.cms.common.response.ApiResponse;
import org.max.cms.todo.entity.Todo;
import org.max.cms.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 待办事项控制器
 */
@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    @Autowired
    private TodoService todoService;

    /**
     * 创建待办事项
     */
    @PostMapping
    public ApiResponse<Todo> createTodo(@Valid @RequestBody Todo todo) {
        Long userId = getCurrentUserId();
        todo.setUserId(userId);
        Todo createdTodo = todoService.createTodo(todo);
        return ApiResponse.success(createdTodo, "待办事项创建成功");
    }

    /**
     * 获取待办事项详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Todo> getTodo(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        Todo todo = todoService.getTodoById(id, userId);
        return ApiResponse.success(todo);
    }

    /**
     * 更新待办事项
     */
    @PutMapping("/{id}")
    public ApiResponse<Todo> updateTodo(@PathVariable Long id, @Valid @RequestBody Todo todo) {
        Long userId = getCurrentUserId();
        Todo updatedTodo = todoService.updateTodo(id, todo, userId);
        return ApiResponse.success(updatedTodo, "待办事项更新成功");
    }

    /**
     * 删除待办事项
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTodo(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        todoService.deleteTodo(id, userId);
        return ApiResponse.success("待办事项删除成功");
    }

    /**
     * 获取用户的所有待办事项（分页）
     */
    @GetMapping
    public ApiResponse<Page<Todo>> getUserTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Todo> todos = todoService.getUserTodos(userId, pageable);
        return ApiResponse.success(todos);
    }

    /**
     * 获取用户的所有待办事项（不分页）
     */
    @GetMapping("/all")
    public ApiResponse<List<Todo>> getAllUserTodos() {
        Long userId = getCurrentUserId();
        List<Todo> todos = todoService.getUserTodos(userId);
        return ApiResponse.success(todos);
    }

    /**
     * 根据完成状态筛选待办事项
     */
    @GetMapping("/status")
    public ApiResponse<Page<Todo>> getTodosByStatus(
            @RequestParam Boolean completed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Todo> todos = todoService.getTodosByCompleted(userId, completed, pageable);
        return ApiResponse.success(todos);
    }

    /**
     * 根据优先级筛选待办事项
     */
    @GetMapping("/priority/{priority}")
    public ApiResponse<List<Todo>> getTodosByPriority(@PathVariable Todo.Priority priority) {
        Long userId = getCurrentUserId();
        List<Todo> todos = todoService.getTodosByPriority(userId, priority);
        return ApiResponse.success(todos);
    }

    /**
     * 搜索待办事项
     */
    @GetMapping("/search")
    public ApiResponse<Page<Todo>> searchTodos(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Todo> todos = todoService.searchTodos(userId, keyword, pageable);
        return ApiResponse.success(todos);
    }

    /**
     * 标记待办事项为已完成
     */
    @PatchMapping("/{id}/complete")
    public ApiResponse<Todo> completeTodo(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        Todo todo = todoService.completeTodo(id, userId);
        return ApiResponse.success(todo, "待办事项已标记为完成");
    }

    /**
     * 标记待办事项为未完成
     */
    @PatchMapping("/{id}/incomplete")
    public ApiResponse<Todo> incompleteTodo(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        Todo todo = todoService.incompleteTodo(id, userId);
        return ApiResponse.success(todo, "待办事项已标记为未完成");
    }

    /**
     * 批量更新待办事项完成状态
     */
    @PatchMapping("/batch/status")
    public ApiResponse<Integer> batchUpdateStatus(
            @RequestBody Map<String, Object> request) {
        Long userId = getCurrentUserId();
        
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) request.get("ids");
        Boolean completed = (Boolean) request.get("completed");
        
        int updated = todoService.batchUpdateCompleted(ids, completed, userId);
        return ApiResponse.success(updated, "批量更新成功，共更新 " + updated + " 个待办事项");
    }

    /**
     * 获取逾期的待办事项
     */
    @GetMapping("/overdue")
    public ApiResponse<List<Todo>> getOverdueTodos() {
        Long userId = getCurrentUserId();
        List<Todo> todos = todoService.getOverdueTodos(userId);
        return ApiResponse.success(todos);
    }

    /**
     * 获取今天截止的待办事项
     */
    @GetMapping("/today")
    public ApiResponse<List<Todo>> getTodayTodos() {
        Long userId = getCurrentUserId();
        List<Todo> todos = todoService.getTodayTodos(userId);
        return ApiResponse.success(todos);
    }

    /**
     * 获取指定时间范围内的待办事项
     */
    @GetMapping("/date-range")
    public ApiResponse<List<Todo>> getTodosByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Long userId = getCurrentUserId();
        List<Todo> todos = todoService.getTodosByDateRange(userId, startDate, endDate);
        return ApiResponse.success(todos);
    }

    /**
     * 获取待办事项统计信息
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getTodoStats() {
        Long userId = getCurrentUserId();
        Map<String, Object> stats = todoService.getTodoStats(userId);
        return ApiResponse.success(stats);
    }

    /**
     * 删除用户所有待办事项
     */
    @DeleteMapping("/all")
    public ApiResponse<Void> deleteAllTodos() {
        Long userId = getCurrentUserId();
        todoService.deleteAllUserTodos(userId);
        return ApiResponse.success("所有待办事项已删除");
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            try {
                return Long.parseLong((String) authentication.getPrincipal());
            } catch (NumberFormatException e) {
                throw new RuntimeException("无效的用户ID");
            }
        }
        throw new RuntimeException("用户未登录");
    }
}