import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Auction } from '../../core/models/auction.model';
import { AuctionService } from '../../core/services/auction.service';
import { PagedResponse } from '../../core/models/pagedResponse.model';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-auction-list',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule],
    templateUrl: './auction-list.component.html',
    styles: [`
        .card {
            border: none;
            border-radius: 12px;
        }

        .card-title {
            font-weight: 600;
        }

        input {
            max-width: 300px;
        }
    `],
})
export class AuctionListComponent implements OnInit {
    auctions: Auction[] = [];
    loading: boolean = false;
    page: number = 1;
    totalPages: number = 0;
    query: string = '';

    constructor(private auctionService: AuctionService) {}

    ngOnInit(): void {
        this.loadAuctions();
    }

    loadAuctions(): void {
        this.loading = true;
        this.auctionService.getAllAuctions(this.page, 'startTime', 'asc', this.query).subscribe({
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

    nextPage(): void {
        if (this.page < this.totalPages) {
            this.page++;
            this.loadAuctions();
        }
    }

    previousPage(): void {
        if(this.page > 1) {
            this.page--;
            this.loadAuctions();
        }
    }
}
