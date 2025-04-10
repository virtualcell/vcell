
export const environment = {
  production: true,
  auth: {
    authorizationParams: {
      audience: `https://vcell.cam.uchc.edu`,
    },
  },
  httpInterceptor: {
    allowedList: [ 'https://vcell.cam.uchc.edu/api/*' ],
  },
};
