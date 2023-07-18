import {Component, Input, OnInit} from '@angular/core';
import { ProductsService } from "../services/products.service";
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.css'],
  providers: [ProductsService]
})
export class ProductDetailsComponent implements OnInit {
  @Input() product?: any;

  constructor(
    private productsService: ProductsService,
    private route: ActivatedRoute,
    private location: Location
  ) { }

  ngOnInit(): void {
    this.getProduct();
  }

  getProduct(): void {
    const pid = String(this.route.snapshot.paramMap.get('pid'));
    this.productsService.fetchProduct(pid)
      .subscribe(response => this.product = response)
  }

}
