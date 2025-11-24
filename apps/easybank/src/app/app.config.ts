import {ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection,} from '@angular/core';
import {provideRouter} from '@angular/router';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {appRoutes} from './app.routes';
import {errorInterceptor} from './core';
import {
  provideKeycloak,
  includeBearerTokenInterceptor,
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  createInterceptorCondition
} from "keycloak-angular";

// Configure which URLs should receive the Bearer token
const localhostCondition = createInterceptorCondition({
  urlPattern: /^(http:\/\/localhost:8080)(\/.*)?$/i,
  bearerPrefix: 'Bearer'
});

console.log('Initializing EasyBank Application...');
console.log('Keycloak Config:', {
  url: 'http://localhost:8180/',
  realm: 'EasyBankDev',
  clientId: 'EasyBankPublicClient'
});

export const appConfig: ApplicationConfig = {
  providers: [
    provideKeycloak({
      config: {
        url: 'http://localhost:8180/',
        realm: 'EasyBankDev',
        clientId: 'EasyBankPublicClient'
      },
      initOptions: {
        pkceMethod: 'S256',
        onLoad: 'check-sso',
        redirectUri: 'http://localhost:4200/dashboard',
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html'
      },
      providers: [
        {
          provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
          useValue: [localhostCondition]
        }
      ]
    }),
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(appRoutes),
    provideHttpClient(
      withInterceptors([includeBearerTokenInterceptor, errorInterceptor])
    ),
  ],
};
