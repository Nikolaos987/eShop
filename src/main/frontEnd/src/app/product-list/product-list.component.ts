import {Component, Input, OnInit, Output} from '@angular/core';
import {HomeComponent} from "../home/home.component";
import {take} from "rxjs";
import {MatPaginatorModule} from "@angular/material/paginator";

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['../home/home.component.css', './product-list.component.css'],
  providers: [],
})
export class ProductListComponent implements OnInit {

  // public product: any;
  @Input() image: any;
  @Input() products: any;

  constructor(private homeComponent: HomeComponent) {
  }

  ngOnInit(): void {
    this.homeComponent.getProducts();

    // this.productsService.fetchProducts()
    //   .pipe(take(1))
    //   .subscribe(response => this.productList = response);
  }

}
