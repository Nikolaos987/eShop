import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { ProductsService } from "../services/products.service";
import {HttpParams} from "@angular/common/http";
import {map, Observable, tap} from "rxjs";
import {UsersService} from "../services/users.service";
import {MatPaginatorModule} from "@angular/material/paginator";
import {Product} from "../interfaces/product";


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  providers: [ProductsService]
})
export class HomeComponent implements OnInit{

  uid:string = 'sth';
  image: any;
  productList: Observable<Product[]> | undefined;
  helpText = "Search any product...";

  constructor(private productsService: ProductsService, private _usersService: UsersService, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.cdr.detectChanges();
    console.log('UID from home component: ' + this._usersService.user?.isLoggedIn);
  }

  public getProducts() {
    this.productList = this.productsService.fetchProducts()
    this.productList.forEach(p => console.log(p))
    // this.productsService.fetchProducts()
    //   .subscribe(response => {
    //     this.productList = response;
    //     response.forEach(p => console.log("products: " + p))
    //   });

  }

  public getFilteredProducts(text: string) {
    this.productList = this.productsService.fetchFilteredProducts(text)
  }

  public showDetails(pid: string): void {
    this.productsService.fetchProduct(pid);
  }

}
