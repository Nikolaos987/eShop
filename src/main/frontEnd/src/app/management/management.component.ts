import {AfterViewInit, Component, ElementRef, HostListener, ViewChild} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ProductsService} from "../services/products.service";
import {Product} from "../interfaces/product";
import {resolve} from "@angular/compiler-cli";
import {FileUpdate} from "@angular/compiler-cli/src/ngtsc/program_driver";

@Component({
  selector: 'app-management',
  templateUrl: './management.component.html',
  styleUrls: ['./management.component.css'],
  providers: [ProductsService]
})
export class ManagementComponent implements AfterViewInit {
  @ViewChild('label') labelElement: ElementRef | undefined;
  @ViewChild('input') inputElement: ElementRef | undefined;

  product: Product = {
    pid: undefined,
    name: undefined,
    // image: undefined,
    description: undefined,
    price: undefined,
    quantity: undefined,
    brand: undefined,
    category: undefined
  };

  uploadImageForm = new FormGroup({
    image: new FormControl('',
      [Validators.required, Validators.minLength(3)]),
  });

  createForm = new FormGroup({
    name: new FormControl('',
      [Validators.required, Validators.minLength(3)]),
    description: new FormControl('',
      [Validators.required, Validators.minLength(10)]),
    price: new FormControl<number | null>(null,
      [Validators.required, Validators.pattern("^[0-9]*$"), Validators.min(1.0)]),
    quantity: new FormControl<number | null>(null,
      [Validators.required, Validators.pattern("^[0-9]*$"), Validators.min(1.0)]),
    brand: new FormControl('',
      [Validators.required, Validators.minLength(3)]),
    category: new FormControl('',
      [Validators.required, Validators.minLength(3)]),
  });

  formData: FormData = new FormData();

  ngAfterViewInit() {

  }

  // @HostListener('click', event)
  // handleClick() {
  //   if ()
  // }

  constructor(private _productsService: ProductsService) {
  }

  getFile(event: any) {
    if (event.target.files.length > 0) {
      const file: File = event.target.files[0];
      this.formData.append('file', file);
      console.log("form data: " + this.formData);
    }
  }

  apply() {
    if (this.createForm.valid) {
      this.product = {
        pid: '',
        name: this.createForm.value.name,
        // image: this.createForm.value.imagepath,
        description: this.createForm.value.description,
        price: this.createForm.value.price,
        quantity: this.createForm.value.quantity,
        brand: this.createForm.value.brand,
        category: this.createForm.value.category
      }
      this._productsService.postProduct(this.product)
        .subscribe(product => {
          console.log(product.pid)
          this._productsService.uploadImage(product.pid, this.formData)
            .subscribe();
        });
    }
  }

  // uploadImage() {
  //   this._productsService.uploadImage('a26b9622-32de-4b52-b35e-58239f7fe2c8', this.formData)
  //     .subscribe();
  // }
}
