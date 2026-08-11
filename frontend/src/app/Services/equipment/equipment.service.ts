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
    purpose: string;
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

  exportLogs(equipmentIds: number[]) {
    return this.http.post(`${this.apiUrl}/logs/export`, { equipmentIds }, {
      withCredentials: true,
      responseType: 'blob'
    });
  }

  releaseEquipment(equipmentIds: number[]) {
    return this.http.post(`${this.apiUrl}/admin/release`, { equipmentIds }, { withCredentials: true });
  }

  returnEquipment(equipmentIds: number[]) {
    return this.http.post(`${this.apiUrl}/admin/return`, { equipmentIds }, { withCredentials: true });
  }

  deleteEquipment(equipmentIds: number[]) {
    return this.http.post(`${this.apiUrl}/admin/delete`, { equipmentIds }, { withCredentials: true });
  }

  archiveEquipment(equipmentIds: number[]) {
    return this.http.post(`${this.apiUrl}/admin/archive`, { equipmentIds }, { withCredentials: true });
  }

  unarchiveEquipment(equipmentIds: number[]) {
    return this.http.post(`${this.apiUrl}/admin/unarchive`, { equipmentIds }, { withCredentials: true });
  }

  getArchivedEquipment() {
    return this.http.get<any[]>(`${this.apiUrl}/admin/archived`, { withCredentials: true });
  }
}
