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
  pages: number[] = [];

  pagesList: Array<Array<number>> = [];
  groupLength: number = 4;
  pagesGroupIndex: number = 0;

  from: number = 0;
  page: number | undefined = this._pagingService.page;
  range: number | undefined = this._pagingService.range;

  flag: boolean = false;

  productList: Product[] | undefined;
  helpText = "Search any product...";


  constructor(private productsService: ProductsService,
              private _usersService: UsersService,
              private cdr: ChangeDetectorRef,
              private _pagingService: PagingService) {
  }

  ngOnInit(): void {
    this.flag = true;
    this.pagesGroupIndex = this._pagingService.pageGroup;
    this.range = this._pagingService.range;
    this.page = this._pagingService.page;
    const userData: User = JSON.parse(window.localStorage.getItem('user') || '{}')
    console.log('UID from home component: ' + userData.uid);
    this.cdr.detectChanges();
    this.productsService.fetchAllCategories()
      .subscribe({
        next: value => {
          this.categoriesJson = value.categories;
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
    this.flag = true;
    this.filterText = text;
    this.pagesGroupIndex = 0;
    this._pagingService.newPageGroup(this.pagesGroupIndex);
    this.page = 1;
    this.getProducts();
  }

  public setPage(page: number) {
    this.page = page;
    this.getProducts();
    this._pagingService.newPage(page);
  }

  public setPageGroupIndex(index: number) {
    this.pagesGroupIndex = index;
    this._pagingService.newPageGroup(this.pagesGroupIndex);
    // this.page = this.pagesList[this.pagesGroupIndex].length;
    this.page = this.pagesList[this.pagesGroupIndex][0];
    this.getProducts();
  }

  public setRange(range: number) {
    this.flag = true;
    this.range = range;
    this.pagesGroupIndex = 0;
    this._pagingService.newPageGroup(this.pagesGroupIndex);
    this.page = 1;
    this.getProducts();
    this._pagingService.newRange(range);
  }

  public getCategoryProducts(category: string) {
    this.flag = true;
    this.checkArray(category);
    this.pagesGroupIndex = 0;
    this._pagingService.newPageGroup(this.pagesGroupIndex);
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
      // for (let i = 1; i <= this.totalPages; i++) { // TODO: for (let i = 1; i <= this.totalPages; i++)
      //   this.pages?.pop();
      // }
      for (let i = 0; i < this.pagesList.length; i++) {
        for (let j = 0; j < this.pagesList[i].length; j++) {
          this.pages?.pop(); // at().at();
        }
      }
    }
  }

  public dividePages() {
    if (this.totalPages) {
      let shares: number = Math.ceil(this.totalPages / this.groupLength);
      for (let i = 0; i < shares; i++) {
        let foo: number[] = [];
        for (let j = 1; j <= this.groupLength; j++) { //todo: here
          if (i * this.groupLength + j <= this.totalPages) {
            foo.push(i * this.groupLength + j);
          }
        }
        this.pagesList.push(foo);
        // this.pagesList[i] = this.pages;
      }
      // for (let i = 0; i < this.pagesList[this.pagesGroupIndex].length; i++) {
      //   this.pages?.push(this.pagesList[this.pagesGroupIndex][i]); // at().at();
      // }
      // this.page = this.pages[0];
    }
  }

  public pushPages() {
    if (this.totalPages) {
      for (let i = 0; i < this.pagesList[this.pagesGroupIndex].length; i++) {
        this.pages?.push(this.pagesList[this.pagesGroupIndex][i]); // at().at();
      }
      //   for (let i = 1; i <= this.totalPages; i++) {
      //     this.pages?.push(i);
      //   }
    }
  }

  public getProducts() {
    this.popPages(); // TODO: put this at the start of getProducts() FAULTYYYYYYYYY
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
    this.flag = false;
  }

  public getAllProducts() {
    this.popPages();
    this.productsService.fetchProducts(this.page, this.range)
      .subscribe({
        next: (value) => {
          this.totalProducts = value.totalCount;
          this.totalPages = Math.ceil(value.totalCount / Number(this.range));
          this.pagesList = [];
          this.dividePages();
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
          this.pagesList = [];
          this.dividePages();
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
          this.pagesList = [];
          this.dividePages();
          this.pushPages();
          this.productList = value.products;
        },
        error: err => console.error(err)
      });
  }

  public getProductsByCategories() {
    this.productsService.fetchProductsByCategories(this.categoriesChecked, this.page, this.range)
      .subscribe({
        next: (value) => {
          this.totalProducts = value.totalCount;
          this.totalPages = Math.ceil(value.totalCount / Number(this.range))
          this.pagesList = [];
          this.dividePages();
          this.pushPages();
          this.productList = value.products;
        },
        error: err => console.error(err)
      });
  }

}
