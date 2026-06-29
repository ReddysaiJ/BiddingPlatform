export interface Auction {
    uid: string;
    title: string;
    description: string;
    basePrice: number;
    seller: string;
    status: string;
    baseImageUrl: string;
    startTime: string;
    endTime: string;
    watched: boolean;
    createdAt: string;
    updatedAt: string;
}

export interface AuctionImageResponse {
    auctionUid: string;
    baseImageUrl: string;
    imageUrls: string[];
}

export enum AuctionStatus {
    DRAFT = 'DRAFT',
    OPEN = 'OPEN',
    CLOSED = 'CLOSED',
    CANCELLED = 'CANCELLED',
}
