import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { User } from './permission.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected title = 'frontend-app';
  
  // 开发模式标志 - 生产环境请设置为 false
  private readonly DEV_MODE = true;

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    // 开发模式下自动设置测试用户
    if (this.DEV_MODE && isPlatformBrowser(this.platformId)) {
      this.setupTestUser();
    }
  }

  getCurrentUser(): User | null {
    if (isPlatformBrowser(this.platformId)) {
      const userStr = localStorage.getItem('currentUser');
      return userStr ? JSON.parse(userStr) : null;
    }
    return null;
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('currentUser');
      localStorage.removeItem('token');
    }
    this.router.navigate(['/login']);
  }

  private setupTestUser(): void {
    if (!localStorage.getItem('currentUser')) {
      const testUser = {
        userId: 1,
        username: 'testuser',
        email: 'test@example.com',
        nickname: '测试用户',
        avatar: '',
        roles: ['ADMIN'],
        permissions: ['USER_MANAGE', 'DEPT_MANAGE', 'ASSET_MANAGE'],
        token: 'test-token-for-dev',
        tokenType: 'Bearer'
      };
      
      localStorage.setItem('currentUser', JSON.stringify(testUser));
      localStorage.setItem('token', 'test-token-for-dev');
      
      console.log('🧪 开发模式: 已设置测试用户数据');
    }
  }
}
