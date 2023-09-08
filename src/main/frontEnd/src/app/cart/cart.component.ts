import {Component, OnChanges, OnInit} from '@angular/core';
import {CartService} from "../services/cart.service";
import {ProductsService} from "../services/products.service";
import {debounce, fromEvent, debounceTime, map, Observable, tap, timer} from "rxjs";

import {Item} from "../interfaces/item";
import {UsersService} from "../services/users.service";
import {FormControl, FormGroup} from "@angular/forms";
import {Product} from "../interfaces/product";
import {User} from "../interfaces/user";

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
  providers: [CartService, ProductsService]
})
export class CartComponent implements OnInit {

  items: Item[] | undefined;
  currentUser: User = JSON.parse(window.localStorage.getItem('user') || '{}');

  // cart: any;any

  constructor(private _cartService: CartService,
              private _productsService: ProductsService,
              private _usersService: UsersService) {
  }

  ngOnInit(): void {
    this.fetchCart();
  }

  fetchCart(): void {
    this._cartService.getCart(this.currentUser.uid)
      .subscribe({
        next: response => this.items = response
      });
  }

  // TODO: wait for a few seconds before sending the request
  update(event: { p: Product, q: number }) {
    this._cartService
      .addToCart(this.currentUser.uid, event.p.pid, event.q)
      .subscribe({
        next: result => {
          console.log("updated: ")
          this.fetchCart()
        }
      })
  }

  // increase(event: { p: Product, q: number }) {
  //   this._cartService
  //     .addToCart(this._usersService.user?.uid, event.p.pid, event.q)
  //     .subscribe({
  //       next: result => {
  //         console.log("syn: ")
  //         this.fetchCart()
  //       }
  //     })
  // }

  // changeQuantity(event: { p: Product, q: number }) {
  //   this._cartService
  //     .addToCart(this._usersService.user?.uid, event.p.pid, event.q)
  //     .subscribe(result => console.log(result))
  // }

  remove(product: Product) {
    this._cartService
      .addToCart(this.currentUser.uid, product.pid, 0)
      .subscribe({
        next: result => {
          console.log("diagrafi: " + result)
          this.fetchCart()
        }
      });
  }

}
