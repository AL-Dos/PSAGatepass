import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

export interface Equipment {
  id: number;
  equipmentName: string;
  quantity: number;
  equipmentCode: string;
  requestor: {
    id: number;
    name: string;
    destination: string;
    period: string;
  };
}

@Injectable({ providedIn: 'root' })
export class EquipmentService {
  private apiUrl = '/api';

  constructor(private http: HttpClient) {}

  getAllEquipment() {
    return this.http.get<any[]>(`${this.apiUrl}/requestors`, {
      withCredentials: true
    });
  }
}
