import { Component } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ProductsService} from "../services/products.service";
import {Product} from "../interfaces/product";
import {resolve} from "@angular/compiler-cli";

@Component({
  selector: 'app-management',
  templateUrl: './management.component.html',
  styleUrls: ['./management.component.css'],
  providers: [ProductsService]
})
export class ManagementComponent {
  product: Product = {
    pid: undefined,
    name: undefined,
    image: undefined,
    description: undefined,
    price: undefined,
    quantity: undefined,
    brand: undefined,
    category: undefined
  };

  createForm = new FormGroup({
    name: new FormControl('',
      [Validators.required, Validators.minLength(3)]),
    imagepath: new FormControl('',
      [Validators.required, Validators.minLength(3)]),
    description: new FormControl('',
      [Validators.required, Validators.minLength(10)]),
    price: new FormControl<number|null>(null,
      [Validators.required, Validators.pattern("^[0-9]*$"), Validators.min(1.0)]),
    quantity: new FormControl<number|null>(null,
      [Validators.required, Validators.pattern("^[0-9]*$"), Validators.min(1.0)]),
    brand: new FormControl('',
      [Validators.required, Validators.minLength(3)]),
    category: new FormControl('',
      [Validators.required, Validators.minLength(3)]),
  });

  constructor(private _productsService: ProductsService) {
  }

  apply () {
    if (this.createForm.valid) {
      this.product = {
        pid: '',
        name: this.createForm.value.name,
        image: this.createForm.value.imagepath,
        description: this.createForm.value.description,
        price: this.createForm.value.price,
        quantity: this.createForm.value.quantity,
        brand: this.createForm.value.brand,
        category: this.createForm.value.category
      }
      this._productsService.postProduct(this.product).subscribe(result => console.log(result));
    }
  }
}
