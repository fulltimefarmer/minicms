package org.max.cms.todo.mapper;

import org.max.cms.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 待办事项数据访问接口
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    /**
     * 根据用户ID查找所有待办事项
     */
    List<Todo> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据用户ID分页查找待办事项
     */
    Page<Todo> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 根据用户ID和完成状态查找待办事项
     */
    List<Todo> findByUserIdAndCompletedOrderByCreatedAtDesc(Long userId, Boolean completed);

    /**
     * 根据用户ID、完成状态分页查找待办事项
     */
    Page<Todo> findByUserIdAndCompletedOrderByCreatedAtDesc(Long userId, Boolean completed, Pageable pageable);

    /**
     * 根据用户ID和优先级查找待办事项
     */
    List<Todo> findByUserIdAndPriorityOrderByCreatedAtDesc(Long userId, Todo.Priority priority);

    /**
     * 根据用户ID查找逾期的待办事项
     */
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND t.completed = false AND t.dueDate IS NOT NULL AND t.dueDate < :now")
    List<Todo> findOverdueTodos(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * 根据用户ID查找今天截止的待办事项
     */
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND t.completed = false AND t.dueDate IS NOT NULL AND DATE(t.dueDate) = DATE(:today)")
    List<Todo> findTodayTodos(@Param("userId") Long userId, @Param("today") LocalDateTime today);

    /**
     * 根据用户ID和标题模糊查询
     */
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Todo> findByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

    /**
     * 分页根据用户ID和标题模糊查询
     */
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY t.createdAt DESC")
    Page<Todo> findByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据用户ID查找指定时间范围内的待办事项
     */
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND t.dueDate BETWEEN :startDate AND :endDate ORDER BY t.dueDate ASC")
    List<Todo> findByUserIdAndDueDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 批量更新待办事项的完成状态
     */
    @Modifying
    @Query("UPDATE Todo t SET t.completed = :completed WHERE t.id IN :ids AND t.userId = :userId")
    int batchUpdateCompleted(@Param("ids") List<Long> ids, @Param("completed") Boolean completed, @Param("userId") Long userId);

    /**
     * 获取用户的待办事项统计信息
     */
    @Query("SELECT COUNT(t) FROM Todo t WHERE t.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.userId = :userId AND t.completed = :completed")
    Long countByUserIdAndCompleted(@Param("userId") Long userId, @Param("completed") Boolean completed);

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.userId = :userId AND t.completed = false AND t.dueDate IS NOT NULL AND t.dueDate < :now")
    Long countOverdueTodos(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * 根据用户ID和待办事项ID查找（用于权限检查）
     */
    Optional<Todo> findByIdAndUserId(Long id, Long userId);

    /**
     * 删除用户的所有待办事项
     */
    @Modifying
    @Query("DELETE FROM Todo t WHERE t.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据优先级统计待办事项数量
     */
    @Query("SELECT t.priority, COUNT(t) FROM Todo t WHERE t.userId = :userId AND t.completed = false GROUP BY t.priority")
    List<Object[]> countByUserIdAndPriorityGrouped(@Param("userId") Long userId);
}