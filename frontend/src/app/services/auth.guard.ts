// 此文件为路由守卫服务，负责判断用户是否有权限访问受保护的路由。
// 检查用户登录状态和token有效性，未登录时自动跳转到登录页。
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { AuthService } from './auth.service';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private router: Router,
    private authService: AuthService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    // 在服务端渲染时，允许所有路由通过
    if (!isPlatformBrowser(this.platformId)) {
      return true;
    }

    // 检查用户是否已登录且token有效
    if (this.authService.isLoggedIn()) {
      return true;
    } else {
      // 未登录或token无效，跳转到登录页
      this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
      return false;
    }
  }
}