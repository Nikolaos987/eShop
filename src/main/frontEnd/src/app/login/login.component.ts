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
})
export class LoginComponent {

  errorMessage: string = '';

  profileForm = new FormGroup({
    username: new FormControl('',
      [Validators.required, Validators.minLength(3)]),
    password: new FormControl('',
      [Validators.required, Validators.minLength(4)]),
  },
    {
      updateOn: 'change'
    });

  constructor(private usersService: UsersService, private router: Router) {
  }

  login() {
    if (this.profileForm.valid) {
      this.usersService.fetchUser(this.profileForm.value)
        .subscribe(result => {
          console.log(result);
          this.router.navigateByUrl('/home');
        }, (error) => {
          console.log(error);
          this.errorMessage = error;
        });
    }
  }
}
