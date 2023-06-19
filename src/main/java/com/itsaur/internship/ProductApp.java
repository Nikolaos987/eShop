package com.itsaur.internship;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class ProductApp extends AbstractVerticle {
    private final ProductService productService;

    public ProductApp(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.get("/product/:name").handler(ctx -> {
            this.productService.search(ctx.pathParam("name"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("product found!"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        router.get("/product/filter/:price/:category").handler(ctx -> {
            this.productService.filterProducts(Double.parseDouble(ctx.pathParam("price")), ctx.pathParam("category"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("product found!"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        router.post("/product/cart").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            this.productService.cart(body.getString("name"), Integer.parseInt(body.getString("quantity")))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("product added to cart!"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        server.requestHandler(router).listen(8084);
    }
}
