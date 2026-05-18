import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoggerService } from './logger.service';

@Injectable({ providedIn: 'root' })
export class ApiService {
    private baseUrl = environment.apiUrl;

    constructor(
        private http: HttpClient,
        private logger: LoggerService
    ) {}

    get<T>(endpoint: string, params?: Record<string, any>): Observable<T> {
        const url = `${this.baseUrl}${endpoint}`;
        let httpParams = new HttpParams();

        if (params) {
            Object.keys(params).forEach(key => {
                if (params[key] !== null && params[key] !== undefined) {
                    httpParams = httpParams.set(key, String(params[key]));
                }
            });
        }

        this.logger.debug(`GET ${url}`, { params });
        return this.http.get<T>(url, { params: httpParams });
    }

    post<T>(endpoint: string, body: any): Observable<T> {
        const url = `${this.baseUrl}${endpoint}`;
        this.logger.debug(`POST ${url}`, { body });
        return this.http.post<T>(url, body);
    }

    put<T>(endpoint: string, body: any): Observable<T> {
        const url = `${this.baseUrl}${endpoint}`;
        this.logger.debug(`PUT ${url}`, { body });
        return this.http.put<T>(url, body);
    }

    patch<T>(endpoint: string, body: any): Observable<T> {
        const url = `${this.baseUrl}${endpoint}`;
        this.logger.debug(`PATCH ${url}`, { body });
        return this.http.patch<T>(url, body);
    }

    delete<T>(endpoint: string): Observable<T> {
        const url = `${this.baseUrl}${endpoint}`;
        this.logger.debug(`DELETE ${url}`);
        return this.http.delete<T>(url);
    }
}
