import {Injectable} from '@angular/core';
import {HttpClient, HttpClientModule, HttpParams} from "@angular/common/http";

import {map, Observable, throwError} from "rxjs";
import {catchError, retry} from "rxjs";
import {Product} from "../interfaces/product";
import {getXHRResponse} from "rxjs/internal/ajax/getXHRResponse";
import {AbstractControl, FormControl, FormGroup, ɵFormGroupValue, ɵTypedOrUntyped} from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class ProductsService {
  products: Product[] = [];

  constructor(private http: HttpClient) {
  }

  public fetchProducts(): Observable<Product[]> {
    return this.http
      .get<[Product]>('/api/products', {responseType: "json"})
  }

  public fetchProduct(pid: string | null | undefined): Observable<Product> {
    return this.http.get<Product>('/api/product/' + pid);
  }

  public postProduct(product: Product): Observable<Product> {
    return this.http
      .post<{
        pid: string,
        name: string,
        image: string,
        description: string,
        price: number,
        quantity: number,
        brand: string,
        category: string
      }>(
        '/api/product/insert',
        {
          "name": product.name,
          "imagepath": product.image,
          "description": product.description,
          "price": Number(product.price),
          "quantity": Number(product.quantity),
          "brand": product.brand,
          "category": product.category
        },
        {responseType: "json"})
  }

  public fetchFilteredProducts(filter: string): Observable<Product[]> {
    filter = filter.trim().toLowerCase();
    const options = filter ?
      {params: new HttpParams().set('name', filter)} : {};
    if (!filter) {
      return this.fetchProducts();
    }
    return this.http.get<Product[]>('/api/product/search/' + filter, options);
    //                  <[Product]>
  }

  // TODO: handle errors

}
