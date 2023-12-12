import { Component } from '@angular/core';
import {CommonModule, NgIf} from '@angular/common';
import { RouterOutlet } from '@angular/router';
import {PublicationListComponentComponent} from "./publication-list-component/publication-list-component.component";
import {HttpClientModule} from "@angular/common/http";
import {OAuthService} from 'angular-oauth2-oidc';
import {filter} from 'rxjs/operators';
import {authCodeFlowConfig} from './auth.config';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, PublicationListComponentComponent, NgIf],
  templateUrl: './app.component.html',
  styleUrl: './app.component.less'
})
export class AppComponent {
  title = 'vcell-admin-ng';

  constructor(private oauthService: OAuthService) {
    this.oauthService.configure(authCodeFlowConfig);
    this.oauthService.loadDiscoveryDocumentAndLogin();

    //this.oauthService.setupAutomaticSilentRefresh();

    // Automatically load user profile
    this.oauthService.events
      .pipe(filter((e) => e.type === 'token_received'))
      .subscribe((_) => this.oauthService.loadUserProfile());
  }

  get userName(): string | null {
    const claims = this.oauthService.getIdentityClaims();
    if (!claims) return null;
    return claims['given_name'];
  }

  get idToken(): string {
    return this.oauthService.getIdToken();
  }

  get accessToken(): string {
    return this.oauthService.getAccessToken();
  }

  refresh() {
    this.oauthService.refreshToken();
  }
}
