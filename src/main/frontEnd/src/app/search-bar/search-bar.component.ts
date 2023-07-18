import {Component, OnInit} from '@angular/core';
import { ProductsService } from "../services/products.service";
// import { ProductListComponent } from "../product-list/product-list.component";
import { HomeComponent } from "../home/home.component";

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['../home/home.component.css', './search-bar.component.css'],
  providers: [ProductsService]
})
export class SearchBarComponent implements OnInit {

  text: string = '';

  ngOnInit(): void {
  }

  constructor(private productsService: ProductsService) { }

  filter(text: string) {
    // this.productListComponent.getFilteredProducts(text);
  }

}
