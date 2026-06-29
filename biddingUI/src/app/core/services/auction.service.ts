import { Auction, AuctionImageResponse, AuctionStatus } from './../models/auction.model';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable, switchMap } from 'rxjs';
import { PagedResponse } from '../models/pagedResponse.model';
import { CreateAuctionRequest } from '../models/createAuctionRequest.model';
import { UpdateAuctionRequest } from '../models/updateAuctionRequest.model';
import { SignedUploadResponse } from '../models/signedUploadResponse.model';

@Injectable({ providedIn: 'root' })
export class AuctionService {
    private api = `${environment.apiUrl}/api/auctions`;

    constructor(private http: HttpClient) {}

    getAllAuctions(
        page: number = 1,
        sortBy: string = 'startTime',
        direction: string = 'asc',
        query: string = '',
        status: AuctionStatus,
    ): Observable<PagedResponse<Auction>> {
        const params = new HttpParams()
            .set('page', page)
            .set('sortBy', sortBy)
            .set('direction', direction)
            .set('query', query)
            .set('status', status);
        return this.http.get<PagedResponse<Auction>>(`${this.api}/status`, { params });
    }

    getAuctionById(uid: string): Observable<Auction> {
        return this.http.get<Auction>(`${this.api}/${uid}`);
    }

    createAuction(auction: CreateAuctionRequest): Observable<Auction> {
        return this.http.post<Auction>(this.api, auction);
    }

    updateAuction(auction: UpdateAuctionRequest): Observable<Auction> {
        return this.http.put<Auction>(this.api, auction);
    }

    deleteAuction(uid: String): Observable<void> {
        return this.http.delete<void>(`${this.api}/${uid}`);
    }

    getMyAuctions(
        page: number = 1,
        sortBy: string = 'startTime',
        direction: string = 'asc',
        query: string = '',
    ): Observable<PagedResponse<Auction>> {
        const params = new HttpParams()
            .set('page', page)
            .set('sortBy', sortBy)
            .set('direction', direction)
            .set('query', query);
        return this.http.get<PagedResponse<Auction>>(`${this.api}/my`, { params });
    }

    getBaseImageSignature() {
        return this.http.get<SignedUploadResponse>(`${this.api}/images/sign/base`);
    }

    uploadBaseImage(file: File) {
        return this.getBaseImageSignature().pipe(
            switchMap((signed) =>
                this.uploadToCloudinary(file, signed).pipe(
                    map((res) => ({
                        secureUrl: res.secure_url,
                        publicId: res.public_id,
                    })),
                ),
            ),
        );
    }

    getExtraImageSignatures(uid: string, count: number) {
        return this.http.get<SignedUploadResponse[]>(
            `${this.api}/images/${uid}/sign?count=${count}`,
        );
    }

    saveImageUrls(uid: string, imageUrls: string[], publicIds: string[]) {
        return this.http.patch(`${this.api}/images/${uid}`, {
            imageUrls,
            publicIds,
        });
    }

    uploadToCloudinary(file: File, signed: SignedUploadResponse) {
        const form = new FormData();

        form.append('file', file);
        form.append('api_key', signed.apiKey);
        form.append('timestamp', signed.timestamp.toString());
        form.append('signature', signed.signature);
        form.append('folder', signed.folder);
        form.append('public_id', signed.publicId);

        return this.http.post<any>(signed.uploadUrl, form);
    }

    getImagesByUid(uid: string){
        return this.http.get<AuctionImageResponse>(`${this.api}/images/${uid}`);
    }
}
