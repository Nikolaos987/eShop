import {Component, OnInit} from '@angular/core';
import {UsersService} from "../services/users.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {error} from "@angular/compiler-cli/src/transformers/util";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css', '../login/login.component.css'],
  providers: [UsersService]
})
export class RegisterComponent implements OnInit {

  errorMessage: string = '';
  successMessage: string = '';
  buttonClicked: boolean = false;

  // TODO: Add validators
  registerForm = new FormGroup({
    username: new FormControl('',
      [Validators.required, Validators.min(3)]),
    password: new FormControl('',
      [Validators.required, Validators.min(4)]),
    passwordAgain: new FormControl('',
      [Validators.required, Validators.min(4)])
  });

  constructor(private usersService: UsersService) {
  }

  ngOnInit(): void {
  }

  register() {
    this.buttonClicked = true;
    // if (this.registerForm.value.username!='' && this.registerForm.value.password!='' && this.passwordAgain!='') {
    //   if (this.registerForm.value.password == this.passwordAgain) { // ===
    if (this.registerForm.valid && this.registerForm.value.password === this.registerForm.value.passwordAgain) {
      this.usersService.postUser(this.registerForm.value)
        .subscribe(response => {
            console.log(response);
            this.successMessage = response;
            this.errorMessage = '';
          },
          (error) => {
            this.successMessage = '';
            this.errorMessage = error;
          });
    } else {
      this.successMessage = '';
      this.errorMessage = 'incorrect fields'
    }
    // } else window.alert('passwords do not match');
    // } else window.alert('empty inputs');
  }

}
