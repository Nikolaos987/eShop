import {Injectable} from '@angular/core';
import {Page} from "../interfaces/page";

@Injectable({
  providedIn: 'root'
})
export class PagingService {
  public page: Page | undefined = {range: 5};

  constructor() {
  }

  public newRange(r: number) {
    this.page = {range: r}
  }

}
