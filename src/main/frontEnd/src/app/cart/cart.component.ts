import {Component, OnChanges, OnInit} from '@angular/core';
import {CartService} from "../services/cart.service";
import {ProductsService} from "../services/products.service";
import {map, Observable, tap} from "rxjs";

import {Item} from "../interfaces/item";
import {UsersService} from "../services/users.service";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
  providers: [CartService, ProductsService]
})
export class CartComponent implements OnInit {

  items: Observable<Item[]> | undefined;
  cart: any;

  constructor(private _cartService: CartService,
              private _productsService: ProductsService,
              private _usersService: UsersService) {
  }

  ngOnInit(): void {
    this.fetchCart();
  }

  fetchCart(): void {
    this.items = this._cartService.getCart(this._usersService.user?.uid);
  }

  // TODO: wait for a few seconds before sending the request
  decrease(event: any) {
    this._cartService.addToCart(this._usersService.user?.uid, event.p.pid, event.q).subscribe()
  }

  increase(event: any) {
    // stock = api call PRODUCT BY ID (ID = this.product.pid
    this._cartService.addToCart(this._usersService.user?.uid, event.p.pid, event.q).subscribe()
  }

  remove(product: any) {
    this._cartService.addToCart(this._usersService.user?.uid, product.pid, 0)
      .subscribe(response => {
        this.items = this._cartService.getCart(this._usersService.user?.uid);
      });
  }
}
