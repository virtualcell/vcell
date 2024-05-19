import {Component, OnInit} from '@angular/core';
import {AuthService} from '@auth0/auth0-angular';
import {UserLoginInfoForMapping, UserRegistrationInfo} from 'src/app/core/modules/openapi/model/models';
import {UsersResourceService} from "../../core/modules/openapi";

@Component({
  selector: 'app-vcell-identity',
  templateUrl: './vcell-identity.component.html',
  styleUrls: ['./vcell-identity.component.less'],
})
export class VcellIdentityComponent implements OnInit {
  vcellUserId: string | undefined;
  mapUser: UserLoginInfoForMapping = new class implements UserLoginInfoForMapping {
    password: string | undefined;
    userID: string | undefined;
  };
  newUser: UserRegistrationInfo = new class implements UserRegistrationInfo {
    userID: string | undefined;
    organization: string | undefined;
    title: string | undefined;
    country: string | undefined;
    emailNotification: boolean | undefined;
  };

  constructor(
    private usersResourceService: UsersResourceService,
    public auth: AuthService
  ) {}

  ngOnInit() {
    this.getMappedUser();
  }

  getMappedUser() {
    this.usersResourceService.getMappedUser().subscribe((identity) => {
      this.vcellUserId = identity.userName;
    });
  }

  public setMappedUser() {
    console.log("setVCellIdentity() mapUser = "+this.mapUser.userID+","+this.mapUser.password);
    this.usersResourceService.mapUser(this.mapUser).subscribe((response) => {
      if (response) {
        this.getMappedUser();
      }
    });
  }

  clearMappedUser() {
    if (!this.vcellUserId) {
      return;
    }
    this.usersResourceService.unmapUser(this.vcellUserId).subscribe((response) => {
      if (response) {
        this.vcellUserId = undefined;
      }
    }, error => {
      console.error('Error clearing VCell identity', error);
    });
  }

  mapNewUser() {
    console.log("setVCellIdentity() mapNewUser = "+this.newUser.userID);
    this.usersResourceService.mapNewUser(this.newUser).subscribe((response) => {
      if (response) {
        this.getMappedUser();
      }
    });
  }
}
