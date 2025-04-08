
export const environment = {
  production: true,
  auth: {
    authorizationParams: {
      audience: `https://vcell-dev.cam.uchc.edu`,
    },
  },
  httpInterceptor: {
    allowedList: [ 'https://vcell-dev.cam.uchc.edu/api/*', ],
  },
};
