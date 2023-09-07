import {AfterViewInit, Component, ElementRef, Input, ViewChild} from '@angular/core';
import {ManagementComponent} from "../management/management.component";
import {Form, FormControl, FormControlDirective, FormGroup, FormGroupDirective} from "@angular/forms";

@Component({
  selector: 'app-input-text',
  templateUrl: './input-text.component.html',
  styleUrls: ['./input-text.component.css', '../management/management.component.css']
})
export class InputTextComponent implements AfterViewInit {
  @ViewChild('input') inputElement: ElementRef | undefined;

  @Input() labelText: string = '';
  @Input() InvalidInputText: string = '';
  @Input() control: FormControl = new FormControl('');
  @Input() isSpecialField: boolean = false;

  isActive: boolean = false;
  isInactive: boolean = false;
  isValid: boolean = false;
  invalidBorder: boolean = false;
  validBorder: boolean = false;

  ngAfterViewInit() {
    this.inputElement?.nativeElement.addEventListener('focus', this.activate.bind(this));
    this.inputElement?.nativeElement.addEventListener('focusout', this.deactivate.bind(this));
    this.inputElement?.nativeElement.addEventListener('input', this.handleInput.bind(this));
  }

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

      if (this.control && !this.control.valid) {
        this.validBorder = false;
        this.invalidBorder = true;
      } else {
        this.invalidBorder = false;
        this.validBorder = true;
      }
      this.isActive = true;
    }
  }
}
