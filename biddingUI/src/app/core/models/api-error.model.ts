export interface ApiError {
    type: string;
    title: string;
    status: number;
    detail: string;
    instance: string;
    timestamp?: string;
    errors?: Record<string, string[]>;
    service?: string;
}

export function isApiError(obj: any): obj is ApiError {
    return (
        typeof obj === 'object' &&
        'status' in obj &&
        'title' in obj &&
        'detail' in obj
    );
}
