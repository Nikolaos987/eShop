import {Injectable} from '@angular/core';
import {HttpClient, HttpClientModule, HttpParams} from "@angular/common/http";

import {map, Observable, tap, throwError} from "rxjs";
import {catchError, retry} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class CartService {

  constructor(private http: HttpClient) {
  }

  getCart(uid: string) {
    return this.http.get('/api/user/' + uid + '/cart')
  }

  addToCart(uid: string, pid: string, quantity: any) {
    return this.http.put('/api/user/' + uid + '/product/' + pid + '/' + quantity, {})
      .subscribe();
  }

  updateCart(itemid: string, quantity: number) {

  }

  removeItem(itemid: string) {

  }

  clearCart() {

  }
}
