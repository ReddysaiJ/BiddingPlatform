export interface Auction {
    id: string;
    title: string;
    description: string;
    startPrice: number;
    currentHighestBid: number;
    sellerId: string;
    sellerName: string;
    startTime: string;
    endTime: string;
    status: AuctionStatus;
}

export enum AuctionStatus {
    DRAFT = 'DRAFT',
    OPEN = 'OPEN',
    CLOSED = 'CLOSED',
    CANCELLED = 'CANCELLED',
}
