import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService, LoginRequest } from './auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="login-container">
      <h2>登录</h2>
      <form #loginForm="ngForm" (ngSubmit)="onSubmit()">
        <div>
          <label for="username">用户名</label>
          <input id="username" name="username" [(ngModel)]="username" required />
        </div>
        <div>
          <label for="password">密码</label>
          <input id="password" name="password" type="password" [(ngModel)]="password" required />
        </div>
        <button type="submit" [disabled]="!loginForm.form.valid">登录</button>
      </form>
      <div *ngIf="loginError" class="error">用户名或密码错误</div>
    </div>
  `,
  styles: [`
    :host {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    }
    .login-container {
      max-width: 350px;
      width: 100%;
      padding: 2rem;
      border-radius: 8px;
      box-shadow: 0 4px 16px rgba(0,0,0,0.1);
      background: #fff;
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }
    h2 {
      text-align: center;
      margin-bottom: 1.5rem;
      color: #333;
      font-weight: 500;
    }
    label { 
      display: block; 
      margin-bottom: 0.5rem; 
      color: #555;
      font-weight: 500;
    }
    input { 
      width: 100%; 
      padding: 0.75rem; 
      margin-bottom: 1rem; 
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 1rem;
      box-sizing: border-box;
    }
    input:focus {
      outline: none;
      border-color: #1976d2;
      box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2);
    }
    button { 
      width: 100%; 
      padding: 0.75rem; 
      background: #1976d2; 
      color: #fff; 
      border: none; 
      border-radius: 4px; 
      font-size: 1rem; 
      cursor: pointer;
      transition: background-color 0.2s;
    }
    button:hover {
      background: #1565c0;
    }
    button:disabled {
      background: #ccc;
      cursor: not-allowed;
    }
    .error { 
      color: #d32f2f; 
      margin-top: 1rem; 
      text-align: center;
      font-size: 0.9rem;
    }
  `]
})
export class LoginComponent {
  username = '';
  password = '';
  loginError = false;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    // 如果已经登录，重定向到首页
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  onSubmit() {
    if (!this.username || !this.password) {
      this.loginError = true;
      return;
    }

    const loginRequest: LoginRequest = {
      username: this.username,
      password: this.password
    };

    this.authService.login(loginRequest).subscribe({
      next: (response) => {
        this.loginError = false;
        console.log('登录成功:', response);
        
        // 获取返回URL，如果没有则跳转到默认页面
        const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
        this.router.navigate([returnUrl]);
      },
      error: (error) => {
        console.error('登录失败:', error);
        this.loginError = true;
      }
    });
  }
} 