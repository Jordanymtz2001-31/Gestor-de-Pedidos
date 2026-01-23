import { Component } from '@angular/core';
import { ServidorAuth } from '../../../Auth/servidor-auth';
import { ServidorC } from '../../../Servidor/Cliente/servidorC';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  // Variables
  username = '';
  password = '';
  error?: string;
  loading = false;

  //Constructor para inyectar las dependencias
  constructor(
    private auth: ServidorAuth, private ServiceCl: ServidorC, private router: Router
  ) {}

  onSubmit() {
    // Validar que los campos no esten vacios
    if (!this.username || !this.password) {
      this.error = 'Complete todos los campos';
      return;
    }

    // Si no estan vacios entonces se inicia la carga
    this.loading = true;
    this.error = undefined; // Limpiar el error

    // Guardar credenciales
    this.auth.setCredentials(this.username, this.password);

    // Intentar iniciar sesión Con las credenciales para todos los servicios de Cliente
    this.ServiceCl.ListarClientes()
      .subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['listar-clientes']);
        },
        error: () => {
          this.loading = false;
          this.error = 'Usuario o contraseña incorrectos';
          this.auth.clearCredentials();
        }
      });
  }
}
