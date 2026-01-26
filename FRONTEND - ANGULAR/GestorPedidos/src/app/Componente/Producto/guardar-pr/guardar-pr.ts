import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ServidorPr } from '../../../Servidor/Producto/servidorPr';
import { Producto } from '../../../Entidad/producto';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-guardar-pr',
  imports: [FormsModule],
  templateUrl: './guardar-pr.html',
  styleUrl: './guardar-pr.css',
})
export class GuardarPr implements OnInit{

  //Creamos un contructor para inicializar con sus inyecciones
  constructor (private router: Router, private servicePr: ServidorPr) { }

  Producto: Producto = new Producto() //Intancia de Producto
  Productos: Producto[] = [] //Creamos una lista para almacenar los productos
  // Array de estados posibles
  estadosPosibles = ['DISPONIBLE', 'INDISPONIBLE'];

  //Metodo de OnInit
  ngOnInit(): void {
      
  }
  
  //Boton de Guardar
  btnGuardardProducto(): void{
    this.servicePr.guardarProducto(this.Producto).subscribe({
      next: (response: any) =>{
      Swal.fire({
        title: 'Guardado',
        text: response.body.message || response.body || 'Productos guardada con éxito',
        icon: 'success',
        showConfirmButton: false
      }),
        this.router.navigate(['listar-productos']); //Navegamos a la lista de productos despues de guardar
      },
      error: (err) => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: err.error?.mensaje || 'Error desconocido', //Mostramos el error del backend
          confirmButtonText: 'Aceptar'
        });
      }
    });
  }

}

