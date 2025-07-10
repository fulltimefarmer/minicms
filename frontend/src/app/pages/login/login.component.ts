import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService, LoginRequest } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="login-container">
      <div class="login-card">
        <h2>登录</h2>
        <form #loginForm="ngForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label for="username">用户名</label>
            <input id="username" name="username" [(ngModel)]="username" required />
          </div>
          <div class="form-group">
            <label for="password">密码</label>
            <input id="password" name="password" type="password" [(ngModel)]="password" required />
          </div>
          <button type="submit" [disabled]="!loginForm.form.valid">登录</button>
        </form>
        <div *ngIf="loginError" class="error">用户名或密码错误</div>
      </div>
    </div>
  `,
  styles: [`
    .login-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
      padding: 2rem;
      box-sizing: border-box;
    }
    
    .login-card {
      max-width: 400px;
      width: 100%;
      padding: 2.5rem;
      border-radius: 12px;
      box-shadow: 0 8px 32px rgba(0,0,0,0.12);
      background: #fff;
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
    }
    
    h2 {
      text-align: center;
      margin: 0 0 1rem 0;
      color: #333;
      font-weight: 600;
      font-size: 1.8rem;
    }
    
    .form-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }
    
    label { 
      color: #555;
      font-weight: 500;
      font-size: 0.9rem;
    }
    
    input { 
      width: 100%; 
      padding: 0.875rem; 
      border: 1px solid #ddd;
      border-radius: 6px;
      font-size: 1rem;
      box-sizing: border-box;
      transition: border-color 0.2s, box-shadow 0.2s;
    }
    
    input:focus {
      outline: none;
      border-color: #1976d2;
      box-shadow: 0 0 0 3px rgba(25, 118, 210, 0.1);
    }
    
    button { 
      width: 100%; 
      padding: 0.875rem; 
      background: #1976d2; 
      color: #fff; 
      border: none; 
      border-radius: 6px; 
      font-size: 1rem; 
      font-weight: 500;
      cursor: pointer;
      transition: background-color 0.2s, transform 0.1s;
      margin-top: 0.5rem;
    }
    
    button:hover:not(:disabled) {
      background: #1565c0;
      transform: translateY(-1px);
    }
    
    button:disabled {
      background: #ccc;
      cursor: not-allowed;
      transform: none;
    }
    
    .error { 
      color: #d32f2f; 
      text-align: center;
      font-size: 0.9rem;
      padding: 0.75rem;
      background: #ffebee;
      border-radius: 4px;
      border: 1px solid #ffcdd2;
    }
    
    @media (max-width: 480px) {
      .login-container {
        padding: 1rem;
      }
      
      .login-card {
        padding: 2rem;
      }
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
      next: (response: any) => {
        this.loginError = false;
        console.log('登录成功:', response);
        
        // 获取返回URL，如果没有则跳转到默认页面
        const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
        this.router.navigate([returnUrl]);
      },
      error: (error: any) => {
        console.error('登录失败:', error);
        this.loginError = true;
      }
    });
  }
} 