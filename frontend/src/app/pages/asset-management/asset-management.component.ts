import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { NotificationService } from '../../services/notification.service';

export interface Asset {
  id: number;
  name: string;
  assetNumber: string;
  type: string;
  category: string;
  description: string;
  brand: string;
  model: string;
  serialNumber: string;
  purchasePrice: number;
  purchaseDate: string;
  supplier: string;
  location: string;
  department: string;
  responsiblePerson: string;
  status: string;
  warrantyExpiry: string;
  notes: string;
  userId: number | null;
  createdAt: string;
  updatedAt: string;
}

export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  nickname: string;
}

@Component({
  selector: 'app-asset-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container-fluid">
      <div class="row">
        <div class="col-md-12">
          <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
              <h3>资产管理</h3>
              <div>
                <button class="btn btn-primary me-2" (click)="showCreateForm = true">
                  <i class="fas fa-plus"></i> 新增资产
                </button>
                <button class="btn btn-info" (click)="loadUnassignedAssets()">
                  <i class="fas fa-list"></i> 未分配资产
                </button>
              </div>
            </div>
            
            <div class="card-body">
              <!-- 标签页 -->
              <ul class="nav nav-tabs mb-3">
                <li class="nav-item">
                  <a class="nav-link" 
                     [class.active]="activeTab === 'all'" 
                     (click)="switchTab('all')">
                    所有资产
                  </a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" 
                     [class.active]="activeTab === 'unassigned'" 
                     (click)="switchTab('unassigned')">
                    未分配资产
                  </a>
                </li>
              </ul>
              
              <!-- 新增资产表单 -->
              <div *ngIf="showCreateForm" class="card mb-3">
                <div class="card-header">
                  <h5>新增资产</h5>
                </div>
                <div class="card-body">
                  <form (ngSubmit)="createAsset()">
                    <div class="row">
                      <div class="col-md-6">
                        <div class="mb-3">
                          <label class="form-label">资产名称 *</label>
                          <input type="text" class="form-control" [(ngModel)]="newAsset.name" name="name" required>
                        </div>
                      </div>
                      <div class="col-md-6">
                        <div class="mb-3">
                          <label class="form-label">资产编号 *</label>
                          <input type="text" class="form-control" [(ngModel)]="newAsset.assetNumber" name="assetNumber" required>
                        </div>
                      </div>
                      <div class="col-md-6">
                        <div class="mb-3">
                          <label class="form-label">类型</label>
                          <input type="text" class="form-control" [(ngModel)]="newAsset.type" name="type">
                        </div>
                      </div>
                      <div class="col-md-6">
                        <div class="mb-3">
                          <label class="form-label">分类</label>
                          <input type="text" class="form-control" [(ngModel)]="newAsset.category" name="category">
                        </div>
                      </div>
                      <div class="col-md-6">
                        <div class="mb-3">
                          <label class="form-label">品牌</label>
                          <input type="text" class="form-control" [(ngModel)]="newAsset.brand" name="brand">
                        </div>
                      </div>
                      <div class="col-md-6">
                        <div class="mb-3">
                          <label class="form-label">型号</label>
                          <input type="text" class="form-control" [(ngModel)]="newAsset.model" name="model">
                        </div>
                      </div>
                      <div class="col-md-12">
                        <div class="mb-3">
                          <label class="form-label">描述</label>
                          <textarea class="form-control" [(ngModel)]="newAsset.description" name="description" rows="3"></textarea>
                        </div>
                      </div>
                    </div>
                    <div class="d-flex gap-2">
                      <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> 保存
                      </button>
                      <button type="button" class="btn btn-secondary" (click)="cancelCreate()">
                        <i class="fas fa-times"></i> 取消
                      </button>
                    </div>
                  </form>
                </div>
              </div>
              
              <!-- 资产列表 -->
              <div class="table-responsive">
                <table class="table table-striped">
                  <thead>
                    <tr>
                      <th>资产编号</th>
                      <th>名称</th>
                      <th>类型</th>
                      <th>品牌</th>
                      <th>型号</th>
                      <th>状态</th>
                      <th>绑定用户</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr *ngFor="let asset of displayAssets">
                      <td>{{ asset.assetNumber }}</td>
                      <td>{{ asset.name }}</td>
                      <td>{{ asset.type }}</td>
                      <td>{{ asset.brand }}</td>
                      <td>{{ asset.model }}</td>
                      <td>
                        <span class="badge" [class]="getStatusClass(asset.status)">
                          {{ getStatusText(asset.status) }}
                        </span>
                      </td>
                      <td>
                        <span *ngIf="asset.userId">
                          {{ getUserName(asset.userId) }}
                        </span>
                        <span *ngIf="!asset.userId" class="text-muted">未分配</span>
                      </td>
                      <td>
                        <div class="btn-group">
                          <button class="btn btn-sm btn-outline-primary" (click)="editAsset(asset)">
                            <i class="fas fa-edit"></i> 编辑
                          </button>
                          <button *ngIf="!asset.userId" class="btn btn-sm btn-outline-success" (click)="showBindDialog(asset)">
                            <i class="fas fa-link"></i> 绑定
                          </button>
                          <button *ngIf="asset.userId" class="btn btn-sm btn-outline-warning" (click)="unbindAsset(asset)">
                            <i class="fas fa-unlink"></i> 解绑
                          </button>
                          <button class="btn btn-sm btn-outline-danger" (click)="deleteAsset(asset)">
                            <i class="fas fa-trash"></i> 删除
                          </button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 绑定用户弹窗 -->
    <div *ngIf="showBindModal" class="modal d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">绑定资产到用户</h5>
            <button type="button" class="btn-close" (click)="hideBindDialog()"></button>
          </div>
          <div class="modal-body">
            <p>资产: {{ selectedAsset?.name }} ({{ selectedAsset?.assetNumber }})</p>
            <div class="mb-3">
              <label class="form-label">选择用户</label>
              <select class="form-select" [(ngModel)]="selectedUserId">
                <option value="">请选择用户</option>
                <option *ngFor="let user of users" [value]="user.id">
                  {{ user.username }} ({{ user.firstName }} {{ user.lastName }})
                </option>
              </select>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="hideBindDialog()">取消</button>
            <button type="button" class="btn btn-primary" (click)="bindAsset()" [disabled]="!selectedUserId">绑定</button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .container-fluid {
      padding: 20px;
    }
    
    .card {
      border: none;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    
    .card-header {
      background-color: #f8f9fa;
      border-bottom: 1px solid #dee2e6;
    }
    
    .table {
      margin-bottom: 0;
    }
    
    .badge {
      font-size: 0.75em;
    }
    
    .modal {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      z-index: 1050;
    }
    
    .btn-group .btn {
      margin-right: 5px;
    }
    
    .nav-tabs .nav-link {
      cursor: pointer;
    }
  `]
})
export class AssetManagementComponent implements OnInit {
  
  assets: Asset[] = [];
  unassignedAssets: Asset[] = [];
  users: User[] = [];
  displayAssets: Asset[] = [];
  activeTab = 'all';
  
  showCreateForm = false;
  showBindModal = false;
  selectedAsset: Asset | null = null;
  selectedUserId: number | null = null;
  
  newAsset: Partial<Asset> = {
    name: '',
    assetNumber: '',
    type: '',
    category: '',
    description: '',
    brand: '',
    model: '',
    status: 'ACTIVE'
  };
  
  constructor(
    private http: HttpClient, 
    private notification: NotificationService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}
  
  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.loadAllAssets();
      this.loadUsers();
    }
  }
  
  switchTab(tab: string) {
    this.activeTab = tab;
    if (tab === 'all') {
      this.displayAssets = this.assets;
    } else if (tab === 'unassigned') {
      this.displayAssets = this.unassignedAssets;
    }
  }
  
  loadAllAssets() {
    this.http.get<Asset[]>('/api/assets').subscribe({
      next: (data) => {
        this.assets = data;
        if (this.activeTab === 'all') {
          this.displayAssets = this.assets;
        }
      },
      error: (error) => {
        console.error('加载资产失败:', error);
        this.notification.alert('加载资产失败');
      }
    });
  }
  
  loadUnassignedAssets() {
    this.http.get<Asset[]>('/api/assets/unassigned').subscribe({
      next: (data) => {
        this.unassignedAssets = data;
        this.activeTab = 'unassigned';
        this.displayAssets = this.unassignedAssets;
      },
      error: (error) => {
        console.error('加载未分配资产失败:', error);
        this.notification.alert('加载未分配资产失败');
      }
    });
  }
  
  loadUsers() {
    this.http.get<User[]>('/api/users').subscribe({
      next: (data) => {
        this.users = data;
      },
      error: (error) => {
        console.error('加载用户失败:', error);
      }
    });
  }
  
  createAsset() {
    if (!this.newAsset.name || !this.newAsset.assetNumber) {
      this.notification.alert('请填写必填字段');
      return;
    }
    
    this.http.post<Asset>('/api/assets', this.newAsset).subscribe({
      next: (data) => {
        this.loadAllAssets();
        this.cancelCreate();
        this.notification.alert('资产创建成功');
      },
      error: (error) => {
        console.error('创建资产失败:', error);
        this.notification.alert('创建资产失败');
      }
    });
  }
  
  cancelCreate() {
    this.showCreateForm = false;
    this.newAsset = {
      name: '',
      assetNumber: '',
      type: '',
      category: '',
      description: '',
      brand: '',
      model: '',
      status: 'ACTIVE'
    };
  }
  
  editAsset(asset: Asset) {
    // 编辑功能的实现
    this.notification.alert('编辑功能待实现');
  }
  
  showBindDialog(asset: Asset) {
    this.selectedAsset = asset;
    this.selectedUserId = null;
    this.showBindModal = true;
  }
  
  hideBindDialog() {
    this.showBindModal = false;
    this.selectedAsset = null;
    this.selectedUserId = null;
  }
  
  bindAsset() {
    if (!this.selectedAsset || !this.selectedUserId) {
      return;
    }
    
    this.http.post<string>(`/api/assets/${this.selectedAsset.id}/bind/${this.selectedUserId}`, {}).subscribe({
      next: (response) => {
        this.loadAllAssets();
        this.loadUnassignedAssets();
        this.hideBindDialog();
        this.notification.alert('资产绑定成功');
      },
      error: (error) => {
        console.error('绑定资产失败:', error);
        this.notification.alert('绑定资产失败: ' + (error.error || error.message));
      }
    });
  }
  
  unbindAsset(asset: Asset) {
    if (!this.notification.confirm('确定要解绑该资产吗？')) {
      return;
    }
    
    this.http.post<string>(`/api/assets/${asset.id}/unbind`, {}).subscribe({
      next: (response) => {
        this.loadAllAssets();
        this.loadUnassignedAssets();
        this.notification.alert('资产解绑成功');
      },
      error: (error) => {
        console.error('解绑资产失败:', error);
        this.notification.alert('解绑资产失败: ' + (error.error || error.message));
      }
    });
  }
  
  deleteAsset(asset: Asset) {
    if (!this.notification.confirm('确定要删除该资产吗？')) {
      return;
    }
    
    this.http.delete<string>(`/api/assets/${asset.id}`).subscribe({
      next: (response) => {
        this.loadAllAssets();
        this.loadUnassignedAssets();
        this.notification.alert('资产删除成功');
      },
      error: (error) => {
        console.error('删除资产失败:', error);
        this.notification.alert('删除资产失败: ' + (error.error || error.message));
      }
    });
  }
  
  getStatusClass(status: string): string {
    switch (status) {
      case 'ACTIVE': return 'bg-success';
      case 'MAINTENANCE': return 'bg-warning text-dark';
      case 'DEPRECATED': return 'bg-secondary';
      case 'DISPOSED': return 'bg-danger';
      default: return 'bg-secondary';
    }
  }
  
  getStatusText(status: string): string {
    switch (status) {
      case 'ACTIVE': return '正常';
      case 'MAINTENANCE': return '维护中';
      case 'DEPRECATED': return '已废弃';
      case 'DISPOSED': return '已处置';
      default: return '未知';
    }
  }
  
  getUserName(userId: number): string {
    const user = this.users.find(u => u.id === userId);
    return user ? `${user.username} (${user.firstName} ${user.lastName})` : '未知用户';
  }
}