import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})

// Servidor de autenticación
export class ServidorAuth {

  // Declaramos las variables
  private username?: string; 
  private password?: string;

// Estado logueado (sincronizado con credenciales)
  private isLogged = false;

  // Establecer credenciales + marcar como logueado
  setCredentials(username: string, password: string) {
    this.username = username;
    this.password = password;
    this.isLogged = true;  // Login exitoso
  }

  //Limpiar TODO (logout completo)
  clearCredentials() {
    this.username = undefined;
    this.password = undefined;
    this.isLogged = false;  // Ya no está logueado
  }

  // Header Authorization (sin cambios)
  getAuthHeader() {
    if(!this.username || !this.password){
      return null;
    }
    const token = btoa(this.username + ':' + this.password);
    return 'Basic ' + token;
  }

  // Verificar login (usa estado interno)
  isLogeo(): boolean {
    return this.isLogged;  //Más confiable que checar credenciales
  }

  // Método público para componentes (opcional)
  logout() {
    this.clearCredentials();
  }
}