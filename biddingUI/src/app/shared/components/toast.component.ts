import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ToastService, Toast } from '../../core/services/toast.service';

@Component({
    selector: 'app-toast-container',
    standalone: true,
    imports: [CommonModule],
    template: `
        <div class="toast-container position-fixed top-0 end-0 p-3">
            <div
                *ngFor="let toast of toasts"
                class="toast show"
                role="alert"
                [ngClass]="'toast-' + toast.type"
                aria-live="assertive"
                aria-atomic="true"
            >
                <div class="toast-header">
                    <strong class="me-auto">
                        {{ getTitle(toast.type) }}
                    </strong>
                    <button
                        type="button"
                        class="btn-close"
                        (click)="toastService.remove(toast.id)"
                        aria-label="Close"
                    ></button>
                </div>
                <div class="toast-body">
                    {{ toast.message }}
                </div>
            </div>
        </div>
    `,
    styles: [`
        .toast-error {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
        }
        .toast-error .toast-header {
            background-color: #f5c6cb;
            color: #721c24;
        }
        .toast-error .toast-body {
            color: #721c24;
        }

        .toast-success {
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
        }
        .toast-success .toast-header {
            background-color: #c3e6cb;
            color: #155724;
        }
        .toast-success .toast-body {
            color: #155724;
        }

        .toast-warning {
            background-color: #fff3cd;
            border: 1px solid #ffeeba;
        }
        .toast-warning .toast-header {
            background-color: #ffeeba;
            color: #856404;
        }
        .toast-warning .toast-body {
            color: #856404;
        }

        .toast-info {
            background-color: #d1ecf1;
            border: 1px solid #bee5eb;
        }
        .toast-info .toast-header {
            background-color: #bee5eb;
            color: #0c5460;
        }
        .toast-info .toast-body {
            color: #0c5460;
        }
    `]
})
export class ToastContainerComponent implements OnInit, OnDestroy {
    toasts: Toast[] = [];
    private destroy$ = new Subject<void>();

    constructor(public toastService: ToastService) {}

    ngOnInit(): void {
        this.toastService.toasts$
            .pipe(takeUntil(this.destroy$))
            .subscribe(toasts => {
                this.toasts = toasts;
            });
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    getTitle(type: string): string {
        const titles: Record<string, string> = {
            'error': 'Error',
            'success': 'Success',
            'warning': 'Warning',
            'info': 'Info'
        };
        return titles[type] || 'Notification';
    }
}
