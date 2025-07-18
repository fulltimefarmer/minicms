const fs = require('fs');
const path = require('path');

// 生成随机数据的辅助函数
function randomId() {
  return Math.floor(Math.random() * 1000) + 1;
}

function randomDate() {
  const start = new Date(2024, 0, 1);
  const end = new Date();
  return new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime())).toISOString();
}

function randomFutureDate() {
  const start = new Date();
  const end = new Date(2025, 11, 31);
  return new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime())).toISOString();
}

function randomChoice(array) {
  return array[Math.floor(Math.random() * array.length)];
}

// 生成Todo数据
function generateTodos() {
  const priorities = ['LOW', 'MEDIUM', 'HIGH'];
  const titles = [
    '完成项目需求分析',
    '设计系统架构',
    '编写用户手册',
    '进行系统测试',
    '优化数据库性能',
    '实施安全审计',
    '更新API文档',
    '重构前端组件',
    '集成第三方服务',
    '部署生产环境',
    '修复用户反馈bug',
    '实现新功能模块',
    '进行代码评审',
    '优化用户界面',
    '配置监控系统',
    '制定备份策略',
    '培训新员工',
    '更新依赖包',
    '编写单元测试',
    '性能优化分析'
  ];

  const descriptions = [
    '与客户确认项目需求，整理需求文档',
    '设计可扩展的系统架构方案',
    '编写详细的用户操作指南',
    '执行完整的系统功能测试',
    '分析并优化数据库查询性能',
    '检查系统安全漏洞和风险',
    '更新API接口说明文档',
    '重构老旧的前端组件代码',
    '集成支付、短信等第三方服务',
    '配置并部署到生产服务器',
    '根据用户反馈修复相关问题',
    '开发新的业务功能模块',
    '审查团队成员提交的代码',
    '改进用户体验和界面设计',
    '设置系统监控和告警机制',
    '制定数据备份和恢复策略',
    '为新加入的团队成员提供培训',
    '升级项目依赖到最新版本',
    '为核心功能编写测试用例',
    '分析系统性能瓶颈并优化'
  ];

  return Array.from({ length: 25 }, (_, i) => ({
    id: i + 1,
    title: titles[i] || `任务 ${i + 1}`,
    description: descriptions[i] || `这是任务 ${i + 1} 的详细描述`,
    completed: Math.random() > 0.5,
    priority: randomChoice(priorities),
    dueDate: randomFutureDate(),
    createdAt: randomDate(),
    updatedAt: randomDate()
  }));
}

// 生成用户数据
function generateUsers() {
  const usernames = ['admin', 'user1', 'user2', 'manager', 'developer', 'tester', 'designer', 'analyst'];
  const statuses = ['active', 'inactive'];
  
  return usernames.map((username, i) => ({
    id: i + 1,
    username,
    email: `${username}@example.com`,
    password: '123456', // 在实际应用中不应该存储明文密码
    nickname: `${username}用户`,
    avatar: `https://via.placeholder.com/40?text=${username.charAt(0).toUpperCase()}`,
    roleIds: i === 0 ? [1] : [2, 3], // admin用户有管理员角色，其他用户有普通角色
    status: i < 6 ? 'active' : 'inactive',
    departmentId: Math.floor(i / 2) + 1,
    phone: `138${String(i).padStart(8, '0')}`,
    createdAt: randomDate(),
    updatedAt: randomDate()
  }));
}

// 生成角色数据
function generateRoles() {
  return [
    {
      id: 1,
      name: '系统管理员',
      code: 'admin',
      description: '拥有系统所有权限',
      permissionIds: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
      createdAt: randomDate(),
      updatedAt: randomDate()
    },
    {
      id: 2,
      name: '普通用户',
      code: 'user',
      description: '普通用户权限',
      permissionIds: [1, 2, 5, 6],
      createdAt: randomDate(),
      updatedAt: randomDate()
    },
    {
      id: 3,
      name: '部门经理',
      code: 'manager',
      description: '部门管理权限',
      permissionIds: [1, 2, 3, 5, 6, 7],
      createdAt: randomDate(),
      updatedAt: randomDate()
    }
  ];
}

// 生成权限数据
function generatePermissions() {
  return [
    { id: 1, name: '查看待办', code: 'todo:view', description: '查看待办事项', module: 'todo', createdAt: randomDate(), updatedAt: randomDate() },
    { id: 2, name: '创建待办', code: 'todo:create', description: '创建待办事项', module: 'todo', createdAt: randomDate(), updatedAt: randomDate() },
    { id: 3, name: '编辑待办', code: 'todo:edit', description: '编辑待办事项', module: 'todo', createdAt: randomDate(), updatedAt: randomDate() },
    { id: 4, name: '删除待办', code: 'todo:delete', description: '删除待办事项', module: 'todo', createdAt: randomDate(), updatedAt: randomDate() },
    { id: 5, name: '查看用户', code: 'user:view', description: '查看用户信息', module: 'user', createdAt: randomDate(), updatedAt: randomDate() },
    { id: 6, name: '创建用户', code: 'user:create', description: '创建用户', module: 'user', createdAt: randomDate(), updatedAt: randomDate() },
    { id: 7, name: '编辑用户', code: 'user:edit', description: '编辑用户信息', module: 'user', createdAt: randomDate(), updatedAt: randomDate() },
    { id: 8, name: '删除用户', code: 'user:delete', description: '删除用户', module: 'user', createdAt: randomDate(), updatedAt: randomDate() },
    { id: 9, name: '权限管理', code: 'permission:manage', description: '管理权限', module: 'permission', createdAt: randomDate(), updatedAt: randomDate() },
    { id: 10, name: '系统设置', code: 'system:config', description: '系统配置', module: 'system', createdAt: randomDate(), updatedAt: randomDate() }
  ];
}

// 生成字典类型数据
function generateDictTypes() {
  return [
    {
      id: 1,
      name: '用户状态',
      code: 'user_status',
      description: '用户账户状态分类',
      status: 'active',
      createdAt: randomDate(),
      updatedAt: randomDate()
    },
    {
      id: 2,
      name: '优先级',
      code: 'priority',
      description: '任务优先级分类',
      status: 'active',
      createdAt: randomDate(),
      updatedAt: randomDate()
    },
    {
      id: 3,
      name: '资产类型',
      code: 'asset_type',
      description: '资产分类管理',
      status: 'active',
      createdAt: randomDate(),
      updatedAt: randomDate()
    }
  ];
}

// 生成字典项数据
function generateDictItems() {
  return [
    // 用户状态
    { id: 1, typeId: 1, label: '激活', value: 'active', sort: 1, status: 'active', createdAt: randomDate(), updatedAt: randomDate() },
    { id: 2, typeId: 1, label: '禁用', value: 'inactive', sort: 2, status: 'active', createdAt: randomDate(), updatedAt: randomDate() },
    // 优先级
    { id: 3, typeId: 2, label: '低', value: 'LOW', sort: 1, status: 'active', createdAt: randomDate(), updatedAt: randomDate() },
    { id: 4, typeId: 2, label: '中', value: 'MEDIUM', sort: 2, status: 'active', createdAt: randomDate(), updatedAt: randomDate() },
    { id: 5, typeId: 2, label: '高', value: 'HIGH', sort: 3, status: 'active', createdAt: randomDate(), updatedAt: randomDate() },
    // 资产类型
    { id: 6, typeId: 3, label: '电脑', value: 'computer', sort: 1, status: 'active', createdAt: randomDate(), updatedAt: randomDate() },
    { id: 7, typeId: 3, label: '显示器', value: 'monitor', sort: 2, status: 'active', createdAt: randomDate(), updatedAt: randomDate() },
    { id: 8, typeId: 3, label: '打印机', value: 'printer', sort: 3, status: 'active', createdAt: randomDate(), updatedAt: randomDate() }
  ];
}

// 生成部门数据
function generateDepartments() {
  return [
    {
      id: 1,
      name: '技术部',
      code: 'tech',
      description: '技术研发部门',
      parentId: null,
      sort: 1,
      status: 'active',
      createdAt: randomDate(),
      updatedAt: randomDate(),
      children: [
        {
          id: 2,
          name: '前端组',
          code: 'frontend',
          description: '前端开发团队',
          parentId: 1,
          sort: 1,
          status: 'active',
          createdAt: randomDate(),
          updatedAt: randomDate()
        },
        {
          id: 3,
          name: '后端组',
          code: 'backend',
          description: '后端开发团队',
          parentId: 1,
          sort: 2,
          status: 'active',
          createdAt: randomDate(),
          updatedAt: randomDate()
        }
      ]
    },
    {
      id: 4,
      name: '产品部',
      code: 'product',
      description: '产品设计部门',
      parentId: null,
      sort: 2,
      status: 'active',
      createdAt: randomDate(),
      updatedAt: randomDate(),
      children: [
        {
          id: 5,
          name: 'UI设计组',
          code: 'ui',
          description: 'UI设计团队',
          parentId: 4,
          sort: 1,
          status: 'active',
          createdAt: randomDate(),
          updatedAt: randomDate()
        }
      ]
    },
    {
      id: 6,
      name: '运营部',
      code: 'operations',
      description: '运营推广部门',
      parentId: null,
      sort: 3,
      status: 'active',
      createdAt: randomDate(),
      updatedAt: randomDate()
    }
  ];
}

// 生成资产数据
function generateAssets() {
  const assetTypes = ['computer', 'monitor', 'printer'];
  const statuses = ['available', 'assigned', 'maintenance', 'retired'];
  
  return Array.from({ length: 20 }, (_, i) => ({
    id: i + 1,
    name: `资产设备${i + 1}`,
    type: randomChoice(assetTypes),
    serialNumber: `SN${String(i + 1).padStart(6, '0')}`,
    model: `Model-${randomChoice(['A', 'B', 'C'])}${i + 1}`,
    status: randomChoice(statuses),
    assignedUserId: Math.random() > 0.5 ? Math.floor(Math.random() * 8) + 1 : null,
    purchaseDate: randomDate(),
    warrantyExpiry: randomFutureDate(),
    price: Math.floor(Math.random() * 10000) + 1000,
    description: `这是资产设备${i + 1}的详细描述`,
    createdAt: randomDate(),
    updatedAt: randomDate()
  }));
}

// 生成完整的数据库
function generateDatabase() {
  const data = {
    // 认证相关（用于middleware处理）
    auth: {
      login: {},
      logout: {},
      refresh: {}
    },
    
    // 实际数据
    todos: generateTodos(),
    users: generateUsers(),
    roles: generateRoles(),
    permissions: generatePermissions(),
    dictTypes: generateDictTypes(),
    dictItems: generateDictItems(),
    departments: generateDepartments(),
    assets: generateAssets()
  };

  return data;
}

// 写入文件
const dbData = generateDatabase();
const outputPath = path.join(__dirname, 'db.json');

fs.writeFileSync(outputPath, JSON.stringify(dbData, null, 2), 'utf8');

console.log('✅ Mock数据生成完成!');
console.log(`📁 文件位置: ${outputPath}`);
console.log(`📊 数据统计:`);
console.log(`  - Todos: ${dbData.todos.length}条`);
console.log(`  - Users: ${dbData.users.length}条`);
console.log(`  - Roles: ${dbData.roles.length}条`);
console.log(`  - Permissions: ${dbData.permissions.length}条`);
console.log(`  - Dict Types: ${dbData.dictTypes.length}条`);
console.log(`  - Dict Items: ${dbData.dictItems.length}条`);
console.log(`  - Departments: ${dbData.departments.length}条`);
console.log(`  - Assets: ${dbData.assets.length}条`);