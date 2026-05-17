import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient, private router: Router) {}

  // POST /login → Gateway → UserManagementMS
  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, { username, password }).pipe(
      tap(res => {
        if (res.token) {
          localStorage.setItem('token', res.token);
          localStorage.setItem('username', username);
        }
      })
    );
  }

  // POST /register → Gateway → UserManagementMS
  register(data: { username: string; email: string; password: string; role: string }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register`, data);
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
