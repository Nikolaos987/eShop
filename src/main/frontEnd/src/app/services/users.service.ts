import {Injectable} from '@angular/core';
import {HttpClient, HttpClientModule, HttpErrorResponse, HttpParams} from "@angular/common/http";

import {map, Observable, of, tap, throwError} from "rxjs";
import {catchError, retry} from "rxjs";

import {User} from "../interfaces/user";
import {Profile} from "../interfaces/profile";
import {Credentials} from "../interfaces/credentials";

@Injectable({
  providedIn: 'root'
})
export class UsersService {
  public user: User | undefined = {uid: '', username: '', isLoggedIn: false}; // TODO: clear these fields

  // public errorMessage: string = 'error';

  constructor(private http: HttpClient) {
  }

  public fetchUser(data: Credentials): Observable<{ uid: string, username: string, password: string }> {
    return this.http
      .post <{ uid: string, username: string, password: string }>
      ('/api/user/login', {"username": data.username, "password": data.password}, {responseType: "json"}) // TODO: don't return the "password: string from server"
      .pipe(
        catchError(this.handleError));
  }

  // public fetchUser(username: string, password: string) {
  //   const params = new HttpParams()
  //     .set('username', username)
  //     .set('password', password);
  //   return this.http.post('http://localhost:8084/user/login', {params});
  // }

  public postUser(data: Credentials): Observable<{ uid: string }> {
    return this.http
      .post<{ uid: string }>('/api/user/register',
        {
          "username": data.username,
          "password": data.password
        }, {responseType: "json"})
      .pipe(
        catchError(this.handleError));
  }

  public putUser(data: Profile): Observable<{ uid: string }> {
    return this.http
      .put<{ uid: string }>('/api/user/' + this.user?.uid + '/password',
        {
          "currentPassword": data.currentPassword,
          "newPassword": data.password
        }, {responseType: "json"})
      .pipe(
        // map((response) => {
        //   return response.message;
        // }),
        catchError(this.handleError));
  }

  public deleteUser(): Observable<void> {
    return this.http
      .delete('/api/user/' + this.user?.uid, {responseType: "text"})
      .pipe(
        map(() => {
          this.user = undefined;
        }),
        catchError(this.handleError));
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
