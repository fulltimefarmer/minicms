import { Routes } from '@angular/router';
import { LoginComponent } from './login.component';
import { PermissionManagementComponent } from './permission-management.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'permission-management', component: PermissionManagementComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];
