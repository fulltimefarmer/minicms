import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService, LoginResponse } from '../../services/auth.service';
import { TodoService, Todo } from '../../services/todo.service';
import { filter, Subscription } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard-container">
      <header class="dashboard-header">
        <h1>欢迎回来，{{currentUser?.nickname || currentUser?.username}}！</h1>
        <div class="user-info">
          <span class="user-email">{{currentUser?.email}}</span>
        </div>
      </header>
      
      <div class="dashboard-content">
        <!-- 待办事项列表 -->
        <div class="todo-section">
          <h2>我的待办事项</h2>
          
          <div class="loading-error-container" *ngIf="loading || error">
            <div class="loading" *ngIf="loading">加载中...</div>
            <div class="error" *ngIf="error">{{ error }}</div>
          </div>
          
          <div class="todo-list" *ngIf="!loading && !error">
            <div class="empty-state" *ngIf="todos.length === 0">
              <div class="empty-icon">📝</div>
              <p>暂无待办事项</p>
            </div>
            
            <table class="todo-table" *ngIf="todos.length > 0">
              <thead>
                <tr>
                  <th>状态</th>
                  <th>标题</th>
                  <th>优先级</th>
                  <th>截止日期</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let todo of todos" [class.completed]="todo.completed">
                  <td class="status-cell">
                    <span class="status-icon" [class.completed]="todo.completed">
                      {{ todo.completed ? '✓' : '○' }}
                    </span>
                  </td>
                  <td class="title-cell">
                    <div class="todo-title">{{ todo.title }}</div>
                    <div class="todo-description" *ngIf="todo.description">{{ todo.description }}</div>
                  </td>
                  <td class="priority-cell">
                    <span class="priority-badge" [class]="getPriorityClass(todo.priority)">
                      {{ getPriorityText(todo.priority) }}
                    </span>
                  </td>
                  <td class="date-cell">{{ formatDate(todo.dueDate) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 快速导航卡片已移除 -->
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      padding: 2rem;
      max-width: 1200px;
      margin: 0 auto;
      min-height: 100%;
      display: flex;
      flex-direction: column;
    }
    
    .dashboard-header {
      margin-bottom: 2rem;
      padding-bottom: 1.5rem;
      border-bottom: 2px solid #e0e0e0;
    }
    
    .dashboard-header h1 {
      font-size: 2rem;
      color: #333;
      margin-bottom: 0.5rem;
      font-weight: 600;
    }
    
    .user-info {
      display: flex;
      align-items: center;
    }
    
    .user-email {
      color: #666;
      font-size: 1rem;
      background: #f0f0f0;
      padding: 0.25rem 0.75rem;
      border-radius: 16px;
    }
    
    .dashboard-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 2rem;
    }
    
    /* 待办事项列表样式 */
    .todo-section {
      background: white;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
      padding: 1.5rem;
      margin-bottom: 2rem;
    }
    
    .todo-section h2 {
      font-size: 1.5rem;
      color: #333;
      margin-top: 0;
      margin-bottom: 1.5rem;
      font-weight: 600;
      border-bottom: 1px solid #eee;
      padding-bottom: 0.75rem;
    }
    
    .loading-error-container {
      padding: 2rem;
      text-align: center;
    }
    
    .loading {
      color: #666;
      font-size: 1rem;
    }
    
    .error {
      color: #e74c3c;
      font-size: 1rem;
      background: #ffeaea;
      padding: 1rem;
      border-radius: 8px;
    }
    
    .empty-state {
      text-align: center;
      padding: 3rem 1rem;
      color: #999;
    }
    
    .empty-icon {
      font-size: 3rem;
      margin-bottom: 1rem;
    }
    
    .todo-table {
      width: 100%;
      border-collapse: collapse;
    }
    
    .todo-table th {
      text-align: left;
      padding: 1rem;
      border-bottom: 2px solid #eee;
      color: #666;
      font-weight: 600;
      font-size: 0.9rem;
    }
    
    .todo-table td {
      padding: 1rem;
      border-bottom: 1px solid #eee;
      vertical-align: top;
    }
    
    .todo-table tr.completed {
      background-color: #f9f9f9;
    }
    
    .todo-table tr:hover {
      background-color: #f5f5f5;
    }
    
    .status-cell {
      width: 50px;
      text-align: center;
    }
    
    .status-icon {
      display: inline-block;
      width: 24px;
      height: 24px;
      line-height: 24px;
      text-align: center;
      border-radius: 50%;
      border: 2px solid #ddd;
      color: transparent;
    }
    
    .status-icon.completed {
      border-color: #27ae60;
      background-color: #27ae60;
      color: white;
    }
    
    .title-cell {
      max-width: 400px;
    }
    
    .todo-title {
      font-weight: 500;
      color: #333;
      margin-bottom: 0.25rem;
    }
    
    tr.completed .todo-title {
      text-decoration: line-through;
      color: #999;
    }
    
    .todo-description {
      font-size: 0.9rem;
      color: #666;
    }
    
    .priority-cell {
      width: 100px;
    }
    
    .priority-badge {
      display: inline-block;
      padding: 0.25rem 0.75rem;
      border-radius: 16px;
      font-size: 0.8rem;
      font-weight: 500;
      text-align: center;
    }
    
    .priority-low {
      background-color: #e8f5e9;
      color: #2e7d32;
    }
    
    .priority-medium {
      background-color: #fff8e1;
      color: #f57f17;
    }
    
    .priority-high {
      background-color: #ffebee;
      color: #c62828;
    }
    
    .date-cell {
      width: 180px;
      white-space: nowrap;
      color: #666;
      font-size: 0.9rem;
    }
    
    /* 导航卡片样式 */
    h2 {
      font-size: 1.5rem;
      color: #333;
      margin-top: 0;
      margin-bottom: 1.5rem;
      font-weight: 600;
    }
    
    .nav-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 1.5rem;
    }
    
    .nav-card {
      padding: 2rem;
      border: 1px solid #e0e0e0;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.3s ease;
      background: white;
      text-align: center;
    }
    
    .nav-card:hover {
      box-shadow: 0 8px 24px rgba(0,0,0,0.15);
      transform: translateY(-2px);
      border-color: #1976d2;
    }
    
    .nav-icon {
      font-size: 2.5rem;
      margin-bottom: 1rem;
    }
    
    .nav-card h3 {
      margin: 0 0 1rem 0;
      color: #333;
      font-size: 1.25rem;
      font-weight: 600;
    }
    
    .nav-card p {
      margin: 0;
      color: #666;
      font-size: 0.95rem;
      line-height: 1.5;
    }
    
    @media (max-width: 768px) {
      .dashboard-container {
        padding: 1rem;
      }
      
      .dashboard-header h1 {
        font-size: 1.5rem;
      }
      
      .todo-table th, .todo-table td {
        padding: 0.75rem 0.5rem;
      }
      
      .date-cell {
        display: none;
      }
      
      .nav-grid {
        grid-template-columns: 1fr;
      }
      
      .nav-card {
        padding: 1.5rem;
      }
    }
  `]
})
export class DashboardComponent implements OnInit, OnDestroy {
  currentUser: LoginResponse | null = null;
  todos: Todo[] = [];
  loading = false;
  error = '';
  private subscriptions: Subscription[] = [];

  constructor(
    private authService: AuthService,
    private todoService: TodoService,
    private router: Router
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.currentUserValue;
    
    // 监听用户状态变化
    const userSub = this.authService.currentUser.subscribe((user: LoginResponse | null) => {
      this.currentUser = user;
      if (!user) {
        this.router.navigate(['/login']);
      }
    });
    this.subscriptions.push(userSub);

    // 监听路由事件，每次导航到dashboard时重新加载数据
    const routerSub = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      if (event.url === '/dashboard' || event.url === '/') {
        console.log('重新加载待办事项列表');
        this.loadTodos();
      }
    });
    this.subscriptions.push(routerSub);

    // 初始加载待办事项列表
    this.loadTodos();
  }
  
  ngOnDestroy() {
    // 清理订阅，防止内存泄漏
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  loadTodos() {
    this.loading = true;
    this.error = '';
    
    this.todoService.getTodos().subscribe({
      next: (data) => {
        this.todos = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('加载待办事项失败', err);
        this.error = '加载待办事项失败，请稍后再试';
        this.loading = false;
      }
    });
  }

  navigateTo(path: string) {
    this.router.navigate([path]);
  }

  // 格式化日期显示
  formatDate(dateString: string | undefined): string {
    if (!dateString) return '无截止日期';
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  // 获取优先级显示文本
  getPriorityText(priority: string): string {
    const priorityMap: {[key: string]: string} = {
      'LOW': '低',
      'MEDIUM': '中',
      'HIGH': '高'
    };
    return priorityMap[priority] || priority;
  }

  // 获取优先级显示样式
  getPriorityClass(priority: string): string {
    const priorityClassMap: {[key: string]: string} = {
      'LOW': 'priority-low',
      'MEDIUM': 'priority-medium',
      'HIGH': 'priority-high'
    };
    return priorityClassMap[priority] || '';
  }
}