import { Component, inject, OnInit } from '@angular/core';
import { TransactionService } from '../../services/transaction.service';
import { Transaction } from '../../models/transaction.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit{
  private service = inject(TransactionService);
  public transactions: Transaction[] = [];

  ngOnInit() {
    this.service.getTransactions().subscribe((data) => {
      this.transactions = data;
      console.log(data);
    });
  }
}
