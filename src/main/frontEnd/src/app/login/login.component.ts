import { Component } from '@angular/core';
import { FormControl, FormGroup } from "@angular/forms";
import { UsersService } from "../services/users.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  providers: [UsersService]
})
export class LoginComponent {

  profileForm = new FormGroup({
    username: new FormControl(''),
    password: new FormControl(''),
  });

  constructor(private usersService: UsersService) {
  }

  login() {
    window.alert(this.profileForm.value)
    // this.user.setValue(this.usersService.fetchUser(username, password));
  }
}
