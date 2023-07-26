import { Injectable } from '@angular/core';
import {HttpClient, HttpClientModule, HttpParams} from "@angular/common/http";

import { Observable, throwError } from "rxjs";
import { catchError, retry } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class CartService {

  constructor(private http:HttpClient) { }

  getCart(uid:string) {
    return this.http.get('http://localhost:8084/user/'+uid+'/cart');
  }

  addToCart(uid:string) {
    // this.http.put('http://localhost:8084/user/'+uid+'/cart')
  }

  updateCart(itemid:string, quantity:number) {

  }

  removeItem(itemid:string) {

  }

  clearCart() {

  }
}
