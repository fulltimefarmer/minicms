import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

export interface Todo {
  id?: number;
  title: string;
  description?: string;
  completed: boolean;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  dueDate?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  code?: string;
}

export interface PageResult<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class TodoService {
  private baseUrl = '/api/todos';

  // 临时数据，作为后备方案
  private mockTodos: Todo[] = [
    {
      id: 1,
      title: '完成项目需求分析',
      description: '与客户确认项目需求，整理需求文档',
      completed: false,
      priority: 'HIGH',
      dueDate: '2024-12-31T23:59:00',
      createdAt: '2024-12-20T10:00:00',
      updatedAt: '2024-12-20T10:00:00'
    },
    {
      id: 2,
      title: '优化数据库查询',
      description: '针对慢查询进行优化',
      completed: false,
      priority: 'MEDIUM',
      createdAt: '2024-12-20T11:00:00',
      updatedAt: '2024-12-20T11:00:00'
    },
    {
      id: 3,
      title: '更新用户手册',
      description: '根据新功能更新用户操作手册',
      completed: true,
      priority: 'LOW',
      createdAt: '2024-12-19T14:00:00',
      updatedAt: '2024-12-20T09:00:00'
    }
  ];

  constructor(private http: HttpClient) {}

  getTodos(): Observable<Todo[]> {
    return this.http.get<ApiResponse<Todo[]>>(`${this.baseUrl}/all`).pipe(
      map(response => {
        if (response.success && response.data) {
          return response.data;
        }
        throw new Error(response.message || '获取待办事项失败');
      })
    ).pipe(
      // 如果API调用失败，使用mock数据作为后备
      // catchError(() => of([...this.mockTodos]))
    );
  }

  getTodosPage(page: number = 0, size: number = 20): Observable<PageResult<Todo>> {
    return this.http.get<ApiResponse<PageResult<Todo>>>(`${this.baseUrl}?page=${page}&size=${size}`).pipe(
      map(response => {
        if (response.success && response.data) {
          return response.data;
        }
        throw new Error(response.message || '获取待办事项失败');
      })
    );
  }

  getTodo(id: number): Observable<Todo> {
    return this.http.get<ApiResponse<Todo>>(`${this.baseUrl}/${id}`).pipe(
      map(response => {
        if (response.success && response.data) {
          return response.data;
        }
        throw new Error(response.message || '获取待办事项详情失败');
      })
    );
  }

  createTodo(todo: Todo): Observable<Todo> {
    return this.http.post<ApiResponse<Todo>>(this.baseUrl, todo).pipe(
      map(response => {
        if (response.success && response.data) {
          return response.data;
        }
        throw new Error(response.message || '创建待办事项失败');
      })
    );
  }

  updateTodo(id: number, todo: Todo): Observable<Todo> {
    return this.http.put<ApiResponse<Todo>>(`${this.baseUrl}/${id}`, todo).pipe(
      map(response => {
        if (response.success && response.data) {
          return response.data;
        }
        throw new Error(response.message || '更新待办事项失败');
      })
    );
  }

  deleteTodo(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(
      map(response => {
        if (response.success) {
          return;
        }
        throw new Error(response.message || '删除待办事项失败');
      })
    );
  }

  completeTodo(id: number): Observable<Todo> {
    return this.http.patch<ApiResponse<Todo>>(`${this.baseUrl}/${id}/complete`, {}).pipe(
      map(response => {
        if (response.success && response.data) {
          return response.data;
        }
        throw new Error(response.message || '标记完成失败');
      })
    );
  }

  incompleteTodo(id: number): Observable<Todo> {
    return this.http.patch<ApiResponse<Todo>>(`${this.baseUrl}/${id}/incomplete`, {}).pipe(
      map(response => {
        if (response.success && response.data) {
          return response.data;
        }
        throw new Error(response.message || '标记未完成失败');
      })
    );
  }

  searchTodos(keyword: string, page: number = 0, size: number = 20): Observable<PageResult<Todo>> {
    return this.http.get<ApiResponse<PageResult<Todo>>>(`${this.baseUrl}/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`).pipe(
      map(response => {
        if (response.success && response.data) {
          return response.data;
        }
        throw new Error(response.message || '搜索待办事项失败');
      })
    );
  }

  getTodosByStatus(completed: boolean, page: number = 0, size: number = 20): Observable<PageResult<Todo>> {
    return this.http.get<ApiResponse<PageResult<Todo>>>(`${this.baseUrl}/status?completed=${completed}&page=${page}&size=${size}`).pipe(
      map(response => {
        if (response.success && response.data) {
          return response.data;
        }
        throw new Error(response.message || '获取待办事项失败');
      })
    );
  }

  getTodosByPriority(priority: string): Observable<Todo[]> {
    return this.http.get<ApiResponse<Todo[]>>(`${this.baseUrl}/priority/${priority}`).pipe(
      map(response => {
        if (response.success && response.data) {
          return response.data;
        }
        throw new Error(response.message || '获取待办事项失败');
      })
    );
  }

  getOverdueTodos(): Observable<Todo[]> {
    return this.http.get<ApiResponse<Todo[]>>(`${this.baseUrl}/overdue`).pipe(
      map(response => {
        if (response.success && response.data) {
          return response.data;
        }
        throw new Error(response.message || '获取逾期待办事项失败');
      })
    );
  }

  getTodayTodos(): Observable<Todo[]> {
    return this.http.get<ApiResponse<Todo[]>>(`${this.baseUrl}/today`).pipe(
      map(response => {
        if (response.success && response.data) {
          return response.data;
        }
        throw new Error(response.message || '获取今日待办事项失败');
      })
    );
  }

  getTodoStats(): Observable<{total: number, completed: number, pending: number, overdue: number}> {
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/stats`).pipe(
      map(response => {
        if (response.success && response.data) {
          return {
            total: response.data.total || 0,
            completed: response.data.completed || 0,
            pending: response.data.pending || 0,
            overdue: response.data.overdue || 0
          };
        }
        throw new Error(response.message || '获取统计信息失败');
      })
    );
  }

  batchUpdateStatus(ids: number[], completed: boolean): Observable<number> {
    return this.http.patch<ApiResponse<number>>(`${this.baseUrl}/batch/status`, {
      ids: ids,
      completed: completed
    }).pipe(
      map(response => {
        if (response.success && response.data !== undefined) {
          return response.data;
        }
        throw new Error(response.message || '批量更新失败');
      })
    );
  }

  deleteAllTodos(): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/all`).pipe(
      map(response => {
        if (response.success) {
          return;
        }
        throw new Error(response.message || '删除所有待办事项失败');
      })
    );
  }
}