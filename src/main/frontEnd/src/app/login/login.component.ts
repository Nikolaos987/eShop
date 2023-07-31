import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {UsersService} from "../services/users.service";
import {tap} from "rxjs";
import {Router} from "@angular/router";

// import { User } from "../user";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  providers: [UsersService]
})
export class LoginComponent {

  profileForm = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
  });

  constructor(private usersService: UsersService, private router: Router) {
  }

  login() {
    // window.alert('UID: ' + this.usersService.user.uid)
    if (this.profileForm.valid) {
      this.usersService.fetchUser(this.profileForm.value)
        .subscribe(result => {
          console.log('from login component: ' + result);
          // window.alert('UID: ' + this.usersService.user.uid)
          this.router.navigateByUrl('/home');
        });
    }
  }
}
