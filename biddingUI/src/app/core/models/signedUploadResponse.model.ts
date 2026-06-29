export interface SignedUploadResponse {
    signature: string;
    apiKey: string;
    timestamp: number;
    cloudName: string;
    folder: string;
    publicId: string;
    uploadUrl: string;
}
