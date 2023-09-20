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
  page: number = 1;
  range: number | undefined = this._pagingService.page?.range;

  productList: Product[] | undefined;
  helpText = "Search any product...";


  constructor(private productsService: ProductsService,
              private _usersService: UsersService,
              private cdr: ChangeDetectorRef,
              private _pagingService: PagingService) {
  }

  ngOnInit(): void {
    this.range = this._pagingService.page?.range;
    const userData: User = JSON.parse(window.localStorage.getItem('user') || '{}')
    console.log('UID from home component: ' + userData.uid);
    this.cdr.detectChanges();
    this.productsService.fetchAllCategories()
      .subscribe({
        next: value => {
          this.popPages();
          this.categoriesJson = value;
          this.categoriesJson.forEach(category => {
            this.categoriesArray?.push(category.category);
          })
        },
        error: err => console.error(err),
        complete: () => {
          this.popPages();
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
              },
              complete: () => {
                // this.getProducts() //TODO: (this.page)
                this.range = this._pagingService.page?.range;
                this.getProducts();
              }
            });
        }
      })
  }

  // public getProducts(page: number) {
  //   this.page = page;
  //   console.log("from getProducts(): page = " + this.page)
  //   if (this.filterText == '') {
  //     if (this.categoriesChecked.length == 0) {
  //       this.productsService.fetchProducts(page, this.range)
  //         .subscribe({
  //           next: value => {
  //             this.productList = value;
  //           },
  //           error: err => console.error(err)
  //         })
  //     } else {
  //       if (this.range)
  //         this.categorizedProducts(this.page, this.range);
  //     }
  //   } else {
  //     if (this.categoriesChecked.length == 0) {
  //       if (this.range)
  //         this.filteredProducts(this.filterText, this.page, this.range)
  //     } else {
  //       this.getTotalFilteredProductsByCategories();
  //       this.getFilteredProductsByCategories();
  //     }
  //   }
  // }
  //
  // changeRange(r: number) {
  //   if (this.totalPages)
  //     for (let i = 1; i <= this.totalPages; i++) {
  //       this.pages?.pop(); // removing the old pages
  //     }
  //   this.range = r;
  //   if (typeof this.totalProducts == "number") {
  //     this.totalPages = Math.ceil(this.totalProducts / Number(this.range))
  //     for (let i = 1; i <= this.totalPages; i++) {
  //       this.pages?.push(i);
  //     }
  //   }
  //   // if the total pages become less after the range is changed, move to the last possible page
  //   if (this.pages && this.page > this.pages.length) {
  //     this.page = this.pages.length;
  //   }
  //   this._pagingService.newRange(r);
  //   if (this.filterText == '') {
  //     if (this.categorySelected != '') {
  //       this.getCategoryProducts(this.categorySelected)
  //     } else {
  //       this.getProducts(this.page)
  //     }
  //   } else
  //     this.getFilteredProducts(this.filterText);
  // }
  //
  // prevPage() {
  //   if (this.page > 1) {
  //     this.page = this.page - 1;
  //     this.getProducts(this.page);
  //   }
  // }
  //
  // nextPage() {
  //   if (this.totalPages && this.page < this.totalPages) {
  //     this.page = this.page + 1;
  //     this.getProducts(this.page);
  //   }
  // }
  //
  // public getFilteredProducts(text: string) {
  //   this.filterText = text;
  //   if (this.totalPages)
  //     for (let i = 1; i <= this.totalPages; i++) {
  //       this.pages?.pop();
  //     }
  //   if (this.categoriesChecked.length == 0) {
  //     this.productsService.fetchTotalSearchedProducts(text)
  //       .subscribe({
  //         next: (result) => {
  //           this.totalProducts = result.totalProducts;
  //           this.totalPages = Math.ceil(result.totalProducts / Number(this.range))
  //           for (let i = 1; i <= this.totalPages; i++) {
  //             this.pages?.push(i);
  //           }
  //         },
  //         error: (error: Error) => {
  //           console.error(error);
  //         },
  //         complete: () => {
  //           this.page = 1;
  //           if (this.range)
  //             this.filteredProducts(text, this.page, this.range)
  //         }
  //       })
  //   } else {
  //     this.getTotalFilteredProductsByCategories();
  //     this.getFilteredProductsByCategories();
  //   }
  // }
  //
  // public filteredProducts(text: string, page: number, range: number) {
  //   this.productsService.fetchFilteredProducts(text, this.page, this.range)
  //     .subscribe({
  //       next: value => {
  //         this.productList = value;
  //       },
  //       error: (error: Error) => {
  //         console.error(error)
  //       }
  //     })
  // }
  //
  //
  // public getTotalFilteredProductsByCategories() {
  //   this.productsService.fetchTotalFilteredProductsByCategories(this.categoriesChecked, this.filterText)
  //     .subscribe({
  //       next: (value) => {
  //         console.log("value: " + value.totalProducts);
  //         this.totalProducts = value.totalProducts;
  //         this.totalPages = Math.ceil(value.totalProducts / Number(this.range))
  //         for (let i = 1; i <= this.totalPages; i++) {
  //           this.pages?.push(i);
  //         }
  //       },
  //       error: err => console.error(err),
  //       complete: () => {
  //         this.page = 1;
  //         if (this.range) {
  //           // if (this.categoriesChecked.length > 0) {
  //           //   this.categorizedProducts(this.page, this.range)
  //           // } else {
  //             this.getProducts(this.page);
  //           // }
  //         }
  //       }
  //     })
  // }
  //
  // public getFilteredProductsByCategories() {
  //   this.productsService.fetchFilteredProductsByCategories(this.filterText, this.categoriesChecked, this.page, this.range)
  //     .subscribe({
  //       next: value => {
  //         this.productList = value;
  //       },
  //       error: (error: Error) => {
  //         console.error(error)
  //       }
  //     })
  // }
  //
  // public categorizedProducts(page: number, range: number) {
  //   this.productsService.fetchProductsByCategories(this.categoriesChecked, this.page, this.range) // TODO
  //     .subscribe({
  //       next: value => {
  //         this.productList = value;
  //       },
  //       error: (error: Error) => {
  //         console.error(error)
  //       }
  //     })
  // }

  public setText(text: string) {
    this.filterText = text;
  }
  public setPage(page: number) {
    this.page = page;
    this.getProducts();
  }
  public setRange(range: number) {
    this.range = range;
    this.getProducts();
    this._pagingService.newRange(range);
  }

  public getCategoryProducts(category: string) {
    this.checkArray(category);
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
    if (this.totalPages)
      for (let i = 1; i <= this.totalPages; i++) {
        this.pages?.pop();
      }
  }

  public getProducts() {
    // this.page = page;
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
    this.productsService.fetchTotalProducts()
      .subscribe({
        next: (value) => {
          console.log("value: " + value.totalProducts);
          this.totalProducts = value.totalProducts;
          this.totalPages = Math.ceil(value.totalProducts / Number(this.range))
          for (let i = 1; i <= this.totalPages; i++) {
            this.pages?.push(i);
          }
          // if the total pages become less after the range is changed, move to the last possible page
          if (this.pages && this.page > this.pages.length) {
            this.page = this.pages.length;
          }
        },
        error: err => console.error(err),
        complete: () => {
          this.productsService.fetchProducts(this.page, this.range)
            .subscribe({
              next: value => {
                this.productList = value;
              },
              error: err => console.error(err)
            });
        }
      })

  }

  public getFilteredProducts() {
    this.productsService.fetchTotalSearchedProducts(this.filterText) // TODO: rename to fetchTotalFilteredProducts
      .subscribe({
        next: (value) => {
          console.log("value: " + value.totalProducts);
          this.totalProducts = value.totalProducts;
          this.totalPages = Math.ceil(value.totalProducts / Number(this.range))
          for (let i = 1; i <= this.totalPages; i++) {
            this.pages?.push(i);
          }
          // if the total pages become less after the range is changed, move to the last possible page
          if (this.pages && this.page > this.pages.length) {
            this.page = this.pages.length;
          }
        },
        error: err => console.error(err),
        complete: () => {
          this.productsService.fetchFilteredProducts(this.filterText, this.page, this.range)
            .subscribe({
              next: value => {
                this.productList = value;
              },
              error: err => console.error(err)
            });
        }
      });
  }

  public getFilteredProductsByCategories() {
    this.productsService.fetchTotalFilteredProductsByCategories(this.categoriesChecked, this.filterText) // TODO: rename to fetchTotalFilteredProducts
      .subscribe({
        next: (value) => {
          console.log("value: " + value.totalProducts);
          this.totalProducts = value.totalProducts;
          this.totalPages = Math.ceil(value.totalProducts / Number(this.range))
          for (let i = 1; i <= this.totalPages; i++) {
            this.pages?.push(i);
          }
          // if the total pages become less after the range is changed, move to the last possible page
          if (this.pages && this.page > this.pages.length) {
            this.page = this.pages.length;
          }
        },
        error: err => console.error(err),
        complete: () => {
          this.productsService.fetchFilteredProductsByCategories(this.filterText, this.categoriesChecked, this.page, this.range)
            .subscribe({
              next: value => {
                this.productList = value;
              },
              error: err => console.error(err)
            });
        }
      });
  }

  public getProductsByCategories() {
    this.productsService.fetchTotalProductsByCategories(this.categoriesChecked) // TODO: rename to fetchTotalFilteredProducts
      .subscribe({
        next: (value) => {
          console.log("value: " + value.totalProducts);
          this.totalProducts = value.totalProducts;
          this.totalPages = Math.ceil(value.totalProducts / Number(this.range))
          for (let i = 1; i <= this.totalPages; i++) {
            this.pages?.push(i);
          }
          // if the total pages become less after the range is changed, move to the last possible page
          if (this.pages && this.page > this.pages.length) {
            this.page = this.pages.length;
          }
        },
        error: err => console.error(err),
        complete: () => {
          this.productsService.fetchProductsByCategories(this.categoriesChecked, this.page, this.range)
            .subscribe({
              next: value => {
                this.productList = value;
              },
              error: err => console.error(err)
            });
        }
      });
  }

}
