import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';
import { NotificationService } from './notification.service';

interface DictType {
  id?: number;
  typeCode: string;
  typeName: string;
  description?: string;
  status: 'ACTIVE' | 'INACTIVE';
  sortOrder?: number;
  createdAt?: Date;
  updatedAt?: Date;
}

@Component({
  selector: 'app-dict-type-management',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  template: `
    <div class="dict-type-management">
      <div class="header">
        <h1>数据字典类型管理</h1>
        <div class="header-actions">
          <button class="btn btn-secondary" (click)="goToDictItems()">
            <i class="icon">📝</i> 字典项管理
          </button>
          <button class="btn btn-primary" (click)="openAddModal()">
            <i class="icon">+</i> 添加字典类型
          </button>
        </div>
      </div>

      <!-- 搜索和筛选 -->
      <div class="search-bar">
        <input 
          type="text" 
          placeholder="搜索类型编码、名称或描述..." 
          [(ngModel)]="searchTerm"
          (input)="filterItems()"
          class="search-input"
        />
        <select [(ngModel)]="filterStatus" (change)="filterItems()" class="filter-select">
          <option value="">所有状态</option>
          <option value="ACTIVE">启用</option>
          <option value="INACTIVE">禁用</option>
        </select>
        <button class="btn btn-secondary" (click)="refreshData()">
          <i class="icon">🔄</i> 刷新
        </button>
      </div>

      <!-- 字典类型表格 -->
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>类型编码</th>
              <th>类型名称</th>
              <th>描述</th>
              <th>状态</th>
              <th>排序</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let item of filteredItems" [class.inactive]="item.status === 'INACTIVE'">
              <td>{{ item.id }}</td>
              <td>{{ item.typeCode }}</td>
              <td>{{ item.typeName }}</td>
              <td>{{ item.description || '-' }}</td>
              <td>
                <span class="status-badge" [class]="'status-' + item.status.toLowerCase()">
                  {{ item.status === 'ACTIVE' ? '启用' : '禁用' }}
                </span>
              </td>
              <td>{{ item.sortOrder || 0 }}</td>
              <td>{{ formatDate(item.createdAt) }}</td>
              <td class="actions">
                <button class="btn btn-sm btn-info" (click)="viewDictItems(item)">字典项</button>
                <button class="btn btn-sm btn-secondary" (click)="editItem(item)">编辑</button>
                <button 
                  class="btn btn-sm" 
                  [class]="item.status === 'ACTIVE' ? 'btn-warning' : 'btn-success'"
                  (click)="toggleStatus(item)"
                >
                  {{ item.status === 'ACTIVE' ? '禁用' : '启用' }}
                </button>
                <button class="btn btn-sm btn-danger" (click)="deleteItem(item)">删除</button>
              </td>
            </tr>
            <tr *ngIf="filteredItems.length === 0">
              <td colspan="8" class="no-data">暂无数据</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 模态框 -->
      <div class="modal" [class.show]="showModal" (click)="closeModal($event)">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ isEditing ? '编辑字典类型' : '添加字典类型' }}</h3>
            <button class="close-btn" (click)="closeModal()">&times;</button>
          </div>
          <form #itemForm="ngForm" (ngSubmit)="saveItem()">
            <div class="form-group">
              <label for="typeCode">类型编码 *</label>
              <input 
                id="typeCode" 
                name="typeCode" 
                [(ngModel)]="currentItem.typeCode" 
                required 
                pattern="^[a-zA-Z][a-zA-Z0-9_]*$"
                class="form-control"
                #codeInput="ngModel"
                placeholder="如：user_status"
              />
              <div *ngIf="codeInput.invalid && codeInput.touched" class="error">
                <span *ngIf="codeInput.errors?.['required']">类型编码是必填项</span>
                <span *ngIf="codeInput.errors?.['pattern']">编码只能包含字母、数字和下划线，且以字母开头</span>
              </div>
            </div>
            
            <div class="form-group">
              <label for="typeName">类型名称 *</label>
              <input 
                id="typeName" 
                name="typeName" 
                [(ngModel)]="currentItem.typeName" 
                required 
                class="form-control"
                #nameInput="ngModel"
                placeholder="如：用户状态"
              />
              <div *ngIf="nameInput.invalid && nameInput.touched" class="error">类型名称是必填项</div>
            </div>
            
            <div class="form-group">
              <label for="description">描述</label>
              <textarea 
                id="description" 
                name="description" 
                [(ngModel)]="currentItem.description" 
                class="form-control"
                rows="3"
                placeholder="字典类型的详细描述"
              ></textarea>
            </div>
            
            <div class="form-group">
              <label for="sortOrder">排序序号</label>
              <input 
                id="sortOrder" 
                name="sortOrder" 
                type="number"
                [(ngModel)]="currentItem.sortOrder" 
                class="form-control"
                min="0"
                placeholder="0"
              />
            </div>
            
            <div class="form-group">
              <label for="status">状态</label>
              <select 
                id="status" 
                name="status" 
                [(ngModel)]="currentItem.status" 
                class="form-control"
              >
                <option value="ACTIVE">启用</option>
                <option value="INACTIVE">禁用</option>
              </select>
            </div>
            
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="closeModal()">取消</button>
              <button type="submit" class="btn btn-primary" [disabled]="!itemForm.form.valid || saving">
                {{ saving ? '保存中...' : (isEditing ? '更新' : '保存') }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- 统计信息 -->
      <div class="stats">
        <div class="stat-item">
          <h4>{{ getTotalItems() }}</h4>
          <p>总类型数</p>
        </div>
        <div class="stat-item">
          <h4>{{ getActiveItems() }}</h4>
          <p>启用类型</p>
        </div>
        <div class="stat-item">
          <h4>{{ getInactiveItems() }}</h4>
          <p>禁用类型</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dict-type-management {
      padding: 2rem;
      max-width: 1400px;
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

    .header-actions {
      display: flex;
      gap: 1rem;
    }

    .search-bar {
      display: flex;
      gap: 1rem;
      margin-bottom: 2rem;
      flex-wrap: wrap;
    }

    .search-input {
      flex: 1;
      min-width: 250px;
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

    .data-table {
      width: 100%;
      border-collapse: collapse;
    }

    .data-table th,
    .data-table td {
      padding: 1rem;
      text-align: left;
      border-bottom: 1px solid #eee;
    }

    .data-table th {
      background: #f8f9fa;
      font-weight: 600;
      color: #333;
      position: sticky;
      top: 0;
    }

    .data-table tr:hover {
      background: #f8f9fa;
    }

    .data-table tr.inactive {
      opacity: 0.6;
    }

    .status-badge {
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      font-size: 0.875rem;
      font-weight: 500;
    }

    .status-active { background: #e8f5e8; color: #2e7d32; }
    .status-inactive { background: #ffebee; color: #c62828; }

    .actions {
      display: flex;
      gap: 0.5rem;
      flex-wrap: wrap;
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

    .btn:disabled {
      opacity: 0.6;
      cursor: not-allowed;
      transform: none;
    }

    .btn-primary { background: #1976d2; color: white; }
    .btn-secondary { background: #6c757d; color: white; }
    .btn-info { background: #0288d1; color: white; }
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
      font-size: 0.875rem;
    }

    .form-control:focus {
      outline: none;
      border-color: #1976d2;
      box-shadow: 0 0 0 2px rgba(25,118,210,0.2);
    }

    textarea.form-control {
      resize: vertical;
      min-height: 80px;
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

    .no-data {
      text-align: center;
      color: #666;
      font-style: italic;
    }

    @media (max-width: 768px) {
      .dict-type-management {
        padding: 1rem;
      }

      .header {
        flex-direction: column;
        gap: 1rem;
        align-items: stretch;
      }

      .header-actions {
        justify-content: center;
      }

      .search-bar {
        flex-direction: column;
      }

      .search-input {
        min-width: auto;
      }

      .actions {
        flex-direction: column;
      }
    }
  `]
})
export class DictTypeManagementComponent implements OnInit {
  items: DictType[] = [];
  filteredItems: DictType[] = [];
  searchTerm = '';
  filterStatus = '';

  showModal = false;
  isEditing = false;
  saving = false;
  currentItem: Partial<DictType> = {};

  private apiUrl = 'http://localhost:8080/api/dict-types';

  constructor(private http: HttpClient, private router: Router, private notification: NotificationService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.http.get<any>(`${this.apiUrl}`).subscribe({
      next: (response) => {
        if (response.success) {
          this.items = response.data || [];
          this.filterItems();
        }
      },
      error: (error) => {
        console.error('加载字典类型失败:', error);
        this.notification.alert('加载数据失败，请检查网络连接');
      }
    });
  }

  refreshData() {
    this.loadData();
  }

  filterItems() {
    this.filteredItems = this.items.filter(item => {
      const matchesSearch = !this.searchTerm || 
        item.typeCode.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        item.typeName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        (item.description && item.description.toLowerCase().includes(this.searchTerm.toLowerCase()));
      
      const matchesStatus = !this.filterStatus || item.status === this.filterStatus;
      
      return matchesSearch && matchesStatus;
    });
  }

  openAddModal() {
    this.isEditing = false;
    this.currentItem = {
      status: 'ACTIVE',
      sortOrder: 0
    };
    this.showModal = true;
  }

  editItem(item: DictType) {
    this.isEditing = true;
    this.currentItem = { ...item };
    this.showModal = true;
  }

  closeModal(event?: Event) {
    this.showModal = false;
    this.currentItem = {};
  }

  saveItem() {
    if (this.saving) return;
    
    this.saving = true;
    const url = this.isEditing ? `${this.apiUrl}/${this.currentItem.id}` : this.apiUrl;

    const request$ = this.isEditing 
      ? this.http.put<any>(url, this.currentItem)
      : this.http.post<any>(url, this.currentItem);

    request$.subscribe({
      next: (response) => {
        this.saving = false;
        if (response.success) {
          this.notification.alert(this.isEditing ? '更新成功' : '添加成功');
          this.closeModal();
          this.loadData();
        } else {
          this.notification.alert(response.message || '操作失败');
        }
      },
      error: (error) => {
        this.saving = false;
        console.error('保存失败:', error);
        this.notification.alert(error.error?.message || '保存失败，请重试');
      }
    });
  }

  toggleStatus(item: DictType) {
    const newStatus = item.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    const action = newStatus === 'ACTIVE' ? '启用' : '禁用';
    
    if (this.notification.confirm(`确定要${action}字典类型"${item.typeName}"吗？`)) {
      this.http.put<any>(`${this.apiUrl}/${item.id}/status`, null, {
        params: { status: newStatus }
      }).subscribe({
        next: (response) => {
          if (response.success) {
            item.status = newStatus;
            this.notification.alert(`${action}成功`);
            this.filterItems();
          } else {
            this.notification.alert(response.message || `${action}失败`);
          }
        },
        error: (error) => {
          console.error(`${action}失败:`, error);
          this.notification.alert(`${action}失败，请重试`);
        }
      });
    }
  }

  deleteItem(item: DictType) {
    if (this.notification.confirm(`确定要删除字典类型"${item.typeName}"吗？删除后不可恢复。`)) {
      this.http.delete<any>(`${this.apiUrl}/${item.id}`).subscribe({
        next: (response) => {
          if (response.success) {
            this.notification.alert('删除成功');
            this.loadData();
          } else {
            this.notification.alert(response.message || '删除失败');
          }
        },
        error: (error) => {
          console.error('删除失败:', error);
          this.notification.alert(error.error?.message || '删除失败，请重试');
        }
      });
    }
  }

  viewDictItems(dictType: DictType) {
    this.router.navigate(['/dict-items'], { 
      queryParams: { typeId: dictType.id, typeName: dictType.typeName } 
    });
  }

  goToDictItems() {
    this.router.navigate(['/dict-items']);
  }

  formatDate(date: Date | undefined): string {
    if (!date) return '-';
    return new Date(date).toLocaleDateString('zh-CN');
  }

  getTotalItems(): number {
    return this.items.length;
  }

  getActiveItems(): number {
    return this.items.filter(item => item.status === 'ACTIVE').length;
  }

  getInactiveItems(): number {
    return this.items.filter(item => item.status === 'INACTIVE').length;
  }
}