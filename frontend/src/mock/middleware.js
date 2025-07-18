module.exports = (req, res, next) => {
  // 添加 CORS 头
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,PATCH,OPTIONS');
  res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization, Content-Length, X-Requested-With');

  // 预检请求
  if (req.method === 'OPTIONS') {
    res.sendStatus(200);
    return;
  }
  
  // 确保请求体已解析
  if (req.method === 'POST' && req.body && typeof req.body === 'string') {
    try {
      req.body = JSON.parse(req.body);
    } catch (e) {
      console.log('请求体解析失败:', e);
    }
  }

  // 模拟认证
  if (req.path.includes('/auth/login') && req.method === 'POST') {
    // 打印完整的请求信息以便调试
    console.log('登录请求路径:', req.path);
    console.log('登录请求方法:', req.method);
    console.log('登录请求头:', req.headers);
    console.log('登录请求体:', req.body);
    
    // 确保req.body是对象
    let username, password;
    if (typeof req.body === 'string') {
      try {
        const parsedBody = JSON.parse(req.body);
        username = parsedBody.username;
        password = parsedBody.password;
      } catch (e) {
        console.error('解析请求体失败:', e);
      }
    } else if (req.body && typeof req.body === 'object') {
      username = req.body.username;
      password = req.body.password;
    }
    
    console.log('提取的登录信息:', { username, password });
    
    // 硬编码检查admin/123456
    if (username === 'admin' && password === '123456') {
      console.log('使用硬编码验证成功');
      
      // 从数据库中获取用户详细信息
      const fs = require('fs');
      const path = require('path');
      const dbPath = path.join(__dirname, 'db.json');
      let db;
      
      try {
        db = JSON.parse(fs.readFileSync(dbPath, 'utf8'));
      } catch (e) {
        console.error('读取数据库失败:', e);
        db = { users: [], roles: [], permissions: [] };
      }
      
      // 获取admin用户
      const adminUser = db.users.find(u => u.username === 'admin') || {
        id: 1,
        username: 'admin',
        email: 'admin@example.com',
        nickname: 'admin用户',
        avatar: 'https://via.placeholder.com/40?text=A',
        roleIds: [1]
      };
      
      // 获取角色和权限
      const adminRoles = db.roles.filter(role => adminUser.roleIds.includes(role.id)) || [
        { id: 1, name: '系统管理员', code: 'admin' }
      ];
      
      // 构建响应
      return res.json({
        success: true,
        message: '登录成功',
        data: {
          token: 'mock-jwt-token-' + Date.now(),
          tokenType: 'Bearer',
          userId: adminUser.id,
          username: adminUser.username,
          email: adminUser.email,
          nickname: adminUser.nickname || '系统管理员',
          avatar: adminUser.avatar || 'https://via.placeholder.com/40?text=A',
          roles: adminRoles.map(role => role.code),
          permissions: ['*'] // 管理员拥有所有权限
        }
      });
    } else {
      // 如果不是admin/123456，尝试从数据库查找
      try {
        const fs = require('fs');
        const path = require('path');
        const dbPath = path.join(__dirname, 'db.json');
        const db = JSON.parse(fs.readFileSync(dbPath, 'utf8'));
        
        const user = db.users.find(u => 
          u.username === String(username) && 
          u.password === String(password) && 
          u.status === 'active'
        );
        
        if (user) {
          // 获取用户角色和权限
          const userRoles = db.roles.filter(role => user.roleIds.includes(role.id));
          const rolePermissionIds = userRoles.flatMap(role => role.permissionIds || []);
          const userPermissions = db.permissions.filter(perm => rolePermissionIds.includes(perm.id));
          
          return res.json({
            success: true,
            message: '登录成功',
            data: {
              token: 'mock-jwt-token-' + Date.now(),
              tokenType: 'Bearer',
              userId: user.id,
              username: user.username,
              email: user.email,
              nickname: user.nickname,
              avatar: user.avatar,
              roles: userRoles.map(role => role.code),
              permissions: userPermissions.map(perm => perm.code)
            }
          });
        }
      } catch (e) {
        console.error('数据库查询失败:', e);
      }
      
      // 调试信息
      console.log('登录失败，未找到匹配用户');
      
      return res.status(401).json({
        success: false,
        message: '用户名或密码错误',
        data: null
      });
    }
  }

  // 模拟登出
  if (req.path.includes('/auth/logout')) {
    return res.json({
      success: true,
      message: '登出成功',
      data: null
    });
  }

  // 模拟刷新token
  if (req.path.includes('/auth/refresh')) {
    return res.json({
      success: true,
      message: '刷新成功',
      data: 'mock-jwt-token-refreshed-' + Date.now()
    });
  }

  // 模拟统计数据
  if (req.path.includes('/todos/stats')) {
    return res.json({
      success: true,
      message: '获取统计数据成功',
      data: {
        total: 25,
        completed: 12,
        pending: 13,
        overdue: 3,
        today: 5
      }
    });
  }

  // 模拟批量操作
  if (req.path.includes('/todos/batch/status')) {
    return res.json({
      success: true,
      message: '批量操作成功',
      data: req.body.todoIds.length
    });
  }

  // 模拟完成/未完成操作
  if (req.path.includes('/complete') || req.path.includes('/incomplete')) {
    const todoId = req.path.split('/')[2];
    return res.json({
      success: true,
      message: '操作成功',
      data: {
        id: parseInt(todoId),
        completed: req.path.includes('/complete')
      }
    });
  }
  
  // 模拟获取所有待办事项
  if (req.path.includes('/todos/all')) {
    const fs = require('fs');
    const path = require('path');
    const dbPath = path.join(__dirname, 'db.json');
    
    try {
      const db = JSON.parse(fs.readFileSync(dbPath, 'utf8'));
      return res.json({
        success: true,
        message: '获取待办事项成功',
        data: db.todos
      });
    } catch (e) {
      console.error('读取待办事项数据失败:', e);
      return res.status(500).json({
        success: false,
        message: '获取待办事项失败',
        data: []
      });
    }
  }
  
  // 模拟部门树结构
  if (req.path.includes('/departments/tree')) {
    const fs = require('fs');
    const path = require('path');
    const dbPath = path.join(__dirname, 'db.json');
    
    try {
      const db = JSON.parse(fs.readFileSync(dbPath, 'utf8'));
      
      // 确保部门数据包含必要的字段
      const enhancedDepartments = db.departments.map(dept => {
        // 添加必要的字段
        const enhancedDept = {
          ...dept,
          path: dept.path || `/${dept.code}`,
          level: dept.level || (dept.parentId ? 2 : 1),
          enabled: dept.enabled !== undefined ? dept.enabled : true
        };
        
        // 处理子部门
        if (enhancedDept.children && Array.isArray(enhancedDept.children)) {
          enhancedDept.children = enhancedDept.children.map(child => ({
            ...child,
            path: child.path || `/${dept.code}/${child.code}`,
            level: child.level || (child.parentId ? 2 : 1),
            enabled: child.enabled !== undefined ? child.enabled : true
          }));
        }
        
        return enhancedDept;
      });
      
      console.log('返回部门树数据:', JSON.stringify(enhancedDepartments).substring(0, 200) + '...');
      
      return res.json({
        success: true,
        message: '获取部门树成功',
        data: enhancedDepartments
      });
    } catch (e) {
      console.error('读取部门数据失败:', e);
      return res.status(500).json({
        success: false,
        message: '获取部门树失败',
        data: []
      });
    }
  }
  
  // 处理普通部门列表请求
  if (req.path === '/departments' || req.path === '/api/departments') {
    const fs = require('fs');
    const path = require('path');
    const dbPath = path.join(__dirname, 'db.json');
    
    try {
      const db = JSON.parse(fs.readFileSync(dbPath, 'utf8'));
      // 扁平化部门列表
      let flatDepartments = [];
      
      const flattenDepts = (depts, parentPath = '') => {
        depts.forEach(dept => {
          const deptPath = parentPath ? `${parentPath}/${dept.code}` : `/${dept.code}`;
          const flatDept = {
            ...dept,
            path: dept.path || deptPath,
            level: dept.level || (dept.parentId ? 2 : 1),
            enabled: dept.enabled !== undefined ? dept.enabled : true
          };
          
          // 删除children字段，因为扁平列表不需要
          const { children, ...deptWithoutChildren } = flatDept;
          flatDepartments.push(deptWithoutChildren);
          
          // 递归处理子部门
          if (children && Array.isArray(children)) {
            flattenDepts(children, deptPath);
          }
        });
      };
      
      flattenDepts(db.departments);
      
      return res.json({
        success: true,
        message: '获取部门列表成功',
        data: flatDepartments
      });
    } catch (e) {
      console.error('读取部门数据失败:', e);
      return res.status(500).json({
        success: false,
        message: '获取部门列表失败',
        data: []
      });
    }
  }

  // 包装所有其他响应为标准API格式
  const originalSend = res.send;
  res.send = function(data) {
    // 如果已经是我们的格式，直接返回
    if (typeof data === 'string') {
      try {
        const parsed = JSON.parse(data);
        if (parsed.success !== undefined) {
          return originalSend.call(this, data);
        }
      } catch (e) {
        // 继续处理
      }
    }

    // 包装为标准格式
    const wrappedData = {
      success: true,
      message: '操作成功',
      data: typeof data === 'string' ? JSON.parse(data) : data
    };
    
    return originalSend.call(this, JSON.stringify(wrappedData));
  };

  next();
};