import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
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
    data!: PagedResponse<Auction>;
    auctions: Auction[] = [];
    loading: boolean = false;
    page: number = 1;
    query: string = '';

    constructor(private auctionService: AuctionService, private cdr: ChangeDetectorRef) {}

    ngOnInit(): void {
        this.loadAuctions();
        console.log(this.auctions);
    }

    loadAuctions(): void {
        this.loading = true;
        this.auctionService.getAllAuctions(this.page, 'startTime', 'asc', this.query).subscribe({
            next: (response: PagedResponse<Auction>) => {
                console.log(response);
                this.data = response
                this.auctions = response.data;
                this.loading = false;
                this.cdr.detectChanges();
            },

            error: () => {
                this.loading = false;
                this.cdr.detectChanges();
            },
        });
    }

    onSearch(): void {
        this.page = 1;
        this.loadAuctions();
    }

    nextPage(): void {
        if (this.data.hasNext) {
            this.page++;
            this.loadAuctions();
        }
    }

    previousPage(): void {
        if(this.data.hasPrevious) {
            this.page--;
            this.loadAuctions();
        }
    }
}
