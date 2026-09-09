import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ScanRequestDTO {
  qrToken: string;
}

export interface ScanResponse {
  message: string;
  action: string;
  gatepass: any;
}

@Injectable({
  providedIn: 'root',
})
export class GuardService {
  private http = inject(HttpClient);
  private baseUrl = '/api/guard';

  scan(data: ScanRequestDTO): Observable<ScanResponse> {
    return this.http.post<ScanResponse>(`${this.baseUrl}/scan`, data);
  }
}
