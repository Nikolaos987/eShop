import { Injectable } from '@angular/core';
import {HttpClient, HttpClientModule, HttpParams} from "@angular/common/http";

import {map, Observable, tap, throwError} from "rxjs";
import { catchError, retry } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class UsersService {
  uid:string = '';

  constructor(private http: HttpClient) { }

  public fetchUser(data:any) {
    const params = new HttpParams()
      // .set('username',data.username)  //lathos: den mpainei os body parameters alla os uri parameters
      // .set('password',data.password);
    return this.http.post('http://localhost:8084/user/login', data);
      // .pipe(
      //   map(user => this.uid = user.uid)
      // );
  }

  // public fetchUser(username: string, password: string) {
  //   const params = new HttpParams()
  //     .set('username', username)
  //     .set('password', password);
  //   return this.http.post('http://localhost:8084/user/login', {params});
  // }

  public postUser(data:any) {
    return this.http.post('http://localhost:8084/user/register', data);
  }

  public putUser(data:any, uid:string) {
    return this.http.put('http://localhost:8084/user/'+uid+'/password', data);
  }

}
