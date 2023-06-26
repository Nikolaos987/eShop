package com.itsaur.internship;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.UUID;

public class CustomerApp extends AbstractVerticle {

    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;

    public CustomerApp(UserService userService, ProductService productService, CartService cartService) {
        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        /* USER ENTITY */

        router.post("/customer/login").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            this.userService.login(body.getString("username"), body.getString("password"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user logged in"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        router.post("/customer/register").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            this.userService.register(body.getString("username"), body.getString("password"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user registered"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        router.delete("/customer/:uid").handler(ctx -> this.userService.delete(ctx.pathParam("uid"))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user deleted"))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.put("/customer/:uid/password").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            this.userService.update(ctx.pathParam("uid"), body.getString("currentPassword"), body.getString("newPassword"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user updated"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });




        /* PRODUCT ENTITY */

        router.get("/product/find/:pid").handler(ctx -> this.productService.product(UUID.fromString(ctx.pathParam("pid")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(v.toBuffer()))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.get("/product/search/:regex").handler(ctx -> this.productService.searchByName(ctx.pathParam("regex"))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(v.toBuffer()))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.get("/product/filter/").handler(ctx -> this.productService.filterProducts(
                        Double.parseDouble(ctx.request().getParam("price")),
                        ctx.request().getParam("brand"),
                        ctx.request().getParam("category"))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(v.toBuffer()))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));




        /* CART ENTITY */

        router.get("/cart/:cid").handler(ctx -> this.cartService.showCart(UUID.fromString(ctx.pathParam("cid")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(v.toBuffer()))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.put("/cart/:cid/product/:pid/:quantity").handler(ctx -> this.cartService.addCart(UUID.fromString(ctx.pathParam("cid")), UUID.fromString(ctx.pathParam("pid")), Integer.parseInt(ctx.pathParam("quantity")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("product added to cart!"))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.delete("/cart/:cid").handler(ctx -> this.cartService.buyCart(UUID.fromString("cid"))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("all products from carts were bought"))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.delete("/cart/:cid/item/:pid/:quantity").handler(ctx -> this.cartService.removeCart(UUID.fromString(ctx.pathParam("cid")), UUID.fromString(ctx.pathParam("pid")), Integer.parseInt(ctx.pathParam("quantity")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("product quantity removed from your cart"))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        server.requestHandler(router).listen(8084);
    }
}
