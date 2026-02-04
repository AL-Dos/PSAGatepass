import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ScanRequestDTO {
  qrToken: string;
  pin: string;
}

export interface ScanResponse {
  message: string;
  guard: string;
  action: string;
  gatepass: any;
}

@Injectable({
  providedIn: 'root',
})
export class GuardService {
  private http = inject(HttpClient);
  // Same-origin API base (works via Angular dev proxy and Nginx in prod)
  private baseUrl = `/api/guard`;

  constructor() {}

  scan(data: ScanRequestDTO): Observable<ScanResponse> {
    return this.http.post<ScanResponse>(`${this.baseUrl}/scan`, data);
  }
}
