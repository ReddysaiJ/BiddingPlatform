export interface Auction {
    uid: string;
    title: string;
    description: string;
    basePrice: number;
    seller: string;
    status: AuctionStatus;
    startTime: string;
    endTime: string;
    createdAt: string;
    updatedAt: string;
}

export enum AuctionStatus {
    DRAFT = 'DRAFT',
    OPEN = 'OPEN',
    CLOSED = 'CLOSED',
    CANCELLED = 'CANCELLED',
}
