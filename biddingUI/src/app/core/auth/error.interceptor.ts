import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { ToastService } from '../services/toast.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    constructor(
        private authService: AuthService,
        private toastService: ToastService,
        private router: Router
    ) {}

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (request.url.includes('api.cloudinary.com'))
            return next.handle(request);

        return next.handle(request).pipe(
            catchError((error: HttpErrorResponse) => {
                if (error.status === 401) {
                    return this.handle401(request, next);
                }

                if (error.status === 403) {
                    this.toastService.error('Access denied');
                    return throwError(() => error);
                }

                if (error.status === 400 || error.status === 422) {
                    const detail = error.error?.detail || 'Invalid request';
                    this.toastService.error(detail);
                    return throwError(() => error);
                }

                if (error.status >= 500) {
                    this.toastService.error('Server error. Please try again later.');
                    return throwError(() => error);
                }

                this.toastService.error(error.error?.message || 'An error occurred');
                return throwError(() => error);
            })
        );
    }

    private handle401(
        request: HttpRequest<any>,
        next: HttpHandler
    ): Observable<HttpEvent<any>> {
        return this.authService.refreshAccessToken().pipe(
            switchMap(() => {
                // Retry original request with new token
                const token = this.authService.getAccessToken();
                const cloned = request.clone({
                    setHeaders: {
                        Authorization: `Bearer ${token}`
                    }
                });
                return next.handle(cloned);
            }),
            catchError((error) => {
                this.toastService.error('Session expired. Please login again.');
                this.authService.logout();
                this.router.navigate(['/login']);
                return throwError(() => error);
            })
        );
    }
}
