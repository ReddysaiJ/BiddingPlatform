import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { Auction } from '../../core/models/auction.model';
import { AuctionService } from '../../core/services/auction.service';
import { RealtimeService } from '../../core/services/realtime.service';
import { BidService } from '../../core/services/bid.service';
import { PlaceBidComponent } from '../bid/placeBid.component';
import { CountdownTimerComponent } from "../../shared/components/countdown-timer.component";

@Component({
    selector: 'app-auction-detail',
    standalone: true,
    imports: [CommonModule, PlaceBidComponent, CountdownTimerComponent],
    templateUrl: './auction-detail.component.html',
    styles: [`
        .info-box {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 12px;
            text-align: center;
        }
    `],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AuctionDetailComponent implements OnInit, OnDestroy {
    auctionId!: string;
    auction!: Auction;
    highestBid: any;
    status!: string;
    winner: any;
    loading = false;
    bidHistory: any[] = [];
    pulse = false;
    connected = false;

    selectedImage = '';
    allImages: string[] = [];

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

                // this.allImages = [];

                if (response.baseImageUrl) {
                    this.allImages.push(response.baseImageUrl);
                }

                this.selectedImage =
                    this.allImages.length > 0
                        ? this.allImages[0]
                        : 'https://via.placeholder.com/800x500?text=No+Image';

                this.loading = false;
                this.cdr.markForCheck();
            },
            error: () => {
                this.loading = false;
                this.cdr.markForCheck();
            },
        });

        this.auctionService.getImagesByUid(this.auctionId).subscribe({
            next: (response) => {
                if(response.imageUrls)
                    this.allImages.push(...response.imageUrls);
                this.cdr.markForCheck();
            },
            error: () => {
                console.warn("Load images error");
                this.cdr.markForCheck();
            }
        })
    }

    selectImage(image: string): void {
        this.selectedImage = image;
        this.cdr.markForCheck();
    }

    loadRealtimeSnapshot(): void {
        this.realtimeService.getAuctionSummary(this.auctionId).subscribe({
            next: (response) => {
                // console.log(response);
                this.highestBid = response.highestBid;
                this.status = response.status;
                this.winner = response.winner;
                this.cdr.markForCheck();
            },
            error: () => {
                console.warn('Realtime snapshot not available, using auction status from DB');
                this.cdr.markForCheck();
            }
        });
    }

    connectRealtime(): void {
        if (this.connected)
            return;

        this.connected = true;
        this.realtimeService.connect(() => {
            this.subscriptions.push(
                this.realtimeService
                    .watchHighestBid(this.auctionId)
                    .subscribe(data => {
                        this.highestBid = data;
                        this.bidHistory.unshift(data);
                        this.animateHighestBid();
                        this.cdr.markForCheck();
                    }),

                this.realtimeService
                    .watchStatus(this.auctionId)
                    .subscribe(data => {
                        this.status = data;
                        this.cdr.markForCheck();
                    }),

                this.realtimeService
                    .watchWinner(this.auctionId)
                    .subscribe(data => {
                        this.winner = data;
                        this.cdr.markForCheck();
                    })
            );
        });
    }

    animateHighestBid(): void {
        this.pulse = true;
        setTimeout(() => {
            this.pulse = false;
            this.cdr.markForCheck();
        }, 800);
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach((sub) => sub.unsubscribe());
        this.realtimeService.disconnect();
    }
}
