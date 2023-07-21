import { Component } from '@angular/core';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent {
  message: string = '';

  showMessage() {
    this.message = '';
    this.message = 'password changed successfully!';
  }

}
