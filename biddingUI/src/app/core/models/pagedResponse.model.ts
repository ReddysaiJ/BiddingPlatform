export interface PagedResponse<T> {
    data: T[];
    pageNumber: number;
    totalPages: number;
    totalElements: number;
    isFirst: boolean;
    isLast: boolean;
    hasNext: boolean;
    hasPrevious: boolean;
}
