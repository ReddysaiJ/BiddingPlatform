import { Auction } from './../models/auction.model';
import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { PlaceBidRequest } from "../models/placeBidRequest.model";
import { Observable } from "rxjs";
import { Bid } from "../models/bid.model";
import { PagedResponse } from "../models/pagedResponse.model";
import { BidAuction } from '../models/bidAuction.model';

@Injectable({providedIn: 'root'})
export class BidService{
    private api = `${environment.apiUrl}/api/bids`;

    constructor(private http: HttpClient){}

    placeBid(request: PlaceBidRequest): Observable<Bid>{
        return this.http.post<Bid>(this.api, request);
    }

    getBidsByUser(userId: string, page: number = 1): Observable<PagedResponse<BidAuction>>{
        const params = new HttpParams()
                        .set('page', page);
        return this.http.get<PagedResponse<BidAuction>>(`${this.api}/user/${userId}`, { params });
    }

    getBidsByUserAndAuction(userId: string, auctionId: string, page: number = 1): Observable<PagedResponse<Bid>> {
        const params = new HttpParams().set('page', page);
        return this.http.get<PagedResponse<Bid>>(`${this.api}/user/${userId}/${auctionId}`, { params });
    }

    getBidsByAuction(auctionId: string, page: number = 1): Observable<PagedResponse<Bid>>{
        const params = new HttpParams()
                        .set('page', page);
        return this.http.get<PagedResponse<Bid>>(`${this.api}/auction/${auctionId}`, { params });
    }

    getHighestBid(auctionId: string): Observable<Bid>{
        return this.http.get<Bid>(`${this.api}/auction/${auctionId}/highest`);
    }
}
