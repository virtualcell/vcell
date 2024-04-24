import {Component, OnInit} from '@angular/core';
import {AuthService} from '@auth0/auth0-angular';
import {UserLoginInfoForMapping} from 'src/app/core/modules/openapi/model/models';
import {UsersResourceService} from "../../core/modules/openapi";

@Component({
  selector: 'app-vcell-identity',
  templateUrl: './vcell-identity.component.html',
  styleUrls: ['./vcell-identity.component.less'],
})
export class VcellIdentityComponent implements OnInit {
  vcellUserId: string = 'not-set';
  mapUser: UserLoginInfoForMapping = new class implements UserLoginInfoForMapping {
    password: string;
    userID: string;
  };

  constructor(
    private usersResourceService: UsersResourceService,
    public auth: AuthService
  ) {}

  ngOnInit() {
    this.getVCellIdentity();
  }

  getVCellIdentity() {
    this.usersResourceService.getVCellIdentity().subscribe((identity) => {
      this.vcellUserId = identity.userName;
    });
  }

  public setVCellIdentity() {
    console.log("setVCellIdentity() mapUser = "+this.mapUser.userID+","+this.mapUser.digestedPassword);
    this.usersResourceService.setVCellIdentity(this.mapUser).subscribe((response) => {
      if (response) {
        this.getVCellIdentity();
      }
    });
  }
}
