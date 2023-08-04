import {Injectable} from '@angular/core';
import {HttpClient, HttpClientModule, HttpParams} from "@angular/common/http";

import {map, Observable, tap, throwError} from "rxjs";
import {catchError, retry} from "rxjs";
import {UsersService} from "./users.service";

@Injectable({
  providedIn: 'root'
})
export class CartService {

  constructor(private http: HttpClient, private _usersService: UsersService) {
  }

  getCart(uid: string | undefined) {
    return this.http
      .get('/api/user/' + uid + '/cart') // TODO .get<[{item: Item}]>(url...)
  }

  addToCart(uid: string | undefined, pid: string, quantity: any) {
    return this.http.put('/api/user/' + uid + '/product/' + pid + '/' + quantity, {}, {responseType: "text"})
  }

  updateCart(itemid: string, quantity: number) {

  }

  removeItem(itemid: string) {

  }

  clearCart() {

  }
}
