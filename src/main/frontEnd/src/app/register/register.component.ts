import { Component, OnInit} from '@angular/core';
import {UsersService} from "../services/users.service";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css', '../login/login.component.css'],
  providers: [UsersService]
})
export class RegisterComponent implements OnInit {
  username: string = '';
  password: string = '';
  passwordAgain: string = '';

  constructor(private usersService: UsersService) {
  }

  register() {
    if (this.username!='' && this.password!='' && this.passwordAgain!='') {
      if (this.password == this.passwordAgain) {
        this.usersService.postUser(this.username, this.password)
          .subscribe(user => console.log(user));
      }
    }
  }

  ngOnInit(): void {
    this.username = '';
    this.password = '';
    this.passwordAgain = '';
  }
}
