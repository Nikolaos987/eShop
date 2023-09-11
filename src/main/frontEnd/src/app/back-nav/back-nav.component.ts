import { Component } from '@angular/core';
import {Location} from "@angular/common";

@Component({
  selector: 'app-back-nav',
  templateUrl: './back-nav.component.html',
  styleUrls: ['./back-nav.component.css']
})
export class BackNavComponent {

  constructor(private location: Location) { }

  goBack(): void {
    this.location.back();
  }
}
