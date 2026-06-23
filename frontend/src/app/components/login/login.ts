import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TransactionService } from '../../services/transaction.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  isLoading: boolean = false;
  errorMessage: string | null = null;

  private transactionService = inject(TransactionService);
  private router = inject(Router);

  onLogin() {
    this.isLoading = true;
    this.errorMessage = null;

    this.transactionService.login(this.username, this.password).subscribe({
      next: (response: any) => {
        console.log('Login successful, token:', response.token);
        localStorage.setItem('token', response.token);
        console.log('Token saved to localStorage');

        // Prova a fare refresh della pagina
        setTimeout(() => {
          window.location.href = '/dashboard';
        }, 500);
      },
      error: (error: any) => {
        this.errorMessage = 'Invalid username or password.';
        this.isLoading = false;
      }
    });
  }
}