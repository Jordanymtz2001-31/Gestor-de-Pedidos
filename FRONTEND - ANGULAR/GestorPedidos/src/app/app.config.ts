import { ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authBasicInterceptor } from './Auth/interceptor';
import { FormsModule } from '@angular/forms';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(), // Manejador de errores globales
    provideRouter(routes), // Proveedor de Router
    provideZoneChangeDetection({eventCoalescing: true}), // Detección de cambios optimizada
    importProvidersFrom(FormsModule), // Importación de FormsModule para formularios
    provideHttpClient(withInterceptors([authBasicInterceptor])), // Añadimos el interceptor que creamos y Proveedor de HttpClient para solicitudes HTTP
  ]
};
