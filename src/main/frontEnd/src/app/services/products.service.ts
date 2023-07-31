import { Injectable } from '@angular/core';
import {HttpClient, HttpClientModule, HttpParams} from "@angular/common/http";

import { Observable, throwError } from "rxjs";
import { catchError, retry } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ProductsService {
  products: any;

  constructor(private http: HttpClient) { }

  public fetchProducts() {
    return this.http.get('http://localhost:8084/products');
  }

  public fetchProduct(pid: string) {
    return this.http.get('http://localhost:8084/product/'+pid);
  }

  public fetchFilteredProducts(filter: string) {
    filter = filter.trim();

    const options = filter ?
      { params: new HttpParams().set('name', filter) } : {};

    if (!filter) {
      return this.fetchProducts();
    }
    return this.http.get('http://localhost:8084/product/search/'+filter, options);
  }

  public fetchProductImage(pid: string) {
    return this.http.get('http://localhost:8084/image/'+pid);
  }

}
