import { Component } from '@angular/core';
import { FormControl, FormGroup } from "@angular/forms";
import { UsersService } from "../services/users.service";
import {tap} from "rxjs";

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
    window.alert('button pressed');
    this.usersService.fetchUser(this.profileForm.value)
      .pipe(
        tap(user => console.log(user)) //TODO: den leitourgei to user.uid, mono to user
      )
      .subscribe();

    // window.alert(this.profileForm.value)
    // this.usersService.fetchUser(this.profileForm.get("username"), this.profileForm.get("password"))
    //   .subscribe();
  }
}
