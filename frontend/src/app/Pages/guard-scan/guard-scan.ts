import { Component, OnDestroy, OnInit, signal, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Html5QrcodeScanner } from 'html5-qrcode';
import { GuardService } from '../../Services/guard/guard.service';

@Component({
  selector: 'app-guard-scan',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatCardModule,
    MatSnackBarModule
  ],
  templateUrl: './guard-scan.html',
  styleUrls: ['./guard-scan.css']
})
export class GuardScan implements OnInit, OnDestroy {
  scanner: Html5QrcodeScanner | null = null;
  scannedToken = signal<string | null>(null);
  pin = signal<string>('');
  loading = signal<boolean>(false);
  result = signal<any>(null);
  cameraFacing = signal<'environment' | 'user'>('environment');
  showPassword = signal<boolean>(false);

  constructor(
    private guardService: GuardService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // Initialize scanner after view init logic if needed, but here simple init
    this.startScanner();
  }

  startScanner() {
    // Timeout to ensure DOM is ready if needed
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
    // Backend generates "http://localhost:4200/verify/" + gatepass.getQrToken()
    // We need the token part.
    let token = decodedText;
    if (decodedText.includes('/verify/')) {
        token = decodedText.split('/verify/')[1];
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
    this.pin.set('');
    this.result.set(null);
    this.startScanner();
  }

  submitPin() {
    if (!this.scannedToken() || !this.pin()) return;

    this.loading.set(true);
    this.guardService.scan({
      qrToken: this.scannedToken()!,
      pin: this.pin()
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
    if (this.scanner) {
      this.scanner.clear().catch(err => console.error("Failed to clear scanner", err));
    }
  }
}
