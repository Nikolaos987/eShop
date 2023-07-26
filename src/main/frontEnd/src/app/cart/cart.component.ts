import {Component, OnInit} from '@angular/core';
import { CartService } from "../services/cart.service";

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
  providers: [CartService]
})
export class CartComponent implements OnInit{

  items:any;

  constructor(private cartService: CartService) {
  }

  ngOnInit(): void {
    // TODO: put the uid for the logged in user
    this.items = this.cartService.getCart('14bc8ced-3049-4df2-a262-fe36213617c8');
  }

}
