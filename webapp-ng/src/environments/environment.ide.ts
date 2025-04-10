export const environment = {
  production: false,
  auth: {
    authorizationParams: {
      audience: `https://minikube.island`,
    },
  },
  httpInterceptor: {
    allowedList: [ 'https://minikube.island/api/*' ],
  },
};
