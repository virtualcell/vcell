import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Observable} from "rxjs";
import {AdminResourceService} from "../../core/modules/openapi";
import {AuthorizationService} from "../../services/authorization.service";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css'
})
export class AdminComponent {
  isAdmin$: Observable<boolean>;

  constructor(private authorizationService: AuthorizationService, private adminResourceService: AdminResourceService) {
    this.isAdmin$ = authorizationService.isAdmin();
  }

  downloadUsageReport(): void {
    this.adminResourceService.getUsage('body', false, {httpHeaderAccept: 'application/pdf'})
      .subscribe(data => {
        const blob = new Blob([data], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'usage.pdf');
        document.body.appendChild(link);
        link.click();
        link.remove();
    });
  }

}
