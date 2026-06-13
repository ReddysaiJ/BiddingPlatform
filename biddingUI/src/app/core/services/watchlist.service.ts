import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PagedResponse } from '../models/pagedResponse.model';
import { Auction } from '../models/auction.model';

@Injectable({
    providedIn: 'root',
})
export class WatchlistService {
    private api = `${environment.apiUrl}/api/auctions/watch`;
    private watchlistCountSubject = new BehaviorSubject<number>(0);
    watchlistCount$ = this.watchlistCountSubject.asObservable();

    constructor(private http: HttpClient) {}

    getWatchlist(
        page = 1,
        sortBy = 'startTime',
        direction = 'asc',
    ): Observable<PagedResponse<Auction>> {
        const params = new HttpParams()
            .set('page', page)
            .set('sortBy', sortBy)
            .set('direction', direction);
        return this.http.get<PagedResponse<Auction>>(this.api, { params });
    }

    addToWatchlist(auctionId: string): Observable<void> {
        const params = new HttpParams().set('uid', auctionId);

        return this.http.post<void>(this.api, {}, { params }).pipe(
            tap(() => {
                this.incrementCount();
            }),
        );
    }

    removeFromWatchlist(auctionId: string): Observable<void> {
        const params = new HttpParams().set('uid', auctionId);

        return this.http.delete<void>(this.api, { params }).pipe(
            tap(() => {
                this.decrementCount();
            }),
        );
    }

    setWatchlistCount(count: number): void {
        this.watchlistCountSubject.next(count);
    }

    private incrementCount(): void {
        this.watchlistCountSubject.next(this.watchlistCountSubject.value + 1);
    }

    private decrementCount(): void {
        const current = this.watchlistCountSubject.value;
        this.watchlistCountSubject.next(current > 0 ? current - 1 : 0);
    }
}
