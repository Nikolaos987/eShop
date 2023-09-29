import {Component, EventEmitter, Input, numberAttribute, OnInit, Output} from '@angular/core';
import {PagingService} from "../services/paging.service";

@Component({
  selector: 'app-paging',
  templateUrl: './paging.component.html',
  styleUrls: ['./paging.component.css']
})
export class PagingComponent implements OnInit {
  input: number = 1;
  // @Input() totalProducts: number | null | undefined;
  // @Input() totalPages: any | null | undefined;
  @Input() pages: number[] | null | undefined = [];
  @Input() range: number | null | undefined;
  @Input() page: number | null | undefined;

  @Input() groupIndex: number = 0;
  @Input() groupLength: number = 4;
  @Input() pagesGroup: Array<Array<number>> = [];

  // page: number = 1;
  @Output() rangeOutput = new EventEmitter<number>();
  @Output() pageOutput = new EventEmitter<number>();
  @Output() groupIndexOutput = new EventEmitter<number>();
  @Output() page_indexOutput = new EventEmitter<{ page: number, index: number }>();

  constructor(private _pagingService: PagingService) {
  }

  ngOnInit() {
    // this.rangeOutput.emit(5)
    this.range = this._pagingService.range;
    this.page = this._pagingService.page;
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
      if (this.page > this.pagesGroup[this.groupIndex][0]) {
        this.page = this.page - 1;
        this.pageOutput.emit(this.page)
      } else if (this.groupIndex > 0 && this.page == this.pagesGroup[this.groupIndex][0]) {
        this.groupIndex--;
        this.page_indexOutput.emit({page: this.page - 1, index: this.groupIndex});
      }
  }

  nextPage() {
    if (this.page && this.pages) {
      if (this.page < this.groupLength * this.groupIndex + this.pagesGroup[this.groupIndex].length) {
        this.page = this.page + 1;
        this.pageOutput.emit(this.page)
      } else if (this.groupIndex < this.pagesGroup.length - 1 && this.page == this.groupLength * this.groupIndex + this.pagesGroup[this.groupIndex].length) {
        this.groupIndex++;
        this.groupIndexOutput.emit(this.groupIndex);
      }
    }
  }

  prevGroup() {
    if (this.groupIndexOutput && this.page) {
      if (this.groupIndex > 0) {
        this.groupIndex--;
        this.groupIndexOutput.emit(this.groupIndex);
      }
    }
  }

  nextGroup() {
    if (this.groupIndexOutput) {
      if (this.groupIndex < this.pagesGroup.length - 1) {
        this.groupIndex++;
        this.groupIndexOutput.emit(this.groupIndex);
      }
    }
  }

  changePage() {
    if (this.pages?.length
      && this.pagesGroup
      && this.input <= (this.pagesGroup
        [this.pagesGroup.length - 1]
        [this.pagesGroup[this.pagesGroup.length - 1].length - 1])
      && this.input > 0) {
      // this.groupIndexOutput.emit(Math.ceil(this.input / this.groupLength) - 1);
      // this.page = this.input;
      this.page_indexOutput.emit({page: this.input, index: Math.ceil(this.input / this.groupLength) - 1})
      // this.groupIndexOutput.emit(1);
      // this.page_indexOutput.emit({page:1,index:2});
      // this.pageOutput.emit(this.page);
      // this.groupIndexOutput.emit(1);
    }
  }
}
