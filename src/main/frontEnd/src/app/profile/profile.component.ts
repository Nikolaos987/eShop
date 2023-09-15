import {Component, OnInit} from '@angular/core';
import {UsersService} from "../services/users.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {passwordMatchesValidator} from "../passwordMatchesValidator";
import {Profile} from "../interfaces/profile";
import {User} from "../interfaces/user";
import {interval} from "rxjs";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  profile: Profile = {
    currentPassword: undefined,
    password: undefined
  }

  copyMessage: boolean = false;
  isActive1: boolean = true;
  isActive2: boolean = false;
  isActive3: boolean = false;
  isActiveProfile: boolean = true;
  isActivePassword: boolean = false;
  isActiveDelete: boolean = false;

  message: string = '';
  currentUser: User = JSON.parse(window.localStorage.getItem('user') || '{}');

  errorMessage: string = '';
  successMessage: string = '';
  errorDeleteMessage: string = '';
  successDeleteMessage: string = '';
  delBtnClicked: boolean = false;
  changePswdBtnClicked: boolean = false;

  profileForm = new FormGroup({
      currentPassword: new FormControl('',
        [Validators.required, Validators.minLength(4)]),
      password: new FormControl('',
        [Validators.required, Validators.minLength(4)]),
      passwordAgain: new FormControl('',
        [Validators.required, Validators.minLength(4)]),
    },
    {
      updateOn: 'change',
      validators: [passwordMatchesValidator]
    });

  constructor(private _usersService: UsersService, private router: Router) {
  }

  ngOnInit(): void {
  }

  showMessage() {
    this.message = '';
    this.message = 'password changed successfully!';
  }

  changePassword() {
    this.changePswdBtnClicked = true;
    if (this.profileForm.valid) {
      this.profile = {
        currentPassword: this.profileForm.value.currentPassword,
        password: this.profileForm.value.password
      }
      this._usersService.putUser(this.profile)
        .subscribe({
          next: result => {
            this.successMessage = 'Password changed!';
            this.errorMessage = '';
            console.log('this is the result emitted: ' + result);
            // this.router.navigateByUrl('/home')
          },
          error: error => {
            this.successMessage = '';
            this.errorMessage = error;
          },
          complete: () => console.log("complete")
        });
    } else {
      this.successMessage = '';
      this.errorMessage = 'passwords do not match';
    }
  }

  deleteUser() {
    this._usersService.deleteUser()
      .subscribe({
        next: result => {
          this.successDeleteMessage = "Account Deleted";
          this.errorDeleteMessage = "";
          this.router.navigateByUrl('/home')
        },
        error: error => {
          console.log(error);
          this.errorDeleteMessage = "";
          this.errorDeleteMessage = error;
        }
      });
  }

  showProfile() {
    this.isActive1 = true;
    this.isActive2 = false;
    this.isActive3 = false;

    this.isActiveProfile = true;
    this.isActivePassword = false;
    this.isActiveDelete = false;
  }

  showChangePassword() {
    this.isActive1 = false;
    this.isActive2 = true;
    this.isActive3 = false;

    this.isActiveProfile = false;
    this.isActivePassword = true;
    this.isActiveDelete = false;
  }

  showDeleteAccount() {
    this.isActive1 = false;
    this.isActive2 = false;
    this.isActive3 = true;

    this.isActiveProfile = false;
    this.isActivePassword = false;
    this.isActiveDelete = true;
  }

  copy(uid: string) {
    this.copyMessage = true;
    navigator.clipboard.writeText(uid);
    this.scheduleCopyTextFalse();
  }

  scheduleCopyTextFalse() {
    setTimeout(() => {
      this.copyMessage = false;
    }, 3000);
  }
}
