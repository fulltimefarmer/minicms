#!/bin/bash

# Workflow Service API Examples
# 工作流服务API使用示例

BASE_URL="http://localhost:8080/workflow-service/api/workflow"

echo "=== Workflow Service API Examples ==="
echo "Base URL: $BASE_URL"
echo ""

# 1. 提交请假申请
echo "1. 提交请假申请"
curl -X POST "$BASE_URL/leave-request" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "EMP001",
    "employeeName": "张三",
    "leaveType": "年假",
    "startDate": "2024-01-15",
    "endDate": "2024-01-20",
    "reason": "休假",
    "managerId": "MGR001"
  }'
echo -e "\n"

# 2. 查看员工请假记录
echo "2. 查看员工请假记录"
curl -X GET "$BASE_URL/leave-request/employee/EMP001"
echo -e "\n"

# 3. 查看Manager待审批请假
echo "3. 查看Manager待审批请假"
curl -X GET "$BASE_URL/leave-request/manager/MGR001"
echo -e "\n"

# 4. 获取Manager任务列表
echo "4. 获取Manager任务列表"
curl -X GET "$BASE_URL/tasks/group/managers"
echo -e "\n"

# 5. 提交业务申请（不含文件）
echo "5. 提交业务申请"
curl -X POST "$BASE_URL/business-request" \
  -F "employeeId=EMP002" \
  -F "employeeName=李四" \
  -F "businessType=合同审批" \
  -F "title=供应商合同" \
  -F "description=新供应商合同审批" \
  -F "managerId=MGR002"
echo -e "\n"

# 6. 查看员工业务申请记录
echo "6. 查看员工业务申请记录"
curl -X GET "$BASE_URL/business-request/employee/EMP002"
echo -e "\n"

# 7. 查看Manager待审批业务申请
echo "7. 查看Manager待审批业务申请"
curl -X GET "$BASE_URL/business-request/manager/MGR002"
echo -e "\n"

echo "=== API Examples Complete ==="
echo ""
echo "注意事项:"
echo "1. 请确保工作流服务已启动"
echo "2. 审批操作需要获取具体的taskId"
echo "3. 可以通过Camunda Webapp查看流程状态: http://localhost:8080/workflow-service/app"
echo "4. 登录信息: 用户名demo, 密码demo"
echo ""
echo "高级操作示例:"
echo ""
echo "审批请假申请:"
echo 'curl -X POST "$BASE_URL/leave-request/approve" \'
echo '  -H "Content-Type: application/json" \'
echo '  -d "{"taskId": "your-task-id", "managerId": "MGR001", "decision": "APPROVED", "comment": "同意请假"}"'
echo ""
echo "文档审查:"
echo 'curl -X POST "$BASE_URL/business-request/review-document" \'
echo '  -H "Content-Type: application/json" \'
echo '  -d "{"taskId": "your-task-id", "managerId": "MGR002", "documentReviewed": true}"'
echo ""
echo "审批业务申请:"
echo 'curl -X POST "$BASE_URL/business-request/approve" \'
echo '  -H "Content-Type: application/json" \'
echo '  -d "{"taskId": "your-task-id", "managerId": "MGR002", "decision": "APPROVED", "comment": "同意业务申请"}"'