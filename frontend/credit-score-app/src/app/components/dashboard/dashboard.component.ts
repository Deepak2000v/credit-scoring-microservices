import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CreditScoreService } from '../../services/credit-score.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  username = '';
  averageScore: number | null = null;
  loading = false;

  cards = [
    { icon: '📊', title: 'Credit Score', desc: 'Calculate & view credit scores for clients', route: '/credit-score', color: '#1a237e' },
    { icon: '💰', title: 'Financial Data', desc: 'Submit & manage financial transaction records', route: '/financial-data', color: '#1b5e20' },
  ];

  stats = [
    { icon: '🏦', label: 'Microservices', value: '3 Active' },
    { icon: '🔐', label: 'Security', value: 'JWT + OAuth2' },
    { icon: '⚡', label: 'Cache', value: 'Redis' },
    { icon: '📨', label: 'Messaging', value: 'Kafka' },
  ];

  constructor(public authService: AuthService, private creditScoreService: CreditScoreService) {}

  ngOnInit(): void {
    this.username = this.authService.getUsername() || 'Officer';
    this.loadAverageScore();
  }

  loadAverageScore() {
    this.loading = true;
    this.creditScoreService.getAverageScore().subscribe({
      next: (res) => { this.averageScore = res.averageScore; this.loading = false; },
      error: () => { this.averageScore = null; this.loading = false; }
    });
  }

  getScoreColor(score: number): string {
    if (score >= 750) return '#2e7d32';
    if (score >= 650) return '#f57f17';
    return '#c62828';
  }

  getScoreLabel(score: number): string {
    if (score >= 750) return 'Excellent';
    if (score >= 650) return 'Good';
    return 'Needs Improvement';
  }
}
