import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

  alert(message: string): void {
    if (isPlatformBrowser(this.platformId)) {
      alert(message);
    } else {
      // In server environment, just log the message
      console.log('Alert message:', message);
    }
  }

  confirm(message: string): boolean {
    if (isPlatformBrowser(this.platformId)) {
      return confirm(message);
    } else {
      // In server environment, return false as default
      console.log('Confirm message:', message);
      return false;
    }
  }
} 