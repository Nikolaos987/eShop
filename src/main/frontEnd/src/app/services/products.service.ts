import { Injectable } from '@angular/core';
import { HttpClient, HttpClientModule} from "@angular/common/http";

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
    if (!filter) {
      return this.fetchProducts();
    }
    return this.http.get('http://localhost:8084/product/search/'+filter);
  }

}
