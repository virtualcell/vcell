import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, NgModule} from '@angular/core';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {HIGHLIGHT_OPTIONS, HighlightModule} from 'ngx-highlightjs';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {MatSortModule} from '@angular/material/sort';
import {MatPaginatorModule} from '@angular/material/paginator';
import {FormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatTableModule} from '@angular/material/table';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HomeComponent} from './pages/home/home.component';
import {ProfileComponent} from './pages/profile/profile.component';
import {ErrorComponent} from './pages/error/error.component';
import {NavBarComponent} from './components/nav-bar/nav-bar.component';
import {FooterComponent} from './components/footer/footer.component';
import {HeroComponent} from './components/hero/hero.component';
import {HomeContentComponent} from './components/home-content/home-content.component';
import {LoadingComponent} from './components/loading/loading.component';
import {AuthHttpInterceptor, AuthModule} from '@auth0/auth0-angular';
import { environment as env} from '../environments/environment';
import {PublicationListComponent} from './components/publication-list/publication-list.component';
import {PublicationEditComponent} from './components/publication-edit/publication-edit.component';
import {ApiModule, Configuration as ApiConfiguration} from "./core/modules/openapi";
import {VcellIdentityComponent} from "./components/vcell-identity/vcell-identity.component";
import {BaseuriConfigService} from "./config/baseuri-config.service";
import {BaseuriConfig} from "./config/baseuri-config";
import {MatCardModule} from "@angular/material/card";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {LoginSuccessComponent} from "./pages/login-success/login-success.component";
import auth_config from '../../auth_config.json';

const { domain, clientId, errorPath } = auth_config as {
  domain: string;
  clientId: string;
  errorPath: string;
};

export function apiConfigFactory() {
  return new ApiConfiguration({
    basePath: env.auth.authorizationParams.audience,
  });
}

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    ProfileComponent,
    NavBarComponent,
    FooterComponent,
    HeroComponent,
    HomeContentComponent,
    LoadingComponent,
    LoginSuccessComponent,
    PublicationListComponent,
    VcellIdentityComponent,
    ErrorComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    NgbModule,
    HighlightModule,
    FontAwesomeModule,
    AuthModule.forRoot({
      domain: auth_config.domain,
      clientId: auth_config.clientId,
      authorizationParams: {
        scope: 'openid profile email offline_access',
        audience: env.auth.authorizationParams.audience,
        redirect_uri: window.location.origin,
        prompt: "select_account"
      },
      errorPath: auth_config.errorPath,
      httpInterceptor: {
        allowedList: env.httpInterceptor.allowedList
      }
    }),
    BrowserAnimationsModule,
    FormsModule,
    MatSortModule,
    MatPaginatorModule,
    MatTableModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatButtonModule,
    ApiModule,
    MatCardModule,
    MatCheckboxModule,
    PublicationEditComponent,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthHttpInterceptor,
      multi: true,
    },
    {
      provide: Window,
      useValue: window,
    },
    {
      provide: HIGHLIGHT_OPTIONS,
      useValue: {
        coreLibraryLoader: () => import('highlight.js/lib/core'),
        languages: {
          json: () => import('highlight.js/lib/languages/json'),
        },
      },
    },
    {
      provide: ApiConfiguration,
      useFactory: apiConfigFactory,
      deps: [BaseuriConfigService]
    }

  ],
  bootstrap: [AppComponent],
  exports: [
    PublicationEditComponent
  ]
})
export class AppModule {}
