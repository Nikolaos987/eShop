import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-quantity-number',
  templateUrl: './quantity-number.component.html',
  styleUrls: ['./quantity-number.component.css']
})
export class QuantityNumberComponent implements OnInit {

  @Input() currentQuantity: number | undefined;
  formQuantity = new FormGroup({
    quantity: new FormControl(1)
  })

  @Output() decreaseQuantity = new EventEmitter<number>();
  @Output() increaseQuantity = new EventEmitter<number>();

  ngOnInit(): void {
    // TODO: can be deleted, it is never used
    if (this.currentQuantity != undefined && this.currentQuantity >= 0) {
      this.formQuantity.controls['quantity'].setValue(this.currentQuantity)
    }
  }

  stepDown() {
    if (this.formQuantity.controls['quantity'].value != null && this.formQuantity.controls['quantity'].value > 1) {
      this.formQuantity.controls['quantity'].setValue(this.formQuantity.controls['quantity'].value - 1);
      this.decreaseQuantity.emit(this.formQuantity.controls['quantity'].value)
    }
  }

  stepUp() {
    if (this.formQuantity.controls['quantity'].value != null && this.formQuantity.controls['quantity'].value < 9) {
      this.formQuantity.controls['quantity'].setValue(this.formQuantity.controls['quantity'].value + 1);
      this.increaseQuantity.emit(this.formQuantity.controls['quantity'].value)
    }
  }

}
