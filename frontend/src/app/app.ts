import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterModule } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class AppComponent implements OnInit {
  title = 'frontend-app';
  sidebarCollapsed = false;
  currentRoute = '';
  
  // 页面标题映射
  private pageTitles: { [key: string]: string } = {
    '/todos': '待办事项',
    '/dashboard': '仪表板',
    '/users': '用户管理',
    '/departments': '部门管理',
    '/assets': '资产管理',
    '/permission-management': '权限管理',
    '/dict-types': '数据字典',
    '/dict-items': '数据字典项',
    '/login': '登录'
  };

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    // 监听路由变化，更新当前路由
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.currentRoute = event.url;
    });

    // 检查本地存储中的侧边栏状态
    const sidebarState = localStorage.getItem('sidebarCollapsed');
    if (sidebarState) {
      this.sidebarCollapsed = JSON.parse(sidebarState);
    }
  }

  getCurrentUser() {
    return this.authService.currentUserValue;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  toggleSidebar() {
    this.sidebarCollapsed = !this.sidebarCollapsed;
    // 保存侧边栏状态到本地存储
    localStorage.setItem('sidebarCollapsed', JSON.stringify(this.sidebarCollapsed));
  }

  getPageTitle(): string {
    // 根据当前路由返回页面标题
    const baseRoute = this.currentRoute.split('?')[0]; // 移除查询参数
    return this.pageTitles[baseRoute] || '权限管理系统';
  }
}
