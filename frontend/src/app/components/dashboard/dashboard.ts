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
          this.cdr.detectChanges(); // Forza il refresh della grafica
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