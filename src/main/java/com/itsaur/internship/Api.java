package com.itsaur.internship;

import cartEntity.CartService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import productEntity.Category;
import productEntity.ProductService;
import query.cart.CartQueryModelStore;
import query.product.ProductQueryModelStore;
import query.user.UserQueryModelStore;
import userEntity.UserService;

import java.util.UUID;

public class Api extends AbstractVerticle {

    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;
    private final CartQueryModelStore cartQueryModelStore;
    private final ProductQueryModelStore productQueryModelStore;
    private final UserQueryModelStore userQueryModelStore;

    public Api(UserService userService, ProductService productService, CartService cartService, CartQueryModelStore cartQueryModelStore, ProductQueryModelStore productQueryModelStore, UserQueryModelStore userQueryModelStore) {
        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService;
        this.cartQueryModelStore = cartQueryModelStore;
        this.productQueryModelStore = productQueryModelStore;
        this.userQueryModelStore = userQueryModelStore;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        /* USER ENTITY */

        router.post("/user/login").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            this.userService.login(body.getString("username"), body.getString("password"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user logged in"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        router.post("/user/register").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            this.userService.register(body.getString("username"), body.getString("password"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user registered"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        router.delete("/user/:uid").handler(ctx -> this.userService.delete(UUID.fromString(ctx.pathParam("uid")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user deleted"))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.put("/user/:uid/password").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            this.userService.update(UUID.fromString(ctx.pathParam("uid")), body.getString("currentPassword"), body.getString("newPassword"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user updated"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        router.get("/user/:uid/cart").handler(ctx -> {
            cartQueryModelStore.findByUserId(UUID.fromString(ctx.pathParam("uid")))
                    .onSuccess(cart -> {
                        ctx.response().end(Json.encode(cart));
                    });
        });




        /* PRODUCT ENTITY */

        router.get("/product/:pid/image").handler(ctx -> this.productService.findProductImage(UUID.fromString(ctx.pathParam("pid")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(v))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.get("/product/find/:pid").handler(ctx -> this.productQueryModelStore.findProductById(UUID.fromString(ctx.pathParam("pid")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(v)))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.get("/product/search/:regex").handler(ctx -> this.productQueryModelStore.findProductsByName(ctx.pathParam("regex"))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(v)))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        // TODO: 7/7/23 findProducts
        router.get("/product/find").handler(ctx -> this.productQueryModelStore.findProducts()
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(v)))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.post("/product/insert/:name/:imagePath/:description/:price/:quantity/:brand/:category").handler(ctx -> {
            try {
                this.productService.addProduct(ctx.pathParam("name"), ctx.pathParam("imagePath"), ctx.pathParam("description"), Double.parseDouble(ctx.pathParam("price")), Integer.parseInt(ctx.pathParam("quantity")), ctx.pathParam("brand"), Category.valueOf(ctx.pathParam("category")))
                        .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("product created successfully"))
                        .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
            } catch (IllegalArgumentException e) {
                ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end("category should be either cellphone or smartphone!");
            }
        });

        router.delete("/product/delete/:pid").handler(ctx ->
                this.productService.deleteProduct(UUID.fromString(ctx.pathParam("pid")))
                        .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("product deleted successfully"))
                        .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.put("/product/update/:pid/:price").handler(ctx ->
                this.productService.updateProduct(UUID.fromString(ctx.pathParam("pid")), Double.parseDouble(ctx.pathParam("price")))
                        .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("product updated successfully"))
                        .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));




        /* CART ENTITY */

        router.get("/user/:uid/cart").handler(ctx -> this.cartQueryModelStore.findByUserId(UUID.fromString(ctx.pathParam("uid")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(v)))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.put("/user/:uid/product/:pid/:quantity").handler(ctx -> this.cartService.addItem(UUID.fromString(ctx.pathParam("uid")), UUID.fromString(ctx.pathParam("pid")), Integer.parseInt(ctx.pathParam("quantity")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("item updated!"))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.delete("/user/:uid/cart").handler(ctx -> this.cartService.buyCart(UUID.fromString(ctx.pathParam("uid")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("all products from carts were bought"))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        server.requestHandler(router).listen(8084);
    }
}
