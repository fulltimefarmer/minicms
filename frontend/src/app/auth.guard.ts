import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
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

    // 检查用户是否已登录
    if (this.authService.isLoggedIn()) {
      // 验证token是否有效
      return this.authService.validateToken().pipe(
        map(isValid => {
          if (isValid) {
            return true;
          } else {
            // token无效，跳转到登录页
            this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
            return false;
          }
        }),
        catchError(() => {
          // 验证失败，跳转到登录页
          this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
          return of(false);
        })
      );
    } else {
      // 未登录，跳转到登录页
      this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
      return false;
    }
  }
}