import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ServidorPr } from '../../../Servidor/Producto/servidorPr';
import { ActivatedRoute, Router } from '@angular/router';
import { Producto } from '../../../Entidad/producto';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-editar-pr',
  imports: [FormsModule],
  templateUrl: './editar-pr.html',
  styleUrl: './editar-pr.css',
})
export class EditarPr implements OnInit{
  
  // Creamos el constructor que inyecta el servicio Servidor
  constructor(private servicioPr: ServidorPr, private router: Router, private route: ActivatedRoute) { }

  // Creamos la instancia de la entidad Productos
  producto: Producto = new Producto(); //Intanciamos Producto
  loading = true; //Variable para indicar que si esta cargando
  error = '';

  ngOnInit(): void {
    
    //Obtenerl El iD de la tienda desde la ruta
    this.route.params.subscribe(params =>{
      const id = +params['id']; // El + convierte string a number
      if (id) {
        this.cargarProductos(id);
      } else {
        this.error = 'ID del producto no válido';
        this.loading = false;
      }
    })
  }

  cargarProductos(id: number): void {
    this.servicioPr.buscarProducto(id).subscribe({
      next: (Response) => {
        Swal.fire({
          title: 'Cargado...✓'
        });
        this.producto = Response;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar el producto:', err);
        this.error = 'Error al cargar los datos del producto';
        this.loading = false;
      }
    });
  }

  btnEditarProducto(): void {
    Swal.fire({
      title: "¿Estás seguro de editar este producto?",  // ← Corregido texto
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, editar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.servicioPr.actualizarProducto(this.producto).subscribe({
          next: (response: any) => {
            Swal.fire({
              icon: 'success',
              title: 'Producto editado',  // ← Corregido typo
              text: response?.message || 'Producto editada correctamente',
              showConfirmButton: false,
              timer: 1500
            }).then(() => {
              this.router.navigate(['listar-productos']);  // ← DENTRO del success
            });
          },
          error: (err: any) => {  // ← CORREGIDO: fuera de then()
            Swal.fire({
              icon: 'error',
              title: 'Error al editar el producto',  
              text: err.error?.error || 'Error desconocido',
              confirmButtonText: 'Aceptar'
            });
          }
        });
      }
    });
  }
}
