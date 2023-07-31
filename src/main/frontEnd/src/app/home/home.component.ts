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

  uid:string = 'sth';
  image: any;
  productList: any;
  helpText = "Search any product...";

  constructor(private productsService: ProductsService, private _usersService: UsersService) { }

  ngOnInit(): void {
    console.log('UID from home component: ' + this.uid);
  }

  public getProducts() {
    this.productList = this.productsService.fetchProducts();
    this.image = this.productsService.fetchProductImage('ae7c94b7-423f-4d4d-afc3-3f181fd5ba5a');
  }

  public getFilteredProducts(text: string) {
    this.productList = this.productsService.fetchFilteredProducts(text)
  }

  public showDetails(pid: string): void {
    this.productsService.fetchProduct(pid);
  }

}
