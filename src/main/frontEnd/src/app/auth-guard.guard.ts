import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {UsersService} from "./services/users.service";

export const authGuardGuard: CanActivateFn = (

  route, state) => {
  const usersService = inject(UsersService);
  const router = inject(Router);

  if (usersService.user.username != '') {
    return true;
  }
  return router.parseUrl('/login');

// export const authGuardGuard = () => {
//   const usersService = inject(UsersService);
//   const router = inject(Router);
//
//   if (usersService.user.username != '') {
//     return true;
//   }
//   return router.parseUrl('/home');

};
