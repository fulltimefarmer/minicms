import { Injectable } from '@angular/core';

export interface User {
  id: number;
  username: string;
  email: string;
  password: string;
  roleIds: number[];
  status: 'active' | 'inactive';
  createdAt: Date;
  updatedAt: Date;
}

export interface Role {
  id: number;
  name: string;
  description: string;
  permissionIds: number[];
  createdAt: Date;
  updatedAt: Date;
}

export interface Permission {
  id: number;
  name: string;
  code: string;
  description: string;
  module: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface PermissionModule {
  name: string;
  permissions: Permission[];
}

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  private users: User[] = [
    {
      id: 1,
      username: 'admin',
      email: 'admin@example.com',
      password: '1234',
      roleIds: [1],
      status: 'active',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: 2,
      username: 'editor',
      email: 'editor@example.com',
      password: '1234',
      roleIds: [2],
      status: 'active',
      createdAt: new Date('2024-01-02'),
      updatedAt: new Date('2024-01-02')
    },
    {
      id: 3,
      username: 'viewer',
      email: 'viewer@example.com',
      password: '1234',
      roleIds: [3],
      status: 'inactive',
      createdAt: new Date('2024-01-03'),
      updatedAt: new Date('2024-01-03')
    }
  ];

  private roles: Role[] = [
    {
      id: 1,
      name: '超级管理员',
      description: '拥有系统所有权限',
      permissionIds: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: 2,
      name: '编辑者',
      description: '可以编辑内容，但不能管理用户',
      permissionIds: [4, 5, 6, 7, 8],
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: 3,
      name: '查看者',
      description: '只能查看内容',
      permissionIds: [4, 7],
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    }
  ];

  private permissions: Permission[] = [
    // 用户管理模块
    {
      id: 1,
      name: '查看用户',
      code: 'user:view',
      description: '查看用户列表和详情',
      module: '用户管理',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: 2,
      name: '创建用户',
      code: 'user:create',
      description: '创建新用户',
      module: '用户管理',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: 3,
      name: '编辑用户',
      code: 'user:edit',
      description: '编辑用户信息',
      module: '用户管理',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    // 内容管理模块
    {
      id: 4,
      name: '查看文章',
      code: 'article:view',
      description: '查看文章列表和详情',
      module: '内容管理',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: 5,
      name: '创建文章',
      code: 'article:create',
      description: '创建新文章',
      module: '内容管理',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: 6,
      name: '编辑文章',
      code: 'article:edit',
      description: '编辑文章内容',
      module: '内容管理',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: 7,
      name: '发布文章',
      code: 'article:publish',
      description: '发布或取消发布文章',
      module: '内容管理',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: 8,
      name: '删除文章',
      code: 'article:delete',
      description: '删除文章',
      module: '内容管理',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    // 系统设置模块
    {
      id: 9,
      name: '查看系统设置',
      code: 'system:view',
      description: '查看系统配置',
      module: '系统设置',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: 10,
      name: '修改系统设置',
      code: 'system:edit',
      description: '修改系统配置',
      module: '系统设置',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    // 权限管理模块
    {
      id: 11,
      name: '权限管理',
      code: 'permission:manage',
      description: '管理角色和权限',
      module: '权限管理',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: 12,
      name: '日志查看',
      code: 'log:view',
      description: '查看系统日志',
      module: '系统设置',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    }
  ];

  private nextUserId = 4;
  private nextRoleId = 4;
  private nextPermissionId = 13;

  constructor() {}

  // 用户管理方法
  getUsers(): User[] {
    return [...this.users];
  }

  getUserById(id: number): User | undefined {
    return this.users.find(user => user.id === id);
  }

  createUser(userData: Partial<User>): User {
    const newUser: User = {
      id: this.nextUserId++,
      username: userData.username || '',
      email: userData.email || '',
      password: userData.password || '',
      roleIds: userData.roleIds || [],
      status: userData.status || 'active',
      createdAt: new Date(),
      updatedAt: new Date()
    };
    this.users.push(newUser);
    return newUser;
  }

  updateUser(id: number, userData: Partial<User>): User | null {
    const userIndex = this.users.findIndex(user => user.id === id);
    if (userIndex === -1) return null;

    this.users[userIndex] = {
      ...this.users[userIndex],
      ...userData,
      id,
      updatedAt: new Date()
    };
    return this.users[userIndex];
  }

  deleteUser(id: number): boolean {
    const userIndex = this.users.findIndex(user => user.id === id);
    if (userIndex === -1) return false;

    this.users.splice(userIndex, 1);
    return true;
  }

  // 角色管理方法
  getRoles(): Role[] {
    return [...this.roles];
  }

  getRoleById(id: number): Role | undefined {
    return this.roles.find(role => role.id === id);
  }

  createRole(roleData: Partial<Role>): Role {
    const newRole: Role = {
      id: this.nextRoleId++,
      name: roleData.name || '',
      description: roleData.description || '',
      permissionIds: roleData.permissionIds || [],
      createdAt: new Date(),
      updatedAt: new Date()
    };
    this.roles.push(newRole);
    return newRole;
  }

  updateRole(id: number, roleData: Partial<Role>): Role | null {
    const roleIndex = this.roles.findIndex(role => role.id === id);
    if (roleIndex === -1) return null;

    this.roles[roleIndex] = {
      ...this.roles[roleIndex],
      ...roleData,
      id,
      updatedAt: new Date()
    };
    return this.roles[roleIndex];
  }

  deleteRole(id: number): boolean {
    const roleIndex = this.roles.findIndex(role => role.id === id);
    if (roleIndex === -1) return false;

    // 检查是否有用户使用这个角色
    const hasUsers = this.users.some(user => user.roleIds.includes(id));
    if (hasUsers) {
      throw new Error('无法删除正在使用的角色');
    }

    this.roles.splice(roleIndex, 1);
    return true;
  }

  // 权限管理方法
  getPermissions(): Permission[] {
    return [...this.permissions];
  }

  getPermissionById(id: number): Permission | undefined {
    return this.permissions.find(permission => permission.id === id);
  }

  createPermission(permissionData: Partial<Permission>): Permission {
    const newPermission: Permission = {
      id: this.nextPermissionId++,
      name: permissionData.name || '',
      code: permissionData.code || '',
      description: permissionData.description || '',
      module: permissionData.module || '',
      createdAt: new Date(),
      updatedAt: new Date()
    };
    this.permissions.push(newPermission);
    return newPermission;
  }

  updatePermission(id: number, permissionData: Partial<Permission>): Permission | null {
    const permissionIndex = this.permissions.findIndex(permission => permission.id === id);
    if (permissionIndex === -1) return null;

    this.permissions[permissionIndex] = {
      ...this.permissions[permissionIndex],
      ...permissionData,
      id,
      updatedAt: new Date()
    };
    return this.permissions[permissionIndex];
  }

  deletePermission(id: number): boolean {
    const permissionIndex = this.permissions.findIndex(permission => permission.id === id);
    if (permissionIndex === -1) return false;

    // 检查是否有角色使用这个权限
    const hasRoles = this.roles.some(role => role.permissionIds.includes(id));
    if (hasRoles) {
      throw new Error('无法删除正在使用的权限');
    }

    this.permissions.splice(permissionIndex, 1);
    return true;
  }

  // 获取按模块分组的权限
  getPermissionModules(): PermissionModule[] {
    const modules = new Map<string, Permission[]>();
    
    this.permissions.forEach(permission => {
      if (!modules.has(permission.module)) {
        modules.set(permission.module, []);
      }
      modules.get(permission.module)!.push(permission);
    });

    return Array.from(modules.entries()).map(([name, permissions]) => ({
      name,
      permissions: permissions.sort((a, b) => a.id - b.id)
    }));
  }

  // 权限检查方法
  hasPermission(userId: number, permissionCode: string): boolean {
    const user = this.getUserById(userId);
    if (!user) return false;

    const userPermissions = this.getUserPermissions(userId);
    return userPermissions.some(permission => permission.code === permissionCode);
  }

  getUserPermissions(userId: number): Permission[] {
    const user = this.getUserById(userId);
    if (!user) return [];

    const permissionIds = new Set<number>();
    
    user.roleIds.forEach(roleId => {
      const role = this.getRoleById(roleId);
      if (role) {
        role.permissionIds.forEach(permissionId => {
          permissionIds.add(permissionId);
        });
      }
    });

    return Array.from(permissionIds).map(id => this.getPermissionById(id)!).filter(Boolean);
  }

  getUserRoles(userId: number): Role[] {
    const user = this.getUserById(userId);
    if (!user) return [];

    return user.roleIds.map(roleId => this.getRoleById(roleId)!).filter(Boolean);
  }

  // 验证用户登录
  validateLogin(username: string, password: string): User | null {
    const user = this.users.find(u => 
      u.username === username && 
      u.password === password && 
      u.status === 'active'
    );
    return user || null;
  }
}