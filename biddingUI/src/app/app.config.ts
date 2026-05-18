import { ApplicationConfig, ErrorHandler, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { ErrorInterceptor } from './core/auth/error.interceptor';
import { TokenInterceptor } from './core/auth/token.interceptor';

class GlobalErrorHandler implements ErrorHandler {
    handleError(error: Error): void {
        console.error('Uncaught error:', error);
    }
}

export const appConfig: ApplicationConfig = {
    providers: [
        provideBrowserGlobalErrorListeners(),
        {
            provide: ErrorHandler,
            useClass: GlobalErrorHandler
        },
        provideRouter(routes),
        provideHttpClient(
            withInterceptorsFromDi()
        ),
        {
            provide: HTTP_INTERCEPTORS,
            useClass: TokenInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorInterceptor,
            multi: true
        }
    ]
};
