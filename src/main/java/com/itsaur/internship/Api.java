package com.itsaur.internship;

import com.itsaur.internship.cartEntity.CartService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
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

        /* USER ENTITY */

        router.post("/user/login").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            Objects.requireNonNull(body.getString("username"));
            Objects.requireNonNull(body.getString("password"));
            this.userService.login(
                            body.getString("username"),
                            body.getString("password"))
                    .onSuccess(v -> {
                        JsonObject user_json = new JsonObject();
                        user_json.put("uid", v.uid().toString());
                        user_json.put("username", v.username());
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(user_json.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                            .end(v.getMessage()));
        });

        router.post("/user/register").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            Objects.requireNonNull(body.getString("username"));
            Objects.requireNonNull(body.getString("password"));
            this.userService.register(
                            body.getString("username"),
                            body.getString("password"))
                    .onSuccess(v -> {
                        JsonObject uid_json = new JsonObject();
                        uid_json.put("uid", v);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(uid_json.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                            .end(v.getMessage()));
        });

        router.delete("/user/:uid").handler(ctx -> {
            Objects.requireNonNull(ctx.pathParam("uid"));
            String uid = ctx.pathParam("uid");
            this.userService.delete(UUID.fromString(uid))
                    .onSuccess(v -> {
                        JsonObject uid_json = new JsonObject();
                        uid_json.put("uid", uid);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(uid_json.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        router.get("/user/:uid").handler(ctx -> {
            Objects.requireNonNull(ctx.pathParam("uid"));
            this.userService.fetch(UUID.fromString(ctx.pathParam("uid")))
                    .onSuccess(v -> {
                        JsonObject uid_json = new JsonObject().put("uid", v);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(uid_json.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        router.put("/user/:uid/password").handler(ctx -> {
            final JsonObject body = ctx.body().asJsonObject();
            Objects.requireNonNull(ctx.pathParam("uid"));
            Objects.requireNonNull(body.getString("currentPassword"));
            Objects.requireNonNull(body.getString("newPassword"));
            this.userService.update(UUID.fromString(ctx.pathParam("uid")),
                            body.getString("currentPassword"),
                            body.getString("newPassword"))
                    .onSuccess(v -> {
                        JsonObject uid_json = new JsonObject().put("uid", v);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(uid_json.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                            .end(v.getMessage()));
        });




        /* PRODUCT ENTITY */

        router.get("/product/count").handler(ctx -> {
            this.productQueryModelStore.productsCount()
                    .onSuccess(v -> {
                        JsonObject count_json = new JsonObject().put("totalProducts", v);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(count_json.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        router.get("/product/image/:pid").handler(ctx -> {
            Objects.requireNonNull(ctx.pathParam("pid"));
            try {
                this.productQueryModelStore
                        .findImageById(UUID.fromString(ctx.pathParam("pid")))
                        .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end(v))
                        .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
            } catch (IllegalArgumentException e) {
                ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(e.getMessage());
            }
        });

        router.get("/product/:pid").handler(ctx -> {
            Objects.requireNonNull(ctx.pathParam("pid"));
            try {
                this.productQueryModelStore
                        .findProductById(UUID.fromString(ctx.pathParam("pid")))
                        .onSuccess(v -> {
                            JsonObject product_json = new JsonObject();
                            product_json.put("pid", v.pid());
                            product_json.put("name", v.name());
                            product_json.put("description", v.description());
                            product_json.put("price", v.price());
                            product_json.put("quantity", v.quantity());
                            product_json.put("brand", v.brand());
                            product_json.put("category", v.category());
                            ctx.response().setStatusCode(200).setStatusMessage("OK").end(product_json.toBuffer());
                        })
                        .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                                .end(v.getMessage()));
            } catch (IllegalArgumentException e) {
                ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(e.getMessage());
            }

        });

        router.get("/products/search").handler(ctx -> {
            MultiMap params = ctx.queryParams();
            Objects.requireNonNull(params.get("regex"));
            Objects.requireNonNull(params.get("from"));
            Objects.requireNonNull(params.get("range"));
            if (Integer.parseInt(params.get("range")) > 50) {
                ctx.response().setStatusCode(400).end("Range too large");
                return;
            }
            String regex = params.get("regex");
            int from = Integer.parseInt(params.get("from"));
            int range = Integer.parseInt(params.get("range"));

            this.productQueryModelStore.findProductsByName(regex, from, range)
                    .onSuccess(v -> {
                        JsonArray products_jsonArray = new JsonArray();
                        v.forEach(product -> {
                            JsonObject product_json = new JsonObject();
                            product_json.put("pid", product.pid());
                            product_json.put("name", product.name());
                            product_json.put("description", product.description());
                            product_json.put("price", product.price());
                            product_json.put("quantity", product.quantity());
                            product_json.put("brand", product.brand());
                            product_json.put("category", product.category());
                            products_jsonArray.add(product_json);
                        });
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(products_jsonArray.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                            .end(v.getMessage()));
        });

        router.get("/product/search/count").handler(ctx -> {
            MultiMap params = ctx.queryParams();
            Objects.requireNonNull(params.get("regex"));
            String regex = params.get("regex");

            this.productQueryModelStore.filteredProductsCount(regex)
                    .onSuccess(v -> {
                        JsonObject count_json = new JsonObject().put("totalProducts", v);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(count_json.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                            .end(v.getMessage()));
        });

        // TODO: 7/7/23 findProducts
        router.get("/products").handler(ctx -> {
            MultiMap params = ctx.queryParams();
            Objects.requireNonNull(params.get("from"));
            Objects.requireNonNull(params.get("range"));
            int from = Integer.parseInt(params.get("from"));
            int range = Integer.parseInt(params.get("range"));
            if (range > 50) {
                ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end("Range too large");
                throw new IllegalArgumentException("range too large");
            }

            this.productQueryModelStore.findProducts(from, range)
                    .onSuccess(v -> {
                        JsonArray products_jsonArray = new JsonArray();
                        v.forEach(product -> {
                            JsonObject product_json = new JsonObject();
                            product_json.put("pid", product.pid());
                            product_json.put("name", product.name());
                            product_json.put("description", product.description());
                            product_json.put("price", product.price());
                            product_json.put("quantity", product.quantity());
                            product_json.put("brand", product.brand());
                            product_json.put("category", product.category());
                            products_jsonArray.add(product_json);
                        });
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(products_jsonArray.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                            .end(v.getMessage()));
        });

        router.post("/product/insert").handler(ctx -> {
            try {
                final JsonObject body = ctx.body().asJsonObject();
                Objects.requireNonNull(body.getString("name"));
                Objects.requireNonNull(body.getString("description"));
                Objects.requireNonNull(body.getString("price"));
                Objects.requireNonNull(body.getString("quantity"));
                Objects.requireNonNull(body.getString("brand"));
                this.productService.addProduct(
                                body.getString("name"),
                                body.getString("description"),
                                body.getDouble("price"),
                                body.getInteger("quantity"),
                                body.getString("brand"),
                                Category.valueOf(body.getString("category")))
                        .onSuccess(v -> {
                            JsonObject product_json = new JsonObject();
                            product_json.put("pid", v.pid());
                            product_json.put("name", v.name());
                            product_json.put("description", v.description());
                            product_json.put("price", v.price());
                            product_json.put("quantity", v.quantity());
                            product_json.put("brand", v.brand());
                            product_json.put("category", v.category());
                            ctx.response().setStatusCode(200).setStatusMessage("OK").end(product_json.toBuffer());
                        })
                        .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                                .end(v.getMessage()));
            } catch (IllegalArgumentException e) {
                ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
                        .end("category should be either cellphone, smartphone or watch!");
            }
        });

//        router.delete("/product/delete/:pid").handler(ctx ->
//                this.productService.deleteProduct(UUID.fromString(ctx.pathParam("pid")))
//                        .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK")
//                                .end("product deleted successfully"))
//                        .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
//                                .end(v.getMessage())));

//        router.put("/product/update/:pid/:name/:imagepath/:description/:price/:quantity/:brand/:category")
//                .handler(ctx -> {
//                    UUID pid = UUID.fromString(ctx.pathParam("pid"));
//                    String name = ctx.pathParam("name");
//                    String image = ctx.pathParam("imagepath");
//                    String description = ctx.pathParam("description");
//                    double price = Double.parseDouble(ctx.pathParam("price"));
//                    int quantity = Integer.parseInt(ctx.pathParam("quantity"));
//                    String brand = ctx.pathParam("brand");
//                    Category category = Category.valueOf(ctx.pathParam("category"));
//
//                    this.productService.updateProduct(pid, name, image, description, price, quantity, brand, category)
//                            .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK")
//                                    .end("product updated successfully"))
//                            .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request")
//                                    .end(v.getMessage()));
//                });

        router.post("/product/:pid/image").handler(ctx -> {
            Objects.requireNonNull(ctx.pathParam("pid"));
            UUID pid = UUID.fromString(ctx.pathParam("pid"));
//            ctx.response().putHeader("Content-Type", "multipart/form-data");
            List<FileUpload> fileUploadSet = ctx.fileUploads();
            FileUpload fileUpload = fileUploadSet.get(fileUploadSet.size() - 1);
            vertx.fileSystem().readFile(fileUpload.uploadedFileName())
                    .compose(buffer -> this.productService.insertImage(pid, buffer))
                    .onSuccess(v -> {
//                        System.out.println(fileUpload.charSet());
                        JsonObject pid_json = new JsonObject();
                        pid_json.put("pid", pid);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(pid_json.toBuffer());
                    })
                    .onFailure(v ->
                            ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });




        /* CART ENTITY */

        // fetch cart products
        router.get("/user/:uid/cart").handler(ctx -> {
            Objects.requireNonNull(ctx.pathParam("uid"));
            this.cartQueryModelStore
                    .findByUserId(UUID.fromString(ctx.pathParam("uid")))
                    .onSuccess(v -> {
                        JsonArray items_jsonArray = new JsonArray();
                        v.forEach(item -> {
                            JsonObject item_json = new JsonObject();
                            item_json.put("pid", item.pid());
                            item_json.put("name", item.name());
                            item_json.put("price", item.price());
                            item_json.put("quantity", item.quantity());
                            items_jsonArray.add(item_json);
                        });
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(items_jsonArray.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        // add or remove from cart
        router.put("/user/:uid/product/:pid/:quantity").handler(ctx -> {
            Objects.requireNonNull(ctx.pathParam("uid"));
            Objects.requireNonNull(ctx.pathParam("pid"));
            Objects.requireNonNull(ctx.pathParam("quantity"));
            this.cartService
                    .addItem(UUID.fromString(ctx.pathParam("uid")),
                            UUID.fromString(ctx.pathParam("pid")),
                            Integer.parseInt(ctx.pathParam("quantity")))
                    .onSuccess(v -> {
                        JsonObject cid_json = new JsonObject();
                        cid_json.put("itemid", v);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(cid_json.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        // fetch total price of the cart products
        router.get("/user/:uid/cart/total_price").handler(ctx -> {
            Objects.requireNonNull(ctx.pathParam("uid"));
            this.cartQueryModelStore
                    .totalPrice(UUID.fromString(ctx.pathParam("uid")))
                    .onSuccess(v -> {
                        JsonObject totalPriceJson = new JsonObject();
                        totalPriceJson.put("total_price", v);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(totalPriceJson.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("OK").end());
        });

//        router.delete("/user/:uid/cart").handler(ctx -> this.cartService
//                .buyCart(UUID.fromString(ctx.pathParam("uid")))
//                .onSuccess(v -> {
//                    JsonObject cid_json = new JsonObject();
//                    cid_json.put("cid", v);
//                    ctx.response().setStatusCode(200).setStatusMessage("OK").end(cid_json.toBuffer());
//                })
//                .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage())));

        server.requestHandler(router).listen(8084);
    }
}
