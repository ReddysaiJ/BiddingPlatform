import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CreateAuctionRequest } from '../../core/models/createAuctionRequest.model';
import { AuctionService } from '../../core/services/auction.service';
import { AuctionStatus } from '../../core/models/auction.model';

@Component({
    selector: 'app-auction-create',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './auctionCreate.component.html',
    styles: [`
        .card {
            border-radius: 14px;
        }
    `],
})
export class AuctionCreateComponent {
    request: CreateAuctionRequest = {
        title: '',
        description: '',
        basePrice: 0,
        startTime: '',
        endTime: '',
    };

    loading: boolean = false;
    errorMessage: string = '';

    constructor(private auctionService: AuctionService, private router: Router) {}

    createAuction(): void {
        this.errorMessage = '';
        this.loading = true;

        this.auctionService.createAuction(this.request).subscribe({
            next: (auction) => {
                this.loading = false;
                this.router.navigate(['/auction', auction.id]);
            },

            error: (error) => {
                this.loading = false;

                this.errorMessage = error?.error?.message || 'Failed to create auction';
            },
        });
    }
}
