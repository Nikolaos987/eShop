import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {UsersService} from "../services/users.service";
import {tap} from "rxjs";
import {Router} from "@angular/router";
import {Credentials} from "../interfaces/credentials";
import {error} from "@angular/compiler-cli/src/transformers/util";

// import { User } from "../user";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  loginCredentials: Credentials = {
    username: undefined,
    password: undefined
  }

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
      this.loginCredentials = {
        username: this.profileForm.value.username,
        password: this.profileForm.value.password
      }

      this.usersService.fetchUser(this.loginCredentials)
        .subscribe( {
          next: result => {
            window.localStorage.setItem('user', JSON.stringify(result))
            console.log("next: " + result);
            this.router.navigateByUrl('/home');
          },
          error: err => {
            console.error(err);
            this.errorMessage = err;
          },
          complete: () => console.log("complete")
        });

      // this.usersService.fetchUser(this.loginCredentials)
      //   .subscribe({
      //     next(result) {
      //       console.log("next: " + result);
      //     },
      //     error(message) {
      //       console.log("error: " + message);
      //     },
      //     complete() {
      //       console.log("complete");
      //     }
      //   });

      // this.usersService.fetchUser(this.loginCredentials)
      //   .subscribe(result => {
      //     console.log(result);
      //     this.usersService.user = {
      //       uid: result.uid,
      //       username: result.username,
      //       isLoggedIn: true
      //     }
      //     this.router.navigateByUrl('/home');
      //   },(error) => {
      //     console.log(error);
      //     this.errorMessage = error;
      //   });
    }
  }
}
