package com.itsaur.internship;

import com.itsaur.internship.cartEntity.CartService;
import com.itsaur.internship.query.relatedProducts.RelatedProductsQueryModelStore;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
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

    private final RelatedProductsQueryModelStore relatedProductsQueryModelStore;

    public Api(UserService userService, ProductService productService, CartService cartService,
               CartQueryModelStore cartQueryModelStore, ProductQueryModelStore productQueryModelStore,
               UserQueryModelStore userQueryModelStore, RelatedProductsQueryModelStore relatedProductsQueryModelStore) {
        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService;
        this.cartQueryModelStore = cartQueryModelStore;
        this.productQueryModelStore = productQueryModelStore;
        this.userQueryModelStore = userQueryModelStore;
        this.relatedProductsQueryModelStore = relatedProductsQueryModelStore;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        /* USER ENTITY */

        // login
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

        // register
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

        // delete
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

        // get user ID
        router.get("/user/:uid").handler(ctx -> {
            Objects.requireNonNull(ctx.pathParam("uid"));
            this.userService.fetch(UUID.fromString(ctx.pathParam("uid")))
                    .onSuccess(v -> {
                        JsonObject uid_json = new JsonObject().put("uid", v);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(uid_json.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        // update password
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

        router.get("/products").handler(ctx -> { // get products
            MultiMap params = ctx.queryParams();
            Objects.requireNonNull(params.get("from"));
            Objects.requireNonNull(params.get("range"));
            int from = Integer.parseInt(params.get("from"));
            int range = Integer.parseInt(params.get("range"));
            if (range > 50) {
                ctx.response().setStatusCode(500).setStatusMessage("Bad Request").end("Range too large");
                throw new IllegalArgumentException("range too large");
            }
            this.productQueryModelStore
                    .findProducts(from, range)
                    .onSuccess(v -> {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.put("products", v.products());
                        jsonObject.put("totalCount", v.count());
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(jsonObject.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        // get Filtered products
        router.get("/products/search").handler(ctx -> {
            MultiMap params = ctx.queryParams();
            Objects.requireNonNull(params.get("regex"));
            Objects.requireNonNull(params.get("from"));
            Objects.requireNonNull(params.get("range"));
            int from = Integer.parseInt(params.get("from"));
            int range = Integer.parseInt(params.get("range"));
            if (range > 50) {
                ctx.response().setStatusCode(500).end("Range too large");
                return;
            }
            String regex = params.get("regex");
            this.productQueryModelStore.findProductsByName(regex, from, range)
                    .onSuccess(v -> {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.put("products", v.products());
                        jsonObject.put("totalCount", v.count());
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(jsonObject.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        // get products by Categories
        router.get("/products/categories").handler(ctx -> {
            MultiMap params = ctx.queryParams();
            int from = Integer.parseInt(params.get("from"));
            int range = Integer.parseInt(params.get("range"));
            String categories = params.get("category");
            System.out.println(categories);
            List<String> convertedCategoryArray = Arrays.asList(categories.split(","));
            String[] array = new String[convertedCategoryArray.size()];
            for (int i=0; i< array.length; i++) {
                array[i] = convertedCategoryArray.get(i);
            }
            this.productQueryModelStore.findProductsByCategories(array, from, range)
                    .onSuccess(v -> {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.put("products", v.products());
                        jsonObject.put("totalCount", v.count());
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(jsonObject.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        // get Filtered products by Categories
        router.get("/products/filtered/categories").handler(ctx -> {
            MultiMap params = ctx.queryParams();
            int from = Integer.parseInt(params.get("from"));
            int range = Integer.parseInt(params.get("range"));
            String regex = params.get("regex");
            String categories = params.get("category");
            List<String> convertedCategoryArray = Arrays.asList(categories.split(","));
            String[] array = new String[convertedCategoryArray.size()];
            for (int i=0; i< array.length; i++) {
                array[i] = convertedCategoryArray.get(i);
            }
            this.productQueryModelStore.findFilteredProductsByCategories(regex, array, from, range)
                    .onSuccess(v -> {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.put("products", v.products());
                        jsonObject.put("totalCount", v.count());
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(jsonObject.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });


        // get all category names
        router.get("/product/categories/names").handler(ctx -> {
            this.productQueryModelStore
                    .fetchCategories()
                    .onSuccess(v -> {
                        JsonObject categories = new JsonObject();
                        categories.put("categories", v.category());
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(String.valueOf(categories.toBuffer()));
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        // relate a product to another product
        router.post("/product/:r_pid/relate/:to_pid").handler(ctx -> {
            Objects.requireNonNull(ctx.pathParam("r_pid"));
            Objects.requireNonNull(ctx.pathParam("to_pid"));
            UUID r_pid = UUID.fromString(ctx.pathParam("r_pid"));
            UUID to_pid = UUID.fromString(ctx.pathParam("to_pid"));
            this.productService
                    .relateProduct(r_pid, to_pid)
                    .onSuccess(v -> {
                        JsonObject pidJson = new JsonObject();
                        pidJson.put("id", v);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(pidJson.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        // get product by ID
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

        // get image by ID
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

        // fake data
        router.post("/product/fake/data/:size").handler(ctx -> {
            int size = Integer.parseInt(ctx.pathParam("size"));
            this.productService.addFakeProducts(size)
                    .onSuccess(v -> ctx.response().setStatusCode(200).setStatusMessage("OK").end("Products created"))
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        // post new product
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
                                body.getString("category"))
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

        // post product Image
        router.post("/product/:pid/image").handler(ctx -> {
            Objects.requireNonNull(ctx.pathParam("pid"));
            UUID pid = UUID.fromString(ctx.pathParam("pid"));
//            ctx.response().putHeader("Content-Type", "multipart/form-data");
            List<FileUpload> fileUploadSet = ctx.fileUploads();
            FileUpload fileUpload = fileUploadSet.get(fileUploadSet.size() - 1);
            vertx.fileSystem().readFile(fileUpload.uploadedFileName())
                    .compose(buffer -> this.productService.insertImage(pid, buffer))
                    .onSuccess(v -> {
                        JsonObject pid_json = new JsonObject();
                        pid_json.put("pid", pid);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(pid_json.toBuffer());
                    })
                    .onFailure(v ->
                            ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });

        // Related Products
        router.get("/related_products/:r_pid").handler(ctx -> {
            Objects.requireNonNull(ctx.pathParam("r_pid"));
            UUID r_pid = UUID.fromString(ctx.pathParam("r_pid"));
            this.productQueryModelStore
                    .getRelatedProducts(r_pid)
                    .onSuccess(v -> {
                        JsonObject productsJson = new JsonObject();
                        productsJson.put("products", v);
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(productsJson.toBuffer());
                    })
                    .onFailure(v -> ctx.response().setStatusCode(400).setStatusMessage("Bad Request").end(v.getMessage()));
        });




        /* CART ENTITY */

        // fetch cart products by user ID
        router.get("/user/:uid/cart").handler(ctx -> {
            Objects.requireNonNull(ctx.pathParam("uid"));
            this.cartQueryModelStore
                    .findByUserId(UUID.fromString(ctx.pathParam("uid")))
                    .onSuccess(v -> {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.put("cartItems", v.cartItem());
                        jsonObject.put("totalPrice", v.totalPrice());
                        ctx.response().setStatusCode(200).setStatusMessage("OK").end(jsonObject.toBuffer());
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

        server.requestHandler(router).listen(8084);
    }
}
