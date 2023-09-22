import {AfterViewInit, ChangeDetectorRef, Component, computed, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ProductsService} from "../services/products.service";
import {UsersService} from "../services/users.service";
import {Product} from "../interfaces/product";
import {PagingService} from "../services/paging.service";
import {User} from "../interfaces/user";
import {Category} from "../interfaces/category";


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  providers: [ProductsService]
})
export class HomeComponent implements OnInit {

  filterText: string = '';
  categoriesChecked: Array<string> = [];

  categoriesJson: Category[] = [];
  categoriesArray: string[] = [];

  totalProducts: number | null | undefined;
  totalPages: number | null | undefined;
  pages: number[] | null | undefined = [];

  from: number = 0;
  page: number | undefined = this._pagingService.page;
  range: number | undefined = this._pagingService.range;

  productList: Product[] | undefined;
  helpText = "Search any product...";


  constructor(private productsService: ProductsService,
              private _usersService: UsersService,
              private cdr: ChangeDetectorRef,
              private _pagingService: PagingService) {
  }

  ngOnInit(): void {
    this.range = this._pagingService.range;
    this.page = this._pagingService.page;
    const userData: User = JSON.parse(window.localStorage.getItem('user') || '{}')
    console.log('UID from home component: ' + userData.uid);
    this.cdr.detectChanges();
    this.productsService.fetchAllCategories()
      .subscribe({
        next: value => {
          this.categoriesJson = value;
          this.categoriesJson.forEach(category => {
            this.categoriesArray?.push(category.category);
          })
        },
        error: err => console.error(err),
        complete: () => {
          // this.productsService.fetchTotalProducts()
          this.range = this._pagingService.range;

          this.getAllProducts(); // todo: 1st call
        }
      });
  }

  public setText(text: string) {
    this.filterText = text;
    this.page = 1;
    this.getProducts();
  }

  public setPage(page: number) {
    this.page = page;
    this.getProducts();
    this._pagingService.newPage(page);
  }

  public setRange(range: number) {
    this.range = range;
    this.page = 1;
    this.getProducts();
    this._pagingService.newRange(range);
  }

  public getCategoryProducts(category: string) {
    this.checkArray(category);
    this.page = 1;
    this.getProducts();
  }

  public checkArray(category: string) {
    let flag: boolean = false;
    for (let i = 0; i < this.categoriesChecked.length; i++) {
      if (this.categoriesChecked[i] == category) {
        this.categoriesChecked.splice(i, 1);
        flag = true;
      }
    }
    if (!flag) {
      this.categoriesChecked.push(category);
    }
  }

  public popPages() {
    if (this.totalPages) {
      console.log("popping pages...")
      for (let i = 1; i <= this.totalPages; i++) { // TODO: for (let i = 1; i <= this.totalPages; i++)
        this.pages?.pop();
      }
    }
  }

  public pushPages() {
    if (this.totalPages) {
      for (let i = 1; i <= this.totalPages; i++) {
        this.pages?.push(i);
      }
    }
  }

  public getProducts() {
    this.popPages(); // TODO: put this at the start of getProducts()
    if (this.filterText != '') {
      if (this.categoriesChecked.length == 0) {
        this.getFilteredProducts();
      } else {
        this.getFilteredProductsByCategories();
      }
    } else {
      if (this.categoriesChecked.length == 0) {
        this.getAllProducts();
      } else {
        this.getProductsByCategories();
      }
    }
  }

  public getAllProducts() {
    this.popPages();
    this.productsService.fetchProducts(this.page, this.range)
      .subscribe({
        next: (value) => {
          this.totalProducts = value.totalCount;
          this.totalPages = Math.ceil(value.totalCount / Number(this.range));
          this.pushPages();
          this.productList = value.products;
        },
        error: err => console.error(err)
      });
  }

  public getFilteredProducts() {
    this.productsService.fetchFilteredProducts(this.filterText, this.page, this.range) // TODO: rename to fetchTotalFilteredProducts
      .subscribe({
        next: (value) => {
          this.totalProducts = value.totalCount;
          this.totalPages = Math.ceil(value.totalCount / Number(this.range))
          console.log("totalPages: " + this.totalPages);
          this.pushPages();
          this.productList = value.products;
        },
        error: err => console.error(err)
      });
  }

  public getFilteredProductsByCategories() {
    this.productsService.fetchFilteredProductsByCategories(this.filterText, this.categoriesChecked, this.page, this.range)
      .subscribe({
        next: (value) => {
          this.totalProducts = value.totalCount;
          this.totalPages = Math.ceil(value.totalCount / Number(this.range))
          this.pushPages();
          this.productList = value.products;
        },
        error: err => console.error(err)
      });
  }

  public getProductsByCategories() {
    this.productsService.fetchProductsByCategories(this.categoriesChecked, this.page, this.range) // TODO: rename to fetchTotalFilteredProducts
      .subscribe({
        next: (value) => {
          this.totalProducts = value.totalCount;
          this.totalPages = Math.ceil(value.totalCount / Number(this.range))
          this.pushPages();
          this.productList = value.products;
        },
        error: err => console.error(err)
      });
  }

}
