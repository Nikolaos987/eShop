import {Component, OnInit} from '@angular/core';
import { ProductsService } from "../services/products.service";
import {HttpParams} from "@angular/common/http";
import {map, tap} from "rxjs";
import {UsersService} from "../services/users.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  providers: [ProductsService]
})
export class HomeComponent implements OnInit{

  productList: any;
  helpText = "Search any product...";

  constructor(private productsService: ProductsService, private _usersService: UsersService) { }

  ngOnInit(): void {
  }

  public getProducts() {
    this.productList = this.productsService.fetchProducts();
  }

  public getFilteredProducts(text: string) {
    this.productList = this.productsService.fetchFilteredProducts(text)
  }

  public showDetails(pid: string): void {
    this.productsService.fetchProduct(pid);
  }

}
