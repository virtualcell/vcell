import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {BASE_PATH} from "./core/modules/openapi";
import {provideHttpClient} from '@angular/common/http';
import {provideOAuthClient} from 'angular-oauth2-oidc';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    {provide: BASE_PATH, useValue: 'http://localhost:9000'},
    provideHttpClient(),
    provideOAuthClient(),
  ]
};
