import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";
import {ReactiveFormsModule} from "@angular/forms";

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ProductDetailsComponent} from './product-details/product-details.component';
import {HomeComponent} from './home/home.component';
import {ProfileComponent} from './profile/profile.component';
import {CartComponent} from './cart/cart.component';
import {ProductListComponent} from './product-list/product-list.component';
import {SearchBarComponent} from './search-bar/search-bar.component';
import {BackNavComponent} from './back-nav/back-nav.component';
import {LoginComponent} from './login/login.component';
import {RegisterComponent} from './register/register.component';
import {PageNotFoundComponent} from './error/page-not-found/page-not-found.component';
import {ProductNotFoundComponent} from './error/product-not-found/product-not-found.component';
import {ProductComponent} from './product/product.component';
import {UsersService} from "./services/users.service";
import {QuantityNumberComponent} from './quantity-number/quantity-number.component';
import {ManagementComponent} from './management/management.component';
// import {PaginatorComponent} from './paginator/paginator.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'
import {MatPaginatorModule} from "@angular/material/paginator";
import { PagingComponent } from './paging/paging.component';
import { InputTextComponent } from './input-text/input-text.component';
import { SubFootComponent } from './sub-foot/sub-foot.component';

@NgModule({
  declarations: [
    AppComponent,
    ProductDetailsComponent,
    HomeComponent,
    ProfileComponent,
    CartComponent,
    ProductListComponent,
    SearchBarComponent,
    BackNavComponent,
    LoginComponent,
    RegisterComponent,
    PageNotFoundComponent,
    ProductNotFoundComponent,
    ProductComponent,
    QuantityNumberComponent,
    ManagementComponent,
    PagingComponent,
    InputTextComponent,
    SubFootComponent,
    // PaginatorComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatPaginatorModule
  ],
  providers: [{provide: UsersService, useClass: UsersService}],
  bootstrap: [AppComponent]
})
export class AppModule {
}
