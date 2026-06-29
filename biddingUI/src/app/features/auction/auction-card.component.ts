import { Component, EventEmitter, Input, Output, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Auction } from '../../core/models/auction.model';
import { WatchlistService } from '../../core/services/watchlist.service';
import { CountdownTimerComponent } from "../../shared/components/countdown-timer.component";

@Component({
    selector: 'app-auction-card',
    standalone: true,
    imports: [CommonModule, RouterLink, CountdownTimerComponent],
    templateUrl: './auction-card.component.html',
    styles: [`
        .auction-card {
            border: none;
            border-radius: 18px;
            overflow: hidden;
            transition: 0.3s;
            cursor: pointer;
        }

        .auction-card:hover {
            transform: translateY(-6px);
        }

        .image-wrapper {
            position: relative;
        }

        .card-img-top {
            height: 220px;
            object-fit: cover;
        }

        .watch-btn {
            position: absolute;
            top: 12px;
            right: 12px;
            width: 42px;
            height: 42px;
            border: none;
            border-radius: 50%;
            background: white;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 18px;
        }

        .bi-heart-fill {
            color: #ef4444;
        }

        .price {
            font-size: 24px;
            font-weight: 700;
            color: #2563eb;
        }
    `],
})
export class AuctionCardComponent {
    @Input()
    auction!: Auction;

    loading = false;

    constructor(private watchlistService: WatchlistService, private cdr: ChangeDetectorRef) {}

    toggleWatchlist(event: MouseEvent): void {
        event.preventDefault();
        event.stopPropagation();
        if(this.loading)
            return;
        this.loading = true;

        const request = this.auction.watched
            ? this.watchlistService.removeFromWatchlist(this.auction.uid)
            : this.watchlistService.addToWatchlist(this.auction.uid);

        request.subscribe({
            next: () => {
                this.auction.watched = !this.auction.watched;
                this.loading = false;
                this.cdr.markForCheck();
            },

            error: () => {
                this.loading = false;
            },
        });
    }
}
