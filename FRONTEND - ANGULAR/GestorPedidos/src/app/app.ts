import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { ServidorAuth } from './Auth/servidor-auth';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('GestorPedidos');

  //Set para controlar múltiples menús abiertos simultáneamente
  //Puede contener varios valores de tipo string que representan los menús abiertos
  protected openMenus = new Set<string>();

  //Creamos un constructor para inicializar el servidor y inyectar las dependencias
  constructor(private router: Router,  private auth: ServidorAuth) {}

  //Método para alternar menús
  //Si ago clic en un menú y está abierto, lo cierra
  //Si ago clic en un menú y está cerrado, lo abre
  toggleMenu(menu: string) {
    if (this.openMenus.has(menu)) { //Si el menu esta abierto
      this.openMenus.delete(menu); //Lo cerramos
    } else {
      this.openMenus.add(menu); //Lo abrimos
    }
  }

  //Método para verificar si un menú está abierto
  isMenuOpen(menu: string): boolean {
    return this.openMenus.has(menu);
  }

  //Método para cerrar sesión
  logout() {
    this.auth.clearCredentials();  // Limpia username/password
    this.router.navigate(['/login']);  // Va al login
  }

  //Creamos los metodos de navegacion que se usaran en el template de app.html para navegar entre componentes

  //LISTAR+--------------------------------------------------------------------------------------------------------------------------------

  listarProductos() {
    this.router.navigate(['listar-productos']);
  }

  listarClientes() {
    this.router.navigate(['listar-clientes']);
  }

  listarPedidos(){
    this.router.navigate(['listar-pedidos']);
  }

  //GUARDAR+--------------------------------------------------------------------------------------------------------------------------------

  guardarProductos() {
    this.router.navigate(['guardar-producto']);
  }

  guardarClientes() {
    this.router.navigate(['guardar-cliente']);
  }

}
