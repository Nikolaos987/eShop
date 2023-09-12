import {Component, OnInit} from '@angular/core';
import {UsersService} from "../services/users.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {passwordMatchesValidator} from "../passwordMatchesValidator";
import {Profile} from "../interfaces/profile";
import {User} from "../interfaces/user";

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

  isActiveProfile: boolean = true;
  isActivePassword: boolean = false;
  isActiveDelete: boolean = false;

  message: string = '';
  currentUser: User = JSON.parse(window.localStorage.getItem('user') || '{}');

  errorMessage: string = '';
  successMessage: string = '';
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
        next: result => this.router.navigateByUrl('/home'),
        error: error => {
          console.log(error);
          this.errorMessage = error;
        }
      });
  }

  showProfile() {
    this.isActiveProfile = true;
    this.isActivePassword = false;
    this.isActiveDelete = false;
  }

  showChangePassword() {
    this.isActiveProfile = false;
    this.isActivePassword = true;
    this.isActiveDelete = false;
  }

  showDeleteAccount() {
    this.isActiveProfile = false;
    this.isActivePassword = false;
    this.isActiveDelete = true;
  }
}
