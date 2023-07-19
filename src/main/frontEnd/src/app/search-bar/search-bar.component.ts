import {Component, OnInit} from '@angular/core';
import { HomeComponent } from "../home/home.component";
import {filter} from "rxjs";

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['../home/home.component.css', './search-bar.component.css'],
  providers: []
})
export class SearchBarComponent implements OnInit {

  txt: string = '';

  constructor(private homeComponent: HomeComponent) {
    this.txt='';
  }

  ngOnInit(): void {
  }

  filter(text: string) {
    this.homeComponent.getFilteredProducts(text);
  }

  clearText(text: string) {
    this.txt='';
    this.filter(this.txt);
  }

}
