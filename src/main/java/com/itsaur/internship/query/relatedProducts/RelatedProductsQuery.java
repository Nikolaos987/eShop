package com.itsaur.internship.query.relatedProducts;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgPool;

import java.util.List;
import java.util.UUID;

public class RelatedProductsQuery implements RelatedProductsQueryModelStore {
    Vertx vertx = Vertx.vertx();
    private final PgPool pgPool;

    public RelatedProductsQuery(PgPool pgPool) {
        this.pgPool = pgPool;
    }


}
