import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    constructor(private authService: AuthService) {}

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(
            catchError((error: HttpErrorResponse) => {
                if (error.status === 401) {
                    return this.authService.refreshAccessToken().pipe(
                        switchMap(() => {
                            const token = this.authService.getAccessToken();

                            const cloned = request.clone({
                                setHeaders: {
                                    Authorization: `Bearer ${token}`,
                                },
                            });

                            return next.handle(cloned);
                        }),

                        catchError((err) => {
                            this.authService.logout();
                            return throwError(() => err);
                        }),
                    );
                }

                if (error.status === 403)
                    alert('Access denied');

                if (error.status >= 500)
                    alert('Server error');

                return throwError(() => error);
            }),
        );
    }
}
