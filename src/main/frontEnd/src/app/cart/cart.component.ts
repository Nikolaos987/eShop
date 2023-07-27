import {Component, OnInit} from '@angular/core';
import { CartService } from "../services/cart.service";
import {ProductsService} from "../services/products.service";
import {map, tap} from "rxjs";

import { Item } from "../item";

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
  providers: [CartService, ProductsService]
})
export class CartComponent implements OnInit{

  items:any;

  cart:any;

  constructor(private _cartService:CartService, private _productsService:ProductsService) {
  }

  ngOnInit(): void {
    // TODO: put the uid for the logged in user
    this.items = this._cartService.getCart('14bc8ced-3049-4df2-a262-fe36213617c8');

// .pipe(
//   tap(cart => console.log(cart)),
//   map(cart => cart.cid));
  }

}
