import { Routes } from '@angular/router';
import { ListarP } from './Componente/Pedido/listar-p/listar-p';
import { ListasC } from './Componente/Cliente/listas-c/listas-c';
import { ListarPr } from './Componente/Producto/listar-pr/listar-pr';
import { GuardarPr } from './Componente/Producto/guardar-pr/guardar-pr';
import { GuardarC } from './Componente/Cliente/guardar-c/guardar-c';
import { EditarPr } from './Componente/Producto/editar-pr/editar-pr';
import { EditarC } from './Componente/Cliente/editar-c/editar-c';
import { Login } from './Componente/Loging/login/login';
import { GuardarP } from './Componente/Pedido/guardar-p/guardar-p';
import { ListarPC } from './Componente/Pedido/listar-pc/listar-pc';

export const routes: Routes = [

    // Ruta principal VACIÍA → LOGIN
    { path: '', redirectTo: '/login', pathMatch: 'full' },

    // Login público
    { path: 'login', component: Login },
    //Aqui definimos las rutas de la aplicación que utilizaremos en la navegación entre componentes de la aplicación

    //LSITAR------------------------------------------------------------------------------------------------------------------
    {path: 'listar-productos', component: ListarPr},
    {path: 'listar-clientes', component: ListasC},
    {path: 'listar-pedidos', component: ListarP},
    {path: 'listar-pedidos-cancelados', component: ListarPC},
    

    //GUARDAR------------------------------------------------------------------------------------------------------------------
    {path: 'guardar-producto', component: GuardarPr},
    {path: 'guardar-cliente', component: GuardarC},
    {path: 'guardar-pedido', component: GuardarP},

    //EDITAR------------------------------------------------------------------------------------------------------------------
    {path: 'editar-producto/:id', component: EditarPr},
    {path: 'editar-cliente/:id', component: EditarC}

    
];
