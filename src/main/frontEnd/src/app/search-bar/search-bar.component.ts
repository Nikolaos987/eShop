import {Component, OnInit} from '@angular/core';
import { HomeComponent } from "../home/home.component";

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['../home/home.component.css', './search-bar.component.css'],
  providers: []
})
export class SearchBarComponent implements OnInit {

  text: string = '';

  constructor(private homeComponent: HomeComponent) { }

  ngOnInit(): void {
  }

  filter(text: string) {
    this.homeComponent.getFilteredProducts(text);
  }

}
