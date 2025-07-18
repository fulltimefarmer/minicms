// 此文件为应用的主路由配置，定义了各页面组件的路由路径及守卫。
// 包含登录、首页、用户管理、部门管理、资产管理等路由及默认跳转逻辑。
import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { PermissionManagementComponent } from './pages/permission-management/permission-management.component';
import { UserManagementComponent } from './pages/user-management/user-management.component';
import { DepartmentManagementComponent } from './pages/department-management/department-management.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { AssetManagementComponent } from './pages/asset-management/asset-management.component';
import { DictTypeManagementComponent } from './pages/dict-management/dict-type-management.component';
import { DictItemManagementComponent } from './pages/dict-management/dict-item-management.component';
import { TodoManagementComponent } from './pages/todo-management/todo-management.component';
import { AuthGuard } from './services/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'todos', component: TodoManagementComponent, canActivate: [AuthGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'users', component: UserManagementComponent, canActivate: [AuthGuard] },
  { path: 'departments', component: DepartmentManagementComponent, canActivate: [AuthGuard] },
  { path: 'assets', component: AssetManagementComponent, canActivate: [AuthGuard] },
  { path: 'permission-management', component: PermissionManagementComponent, canActivate: [AuthGuard] },
  { path: 'dict-types', component: DictTypeManagementComponent, canActivate: [AuthGuard] },
  { path: 'dict-items', component: DictItemManagementComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' }, // 默认显示首页
  { path: '**', redirectTo: 'login' }
];
