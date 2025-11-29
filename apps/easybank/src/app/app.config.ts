import {ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection,} from '@angular/core';
import {provideRouter} from '@angular/router';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {appRoutes} from './app.routes';
import {errorInterceptor} from './core';
import {
  createInterceptorCondition,
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  includeBearerTokenInterceptor,
  provideKeycloak
} from "keycloak-angular";
import {environment} from '../environments/environment';

// Configure which URLs should receive the Bearer token
// Dynamically create pattern based on environment API URL
const apiUrlPattern = new RegExp(`^(${environment.api.baseUrl.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})(/.*)?$`, 'i');

const apiCondition = createInterceptorCondition({
  urlPattern: apiUrlPattern,
  bearerPrefix: 'Bearer'
});

export const appConfig: ApplicationConfig = {
  providers: [
    provideKeycloak({
      config: {
        url: environment.keycloak.url,
        realm: environment.keycloak.realm,
        clientId: environment.keycloak.clientId
      },
      initOptions: {
        pkceMethod: 'S256',
        onLoad: 'check-sso',
        redirectUri: environment.keycloak.redirectUri,
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html'
      },
      providers: [
        {
          provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
          useValue: [apiCondition]
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
