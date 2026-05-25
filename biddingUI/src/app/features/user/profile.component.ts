import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/auth/auth.service';

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './profile.component.html',
    styles: [`
        :host {
            display: block;
        }

        .profile-hero {
            background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
            padding: 3rem 0 5rem;
            position: relative;
            overflow: hidden;
        }

        .profile-hero::before {
            content: '';
            position: absolute;
            inset: 0;
            background: radial-gradient(ellipse at 70% 50%, rgba(102, 126, 234, 0.15) 0%, transparent 60%);
        }

        .avatar-ring {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea, #764ba2);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 2.5rem;
            font-weight: 700;
            color: #fff;
            box-shadow: 0 0 0 4px rgba(102,126,234,0.3), 0 8px 32px rgba(0,0,0,0.4);
            letter-spacing: -1px;
        }

        .profile-card {
            border-radius: 20px;
            border: none;
            box-shadow: 0 4px 24px rgba(0,0,0,0.08);
            margin-top: -3rem;
            position: relative;
        }

        .stat-pill {
            background: #f8f9fa;
            border-radius: 12px;
            padding: 1rem 1.5rem;
            text-align: center;
            flex: 1;
        }

        .stat-pill .value {
            font-size: 1.6rem;
            font-weight: 700;
            color: #1a1a2e;
            line-height: 1;
        }

        .stat-pill .label {
            font-size: 0.75rem;
            color: #888;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            margin-top: 4px;
        }

        .info-row {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 0.85rem 0;
            border-bottom: 1px solid #f0f0f0;
        }

        .info-row:last-child {
            border-bottom: none;
        }

        .info-icon {
            width: 36px;
            height: 36px;
            border-radius: 10px;
            background: linear-gradient(135deg, #667eea20, #764ba220);
            color: #667eea;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 0.95rem;
            flex-shrink: 0;
        }

        .info-label {
            font-size: 0.72rem;
            text-transform: uppercase;
            letter-spacing: 0.06em;
            color: #aaa;
            line-height: 1;
            margin-bottom: 2px;
        }

        .info-value {
            font-size: 0.95rem;
            font-weight: 500;
            color: #222;
        }

        .role-badge {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.78rem;
            font-weight: 600;
            letter-spacing: 0.03em;
        }

        .role-seller {
            background: linear-gradient(135deg, #667eea20, #764ba220);
            color: #667eea;
            border: 1px solid #667eea40;
        }

        .role-bidder {
            background: linear-gradient(135deg, #11998e20, #38ef7d20);
            color: #11998e;
            border: 1px solid #11998e40;
        }
    `],
})
export class ProfileComponent implements OnInit {
    username: string | null = null;
    userId: string | null = null;
    roles: string[] = [];
    initials: string = '?';

    constructor(private authService: AuthService) {}

    ngOnInit(): void {
        this.username = this.authService.getUsername();
        this.userId = this.authService.getUserId();
        this.roles = this.authService.getRoles();
        if (this.username) {
            this.initials = this.username
                .split(/[\s._-]/)
                .slice(0, 2)
                .map(w => w[0]?.toUpperCase() ?? '')
                .join('');
        }
    }

    get isSeller(): boolean {
        return this.roles.includes('ROLE_SELLER');
    }

    get displayRoles(): string[] {
        return this.roles.map(r => r.replace('ROLE_', ''));
    }

    get shortUserId(): string {
        return this.userId ? this.userId.split('-')[0].toUpperCase() : '—';
    }
}
