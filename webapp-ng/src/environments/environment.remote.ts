
export const environment = {
  production: false,
  auth: {
    authorizationParams: {
      audience: `https://minikube.remote`,
    },
  },
  httpInterceptor: {
    allowedList: [ 'https://minikube.remote/api/*' ],
  },
};
