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

  // 为admin用户创建特定的待办事项
  const adminTodos = [
    {
      id: 1,
      title: '审核本周系统权限变更',
      description: '检查并审核本周提交的所有系统权限变更请求，确保符合安全策略',
      completed: false,
      priority: 'HIGH',
      dueDate: new Date(new Date().setDate(new Date().getDate() + 2)).toISOString(), // 两天后到期
      createdAt: new Date(new Date().setDate(new Date().getDate() - 1)).toISOString(), // 昨天创建
      updatedAt: new Date().toISOString() // 今天更新
    },
    {
      id: 2,
      title: '完成新员工账号配置',
      description: '为下周入职的5名新员工创建账号并分配适当的系统权限',
      completed: false,
      priority: 'MEDIUM',
      dueDate: new Date(new Date().setDate(new Date().getDate() + 3)).toISOString(), // 三天后到期
      createdAt: new Date(new Date().setDate(new Date().getDate() - 2)).toISOString(), // 两天前创建
      updatedAt: new Date().toISOString() // 今天更新
    },
    {
      id: 3,
      title: '系统安全漏洞修复',
      description: '修复安全团队上周报告的三个高危安全漏洞',
      completed: false,
      priority: 'HIGH',
      dueDate: new Date(new Date().setDate(new Date().getDate() + 1)).toISOString(), // 明天到期
      createdAt: new Date(new Date().setDate(new Date().getDate() - 3)).toISOString(), // 三天前创建
      updatedAt: new Date().toISOString() // 今天更新
    },
    {
      id: 4,
      title: '更新系统备份策略',
      description: '根据新的数据保护要求，更新系统备份策略和恢复流程',
      completed: true,
      priority: 'MEDIUM',
      dueDate: new Date(new Date().setDate(new Date().getDate() - 1)).toISOString(), // 昨天到期
      createdAt: new Date(new Date().setDate(new Date().getDate() - 5)).toISOString(), // 五天前创建
      updatedAt: new Date().toISOString() // 今天更新
    },
    {
      id: 5,
      title: '准备月度系统报告',
      description: '收集系统使用数据，准备本月系统性能和安全报告',
      completed: false,
      priority: 'LOW',
      dueDate: new Date(new Date().setDate(new Date().getDate() + 5)).toISOString(), // 五天后到期
      createdAt: new Date(new Date().setDate(new Date().getDate() - 1)).toISOString(), // 昨天创建
      updatedAt: new Date().toISOString() // 今天更新
    },
    {
      id: 6,
      title: '审核部门资源申请',
      description: '审核并批准各部门提交的系统资源申请',
      completed: false,
      priority: 'MEDIUM',
      dueDate: new Date(new Date().setDate(new Date().getDate() + 2)).toISOString(), // 两天后到期
      createdAt: new Date(new Date().setDate(new Date().getDate() - 2)).toISOString(), // 两天前创建
      updatedAt: new Date().toISOString() // 今天更新
    },
    {
      id: 7,
      title: '更新系统文档',
      description: '更新管理员手册和用户指南，反映最新的系统变更',
      completed: false,
      priority: 'LOW',
      dueDate: new Date(new Date().setDate(new Date().getDate() + 7)).toISOString(), // 一周后到期
      createdAt: new Date(new Date().setDate(new Date().getDate() - 1)).toISOString(), // 昨天创建
      updatedAt: new Date().toISOString() // 今天更新
    },
    {
      id: 8,
      title: '系统性能优化',
      description: '分析系统性能瓶颈，实施优化措施提高响应速度',
      completed: false,
      priority: 'HIGH',
      dueDate: new Date(new Date().setDate(new Date().getDate() + 4)).toISOString(), // 四天后到期
      createdAt: new Date(new Date().setDate(new Date().getDate() - 3)).toISOString(), // 三天前创建
      updatedAt: new Date().toISOString() // 今天更新
    },
    {
      id: 9,
      title: '配置新服务器',
      description: '为新上线的应用配置服务器环境和安全设置',
      completed: true,
      priority: 'MEDIUM',
      dueDate: new Date(new Date().setDate(new Date().getDate() - 2)).toISOString(), // 两天前到期
      createdAt: new Date(new Date().setDate(new Date().getDate() - 7)).toISOString(), // 一周前创建
      updatedAt: new Date(new Date().setDate(new Date().getDate() - 2)).toISOString() // 两天前更新
    },
    {
      id: 10,
      title: '用户权限审计',
      description: '对所有系统用户进行权限审计，确保符合最小权限原则',
      completed: false,
      priority: 'HIGH',
      dueDate: new Date(new Date().setDate(new Date().getDate() + 3)).toISOString(), // 三天后到期
      createdAt: new Date(new Date().setDate(new Date().getDate() - 2)).toISOString(), // 两天前创建
      updatedAt: new Date().toISOString() // 今天更新
    }
  ];

  // 生成其他待办事项
  const otherTodos = Array.from({ length: 15 }, (_, i) => ({
    id: i + 11, // ID从11开始
    title: titles[i] || `任务 ${i + 11}`,
    description: descriptions[i] || `这是任务 ${i + 11} 的详细描述`,
    completed: Math.random() > 0.5,
    priority: randomChoice(priorities),
    dueDate: randomFutureDate(),
    createdAt: randomDate(),
    updatedAt: randomDate()
  }));

  // 合并admin的待办事项和其他待办事项
  return [...adminTodos, ...otherTodos];
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
        },
        {
          id: 4,
          name: '测试组',
          code: 'testing',
          description: '测试与质量保障团队',
          parentId: 1,
          sort: 3,
          status: 'active',
          createdAt: randomDate(),
          updatedAt: randomDate()
        },
        {
          id: 5,
          name: '运维组',
          code: 'devops',
          description: '系统运维与部署团队',
          parentId: 1,
          sort: 4,
          status: 'active',
          createdAt: randomDate(),
          updatedAt: randomDate()
        }
      ]
    },
    {
      id: 6,
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
          id: 7,
          name: 'UI设计组',
          code: 'ui',
          description: 'UI设计团队',
          parentId: 6,
          sort: 1,
          status: 'active',
          createdAt: randomDate(),
          updatedAt: randomDate()
        },
        {
          id: 8,
          name: '用户体验组',
          code: 'ux',
          description: '用户体验研究团队',
          parentId: 6,
          sort: 2,
          status: 'active',
          createdAt: randomDate(),
          updatedAt: randomDate()
        },
        {
          id: 9,
          name: '产品规划组',
          code: 'planning',
          description: '产品规划与路线图团队',
          parentId: 6,
          sort: 3,
          status: 'active',
          createdAt: randomDate(),
          updatedAt: randomDate()
        }
      ]
    },
    {
      id: 10,
      name: '运营部',
      code: 'operations',
      description: '运营推广部门',
      parentId: null,
      sort: 3,
      status: 'active',
      createdAt: randomDate(),
      updatedAt: randomDate(),
      children: [
        {
          id: 11,
          name: '市场营销组',
          code: 'marketing',
          description: '市场营销与推广团队',
          parentId: 10,
          sort: 1,
          status: 'active',
          createdAt: randomDate(),
          updatedAt: randomDate()
        },
        {
          id: 12,
          name: '用户运营组',
          code: 'user-ops',
          description: '用户运营与活动策划团队',
          parentId: 10,
          sort: 2,
          status: 'active',
          createdAt: randomDate(),
          updatedAt: randomDate()
        }
      ]
    },
    {
      id: 13,
      name: '人力资源部',
      code: 'hr',
      description: '人力资源管理部门',
      parentId: null,
      sort: 4,
      status: 'active',
      createdAt: randomDate(),
      updatedAt: randomDate(),
      children: [
        {
          id: 14,
          name: '招聘组',
          code: 'recruitment',
          description: '人才招聘团队',
          parentId: 13,
          sort: 1,
          status: 'active',
          createdAt: randomDate(),
          updatedAt: randomDate()
        },
        {
          id: 15,
          name: '培训发展组',
          code: 'training',
          description: '员工培训与发展团队',
          parentId: 13,
          sort: 2,
          status: 'active',
          createdAt: randomDate(),
          updatedAt: randomDate()
        }
      ]
    },
    {
      id: 16,
      name: '财务部',
      code: 'finance',
      description: '财务管理部门',
      parentId: null,
      sort: 5,
      status: 'active',
      createdAt: randomDate(),
      updatedAt: randomDate()
    }
  ];
}

// 生成资产数据
function generateAssets() {
  // 定义更多资产类型和品牌
  const assetTypes = ['computer', 'monitor', 'printer', 'server', 'network', 'mobile', 'tablet', 'projector', 'camera'];
  const statuses = ['available', 'assigned', 'maintenance', 'retired'];
  const brands = ['Dell', 'HP', 'Lenovo', 'Apple', 'Samsung', 'Cisco', 'Huawei', 'Canon', 'Epson'];
  
  // 预定义一些特定资产
  const specificAssets = [
    {
      id: 1,
      name: '开发服务器',
      type: 'server',
      serialNumber: 'SRV20240001',
      model: 'Dell PowerEdge R740',
      status: 'available',
      assignedUserId: null,
      purchaseDate: new Date(2024, 1, 15).toISOString(),
      warrantyExpiry: new Date(2027, 1, 15).toISOString(),
      price: 45000,
      description: '主要开发环境服务器，配置：64GB RAM, 8TB存储, Intel Xeon处理器',
      createdAt: new Date(2024, 1, 16).toISOString(),
      updatedAt: new Date(2024, 1, 16).toISOString()
    },
    {
      id: 2,
      name: '测试服务器',
      type: 'server',
      serialNumber: 'SRV20240002',
      model: 'HP ProLiant DL380',
      status: 'available',
      assignedUserId: null,
      purchaseDate: new Date(2024, 1, 15).toISOString(),
      warrantyExpiry: new Date(2027, 1, 15).toISOString(),
      price: 38000,
      description: '测试环境服务器，配置：32GB RAM, 4TB存储, Intel Xeon处理器',
      createdAt: new Date(2024, 1, 16).toISOString(),
      updatedAt: new Date(2024, 1, 16).toISOString()
    },
    {
      id: 3,
      name: '核心交换机',
      type: 'network',
      serialNumber: 'NET20240001',
      model: 'Cisco Catalyst 9300',
      status: 'available',
      assignedUserId: null,
      purchaseDate: new Date(2024, 0, 10).toISOString(),
      warrantyExpiry: new Date(2029, 0, 10).toISOString(),
      price: 25000,
      description: '数据中心核心交换机，48端口千兆，4个10G上行端口',
      createdAt: new Date(2024, 0, 11).toISOString(),
      updatedAt: new Date(2024, 0, 11).toISOString()
    },
    {
      id: 4,
      name: '防火墙',
      type: 'network',
      serialNumber: 'NET20240002',
      model: 'Fortinet FortiGate 100F',
      status: 'available',
      assignedUserId: null,
      purchaseDate: new Date(2024, 0, 10).toISOString(),
      warrantyExpiry: new Date(2029, 0, 10).toISOString(),
      price: 18000,
      description: '企业级防火墙，支持VPN、入侵防护、Web过滤等功能',
      createdAt: new Date(2024, 0, 11).toISOString(),
      updatedAt: new Date(2024, 0, 11).toISOString()
    },
    {
      id: 5,
      name: '会议室投影仪',
      type: 'projector',
      serialNumber: 'PRJ20240001',
      model: 'Epson EB-2250U',
      status: 'available',
      assignedUserId: null,
      purchaseDate: new Date(2024, 2, 5).toISOString(),
      warrantyExpiry: new Date(2026, 2, 5).toISOString(),
      price: 6500,
      description: '大会议室投影仪，5000流明，WUXGA分辨率',
      createdAt: new Date(2024, 2, 6).toISOString(),
      updatedAt: new Date(2024, 2, 6).toISOString()
    }
  ];
  
  // 生成其他随机资产
  const randomAssets = Array.from({ length: 25 }, (_, i) => {
    const type = randomChoice(assetTypes);
    const brand = randomChoice(brands);
    let name, model;
    
    switch(type) {
      case 'computer':
        name = `${brand} 笔记本电脑`;
        model = `${brand} ${['ThinkPad', 'Latitude', 'EliteBook', 'MacBook Pro', 'XPS'][Math.floor(Math.random() * 5)]} ${new Date().getFullYear()}`;
        break;
      case 'monitor':
        name = `${brand} 显示器`;
        model = `${brand} ${['P2419H', 'U2720Q', 'S2721QS', 'ProDisplay', 'UltraSharp'][Math.floor(Math.random() * 5)]}`;
        break;
      case 'printer':
        name = `${brand} 打印机`;
        model = `${brand} ${['LaserJet', 'OfficeJet', 'EcoTank', 'PIXMA', 'WorkForce'][Math.floor(Math.random() * 5)]} ${Math.floor(Math.random() * 9000) + 1000}`;
        break;
      case 'mobile':
        name = `${brand} 手机`;
        model = `${brand} ${['Galaxy', 'iPhone', 'Mate', 'P40', 'Mi'][Math.floor(Math.random() * 5)]} ${Math.floor(Math.random() * 20) + 10}`;
        break;
      case 'tablet':
        name = `${brand} 平板电脑`;
        model = `${brand} ${['iPad', 'Galaxy Tab', 'MatePad', 'Surface', 'Mi Pad'][Math.floor(Math.random() * 5)]} ${Math.floor(Math.random() * 10) + 1}`;
        break;
      default:
        name = `${brand} ${type}`;
        model = `${brand} ${type.charAt(0).toUpperCase() + type.slice(1)} ${Math.floor(Math.random() * 900) + 100}`;
    }
    
    return {
      id: i + specificAssets.length + 1,
      name: name,
      type: type,
      serialNumber: `${type.substring(0, 3).toUpperCase()}${String(i + 1).padStart(6, '0')}`,
      model: model,
      status: randomChoice(statuses),
      assignedUserId: Math.random() > 0.5 ? Math.floor(Math.random() * 8) + 1 : null,
      purchaseDate: randomDate(),
      warrantyExpiry: randomFutureDate(),
      price: Math.floor(Math.random() * 15000) + 1000,
      description: `${brand} ${type}设备，用于${['办公', '开发', '测试', '会议', '演示'][Math.floor(Math.random() * 5)]}`,
      createdAt: randomDate(),
      updatedAt: randomDate()
    };
  });
  
  // 合并特定资产和随机资产
  return [...specificAssets, ...randomAssets];
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