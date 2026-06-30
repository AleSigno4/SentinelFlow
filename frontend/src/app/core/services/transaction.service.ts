import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Transaction } from '../../models/transaction.model';
import { environment } from '../../../environments/environment.development';

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private http = inject(HttpClient);
  url = `${environment.apiUrl}/transactions`;

  getTransactions() {
    return this.http.get<Transaction[]>(this.url);
  }

  updateTransactionStatus(transactionId: number, newStatus: 'CONFIRMED' | 'REJECTED' | 'PENDING') {
    return this.http.patch(`${this.url}/${transactionId}/status`, { status: newStatus });
  }

  login(username: string, password: string) {
    return this.http.post<{ token: string }>(`${environment.apiUrl}/auth/login`, { username, password });
  }

}
