import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CartService} from "../services/cart.service";
import {UsersService} from "../services/users.service";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.css'],
  providers: [CartService]
})
export class ProductComponent implements OnInit {
  @Input() image: any;
  @Input() product: any;
  @Input() quant: number = 0;

  @Output() removeItem = new EventEmitter<any>();
  @Output() incrementItem = new EventEmitter<{p: any, q: number}>();
  @Output() decrementItem = new EventEmitter<{p: any, q: number}>();

  // TODO: remove constructor, unused declarations
  constructor(private _cartService: CartService, private usersService: UsersService) {
  }

  ngOnInit() {
  }

  // TODO: wait for a few seconds before sending the request
  decrease(quantity: any) {
    // if (quantity > 1) {
      // quantity = quantity - 1;
      this.decrementItem.emit({p: this.product, q: quantity})
    // }
  }

  increase(quantity: any) {
    // if (quantity < 9) {
      // quantity = quantity + 1;
      this.incrementItem.emit({p: this.product, q: quantity});
    // }
  }

  remove() {
    this.removeItem.emit(this.product);
  }

}
