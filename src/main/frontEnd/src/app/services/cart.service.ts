import {Injectable} from '@angular/core';
import {HttpClient, HttpClientModule, HttpErrorResponse, HttpParams} from "@angular/common/http";

import {debounce, debounceTime, map, Observable, tap, throwError, timer} from "rxjs";
import {catchError, retry} from "rxjs";
import {UsersService} from "./users.service";
import {Item} from "../interfaces/item";

@Injectable({
  providedIn: 'root'
})
export class CartService {
  public item: Item | undefined = {pid: '', name: '', quantity: 0, price: 0};

  constructor(private http: HttpClient, private _usersService: UsersService) {
  }

  getCart(uid: string | null): Observable<Item[]> {
    return this.http
      .get<Item[]>('/api/user/' + uid + '/cart', {responseType: "json"})
      .pipe(
        catchError(this.handleError));
  }

  addToCart(uid: string | null | undefined,
            pid: string | null | undefined,
            quantity: number | null | undefined): Observable<{ itemid: string }> {
    return this.http.put<{ itemid: string }>('/api/user/' + uid + '/product/' + pid + '/' + quantity,
      {},
      {responseType: "json"})
      .pipe(
        catchError(this.handleError));
  }

  updateCart(itemid: string, quantity: number) {
    // is done by addToCart
  }

  removeItem(itemid: string) {
    // is done by addToCart
  }

  clearCart() {

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
