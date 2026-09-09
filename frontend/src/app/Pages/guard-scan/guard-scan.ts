import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Html5QrcodeScanner } from 'html5-qrcode';
import { GuardService } from '../../Services/guard/guard.service';

@Component({
  selector: 'app-guard-scan',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatSnackBarModule
  ],
  templateUrl: './guard-scan.html',
  styleUrls: ['./guard-scan.css']
})
export class GuardScan implements OnInit, OnDestroy {
  scanner: Html5QrcodeScanner | null = null;
  scannedToken = signal<string | null>(null);
  loading = signal<boolean>(false);
  result = signal<any>(null);
  cameraFacing = signal<'environment' | 'user'>('environment');

  constructor(
    private guardService: GuardService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.startScanner();
  }

  startScanner() {
    setTimeout(() => {
      // Clear existing scanner if present
      if (this.scanner) {
        try { this.scanner.clear(); } catch (e) { /* ignore */ }
        this.scanner = null;
      }

      const desiredFacing = this.cameraFacing();
      const config: any = {
        fps: 10,
        qrbox: { width: Math.min(480, Math.floor(window.innerWidth * 0.7)), height: Math.min(480, Math.floor(window.innerWidth * 0.7)) },
        disableFlip: false,
        // prefer facing mode; library will fall back if not available
        videoConstraints: { facingMode: { ideal: desiredFacing } }
      };

      this.scanner = new Html5QrcodeScanner(
        "reader",
        config,
        /* verbose= */ false
      );
      this.scanner.render(this.onScanSuccess.bind(this), this.onScanFailure.bind(this));
    }, 120);
  }

  stopScanner() {
    if (this.scanner) {
      this.scanner.clear().catch(() => {});
      this.scanner = null;
    }
  }

  toggleCamera() {
    // switch between environment (back) and user (front)
    const next = this.cameraFacing() === 'environment' ? 'user' : 'environment';
    this.cameraFacing.set(next);
    // restart scanner to apply new facing mode
    if (this.scanner) {
      this.scanner.clear().catch(() => {}).finally(() => this.startScanner());
    } else {
      this.startScanner();
    }
  }

  onScanSuccess(decodedText: string, decodedResult: any) {
    console.log(`Code matched = ${decodedText}`, decodedResult);
    // Extract token if it's a URL or just the token.
    // Backend generates a verification URL like "http://localhost:8080/api/verify/" + gatepass.getQrToken()
    // We need the token part.
    let token = decodedText.trim();
    if (decodedText.includes('/verify/')) {
        token = decodedText.split('/verify/')[1].split(/[?#]/)[0];
    }

    this.scannedToken.set(token);

    // Pause or clear scanner?
    this.scanner?.clear();
  }

  onScanFailure(error: any) {
    // frequent errors, ignore
  }

  resetScan() {
    this.scannedToken.set(null);
    this.result.set(null);
    this.startScanner();
  }

  submitScan() {
    if (!this.scannedToken()) return;

    this.loading.set(true);
    this.guardService.scan({
      qrToken: this.scannedToken()!
    }).subscribe({
      next: (res) => {
        this.loading.set(false);
        this.result.set(res);
        this.snackBar.open(res.message, 'Close', { duration: 5000 });
      },
      error: (err) => {
        this.loading.set(false);
        console.error(err);
        const msg = err.error || err.message || 'Error occurred';
        this.snackBar.open(msg, 'Close', { duration: 5000 });
      }
    });
  }

  ngOnDestroy(): void {
    this.stopScanner();
  }
}
