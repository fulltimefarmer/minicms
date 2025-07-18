// 此文件为待办事项管理页面组件，负责待办事项的增删改查、筛选、状态切换等操作及界面展示。
// 包含待办事项数据结构定义，表单处理，筛选与优先级显示等核心逻辑。
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TodoService } from '../../services/todo.service';

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

@Component({
  selector: 'app-todo-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="todo-management">
      <div class="header">
        <h2>待办事项管理</h2>
        <button class="btn btn-primary" (click)="openCreateModal()">添加待办事项</button>
      </div>

      <!-- 筛选和搜索 -->
      <div class="filters">
        <div class="filter-group">
          <label>状态:</label>
          <select [(ngModel)]="filter.status" (change)="applyFilters()">
            <option value="">全部</option>
            <option value="false">待完成</option>
            <option value="true">已完成</option>
          </select>
        </div>
        <div class="filter-group">
          <label>优先级:</label>
          <select [(ngModel)]="filter.priority" (change)="applyFilters()">
            <option value="">全部</option>
            <option value="HIGH">高</option>
            <option value="MEDIUM">中</option>
            <option value="LOW">低</option>
          </select>
        </div>
        <div class="filter-group">
          <label>搜索:</label>
          <input type="text" [(ngModel)]="filter.search" (input)="applyFilters()" placeholder="搜索标题或描述...">
        </div>
      </div>

      <!-- 待办事项列表 -->
      <div class="todo-grid">
        <div class="todo-card" *ngFor="let todo of filteredTodos" [class.completed]="todo.completed">
          <div class="todo-header">
            <div class="todo-title">
              <input type="checkbox" [checked]="todo.completed" (change)="toggleComplete(todo)">
              <span [class.strikethrough]="todo.completed">{{ todo.title }}</span>
            </div>
            <div class="todo-actions">
              <span class="priority-badge" [class]="'priority-' + todo.priority.toLowerCase()">
                {{ getPriorityText(todo.priority) }}
              </span>
              <button class="btn-sm btn-secondary" (click)="editTodo(todo)">编辑</button>
              <button class="btn-sm btn-danger" (click)="deleteTodo(todo.id!)">删除</button>
            </div>
          </div>
          <div class="todo-description" *ngIf="todo.description">
            {{ todo.description }}
          </div>
          <div class="todo-meta">
            <span *ngIf="todo.dueDate" class="due-date" [class.overdue]="isOverdue(todo.dueDate)">
              截止: {{ formatDate(todo.dueDate) }}
            </span>
            <span class="created-at">创建: {{ formatDate(todo.createdAt!) }}</span>
          </div>
        </div>
      </div>

      <!-- 创建/编辑模态框 -->
      <div class="modal" *ngIf="showModal" (click)="closeModal($event)">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ editingTodo ? '编辑待办事项' : '创建待办事项' }}</h3>
            <button class="close-btn" (click)="closeModal()">&times;</button>
          </div>
          <form (ngSubmit)="saveTodo()" class="modal-body">
            <div class="form-group">
              <label>标题 *</label>
              <input type="text" [(ngModel)]="currentTodo.title" name="title" required placeholder="请输入标题">
            </div>
            <div class="form-group">
              <label>描述</label>
              <textarea [(ngModel)]="currentTodo.description" name="description" rows="3" placeholder="请输入描述"></textarea>
            </div>
            <div class="form-group">
              <label>优先级</label>
              <select [(ngModel)]="currentTodo.priority" name="priority">
                <option value="LOW">低</option>
                <option value="MEDIUM">中</option>
                <option value="HIGH">高</option>
              </select>
            </div>
            <div class="form-group">
              <label>截止日期</label>
              <input type="datetime-local" [(ngModel)]="currentTodo.dueDate" name="dueDate">
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="closeModal()">取消</button>
              <button type="submit" class="btn btn-primary">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .todo-management {
      padding: 2rem;
      max-width: 1200px;
      margin: 0 auto;
    }

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }

    .header h2 {
      margin: 0;
      color: #333;
    }

    .filters {
      display: flex;
      gap: 1rem;
      margin-bottom: 2rem;
      padding: 1rem;
      background: #f8f9fa;
      border-radius: 8px;
      flex-wrap: wrap;
    }

    .filter-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .filter-group label {
      font-weight: 500;
      color: #555;
    }

    .filter-group select,
    .filter-group input {
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      min-width: 150px;
    }

    .todo-grid {
      display: grid;
      gap: 1rem;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    }

    .todo-card {
      background: white;
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      padding: 1rem;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      transition: transform 0.2s, box-shadow 0.2s;
    }

    .todo-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0,0,0,0.15);
    }

    .todo-card.completed {
      opacity: 0.7;
      background: #f8f9fa;
    }

    .todo-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 0.5rem;
    }

    .todo-title {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      flex: 1;
    }

    .todo-title span {
      font-weight: 500;
      color: #333;
    }

    .strikethrough {
      text-decoration: line-through;
      color: #888 !important;
    }

    .todo-actions {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }

    .priority-badge {
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      font-size: 0.75rem;
      font-weight: 500;
    }

    .priority-high {
      background: #ffebee;
      color: #c62828;
    }

    .priority-medium {
      background: #fff3e0;
      color: #ef6c00;
    }

    .priority-low {
      background: #e8f5e8;
      color: #2e7d32;
    }

    .todo-description {
      color: #666;
      margin-bottom: 0.5rem;
      line-height: 1.4;
    }

    .todo-meta {
      display: flex;
      justify-content: space-between;
      font-size: 0.75rem;
      color: #888;
    }

    .due-date.overdue {
      color: #d32f2f;
      font-weight: 500;
    }

    .btn {
      padding: 0.75rem 1.5rem;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 1rem;
      transition: background-color 0.3s;
    }

    .btn-primary {
      background: #1976d2;
      color: white;
    }

    .btn-primary:hover {
      background: #1565c0;
    }

    .btn-secondary {
      background: #6c757d;
      color: white;
    }

    .btn-secondary:hover {
      background: #5a6268;
    }

    .btn-danger {
      background: #dc3545;
      color: white;
    }

    .btn-danger:hover {
      background: #c82333;
    }

    .btn-sm {
      padding: 0.375rem 0.75rem;
      font-size: 0.875rem;
    }

    .modal {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0,0,0,0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
    }

    .modal-content {
      background: white;
      border-radius: 8px;
      max-width: 500px;
      width: 90%;
      max-height: 80vh;
      overflow-y: auto;
    }

    .modal-header {
      padding: 1rem 1.5rem;
      border-bottom: 1px solid #e0e0e0;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .modal-header h3 {
      margin: 0;
    }

    .close-btn {
      background: none;
      border: none;
      font-size: 1.5rem;
      cursor: pointer;
      color: #666;
    }

    .modal-body {
      padding: 1.5rem;
    }

    .form-group {
      margin-bottom: 1rem;
    }

    .form-group label {
      display: block;
      margin-bottom: 0.5rem;
      font-weight: 500;
      color: #333;
    }

    .form-group input,
    .form-group select,
    .form-group textarea {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 1rem;
    }

    .form-group textarea {
      resize: vertical;
    }

    .modal-footer {
      display: flex;
      gap: 1rem;
      justify-content: flex-end;
      margin-top: 1.5rem;
    }

    @media (max-width: 768px) {
      .todo-management {
        padding: 1rem;
      }

      .filters {
        flex-direction: column;
      }

      .todo-grid {
        grid-template-columns: 1fr;
      }

      .header {
        flex-direction: column;
        gap: 1rem;
        align-items: stretch;
      }
    }
  `]
})
export class TodoManagementComponent implements OnInit {
  todos: Todo[] = [];
  filteredTodos: Todo[] = [];
  showModal = false;
  editingTodo: Todo | null = null;
  currentTodo: Todo = {
    title: '',
    description: '',
    completed: false,
    priority: 'MEDIUM'
  };

  filter = {
    status: '',
    priority: '',
    search: ''
  };

  constructor(private todoService: TodoService) {}

  ngOnInit() {
    this.loadTodos();
  }

  loadTodos() {
    this.todoService.getTodos().subscribe({
      next: (todos) => {
        this.todos = todos;
        this.applyFilters();
      },
      error: (error) => {
        console.error('加载待办事项失败:', error);
      }
    });
  }

  applyFilters() {
    this.filteredTodos = this.todos.filter(todo => {
      const statusMatch = this.filter.status === '' || 
        todo.completed.toString() === this.filter.status;
      
      const priorityMatch = this.filter.priority === '' || 
        todo.priority === this.filter.priority;
      
      const searchMatch = this.filter.search === '' ||
        todo.title.toLowerCase().includes(this.filter.search.toLowerCase()) ||
        (todo.description && todo.description.toLowerCase().includes(this.filter.search.toLowerCase()));
      
      return statusMatch && priorityMatch && searchMatch;
    });

    // 排序：未完成的在前，按优先级和创建时间排序
    this.filteredTodos.sort((a, b) => {
      if (a.completed !== b.completed) {
        return a.completed ? 1 : -1;
      }
      
      const priorityOrder = { HIGH: 3, MEDIUM: 2, LOW: 1 };
      const priorityDiff = priorityOrder[b.priority] - priorityOrder[a.priority];
      
      if (priorityDiff !== 0) {
        return priorityDiff;
      }
      
      return new Date(b.createdAt!).getTime() - new Date(a.createdAt!).getTime();
    });
  }

  openCreateModal() {
    this.editingTodo = null;
    this.currentTodo = {
      title: '',
      description: '',
      completed: false,
      priority: 'MEDIUM'
    };
    this.showModal = true;
  }

  editTodo(todo: Todo) {
    this.editingTodo = todo;
    this.currentTodo = { ...todo };
    this.showModal = true;
  }

  saveTodo() {
    if (!this.currentTodo.title.trim()) {
      return;
    }

    if (this.editingTodo) {
      this.todoService.updateTodo(this.editingTodo.id!, this.currentTodo).subscribe({
        next: () => {
          this.loadTodos();
          this.closeModal();
        },
        error: (error) => {
          console.error('更新待办事项失败:', error);
        }
      });
    } else {
      this.todoService.createTodo(this.currentTodo).subscribe({
        next: () => {
          this.loadTodos();
          this.closeModal();
        },
        error: (error) => {
          console.error('创建待办事项失败:', error);
        }
      });
    }
  }

  deleteTodo(id: number) {
    if (confirm('确定要删除这个待办事项吗？')) {
      this.todoService.deleteTodo(id).subscribe({
        next: () => {
          this.loadTodos();
        },
        error: (error) => {
          console.error('删除待办事项失败:', error);
        }
      });
    }
  }

  toggleComplete(todo: Todo) {
    const updatedTodo = { ...todo, completed: !todo.completed };
    this.todoService.updateTodo(todo.id!, updatedTodo).subscribe({
      next: () => {
        this.loadTodos();
      },
      error: (error) => {
        console.error('更新待办事项状态失败:', error);
      }
    });
  }

  closeModal(event?: Event) {
    if (event && event.target !== event.currentTarget) {
      return;
    }
    this.showModal = false;
    this.editingTodo = null;
  }

  getPriorityText(priority: string): string {
    const map: { [key: string]: string } = {
      HIGH: '高',
      MEDIUM: '中',
      LOW: '低'
    };
    return map[priority] || priority;
  }

  formatDate(dateStr: string): string {
    const date = new Date(dateStr);
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  isOverdue(dueDate: string): boolean {
    return new Date(dueDate) < new Date();
  }
}