import { Component } from '@angular/core';
import { ProductsService } from "../services/products.service";
import { SearchBarComponent } from "../search-bar/search-bar.component";
import { ProductListComponent } from "../product-list/product-list.component";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  providers: [ProductsService]
})
export class HomeComponent {

  constructor(productsService: ProductsService) { }


}
