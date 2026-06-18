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

  public totalTransactionsCount: number = 0;
  public limitTabella: number = 100;
  private fullSortedTransactions: Transaction[] = [];

  public activeMenuId: number | null = null;

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
        opacityFrom: 0.3,
        opacityTo: 0.5,
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
    this.sub = interval(10000)
      .pipe(
        startWith(0),
        switchMap(() => this.service.getTransactions())
      )
      .subscribe({
        next: (data) => {
          const sortedData = [...data].sort((a, b) =>
            new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()
          );

          sortedData.forEach(tran => {
            const exists = this.transactions.some(t => t.id === tran.id);
            if (!exists && tran.status === 'PENDING') {
              tran.isProcessing = true;
            }
          });

          this.fullSortedTransactions = sortedData;
          this.totalTransactionsCount = sortedData.length;

          const processedTransactions = sortedData.filter(tx => tx.status !== 'PENDING');

          this.updateTotals(processedTransactions);

          this.renderTabella();

          const rollingData = sortedData.slice(-50);
          const legitTx = rollingData.filter(tx => tx.status === 'CONFIRMED');
          const fraudTx = rollingData.filter(tx => tx.status === 'REJECTED');

          this.chartOptions.series = [
            {
              name: 'Transazioni Legittime',
              type: 'area',
              data: legitTx.map(tx => [new Date(tx.timestamp).getTime(), Number(tx.amount)])
            },
            {
              name: 'Tentativi di Frode',
              type: 'column',
              data: fraudTx.map(tx => [new Date(tx.timestamp).getTime(), Number(tx.amount)])
            }
          ];

          this.cdr.detectChanges();

          setTimeout(() => {
            this.fullSortedTransactions.forEach(t => {
              if (t.isProcessing) t.isProcessing = false;
            });
            this.renderTabella();
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

  private renderTabella() {
    this.transactions = [...this.fullSortedTransactions]
      .reverse()
      .slice(0, this.limitTabella);
  }

  public caricaAncora() {
    this.limitTabella += 100;
    this.renderTabella();
    this.cdr.detectChanges();
  }

  public toggleMenu(id: number) {
    this.activeMenuId = this.activeMenuId === id ? null : id;
  }

  public closeMenu() {
    this.activeMenuId = null;
  }

  private updateTotals(listaModello: Transaction[]) {
    this.totalAmount = listaModello.reduce((sum, tx) => sum + tx.amount, 0);
    this.averageAmount = listaModello.length > 0 ? this.totalAmount / listaModello.length : 0;
    this.pendingTransactions = listaModello.filter(tx => tx.status === 'PENDING');

    const rejectedCount = listaModello.filter(tx => tx.status === 'REJECTED').length;
    this.rejectionRate = listaModello.length > 0 ? (rejectedCount / listaModello.length) * 100 : 0;
  }

  public handleAction(transaction: Transaction, newStatus: 'CONFIRMED' | 'REJECTED' | 'PENDING') {
    this.closeMenu();

    transaction.isProcessing = true;

    this.service.updateTransactionStatus(transaction.id, newStatus).subscribe({
      next: () => {
        this.fullSortedTransactions = this.fullSortedTransactions.map(t => 
          t.id === transaction.id 
            ? { ...t, status: newStatus, manualOverride: (newStatus !== 'PENDING'), isProcessing: false }
            : t
        );

        const processedTransactions = this.fullSortedTransactions.filter(tx => tx.status !== 'PENDING');
        this.updateTotals(processedTransactions);

        this.renderTabella();

        const rollingData = this.fullSortedTransactions.slice(-50);
        const legitTx = rollingData.filter(tx => tx.status === 'CONFIRMED');
        const fraudTx = rollingData.filter(tx => tx.status === 'REJECTED');

        this.chartOptions.series = [
          {
            name: 'Transazioni Legittime',
            type: 'area',
            data: legitTx.map(tx => [new Date(tx.timestamp).getTime(), Number(tx.amount)])
          },
          {
            name: 'Tentativi di Frode',
            type: 'column',
            data: fraudTx.map(tx => [new Date(tx.timestamp).getTime(), Number(tx.amount)])
          }
        ];

        this.cdr.detectChanges();
      },
      error: (err) => {
        transaction.isProcessing = false;
        this.cdr.detectChanges();
        console.error('Errore nell\'aggiornamento:', err);
      }
    });
  }
  
  getRiskClass(score: number): string {
    if (score <= 0.15) return 'bg-green-100 text-green-700';
    if (score >= 0.75) return 'bg-red-100 text-red-700';
    return 'bg-yellow-100 text-yellow-700';
  }
}