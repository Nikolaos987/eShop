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
  @ViewChild('inputArea') inputElement: ElementRef | undefined;

  isActive: boolean = false;
  isInactive: boolean = false;
  isValid: boolean = false;
  invalidBorder: boolean = false;
  validBorder: boolean = false;

  errorMessage: string = '';
  successMessage: string = '';
  changePswdBtnClicked: boolean = false;

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
    this.inputElement?.nativeElement.addEventListener('focus', this.activate.bind(this));
    this.inputElement?.nativeElement.addEventListener('focusout', this.deactivate.bind(this));
    this.inputElement?.nativeElement.addEventListener('input', this.handleInput.bind(this));
  }

  // @HostListener('click')
  activate(event: ManagementComponent) {
    this.isInactive = false;
    this.isActive = true;
  }

  deactivate(event: any) {
    if (this.inputElement?.nativeElement.value === '') {
      this.validBorder = false;
      this.invalidBorder = true;
      this.isActive = false;
      this.isInactive = true;
    }
    this.isInactive = false;
  }

  handleInput(event: any) {
    if (this.inputElement?.nativeElement.value === '') {
      this.isActive = false;
    } else {

      if (!this.createForm.controls.description.valid) {
        this.validBorder = false;
        this.invalidBorder = true;
      } else {
        this.invalidBorder = false;
        this.validBorder = true;
      }
      this.isActive = true;
    }
  }

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
    this.changePswdBtnClicked = true;
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
        .subscribe({
          next: product => {
            this.errorMessage = "";
            this.successMessage = "Product Created!";
            console.log(product.pid);
            if (this.formData)
              this._productsService.uploadImage(product.pid, this.formData)
                .subscribe();
          },
          error: err => {
            this.successMessage = "";
            this.errorMessage = "err";
          }
        });
    } else {
      this.successMessage = "";
      this.errorMessage = "Please fill in the form first";
    }
  }

  // uploadImage() {
  //   this._productsService.uploadImage('a26b9622-32de-4b52-b35e-58239f7fe2c8', this.formData)
  //     .subscribe();
  // }
}
