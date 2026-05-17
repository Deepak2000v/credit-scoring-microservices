import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CreditScoreService } from '../../services/credit-score.service';

@Component({
  selector: 'app-credit-score',
  templateUrl: './credit-score.component.html',
  styleUrls: ['./credit-score.component.css']
})
export class CreditScoreComponent implements OnInit {
  calculateForm: FormGroup;
  fetchForm: FormGroup;
  historyForm: FormGroup;

  calculateResult: any = null;
  fetchResult: any = null;
  historyResult: any[] = [];
  averageScore: number | null = null;

  calcLoading = false;
  fetchLoading = false;
  historyLoading = false;

  calcError = '';
  fetchError = '';
  historyError = '';

  activeTab = 'calculate';

  scoreTypes = ['FICO', 'VantageScore', 'Experian', 'Equifax', 'TransUnion'];
  algorithms = ['StandardAlgo', 'MLAlgo', 'HybridAlgo'];

  constructor(private fb: FormBuilder, private creditScoreService: CreditScoreService) {
    this.calculateForm = this.fb.group({
      userId: ['', [Validators.required, Validators.min(1)]],
      emailId: ['', [Validators.required, Validators.email]],
      score: ['', [Validators.required, Validators.min(300), Validators.max(900)]],
      scoreType: ['FICO', Validators.required],
      algorithmUsed: ['StandardAlgo', Validators.required]
    });
    this.fetchForm = this.fb.group({
      userId: ['', [Validators.required, Validators.min(1)]],
      emailId: ['', [Validators.required, Validators.email]]
    });
    this.historyForm = this.fb.group({
      userId: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.loadAverage();
  }

  loadAverage() {
    this.creditScoreService.getAverageScore().subscribe({
      next: (res) => this.averageScore = res.averageScore,
      error: () => {}
    });
  }

  onCalculate() {
    if (this.calculateForm.invalid) return;
    this.calcLoading = true; this.calcError = ''; this.calculateResult = null;
    this.creditScoreService.calculateScore(this.calculateForm.value).subscribe({
      next: (res) => { this.calculateResult = res; this.calcLoading = false; this.loadAverage(); },
      error: (err) => { this.calcError = err?.error?.message || 'Failed to calculate score.'; this.calcLoading = false; }
    });
  }

  onFetch() {
    if (this.fetchForm.invalid) return;
    this.fetchLoading = true; this.fetchError = ''; this.fetchResult = null;
    const { userId, emailId } = this.fetchForm.value;
    this.creditScoreService.getScore(userId, emailId).subscribe({
      next: (res) => { this.fetchResult = res; this.fetchLoading = false; },
      error: (err) => { this.fetchError = 'No score found or error fetching.'; this.fetchLoading = false; }
    });
  }

  onHistory() {
    if (this.historyForm.invalid) return;
    this.historyLoading = true; this.historyError = ''; this.historyResult = [];
    this.creditScoreService.getScoreHistory(this.historyForm.value.userId).subscribe({
      next: (res) => { this.historyResult = res; this.historyLoading = false; },
      error: () => { this.historyError = 'Could not fetch score history.'; this.historyLoading = false; }
    });
  }

  onRefresh() {
    this.creditScoreService.refreshScores().subscribe({
      next: () => alert('Scores refreshed successfully!'),
      error: () => alert('Refresh triggered.')
    });
  }

  getScoreClass(score: number): string {
    if (score >= 750) return 'score-excellent';
    if (score >= 650) return 'score-good';
    return 'score-poor';
  }

  getScoreLabel(score: number): string {
    if (score >= 750) return 'Excellent';
    if (score >= 650) return 'Good';
    return 'Poor';
  }
}
