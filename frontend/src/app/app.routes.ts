import { Routes } from '@angular/router';
import { LoginComponent } from './login.component';
import { UserManagementComponent } from './user-management.component';

export const routes: Routes = [
  { path: 'users', component: UserManagementComponent },
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: 'users', pathMatch: 'full' }
];
