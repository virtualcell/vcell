import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '@auth0/auth0-angular';
import {UserLoginInfoForMapping, UserRegistrationInfo} from 'src/app/core/modules/openapi/model/models';
import {UsersResourceService} from "../../core/modules/openapi";

@Component({
  selector: 'app-vcell-identity',
  templateUrl: './vcell-identity.component.html',
  styleUrls: ['./vcell-identity.component.less'],
})
export class VcellIdentityComponent implements OnInit, OnDestroy {
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
  showRecoverAccountPanel = false;
  private checkUserIdInterval: number | undefined;
  recoveryStatus = "";

  constructor(
    private usersResourceService: UsersResourceService,
    public auth: AuthService
  ) {
  }

  ngOnInit() {
    this.getMappedUser();
    this.setupUserIdCheck();
  }

  ngOnDestroy() {
    if (this.checkUserIdInterval) {
      clearInterval(this.checkUserIdInterval);
    }
  }

  setupUserIdCheck() {
    this.checkUserIdInterval = setInterval(() => {
      // Assuming getMappedUser() updates vcellUserId
      this.getMappedUser();
      if (this.vcellUserId) {
        console.log('VCell User ID is set:', this.vcellUserId);
        // Display your confirmation message here
        // For example, you could set a property and bind it in your template to show the message
        this.errorMessage = 'Your VCell User ID has been successfully set.';
        clearInterval(this.checkUserIdInterval); // Stop checking once found
      }
    }, 5000); // Check every 5 seconds
  }

  toggleRecoverAccountPanel() {
    this.showRecoverAccountPanel = !this.showRecoverAccountPanel;
  }

  sendRecoverAccountLink(email: string, userid: string) {
    this.usersResourceService.requestRecoveryEmail(email, userid).subscribe(
      (response) => {
        console.log('Successfully sent account recovery link, check your email');
        this.errorMessage = "";
        this.recoveryStatus = "Successfully sent account recovery link, check your email";
      },
      (error) => {
        console.error('Error sending reset password link', error);
        this.errorMessage = 'Failed to send account recovery link. Please try again.';
        this.recoveryStatus = "";
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
          this.getMappedUser();
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
    this.errorMessage = "";
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
        this.getMappedUser();
        console.log("mapNewUser() success, response = " + response);
        this.errorMessage = "";
      },
      (error) => {
        console.error('Error mapping new VCell identity', error);
        this.errorMessage = 'Failed to map new user. Please try again.';
      });
  }
}
