import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Auction } from '../../core/models/auction.model';
import { AuctionService } from '../../core/services/auction.service';
import { PagedResponse } from '../../core/models/pagedResponse.model';

@Component({
    selector: 'app-my-auctions',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './my-auction.component.html',
    styles: [`
        .card {
            border-radius: 14px;
        }
    `],
})
export class MyAuctionsComponent implements OnInit {
    auctions: Auction[] = [];
    loading: boolean = false;
    page: number = 1;
    totalPages: number = 0;

    constructor(private auctionService: AuctionService) {}

    ngOnInit(): void {
        this.loadAuctions();
    }

    loadAuctions(): void {
        this.loading = true;

        this.auctionService.getMyAuctions(this.page).subscribe({
            next: (response: PagedResponse<Auction>) => {
                this.auctions = response.data;
                this.totalPages = response.totalPages;
                this.loading = false;
            },

            error: () => {
                this.loading = false;
            },
        });
    }

    deleteAuction(uid: string): void {
        const confirmed = confirm('Delete this auction?');

        if (!confirmed)
            return;

        this.auctionService.deleteAuction(uid).subscribe({
            next: () => {
                this.loadAuctions();
            },
        });
    }

    nextPage(): void {
        if (this.page < this.totalPages) {
            this.page++;
            this.loadAuctions();
        }
    }

    previousPage(): void {
        if (this.page > 1) {
            this.page--;
            this.loadAuctions();
        }
    }
}
