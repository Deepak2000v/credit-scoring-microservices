import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class FinancialDataService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({ Authorization: `Bearer ${this.authService.getToken()}` });
  }

  // POST /data → Gateway → CreditScoringServiceMS
  submitFinancialData(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/data`, data, { headers: this.getHeaders() });
  }

  // GET /data/{userId} → Gateway → CreditScoringServiceMS
  getFinancialData(userId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/data/${userId}`, { headers: this.getHeaders() });
  }
}
