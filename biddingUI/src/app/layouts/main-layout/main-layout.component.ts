import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
    selector: 'app-main-layout',
    standalone: true,
    imports: [RouterLink, RouterOutlet],
    templateUrl: './main-layout.component.html',
    styles: [`
        .layout {
            min-height: 100vh;
            background-color: #f5f7fa;
        }

        .content {
            padding-top: 24px;
        }
    `],
})
export class MainLayoutComponent {
    constructor(public authService: AuthService) {}

    logout(): void {
        this.authService.logout();
    }
}
