import { Component, OnInit, signal } from '@angular/core';

import { RouterOutlet } from '@angular/router';

import { ToastContainerComponent } from './shared/components/toast.component';

import { AuthService } from './core/auth/auth.service';

@Component({
    selector: 'app-root',
    imports: [RouterOutlet, ToastContainerComponent],
    templateUrl: './app.html',
    styleUrl: './app.css',
})
export class App implements OnInit {
    protected readonly title = signal('biddingUI');

    constructor(private authService: AuthService) {}

    ngOnInit(): void {
        this.authService.checkAuthCallback();
    }
}
