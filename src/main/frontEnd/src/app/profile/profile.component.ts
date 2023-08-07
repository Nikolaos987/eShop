import {Component, OnInit} from '@angular/core';
import {UsersService} from "../services/users.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {passwordMatchesValidator} from "../passwordMatchesValidator";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  message: string = '';
  currentUsername: string | undefined = this._usersService.user?.username;

  errorMessage: string = '';
  successMessage: string = '';
  delBtnClicked: boolean = false;
  changePswdBtnClicked: boolean = false;

  // currentPassword: string = '';
  // password: string = '';
  // passwordAgain: string = '';
  // uid: string = '';

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
      this._usersService.putUser(this.profileForm.value)
        .subscribe(result => {
          this.successMessage = result;
          this.errorMessage = '';
          console.log('this is the result emitted: '+ result);
          // this.router.navigateByUrl('/home')
        }, (error) => {
          this.successMessage = '';
          this.errorMessage = error;
        });
    } else {
      this.successMessage = '';
      this.errorMessage = 'passwords do not match';
    }
  }

  deleteUser() {
    this._usersService.deleteUser()
      .subscribe((result) => {
        this.router.navigateByUrl('/home');
      }, (error) => {
        console.log(error);
        this.errorMessage = error;
      });
  }

}
