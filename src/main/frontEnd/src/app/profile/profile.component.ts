import {Component, OnInit} from '@angular/core';
import {UsersService} from "../services/users.service";
import {FormControl, FormGroup} from "@angular/forms";
import {Router} from "@angular/router";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  message: string = '';
  currentUsername: string = this._usersService.user.username;

  // currentPassword: string = '';
  // password: string = '';
  // passwordAgain: string = '';
  // uid: string = '';

  profileForm = new FormGroup({
    currentPassword: new FormControl(''),
    password: new FormControl(''),
    passwordAgain: new FormControl('')
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
    // if (this.currentPassword != '' && this.password != '' && this.passwordAgain != '') {
    //   if (this.password == this.passwordAgain) {
        this._usersService.putUser(this.profileForm.value)
          .subscribe(result => console.log(result));
    //   }
    // }
  }

  deleteUser() {
    this._usersService.deleteUser()
      .subscribe((result) => {
        window.alert('this is result:');
        this.router.navigateByUrl('/home');
      });
  }

}
