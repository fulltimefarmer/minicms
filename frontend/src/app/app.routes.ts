import { Routes } from '@angular/router';
import { LoginComponent } from './login.component';
import { PermissionManagementComponent } from './permission-management.component';
import { UserManagementComponent } from './user-management.component';
import { DepartmentManagementComponent } from './department-management.component';
import { DashboardComponent } from './dashboard.component';
import { AssetManagementComponent } from './asset-management.component';
import { AuthGuard } from './auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'users', component: UserManagementComponent, canActivate: [AuthGuard] },
  { path: 'departments', component: DepartmentManagementComponent, canActivate: [AuthGuard] },
  { path: 'assets', component: AssetManagementComponent, canActivate: [AuthGuard] },
  { path: 'permission-management', component: PermissionManagementComponent, canActivate: [AuthGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];
