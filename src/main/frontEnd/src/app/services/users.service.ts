import {Injectable} from '@angular/core';
import {HttpClient, HttpClientModule, HttpErrorResponse, HttpParams} from "@angular/common/http";

import {map, Observable, of, tap, throwError} from "rxjs";
import {catchError, retry} from "rxjs";

import {User} from "../user";

@Injectable({
  providedIn: 'root'
})
export class UsersService {
  public user: User | undefined = {uid: '92905352-ed39-4c4d-9e97-4c81ed03cab1', username: 'One', isLoggedIn: true}; // TODO: clear the fields

  // public errorMessage: string = 'error';

  constructor(private http: HttpClient) {
  }

  public fetchUser(data: any): Observable<any> {
    return this.http
      .post <{ uid: string, username: string, password: string }>('/api/user/login', data, {responseType: "json"}) // TODO: don't return the "password: string from server"
      .pipe(
        map(response => {
          // this.errorMessage = 'no error'
          this.user = {
            uid: response.uid,
            username: response.username,
            isLoggedIn: true
          }
        }),
        catchError(this.handleError)
      );
  }

  // public fetchUser(username: string, password: string) {
  //   const params = new HttpParams()
  //     .set('username', username)
  //     .set('password', password);
  //   return this.http.post('http://localhost:8084/user/login', {params});
  // }

  public postUser(data: any): Observable<string> {
    return this.http
      .post('/api/user/register',
        {
          "username": data.username,
          "password": data.password
        }, {responseType: "text"})
      .pipe(
        catchError(this.handleError)
      );
  }

  public putUser(data: any): Observable<string> {
    return this.http
      .put('/api/user/' + this.user?.uid + '/password',
        {
          "currentPassword": data.currentPassword,
          "newPassword": data.password
        }, {responseType: "text"})
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
