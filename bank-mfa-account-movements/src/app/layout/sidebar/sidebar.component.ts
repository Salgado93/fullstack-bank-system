import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

interface MenuItem {
  label: string;
  route: string;
  icon: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss',
})
export class SidebarComponent {
  readonly menuItems: MenuItem[] = [
    { label: 'Clientes', route: '/clientes', icon: '👤' },
    { label: 'Cuentas', route: '/cuentas', icon: '💳' },
    { label: 'Movimientos', route: '/movimientos', icon: '💰' },
    { label: 'Reportes', route: '/reportes', icon: '📊' },
  ];
}
