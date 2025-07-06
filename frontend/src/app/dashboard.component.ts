import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService, LoginResponse } from './auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard-container">
      <header class="dashboard-header">
        <h1>欢迎回来，{{currentUser?.nickname || currentUser?.username}}！</h1>
        <div class="user-info">
          <span>{{currentUser?.email}}</span>
          <button (click)="logout()" class="logout-btn">退出登录</button>
        </div>
      </header>
      
      <div class="dashboard-content">
        <div class="nav-grid">
          <div class="nav-card" (click)="navigateTo('/users')">
            <h3>用户管理</h3>
            <p>管理系统用户</p>
          </div>
          <div class="nav-card" (click)="navigateTo('/departments')">
            <h3>部门管理</h3>
            <p>管理组织架构</p>
          </div>
          <div class="nav-card" (click)="navigateTo('/permission-management')">
            <h3>权限管理</h3>
            <p>管理角色和权限</p>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      padding: 2rem;
      max-width: 1200px;
      margin: 0 auto;
    }
    
    .dashboard-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
      padding-bottom: 1rem;
      border-bottom: 1px solid #e0e0e0;
    }
    
    .user-info {
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    
    .logout-btn {
      padding: 0.5rem 1rem;
      background: #dc3545;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
    
    .logout-btn:hover {
      background: #c82333;
    }
    
    .nav-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 2rem;
    }
    
    .nav-card {
      padding: 2rem;
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      cursor: pointer;
      transition: box-shadow 0.2s;
      background: white;
    }
    
    .nav-card:hover {
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }
    
    .nav-card h3 {
      margin: 0 0 1rem 0;
      color: #333;
    }
    
    .nav-card p {
      margin: 0;
      color: #666;
    }
  `]
})
export class DashboardComponent implements OnInit {
  currentUser: LoginResponse | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.currentUserValue;
    
    // 监听用户状态变化
    this.authService.currentUser.subscribe(user => {
      this.currentUser = user;
      if (!user) {
        this.router.navigate(['/login']);
      }
    });
  }

  navigateTo(path: string) {
    this.router.navigate([path]);
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('退出登录失败:', error);
        // 即使退出登录API失败，也清除本地数据并跳转
        this.router.navigate(['/login']);
      }
    });
  }
}