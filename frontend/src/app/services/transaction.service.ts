import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Transaction } from '../models/transaction.model';

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private http = inject(HttpClient);
  url = 'http://localhost:8081/api/transactions';

  getTransactions() {
    return this.http.get<Transaction[]>(this.url);
  }
}
