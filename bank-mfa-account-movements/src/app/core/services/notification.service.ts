import { Injectable, signal } from '@angular/core';

export interface Notification {
  message: string;
  type: 'success' | 'error' | 'info';
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  readonly notification = signal<Notification | null>(null);

  success(message: string): void {
    this.show({ message, type: 'success' });
  }

  error(message: string): void {
    this.show({ message, type: 'error' });
  }

  info(message: string): void {
    this.show({ message, type: 'info' });
  }

  clear(): void {
    this.notification.set(null);
  }

  private show(notification: Notification): void {
    this.notification.set(notification);
    setTimeout(() => this.clear(), 5000);
  }
}
