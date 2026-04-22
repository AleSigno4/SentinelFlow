import { Component, inject, OnDestroy, OnInit, ChangeDetectorRef } from '@angular/core';
import { TransactionService } from '../../services/transaction.service';
import { Transaction } from '../../models/transaction.model';
import { CommonModule } from '@angular/common';
import { interval, startWith, Subscription, switchMap } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit, OnDestroy {
  private service = inject(TransactionService);
  private cdr = inject(ChangeDetectorRef);
  
  public transactions: Transaction[] = [];
  public totalAmount: number = 0;
  public averageAmount: number = 0;
  public pendingTransactions: Transaction[] = [];
  public rejectionRate: number = 0;
  private sub: Subscription | undefined;
  
  ngOnInit() {
    this.sub = interval(5000)
      .pipe(
        startWith(0),           
        switchMap(() => {
          return this.service.getTransactions();
        })
      )
      .subscribe({
        next: (data) => {
          //console.log('Dati ricevuti con successo:', data);
          this.transactions = [...data];
          this.totalAmount = this.transactions.reduce((sum, tx) => sum + tx.amount, 0);
          this.averageAmount = this.transactions.length > 0 ? this.totalAmount / this.transactions.length : 0;
          this.pendingTransactions = this.transactions.filter(tx => tx.status === 'PENDING');
          const rejectedCount = this.transactions.filter(tx => tx.status === 'REJECTED').length;
          this.rejectionRate = this.transactions.length > 0 ? (rejectedCount / this.transactions.length) * 100 : 0;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('ERRORE NEL FLUSSO:', err);
        }
      });
  }

  ngOnDestroy() {
    this.sub?.unsubscribe();
  }
}