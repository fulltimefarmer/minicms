# 数据字典功能说明

## 功能概述

数据字典功能为CMS系统提供了统一的字典数据管理能力，支持字典类型和字典项的增删改查操作，提高系统配置的灵活性和可维护性。

## 技术架构

### 后端架构 (Spring Boot 3)

```
common-service/
├── entity/           # 实体类
│   ├── DictType.java      # 字典类型实体
│   └── DictItem.java      # 字典项实体
├── repository/       # 数据访问层
│   ├── DictTypeRepository.java
│   └── DictItemRepository.java
├── service/         # 业务逻辑层
│   ├── DictTypeService.java
│   ├── DictItemService.java
│   ├── impl/
│   │   ├── DictTypeServiceImpl.java
│   │   └── DictItemServiceImpl.java
└── controller/      # 控制器层
    ├── DictTypeController.java
    └── DictItemController.java
```

### 前端架构 (Angular 20)

```
frontend/src/app/
├── dict-type-management.component.ts    # 字典类型管理页面
├── dict-item-management.component.ts    # 字典项管理页面
├── app.routes.ts                        # 路由配置
└── app.html                            # 导航菜单
```

### 数据库结构

```sql
-- 字典类型表
CREATE TABLE dict_type (
    id BIGSERIAL PRIMARY KEY,
    type_code VARCHAR(50) NOT NULL UNIQUE,
    type_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    deleted BOOLEAN DEFAULT FALSE
);

-- 字典项表
CREATE TABLE dict_item (
    id BIGSERIAL PRIMARY KEY,
    type_id BIGINT NOT NULL,
    item_code VARCHAR(50) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    item_value VARCHAR(500),
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    sort_order INTEGER DEFAULT 0,
    parent_id BIGINT,
    level_depth INTEGER DEFAULT 1,
    css_class VARCHAR(100),
    icon VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    deleted BOOLEAN DEFAULT FALSE
);
```

## 功能特性

### 字典类型管理
- ✅ 字典类型的增删改查
- ✅ 类型编码唯一性验证
- ✅ 状态启用/禁用切换
- ✅ 按编码、名称、描述搜索
- ✅ 按状态筛选
- ✅ 排序支持

### 字典项管理
- ✅ 字典项的增删改查
- ✅ 字典项编码唯一性验证（同类型下）
- ✅ 状态启用/禁用切换
- ✅ 层级结构支持（父子关系）
- ✅ CSS样式类和图标支持
- ✅ 多条件搜索和筛选
- ✅ 排序支持

### 用户界面
- ✅ 响应式设计，支持移动端
- ✅ 模态框表单，用户体验友好
- ✅ 实时搜索和筛选
- ✅ 数据统计展示
- ✅ 操作确认提示
- ✅ 错误处理和用户反馈

## API接口

### 字典类型接口

```http
# 分页查询字典类型
GET /api/dict-types?page=1&size=10&typeCode=user&status=ACTIVE

# 查询所有启用的字典类型
GET /api/dict-types/active

# 根据ID查询字典类型
GET /api/dict-types/{id}

# 根据类型编码查询字典类型
GET /api/dict-types/code/{typeCode}

# 新增字典类型
POST /api/dict-types
Content-Type: application/json
{
  "typeCode": "user_status",
  "typeName": "用户状态",
  "description": "用户账号状态字典",
  "status": "ACTIVE",
  "sortOrder": 1
}

# 更新字典类型
PUT /api/dict-types/{id}

# 删除字典类型
DELETE /api/dict-types/{id}

# 批量删除字典类型
DELETE /api/dict-types/batch
Content-Type: application/json
[1, 2, 3]

# 更新字典类型状态
PUT /api/dict-types/{id}/status?status=INACTIVE

# 检查类型编码是否存在
GET /api/dict-types/check-code?typeCode=user_status&excludeId=1
```

### 字典项接口

```http
# 分页查询字典项
GET /api/dict-items?page=1&size=10&typeId=1&status=ACTIVE

# 根据类型ID查询所有字典项
GET /api/dict-items/type/{typeId}

# 根据类型编码查询所有字典项
GET /api/dict-items/type-code/{typeCode}

# 根据ID查询字典项
GET /api/dict-items/{id}

# 根据类型编码和字典项编码查询字典项
GET /api/dict-items/{typeCode}/{itemCode}

# 查询子级字典项
GET /api/dict-items/children/{parentId}

# 新增字典项
POST /api/dict-items
Content-Type: application/json
{
  "typeId": 1,
  "itemCode": "active",
  "itemName": "正常",
  "itemValue": "ACTIVE",
  "description": "用户账号正常状态",
  "status": "ACTIVE",
  "sortOrder": 1,
  "levelDepth": 1
}

# 更新字典项
PUT /api/dict-items/{id}

# 删除字典项
DELETE /api/dict-items/{id}

# 批量删除字典项
DELETE /api/dict-items/batch

# 更新字典项状态
PUT /api/dict-items/{id}/status?status=INACTIVE

# 检查字典项编码是否存在
GET /api/dict-items/check-code?typeId=1&itemCode=active&excludeId=1

# 根据字典值获取字典项名称
GET /api/dict-items/name-by-value?typeCode=user_status&itemValue=ACTIVE

# 根据字典编码获取字典项名称
GET /api/dict-items/name-by-code?typeCode=user_status&itemCode=active
```

## 预置数据

系统已预置以下字典类型和字典项：

### 字典类型
- `user_status` - 用户状态
- `gender` - 性别
- `dept_type` - 部门类型
- `asset_type` - 资产类型
- `asset_status` - 资产状态
- `doc_type` - 文档类型
- `priority_level` - 优先级
- `audit_risk_level` - 风险级别

### 字典项示例
- 用户状态：正常(ACTIVE)、禁用(INACTIVE)、锁定(LOCKED)
- 性别：男(M)、女(F)、未知(U)
- 部门类型：公司(COMPANY)、事业部(DIVISION)、部门(DEPARTMENT)、小组(TEAM)

## 部署说明

### 数据库迁移
执行数据库迁移脚本：
```bash
# 脚本位置：cms/scripts/db/migration/V000007__create_data_dictionary_tables.sql
```

### 后端部署
```bash
cd cms
mvn clean package
java -jar common-service/target/common-service.jar
```

### 前端部署
```bash
cd frontend
npm install
npm run build
npm start
```

## 使用指南

### 管理字典类型
1. 访问系统主页，点击"数据字典"菜单
2. 在字典类型管理页面，可以：
   - 查看所有字典类型列表
   - 点击"添加字典类型"新增类型
   - 点击"编辑"修改现有类型
   - 点击"启用/禁用"切换状态
   - 点击"字典项"管理该类型下的字典项

### 管理字典项
1. 从字典类型页面点击"字典项"按钮，或直接访问字典项管理页面
2. 选择要管理的字典类型
3. 在字典项列表中，可以：
   - 查看该类型下的所有字典项
   - 添加、编辑、删除字典项
   - 启用/禁用字典项
   - 设置层级关系和排序

### 在其他模块中使用字典
```java
// 注入服务
@Autowired
private DictItemService dictItemService;

// 根据类型编码获取字典项
List<DictItem> userStatusOptions = dictItemService.getByTypeCode("user_status");

// 根据字典值获取显示名称
String statusName = dictItemService.getItemNameByValue("user_status", "ACTIVE");
```

## 扩展功能

系统预留了以下扩展接口：
- 层级字典支持（父子关系）
- CSS样式类支持（用于前端显示）
- 图标支持（用于前端显示）
- 缓存支持（可添加Redis缓存）
- 多语言支持（可扩展国际化）

## 注意事项

1. **编码规范**：字典类型编码和字典项编码建议使用下划线命名法，如 `user_status`
2. **删除限制**：删除字典类型前，请确保没有关联的字典项
3. **状态管理**：禁用字典类型会影响该类型下所有字典项的可用性
4. **排序规则**：数字越小排序越靠前，建议使用10的倍数便于插入新项
5. **层级深度**：建议层级深度不超过3级，以保证性能和用户体验

## 技术栈版本

- Spring Boot: 3.x
- MyBatis Plus: 3.x
- PostgreSQL: 14+
- Angular: 20.x
- TypeScript: 5.x

## 联系支持

如有问题或建议，请联系开发团队或提交Issue。