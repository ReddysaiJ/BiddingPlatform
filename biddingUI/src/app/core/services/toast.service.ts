import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Toast {
    id: string;
    message: string;
    type: 'success' | 'error' | 'warning' | 'info';
    duration?: number;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
    private toastsSubject = new BehaviorSubject<Toast[]>([]);
    public toasts$: Observable<Toast[]> = this.toastsSubject.asObservable();

    show(message: string, type: 'success' | 'error' | 'warning' | 'info' = 'info',
        duration = 5000): void {
        const id = Math.random().toString(36).substring(2, 11);
        const toast: Toast = { id, message, type, duration };

        const current = this.toastsSubject.value;
        this.toastsSubject.next([...current, toast]);

        if (duration > 0) {
            setTimeout(() => this.remove(id), duration);
        }
    }

    error(message: string, duration = 5000): void {
        this.show(message, 'error', duration);
    }

    success(message: string, duration = 3000): void {
        this.show(message, 'success', duration);
    }

    warning(message: string, duration = 5000): void {
        this.show(message, 'warning', duration);
    }

    info(message: string, duration = 5000): void {
        this.show(message, 'info', duration);
    }

    remove(id: string): void {
        const current = this.toastsSubject.value;
        this.toastsSubject.next(current.filter((t) => t.id !== id));
    }

    clear(): void {
        this.toastsSubject.next([]);
    }
}
