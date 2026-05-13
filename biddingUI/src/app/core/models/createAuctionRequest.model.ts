import { AuctionStatus } from './auction.model';

export interface CreateAuctionRequest {
    title: string;
    description: string;
    basePrice: number;
    startTime: string;
    endTime: string;
}
