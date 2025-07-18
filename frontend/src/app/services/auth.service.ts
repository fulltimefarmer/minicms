import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../environments/environment';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  userId: number;
  username: string;
  email: string;
  nickname: string;
  avatar: string;
  roles: string[];
  permissions: string[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  code?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject: BehaviorSubject<LoginResponse | null>;
  public currentUser: Observable<LoginResponse | null>;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    const userData = this.getStoredUser();
    this.currentUserSubject = new BehaviorSubject<LoginResponse | null>(userData);
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): LoginResponse | null {
    return this.currentUserSubject.value;
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/login`, credentials)
      .pipe(
        map(response => {
          if (response.success && response.data) {
            return response.data;
          } else {
            throw new Error(response.message || '登录失败');
          }
        }),
        tap(user => {
          // 存储用户信息和token到localStorage
          if (isPlatformBrowser(this.platformId)) {
            localStorage.setItem('currentUser', JSON.stringify(user));
            localStorage.setItem('token', user.token);
          }
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
      this.clearUserData();
      return false;
    }
  }

  /**
   * 解析JWT token的payload部分
   */
  private parseJwtPayload(token: string): any {
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

  private clearUserData(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('currentUser');
      localStorage.removeItem('token');
    }
    this.currentUserSubject.next(null);
  }
}