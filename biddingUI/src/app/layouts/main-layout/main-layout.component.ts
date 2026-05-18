import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from '../../core/auth/auth.service';

@Component({
    selector: 'app-main-layout',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './main-layout.component.html',
    styles: [`
        .navbar {
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            padding: 0.5rem 0;
        }

        .navbar-brand {
            font-size: 24px;
            font-weight: bold;
            margin-right: 2rem;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .navbar-brand i {
            font-size: 28px;
        }

        .nav-link {
            display: flex;
            align-items: center;
            gap: 6px;
            transition: color 0.3s ease;
        }

        .nav-link:hover {
            color: #667eea !important;
        }

        .nav-link.active {
            color: #667eea !important;
            border-bottom: 3px solid #667eea;
            padding-bottom: 8px;
        }

        .dropdown-menu {
            animation: slideDown 0.2s ease-out;
        }

        @keyframes slideDown {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .main-content {
            min-height: calc(100vh - 200px);
            padding-top: 2rem;
        }

        .footer {
            border-top: 1px solid #333;
            margin-top: auto;
        }

        .footer h5 {
            font-weight: 600;
            margin-bottom: 1rem;
        }

        .footer a:hover {
            color: #667eea !important;
        }

        /* Mobile responsive */
        @media (max-width: 991px) {
            .navbar-brand {
                margin-right: 1rem;
                font-size: 20px;
            }

            .navbar-collapse {
                border-top: 1px solid #444;
                margin-top: 1rem;
                padding-top: 1rem;
            }

            .nav-link.active {
                border-bottom: none;
                color: #667eea !important;
            }
        }

        /* Dark mode */
        @media (prefers-color-scheme: dark) {
            .navbar {
                background-color: #1a1a1a !important;
                border-bottom: 1px solid #333;
            }

            .dropdown-menu {
                background-color: #2a2a2a;
                border-color: #444;
            }

            .dropdown-item {
                color: #ccc;
            }

            .dropdown-item:hover,
            .dropdown-item.active {
                background-color: #333;
                color: #667eea;
            }

            .dropdown-divider {
                border-color: #444;
            }
        }
    `]
})
export class MainLayoutComponent implements OnInit, OnDestroy {
    username: string | null = null;
    isSeller = false;
    navbarOpen = false;
    userMenuOpen = false;
    private destroy$ = new Subject<void>();

    constructor(
        private authService: AuthService,
        private router: Router
    ) {}

    ngOnInit(): void {
        // Get username from token
        this.username = this.authService.getUsername();
        // Check if user has ROLE_SELLER
        this.isSeller = this.authService.hasRole('ROLE_SELLER');
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    toggleNavbar(): void {
        this.navbarOpen = !this.navbarOpen;
        this.userMenuOpen = false;
    }

    toggleUserMenu(): void {
        this.userMenuOpen = !this.userMenuOpen;
    }

    viewProfile(): void {
        // TODO: Navigate to profile page
        console.log('Navigate to profile');
        this.userMenuOpen = false;
    }

    viewSettings(): void {
        // TODO: Navigate to settings page
        console.log('Navigate to settings');
        this.userMenuOpen = false;
    }

    logout(): void {
        this.authService.logout();
        this.router.navigate(['/login']);
        this.userMenuOpen = false;
    }
}
