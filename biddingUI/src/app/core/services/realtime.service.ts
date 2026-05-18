import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Client, IMessage } from '@stomp/stompjs';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';

@Injectable({providedIn: 'root',})
export class RealtimeService {
    private client!: Client;
    private api = `${environment.apiUrl}/realtime/auctions`;
    private wsUrl = environment.wsUrl || 'ws://localhost:8088/ws';

    constructor(private authService: AuthService, private http: HttpClient){}

    connect(): void {
        this.client = new Client({
            brokerURL: this.wsUrl,
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            connectHeaders: {
                Authorization: `Bearer ${this.authService.getAccessToken()}`,
            },
        });
        this.client.activate();
    }

    disconnect(): void {
        if(this.client)
            this.client.deactivate();
    }

    subscribe(destination: string): Observable<any> {
        const subject = new Subject<any>();
        this.client.subscribe(destination, (message: IMessage) => {
            subject.next(JSON.parse(message.body));
        });
        return subject.asObservable();
    }

    watchHighestBid(auctionId: string): Observable<any> {
        return this.subscribe(`/topic/auction/${auctionId}/highestBid`);
    }

    watchStatus(auctionId: string): Observable<any> {
        return this.subscribe(`/topic/auction/${auctionId}/status`);
    }

    watchWinner(auctionId: string): Observable<any> {
        return this.subscribe(`/topic/auction/${auctionId}/winner`);
    }

    //http
    getAuctionSummary(auctionId: string): Observable<any> {
        return this.http.get(`${this.api}/${auctionId}`);
    }

    getAuctionDetails(auctionId: string): Observable<any> {
        return this.http.get(`${this.api}/${auctionId}/details`);
    }

    getAuctionStatus(auctionId: string): Observable<string> {
        return this.http.get(`${this.api}/${auctionId}/status`, {
            responseType: 'text',
        });
    }

    getHighestBid(auctionId: string): Observable<any> {
        return this.http.get(`${this.api}/${auctionId}/highest-bid`);
    }

    getWinner(auctionId: string): Observable<any> {
        return this.http.get(`${this.api}/${auctionId}/winner`);
    }
}
