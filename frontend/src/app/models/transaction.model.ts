export interface Transaction {
    id: number;
    userId: number;
    amount: number;
    description: string;
    category: string;
    status: 'PENDING' | 'CONFIRMED' | 'REJECTED'; 
    timestamp: string;
}