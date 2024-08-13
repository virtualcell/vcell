import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { ErrorComponent } from './pages/error/error.component';
import { AuthGuard } from '@auth0/auth0-angular';
import { PublicationListComponent } from './components/publication-list/publication-list.component';
import {LoginSuccessComponent} from "./pages/login-success/login-success.component";
import {AdminComponent} from "./pages/admin/admin.component";
import {PublicationNewComponent} from "./components/publication-new/publication-new.component";
import {PublicationDetailComponent} from "./components/publication-detail/publication-detail.component";

const routes: Routes = [
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [AuthGuard],
  },
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
    path: 'publications/new',
    component: PublicationNewComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'publications/:pubId',
    component: PublicationDetailComponent,
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
  {
    path: 'login_success',
    component: LoginSuccessComponent,
    canActivate: [AuthGuard],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {})],
  exports: [RouterModule],
})
export class AppRoutingModule {}
