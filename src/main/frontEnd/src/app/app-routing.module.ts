import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {HomeComponent} from "./home/home.component";
import {ProductDetailsComponent} from "./product-details/product-details.component";
import {PageNotFoundComponent} from "./error/page-not-found/page-not-found.component";
import {ProfileComponent} from "./profile/profile.component";
import {CartComponent} from "./cart/cart.component";
import {LoginComponent} from "./login/login.component";
import {RegisterComponent} from "./register/register.component";
import {authGuardGuard} from "./auth-guard.guard";
import {UsersService} from "./services/users.service";

const routes: Routes = [
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {path: 'home', component: HomeComponent},
  {path: 'profile', component: ProfileComponent}, // canActivate: [authGuardGuard]
  {path: 'cart', component: CartComponent, canActivate: [authGuardGuard]},
  {path: 'details/:pid', component: ProductDetailsComponent},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: '**', component: PageNotFoundComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [UsersService]
})
export class AppRoutingModule {
}
