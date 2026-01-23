import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ServidorP } from '../../../Servidor/PedidoYDetalles/servidorP';
import { Pedido } from '../../../Entidad/pedido';
import Swal from 'sweetalert2';
import { ServidorC } from '../../../Servidor/Cliente/servidorC';
import { Cliente } from '../../../Entidad/cliente';

@Component({
  selector: 'app-guardar-p',
  imports: [FormsModule],
  templateUrl: './guardar-p.html',
  styleUrl: './guardar-p.css',
})
export class GuardarP implements OnInit{

  //Contructor para inicializar con sus inyecciones
  constructor (private router: Router, private servicioP: ServidorP, private servicioC: ServidorC, private changeDetectorRef: ChangeDetectorRef) {}

  
  Pedido: Pedido = new Pedido() //Intancia de pedido
  pedidos: Pedido[] = [] //Creamos una lista para almacenar los pedidos

  loading: boolean = false;//Bandera que indica si hay una operación en curso (cargando datos o guardando)
  clientes: Cliente[] = [];  // Lista de clientes

  //Metodo de OnInit
  ngOnInit(): void {

    this.cargarClientes(); //Cargamos los clientes
    
  }

  // Metodo para cargar los clientes
  cargarClientes() {
    this.loading = true; // Activar el estado de carga
    this.servicioC.ListarClientes().subscribe({
      //Se suscribe al Observable para recibir la respuesta
      next: (data) => {
        this.clientes = data;//Guarda los datos en el array
        this.loading = false;//Desactiva el estado de carga
        this.changeDetectorRef.detectChanges();// Fuerza a Angular a actualizar la vista
      },
      error: (err) => {
        console.error('Error al cargar los clientes', err);
        this.loading = false;
        //Muestra error en consola.Desactiva el estado de carga
      }
    });
  }

  //Boton de Guardar
  btnGuardar(): void{
    this.servicioP.guardarPedido(this.Pedido).subscribe({
      next: (response: any) =>{
      Swal.fire({
        title: 'Guardado...✓',
        text: response.body.message || 'Pedido guardado con éxito',
        icon: 'success',
        showConfirmButton: false
      }),
        this.router.navigate(['listar-pedidos']); //Navegamos a la lista de Pedidos despues de guardar
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