import {AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild} from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import {User} from "./interfaces/user";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements AfterViewInit, OnInit {
  @ViewChild('topnav') menuElement: ElementRef | undefined;
  menuPosition: any;
  sticky: boolean = false;

  currentUser: User = JSON.parse(window.localStorage.getItem('user') || '{}');

  ngAfterViewInit() {
    this.menuPosition = this.menuElement?.nativeElement.offsetTop
  }

  ngOnInit(): void {
    console.log("THE USER ID IS: " + this.currentUser.uid);
    console.log("THE USERNAME IS: " + this.currentUser.username);
  }

  logout() {
    window.localStorage.removeItem('user');
    console.log("cleared")
  }

  @HostListener('window:scroll', ['event'])
  handleScroll() {
    const windowScroll = window.scrollY;
    if (windowScroll >= 80) {
      this.sticky = true;
    } else {
      this.sticky = false;
    }
  }

}
