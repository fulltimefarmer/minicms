import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface User {
  id: number;
  name: string;
  email: string;
  phone?: string;
  role: string;
  status: 'active' | 'inactive';
  createdAt: Date;
}

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="user-management">
      <div class="header">
        <h1>用户管理</h1>
        <button class="btn btn-primary" (click)="openAddUserModal()">
          <i class="icon">+</i> 添加用户
        </button>
      </div>

      <!-- 搜索和筛选 -->
      <div class="search-bar">
        <input 
          type="text" 
          placeholder="搜索用户名或邮箱..." 
          [(ngModel)]="searchTerm"
          (input)="filterUsers()"
          class="search-input"
        />
        <select [(ngModel)]="filterRole" (change)="filterUsers()" class="filter-select">
          <option value="">所有角色</option>
          <option value="admin">管理员</option>
          <option value="user">普通用户</option>
          <option value="guest">访客</option>
        </select>
        <select [(ngModel)]="filterStatus" (change)="filterUsers()" class="filter-select">
          <option value="">所有状态</option>
          <option value="active">激活</option>
          <option value="inactive">停用</option>
        </select>
      </div>

      <!-- 用户表格 -->
      <div class="table-container">
        <table class="user-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>姓名</th>
              <th>邮箱</th>
              <th>电话</th>
              <th>角色</th>
              <th>状态</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let user of filteredUsers" [class.inactive]="user.status === 'inactive'">
              <td>{{ user.id }}</td>
              <td>{{ user.name }}</td>
              <td>{{ user.email }}</td>
              <td>{{ user.phone || '-' }}</td>
              <td>
                <span class="role-badge" [class]="'role-' + user.role">
                  {{ getRoleDisplayName(user.role) }}
                </span>
              </td>
              <td>
                <span class="status-badge" [class]="'status-' + user.status">
                  {{ user.status === 'active' ? '激活' : '停用' }}
                </span>
              </td>
              <td>{{ formatDate(user.createdAt) }}</td>
              <td class="actions">
                <button class="btn btn-sm btn-secondary" (click)="editUser(user)">编辑</button>
                <button 
                  class="btn btn-sm" 
                  [class]="user.status === 'active' ? 'btn-warning' : 'btn-success'"
                  (click)="toggleUserStatus(user)"
                >
                  {{ user.status === 'active' ? '停用' : '激活' }}
                </button>
                <button class="btn btn-sm btn-danger" (click)="deleteUser(user)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 用户模态框 -->
      <div class="modal" [class.show]="showModal" (click)="closeModal($event)">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ isEditing ? '编辑用户' : '添加用户' }}</h3>
            <button class="close-btn" (click)="closeModal()">&times;</button>
          </div>
          <form #userForm="ngForm" (ngSubmit)="saveUser()">
            <div class="form-group">
              <label for="name">姓名 *</label>
              <input 
                id="name" 
                name="name" 
                [(ngModel)]="currentUser.name" 
                required 
                class="form-control"
                #nameInput="ngModel"
              />
              <div *ngIf="nameInput.invalid && nameInput.touched" class="error">姓名是必填项</div>
            </div>
            
            <div class="form-group">
              <label for="email">邮箱 *</label>
              <input 
                id="email" 
                name="email" 
                type="email"
                [(ngModel)]="currentUser.email" 
                required 
                email
                class="form-control"
                #emailInput="ngModel"
              />
              <div *ngIf="emailInput.invalid && emailInput.touched" class="error">
                <span *ngIf="emailInput.errors?.['required']">邮箱是必填项</span>
                <span *ngIf="emailInput.errors?.['email']">请输入有效的邮箱地址</span>
              </div>
            </div>
            
            <div class="form-group">
              <label for="phone">电话</label>
              <input 
                id="phone" 
                name="phone" 
                [(ngModel)]="currentUser.phone" 
                class="form-control"
              />
            </div>
            
            <div class="form-group">
              <label for="role">角色 *</label>
              <select 
                id="role" 
                name="role" 
                [(ngModel)]="currentUser.role" 
                required 
                class="form-control"
              >
                <option value="">请选择角色</option>
                <option value="admin">管理员</option>
                <option value="user">普通用户</option>
                <option value="guest">访客</option>
              </select>
            </div>
            
            <div class="form-group">
              <label for="status">状态</label>
              <select 
                id="status" 
                name="status" 
                [(ngModel)]="currentUser.status" 
                class="form-control"
              >
                <option value="active">激活</option>
                <option value="inactive">停用</option>
              </select>
            </div>
            
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="closeModal()">取消</button>
              <button type="submit" class="btn btn-primary" [disabled]="!userForm.form.valid">
                {{ isEditing ? '更新' : '保存' }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- 统计信息 -->
      <div class="stats">
        <div class="stat-item">
          <h4>{{ getTotalUsers() }}</h4>
          <p>总用户数</p>
        </div>
        <div class="stat-item">
          <h4>{{ getActiveUsers() }}</h4>
          <p>激活用户</p>
        </div>
        <div class="stat-item">
          <h4>{{ getInactiveUsers() }}</h4>
          <p>停用用户</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .user-management {
      padding: 2rem;
      max-width: 1200px;
      margin: 0 auto;
    }

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }

    .header h1 {
      margin: 0;
      color: #333;
    }

    .search-bar {
      display: flex;
      gap: 1rem;
      margin-bottom: 2rem;
      flex-wrap: wrap;
    }

    .search-input {
      flex: 1;
      min-width: 200px;
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
    }

    .filter-select {
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      min-width: 120px;
    }

    .table-container {
      overflow-x: auto;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
      margin-bottom: 2rem;
    }

    .user-table {
      width: 100%;
      border-collapse: collapse;
    }

    .user-table th,
    .user-table td {
      padding: 1rem;
      text-align: left;
      border-bottom: 1px solid #eee;
    }

    .user-table th {
      background: #f8f9fa;
      font-weight: 600;
      color: #333;
    }

    .user-table tr:hover {
      background: #f8f9fa;
    }

    .user-table tr.inactive {
      opacity: 0.6;
    }

    .role-badge,
    .status-badge {
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      font-size: 0.875rem;
      font-weight: 500;
    }

    .role-admin { background: #e3f2fd; color: #1976d2; }
    .role-user { background: #f3e5f5; color: #7b1fa2; }
    .role-guest { background: #fff3e0; color: #f57c00; }

    .status-active { background: #e8f5e8; color: #2e7d32; }
    .status-inactive { background: #ffebee; color: #c62828; }

    .actions {
      display: flex;
      gap: 0.5rem;
    }

    .btn {
      padding: 0.5rem 1rem;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 0.875rem;
      font-weight: 500;
      text-decoration: none;
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      transition: all 0.2s;
    }

    .btn:hover {
      opacity: 0.9;
      transform: translateY(-1px);
    }

    .btn-primary { background: #1976d2; color: white; }
    .btn-secondary { background: #6c757d; color: white; }
    .btn-success { background: #2e7d32; color: white; }
    .btn-warning { background: #f57c00; color: white; }
    .btn-danger { background: #d32f2f; color: white; }

    .btn-sm {
      padding: 0.25rem 0.5rem;
      font-size: 0.75rem;
    }

    .icon {
      font-size: 1rem;
    }

    .modal {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0,0,0,0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
      opacity: 0;
      visibility: hidden;
      transition: all 0.3s;
    }

    .modal.show {
      opacity: 1;
      visibility: visible;
    }

    .modal-content {
      background: white;
      border-radius: 8px;
      width: 90%;
      max-width: 500px;
      max-height: 90vh;
      overflow-y: auto;
      transform: scale(0.7);
      transition: transform 0.3s;
    }

    .modal.show .modal-content {
      transform: scale(1);
    }

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1.5rem;
      border-bottom: 1px solid #eee;
    }

    .modal-header h3 {
      margin: 0;
    }

    .close-btn {
      background: none;
      border: none;
      font-size: 1.5rem;
      cursor: pointer;
      color: #666;
    }

    .form-group {
      margin: 1rem 1.5rem;
    }

    .form-group label {
      display: block;
      margin-bottom: 0.5rem;
      font-weight: 500;
      color: #333;
    }

    .form-control {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      box-sizing: border-box;
    }

    .form-control:focus {
      outline: none;
      border-color: #1976d2;
      box-shadow: 0 0 0 2px rgba(25,118,210,0.2);
    }

    .error {
      color: #d32f2f;
      font-size: 0.875rem;
      margin-top: 0.25rem;
    }

    .modal-footer {
      display: flex;
      justify-content: flex-end;
      gap: 1rem;
      padding: 1.5rem;
      border-top: 1px solid #eee;
    }

    .stats {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1rem;
    }

    .stat-item {
      background: white;
      padding: 1.5rem;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
      text-align: center;
    }

    .stat-item h4 {
      font-size: 2rem;
      margin: 0;
      color: #1976d2;
    }

    .stat-item p {
      margin: 0.5rem 0 0;
      color: #666;
    }

    @media (max-width: 768px) {
      .user-management {
        padding: 1rem;
      }

      .header {
        flex-direction: column;
        gap: 1rem;
        align-items: stretch;
      }

      .search-bar {
        flex-direction: column;
      }

      .actions {
        flex-direction: column;
      }

      .stats {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class UserManagementComponent {
  users: User[] = [
    {
      id: 1,
      name: '张三',
      email: 'zhangsan@example.com',
      phone: '13800138001',
      role: 'admin',
      status: 'active',
      createdAt: new Date('2023-01-15')
    },
    {
      id: 2,
      name: '李四',
      email: 'lisi@example.com',
      phone: '13800138002',
      role: 'user',
      status: 'active',
      createdAt: new Date('2023-02-20')
    },
    {
      id: 3,
      name: '王五',
      email: 'wangwu@example.com',
      role: 'user',
      status: 'inactive',
      createdAt: new Date('2023-03-10')
    },
    {
      id: 4,
      name: '赵六',
      email: 'zhaoliu@example.com',
      phone: '13800138004',
      role: 'guest',
      status: 'active',
      createdAt: new Date('2023-04-05')
    }
  ];

  filteredUsers: User[] = [];
  searchTerm = '';
  filterRole = '';
  filterStatus = '';
  
  showModal = false;
  isEditing = false;
  currentUser: Partial<User> = {};

  ngOnInit() {
    this.filteredUsers = [...this.users];
  }

  filterUsers() {
    this.filteredUsers = this.users.filter(user => {
      const matchesSearch = !this.searchTerm || 
        user.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.email.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesRole = !this.filterRole || user.role === this.filterRole;
      const matchesStatus = !this.filterStatus || user.status === this.filterStatus;
      
      return matchesSearch && matchesRole && matchesStatus;
    });
  }

  openAddUserModal() {
    this.isEditing = false;
    this.currentUser = {
      role: 'user',
      status: 'active'
    };
    this.showModal = true;
  }

  editUser(user: User) {
    this.isEditing = true;
    this.currentUser = { ...user };
    this.showModal = true;
  }

  closeModal(event?: Event) {
    this.showModal = false;
    this.currentUser = {};
  }

  saveUser() {
    if (this.isEditing) {
      const index = this.users.findIndex(u => u.id === this.currentUser.id);
      if (index !== -1) {
        this.users[index] = { ...this.currentUser } as User;
      }
    } else {
      const newUser: User = {
        id: Math.max(...this.users.map(u => u.id)) + 1,
        name: this.currentUser.name!,
        email: this.currentUser.email!,
        phone: this.currentUser.phone,
        role: this.currentUser.role!,
        status: this.currentUser.status!,
        createdAt: new Date()
      };
      this.users.push(newUser);
    }
    
    this.filterUsers();
    this.closeModal();
  }

  toggleUserStatus(user: User) {
    user.status = user.status === 'active' ? 'inactive' : 'active';
    this.filterUsers();
  }

  deleteUser(user: User) {
    if (confirm(`确定要删除用户 "${user.name}" 吗？`)) {
      this.users = this.users.filter(u => u.id !== user.id);
      this.filterUsers();
    }
  }

  getRoleDisplayName(role: string): string {
    const roleMap: { [key: string]: string } = {
      'admin': '管理员',
      'user': '普通用户',
      'guest': '访客'
    };
    return roleMap[role] || role;
  }

  formatDate(date: Date): string {
    return date.toLocaleDateString('zh-CN');
  }

  getTotalUsers(): number {
    return this.users.length;
  }

  getActiveUsers(): number {
    return this.users.filter(u => u.status === 'active').length;
  }

  getInactiveUsers(): number {
    return this.users.filter(u => u.status === 'inactive').length;
  }
}