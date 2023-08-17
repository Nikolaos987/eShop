import { Component } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ProductsService} from "../services/products.service";

@Component({
  selector: 'app-management',
  templateUrl: './management.component.html',
  styleUrls: ['./management.component.css'],
  providers: [ProductsService]
})
export class ManagementComponent {
  createForm = new FormGroup({
    name: new FormControl('',
      [Validators.required, Validators.minLength(3)]),
    imagepath: new FormControl('',
      [Validators.required, Validators.minLength(3)]),
    description: new FormControl('',
      [Validators.required, Validators.minLength(10)]),
    price: new FormControl('',
      [Validators.required, Validators.pattern("^[0-9]*$"), Validators.min(1.0)]),
    quantity: new FormControl('',
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
      this._productsService.postProduct(this.createForm.value).subscribe()
    }
  }
}
