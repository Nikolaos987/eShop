import { Component } from '@angular/core';
import { ProductsService } from "../services/products.service";
import {HttpParams} from "@angular/common/http";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  providers: [ProductsService]
})
export class HomeComponent {

  productList: any;

  constructor(private productsService: ProductsService) { }

  public getProducts() {
    this.productList = this.productList = this.productsService.fetchProducts();
  }

  public getFilteredProducts(text: string) {
    this.productList = this.productsService.fetchFilteredProducts(text);
  }

  public showDetails(pid: string): void {
    this.productsService.fetchProduct(pid);
  }

}
