import {ChangeDetectorRef, Component, computed, OnInit} from '@angular/core';
import {ProductsService} from "../services/products.service";
import {HttpParams} from "@angular/common/http";
import {ignoreElements, map, Observable, range, tap} from "rxjs";
import {UsersService} from "../services/users.service";
import {MatPaginatorModule} from "@angular/material/paginator";
import {Product} from "../interfaces/product";
import {FormControl, FormGroup} from "@angular/forms";
import {Paging} from "../interfaces/paging";
import {PagingService} from "../services/paging.service";


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  providers: [ProductsService]
})
export class HomeComponent implements OnInit {
  // focus: boolean = false;
  filterText: string = '';

  products: Product[] = [];
  totalProducts: number | null | undefined;
  totalPages: number | null | undefined;
  pages: number[] | null | undefined = [];

  from: number = 0;
  page: number = 1;
  range: number | undefined = this._pagingService.page?.range;

  // uid: string = 'sth';
  // image: string | undefined;
  productList: Product[] | undefined;
  helpText = "Search any product...";


  constructor(private productsService: ProductsService,
              private _usersService: UsersService,
              private cdr: ChangeDetectorRef,
              private _pagingService: PagingService) {
  }

  ngOnInit(): void {
    this.cdr.detectChanges();
    // const userData = window.localStorage.getItem('user')
    // console.log('UID from home component: ' + JSON.parse(userData));
    this.productsService.fetchTotalProducts()
      .subscribe({
        next: (result) => {
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
    this.getProducts(this.page)
    this.range = this._pagingService.page?.range;
  }

  public getProducts(page: number) {
    this.page = page;
    console.log("from getProducts(): page = " + this.page)
    if (this.filterText == '') {
      this.productsService.fetchProducts(page, this.range)
        .subscribe({
          next: value => {
            this.productList = value;
          },
          error: err => console.error(err)
        })
      // this.productList.forEach(p => console.log(p))
    } else {
      // this.getFilteredProducts(this.filterText);
      if (this.range)
      this.filteredProducts(this.filterText, this.page, this.range)
    }
  }

  changeRange(r: number) {
    if (this.totalPages)
      for (let i = 1; i <= this.totalPages; i++) {
        this.pages?.pop(); // removing the old pages
      }
    this.range = r;
    if (typeof this.totalProducts == "number") {
      this.totalPages = Math.ceil(this.totalProducts / Number(this.range))
      for (let i = 1; i <= this.totalPages; i++) {
        this.pages?.push(i);
      }
    }
    // if the total pages become less after the range is changed, move to the last possible page
    if (this.pages && this.page > this.pages.length) {
      this.page = this.pages.length;
    }
    this._pagingService.newRange(r);
    if (this.filterText == '')
      this.getProducts(this.page)
    else
      this.getFilteredProducts(this.filterText);
  }

  prevPage() {
    if (this.page > 1) {
      this.page = this.page - 1;
      this.getProducts(this.page);
    }
  }

  nextPage() {
    if (this.totalPages && this.page < this.totalPages) {
      this.page = this.page + 1;
      this.getProducts(this.page);
    }
  }

  public getFilteredProducts(text: string) {
    this.filterText = text;
    if (this.totalPages)
      for (let i = 1; i <= this.totalPages; i++) {
        this.pages?.pop();
      }
    this.productsService.fetchTotalSearchedProducts(text)
      .subscribe({
        next: (result) => {
          this.totalProducts = result.rows;
          this.totalPages = Math.ceil(result.rows / Number(this.range))
          for (let i = 1; i <= this.totalPages; i++) {
            this.pages?.push(i);
          }
        },
        error: (error: Error) => {
          console.error(error);
        },
        complete: () => {
          // if (this.pages && this.page > this.pages.length)
          this.page = 1;
          if (this.range)
          this.filteredProducts(text, this.page, this.range)
        }
      })
  }

  public filteredProducts(text: string, page: number, range: number) {
    this.productsService.fetchFilteredProducts(text, this.page, this.range)
      .subscribe({
        next: value => {
          this.productList = value;
        },
        error: (error: Error) => {
          console.error(error)
        }
      })
  }

}
