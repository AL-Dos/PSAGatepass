import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ScanRequestDTO {
  qrToken: string;
}

export interface ScanResponse {
  message: string;
  guard: string;
  action: string;
  gatepass: any;
}

export interface GuardLoginResponse {
  token: string;
  role: string;
}

@Injectable({
  providedIn: 'root',
})
export class GuardService {
  private http = inject(HttpClient);
  // Same-origin API base (works via Angular dev proxy and Nginx in prod)
  private baseUrl = `/api/guard`;
  private tokenKey = 'guardToken';
  private nameKey = 'guardName';

  constructor() {}

  login(name: string, pin: string): Observable<GuardLoginResponse> {
    return this.http.post<GuardLoginResponse>(`${this.baseUrl}/login`, { name, pin });
  }

  saveSession(name: string, token: string) {
    localStorage.setItem(this.nameKey, name);
    localStorage.setItem(this.tokenKey, token);
  }

  clearSession() {
    localStorage.removeItem(this.nameKey);
    localStorage.removeItem(this.tokenKey);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getName(): string | null {
    return localStorage.getItem(this.nameKey);
  }

  scan(data: ScanRequestDTO): Observable<ScanResponse> {
    const token = this.getToken();
    const headers = token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : undefined;
    return this.http.post<ScanResponse>(`${this.baseUrl}/scan`, data, { headers });
  }
}
