import { Component, inject, OnDestroy, OnInit, ChangeDetectorRef } from '@angular/core';
import { TransactionService } from '../../services/transaction.service';
import { Transaction } from '../../models/transaction.model';
import { CommonModule } from '@angular/common';
import { interval, startWith, Subscription, switchMap } from 'rxjs';
import { NgApexchartsModule } from 'ng-apexcharts';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NgApexchartsModule],
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

 public chartOptions: any = {
    series: [],
    chart: {
      type: 'line',
      height: 350,
      toolbar: { show: false },
      animations: { enabled: false }
    },
    colors: ['#10b981', '#ef4444'],
    stroke: {
      width: [2, 1],
      curve: 'smooth'
    },
    fill: {
      type: ['gradient', 'solid'],
      gradient: {
        shadeIntensity: 1,
        opacityFrom: 0.4,
        opacityTo: 0.1,
        stops: [0, 100]
      }
    },
    dataLabels: { enabled: false },
    xaxis: {
      type: 'datetime',
      labels: {
        datetimeUTC: false,
        style: { colors: '#64748b' }
      }
    },
    yaxis: [
      {
        seriesName: 'Transazioni Legittime',
        title: { 
          text: 'Legittime (€)',
          style: { color: '#10b981' }
        },
        labels: {
          formatter: (val: number) => `€${val.toFixed(0)}`,
          style: { colors: '#10b981' }
        },
        min: 0,
        forceNiceScale: true
      },
      {
        opposite: true,
        seriesName: 'Tentativi di Frode',
        title: { 
          text: 'Frodi (€)',
          style: { color: '#ef4444' }
        },
        labels: {
          formatter: (val: number) => `€${val.toFixed(0)}`,
          style: { colors: '#ef4444' }
        },
        min: 0,
        forceNiceScale: true
      }
    ],
    tooltip: {
      shared: true,
      intersect: false,
      x: { format: 'dd/MM/yy HH:mm:ss' }
    },
    grid: { borderColor: '#f1f5f9' }
  };

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
          const sortedData = [...data].sort((a, b) =>
            new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()
          );

          // 2. Marca le nuove transazioni
          sortedData.forEach(tran => {
            const exists = this.transactions.some(t => t.id === tran.id);
            if (!exists && tran.status === 'PENDING') {
              tran.isProcessing = true;
            }
          });

          // 3. Sovrascrivi l'array della classe
          this.transactions = sortedData;

          this.updateTotals();

          const rollingData = sortedData.slice(-50); // Torniamo a 50 transazioni scorrevoli

          const legitTx = rollingData.filter(tx => tx.status !== 'REJECTED');
          const fraudTx = rollingData.filter(tx => tx.status === 'REJECTED');

          this.chartOptions.series = [
            {
              name: 'Transazioni Legittime',
              type: 'area', 
              data: legitTx.map(tx => [
                new Date(tx.timestamp).getTime(),
                Number(tx.amount)
              ])
            },
            {
              name: 'Tentativi di Frode',
              type: 'column', // <-- LA MAGIA È QUI
              data: fraudTx.map(tx => [
                new Date(tx.timestamp).getTime(),
                Number(tx.amount)
              ])
            }
          ];

          this.cdr.detectChanges();

          setTimeout(() => {
            this.transactions.forEach(t => {
              if (t.isProcessing) {
                t.isProcessing = false;
              }
            });
            // Forza la sparizione degli spinner e l'apparizione dei bottoni
            this.cdr.detectChanges();
          }, 1000);
        },
        error: (err) => {
          console.error('ERRORE NEL FLUSSO:', err);
        }
      });
  }

  ngOnDestroy() {
    this.sub?.unsubscribe();
  }

  private updateTotals() {
    this.totalAmount = this.transactions.reduce((sum, tx) => sum + tx.amount, 0);
    this.averageAmount = this.transactions.length > 0 ? this.totalAmount / this.transactions.length : 0;
    this.pendingTransactions = this.transactions.filter(tx => tx.status === 'PENDING');

    const rejectedCount = this.transactions.filter(tx => tx.status === 'REJECTED').length;
    this.rejectionRate = this.transactions.length > 0 ? (rejectedCount / this.transactions.length) * 100 : 0;
  }

  updateTransactionStatus(transaction: Transaction, newStatus: 'CONFIRMED' | 'REJECTED') {
    transaction.isProcessing = true;

    this.service.updateTransactionStatus(transaction.id, newStatus).subscribe({
      next: () => {
        transaction.status = newStatus;
        transaction.isProcessing = false;
        this.updateTotals();
        this.cdr.detectChanges();
      },
      error: (err) => {
        transaction.isProcessing = false;
        console.error(err);
      }
    });
  }

  getRiskClass(score: number): string {
    if (score <= 0.15) return 'bg-green-100 text-green-700';
    if (score >= 0.75) return 'bg-red-100 text-red-700';
    return 'bg-yellow-100 text-yellow-700';
  }

}