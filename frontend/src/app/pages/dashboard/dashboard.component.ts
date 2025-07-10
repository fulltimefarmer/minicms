import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService, LoginResponse } from '../../services/auth.service';

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
        <div class="stats-grid">
          <div class="stat-card">
            <h3>系统状态</h3>
            <p class="stat-value">正常运行</p>
            <p class="stat-label">System Status</p>
          </div>
          <div class="stat-card">
            <h3>在线用户</h3>
            <p class="stat-value">{{ currentUser ? '1' : '0' }}</p>
            <p class="stat-label">Online Users</p>
          </div>
          <div class="stat-card">
            <h3>权限等级</h3>
            <p class="stat-value">管理员</p>
            <p class="stat-label">Access Level</p>
          </div>
        </div>

        <div class="nav-grid">
          <div class="nav-card" (click)="navigateTo('/users')">
            <div class="nav-icon">👥</div>
            <h3>用户管理</h3>
            <p>管理系统用户、角色分配</p>
          </div>
          <div class="nav-card" (click)="navigateTo('/departments')">
            <div class="nav-icon">🏢</div>
            <h3>部门管理</h3>
            <p>管理组织架构和部门信息</p>
          </div>
          <div class="nav-card" (click)="navigateTo('/assets')">
            <div class="nav-icon">📦</div>
            <h3>资产管理</h3>
            <p>管理公司资产和设备</p>
          </div>
          <div class="nav-card" (click)="navigateTo('/permission-management')">
            <div class="nav-icon">🔐</div>
            <h3>权限管理</h3>
            <p>管理角色权限和访问控制</p>
          </div>
          <div class="nav-card" (click)="navigateTo('/dict-types')">
            <div class="nav-icon">📚</div>
            <h3>数据字典</h3>
            <p>管理系统配置和字典数据</p>
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
    
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1.5rem;
      margin-bottom: 1rem;
    }
    
    .stat-card {
      background: white;
      padding: 1.5rem;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
      text-align: center;
      border: 1px solid #e0e0e0;
    }
    
    .stat-card h3 {
      font-size: 0.9rem;
      color: #666;
      margin-bottom: 0.5rem;
      font-weight: 500;
    }
    
    .stat-value {
      font-size: 2rem;
      font-weight: 700;
      color: #1976d2;
      margin-bottom: 0.25rem;
    }
    
    .stat-label {
      font-size: 0.8rem;
      color: #999;
      margin: 0;
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
      
      .stats-grid {
        grid-template-columns: 1fr;
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
export class DashboardComponent implements OnInit {
  currentUser: LoginResponse | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.currentUserValue;
    
    // 监听用户状态变化
    this.authService.currentUser.subscribe((user: LoginResponse | null) => {
      this.currentUser = user;
      if (!user) {
        this.router.navigate(['/login']);
      }
    });
  }

  navigateTo(path: string) {
    this.router.navigate([path]);
  }
}