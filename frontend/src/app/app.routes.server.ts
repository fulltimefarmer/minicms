// 此文件为服务端渲染的路由配置，定义了SSR下的路由渲染模式。
import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: '**',
    renderMode: RenderMode.Prerender
  }
];
