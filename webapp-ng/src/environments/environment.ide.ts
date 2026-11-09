// `npm run start_ide`: `ng serve` against a local minikube island deployment.
export const environment: { production: boolean; siteOrigin?: string } = {
  production: false,
  siteOrigin: 'https://minikube.island',
};
