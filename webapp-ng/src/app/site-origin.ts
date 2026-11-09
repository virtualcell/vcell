import { environment } from '../environments/environment';

/**
 * The origin of the VCell site this page was served from, e.g. https://vcell.cam.uchc.edu.
 *
 * The webapp and the VCell API share one host on every deployment (the ingress routes /api/* to the API
 * services), so the API base path, the Auth0 audience and the origin the Auth0 interceptor attaches tokens to
 * are all this one value. Reading it from the page, rather than compiling it in, lets one webapp image serve
 * every site. It is read synchronously at module load, as `redirect_uri` already was, so it is available to
 * the static AuthModule.forRoot configuration; a value fetched in an APP_INITIALIZER is not (that is why the
 * 2024 runtime-config attempt led to one image per site).
 *
 * `environment.siteOrigin` overrides it for `ng serve`, where the page comes from localhost but the API does not.
 */
export const siteOrigin: string = environment.siteOrigin ?? window.location.origin;
