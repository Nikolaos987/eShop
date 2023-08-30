import {AfterViewInit, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {debounceTime, fromEvent, identity, map} from "rxjs";
import {considerSettingUpAutocompletion} from "@angular/cli/src/utilities/completion";

@Component({
  selector: 'app-quantity-number',
  templateUrl: './quantity-number.component.html',
  styleUrls: ['./quantity-number.component.css']
})
export class QuantityNumberComponent implements OnInit, AfterViewInit {

  @Input() currentQuantity: number | undefined;
  formQuantity = new FormGroup({
    quantity: new FormControl(1)
  })

  @Output() changedQuantity = new EventEmitter<number>();

  @ViewChild('downRef') downElementRef: ElementRef | undefined;
  @ViewChild('upRef') upElementRef: ElementRef | undefined;

  ngOnInit(): void {
    if (this.currentQuantity != undefined && this.currentQuantity >= 0) {
      this.formQuantity.controls['quantity'].setValue(this.currentQuantity)
    }
  }

  ngAfterViewInit() {
    // console.log(this.nameElementRef);

    // + button
    const clicks = fromEvent(this.downElementRef?.nativeElement, 'click');
    const result = clicks.pipe(
      map(x => {
        if (this.formQuantity.controls['quantity'].value != null && this.formQuantity.controls['quantity'].value > 1) {
          this.formQuantity.controls['quantity'].setValue(this.formQuantity.controls['quantity'].value - 1)
        }
      }),
      debounceTime(1000));
    result.subscribe({
      next: result => {
        if (this.formQuantity.controls['quantity'].value != null && this.formQuantity.controls['quantity'].value >= 1) {
          this.changedQuantity.emit(this.formQuantity.controls['quantity'].value)
        }
      }
    })
    // - button
    const clicks2 = fromEvent(this.upElementRef?.nativeElement, 'click');
    const result2 = clicks2.pipe(
      map(x => {
        if (this.formQuantity.controls['quantity'].value != null && this.formQuantity.controls['quantity'].value < 9) {
          this.formQuantity.controls['quantity'].setValue(this.formQuantity.controls['quantity'].value + 1)
        }
      }),
      debounceTime(1000));
    result2.subscribe({
      next: result => {
        if (this.formQuantity.controls['quantity'].value != null && this.formQuantity.controls['quantity'].value <= 9) {
          this.changedQuantity.emit(this.formQuantity.controls['quantity'].value)
        }
      }
    })
  }

  // stepDown() {
  //   if (this.formQuantity.controls['quantity'].value != null && this.formQuantity.controls['quantity'].value > 1) {
  //     this.formQuantity.controls['quantity'].setValue(this.formQuantity.controls['quantity'].value - 1);
  //     this.decreaseQuantity.emit(this.formQuantity.controls['quantity'].value)
  //   }
  // }
  //
  // stepUp() {
  //   if (this.formQuantity.controls['quantity'].value != null && this.formQuantity.controls['quantity'].value < 9) {
  //     this.formQuantity.controls['quantity'].setValue(this.formQuantity.controls['quantity'].value + 1);
  //     this.increaseQuantity.emit(this.formQuantity.controls['quantity'].value)
  //   }
  // }

}
