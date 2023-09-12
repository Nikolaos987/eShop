import {AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild} from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import {User} from "./interfaces/user";
import {UsersService} from "./services/users.service";
import {Router} from "@angular/router";

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
    if (this.currentUser.uid) {
      this._usersServise.fetchUserById()
        .subscribe({
          next: value => console.log("exists: " + value.uid),
          error: err => {
            window.localStorage.removeItem("user");
            this.router.navigateByUrl('/login')
          }
        })
      console.log("THE USER ID IS: " + this.currentUser.uid);
      console.log("THE USERNAME IS: " + this.currentUser.username);
    }
  }

  constructor(private _usersServise: UsersService, private router: Router) {
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
