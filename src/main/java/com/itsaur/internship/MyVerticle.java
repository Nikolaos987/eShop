package com.itsaur.internship;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class MyVerticle extends AbstractVerticle {

    private final UserService service;

    public MyVerticle(UserService service) {
        this.service = service;
    }


    @Override
    public void start(Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.post("/login").handler(ctx -> {
            System.out.println();
            final JsonObject body = ctx.body().asJsonObject();
            this.service.login(body.getString("username"), body.getString("password"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user logged in"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });


        router.post("/register").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            this.service.register(body.getString("username"), body.getString("password"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user registered"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        router.route().handler(BodyHandler.create());
        router.delete("/users/:username").handler(ctx -> {
            System.out.println();
            this.service.delete(ctx.pathParam("username"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user deleted"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        router.route().handler(BodyHandler.create());
        router.put("/users/:username/password").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            this.service.update(ctx.pathParam("username"), body.getString("currentPassword"), body.getString("newPassword"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user updated"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        server.requestHandler(router).listen(8084);
    }
}
