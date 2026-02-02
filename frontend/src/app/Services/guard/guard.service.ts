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
  // Dynamically determine backend URL (assuming same host, port 8090)
  private baseUrl = `http://${window.location.hostname}:8090/api/guard`;

  constructor() {}

  scan(data: ScanRequestDTO): Observable<ScanResponse> {
    return this.http.post<ScanResponse>(`${this.baseUrl}/scan`, data);
  }
}
