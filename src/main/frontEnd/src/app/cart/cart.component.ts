import {Component, OnChanges, OnInit} from '@angular/core';
import {CartService} from "../services/cart.service";
import {ProductsService} from "../services/products.service";

import {Item} from "../interfaces/item";
import {UsersService} from "../services/users.service";
import {Product} from "../interfaces/product";
import {User} from "../interfaces/user";

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
  providers: [CartService, ProductsService]
})
export class CartComponent implements OnInit {

  totalPrice: number | undefined;
  items: Item[] | undefined;
  currentUser: User = JSON.parse(window.localStorage.getItem('user') || '{}');

  constructor(private _cartService: CartService,
              private _productsService: ProductsService,
              private _usersService: UsersService) {
  }

  ngOnInit(): void {
    this.fetchCart();
    // this.fetchTotalPrice();
  }

  fetchCart(): void {
    this._cartService.getCart(this.currentUser.uid)
      .subscribe({
        next: response => {
          this.items = response.cartItems;
          this.totalPrice = response.totalPrice;
        }
      });
  }

  fetchTotalPrice() {
    this._cartService.getTotalPrice(this.currentUser.uid)
      .subscribe({
        next: totalPriceJson => this.totalPrice = totalPriceJson.total_price,
        error: err => console.error(err)
      })
  }

  update(event: { p: Product, q: number }) {
    this._cartService
      .addToCart(this.currentUser.uid, event.p.pid, event.q)
      .subscribe({
        next: result => {
          console.log("updated: ");
          this.fetchCart();
          // this.fetchTotalPrice();
        }
      })
  }

  remove(product: Product) {
    this._cartService
      .addToCart(this.currentUser.uid, product.pid, 0)
      .subscribe({
        next: result => {
          console.log("diagrafi: " + result)
          this.fetchCart();
          // this.fetchTotalPrice();
        }
      });
  }

}
