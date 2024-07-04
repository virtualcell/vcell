import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { AuthService } from '@auth0/auth0-angular';
import { Observable, throwError } from 'rxjs';
import { catchError, mergeMap } from 'rxjs/operators';
import { BaseuriConfigService } from './config/baseuri-config.service';

@Injectable()
export class CustomAuthInterceptor implements HttpInterceptor {

  constructor(private auth: AuthService, private baseuriConfigService: BaseuriConfigService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    const url = this.baseuriConfigService.config?.baseUri; // your API base url to check against
    const requiresToken = req.method === 'POST' || req.method === 'PUT' || req.method === 'GET' || req.method === 'DELETE';

    // Check the request url starts with your api url, and if request method is POST or PUT or GET or DELETE
    if (url && req.url.startsWith(url) && requiresToken) {
      // Get the access token and clone the request to add the Authorization header
      return this.auth.idTokenClaims$.pipe(
        mergeMap(token => {
          if (token) {
            const tokenReq = req.clone({
              setHeaders: {
                Authorization: `Bearer ${token}`,
              },
            });
            return next.handle(tokenReq);
          } else {
            // Handle the case where there is no token
            return next.handle(req);
          }
        }),
        catchError((error) => {
          return throwError(error);
        })
      );
    } else {
      // Just forward the request without adding the token
      return next.handle(req);
    }
  }
}
