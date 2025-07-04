import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';

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
  private apiUrl = 'http://localhost:8080/api/auth'; // 根据实际后端地址修改
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
          // 存储用户信息和token
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

  validateToken(): Observable<boolean> {
    const token = this.getToken();
    if (!token) {
      return new Observable(observer => {
        observer.next(false);
        observer.complete();
      });
    }

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<ApiResponse<boolean>>(`${this.apiUrl}/validate`, { headers })
      .pipe(
        map(response => response.success && response.data),
        tap(isValid => {
          if (!isValid) {
            this.clearUserData();
          }
        })
      );
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
        tap(newToken => {
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
    return !!this.getToken() && !!this.currentUserValue;
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