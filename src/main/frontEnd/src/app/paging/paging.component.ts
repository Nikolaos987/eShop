import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {PagingService} from "../services/paging.service";

@Component({
  selector: 'app-paging',
  templateUrl: './paging.component.html',
  styleUrls: ['./paging.component.css']
})
export class PagingComponent implements OnInit {
  // @Input() totalProducts: number | null | undefined;
  // @Input() totalPages: any | null | undefined;
  @Input() pages: number[] | null | undefined = [];
  @Input() range: number | null | undefined;
  @Input() page: number | null | undefined;


  // page: number = 1;
  @Output() rangeOutput = new EventEmitter<number>();
  @Output() pageOutput = new EventEmitter<number>();

  constructor(private _pagingService: PagingService) {
  }

  ngOnInit() {
    // this.rangeOutput.emit(5)
    this.range = this._pagingService.page?.range;
  }

  getPage(page: number) {
    this.page = page;
    this.pageOutput.emit(page)
  }

  getRange(r: number) {
    this.rangeOutput.emit(r)
  }

  prevPage() {
    if (this.page)
      if (this.page > 1) {
        this.page = this.page - 1;
        this.pageOutput.emit(this.page)
      }
  }

  nextPage() {
    if (this.page && this.pages)
      if (this.page < this.pages?.length) {
        this.page = this.page + 1;
        this.pageOutput.emit(this.page)
      }
  }

}
