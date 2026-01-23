import { HttpInterceptor, HttpInterceptorFn } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ServidorAuth } from './servidor-auth';

// Función para interceptar las solicitudes
export const authBasicInterceptor: HttpInterceptorFn = (req, next) => { // La constante authBasicInterceptor es para poder interceptar las solicitudes
  const servidorAuth = inject(ServidorAuth);  // Inyectamos el ServidorAuth mediante la función inject sin usar el constructor
  const authHeader = servidorAuth.getAuthHeader(); // Obtenemos el encabezado de autenticación mendiante la instancia del ServidorAuth
  if (!authHeader) { // Si no hay un encabezado de autenticación
    return next(req); // Retornamos la solicitud que no ha sido interceptada
  }
  const authReq = req.clone({setHeaders: {Authorization: authHeader,}, }); // Creamos una copia de la solicitud con el encabezado de autenticación
  return next(authReq); // Retornamos la solicitud con el encabezado de autenticación
}
