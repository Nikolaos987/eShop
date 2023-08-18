import {Injectable} from '@angular/core';
import {HttpClient, HttpClientModule, HttpParams} from "@angular/common/http";

import {Observable, throwError} from "rxjs";
import {catchError, retry} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ProductsService {
  products: any;

  constructor(private http: HttpClient) {
  }

  public fetchProducts(): Observable<any> {
    return this.http.get('/api/products');
  }

  public fetchProduct(pid: string) {
    return this.http.get('/api/product/' + pid);
  }

  public postProduct(data: any): Observable<any> {
    return this.http
      .post(
        '/api/product/insert/'+data.name+'/'+data.imagepath+'/'+data.description+'/'+data.price+'/'+data.quantity+'/'+data.brand+'/'+data.category,
        {},
        {responseType: "text"})
  }

  public fetchFilteredProducts(filter: string) {
    filter = filter.trim();

    const options = filter ?
      {params: new HttpParams().set('name', filter)} : {};

    if (!filter) {
      return this.fetchProducts();
    }
    return this.http.get('/api/product/search/' + filter, options);
  }

}
