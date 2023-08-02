import {Injectable} from '@angular/core';
import {HttpClient, HttpClientModule, HttpParams} from "@angular/common/http";

import {map, Observable, tap, throwError} from "rxjs";
import {catchError, retry} from "rxjs";

import {User} from "../user";

@Injectable({
  providedIn: 'root'
})
export class UsersService {
  public user: User = {uid: '', username: '', isLoggedIn: false};

  constructor(private http: HttpClient) {
  }

  public fetchUser(data: any): Observable<User> {
    return this.http
      .post <{ uid: string, username: string, password: string }>('/api/user/login', data)
      .pipe(
        map(response => {
          this.user = {
            uid: response.uid,
            username: response.username,
            isLoggedIn: true
          }
          return this.user;

          // this.uid = response.uid
          // this.username = response.username;
          // this.isLoggedIn = true;
        })
      );
  }

  public getUsername(): string {
    return this.user.username;
  }

  // public fetchUser(username: string, password: string) {
  //   const params = new HttpParams()
  //     .set('username', username)
  //     .set('password', password);
  //   return this.http.post('http://localhost:8084/user/login', {params});
  // }

  public postUser(data: any) {
    return this.http.post('/api/user/register',
      {
        "username": data.username,
        "password": data.password
      });
  }

  public putUser(data: any) {
    return this.http.put('/api/user/' + this.user.uid + '/password',
      {
        "currentPassword": data.currentPassword,
        "newPassword": data.password
      });
  }

}
