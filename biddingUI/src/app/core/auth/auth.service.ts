import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';

interface TokenResponse {
    access_token: string;
    refresh_token: string;
    expires_in: number;
    refresh_expires_in: number;
    token_type: string;
}

interface JwtPayload {
    sub: string;
    exp: number;
    preferred_username: string;
    resource_access: {
        [key: string]: {
            roles: string[];
        };
    };
}

@Injectable({ providedIn: 'root' })
export class AuthService {
    private accessToken: string | null = null;
    private refreshToken: string | null = null;

    constructor(
        private http: HttpClient,
        private router: Router,
    ) {
        this.restoreTokens();
    }

    login(): void {
        const codeVerifier = this.generateCodeVerifier();
        sessionStorage.setItem('pkce_code_verifier', codeVerifier);
        this.generateCodeChallenge(codeVerifier).then((codeChallenge) => {
            const params = new URLSearchParams({
                client_id: environment.clientId,
                redirect_uri: environment.redirectUri,
                response_type: 'code',
                scope: environment.scope,
                code_challenge: codeChallenge,
                code_challenge_method: 'S256',
            });
            window.location.href = `${environment.authUrl}?${params.toString()}`;
        });
    }

    handleAuthCallback(code: string): Observable<TokenResponse> {
        const codeVerifier = sessionStorage.getItem('pkce_code_verifier') || '';

        const body = new HttpParams()
            .set('grant_type', 'authorization_code')
            .set('client_id', environment.clientId)
            .set('code', code)
            .set('redirect_uri', environment.redirectUri)
            .set('code_verifier', codeVerifier);

        const headers = new HttpHeaders({
            'Content-Type': 'application/x-www-form-urlencoded',
        });

        return this.http
            .post<TokenResponse>(environment.tokenUrl, body.toString(), { headers })
            .pipe(
                tap((res) => {
                    this.storeTokens(res.access_token, res.refresh_token);
                    sessionStorage.removeItem('pkce_code_verifier');
                }),
            );
    }

    checkAuthCallback(): void {
        const params = new URLSearchParams(window.location.search);

        const code = params.get('code');

        if (!code) {
            return;
        }

        const alreadyProcessing = sessionStorage.getItem('auth_processing');

        if (alreadyProcessing === 'true') {
            return;
        }

        sessionStorage.setItem('auth_processing', 'true');

        this.handleAuthCallback(code).subscribe({
            next: () => {
                window.history.replaceState({}, document.title, window.location.pathname);

                sessionStorage.removeItem('auth_processing');

                this.router.navigate(['/auctions']);
            },

            error: (err) => {
                console.error('Token exchange failed', err);

                sessionStorage.removeItem('auth_processing');

                this.logout();

                this.router.navigate(['/login']);
            },
        });
    }

    refreshAccessToken(): Observable<TokenResponse> {
        const body = new HttpParams()
            .set('grant_type', 'refresh_token')
            .set('client_id', environment.clientId)
            .set('refresh_token', this.refreshToken || '');

        const headers = new HttpHeaders({
            'Content-Type': 'application/x-www-form-urlencoded',
        });

        return this.http
            .post<TokenResponse>(environment.tokenUrl, body.toString(), { headers })
            .pipe(
                tap((res) => {
                    this.storeTokens(res.access_token, res.refresh_token);
                }),
            );
    }

    logout(): void {
        this.accessToken = null;
        this.refreshToken = null;

        sessionStorage.removeItem('access_token');
        sessionStorage.removeItem('refresh_token');
        sessionStorage.removeItem('pkce_code_verifier');
    }

    getAccessToken(): string | null {
        return this.accessToken;
    }

    isLoggedIn(): boolean {
        if (!this.accessToken) return false;

        const decoded = this.decodeToken();
        if (!decoded) return false;
        return decoded.exp * 1000 > Date.now();
    }

    getRoles(): string[] {
        const decoded = this.decodeToken();
        if (!decoded) return [];
        return decoded.resource_access?.[environment.clientId]?.roles || [];
    }

    hasRole(role: string): boolean {
        return this.getRoles().includes(role);
    }

    getUsername(): string | null {
        const decoded = this.decodeToken();
        return decoded?.preferred_username || null;
    }

    getUserId(): string | null {
        const decoded = this.decodeToken();
        return decoded?.sub || null;
    }

    private storeTokens(accessToken: string, refreshToken: string): void {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;

        // Store in sessionStorage — cleared on browser close, more secure than localStorage
        sessionStorage.setItem('access_token', accessToken);
        sessionStorage.setItem('refresh_token', refreshToken);
    }

    private restoreTokens(): void {
        const accessToken = sessionStorage.getItem('access_token');
        const refreshToken = sessionStorage.getItem('refresh_token');

        if (accessToken && refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    private decodeToken(): JwtPayload | null {
        if (!this.accessToken) return null;

        try {
            return jwtDecode<JwtPayload>(this.accessToken);
        } catch {
            return null;
        }
    }

    private generateCodeVerifier(): string {
        const array = new Uint8Array(32);
        crypto.getRandomValues(array);

        return btoa(String.fromCharCode(...array))
            .replace(/\+/g, '-')
            .replace(/\//g, '_')
            .replace(/=/g, '');
    }

    private async generateCodeChallenge(verifier: string): Promise<string> {
        const data = new TextEncoder().encode(verifier);
        const hash = await crypto.subtle.digest('SHA-256', data);

        return btoa(String.fromCharCode(...new Uint8Array(hash)))
            .replace(/\+/g, '-')
            .replace(/\//g, '_')
            .replace(/=/g, '');
    }
}
