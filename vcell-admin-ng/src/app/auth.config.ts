import { AuthConfig } from 'angular-oauth2-oidc';

export const authCodeFlowConfig: AuthConfig = {
  issuer: 'https://localhost:8543/realms/quarkus',
  // redirectUri: window.location.origin + '/index.html',
  redirectUri: window.location.origin + '/',
  clientId: 'backend-service',
  responseType: 'code',
  // scope: 'openid profile email offline_access api',
  scope: 'openid profile email offline_access microprofile-jwt',
  showDebugInformation: true,
  timeoutFactor: 0.01,
};
