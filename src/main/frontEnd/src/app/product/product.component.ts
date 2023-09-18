import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CartService} from "../services/cart.service";
import {UsersService} from "../services/users.service";
import {FormControl, FormGroup} from "@angular/forms";
import {Product} from "../interfaces/product";
import {Item} from "../interfaces/item";

@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.css'],
  providers: [CartService]
})
export class ProductComponent implements OnInit {
  // @Input() image: string | undefined;

  // @Input() item: Item = {
  //   pid: undefined,
  //   price: 0,
  //   quantity: 0,
  //   name: undefined
  // };
  @Input() product: any;
  @Input() quant: number | undefined;

  @Output() removeItem = new EventEmitter<Product>();
  @Output() updateItem = new EventEmitter<{p: Product, q: number}>();

  // TODO: remove constructor, unused declarations
  constructor(private _cartService: CartService, private usersService: UsersService) {
  }

  ngOnInit() {
  }

  // TODO: wait for a few seconds before sending the request
  updateQuantity(quantity: number) {
    this.updateItem.emit({p: this.product, q: quantity}) // this at the end
    console.log("this.product.price: " + this.product.price +
      "\nthis.product.quantity: " + this.product.quantity +
      "\nquant: " + this.quant +
      "\nquantity: " + quantity)
    console.log("update: " + "this.product.price = (" + this.product.price + "/(" + (quantity + 1) + ")) * " + quantity)
    // this.product.price = (this.product.price/(quantity+1)) * quantity;
  }

  // increase(quantity: number) {
  //   this.incrementItem.emit({p: this.product, q: quantity});
  //   console.log("this.product.price: " + this.product.price + "\nthis.product.quantity: " + this.product.quantity + "\nquant: " + this.quant + "\nquantity: " + quantity)
  //   console.log("increase: " + "this.product.price = (" + this.product.price + "/(" + (quantity - 1) + ")) * " + quantity)
  //   // this.product.price = (this.product.price/(quantity-1)) * quantity;
  // }

  remove() {
    this.removeItem.emit(this.product);
  }

}
