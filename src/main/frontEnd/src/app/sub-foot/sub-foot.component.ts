import {Component, Input} from '@angular/core';
import {FormControl} from "@angular/forms";

@Component({
  selector: 'app-sub-foot',
  templateUrl: './sub-foot.component.html',
  styleUrls: ['./sub-foot.component.css', '../management/management.component.css']
})
export class SubFootComponent {
  @Input() control: FormControl = new FormControl('');
  @Input() InvalidInputText: string = '';

}
