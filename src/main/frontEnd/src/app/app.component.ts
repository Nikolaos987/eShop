import {AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild} from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements AfterViewInit {
  @ViewChild('topnav') menuElement: ElementRef | undefined;
  menuPosition: any;
  sticky: boolean = false;

  ngAfterViewInit() {
    this.menuPosition = this.menuElement?.nativeElement.offsetTop
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
