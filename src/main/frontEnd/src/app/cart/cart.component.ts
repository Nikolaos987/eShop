import {Component, OnInit} from '@angular/core';
import { CartService } from "../services/cart.service";
import {ProductsService} from "../services/products.service";
import {map, tap} from "rxjs";

import { Item } from "../item";
import {UsersService} from "../services/users.service";

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
  providers: [CartService, ProductsService]
})
export class CartComponent implements OnInit{

  items:any;

  cart:any;

  constructor(private _cartService:CartService, private _productsService:ProductsService, private _usersService: UsersService) {
  }

  ngOnInit(): void {
    this.items = this._cartService.getCart(this._usersService.user?.uid);
  }

  // TODO: wait for a few seconds before sending the request
  decrease(product: any, quant: number) {
    if (product.quantity > 1) {
      product.quantity -= 1;
      this.items = this._cartService.addToCart(this._usersService.user?.uid, product.pid, product.quantity)
        .subscribe(response => {
          this.items = this._cartService.getCart(this._usersService.user?.uid);
        });
    }
  }

  increase(product: any, quant: number) {
    // stock = api call PRODUCT BY ID (ID = this.product.pid
    if (product.quantity < 9 /* stock */) {
      product.quantity += 1;
      this.items = this._cartService.addToCart(this._usersService.user?.uid, product.pid, product.quantity)
        .subscribe(response => {
          this.items = this._cartService.getCart(this._usersService.user?.uid);
        });
    }
  }

  remove(product: any) {
    this._cartService.addToCart(this._usersService.user?.uid, product.pid, 0)
      .subscribe(response => {
        this.items = this._cartService.getCart(this._usersService.user?.uid);
      });
  }

}
