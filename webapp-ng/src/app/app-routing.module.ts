import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { ErrorComponent } from './pages/error/error.component';
import { AuthGuard } from '@auth0/auth0-angular';
import { PublicationListComponent } from './components/publication-list/publication-list.component';

const routes: Routes = [
  {
    path: 'app',
    children: [
      {
        path: 'profile',
        component: ProfileComponent,
        canActivate: [AuthGuard],
      },
      {
        path: 'publications',
        component: PublicationListComponent,
        canActivate: [AuthGuard],
      },
      {
        path: 'error',
        component: ErrorComponent,
      },
      {
        path: '',
        component: HomeComponent,
        pathMatch: 'full',
      },
    ]
  },
  {
    path: '',
    redirectTo: '/app',
    pathMatch: 'full',
  },
  {
    path: '**',
    redirectTo: '/app/error',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {})],
  exports: [RouterModule],
})
export class AppRoutingModule {}
