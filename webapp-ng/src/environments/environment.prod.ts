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
  production: true,
  auth: {
    domain,
    clientId,
    authorizationParams: {
      audience: 'https://vcellapi.cam.uchc.edu',
      redirect_uri: window.location.origin,
    },
    errorPath,
  },
  apiUri: 'https://vcell-stage.cam.uchc.edu',
  httpInterceptor: {
    allowedList: [
      {
        // uri: `${config.apiUri}/api/*`,
        uri: 'https://vcell-stage.cam.uchc.edu/api/*',
        // allowAnonymous: true,
        tokenOptions: {
          authorizationParams: {
            audience: 'https://vcellapi.cam.uchc.edu',
            scope: 'openid profile email'
          }
        }
      },
      ],

  },
};
