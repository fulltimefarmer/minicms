import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NotificationService } from '../../services/notification.service';
import { environment } from '../../../environments/environment';

interface Department {
  id: number;
  name: string;
  code: string;
  description?: string;
  parentId?: number;
  path: string;
  level: number;
  sort: number;
  manager?: string;
  phone?: string;
  email?: string;
  address?: string;
  enabled: boolean;
  children?: Department[];
  parentName?: string;
}

@Component({
  selector: 'app-department-management',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  template: `
    <div class="department-management">
      <div class="header">
        <h1>部门管理</h1>
        <button class="btn btn-primary" (click)="openAddDepartmentModal()">
          <i class="icon">+</i> 添加部门
        </button>
      </div>

      <!-- 部门树形结构 -->
      <div class="tree-container">
        <div class="tree-header">
          <h3>部门架构</h3>
          <button class="btn btn-secondary btn-sm" (click)="expandAll()">展开所有</button>
          <button class="btn btn-secondary btn-sm" (click)="collapseAll()">收起所有</button>
        </div>
        
        <div class="tree-content">
          <div *ngFor="let dept of departmentTree" class="tree-node">
            <div class="node-content" [class.expanded]="isExpanded(dept.id)">
              <div class="node-header" (click)="toggleExpand(dept.id)">
                <span class="expand-icon" *ngIf="dept.children && dept.children.length > 0">
                  {{ isExpanded(dept.id) ? '▼' : '▶' }}
                </span>
                <span class="expand-icon invisible" *ngIf="!dept.children || dept.children.length === 0">
                  ●
                </span>
                <span class="node-name">{{ dept.name }}</span>
                <span class="node-code">[{{ dept.code }}]</span>
                <span class="node-manager" *ngIf="dept.manager">- {{ dept.manager }}</span>
                <div class="node-actions">
                  <button class="btn btn-sm btn-secondary" (click)="editDepartment(dept, $event)">编辑</button>
                  <button class="btn btn-sm btn-primary" (click)="addChildDepartment(dept, $event)">添加子部门</button>
                  <button class="btn btn-sm btn-danger" (click)="deleteDepartment(dept, $event)">删除</button>
                </div>
              </div>
              
              <div class="node-children" *ngIf="isExpanded(dept.id) && dept.children && dept.children.length > 0">
                <div *ngFor="let child of dept.children" class="tree-node child-node">
                  <div class="node-content" [class.expanded]="isExpanded(child.id)">
                    <div class="node-header" (click)="toggleExpand(child.id)">
                      <span class="expand-icon" *ngIf="child.children && child.children.length > 0">
                        {{ isExpanded(child.id) ? '▼' : '▶' }}
                      </span>
                      <span class="expand-icon invisible" *ngIf="!child.children || child.children.length === 0">
                        ●
                      </span>
                      <span class="node-name">{{ child.name }}</span>
                      <span class="node-code">[{{ child.code }}]</span>
                      <span class="node-manager" *ngIf="child.manager">- {{ child.manager }}</span>
                      <div class="node-actions">
                        <button class="btn btn-sm btn-secondary" (click)="editDepartment(child, $event)">编辑</button>
                        <button class="btn btn-sm btn-primary" (click)="addChildDepartment(child, $event)">添加子部门</button>
                        <button class="btn btn-sm btn-danger" (click)="deleteDepartment(child, $event)">删除</button>
                      </div>
                    </div>
                    
                    <div class="node-children" *ngIf="isExpanded(child.id) && child.children && child.children.length > 0">
                      <div *ngFor="let grandChild of child.children" class="tree-node grandchild-node">
                        <div class="node-content">
                          <div class="node-header">
                            <span class="expand-icon invisible">●</span>
                            <span class="node-name">{{ grandChild.name }}</span>
                            <span class="node-code">[{{ grandChild.code }}]</span>
                            <span class="node-manager" *ngIf="grandChild.manager">- {{ grandChild.manager }}</span>
                            <div class="node-actions">
                              <button class="btn btn-sm btn-secondary" (click)="editDepartment(grandChild, $event)">编辑</button>
                              <button class="btn btn-sm btn-danger" (click)="deleteDepartment(grandChild, $event)">删除</button>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 部门模态框 -->
      <div class="modal" [class.show]="showModal" (click)="closeModal($event)">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ isEditing ? '编辑部门' : '添加部门' }}</h3>
            <button class="close-btn" (click)="closeModal()">&times;</button>
          </div>
          <form #deptForm="ngForm" (ngSubmit)="saveDepartment()">
            <div class="form-group">
              <label for="name">部门名称 *</label>
              <input 
                id="name" 
                name="name" 
                [(ngModel)]="currentDepartment.name" 
                required 
                class="form-control"
                #nameInput="ngModel"
              />
              <div *ngIf="nameInput.invalid && nameInput.touched" class="error">部门名称是必填项</div>
            </div>
            
            <div class="form-group">
              <label for="code">部门代码 *</label>
              <input 
                id="code" 
                name="code" 
                [(ngModel)]="currentDepartment.code" 
                required 
                class="form-control"
                #codeInput="ngModel"
              />
              <div *ngIf="codeInput.invalid && codeInput.touched" class="error">部门代码是必填项</div>
            </div>
            
            <div class="form-group">
              <label for="description">部门描述</label>
              <textarea 
                id="description" 
                name="description" 
                [(ngModel)]="currentDepartment.description" 
                class="form-control"
                rows="3"
              ></textarea>
            </div>
            
            <div class="form-group">
              <label for="parentId">上级部门</label>
              <select 
                id="parentId" 
                name="parentId" 
                [(ngModel)]="currentDepartment.parentId" 
                class="form-control"
              >
                <option value="">选择上级部门</option>
                <option *ngFor="let dept of flatDepartments" [value]="dept.id">
                  {{ getIndentPrefix(dept.level) }}{{ dept.name }}
                </option>
              </select>
            </div>
            
            <div class="form-group">
              <label for="manager">部门负责人</label>
              <input 
                id="manager" 
                name="manager" 
                [(ngModel)]="currentDepartment.manager" 
                class="form-control"
              />
            </div>
            
            <div class="form-group">
              <label for="phone">联系电话</label>
              <input 
                id="phone" 
                name="phone" 
                [(ngModel)]="currentDepartment.phone" 
                class="form-control"
              />
            </div>
            
            <div class="form-group">
              <label for="email">联系邮箱</label>
              <input 
                id="email" 
                name="email" 
                type="email"
                [(ngModel)]="currentDepartment.email" 
                class="form-control"
              />
            </div>
            
            <div class="form-group">
              <label for="address">办公地址</label>
              <input 
                id="address" 
                name="address" 
                [(ngModel)]="currentDepartment.address" 
                class="form-control"
              />
            </div>
            
            <div class="form-group">
              <label for="sort">排序号</label>
              <input 
                id="sort" 
                name="sort" 
                type="number"
                [(ngModel)]="currentDepartment.sort" 
                class="form-control"
              />
            </div>
            
            <div class="form-group">
              <label for="enabled">状态</label>
              <select 
                id="enabled" 
                name="enabled" 
                [(ngModel)]="currentDepartment.enabled" 
                class="form-control"
              >
                <option value="true">启用</option>
                <option value="false">禁用</option>
              </select>
            </div>
            
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="closeModal()">取消</button>
              <button type="submit" class="btn btn-primary" [disabled]="!deptForm.form.valid">
                {{ isEditing ? '更新' : '保存' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .department-management {
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

    .tree-container {
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
      overflow: hidden;
    }

    .tree-header {
      padding: 1rem;
      background: #f8f9fa;
      border-bottom: 1px solid #eee;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 1rem;
    }

    .tree-header h3 {
      margin: 0;
      color: #333;
    }

    .tree-content {
      padding: 1rem;
    }

    .tree-node {
      margin-bottom: 0.5rem;
    }

    .node-content {
      border: 1px solid #e0e0e0;
      border-radius: 4px;
      transition: all 0.3s ease;
    }

    .node-content:hover {
      border-color: #1976d2;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .node-header {
      display: flex;
      align-items: center;
      padding: 0.75rem;
      cursor: pointer;
      background: white;
      border-radius: 4px;
      transition: background-color 0.2s;
    }

    .node-header:hover {
      background: #f8f9fa;
    }

    .expand-icon {
      width: 20px;
      font-size: 0.8rem;
      color: #666;
      margin-right: 0.5rem;
    }

    .invisible {
      opacity: 0.3;
    }

    .node-name {
      font-weight: 600;
      color: #333;
      margin-right: 0.5rem;
    }

    .node-code {
      color: #666;
      font-size: 0.9rem;
      margin-right: 0.5rem;
    }

    .node-manager {
      color: #888;
      font-size: 0.9rem;
      margin-right: auto;
    }

    .node-actions {
      display: flex;
      gap: 0.5rem;
    }

    .node-children {
      margin-left: 2rem;
      margin-top: 0.5rem;
    }

    .child-node {
      margin-left: 1rem;
    }

    .grandchild-node {
      margin-left: 2rem;
    }

    .child-node .node-content {
      border-left: 3px solid #1976d2;
    }

    .grandchild-node .node-content {
      border-left: 3px solid #4caf50;
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
      max-width: 600px;
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

    @media (max-width: 768px) {
      .department-management {
        padding: 1rem;
      }

      .header {
        flex-direction: column;
        gap: 1rem;
        align-items: stretch;
      }

      .tree-header {
        flex-direction: column;
        gap: 0.5rem;
      }

      .node-header {
        flex-wrap: wrap;
      }

      .node-actions {
        flex-direction: column;
        width: 100%;
        margin-top: 0.5rem;
      }

      .node-children {
        margin-left: 1rem;
      }
    }
  `]
})
export class DepartmentManagementComponent implements OnInit {
  departments: Department[] = [];
  departmentTree: Department[] = [];
  flatDepartments: Department[] = [];
  expandedNodes: Set<number> = new Set();
  
  showModal = false;
  isEditing = false;
  currentDepartment: Partial<Department> = {};
  
  private apiUrl = `${environment.apiUrl}/departments`;

  constructor(
    private http: HttpClient, 
    private notification: NotificationService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.loadDepartments();
      this.expandedNodes.add(1); // 默认展开第一个节点
    }
  }

  loadDepartments() {
    this.http.get<Department[]>(`${this.apiUrl}/tree`).subscribe({
      next: (data) => {
        this.departmentTree = data;
        this.flattenDepartments();
      },
      error: (error) => console.error('加载部门失败:', error)
    });
  }

  flattenDepartments() {
    this.flatDepartments = [];
    this.flattenRecursive(this.departmentTree, this.flatDepartments);
  }

  flattenRecursive(departments: Department[], result: Department[]) {
    for (const dept of departments) {
      result.push(dept);
      if (dept.children && dept.children.length > 0) {
        this.flattenRecursive(dept.children, result);
      }
    }
  }

  isExpanded(nodeId: number): boolean {
    return this.expandedNodes.has(nodeId);
  }

  toggleExpand(nodeId: number) {
    if (this.expandedNodes.has(nodeId)) {
      this.expandedNodes.delete(nodeId);
    } else {
      this.expandedNodes.add(nodeId);
    }
  }

  expandAll() {
    this.flatDepartments.forEach(dept => this.expandedNodes.add(dept.id));
  }

  collapseAll() {
    this.expandedNodes.clear();
  }

  openAddDepartmentModal() {
    this.currentDepartment = {
      enabled: true,
      sort: 0,
      level: 1
    };
    this.isEditing = false;
    this.showModal = true;
  }

  addChildDepartment(parent: Department, event: Event) {
    event.stopPropagation();
    this.currentDepartment = {
      parentId: parent.id,
      enabled: true,
      sort: 0,
      level: parent.level + 1
    };
    this.isEditing = false;
    this.showModal = true;
  }

  editDepartment(department: Department, event: Event) {
    event.stopPropagation();
    this.currentDepartment = { ...department };
    this.isEditing = true;
    this.showModal = true;
  }

  deleteDepartment(department: Department, event: Event) {
    event.stopPropagation();
    if (this.notification.confirm(`确定要删除部门"${department.name}"吗？`)) {
      this.http.delete(`${this.apiUrl}/${department.id}`).subscribe({
        next: () => {
          this.loadDepartments();
          this.notification.alert('部门删除成功');
        },
        error: (error) => {
          console.error('删除部门失败:', error);
          this.notification.alert('删除失败：' + (error.error?.message || '未知错误'));
        }
      });
    }
  }

  saveDepartment() {
    if (this.isEditing) {
      this.http.put<Department>(`${this.apiUrl}/${this.currentDepartment.id}`, this.currentDepartment).subscribe({
        next: () => {
          this.loadDepartments();
          this.closeModal();
          this.notification.alert('部门更新成功');
        },
        error: (error) => {
          console.error('更新部门失败:', error);
          this.notification.alert('更新失败：' + (error.error?.message || '未知错误'));
        }
      });
    } else {
      this.http.post<Department>(this.apiUrl, this.currentDepartment).subscribe({
        next: () => {
          this.loadDepartments();
          this.closeModal();
          this.notification.alert('部门创建成功');
        },
        error: (error) => {
          console.error('创建部门失败:', error);
          this.notification.alert('创建失败：' + (error.error?.message || '未知错误'));
        }
      });
    }
  }

  closeModal(event?: Event) {
    this.showModal = false;
    this.currentDepartment = {};
  }

  getIndentPrefix(level: number): string {
    return '　'.repeat(level - 1);
  }
}