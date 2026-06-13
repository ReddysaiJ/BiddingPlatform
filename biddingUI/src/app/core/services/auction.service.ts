import { Auction, AuctionStatus } from './../models/auction.model';
import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { PagedResponse } from '../models/pagedResponse.model';
import { CreateAuctionRequest } from '../models/createAuctionRequest.model';
import { UpdateAuctionRequest } from '../models/updateAuctionRequest.model';

@Injectable({providedIn: 'root'})
export class AuctionService{
    private api = `${environment.apiUrl}/api/auctions`;

    constructor(private http: HttpClient){}

    getAllAuctions(page: number = 1, sortBy: string = 'startTime', direction: string = 'asc', query: string = '', status: AuctionStatus): Observable<PagedResponse<Auction>>{
        const params = new HttpParams()
                        .set('page', page)
                        .set('sortBy', sortBy)
                        .set('direction', direction)
                        .set('query', query)
                        .set('status', status);
        return this.http.get<PagedResponse<Auction>>(`${this.api}/status`, { params });
    }

    getAuctionById(uid : string): Observable<Auction>{
        return this.http.get<Auction>(`${this.api}/${uid}`);
    }

    createAuction(auction: CreateAuctionRequest): Observable<Auction>{
        return this.http.post<Auction>(this.api, auction);
    }

    updateAuction(auction: UpdateAuctionRequest): Observable<Auction>{
        return this.http.put<Auction>(this.api, auction);
    }

    deleteAuction(uid: String): Observable<void>{
        return this.http.delete<void>(`${this.api}/${uid}`);
    }

    getMyAuctions(page: number = 1, sortBy: string = 'startTime', direction: string = 'asc', query: string = ''): Observable<PagedResponse<Auction>>{
        const params = new HttpParams()
                        .set('page', page)
                        .set('sortBy', sortBy)
                        .set('direction', direction)
                        .set('query', query);
        return this.http.get<PagedResponse<Auction>>(`${this.api}/my`, { params });
    }

    getWatchList(page: number = 1, sortBy: string = 'startTime', direction: string = 'asc'): Observable<PagedResponse<Auction>>{
        const params = new HttpParams()
                        .set('page', page)
                        .set('sortBy', sortBy)
                        .set('direction', direction);
        return this.http.get<PagedResponse<Auction>>(`${this.api}/watch`, { params });
    }

    addToWatchlist(uid: string): Observable<void> {
        return this.http.post<void>(`${this.api}/${uid}/watch`, {});
    }

    removeFromWatchlist(uid: string): Observable<void> {
        return this.http.delete<void>(`${this.api}/${uid}/watch`);
    }
}
