import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {UsersResourceService} from "../../core/modules/openapi";
import {AuthService} from "@auth0/auth0-angular";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {AppModule} from "../../app.module";

@Component({
  selector: 'app-login-success',
  templateUrl: './login-success.component.html',
  styleUrl: './login-success.component.css'
})
export class LoginSuccessComponent implements OnInit {
  vcellUserId: string | undefined;

  constructor(
    private usersResourceService: UsersResourceService,
    public auth: AuthService
  ) {
  }

  ngOnInit() {
    this.getMappedUser();
  }

  getMappedUser() {
    this.usersResourceService.getMappedUser().subscribe((identity) => {
      this.vcellUserId = identity.userName;
    });
  }

}
