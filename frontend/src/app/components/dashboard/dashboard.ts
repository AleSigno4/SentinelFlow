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
    series: [
      {
        name: 'Importo Transazioni',
        data: []
      }
    ],
    chart: {
      type: 'area',
      height: 350,
      animations: {
        enabled: true,
        easing: 'linear',
        dynamicAnimation: {
          speed: 1000
        }
      },
      toolbar: {
        show: false
      }
    },
    colors: ['#0891b2'],
    dataLabels: {
      enabled: false
    },
    stroke: {
      curve: 'smooth',
      width: 3
    },
    fill: {
      type: 'gradient',
      gradient: {
        shadeIntensity: 1,
        opacityFrom: 0.7,
        opacityTo: 0.2,
        stops: [0, 90, 100]
      }
    },
    xaxis: {
      type: 'datetime',
      labels: {
        datetimeUTC: false,
        style: {
          colors: '#64748b'
        }
      }
    },
    yaxis: {
      labels: {
        formatter: (val: number) => `€${val.toFixed(2)}`,
        style: {
          colors: '#64748b'
        }
      },
      min: 0,
      forceNiceScale: true
    },
    tooltip: {
      x: {
        format: 'dd/MM/yy HH:mm:ss'
      }
    },
    grid: {
      borderColor: '#f1f5f9'
    }
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

          //console.log('Dati ricevuti con successo:', data);
          this.transactions = [...data];
          this.totalAmount = this.transactions.reduce((sum, tx) => sum + tx.amount, 0);
          this.averageAmount = this.transactions.length > 0 ? this.totalAmount / this.transactions.length : 0;
          this.pendingTransactions = this.transactions.filter(tx => tx.status === 'PENDING');
          const rejectedCount = this.transactions.filter(tx => tx.status === 'REJECTED').length;
          this.rejectionRate = this.transactions.length > 0 ? (rejectedCount / this.transactions.length) * 100 : 0;

          this.chartOptions.series = [{
            name: 'Importo Transazione',
            data: sortedData.map(tx => [
              new Date(tx.timestamp).getTime(),
              Number(tx.amount)
            ])
          }];

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