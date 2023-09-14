import {Component, Input, OnInit} from '@angular/core';
import {ProductsService} from "../services/products.service";
import {ActivatedRoute} from '@angular/router';
import {Location} from '@angular/common';
import {CartService} from "../services/cart.service";
import {UsersService} from "../services/users.service";
import {FormControl, FormGroup} from "@angular/forms";
import {Product} from "../interfaces/product";
import {User} from "../interfaces/user";

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.css'],
  providers: [ProductsService, CartService]
})
export class ProductDetailsComponent implements OnInit {
  @Input() product: Product = {
    pid: String(this.route.snapshot.paramMap.get('pid')),
    name: undefined,
    // image: undefined,
    description: undefined,
    price: undefined,
    quantity: undefined,
    brand: undefined,
    category: undefined,
  };

  productExists: boolean = false;

  // quantity: number = 1;

  addToCartForm = new FormGroup({
    quantity: new FormControl(1)
  })

  formData: FormData = new FormData();
  errorMessage: string = '';
  successMessage: string = '';

  currentUser: User = JSON.parse(window.localStorage.getItem('user') || '{}');

  constructor(
    private productsService: ProductsService,
    private cartService: CartService,
    private route: ActivatedRoute,
    private location: Location,
    private _usersService: UsersService
  ) {
  }

  ngOnInit(): void {
    this.getProduct();
  }

  getProduct(): void {
    // const pid = String(this.route.snapshot.paramMap.get('pid'));
    this.productsService.fetchProduct(this.product.pid)
      .subscribe({
        next: product => {
          this.productExists = true;
          this.product = product
        },
        error: err => {
          this.productExists = false;
          console.error(err);
        }
      })
  }

  addToCart() {
    // if (this._usersService.user?.isLoggedIn) {
      if (window.localStorage.getItem('user')) {
        this.cartService.addToCart(this.currentUser.uid, this.product.pid, this.addToCartForm.value.quantity)
          .subscribe(
            x => {
              console.log("next: " + x)
              window.alert(this.product.name + ' has been added to your cart')
            },
            err => console.error(err),
            () => console.log("complete")
          )

      // .subscribe((response) => window.alert(this.product.name + ' has been added to the cart'));
    } else
      window.alert('you are not logged in!');
  }

  stepDown() {
    if (this.addToCartForm.controls['quantity'].value != null && this.addToCartForm.controls['quantity'].value > 1) {
      this.addToCartForm.controls['quantity'].setValue(
        this.addToCartForm.controls['quantity'].value - 1);
    }
  }

  stepUp() {
    if (this.addToCartForm.controls['quantity'].value != null && this.addToCartForm.controls['quantity'].value < 9) {
      this.addToCartForm.controls['quantity'].setValue(
        this.addToCartForm.controls['quantity'].value + 1);
    }
  }

  uploadImage() {
    this.productsService.uploadImage(this.product.pid, this.formData)
      .subscribe({
        // next: value => this.getProduct()  // για να φορτώνει αυτόματα την φωτογραφία
        next: value => {
          this.errorMessage = "";
          this.successMessage = "Image Uploaded!";
        },
        error: err => {
          this.errorMessage = err;
          this.successMessage = "";
        }
      });
  }

  getFile(event: any) {
    if (event.target.files.length > 0) {
      const file: File = event.target.files[0];
      this.formData.append('file', file);
      console.log("form data: " + this.formData);
    }
  }

  testClick() {
    this._router.navigateByUrl('/details/4e323bc6-e034-40b5-a98c-8b48c9da3050');
  }
}
