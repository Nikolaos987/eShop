import {Component, OnInit} from '@angular/core';
import {UsersService} from "../services/users.service";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  message: string = '';
  currentPassword: string = '';
  password: string = '';
  passwordAgain: string = '';
  uid: string = '';

  currentUsername: string = this.usersService.user.username;

  constructor(public usersService: UsersService) {
  }

  ngOnInit(): void {
    // this.currentUsername = this.usersService.getUsername();
    this.currentUsername = this.usersService.getUsername();
    console.log('from profile component: '+this.usersService.user.uid);
    // this.currentUsername = this.usersService.username;
  }

  showMessage() {
    this.message = '';
    this.message = 'password changed successfully!';
  }

  changePassword(data: any) {
    if (this.currentPassword != '' && this.password != '' && this.passwordAgain != '') {
      if (this.password == this.passwordAgain) {
        this.usersService.putUser(data, this.uid)
          .subscribe(result => console.log(result));
      }
    }
  }

}
