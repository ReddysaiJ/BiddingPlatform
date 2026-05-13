import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BidService } from '../../core/services/bid.service';
import { AuthService } from '../../core/auth/auth.service';
import { Bid } from '../../core/models/bid.model';
import { PagedResponse } from '../../core/models/pagedResponse.model';

@Component({
    selector: 'app-my-bids',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './my-bids.component.html',
    styles: [`
        table {
            background: white;
        }
    `],
})
export class MyBidsComponent implements OnInit {
    bids: Bid[] = [];
    loading: boolean = false;
    page: number = 1;
    totalPages: number = 0;

    constructor(
        private bidService: BidService,
        private authService: AuthService,
    ) {}

    ngOnInit(): void {
        this.loadBids();
    }

    loadBids(): void {
        const userId = this.authService.getUserId()!;

        this.loading = true;
        this.bidService.getBidsByUser(userId, this.page).subscribe({
            next: (response: PagedResponse<Bid>) => {
                this.bids = response.data;

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
            this.loadBids();
        }
    }

    previousPage(): void {
        if (this.page > 1) {
            this.page--;
            this.loadBids();
        }
    }
}
