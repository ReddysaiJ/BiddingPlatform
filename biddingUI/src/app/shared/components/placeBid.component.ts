import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BidService } from '../../core/services/bid.service';
import { AuthService } from '../../core/auth/auth.service';
import { PlaceBidRequest } from '../../core/models/placeBidRequest.model';

@Component({
    selector: 'app-place-bid',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './placeBid.component.html',
    styles: [`
        .card {
            border-radius: 12px;
        }
    `],
})
export class PlaceBidComponent {
    @Input()
    auctionId!: string;

    @Output()
    bidPlaced = new EventEmitter<void>();

    amount!: number;
    loading: boolean = false;
    errorMessage: string = '';
    successMessage: string = '';

    constructor(private bidService: BidService, private authService: AuthService) {}

    placeBid(): void {
        this.errorMessage = '';
        this.successMessage = '';
        if (!this.amount || this.amount <= 0) {
            this.errorMessage = 'Enter valid bid amount';
            return;
        }

        const request: PlaceBidRequest = {
            userId: this.authService.getUserId()!,
            auctionId: this.auctionId,
            amount: this.amount,
        };

        this.loading = true;
        this.bidService.placeBid(request).subscribe({
            next: () => {
                this.loading = false;
                this.successMessage = 'Bid placed successfully';
                this.amount = 0;
                this.bidPlaced.emit();
            },

            error: (error) => {
                this.loading = false;
                this.errorMessage = error?.error?.message || 'Failed to place bid';
            },
        });
    }
}
