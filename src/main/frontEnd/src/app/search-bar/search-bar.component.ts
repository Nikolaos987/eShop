import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { HomeComponent } from "../home/home.component";
import {filter} from "rxjs";
import {ProductsService} from "../services/products.service";

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['../home/home.component.css', './search-bar.component.css'],
  providers: []
})
export class SearchBarComponent implements OnInit {
  @Input() help = '';
  @Output() result = new EventEmitter<string>();

  txt: string = '';

  constructor() {
    this.txt='';
  }

  ngOnInit(): void {
  }

  filter(text: string) {
    this.result.emit(text);

    // this.homeComponent.getFilteredProducts(text);
  }

  clearText(text: string) {
    this.txt='';
    this.filter(this.txt);
  }

}
