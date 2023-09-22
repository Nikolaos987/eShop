import {Injectable} from '@angular/core';
import {HttpClient, HttpClientModule, HttpErrorResponse, HttpParams} from "@angular/common/http";

import {map, Observable, of, throwError} from "rxjs";
import {catchError, retry} from "rxjs";
import {Product} from "../interfaces/product";
import {getXHRResponse} from "rxjs/internal/ajax/getXHRResponse";
import {AbstractControl, FormControl, FormGroup, ɵFormGroupValue, ɵTypedOrUntyped} from "@angular/forms";
import {Paging} from "../interfaces/paging";
import {Category} from "../interfaces/category";
import {Item} from "../interfaces/item";

@Injectable({
  providedIn: 'root'
})
export class ProductsService {

  constructor(private http: HttpClient) {
  }

  public fetchTotalProducts(): Observable<{ totalProducts: number }> {
    return this.http
      .get<{ totalProducts: number }>("/api/product/count", {responseType: "json"})
      .pipe(
        catchError(this.handleError))
  }

  public fetchProducts(page: number | null | undefined,
                       range: number | null | undefined): Observable<{ products: Product[], totalCount: number }> {
    // ((page - 1) * range)
    if (typeof page === "number" && typeof range === "number") {
      return this.http
        .get<{ products: Product[], totalCount: number }>('/api/products/', {
          params: {
            from: ((page - 1) * range),
            range: range
          }, responseType: "json"
        })
        .pipe(
          catchError(this.handleError))
    } else return new Observable<{ products: Product[], totalCount: number }>();
  }

  public fetchProduct(pid: string | null | undefined): Observable<Product> {
    return this.http
      .get<Product>
      ('/api/product/' + pid, {responseType: "json"})
      .pipe(
        catchError(this.handleError));
  }

  public postProduct(product: Product): Observable<Product> {
    return this.http
      .post<{
        pid: string,
        name: string,
        description: string,
        price: number,
        quantity: number,
        brand: string,
        category: string
      }>(
        '/api/product/insert',
        {
          "name": product.name,
          // "imagepath": product.image,
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

  public uploadImage(pid: string | null | undefined,
                     formData: FormData): Observable<{ pid: string }> {
    return this.http
      .post<{ pid: string }>
      ('/api/product/' + pid + '/image', formData,
        {responseType: "json"}) //TODO: return a json from server
      .pipe(
        catchError(this.handleError))
  }

  public fetchFilteredProductsByCategories(filter: string,
                                           categories: Array<string>,
                                           page: number | null | undefined,
                                           range: number | null | undefined): Observable<{ products: Product[], totalCount: number }> {
    let categoriesString: string = categories.join(",");
    filter = filter.trim().toLowerCase();
    const options = filter ?
      {params: new HttpParams().set('name', filter)} : {};
    if (typeof page === "number" && typeof range === "number") {
      return this.http
        .get<{ products: Product[], totalCount: number }>('/api/products/filtered/categories',
          {
            params: {
              regex: filter,
              category: categoriesString,
              from: ((page - 1) * range),
              range: range
            }, responseType: "json"
          })
        .pipe(
          catchError(this.handleError))
    } else return new Observable<{ products: Product[], totalCount: number }>();
  }

  public fetchFilteredProducts(filter: string,
                               page: number | null | undefined,
                               range: number | null | undefined): Observable<{ products: Product[], totalCount: number }> {
    filter = filter.trim().toLowerCase();
    const options = filter ?
      {params: new HttpParams().set('name', filter)} : {};
    // if (!filter) {
    //   return this.fetchProducts(page, range);
    // }
    if (page && range) {
      return this.http
        .get<{ products: Product[], totalCount: number }>('/api/products/search', {
          params: {
            regex: filter,
            from: ((page - 1) * range),
            range: range
          }, responseType: "json"
        }) // , options
        .pipe(
          catchError(this.handleError));
    } else return new Observable<{ products: Product[], totalCount: number }>();
    //                  <[Product]>
  }

  public fetchTotalSearchedProducts(filter: string): Observable<{ totalProducts: number }> {
    filter = filter.trim().toLowerCase();
    const options = filter ?
      {params: new HttpParams().set('name', filter)} : {};
    return this.http
      .get<{ totalProducts: number }>("/api/product/search/count", {
        params: {
          regex: filter
        }, responseType: "json"
      })
      .pipe(
        catchError(this.handleError))
  }

  public fetchAllCategories(): Observable<Category[]> {
    return this.http
      .get<Category[]>("/api/product/categories/names",
        {responseType: "json"})
      .pipe(
        catchError(this.handleError))
  }

  public fetchTotalProductsByCategory(category: string): Observable<{ totalProducts: number }> {
    return this.http
      .get<{ totalProducts: number }>('/api/products/' + category + '/count',
        {responseType: "json"})
      .pipe(
        catchError(this.handleError))
  }

  public fetchTotalFilteredProductsByCategories(categories: Array<string>, filter: string): Observable<{ totalProducts: number }> {
    let categoriesString: string = categories.toString();
    filter = filter.trim().toLowerCase();
    const options = filter ?
      {params: new HttpParams().set('name', filter)} : {};
    return this.http
      .get<{ totalProducts: number }>('/api/products/filtered/categories/count',
        {
          params: {
            regex: filter,
            category: categoriesString
          }, responseType: "json"
        })
  }

  public fetchTotalProductsByCategories(categories: Array<string>): Observable<{ totalProducts: number }> {
    let categoriesString: string = categories.toString();
    console.log("categories: " + categories)
    return this.http
      .get<{ totalProducts: number }>('/api/products/categories/counts',
        {
          params: {
            category: categoriesString
          }, responseType: "json"
        })
      .pipe(
        catchError(this.handleError))
  }

  public fetchProductsByCategory(category: string,
                                 page: number | null | undefined,
                                 range: number | null | undefined): Observable<Product[]> {
    if (typeof page === "number" && typeof range === "number") {
      return this.http
        .get<[Product]>('/api/products/' + category + '/', {
          params: {
            from: ((page - 1) * range),
            range: range
          }, responseType: "json"
        })
        .pipe(
          catchError(this.handleError))
    } else return new Observable<Product[]>();
  }

  public fetchProductsByCategories(categories: Array<string>,
                                   page: number | null | undefined,
                                   range: number | null | undefined): Observable<{ products: Product[], totalCount: number }> {
    console.log("categories: " + categories);
    let categoriesString: string = categories.toString();
    if (typeof page === "number" && typeof range === "number") {
      return this.http
        .get<{ products: Product[], totalCount: number }>('/api/products/categories',
          {
            params: {
              category: categoriesString,
              from: ((page - 1) * range),
              range: range
            }, responseType: "json"
          })
        .pipe(
          catchError(this.handleError))
    } else return new Observable<{ products: Product[], totalCount: number }>();
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
