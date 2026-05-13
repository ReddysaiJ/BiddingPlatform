import { AuthService } from './auth.service';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable()
export class TokenInterceptor implements HttpInterceptor{
    constructor(private authService : AuthService){}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if(
            req.url.includes('/protocol/openid-connect/token') ||
            req.url.includes('/protocol/openid-connect/auth')
        ){
            return next.handle(req);
        }

        const token = this.authService.getAccessToken();
        if(!token)
            return next.handle(req);

        const cloned = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
        throw next.handle(cloned);
    }

}
