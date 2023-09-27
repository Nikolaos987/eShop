import {Injectable} from '@angular/core';
import {Page} from "../interfaces/page";

@Injectable({
  providedIn: 'root'
})
export class PagingService {
  // public page: Page | undefined = {range: 5};
  public range: number | undefined = 5;
  public page: number | undefined = 1;
  public pageGroup: number = 0;

  constructor() {
  }

  public newRange(r: number) {
    this.range = r;
  }
  public newPage(p: number) {
    this.page = p;
  }
  public newPageGroup(pg: number) {
    this.pageGroup = pg;
  }

}
