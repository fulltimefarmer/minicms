/**
 * 导入所需的Angular核心模块和服务
 */
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../environments/environment';

/**
 * 登录请求接口
 * 定义用户登录时需要提供的凭据
 */
export interface LoginRequest {
  username: string;  // 用户名
  password: string;  // 密码
}

/**
 * 登录响应接口
 * 定义登录成功后服务器返回的用户信息
 */
export interface LoginResponse {
  token: string;        // JWT令牌
  tokenType: string;    // 令牌类型，通常为"Bearer"
  userId: number;       // 用户ID
  username: string;     // 用户名
  email: string;        // 电子邮箱
  nickname: string;     // 用户昵称
  avatar: string;       // 头像URL
  roles: string[];      // 用户角色列表
  permissions: string[]; // 用户权限列表
}

/**
 * API响应接口
 * 定义后端API的标准响应格式
 */
export interface ApiResponse<T> {
  success: boolean;  // 请求是否成功
  message: string;   // 响应消息
  data: T;           // 响应数据
  code?: string;     // 可选的错误代码
}

/**
 * 认证服务
 * 
 * 负责处理用户认证相关的功能，包括：
 * - 用户登录和登出
 * - 令牌管理和验证
 * - 用户权限检查
 * - 用户会话状态管理
 */
@Injectable({
  providedIn: 'root'  // 在根注入器中提供服务，使其在整个应用中可用
})
export class AuthService {
  /** API基础URL，从环境配置中获取 */
  private apiUrl = `${environment.apiUrl}/auth`;
  
  /** 当前用户信息的BehaviorSubject，用于在组件间共享用户状态 */
  private currentUserSubject: BehaviorSubject<LoginResponse | null>;
  
  /** 当前用户信息的Observable，组件可以订阅此Observable以获取用户状态更新 */
  public currentUser: Observable<LoginResponse | null>;

  /**
   * 构造函数
   * @param http HttpClient服务，用于发送HTTP请求
   * @param platformId 平台ID，用于区分浏览器和服务器环境
   */
  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    console.log('AuthService: 初始化认证服务');
    // 从本地存储中获取用户数据
    const userData = this.getStoredUser();
    // 创建BehaviorSubject，初始值为存储的用户数据
    this.currentUserSubject = new BehaviorSubject<LoginResponse | null>(userData);
    // 将BehaviorSubject转换为Observable
    this.currentUser = this.currentUserSubject.asObservable();
    
    if (userData) {
      console.log('AuthService: 从本地存储恢复用户会话', { username: userData.username });
    } else {
      console.log('AuthService: 没有找到存储的用户会话');
    }
  }

  /**
   * 获取当前用户值
   * @returns 当前用户信息，如果未登录则返回null
   */
  public get currentUserValue(): LoginResponse | null {
    return this.currentUserSubject.value;
  }

  /**
   * 用户登录
   * 发送登录请求到服务器，并在成功时存储用户信息
   * 
   * @param credentials 登录凭据（用户名和密码）
   * @returns 包含用户信息的Observable
   */
  login(credentials: LoginRequest): Observable<LoginResponse> {
    console.log('AuthService: 尝试登录', { username: credentials.username });
    
    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/login`, credentials)
      .pipe(
        map(response => {
          if (response.success && response.data) {
            console.log('AuthService: 登录成功', { username: response.data.username });
            return response.data;
          } else {
            console.error('AuthService: 登录失败', { message: response.message });
            throw new Error(response.message || '登录失败');
          }
        }),
        tap(user => {
          // 存储用户信息和token到localStorage
          if (isPlatformBrowser(this.platformId)) {
            console.log('AuthService: 存储用户会话到本地存储');
            localStorage.setItem('currentUser', JSON.stringify(user));
            localStorage.setItem('token', user.token);
          }
          // 更新当前用户Subject
          this.currentUserSubject.next(user);
        })
      );
  }

  logout(): Observable<any> {
    const token = this.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/logout`, {}, { headers })
      .pipe(
        tap(() => {
          this.clearUserData();
        })
      );
  }

  /**
   * 通过解析JWT token判断是否有效（不需要调用后端接口）
   */
  isTokenValid(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    // 如果是mock模式，直接返回true
    if (environment.mock && token.includes('mock-jwt-token')) {
      return true;
    }

    try {
      const payload = this.parseJwtPayload(token);
      const currentTime = Math.floor(Date.now() / 1000);
      
      // 检查token是否过期
      if (payload.exp && payload.exp < currentTime) {
        this.clearUserData();
        return false;
      }
      
      return true;
    } catch (error) {
      console.error('Token解析失败:', error);
      
      // 如果是mock模式，即使解析失败也返回true
      if (environment.mock && token.includes('mock-jwt-token')) {
        return true;
      }
      
      this.clearUserData();
      return false;
    }
  }

  /**
   * 解析JWT token的payload部分
   */
  private parseJwtPayload(token: string): any {
    // 如果是mock token，返回模拟的payload
    if (environment.mock && token.includes('mock-jwt-token')) {
      return {
        sub: 'admin',
        exp: Math.floor(Date.now() / 1000) + 3600, // 1小时后过期
        permissions: ['*']
      };
    }
    
    const parts = token.split('.');
    if (parts.length !== 3) {
      throw new Error('Invalid JWT token format');
    }
    
    // 解码payload部分
    const payload = parts[1];
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decoded);
  }

  /**
   * 从token中获取用户权限
   */
  getUserPermissions(): string[] {
    const token = this.getToken();
    if (!token || !this.isTokenValid()) {
      return [];
    }

    try {
      const payload = this.parseJwtPayload(token);
      return payload.permissions || [];
    } catch (error) {
      console.error('获取权限失败:', error);
      return [];
    }
  }

  /**
   * 检查用户是否有指定权限
   */
  hasPermission(permission: string): boolean {
    const permissions = this.getUserPermissions();
    return permissions.includes(permission);
  }

  refreshToken(): Observable<string> {
    const token = this.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/refresh`, {}, { headers })
      .pipe(
        map(response => {
          if (response.success && response.data) {
            return response.data;
          } else {
            throw new Error(response.message || '刷新token失败');
          }
        }),
        tap((newToken: string) => {
          if (isPlatformBrowser(this.platformId)) {
            localStorage.setItem('token', newToken);
            // 更新当前用户信息中的token
            const currentUser = this.currentUserValue;
            if (currentUser) {
              currentUser.token = newToken;
              localStorage.setItem('currentUser', JSON.stringify(currentUser));
              this.currentUserSubject.next(currentUser);
            }
          }
        })
      );
  }

  isLoggedIn(): boolean {
    return !!this.getToken() && !!this.currentUserValue && this.isTokenValid();
  }

  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem('token');
    }
    return null;
  }

  private getStoredUser(): LoginResponse | null {
    if (isPlatformBrowser(this.platformId)) {
      const userJson = localStorage.getItem('currentUser');
      if (userJson) {
        try {
          return JSON.parse(userJson);
        } catch (e) {
          console.error('Error parsing stored user data:', e);
          this.clearUserData();
        }
      }
    }
    return null;
  }

  public clearUserData(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('currentUser');
      localStorage.removeItem('token');
    }
    this.currentUserSubject.next(null);
  }
}