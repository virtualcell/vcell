// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.
import config from '../../auth_config.json';

const { domain, clientId, authorizationParams: { audience }, apiUri, errorPath } = config as {
  domain: string;
  clientId: string;
  authorizationParams: {
    audience?: string;
  },
  apiUri: string;
  errorPath: string;
};

export const environment = {
  production: false,
  auth: {
    domain,
    clientId,
    authorizationParams: {
      audience: `${audience}`,
      redirect_uri: window.location.origin,
    },
    errorPath,
  },
  apiUri: `${apiUri}`,
  httpInterceptor: {
    allowedList: [
      {
        // uri: `${config.apiUri}/api/*`,
        // uri: `${apiUri}/api/*`,
        // uri: '/api/*',
        uri: 'https://vcell-stage.cam.uchc.edu/api/*',

        // allowAnonymous: true,
        tokenOptions: {
          authorizationParams: {
            audience: `${audience}`,
            scope: 'openid profile email'
          }
        }
      },
      ],

  },
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
