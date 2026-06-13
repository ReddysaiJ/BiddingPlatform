import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { BidService } from '../../core/services/bid.service';
import { AuthService } from '../../core/auth/auth.service';
import { PagedResponse } from '../../core/models/pagedResponse.model';
import { BidAuction } from '../../core/models/bidAuction.model';

@Component({
    selector: 'app-my-bids',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './my-bids.component.html',
    styles: [`
        .auction-row { cursor: pointer; transition: background 0.15s; }
        .auction-row:hover { background: #f8f9fa; }
        .badge-bids { background: #e9ecef; color: #495057; font-size: 12px; padding: 4px 10px; border-radius: 20px; font-weight: 500; }
    `]
})
export class MyBidsComponent implements OnInit {
    data!: PagedResponse<BidAuction>;
    loading = false;
    page = 1;

    constructor(
        private bidService: BidService,
        private authService: AuthService,
        private router: Router,
        private cdr: ChangeDetectorRef
    ) {}

    ngOnInit(): void { this.loadBids(); }

    loadBids(): void {
        this.loading = true;
        this.cdr.detectChanges();
        const userId = this.authService.getUserId()!;
        this.bidService.getBidsByUser(userId, this.page).subscribe({
            next: (response) => {
                this.data = response;
                console.log(this.data);
                this.loading = false;
                this.cdr.detectChanges();
            },
            error: () => {
                this.loading = false;
                this.cdr.detectChanges();
            }
        });
    }

    openAuction(auction: BidAuction): void {
        this.router.navigate(['/my-bids', auction.auctionUid]);
    }

    nextPage(): void {
        if (this.data?.hasNext) { this.page++; this.loadBids(); }
    }

    previousPage(): void {
        if (this.data?.hasPrevious) { this.page--; this.loadBids(); }
    }
}
