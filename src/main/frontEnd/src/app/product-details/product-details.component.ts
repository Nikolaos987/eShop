import {Component, Input, OnInit} from '@angular/core';
import {ProductsService} from "../services/products.service";
import {ActivatedRoute} from '@angular/router';
import {Location} from '@angular/common';
import {CartService} from "../services/cart.service";
import {UsersService} from "../services/users.service";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.css'],
  providers: [ProductsService, CartService]
})
export class ProductDetailsComponent implements OnInit {
  @Input() product?: any;

  // quantity: number = 1;

  addToCartForm = new FormGroup({
    quantity: new FormControl(1)
  })

  constructor(
    private productsService: ProductsService,
    private cartService: CartService,
    private route: ActivatedRoute,
    private location: Location,
    private _usersService: UsersService
  ) {}

  ngOnInit(): void {
    this.getProduct();
  }

  getProduct(): void {
    const pid = String(this.route.snapshot.paramMap.get('pid'));

    this.productsService.fetchProduct(pid)
      .subscribe(response => this.product = response)
  }

  addToCart() {
    if (this._usersService.user?.isLoggedIn) {
      this.cartService.addToCart(this._usersService.user?.uid, this.product.pid, this.addToCartForm.value.quantity)
        .subscribe((response) => window.alert(this.product.name + ' has been added to the cart'));
    } else
      window.alert('you are not logged in!');
  }

  stepDown(quantity: any) {
    // if (this.addToCartForm.controls['quantity'].value != null && this.addToCartForm.controls['quantity'].value > 1) {
      this.addToCartForm.controls['quantity'].setValue(quantity);
    // }
  }

  stepUp(quantity: any) {
    // if (this.addToCartForm.controls['quantity'].value != null && this.addToCartForm.controls['quantity'].value < 9) {
      this.addToCartForm.controls['quantity'].setValue(quantity);
    // }
  }
}
