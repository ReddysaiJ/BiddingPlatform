import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { Auction } from '../../core/models/auction.model';
import { AuctionService } from '../../core/services/auction.service';
import { RealtimeService } from '../../core/services/realtime.service';
import { BidService } from '../../core/services/bid.service';
import { PlaceBidComponent } from '../bid/placeBid.component';

@Component({
    selector: 'app-auction-detail',
    standalone: true,
    imports: [CommonModule, PlaceBidComponent],
    templateUrl: './auction-detail.component.html',
    styles: [`
        .info-box {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 12px;
            text-align: center;
        }
    `],
})
export class AuctionDetailComponent implements OnInit, OnDestroy {
    auctionId!: string;
    auction!: Auction;
    highestBid: any;
    status!: string;
    winner: any;
    loading = false;

    private subscriptions: Subscription[] = [];

    constructor(
        private route: ActivatedRoute,
        private auctionService: AuctionService,
        private realtimeService: RealtimeService,
        private bidService: BidService,
        private cdr: ChangeDetectorRef
    ) {}

    ngOnInit(): void {
        this.auctionId = this.route.snapshot.paramMap.get('id')!;
        this.loadAuction();
        this.loadRealtimeSnapshot();
        this.connectRealtime();
    }

    loadAuction(): void {
        this.loading = true;
        this.auctionService.getAuctionById(this.auctionId).subscribe({
            next: (response) => {
                this.auction = response;
                this.status = response.status;
                this.loading = false;
                this.cdr.detectChanges();
            },
            error: () => {
                this.loading = false;
                this.cdr.detectChanges();
            },
        });
    }

    loadRealtimeSnapshot(): void {
        this.realtimeService.getAuctionSummary(this.auctionId).subscribe({
            next: (response) => {
                // console.log(response);
                this.highestBid = response.highestBid;
                this.status = response.status;
                this.winner = response.winner;
                this.cdr.detectChanges();
            },
            error: () => {
                console.warn('Realtime snapshot not available, using auction status from DB');
            }
        });
    }

    connectRealtime(): void {
        this.realtimeService.connect(() => {
            const highestBidSub = this.realtimeService
                .watchHighestBid(this.auctionId)
                .subscribe((data) => {
                    this.highestBid = data;
                    this.cdr.detectChanges();
                });

            const statusSub = this.realtimeService
                .watchStatus(this.auctionId)
                .subscribe((data) => {
                    this.status = data;
                    this.cdr.detectChanges();
                });

            const winnerSub = this.realtimeService
                .watchWinner(this.auctionId)
                .subscribe((data) => {
                    this.winner = data;
                    this.cdr.detectChanges();
                });

            this.subscriptions.push(highestBidSub, statusSub, winnerSub);
        });
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach((sub) => sub.unsubscribe());
        this.realtimeService.disconnect();
    }
}
