import {Injectable} from '@angular/core';
import {HttpClient, HttpClientModule, HttpErrorResponse, HttpParams} from "@angular/common/http";

import {map, Observable, of, throwError} from "rxjs";
import {catchError, retry} from "rxjs";
import {Product} from "../interfaces/product";
import {getXHRResponse} from "rxjs/internal/ajax/getXHRResponse";
import {AbstractControl, FormControl, FormGroup, ɵFormGroupValue, ɵTypedOrUntyped} from "@angular/forms";
import {Paging} from "../interfaces/paging";

@Injectable({
  providedIn: 'root'
})
export class ProductsService {

  constructor(private http: HttpClient) {
  }

  public fetchTotalProducts(): Observable<{ rows: number }> {
    return this.http
      .get<{ rows: number }>("/api/product/count", {responseType: "json"})
      .pipe(
        catchError(this.handleError))
  }

  public fetchProducts( page: number | null | undefined, range: number | null | undefined ): Observable<Product[]> {
    // ((page - 1) * range)
    if (typeof page === "number" && typeof range === "number") {
      return this.http
        .get<[Product]>('/api/products/', {params: {
          from: ((page - 1) * range),
          range: range
          }, responseType: "json"})
        .pipe(
          catchError(this.handleError))
    } else return new Observable<Product[]>();
  }

  public fetchProduct(pid: string | null | undefined): Observable<Product> {
    return this.http.get<Product>('/api/product/' + pid, {responseType: "json"})
      .pipe(
        catchError(this.handleError));
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
      .pipe(
        catchError(this.handleError))
  }

  public fetchFilteredProducts(filter: string): Observable<Product[]> {
    filter = filter.trim().toLowerCase();
    const options = filter ?
      {params: new HttpParams().set('name', filter)} : {};
    if (!filter) {
      return this.fetchProducts(1, 5);
    }
    return this.http.get<Product[]>
    ('/api/product/search/' + filter, {responseType: "json"}) // , options
      .pipe(
        catchError(this.handleError));
    //                  <[Product]>
  }

  private handleError(error: HttpErrorResponse) {
    if (error.status === 0) {
      // a client side or network error occured. Handle it accordingly.
      console.error('An error occured: ', error.error);
    } else {
      // the backend returned an unsuccessful response code.
      // the response body may contain clues as to what went wrong.
      console.error(`Backend returned code ${error.status}, body was: `, error);
    }
    // return an observable with a user-facing error message.
    // this.errorMessage = error.error;
    return throwError(() => new Error(error.error));
  }


}
