import { Component, OnInit } from '@angular/core';
import { ProductsService } from "../services/products.service";
// import { SearchBarComponent } from "../search-bar/search-bar.component";
// import { HomeComponent } from "../home/home.component";
import { take } from "rxjs";

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['../home/home.component.css', './product-list.component.css'],
  providers: [ProductsService]
})
export class ProductListComponent implements OnInit {

  productList: any;
  public product: any;

  constructor(private productsService: ProductsService) {
  }

  ngOnInit(): void {
    this.getProducts();

    // this.productsService.fetchProducts()
    //   .pipe(take(1))
    //   .subscribe(response => this.productList = response);
  }

  public getProducts() {
    this.productList = this.productsService.fetchProducts();
  }

  public getFilteredProducts(text: string) {
    this.productsService.fetchFilteredProducts(text);
  }

  public showDetails(pid: string): void {
    this.productsService.fetchProduct(pid);
  }

}
