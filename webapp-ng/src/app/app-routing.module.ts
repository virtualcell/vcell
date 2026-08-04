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
    // Phase 2 feasibility spike — vtk.js rendering of a VCell-style .vti (no auth for easy testing).
    // Lazy-loaded so vtk.js (~1 MB) is code-split out of the initial bundle and only fetched here.
    path: 'vtk-spike',
    loadComponent: () => import('./pages/vtk-spike/vtk-spike.component').then((m) => m.VtkSpikeComponent),
  },
  {
    // vtk.wasm field viewer — loads the custom VTK-compiled-to-wasm bundle (same-origin from
    // /assets/vtk-wasm, placed there at build time) and renders through a standalone session.
    path: 'vtk-wasm',
    loadComponent: () => import('./pages/vtk-wasm-viewer/vtk-wasm-viewer.component').then((m) => m.VtkWasmViewerComponent),
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
