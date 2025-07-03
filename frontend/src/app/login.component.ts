import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

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
    .login-container {
      max-width: 350px;
      margin: 60px auto;
      padding: 2rem;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.08);
      background: #fff;
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }
    label { display: block; margin-bottom: 0.5rem; }
    input { width: 100%; padding: 0.5rem; margin-bottom: 1rem; }
    button { width: 100%; padding: 0.75rem; background: #1976d2; color: #fff; border: none; border-radius: 4px; font-size: 1rem; }
    .error { color: #d32f2f; margin-top: 1rem; }
  `]
})
export class LoginComponent {
  username = '';
  password = '';
  loginError = false;

  onSubmit() {
    // 简单演示，用户名 admin 密码 123456
    if (this.username === 'admin' && this.password === '123456') {
      this.loginError = false;
      alert('登录成功！');
    } else {
      this.loginError = true;
    }
  }
} 