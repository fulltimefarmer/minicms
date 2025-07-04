import { Routes } from '@angular/router';
import { LoginComponent } from './login.component';
import { PermissionManagementComponent } from './permission-management.component';
import { UserManagementComponent } from './user-management.component';

export const routes: Routes = [
  { path: 'users', component: UserManagementComponent },
  { path: 'login', component: LoginComponent },
  { path: 'permission-management', component: PermissionManagementComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '', redirectTo: 'users', pathMatch: 'full' }
];
