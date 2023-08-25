import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ProductsService} from "../services/products.service";
import {HttpParams} from "@angular/common/http";
import {ignoreElements, map, Observable, range, tap} from "rxjs";
import {UsersService} from "../services/users.service";
import {MatPaginatorModule} from "@angular/material/paginator";
import {Product} from "../interfaces/product";
import {FormControl, FormGroup} from "@angular/forms";
import {Paging} from "../interfaces/paging";


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  providers: [ProductsService]
})
export class HomeComponent implements OnInit {
  // focus: boolean = false;
  filterText: string = ''

  products: Product[] = [];
  totalProducts: number | null | undefined;
  totalPages: number | null | undefined;
  pages: number[] | null | undefined = [];

  from: number = 0;
  page: number = 1;
  range: number = 5;

  uid: string = 'sth';
  // image: string | undefined;
  productList: Observable<Product[]> | undefined;
  helpText = "Search any product...";

  constructor(private productsService: ProductsService,
              private _usersService: UsersService,
              private cdr: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.cdr.detectChanges();
    console.log('UID from home component: ' + this._usersService.user?.isLoggedIn);
    this.productsService.fetchTotalProducts()
      .subscribe({
        next: (result) => {
          console.log("next: " + result.rows);
          this.totalProducts = result.rows;
          this.totalPages = Math.ceil(result.rows / Number(this.range))
          for (let i = 1; i <= this.totalPages; i++) {
            this.pages?.push(i);
          }
        },
        error: (error: Error) => {
          console.error(error)
        }
      });
    this.getProducts(1)
  }

  public getProducts(page: number) {
    this.page = page;
    console.log("page: " + this.page)
    this.productList = this.productsService.fetchProducts(page, this.range)
    this.productList.forEach(p => console.log(p))
  }

  changeRange(r: number) {
    if (this.totalPages)
      for (let i = 1; i <= this.totalPages; i++) {
        this.pages?.pop(); // remove the old pages
      }
    this.range = r;
    if (typeof this.totalProducts === "number") {
      this.totalPages = Math.ceil(this.totalProducts / Number(this.range))
      for (let i = 1; i <= this.totalPages; i++) {
        this.pages?.push(i);
      }
    }
    // if the total pages become less after the range is changed, move to the last possible page
    if (this.pages && this.page > this.pages.length) {
      this.page = this.pages.length;
    }
    this.getProducts(this.page)
  }

  prevPage() {
    if (this.page > 1)
      this.getProducts(this.page - 1)
  }

  nextPage() {
    if (this.totalPages && this.page < this.totalPages)
      this.getProducts(this.page + 1)
  }

  public getFilteredProducts(text: string) {
    if (this.totalPages)
      for (let i = 1; i <= this.totalPages; i++) {
        this.pages?.pop();
      }
    this.productsService.fetchTotalSearchedProducts(text)
      .subscribe(
        result => {
          console.log("total filtered products: " + result.rows);
          this.totalProducts = result.rows;
          this.totalPages = Math.ceil(result.rows / Number(this.range))
          for (let i = 1; i <= this.totalPages; i++) {
            this.pages?.push(i);
          }
        },
        error => {
          console.error(error);
        }
      )
    console.log("in filter, page: " + this.page +
      "\nin filter, range: " + this.range)

    this.productList = this.productsService.fetchFilteredProducts(text, this.page, this.range)
  }

}
