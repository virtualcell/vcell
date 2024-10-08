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
        uri: 'https://minikube-remote/api/*',

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
