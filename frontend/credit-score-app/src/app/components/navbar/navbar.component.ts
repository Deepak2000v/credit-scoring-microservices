import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  constructor(public authService: AuthService, private router: Router) {}

  logout() {
    this.authService.logout();
  }

  isActive(route: string): boolean {
    return this.router.url === route;
  }
}
