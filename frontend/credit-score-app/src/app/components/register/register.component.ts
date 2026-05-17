import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;
  error = '';
  success = '';
  loading = false;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['ROLE_USER']
    });
  }

  onSubmit() {
    if (this.registerForm.invalid) return;
    this.loading = true;
    this.error = '';
    this.success = '';
    this.authService.register(this.registerForm.value).subscribe({
      next: (res) => {
        this.success = 'Registration successful! Redirecting to login...';
        this.loading = false;
        setTimeout(() => this.router.navigate(['/login']), 1800);
      },
      error: (err) => {
        this.error = err?.error?.message || 'Registration failed. Try again.';
        this.loading = false;
      }
    });
  }
}
