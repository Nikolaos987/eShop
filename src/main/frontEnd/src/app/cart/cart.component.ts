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
    // TODO: put the uid for the logged in user
    this.items = this._cartService.getCart(this._usersService.user.uid);

// .pipe(
//   tap(cart => console.log(cart)),
//   map(cart => cart.cid));
  }

}
