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
  // @ViewChild('label') labelElement: ElementRef | undefined;
  @ViewChild('input_name') inputNameElement: ElementRef | undefined;
  @ViewChild('input_price') inputPriceElement: ElementRef | undefined;
  // @ViewChild('sub_foot') sub_footElement: ElementRef | undefined;

  isActiveName: boolean = false;
  isActivePrice: boolean = false;
  isInactiveName: boolean = false;
  isInactivePrice: boolean = false;
  isValidName: boolean = false;
  isValidPrice: boolean = false;
  invalidBorderName: boolean = false;
  invalidBorderPrice: boolean = false;
  validBorderName: boolean = false;
  validBorderPrice: boolean = false;

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
    this.inputNameElement?.nativeElement.addEventListener('click', this.activate.bind(this));
    this.inputNameElement?.nativeElement.addEventListener('focusout', this.deactivate.bind(this));
    this.inputNameElement?.nativeElement.addEventListener('input', this.handleInput.bind(this));

    this.inputPriceElement?.nativeElement.addEventListener('click', this.activate.bind(this));
    this.inputPriceElement?.nativeElement.addEventListener('focusout', this.deactivate.bind(this));
    this.inputPriceElement?.nativeElement.addEventListener('input', this.handleInput.bind(this));
  }

  // @HostListener('click')
  activate(event: ManagementComponent) {
    this.isInactiveName = false;
    this.isActiveName = true;
  }

  deactivate(event: any) {
    if (this.inputNameElement?.nativeElement.value === '') {
      this.validBorderName = false;
      this.invalidBorderName = true;
      this.isActiveName = false;
      this.isInactiveName = true;
    }
    this.isInactiveName = false;
  }

  handleInput(event: any) {
    if (this.inputNameElement?.nativeElement.value === '') {
      this.isActiveName = false;
    } else {

      if (!this.createForm.controls.name.valid) {
        this.validBorderName = false;
        this.invalidBorderName = true;
      } else {
        this.invalidBorderName = false;
        this.validBorderName = true;
      }
      // this.isActive = true;
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
      this.errorMessage = "Fill The Form!";
    }
  }

  // uploadImage() {
  //   this._productsService.uploadImage('a26b9622-32de-4b52-b35e-58239f7fe2c8', this.formData)
  //     .subscribe();
  // }
}
