package org.max.cms.todo.service.impl;

import org.max.cms.common.exception.BusinessException;
import org.max.cms.todo.entity.Todo;
import org.max.cms.todo.mapper.TodoRepository;
import org.max.cms.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 待办事项服务实现类
 */
@Service
@Transactional
public class TodoServiceImpl implements TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Override
    public Todo createTodo(Todo todo) {
        if (todo == null) {
            throw new BusinessException("待办事项不能为空");
        }
        if (todo.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 设置默认值
        if (todo.getCompleted() == null) {
            todo.setCompleted(false);
        }
        if (todo.getPriority() == null) {
            todo.setPriority(Todo.Priority.MEDIUM);
        }
        
        return todoRepository.save(todo);
    }

    @Override
    @Transactional(readOnly = true)
    public Todo getTodoById(Long id, Long userId) {
        if (id == null || userId == null) {
            throw new BusinessException("ID和用户ID不能为空");
        }
        
        return todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException("待办事项不存在或无权限访问"));
    }

    @Override
    public Todo updateTodo(Long id, Todo todo, Long userId) {
        if (id == null || todo == null || userId == null) {
            throw new BusinessException("参数不能为空");
        }
        
        Todo existingTodo = getTodoById(id, userId);
        
        // 更新字段
        if (todo.getTitle() != null) {
            existingTodo.setTitle(todo.getTitle());
        }
        if (todo.getDescription() != null) {
            existingTodo.setDescription(todo.getDescription());
        }
        if (todo.getCompleted() != null) {
            existingTodo.setCompleted(todo.getCompleted());
        }
        if (todo.getPriority() != null) {
            existingTodo.setPriority(todo.getPriority());
        }
        if (todo.getDueDate() != null) {
            existingTodo.setDueDate(todo.getDueDate());
        }
        if (todo.getTags() != null) {
            existingTodo.setTags(todo.getTags());
        }
        
        return todoRepository.save(existingTodo);
    }

    @Override
    public void deleteTodo(Long id, Long userId) {
        if (id == null || userId == null) {
            throw new BusinessException("ID和用户ID不能为空");
        }
        
        Todo todo = getTodoById(id, userId);
        todoRepository.delete(todo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Todo> getUserTodos(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        return todoRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Todo> getUserTodos(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        return todoRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Todo> getTodosByCompleted(Long userId, Boolean completed) {
        if (userId == null || completed == null) {
            throw new BusinessException("用户ID和完成状态不能为空");
        }
        
        return todoRepository.findByUserIdAndCompletedOrderByCreatedAtDesc(userId, completed);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Todo> getTodosByCompleted(Long userId, Boolean completed, Pageable pageable) {
        if (userId == null || completed == null) {
            throw new BusinessException("用户ID和完成状态不能为空");
        }
        
        return todoRepository.findByUserIdAndCompletedOrderByCreatedAtDesc(userId, completed, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Todo> getTodosByPriority(Long userId, Todo.Priority priority) {
        if (userId == null || priority == null) {
            throw new BusinessException("用户ID和优先级不能为空");
        }
        
        return todoRepository.findByUserIdAndPriorityOrderByCreatedAtDesc(userId, priority);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Todo> searchTodos(Long userId, String keyword) {
        if (userId == null || keyword == null || keyword.trim().isEmpty()) {
            throw new BusinessException("用户ID和搜索关键词不能为空");
        }
        
        return todoRepository.findByUserIdAndKeyword(userId, keyword.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Todo> searchTodos(Long userId, String keyword, Pageable pageable) {
        if (userId == null || keyword == null || keyword.trim().isEmpty()) {
            throw new BusinessException("用户ID和搜索关键词不能为空");
        }
        
        return todoRepository.findByUserIdAndKeyword(userId, keyword.trim(), pageable);
    }

    @Override
    public Todo completeTodo(Long id, Long userId) {
        if (id == null || userId == null) {
            throw new BusinessException("ID和用户ID不能为空");
        }
        
        Todo todo = getTodoById(id, userId);
        todo.markAsCompleted();
        return todoRepository.save(todo);
    }

    @Override
    public Todo incompleteTodo(Long id, Long userId) {
        if (id == null || userId == null) {
            throw new BusinessException("ID和用户ID不能为空");
        }
        
        Todo todo = getTodoById(id, userId);
        todo.markAsIncomplete();
        return todoRepository.save(todo);
    }

    @Override
    public int batchUpdateCompleted(List<Long> ids, Boolean completed, Long userId) {
        if (ids == null || ids.isEmpty() || completed == null || userId == null) {
            throw new BusinessException("参数不能为空");
        }
        
        return todoRepository.batchUpdateCompleted(ids, completed, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Todo> getOverdueTodos(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        return todoRepository.findOverdueTodos(userId, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Todo> getTodayTodos(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        return todoRepository.findTodayTodos(userId, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Todo> getTodosByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        if (userId == null || startDate == null || endDate == null) {
            throw new BusinessException("参数不能为空");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        
        return todoRepository.findByUserIdAndDueDateBetween(userId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTodoStats(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        // 总数
        Long total = todoRepository.countByUserId(userId);
        stats.put("total", total);
        
        // 已完成数量
        Long completed = todoRepository.countByUserIdAndCompleted(userId, true);
        stats.put("completed", completed);
        
        // 未完成数量
        Long pending = todoRepository.countByUserIdAndCompleted(userId, false);
        stats.put("pending", pending);
        
        // 逾期数量
        Long overdue = todoRepository.countOverdueTodos(userId, LocalDateTime.now());
        stats.put("overdue", overdue);
        
        // 完成率
        double completionRate = total > 0 ? (double) completed / total * 100 : 0;
        stats.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
        
        // 按优先级统计
        List<Object[]> priorityStats = todoRepository.countByUserIdAndPriorityGrouped(userId);
        Map<String, Long> priorityMap = new HashMap<>();
        for (Object[] stat : priorityStats) {
            priorityMap.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("priorityStats", priorityMap);
        
        return stats;
    }

    @Override
    public void deleteAllUserTodos(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        todoRepository.deleteByUserId(userId);
    }
}