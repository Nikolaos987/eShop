package com.itsaur.internship;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class CustomerApp extends AbstractVerticle {

    private final UserService userService;

    public CustomerApp(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.post("/login").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            this.userService.login(body.getString("username"), body.getString("password"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user logged in"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        router.get("/logout").handler(ctx -> this.userService.logout()
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user logged out"))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.post("/register").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            this.userService.register(body.getString("username"), body.getString("password"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user registered"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        router.delete("/customer/:username").handler(ctx -> this.userService.delete(ctx.pathParam("username"))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user deleted"))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.put("/customer/:username/password").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            this.userService.update(ctx.pathParam("username"), body.getString("currentPassword"), body.getString("newPassword"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user updated"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        /* for product interaction */

        router.get("/product/:name").handler(ctx -> this.userService.search(ctx.pathParam("name"))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(String.valueOf(v)))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.get("/product/filter/:price/:category").handler(ctx -> this.userService.filterProducts(Double.parseDouble(ctx.pathParam("price")), ctx.pathParam("category"))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("product found!"))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.put("/product/:name/add/:quantity").handler(ctx -> this.userService.addCart(ctx.pathParam("name"), Integer.parseInt(ctx.pathParam("quantity")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("product added to cart!"))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.delete("/cart/buy").handler(ctx -> this.userService.buyCart()
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("all products from carts were bought"))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.get("/cart/show").handler(ctx -> this.userService.showCart()
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(v.toBuffer()))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.delete("/cart/remove/:name/:quantity").handler(ctx -> this.userService.removeCart(ctx.pathParam("name"), Integer.parseInt(ctx.pathParam("quantity")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("product quantity removed from your cart"))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        server.requestHandler(router).listen(8084);
    }
}
