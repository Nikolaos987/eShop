import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CartService} from "../services/cart.service";
import {UsersService} from "../services/users.service";

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
  @Output() decrementItem = new EventEmitter<any>();

  constructor(private _cartService: CartService, private usersService: UsersService) {
  }

  ngOnInit() {
  }

  // TODO: wait for a few seconds before sending the request
  decrease() {
    this.decrementItem.emit({p: this.product, q: this.quant})
  }

  increase() {
    this.incrementItem.emit({p: this.product, q: this.quant});

  }

  remove() {
    this.removeItem.emit(this.product);
  }

}
