import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/auth/auth.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule],
    template: `
        <div class="login-container">
            <div class="login-card">
                <div class="login-header">
                    <h1>Auction Bidding</h1>

                    <p class="subtitle">Real-time auction platform</p>
                </div>

                <div class="login-content">
                    <h2>Welcome Back</h2>

                    <p class="login-description">Sign in to participate in realtime auctions.</p>

                    <button class="btn btn-primary btn-lg w-100" (click)="login()" type="button">
                        Login with Keycloak
                    </button>
                </div>

                <div class="login-footer">
                    <p class="text-muted small">
                        Authentication powered by

                        <strong> Keycloak </strong>
                    </p>

                    <p class="text-muted small">Secure OAuth2 + PKCE Login</p>
                </div>
            </div>

            <div class="features-section">
                <div class="feature">
                    <h3>Real-time Updates</h3>

                    <p>Live bid updates as auctions progress</p>
                </div>

                <div class="feature">
                    <h3>Secure Authentication</h3>

                    <p>OAuth2 + PKCE secured authentication flow</p>
                </div>

                <div class="feature">
                    <h3>Live Auctions</h3>

                    <p>Create and participate in realtime bidding</p>
                </div>
            </div>
        </div>
    `,
    styles: [
        `
            .login-container {
                min-height: 100vh;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                padding: 20px;
            }

            .login-card {
                background: white;
                border-radius: 12px;
                box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
                width: 100%;
                max-width: 400px;
                overflow: hidden;
                animation: slideUp 0.5s ease-out;
            }

            @keyframes slideUp {
                from {
                    opacity: 0;
                    transform: translateY(30px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            .login-header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 40px 20px;
                text-align: center;
            }

            .login-header h1 {
                margin: 0;
                font-size: 32px;
                font-weight: bold;
            }

            .subtitle {
                margin: 10px 0 0 0;
                font-size: 14px;
                opacity: 0.9;
            }

            .login-content {
                padding: 40px 30px;
            }

            .login-content h2 {
                font-size: 24px;
                margin: 0 0 10px 0;
                color: #333;
            }

            .login-description {
                color: #666;
                font-size: 14px;
                margin: 0 0 30px 0;
            }

            .btn {
                border-radius: 8px;
                font-weight: 500;
                transition: all 0.3s ease;
                margin-bottom: 12px;
            }

            .btn-primary {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                border: none;
            }

            .btn-primary:hover:not(:disabled) {
                transform: translateY(-2px);
                box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
            }

            .btn-primary:disabled {
                opacity: 0.6;
                cursor: not-allowed;
            }

            .btn-outline-secondary {
                color: #666;
                border-color: #ddd;
            }

            .btn-outline-secondary:hover:not(:disabled) {
                background: #f8f9fa;
                border-color: #666;
            }

            .btn-outline-secondary:disabled {
                opacity: 0.5;
                cursor: not-allowed;
            }

            .btn i {
                margin-right: 8px;
            }

            .divider {
                position: relative;
                margin: 25px 0;
                text-align: center;
            }

            .divider::before {
                content: '';
                position: absolute;
                top: 50%;
                left: 0;
                right: 0;
                height: 1px;
                background: #e0e0e0;
            }

            .divider span {
                position: relative;
                background: white;
                padding: 0 10px;
                color: #999;
                font-size: 12px;
            }

            .login-footer {
                border-top: 1px solid #f0f0f0;
                padding: 20px 30px 30px;
                text-align: center;
            }

            .login-footer p {
                margin: 8px 0;
            }

            .login-footer a {
                color: #667eea;
                text-decoration: none;
                font-weight: 500;
            }

            .login-footer a:hover {
                text-decoration: underline;
            }

            .features-section {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                gap: 30px;
                margin-top: 60px;
                color: white;
                max-width: 1000px;
            }

            .feature {
                text-align: center;
                animation: fadeIn 0.6s ease-out backwards;
            }

            .feature:nth-child(1) {
                animation-delay: 0.1s;
            }
            .feature:nth-child(2) {
                animation-delay: 0.2s;
            }
            .feature:nth-child(3) {
                animation-delay: 0.3s;
            }

            @keyframes fadeIn {
                from {
                    opacity: 0;
                    transform: translateY(20px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            .feature i {
                font-size: 48px;
                margin-bottom: 15px;
                display: block;
            }

            .feature h3 {
                font-size: 18px;
                margin: 0 0 10px 0;
                font-weight: 600;
            }

            .feature p {
                font-size: 14px;
                margin: 0;
                opacity: 0.9;
            }

            /* Mobile responsive */
            @media (max-width: 768px) {
                .login-container {
                    padding: 15px;
                }

                .login-card {
                    max-width: 100%;
                }

                .login-header {
                    padding: 30px 20px;
                }

                .login-header h1 {
                    font-size: 24px;
                }

                .login-content {
                    padding: 30px 20px;
                }

                .features-section {
                    margin-top: 40px;
                    gap: 20px;
                }

                .feature i {
                    font-size: 36px;
                }

                .feature h3 {
                    font-size: 16px;
                }
            }

            /* Dark mode support */
            @media (prefers-color-scheme: dark) {
                .login-card {
                    background: #1e1e1e;
                }

                .login-content h2 {
                    color: #fff;
                }

                .login-description {
                    color: #aaa;
                }

                .login-footer {
                    border-top-color: #333;
                }

                .divider::before {
                    background: #444;
                }

                .divider span {
                    background: #1e1e1e;
                    color: #666;
                }
            }
        `,
    ],
})
export class LoginComponent {
    constructor(private authService: AuthService) {}

    login(): void {
        this.authService.login();
    }

    loginAsGuest(): void {
        console.log('Guest login not yet implemented');
    }
}
