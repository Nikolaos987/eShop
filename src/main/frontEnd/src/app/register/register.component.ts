import { Component, OnInit} from '@angular/core';
import {UsersService} from "../services/users.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css', '../login/login.component.css'],
  providers: [UsersService]
})
export class RegisterComponent implements OnInit {

  validators: Validators = {

  }

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
    // if (this.registerForm.value.username!='' && this.registerForm.value.password!='' && this.passwordAgain!='') {
    //   if (this.registerForm.value.password == this.passwordAgain) { // ===
        this.usersService.postUser(this.registerForm.value)
          .subscribe(user => console.log(user));
      // } else window.alert('passwords do not match');
    // } else window.alert('empty inputs');
  }

}
