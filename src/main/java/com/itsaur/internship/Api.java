package com.itsaur.internship;

import com.itsaur.internship.cartEntity.CartService;
import com.itsaur.internship.productEntity.PostgresProductsStore;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import com.itsaur.internship.productEntity.Category;
import com.itsaur.internship.productEntity.ProductService;
import com.itsaur.internship.query.cart.CartQueryModelStore;
import com.itsaur.internship.query.product.ProductQueryModelStore;
import com.itsaur.internship.query.user.UserQueryModelStore;
import com.itsaur.internship.userEntity.UserService;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class Api extends AbstractVerticle {

    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;
    private final CartQueryModelStore cartQueryModelStore;
    private final ProductQueryModelStore productQueryModelStore;
    private final UserQueryModelStore userQueryModelStore;

    public Api(UserService userService, ProductService productService, CartService cartService,
               CartQueryModelStore cartQueryModelStore, ProductQueryModelStore productQueryModelStore,
               UserQueryModelStore userQueryModelStore) {
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

//        router.route().handler(CorsHandler.create("http://localhost:8084"));

//        router.route().handler(io.vertx.ext.web.handler.CorsHandler.create()
//                .allowedMethod(io.vertx.core.http.HttpMethod.GET)
//                .allowedMethod(io.vertx.core.http.HttpMethod.POST)
//                .allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
//                .allowedHeader("Access-Control-Request-Method")
//                .allowedHeader("Access-Control-Allow-Credentials")
//                .allowedHeader("Access-Control-Allow-Origin")
//                .allowedHeader("Access-Control-Allow-Headers")
//                .allowedHeader("Content-Type"));

        /* USER ENTITY */

        router.post("/user/login").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            this.userService.login(body.getString("username"), body.getString("password"))
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(v)))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                            .end(v.getMessage()));
        });

        router.post("/user/register").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            this.userService.register(body.getString("username"), body.getString("password"))
                    .onSuccess(v -> {
                        JsonObject json = new JsonObject().put("uid", v);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(json));
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                            .end(v.getMessage()));
        });

        router.delete("/user/:uid").handler(ctx -> this.userService.delete(UUID.fromString(ctx.pathParam("uid")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("user deleted"))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.put("/user/:uid/password").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            this.userService.update(UUID.fromString(ctx.pathParam("uid")),
                            body.getString("currentPassword"),
                            body.getString("newPassword"))
                    .onSuccess(v -> {
                        JsonObject json = new JsonObject().put("uid", v);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(json));
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                            .end(v.getMessage()));
        });




        /* PRODUCT ENTITY */

        router.get("/product/count").handler(ctx -> this.productQueryModelStore.productsCount()
                .onSuccess(v -> {
                    JsonObject json = new JsonObject().put("rows", v);
                    ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(json));
                })
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.get("/product/image/:pid").handler(ctx -> this.productQueryModelStore
                .findImageById(UUID.fromString(ctx.pathParam("pid")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(v))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.get("/product/:pid").handler(ctx -> this.productQueryModelStore
                .findProductById(UUID.fromString(ctx.pathParam("pid")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(v)))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                        .end(v.getMessage())));

        router.get("/products/search").handler(ctx -> {
            MultiMap params = ctx.queryParams();
            String regex = params.get("regex");
            int from = Integer.parseInt(params.get("from"));
            int range = Integer.parseInt(params.get("range"));

            this.productQueryModelStore.findProductsByName(regex, from, range)
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(v)))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                            .end(v.getMessage()));
        });

        router.get("/product/search/count").handler(ctx -> {
            MultiMap params = ctx.queryParams();
            String regex = params.get("regex");

            this.productQueryModelStore.filteredProductsCount(regex)
                    .onSuccess(v -> {
                        JsonObject json = new JsonObject().put("rows", v);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(json));
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                            .end(v.getMessage()));
        });

        // TODO: 7/7/23 findProducts
        router.get("/products").handler(ctx -> {
            MultiMap params = ctx.queryParams();
            int from = Integer.parseInt(params.get("from"));
            int range = Integer.parseInt(params.get("range"));

            this.productQueryModelStore.findProducts(from, range)
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(v)))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                            .end(v.getMessage()));
        });

//        router.get("/image/:pid")
//                .handler(ctx -> {
//                    ctx.response().sendFile("../resources/assets/")
//                    ctx.response().sendFile(Paths.get("images", ctx.pathParam("uuidimage")));
//                });

        router.post("/product/insert").handler(ctx -> {
            try {
                final JsonObject body = ctx.body().asJsonObject();
//                String path = "src/main/resources/assets/" + body.getString("imagepath");
                this.productService.addProduct(
                                body.getString("name"),
//                                path,
                                body.getString("description"),
                                body.getDouble("price"),
                                body.getInteger("quantity"),
                                body.getString("brand"),
                                Category.valueOf(body.getString("category")))
                        .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(v)))
                        .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                                .end(v.getMessage()));
            } catch (IllegalArgumentException e) {
                ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                        .end("category should be either cellphone, smartphone or watch!");
            }
        });

        router.delete("/product/delete/:pid").handler(ctx ->
                this.productService.deleteProduct(UUID.fromString(ctx.pathParam("pid")))
                        .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK")
                                .end("product deleted successfully"))
                        .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                                .end(v.getMessage())));

        router.put("/product/update/:pid/:name/:imagepath/:description/:price/:quantity/:brand/:category")
                .handler(ctx -> {
                    UUID pid = UUID.fromString(ctx.pathParam("pid"));
                    String name = ctx.pathParam("name");
                    String image = ctx.pathParam("imagepath");
                    String description = ctx.pathParam("description");
                    double price = Double.parseDouble(ctx.pathParam("price"));
                    int quantity = Integer.parseInt(ctx.pathParam("quantity"));
                    String brand = ctx.pathParam("brand");
                    Category category = Category.valueOf(ctx.pathParam("category"));

                    this.productService.updateProduct(pid, name, image, description, price, quantity, brand, category)
                            .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK")
                                    .end("product updated successfully"))
                            .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                                    .end(v.getMessage()));
                });

        router.post("/product/:pid/image").handler(ctx -> {
            System.out.println("entered");
            UUID pid = UUID.fromString(ctx.pathParam("pid"));
//            ctx.response().putHeader("Content-Type", "multipart/form-data");
            List<FileUpload> fileUploadSet = ctx.fileUploads();
            FileUpload fileUpload = fileUploadSet.get(fileUploadSet.size()-1);
            vertx.fileSystem().readFile(fileUpload.uploadedFileName())
                    .compose(buffer -> this.productService.insertImage(pid, buffer))
                    .onSuccess(v -> {
                        System.out.println(fileUpload.charSet());
                        JsonObject json = new JsonObject();
                        json.put("pid", pid);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(json));
                    })
                    .onFailure(v ->
                            ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });




        /* CART ENTITY */

        router.get("/user/:uid/cart").handler(ctx -> this.cartQueryModelStore
                .findByUserId(UUID.fromString(ctx.pathParam("uid")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(v)))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.put("/user/:uid/product/:pid/:quantity").handler(ctx -> this.cartService
                .addItem(UUID.fromString(ctx.pathParam("uid")),
                        UUID.fromString(ctx.pathParam("pid")),
                        Integer.parseInt(ctx.pathParam("quantity")))
                .onSuccess(v -> {
                    JsonObject json = new JsonObject()
                            .put("itemid", v);
                    ctx.response().setStatusCode(200).setStatusMessage("OK").end(Json.encode(json));
                })
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        router.delete("/user/:uid/cart").handler(ctx -> this.cartService
                .buyCart(UUID.fromString(ctx.pathParam("uid")))
                .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK")
                        .end("all products from carts were bought"))
                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        server.requestHandler(router).listen(8084);
    }
}
