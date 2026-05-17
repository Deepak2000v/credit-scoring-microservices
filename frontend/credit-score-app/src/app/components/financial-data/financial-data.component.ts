import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FinancialDataService } from '../../services/financial-data.service';

@Component({
  selector: 'app-financial-data',
  templateUrl: './financial-data.component.html',
  styleUrls: ['./financial-data.component.css']
})
export class FinancialDataComponent {
  submitForm: FormGroup;
  fetchUserId = '';
  submitResult: any = null;
  records: any[] = [];
  submitLoading = false;
  fetchLoading = false;
  submitError = '';
  fetchError = '';
  activeTab = 'submit';

  categories = ['Income', 'Utilities', 'Groceries', 'Entertainment', 'Loan Repayment', 'Healthcare', 'Transport', 'Other'];

  constructor(private fb: FormBuilder, private financialDataService: FinancialDataService) {
    this.submitForm = this.fb.group({
      userId: ['', [Validators.required, Validators.min(1)]],
      emailId: ['', [Validators.required, Validators.email]],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      transactionType: ['credit', Validators.required],
      transactionDescription: ['', Validators.required],
      category: ['Income', Validators.required]
    });
  }

  onSubmit() {
    if (this.submitForm.invalid) return;
    this.submitLoading = true; this.submitError = ''; this.submitResult = null;
    this.financialDataService.submitFinancialData(this.submitForm.value).subscribe({
      next: (res) => { this.submitResult = res; this.submitLoading = false; },
      error: () => { this.submitError = 'Failed to submit financial data.'; this.submitLoading = false; }
    });
  }

  onFetch() {
    if (!this.fetchUserId) return;
    this.fetchLoading = true; this.fetchError = ''; this.records = [];
    this.financialDataService.getFinancialData(+this.fetchUserId).subscribe({
      next: (res) => { this.records = res; this.fetchLoading = false; },
      error: () => { this.fetchError = 'Could not fetch records.'; this.fetchLoading = false; }
    });
  }

  getTypeClass(type: string): string {
    return type === 'credit' ? 'type-credit' : 'type-debit';
  }

  getCreditCount(): number {
    return this.records.filter(r => r.transactionType === 'credit').length;
  }

  getDebitCount(): number {
    return this.records.filter(r => r.transactionType === 'debit').length;
  }
}
