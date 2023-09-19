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
export class HomeComponent implements OnInit, AfterViewInit {
  @ViewChild('checkbox') checkElement: ElementRef | undefined;

  filterText: string = '';
  categorySelected: string = '';
  categoriesChecked: Array<string> = [];

  categoriesJson: Category[] = [];
  categoriesArray: string[] = [];

  products: Product[] = [];
  totalProducts: number | null | undefined;
  totalPages: number | null | undefined;
  pages: number[] | null | undefined = [];

  from: number = 0;
  page: number = 1;
  range: number | undefined = this._pagingService.page?.range;

  productList: Product[] | undefined;
  helpText = "Search any product...";


  constructor(private productsService: ProductsService,
              private _usersService: UsersService,
              private cdr: ChangeDetectorRef,
              private _pagingService: PagingService) {
  }

  ngAfterViewInit() {
    this.checkElement?.nativeElement.addEventListener('click', this.check.bind(this));
    this.checkElement?.nativeElement.addEventListener('unchecked', this.uncheck.bind(this));
  }

  public check(event: any) {

  }
  public uncheck(event: any) {

  }

  ngOnInit(): void {
    this.cdr.detectChanges();
    this.productsService.fetchAllCategories()
      .subscribe({
        next: value => {
          this.categoriesJson = value;
          this.categoriesJson.forEach(category => {
            this.categoriesArray?.push(category.category);
          })
          // console.log(this.categoriesArray);

          // console.log("categoriesArray: " + this.categoriesArray);
          // this.categoriesArray.forEach(category => {
          //   console.log("categories: " + category);
          // })
        },
        error: err => console.error(err)
      })

    const userData: User = JSON.parse(window.localStorage.getItem('user') || '{}')
    console.log('UID from home component: ' + userData.uid);
    this.productsService.fetchTotalProducts()
      .subscribe({
        next: (result) => {
          this.totalProducts = result.totalProducts;
          this.totalPages = Math.ceil(result.totalProducts / Number(this.range))
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
      if (this.categorySelected == '') {
        this.productsService.fetchProducts(page, this.range)
          .subscribe({
            next: value => {
              this.productList = value;
            },
            error: err => console.error(err)
          })
      } else {
        if (this.range)
          this.categorizedProducts(this.page, this.range);
      }
    } else {
      // kai edw:  (this.categorySelected == '')... else
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
    if (this.filterText == '') {
      if (this.categorySelected != '') {
        this.getCategoryProducts(this.categorySelected)
      } else {
        this.getProducts(this.page)
      }
    } else
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
          this.totalProducts = result.totalProducts;
          this.totalPages = Math.ceil(result.totalProducts / Number(this.range))
          for (let i = 1; i <= this.totalPages; i++) {
            this.pages?.push(i);
          }
        },
        error: (error: Error) => {
          console.error(error);
        },
        complete: () => {
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

  public getCategoryProducts(category: string) {
    this.checkArray(category);
    this.categorySelected = category;
    if (this.totalPages) {
      for (let i = 1; i <= this.totalPages; i++) {
        this.pages?.pop(); // removing the old pages
      }
    }
    this.productsService.fetchTotalProductsByCategories(this.categoriesChecked) // TODO: this.categoriesChecked
      .subscribe({
        next: (value) => {
          console.log("value: " + value.totalProducts);
          this.totalProducts = value.totalProducts;
          this.totalPages = Math.ceil(value.totalProducts / Number(this.range))
          for (let i = 1; i <= this.totalPages; i++) {
            this.pages?.push(i);
          }
        },
        error: err => console.error(err),
        complete: () => {
          this.page = 1;
          if (this.range) {
            this.categorizedProducts(this.page, this.range)
          }
        }
      });
    this.categorySelected = '';
    // if (this.filterText == '') {
    //   this.productsService.fetchProducts(page, this.range)
    //     .subscribe({
    //       next: value => {
    //         this.productList = value;
    //       },
    //       error: err => console.error(err)
    //     })
    // } else {
    //   if (this.range)
    //     this.filteredProducts(this.filterText, this.page, this.range)
    // }
  }

  public categorizedProducts(page: number, range: number) {
    this.productsService.fetchProductsByCategories(this.categoriesChecked, this.page, this.range) // TODO
      .subscribe({
        next: value => {
          this.productList = value;
        },
        error: (error: Error) => {
          console.error(error)
        }
      })
  }

  public checkArray(category: string) {
    let flag: boolean = false;
    for (let i=0; i<this.categoriesChecked.length; i++) {
      if (this.categoriesChecked[i] == category) {
        this.categoriesChecked.splice(i, 1);
        flag = true;
      }
    }
    if (!flag) {
      this.categoriesChecked.push(category);
    }
  }

}
