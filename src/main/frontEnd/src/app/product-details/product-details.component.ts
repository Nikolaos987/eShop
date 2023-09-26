import {AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {ProductsService} from "../services/products.service";
import {ActivatedRoute, Router} from '@angular/router';
import {Location} from '@angular/common';
import {CartService} from "../services/cart.service";
import {UsersService} from "../services/users.service";
import {FormControl, FormGroup} from "@angular/forms";
import {Product} from "../interfaces/product";
import {User} from "../interfaces/user";
import {Pid} from "../interfaces/pid";

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.css'],
  providers: [ProductsService, CartService]
})
export class ProductDetailsComponent implements OnInit {
  currentUser: User = JSON.parse(window.localStorage.getItem('user') || '{}');

  pid: Pid = {
    to_pid: undefined
  }

  // @Input() product: Product = {
  product: Product = {
    // pid: String(this.route.snapshot.paramMap.get('pid')),
    pid: undefined,
    name: undefined,
    description: undefined,
    price: undefined,
    quantity: undefined,
    brand: undefined,
    category: undefined,
  };

  relatedProducts: Product[] = [];

  addToCartForm = new FormGroup({
    quantity: new FormControl(1)
  })

  relationForm = new FormGroup({
    to_pidInput: new FormControl('')
  })

  formData: FormData = new FormData();
  errorMessage: string = '';
  successMessage: string = '';
  productExists: boolean = false;
  relationButtonClicked: boolean = false;
  to_pidInput: string = '';

  constructor(
    private productsService: ProductsService,
    private cartService: CartService,
    private route: ActivatedRoute,
    private location: Location,
    private _usersService: UsersService,
    private _router: Router
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap
      .subscribe({
        next: value => {
          this.product.pid = value.get('pid');
          console.log("1st: " + this.product.pid)
          this.getProduct();
          this.productsService
            .fetchRelatedProducts(this.product.pid)
            .subscribe({
              next: value => {
                console.log("2nd: " + this.product.pid);
                this.relatedProducts = value.products
              },
              error: err => console.error(err)
            })
        },
        error: err => console.error(err)
      });
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
        .subscribe({
          next: item => {
            console.log("cart ID: " + item.itemid)
            window.alert(this.product.name + ' has been added to your cart')
          },
          error: err => console.error(err)
        })
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

  relationButton() {
    this.pid = {
      to_pid: this.relationForm.value.to_pidInput
    }
    console.log('this.to_pidInput = ' + this.relationForm.get('to_pidInput'));
    // this.relationButtonClicked = true;
    this.productsService
      .postProductRelation(this.product.pid, this.pid)
      .subscribe({
        next: value => console.log('id: ' + value.id),
        error: err => console.error(err)
      })
  }

  testClick() {
    this._router.navigateByUrl('/details/a5979c00-f262-4d2f-a14d-60b6dbeb1310');
  }
}
