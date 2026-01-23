import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ServidorC } from '../../../Servidor/Cliente/servidorC';
import { Cliente } from '../../../Entidad/cliente';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-guardar-c',
  imports: [FormsModule],
  templateUrl: './guardar-c.html',
  styleUrl: './guardar-c.css',
})
export class GuardarC implements OnInit{

  //Contructor para inicializar con sus inyecciones
  constructor (private router: Router, private servicioC: ServidorC) {}

  
  Cliente: Cliente = new Cliente() //Intancia de cliente
  cliente: Cliente[] = [] //Creamos una lista para almacenar los clientes

  // Array de estados posibles
  estadosPosibles = ['ACTIVO', 'INACTIVO'];

  //Metodo de OnInit
  ngOnInit(): void {
    
  }

  //Boton de Guardar
  btnGuardarCliente(): void{
    this.servicioC.guardarCliente(this.Cliente).subscribe({
      next: (response: any) =>{
      Swal.fire({
        title: 'Guardado...✓',
        text: response.body.message || 'Tienda guardada con éxito',
        icon: 'success',
        showConfirmButton: false
      }),
        this.router.navigate(['listar-clientes']); //Navegamos a la lista de clientes despues de guardar
      },
      error: (err) => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: err.error?.error || 'Error desconocido', //Mostramos el error del backend
          confirmButtonText: 'Aceptar'
        });
      }
    });
    }
  }
