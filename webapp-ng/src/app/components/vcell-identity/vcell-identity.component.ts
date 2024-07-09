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
  errorMessage = "";
  showResetPasswordPanel = false;

  constructor(
    private usersResourceService: UsersResourceService,
    public auth: AuthService
  ) {
  }

  ngOnInit() {
    this.getMappedUser();
  }

  toggleResetPasswordPanel() {
    this.showResetPasswordPanel = !this.showResetPasswordPanel;
  }

  sendResetLink(email: string, userid: string) {
    this.usersResourceService.requestRecoveryEmail(email, userid).subscribe(
      (response) => {
        if (response) {
          console.log('Successfully sent reset password link, check your email');
          this.errorMessage = "";
        } else {
          console.error('Error sending reset password link, returned false');
          this.errorMessage = 'Failed to send reset password link. Please try again.';
        }
      },
      (error) => {
        console.error('Error sending reset password link', error);
        this.errorMessage = 'Failed to send reset password link. Please try again.';
      }
    );
  }

  getMappedUser() {
    this.usersResourceService.getMappedUser().subscribe((identity) => {
      this.vcellUserId = identity.userName;
    });
  }

  public setMappedUser() {
    console.log("setMappedUser() mapUser = " + this.mapUser.userID + "," + this.mapUser.password);
    this.usersResourceService.mapUser(this.mapUser).subscribe(
      (response) => {
        if (response) {
          this.getMappedUser();
          console.log('Successfully mapped VCell identity');
          this.errorMessage = "";
        } else {
          console.error('Error mapping VCell identity, returned false');
          this.errorMessage = 'Failed to map user. Please try again.';
        }
      },
      (error) => {
        console.error('Error mapping VCell identity', error);
        this.errorMessage = 'Failed to map user. Please try again.';
      }
    );
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
    console.log("mapNewUser() mapNewUser = " + this.newUser.userID);
    this.usersResourceService.mapNewUser(this.newUser).subscribe(
      (response) => {
        if (response) {
          this.getMappedUser();
          console.log("mapNewUser() response = " + response);
          this.errorMessage = "";
        } else {
          console.error('Error mapping new VCell identity, returned false');
          this.errorMessage = 'Failed to map new user. Please try again.';
        }
      },
      (error) => {
        console.error('Error mapping new VCell identity', error);
        this.errorMessage = 'Failed to map new user. Please try again.';
      });
  }
}
