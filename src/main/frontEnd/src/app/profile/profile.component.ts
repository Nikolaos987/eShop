import {Component, OnInit} from '@angular/core';
import {UsersService} from "../services/users.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  message: string = '';
  currentUsername: string | undefined = this._usersService.user?.username;

  // currentPassword: string = '';
  // password: string = '';
  // passwordAgain: string = '';
  // uid: string = '';

  profileForm = new FormGroup({
    currentPassword: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
    passwordAgain: new FormControl('', Validators.required)
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
    if (this.profileForm.valid
      && this.profileForm.value.password === this.profileForm.value.passwordAgain
      && this.profileForm.value.currentPassword !== this.profileForm.value.password) {
      this._usersService.putUser(this.profileForm.value)
        .subscribe(result => {
          console.log('this is the result emitted: '+ result);
          this.router.navigateByUrl('/home')
        }, (error) => {
          this.message = error;
        });
    }
  }

  deleteUser() {
    this._usersService.deleteUser()
      .subscribe((result) => {
        window.alert('this is result:');
        this.router.navigateByUrl('/home');
      }, (error) => {
        console.log(error);
      });
  }

}
