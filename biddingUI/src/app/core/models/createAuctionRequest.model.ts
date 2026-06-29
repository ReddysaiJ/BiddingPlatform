export interface CreateAuctionRequest {
    title: string;
    description: string;
    basePrice: number;
    startTime: string;
    endTime: string;
    baseImageUrl?: string;
    basePublicId?: string;
}
