import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { BidService } from '../../core/services/bid.service';
import { AuthService } from '../../core/auth/auth.service';
import { Bid } from '../../core/models/bid.model';
import { PagedResponse } from '../../core/models/pagedResponse.model';

@Component({
    selector: 'app-bid-detail',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './bid-detail.component.html',
    styles: [
        `
            .bid-row {
                transition: background 0.15s;
            }
            .bid-row:hover {
                background: #f8f9fa;
            }
            .amount-badge {
                font-size: 15px;
                font-weight: 600;
                color: #198754;
            }
        `,
    ],
})
export class BidDetailComponent implements OnInit {
    data!: PagedResponse<Bid>;
    loading = false;
    page = 1;
    auctionId = '';
    auctionTitle = '';

    constructor(
        private bidService: BidService,
        private authService: AuthService,
        private route: ActivatedRoute,
        private router: Router,
        private cdr: ChangeDetectorRef,
    ) {}

    ngOnInit(): void {
        this.auctionId = this.route.snapshot.paramMap.get('auctionId')!;
        this.loadBids();
    }

    loadBids(): void {
        this.loading = true;
        this.cdr.detectChanges();
        const userId = this.authService.getUserId()!;
        this.bidService.getBidsByUserAndAuction(userId, this.auctionId, this.page).subscribe({
            next: (response) => {
                this.data = response;
                if (response.data.length > 0) {
                    this.auctionTitle = response.data[0].auctionTitle;
                }
                this.loading = false;
                this.cdr.detectChanges();
            },
            error: () => {
                this.loading = false;
                this.cdr.detectChanges();
            },
        });
    }

    goBack(): void {
        this.router.navigate(['/my-bids']);
    }

    nextPage(): void {
        if (this.data?.hasNext) {
            this.page++;
            this.loadBids();
        }
    }

    previousPage(): void {
        if (this.data?.hasPrevious) {
            this.page--;
            this.loadBids();
        }
    }
}
