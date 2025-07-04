import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PermissionService, User, Role, Permission } from './permission.service';

@Component({
  selector: 'app-permission-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  template: `
    <div class="permission-container">
      <h1>权限管理系统</h1>
      
      <!-- 顶部导航 -->
      <div class="tabs">
        <button 
          *ngFor="let tab of tabs" 
          [class.active]="activeTab === tab.key"
          (click)="activeTab = tab.key">
          {{tab.label}}
        </button>
      </div>

      <!-- 用户管理 -->
      <div *ngIf="activeTab === 'users'" class="tab-content">
        <div class="section-header">
          <h2>用户管理</h2>
          <button class="btn-primary" (click)="showUserForm = true">添加用户</button>
        </div>

        <!-- 用户列表 -->
        <div class="table-container">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>用户名</th>
                <th>邮箱</th>
                <th>角色</th>
                <th>状态</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let user of users">
                <td>{{user.id}}</td>
                <td>{{user.username}}</td>
                <td>{{user.email}}</td>
                <td>
                  <span *ngFor="let roleId of user.roleIds" class="role-tag">
                    {{getRoleName(roleId)}}
                  </span>
                </td>
                <td>
                  <span [class]="'status-' + user.status">{{user.status === 'active' ? '活跃' : '禁用'}}</span>
                </td>
                <td>{{user.createdAt | date:'yyyy-MM-dd'}}</td>
                <td>
                  <button class="btn-edit" (click)="editUser(user)">编辑</button>
                  <button class="btn-delete" (click)="deleteUser(user.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 用户表单 -->
        <div *ngIf="showUserForm" class="modal-overlay" (click)="closeUserForm()">
          <div class="modal-content" (click)="$event.stopPropagation()">
            <h3>{{editingUser ? '编辑用户' : '添加用户'}}</h3>
            <form [formGroup]="userForm" (ngSubmit)="saveUser()">
              <div class="form-group">
                <label for="username">用户名</label>
                <input id="username" formControlName="username" type="text">
                <div *ngIf="userForm.get('username')?.errors?.['required'] && userForm.get('username')?.touched" class="error">
                  用户名是必填项
                </div>
              </div>
              <div class="form-group">
                <label for="email">邮箱</label>
                <input id="email" formControlName="email" type="email">
                <div *ngIf="userForm.get('email')?.errors?.['required'] && userForm.get('email')?.touched" class="error">
                  邮箱是必填项
                </div>
              </div>
              <div class="form-group">
                <label for="password">密码</label>
                <input id="password" formControlName="password" type="password">
                <div *ngIf="userForm.get('password')?.errors?.['required'] && userForm.get('password')?.touched" class="error">
                  密码是必填项
                </div>
              </div>
              <div class="form-group">
                <label>角色</label>
                <div class="checkbox-group">
                  <label *ngFor="let role of roles" class="checkbox-label">
                    <input 
                      type="checkbox" 
                      [checked]="isRoleSelected(role.id)"
                      (change)="toggleRole(role.id, $event)">
                    {{role.name}}
                  </label>
                </div>
              </div>
              <div class="form-group">
                <label for="status">状态</label>
                <select id="status" formControlName="status">
                  <option value="active">活跃</option>
                  <option value="inactive">禁用</option>
                </select>
              </div>
              <div class="form-actions">
                <button type="button" class="btn-cancel" (click)="closeUserForm()">取消</button>
                <button type="submit" class="btn-primary" [disabled]="!userForm.valid">保存</button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- 角色管理 -->
      <div *ngIf="activeTab === 'roles'" class="tab-content">
        <div class="section-header">
          <h2>角色管理</h2>
          <button class="btn-primary" (click)="showRoleForm = true">添加角色</button>
        </div>

        <!-- 角色列表 -->
        <div class="table-container">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>角色名称</th>
                <th>描述</th>
                <th>权限数量</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let role of roles">
                <td>{{role.id}}</td>
                <td>{{role.name}}</td>
                <td>{{role.description}}</td>
                <td>{{role.permissionIds.length}}</td>
                <td>{{role.createdAt | date:'yyyy-MM-dd'}}</td>
                <td>
                  <button class="btn-edit" (click)="editRole(role)">编辑</button>
                  <button class="btn-delete" (click)="deleteRole(role.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 角色表单 -->
        <div *ngIf="showRoleForm" class="modal-overlay" (click)="closeRoleForm()">
          <div class="modal-content" (click)="$event.stopPropagation()">
            <h3>{{editingRole ? '编辑角色' : '添加角色'}}</h3>
            <form [formGroup]="roleForm" (ngSubmit)="saveRole()">
              <div class="form-group">
                <label for="roleName">角色名称</label>
                <input id="roleName" formControlName="name" type="text">
                <div *ngIf="roleForm.get('name')?.errors?.['required'] && roleForm.get('name')?.touched" class="error">
                  角色名称是必填项
                </div>
              </div>
              <div class="form-group">
                <label for="roleDescription">描述</label>
                <textarea id="roleDescription" formControlName="description"></textarea>
              </div>
              <div class="form-group">
                <label>权限</label>
                <div class="permission-tree">
                  <div *ngFor="let module of permissionModules" class="permission-module">
                    <h4>{{module.name}}</h4>
                    <div class="permission-list">
                      <label *ngFor="let permission of module.permissions" class="checkbox-label">
                        <input 
                          type="checkbox" 
                          [checked]="isPermissionSelected(permission.id)"
                          (change)="togglePermission(permission.id, $event)">
                        {{permission.name}} - {{permission.description}}
                      </label>
                    </div>
                  </div>
                </div>
              </div>
              <div class="form-actions">
                <button type="button" class="btn-cancel" (click)="closeRoleForm()">取消</button>
                <button type="submit" class="btn-primary" [disabled]="!roleForm.valid">保存</button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- 权限管理 -->
      <div *ngIf="activeTab === 'permissions'" class="tab-content">
        <div class="section-header">
          <h2>权限管理</h2>
          <button class="btn-primary" (click)="showPermissionForm = true">添加权限</button>
        </div>

        <!-- 权限列表 -->
        <div class="permission-modules">
          <div *ngFor="let module of permissionModules" class="permission-module-card">
            <h3>{{module.name}}</h3>
            <div class="permission-list">
              <div *ngFor="let permission of module.permissions" class="permission-item">
                <div class="permission-info">
                  <strong>{{permission.name}}</strong>
                  <span>{{permission.description}}</span>
                  <code>{{permission.code}}</code>
                </div>
                <div class="permission-actions">
                  <button class="btn-edit" (click)="editPermission(permission)">编辑</button>
                  <button class="btn-delete" (click)="deletePermission(permission.id)">删除</button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 权限表单 -->
        <div *ngIf="showPermissionForm" class="modal-overlay" (click)="closePermissionForm()">
          <div class="modal-content" (click)="$event.stopPropagation()">
            <h3>{{editingPermission ? '编辑权限' : '添加权限'}}</h3>
            <form [formGroup]="permissionForm" (ngSubmit)="savePermission()">
              <div class="form-group">
                <label for="permissionName">权限名称</label>
                <input id="permissionName" formControlName="name" type="text">
              </div>
              <div class="form-group">
                <label for="permissionCode">权限代码</label>
                <input id="permissionCode" formControlName="code" type="text">
              </div>
              <div class="form-group">
                <label for="permissionDescription">描述</label>
                <textarea id="permissionDescription" formControlName="description"></textarea>
              </div>
              <div class="form-group">
                <label for="permissionModule">模块</label>
                <select id="permissionModule" formControlName="module">
                  <option *ngFor="let module of permissionModules" [value]="module.name">{{module.name}}</option>
                </select>
              </div>
              <div class="form-actions">
                <button type="button" class="btn-cancel" (click)="closePermissionForm()">取消</button>
                <button type="submit" class="btn-primary" [disabled]="!permissionForm.valid">保存</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .permission-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 2rem;
    }

    .tabs {
      display: flex;
      border-bottom: 2px solid #e0e0e0;
      margin-bottom: 2rem;
    }

    .tabs button {
      padding: 1rem 2rem;
      background: none;
      border: none;
      cursor: pointer;
      font-size: 1rem;
      border-bottom: 3px solid transparent;
      transition: all 0.3s;
    }

    .tabs button.active {
      color: #1976d2;
      border-bottom-color: #1976d2;
    }

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.5rem;
    }

    .table-container {
      overflow-x: auto;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }

    table {
      width: 100%;
      border-collapse: collapse;
    }

    th, td {
      padding: 1rem;
      text-align: left;
      border-bottom: 1px solid #e0e0e0;
    }

    th {
      background-color: #f5f5f5;
      font-weight: 600;
    }

    .role-tag {
      display: inline-block;
      background: #e3f2fd;
      color: #1976d2;
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      margin-right: 0.5rem;
      font-size: 0.875rem;
    }

    .status-active {
      color: #4caf50;
    }

    .status-inactive {
      color: #f44336;
    }

    .btn-primary {
      background: #1976d2;
      color: white;
      border: none;
      padding: 0.75rem 1.5rem;
      border-radius: 4px;
      cursor: pointer;
      font-size: 1rem;
    }

    .btn-edit {
      background: #ff9800;
      color: white;
      border: none;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      cursor: pointer;
      margin-right: 0.5rem;
    }

    .btn-delete {
      background: #f44336;
      color: white;
      border: none;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      cursor: pointer;
    }

    .btn-cancel {
      background: #757575;
      color: white;
      border: none;
      padding: 0.75rem 1.5rem;
      border-radius: 4px;
      cursor: pointer;
      margin-right: 1rem;
    }

    .modal-overlay {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0,0,0,0.5);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 1000;
    }

    .modal-content {
      background: white;
      padding: 2rem;
      border-radius: 8px;
      max-width: 600px;
      width: 90%;
      max-height: 80vh;
      overflow-y: auto;
    }

    .form-group {
      margin-bottom: 1.5rem;
    }

    .form-group label {
      display: block;
      margin-bottom: 0.5rem;
      font-weight: 600;
    }

    .form-group input,
    .form-group select,
    .form-group textarea {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 1rem;
    }

    .form-group textarea {
      height: 100px;
      resize: vertical;
    }

    .checkbox-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .checkbox-label {
      display: flex;
      align-items: center;
      font-weight: normal;
    }

    .checkbox-label input {
      width: auto;
      margin-right: 0.5rem;
    }

    .form-actions {
      display: flex;
      justify-content: flex-end;
      margin-top: 2rem;
    }

    .error {
      color: #f44336;
      font-size: 0.875rem;
      margin-top: 0.25rem;
    }

    .permission-tree {
      max-height: 300px;
      overflow-y: auto;
    }

    .permission-module {
      margin-bottom: 1.5rem;
    }

    .permission-module h4 {
      margin-bottom: 0.5rem;
      color: #1976d2;
    }

    .permission-list {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
      padding-left: 1rem;
    }

    .permission-modules {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
      gap: 1.5rem;
    }

    .permission-module-card {
      background: white;
      border-radius: 8px;
      padding: 1.5rem;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }

    .permission-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem;
      border: 1px solid #e0e0e0;
      border-radius: 4px;
      margin-bottom: 0.5rem;
    }

    .permission-info {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .permission-info code {
      background: #f5f5f5;
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      font-size: 0.875rem;
    }

    .permission-actions {
      display: flex;
      gap: 0.5rem;
    }

    @media (max-width: 768px) {
      .permission-container {
        padding: 1rem;
      }

      .tabs {
        flex-wrap: wrap;
      }

      .section-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 1rem;
      }

      .modal-content {
        width: 95%;
        padding: 1rem;
      }

      .permission-modules {
        grid-template-columns: 1fr;
      }

      .permission-item {
        flex-direction: column;
        align-items: flex-start;
        gap: 1rem;
      }
    }
  `]
})
export class PermissionManagementComponent implements OnInit {
  activeTab = 'users';
  tabs = [
    { key: 'users', label: '用户管理' },
    { key: 'roles', label: '角色管理' },
    { key: 'permissions', label: '权限管理' }
  ];

  // 数据
  users: User[] = [];
  roles: Role[] = [];
  permissions: Permission[] = [];
  permissionModules: any[] = [];

  // 表单相关
  showUserForm = false;
  showRoleForm = false;
  showPermissionForm = false;
  editingUser: User | null = null;
  editingRole: Role | null = null;
  editingPermission: Permission | null = null;

  userForm: FormGroup;
  roleForm: FormGroup;
  permissionForm: FormGroup;

  selectedRoleIds: number[] = [];
  selectedPermissionIds: number[] = [];

  constructor(
    private permissionService: PermissionService,
    private fb: FormBuilder
  ) {
    this.userForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      status: ['active']
    });

    this.roleForm = this.fb.group({
      name: ['', Validators.required],
      description: ['']
    });

    this.permissionForm = this.fb.group({
      name: ['', Validators.required],
      code: ['', Validators.required],
      description: [''],
      module: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.users = this.permissionService.getUsers();
    this.roles = this.permissionService.getRoles();
    this.permissions = this.permissionService.getPermissions();
    this.permissionModules = this.permissionService.getPermissionModules();
  }

  // 用户管理方法
  editUser(user: User) {
    this.editingUser = user;
    this.selectedRoleIds = [...user.roleIds];
    this.userForm.patchValue({
      username: user.username,
      email: user.email,
      password: '',
      status: user.status
    });
    this.showUserForm = true;
  }

  saveUser() {
    if (this.userForm.valid) {
      const userData = {
        ...this.userForm.value,
        roleIds: this.selectedRoleIds
      };

      if (this.editingUser) {
        this.permissionService.updateUser(this.editingUser.id, userData);
      } else {
        this.permissionService.createUser(userData);
      }

      this.closeUserForm();
      this.loadData();
    }
  }

  deleteUser(id: number) {
    if (confirm('确定要删除这个用户吗？')) {
      this.permissionService.deleteUser(id);
      this.loadData();
    }
  }

  closeUserForm() {
    this.showUserForm = false;
    this.editingUser = null;
    this.selectedRoleIds = [];
    this.userForm.reset();
  }

  // 角色管理方法
  editRole(role: Role) {
    this.editingRole = role;
    this.selectedPermissionIds = [...role.permissionIds];
    this.roleForm.patchValue({
      name: role.name,
      description: role.description
    });
    this.showRoleForm = true;
  }

  saveRole() {
    if (this.roleForm.valid) {
      const roleData = {
        ...this.roleForm.value,
        permissionIds: this.selectedPermissionIds
      };

      if (this.editingRole) {
        this.permissionService.updateRole(this.editingRole.id, roleData);
      } else {
        this.permissionService.createRole(roleData);
      }

      this.closeRoleForm();
      this.loadData();
    }
  }

  deleteRole(id: number) {
    if (confirm('确定要删除这个角色吗？')) {
      this.permissionService.deleteRole(id);
      this.loadData();
    }
  }

  closeRoleForm() {
    this.showRoleForm = false;
    this.editingRole = null;
    this.selectedPermissionIds = [];
    this.roleForm.reset();
  }

  // 权限管理方法
  editPermission(permission: Permission) {
    this.editingPermission = permission;
    this.permissionForm.patchValue({
      name: permission.name,
      code: permission.code,
      description: permission.description,
      module: permission.module
    });
    this.showPermissionForm = true;
  }

  savePermission() {
    if (this.permissionForm.valid) {
      if (this.editingPermission) {
        this.permissionService.updatePermission(this.editingPermission.id, this.permissionForm.value);
      } else {
        this.permissionService.createPermission(this.permissionForm.value);
      }

      this.closePermissionForm();
      this.loadData();
    }
  }

  deletePermission(id: number) {
    if (confirm('确定要删除这个权限吗？')) {
      this.permissionService.deletePermission(id);
      this.loadData();
    }
  }

  closePermissionForm() {
    this.showPermissionForm = false;
    this.editingPermission = null;
    this.permissionForm.reset();
  }

  // 辅助方法
  getRoleName(roleId: number): string {
    const role = this.roles.find(r => r.id === roleId);
    return role ? role.name : '未知角色';
  }

  isRoleSelected(roleId: number): boolean {
    return this.selectedRoleIds.includes(roleId);
  }

  toggleRole(roleId: number, event: any) {
    if (event.target.checked) {
      this.selectedRoleIds.push(roleId);
    } else {
      this.selectedRoleIds = this.selectedRoleIds.filter(id => id !== roleId);
    }
  }

  isPermissionSelected(permissionId: number): boolean {
    return this.selectedPermissionIds.includes(permissionId);
  }

  togglePermission(permissionId: number, event: any) {
    if (event.target.checked) {
      this.selectedPermissionIds.push(permissionId);
    } else {
      this.selectedPermissionIds = this.selectedPermissionIds.filter(id => id !== permissionId);
    }
  }
}