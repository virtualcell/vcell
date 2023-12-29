import { Injectable } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthorizationService {
  constructor(private auth: AuthService) {}

  public isCurator(): Observable<boolean> {
    return this.auth.idTokenClaims$.pipe(
      map(claims => claims ? claims['vcellapi.cam.uchc.edu/roles'].includes('curator') : false)
    );
  }

  public isOwner(): Observable<boolean> {
    return this.auth.idTokenClaims$.pipe(
      map(claims => claims ? claims['vcellapi.cam.uchc.edu/roles'].includes('owner') : false)
    );
  }

  public isUser(): Observable<boolean> {
    return this.auth.isAuthenticated$;
  }
}