import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ServidorC } from '../../../Servidor/Cliente/servidorC';
import { ActivatedRoute, Router } from '@angular/router';
import { Cliente } from '../../../Entidad/cliente';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-editar-c',
  imports: [FormsModule],
  templateUrl: './editar-c.html',
  styleUrl: './editar-c.css',
})
export class EditarC implements OnInit{

  // Creamos el constructor que inyecta el servicio Servidor
  constructor(private servicioC: ServidorC, private router: Router, private route: ActivatedRoute) { }

  // Creamos la instancia de la entidad Clientes
  cliente: Cliente = new Cliente(); //Intanciamos con Cliente
  loading = true; //Variable para indicar que si esta cargando
  error = '';

  ngOnInit(): void {
    
    //Obtenerl El iD de la tienda desde la ruta
    this.route.params.subscribe(params =>{
      const id = +params['id']; // El + convierte string a number
      if (id) {
        this.cargarCliente(id);
      } else {
        this.error = 'ID de cliente no válido';
        this.loading = false;
      }
    })
  }

  cargarCliente(id: number): void {
    this.servicioC.buscarCliente(id).subscribe({
      next: (Response) => {
        Swal.fire({
          title: 'Cargado...✓'
        });
        this.cliente = Response;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar el cliente:', err);
        this.error = 'Error al cargar los datos del cliente';
        this.loading = false;
      }
    });
  }

  btnEditarCliente(): void {
    Swal.fire({
      title: "¿Estás seguro de editar este cliente?",  
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, editar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.servicioC.actualizarCliente(this.cliente).subscribe({
          next: (response: any) => {
            Swal.fire({
              icon: 'success',
              title: 'Cliente editada', 
              text: response?.message || 'Cliente editada correctamente',
              showConfirmButton: false,
              timer: 1500
            }).then(() => {
              this.router.navigate(['listar-clientes']); 
            });
          },
          error: (err: any) => {  
            Swal.fire({
              icon: 'error',
              title: 'Error al editar el cliente',  
              text: err.error?.mensaje || 'Error desconocido',
              confirmButtonText: 'Aceptar'
            });
          }
        });
      }
    });
  }
}