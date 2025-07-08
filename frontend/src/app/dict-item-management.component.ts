import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';

interface DictItem {
  id?: number;
  typeId: number;
  itemCode: string;
  itemName: string;
  itemValue?: string;
  description?: string;
  status: 'ACTIVE' | 'INACTIVE';
  sortOrder?: number;
  parentId?: number;
  levelDepth?: number;
  cssClass?: string;
  icon?: string;
  createdAt?: Date;
  updatedAt?: Date;
}

interface DictType {
  id: number;
  typeCode: string;
  typeName: string;
  status: string;
}

@Component({
  selector: 'app-dict-item-management',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  template: `
    <div class="dict-item-management">
      <div class="header">
        <h1>数据字典项管理</h1>
        <div class="header-info" *ngIf="selectedType">
          <span class="type-badge">{{ selectedType.typeName }} ({{ selectedType.typeCode }})</span>
        </div>
        <div class="header-actions">
          <button class="btn btn-secondary" (click)="goBackToTypes()">
            <i class="icon">← </i> 返回类型管理
          </button>
          <button class="btn btn-primary" (click)="openAddModal()" [disabled]="!selectedType">
            <i class="icon">+</i> 添加字典项
          </button>
        </div>
      </div>

      <!-- 字典类型选择 -->
      <div class="type-selector">
        <label for="typeSelect">选择字典类型：</label>
        <select 
          id="typeSelect"
          [(ngModel)]="selectedTypeId" 
          (change)="onTypeChange()" 
          class="form-control"
        >
          <option value="">请选择字典类型</option>
          <option *ngFor="let type of dictTypes" [value]="type.id">
            {{ type.typeName }} ({{ type.typeCode }})
          </option>
        </select>
      </div>

      <!-- 搜索和筛选 -->
      <div class="search-bar" *ngIf="selectedType">
        <input 
          type="text" 
          placeholder="搜索编码、名称、值或描述..." 
          [(ngModel)]="searchTerm"
          (input)="filterItems()"
          class="search-input"
        />
        <select [(ngModel)]="filterStatus" (change)="filterItems()" class="filter-select">
          <option value="">所有状态</option>
          <option value="ACTIVE">启用</option>
          <option value="INACTIVE">禁用</option>
        </select>
        <select [(ngModel)]="filterParent" (change)="filterItems()" class="filter-select">
          <option value="">所有级别</option>
          <option value="root">顶级项</option>
          <option value="child">子级项</option>
        </select>
        <button class="btn btn-secondary" (click)="refreshData()">
          <i class="icon">🔄</i> 刷新
        </button>
      </div>

      <!-- 字典项表格 -->
      <div class="table-container" *ngIf="selectedType">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>编码</th>
              <th>名称</th>
              <th>值</th>
              <th>描述</th>
              <th>状态</th>
              <th>排序</th>
              <th>层级</th>
              <th>样式/图标</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let item of filteredItems" [class.inactive]="item.status === 'INACTIVE'">
              <td>{{ item.id }}</td>
              <td>{{ item.itemCode }}</td>
              <td>
                <span *ngIf="item.levelDepth && item.levelDepth > 1" class="indent">
                  {{ getIndentPrefix(item.levelDepth) }}
                </span>
                {{ item.itemName }}
              </td>
              <td>{{ item.itemValue || '-' }}</td>
              <td>{{ item.description || '-' }}</td>
              <td>
                <span class="status-badge" [class]="'status-' + item.status.toLowerCase()">
                  {{ item.status === 'ACTIVE' ? '启用' : '禁用' }}
                </span>
              </td>
              <td>{{ item.sortOrder || 0 }}</td>
              <td>{{ item.levelDepth || 1 }}</td>
              <td>
                <span *ngIf="item.cssClass" class="css-class">{{ item.cssClass }}</span>
                <span *ngIf="item.icon" class="icon-display">{{ item.icon }}</span>
                <span *ngIf="!item.cssClass && !item.icon">-</span>
              </td>
              <td>{{ formatDate(item.createdAt) }}</td>
              <td class="actions">
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
              <td colspan="11" class="no-data">暂无字典项数据</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 提示信息 -->
      <div class="empty-state" *ngIf="!selectedType">
        <h3>请选择字典类型</h3>
        <p>选择一个字典类型以查看和管理其字典项</p>
      </div>

      <!-- 模态框 -->
      <div class="modal" [class.show]="showModal" (click)="closeModal($event)">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ isEditing ? '编辑字典项' : '添加字典项' }}</h3>
            <button class="close-btn" (click)="closeModal()">&times;</button>
          </div>
          <form #itemForm="ngForm" (ngSubmit)="saveItem()">
            <div class="form-group">
              <label for="itemCode">字典项编码 *</label>
              <input 
                id="itemCode" 
                name="itemCode" 
                [(ngModel)]="currentItem.itemCode" 
                required 
                pattern="^[a-zA-Z][a-zA-Z0-9_]*$"
                class="form-control"
                #codeInput="ngModel"
                placeholder="如：active"
              />
              <div *ngIf="codeInput.invalid && codeInput.touched" class="error">
                <span *ngIf="codeInput.errors?.['required']">字典项编码是必填项</span>
                <span *ngIf="codeInput.errors?.['pattern']">编码只能包含字母、数字和下划线，且以字母开头</span>
              </div>
            </div>
            
            <div class="form-group">
              <label for="itemName">字典项名称 *</label>
              <input 
                id="itemName" 
                name="itemName" 
                [(ngModel)]="currentItem.itemName" 
                required 
                class="form-control"
                #nameInput="ngModel"
                placeholder="如：正常"
              />
              <div *ngIf="nameInput.invalid && nameInput.touched" class="error">字典项名称是必填项</div>
            </div>
            
            <div class="form-group">
              <label for="itemValue">字典项值</label>
              <input 
                id="itemValue" 
                name="itemValue" 
                [(ngModel)]="currentItem.itemValue" 
                class="form-control"
                placeholder="如：ACTIVE"
              />
            </div>
            
            <div class="form-group">
              <label for="description">描述</label>
              <textarea 
                id="description" 
                name="description" 
                [(ngModel)]="currentItem.description" 
                class="form-control"
                rows="3"
                placeholder="字典项的详细描述"
              ></textarea>
            </div>
            
            <div class="form-row">
              <div class="form-group half">
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
              
              <div class="form-group half">
                <label for="levelDepth">层级深度</label>
                <input 
                  id="levelDepth" 
                  name="levelDepth" 
                  type="number"
                  [(ngModel)]="currentItem.levelDepth" 
                  class="form-control"
                  min="1"
                  placeholder="1"
                />
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group half">
                <label for="cssClass">CSS样式类</label>
                <input 
                  id="cssClass" 
                  name="cssClass" 
                  [(ngModel)]="currentItem.cssClass" 
                  class="form-control"
                  placeholder="如：text-success"
                />
              </div>
              
              <div class="form-group half">
                <label for="icon">图标</label>
                <input 
                  id="icon" 
                  name="icon" 
                  [(ngModel)]="currentItem.icon" 
                  class="form-control"
                  placeholder="如：✓"
                />
              </div>
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
      <div class="stats" *ngIf="selectedType">
        <div class="stat-item">
          <h4>{{ getTotalItems() }}</h4>
          <p>总字典项</p>
        </div>
        <div class="stat-item">
          <h4>{{ getActiveItems() }}</h4>
          <p>启用项</p>
        </div>
        <div class="stat-item">
          <h4>{{ getInactiveItems() }}</h4>
          <p>禁用项</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dict-item-management {
      padding: 2rem;
      max-width: 1400px;
      margin: 0 auto;
    }

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
      flex-wrap: wrap;
      gap: 1rem;
    }

    .header h1 {
      margin: 0;
      color: #333;
    }

    .header-info {
      flex: 1;
      text-align: center;
    }

    .type-badge {
      background: #e3f2fd;
      color: #1976d2;
      padding: 0.5rem 1rem;
      border-radius: 20px;
      font-weight: 500;
    }

    .header-actions {
      display: flex;
      gap: 1rem;
    }

    .type-selector {
      background: white;
      padding: 1.5rem;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
      margin-bottom: 2rem;
    }

    .type-selector label {
      display: block;
      margin-bottom: 0.5rem;
      font-weight: 500;
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
      min-width: 1000px;
    }

    .data-table th,
    .data-table td {
      padding: 0.75rem;
      text-align: left;
      border-bottom: 1px solid #eee;
      font-size: 0.875rem;
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

    .indent {
      color: #999;
      margin-right: 0.25rem;
    }

    .status-badge {
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      font-size: 0.75rem;
      font-weight: 500;
    }

    .status-active { background: #e8f5e8; color: #2e7d32; }
    .status-inactive { background: #ffebee; color: #c62828; }

    .css-class {
      background: #f3e5f5;
      color: #7b1fa2;
      padding: 0.125rem 0.25rem;
      border-radius: 3px;
      font-size: 0.75rem;
      margin-right: 0.25rem;
    }

    .icon-display {
      font-size: 1.2rem;
    }

    .actions {
      display: flex;
      gap: 0.25rem;
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

    .empty-state {
      text-align: center;
      padding: 4rem 2rem;
      color: #666;
    }

    .empty-state h3 {
      margin-bottom: 1rem;
      color: #333;
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
      max-width: 700px;
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

    .form-row {
      display: flex;
      gap: 1rem;
      margin: 1rem 1.5rem;
    }

    .form-group.half {
      flex: 1;
      margin: 0;
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
      .dict-item-management {
        padding: 1rem;
      }

      .header {
        flex-direction: column;
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

      .form-row {
        flex-direction: column;
        gap: 0;
      }

      .form-group.half {
        margin: 1rem 0;
      }

      .actions {
        flex-direction: column;
      }
    }
  `]
})
export class DictItemManagementComponent implements OnInit {
  items: DictItem[] = [];
  filteredItems: DictItem[] = [];
  dictTypes: DictType[] = [];
  selectedType: DictType | null = null;
  selectedTypeId: number | string = '';
  
  searchTerm = '';
  filterStatus = '';
  filterParent = '';

  showModal = false;
  isEditing = false;
  saving = false;
  currentItem: Partial<DictItem> = {};

  private typeApiUrl = 'http://localhost:8080/api/dict-types';
  private itemApiUrl = 'http://localhost:8080/api/dict-items';

  constructor(
    private http: HttpClient, 
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.loadDictTypes();
    this.route.queryParams.subscribe(params => {
      if (params['typeId']) {
        this.selectedTypeId = +params['typeId'];
        setTimeout(() => this.onTypeChange(), 100);
      }
    });
  }

  loadDictTypes() {
    this.http.get<any>(`${this.typeApiUrl}/active`).subscribe({
      next: (response) => {
        if (response.success) {
          this.dictTypes = response.data || [];
        }
      },
      error: (error) => {
        console.error('加载字典类型失败:', error);
      }
    });
  }

  onTypeChange() {
    if (this.selectedTypeId) {
      this.selectedType = this.dictTypes.find(t => t.id === +this.selectedTypeId) || null;
      this.loadItems();
    } else {
      this.selectedType = null;
      this.items = [];
      this.filteredItems = [];
    }
  }

  loadItems() {
    if (!this.selectedType) return;
    
    this.http.get<any>(`${this.itemApiUrl}/type/${this.selectedType.id}`).subscribe({
      next: (response) => {
        if (response.success) {
          this.items = response.data || [];
          this.filterItems();
        }
      },
      error: (error) => {
        console.error('加载字典项失败:', error);
        alert('加载字典项失败，请重试');
      }
    });
  }

  refreshData() {
    this.loadItems();
  }

  filterItems() {
    this.filteredItems = this.items.filter(item => {
      const matchesSearch = !this.searchTerm || 
        item.itemCode.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        item.itemName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        (item.itemValue && item.itemValue.toLowerCase().includes(this.searchTerm.toLowerCase())) ||
        (item.description && item.description.toLowerCase().includes(this.searchTerm.toLowerCase()));
      
      const matchesStatus = !this.filterStatus || item.status === this.filterStatus;
      
      const matchesParent = !this.filterParent || 
        (this.filterParent === 'root' && (!item.parentId || item.levelDepth === 1)) ||
        (this.filterParent === 'child' && item.parentId && item.levelDepth && item.levelDepth > 1);
      
      return matchesSearch && matchesStatus && matchesParent;
    });
  }

  openAddModal() {
    if (!this.selectedType) return;
    
    this.isEditing = false;
    this.currentItem = {
      typeId: this.selectedType.id,
      status: 'ACTIVE',
      sortOrder: 0,
      levelDepth: 1
    };
    this.showModal = true;
  }

  editItem(item: DictItem) {
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
    const method = this.isEditing ? 'put' : 'post';
    const url = this.isEditing ? `${this.itemApiUrl}/${this.currentItem.id}` : this.itemApiUrl;

    this.http[method]<any>(url, this.currentItem).subscribe({
      next: (response) => {
        this.saving = false;
        if (response.success) {
          alert(this.isEditing ? '更新成功' : '添加成功');
          this.closeModal();
          this.loadItems();
        } else {
          alert(response.message || '操作失败');
        }
      },
      error: (error) => {
        this.saving = false;
        console.error('保存失败:', error);
        alert(error.error?.message || '保存失败，请重试');
      }
    });
  }

  toggleStatus(item: DictItem) {
    const newStatus = item.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    const action = newStatus === 'ACTIVE' ? '启用' : '禁用';
    
    if (confirm(`确定要${action}字典项"${item.itemName}"吗？`)) {
      this.http.put<any>(`${this.itemApiUrl}/${item.id}/status`, null, {
        params: { status: newStatus }
      }).subscribe({
        next: (response) => {
          if (response.success) {
            item.status = newStatus;
            alert(`${action}成功`);
            this.filterItems();
          } else {
            alert(response.message || `${action}失败`);
          }
        },
        error: (error) => {
          console.error(`${action}失败:`, error);
          alert(`${action}失败，请重试`);
        }
      });
    }
  }

  deleteItem(item: DictItem) {
    if (confirm(`确定要删除字典项"${item.itemName}"吗？删除后不可恢复。`)) {
      this.http.delete<any>(`${this.itemApiUrl}/${item.id}`).subscribe({
        next: (response) => {
          if (response.success) {
            alert('删除成功');
            this.loadItems();
          } else {
            alert(response.message || '删除失败');
          }
        },
        error: (error) => {
          console.error('删除失败:', error);
          alert(error.error?.message || '删除失败，请重试');
        }
      });
    }
  }

  goBackToTypes() {
    this.router.navigate(['/dict-types']);
  }

  getIndentPrefix(level: number): string {
    return '└ '.repeat(Math.max(0, level - 1));
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