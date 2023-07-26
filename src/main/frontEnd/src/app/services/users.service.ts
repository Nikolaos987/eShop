import { Injectable } from '@angular/core';
import {HttpClient, HttpClientModule, HttpParams} from "@angular/common/http";

import { Observable, throwError } from "rxjs";
import { catchError, retry } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  constructor(private http: HttpClient) { }

  public fetchUser(username: string, password: string) {
    const params = new HttpParams()
      .set('username', username)
      .set('password', password);
    return this.http.post('http://localhost:8084/user/login', {params});
  }

  public postUser(data:any) {
    return this.http.post('http://localhost:8084/user/register', data);
  }

  public putUser(data:any, uid:string) {
    return this.http.put('http://localhost:8084/user/'+uid+'/password', data);
  }

}
