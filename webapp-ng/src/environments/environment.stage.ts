
export const environment = {
  production: true,
  auth: {
    authorizationParams: {
      audience: `https://vcell-stage.cam.uchc.edu`,
    },
  },
  httpInterceptor: {
    allowedList: [ 'https://vcell-stage.cam.uchc.edu/api/*' ],
  },
};
