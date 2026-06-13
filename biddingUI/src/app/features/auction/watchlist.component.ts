import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Auction } from '../../core/models/auction.model';
import { PagedResponse } from '../../core/models/pagedResponse.model';
import { WatchlistService } from '../../core/services/watchlist.service';
import { AuctionCardComponent } from '../auction/auction-card.component';

@Component({
    selector: 'app-watchlist',
    standalone: true,
    imports: [CommonModule, AuctionCardComponent],
    templateUrl: './watchlist.component.html',
})
export class WatchlistComponent implements OnInit {
    data!: PagedResponse<Auction>;
    auctions: Auction[] = [];
    loading = false;
    page = 1;

    constructor(private watchlistService: WatchlistService, private cdr: ChangeDetectorRef) {}

    ngOnInit(): void {
        this.loadWatchlist();
    }

    loadWatchlist(): void {
        this.loading = true;
        this.watchlistService.getWatchlist(this.page).subscribe({
            next: (response) => {
                this.data = response;
                this.auctions = response.data;
                this.loading = false;
                this.cdr.markForCheck();
            },

            error: () => {
                this.loading = false;
                this.cdr.markForCheck();
            },
        });
    }

    nextPage(): void {
        if (!this.data.hasNext)
            return;

        this.page++;
        this.loadWatchlist();
    }

    previousPage(): void {
        if (!this.data.hasPrevious)
            return;

        this.page--;
        this.loadWatchlist();
    }
}
