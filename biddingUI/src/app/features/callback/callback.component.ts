import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { LoggerService } from '../../core/services/logger.service';

@Component({
    selector: 'app-callback',
    standalone: true,
    template: `
        <div class="callback-container">
            <div class="spinner-border" role="status">
                <span class="visually-hidden">Logging in...</span>
            </div>
            <p class="mt-3">Logging in...</p>
        </div>
    `,
    styles: [`
        .callback-container {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            height: 100vh;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
    `]
})
export class CallbackComponent implements OnInit {
    constructor(
        private authService: AuthService,
        private route: ActivatedRoute,
        private router: Router,
        private toastService: ToastService,
        private logger: LoggerService
    ) {}

    ngOnInit(): void {
        this.route.queryParams.subscribe(params => {
            const code = params['code'];
            const error = params['error'];

            if (error) {
                this.logger.error('OAuth error:', error);
                this.toastService.error('Login failed. Please try again.');
                this.router.navigate(['/login']);
                return;
            }

            if (!code) {
                this.logger.error('No authorization code in callback');
                this.toastService.error('Login failed. No authorization code.');
                this.router.navigate(['/login']);
                return;
            }

            this.authService.handleAuthCallback(code).subscribe(
                () => {
                    this.logger.info('Login successful');
                    this.toastService.success('Login successful!');

                    this.router.navigate(['/auctions']);
                },
                (error) => {
                    this.logger.error('Failed to exchange code for token:', error);
                    this.toastService.error('Failed to login. Please try again.');
                    this.router.navigate(['/login']);
                }
            );
        });
    }
}
