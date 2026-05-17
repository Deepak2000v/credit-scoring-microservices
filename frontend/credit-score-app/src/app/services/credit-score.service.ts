import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class CreditScoreService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({ Authorization: `Bearer ${this.authService.getToken()}` });
  }

  // POST /score/calculate → Gateway → CreditScoringServiceMS
  calculateScore(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/score/calculate`, data, { headers: this.getHeaders() });
  }

  // GET /score/{userId}?emailId=... → Gateway → CreditScoringServiceMS
  getScore(userId: number, emailId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/score/${userId}?emailId=${emailId}`, { headers: this.getHeaders() });
  }

  // GET /score/history/{userId} → Gateway → CreditScoringServiceMS
  getScoreHistory(userId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/score/history/${userId}`, { headers: this.getHeaders() });
  }

  // GET /score/average → Gateway → CreditScoringServiceMS
  getAverageScore(): Observable<any> {
    return this.http.get(`${this.apiUrl}/score/average`, { headers: this.getHeaders() });
  }

  // PUT /score/refresh → Gateway → CreditScoringServiceMS
  refreshScores(): Observable<any> {
    return this.http.put(`${this.apiUrl}/score/refresh`, {}, { headers: this.getHeaders() });
  }

  // DELETE /score/{userId} → Gateway → CreditScoringServiceMS
  deleteScore(userId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/score/${userId}`, { headers: this.getHeaders() });
  }
}
