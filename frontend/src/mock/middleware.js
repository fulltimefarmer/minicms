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

  // 模拟认证
  if (req.path.includes('/auth/login')) {
    const { username, password } = req.body;
    if (username === 'admin' && password === '123456') {
      return res.json({
        success: true,
        message: '登录成功',
        data: {
          token: 'mock-jwt-token-' + Date.now(),
          tokenType: 'Bearer',
          userId: 1,
          username: 'admin',
          email: 'admin@example.com',
          nickname: '系统管理员',
          avatar: 'https://via.placeholder.com/40',
          roles: ['admin'],
          permissions: ['*']
        }
      });
    } else {
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