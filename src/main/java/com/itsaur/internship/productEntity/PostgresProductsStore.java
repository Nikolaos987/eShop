package com.itsaur.internship.productEntity;

import com.itsaur.internship.cartEntity.CartItem;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import net.datafaker.Faker;

import javax.swing.text.html.FormSubmitEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresProductsStore implements ProductsStore {

    Vertx vertx = Vertx.vertx();
    private final PgPool pgPool;

    public PostgresProductsStore(PgPool pgPool) {
        this.pgPool = pgPool;
    }

    @Override
    public Future<Void> insertMultiple(int size) {
        List<Integer> products = new ArrayList<>();
        return insertNextProduct(size, 1)
                .compose(result -> Future.succeededFuture());
    }

    public Future<Void> insertNextProduct(int size, int position) {
        Faker faker = new Faker();
        UUID pid = UUID.randomUUID();
        String name = faker.funnyName().name();
        String description = faker.famousLastWords().lastWords();
        int range = (2000 - 10) + 1;
        double price = (int) (((Math.random() * ((2000 - 10) + 1)) + 10) * 100) / 100.0;
        int quantity = (int) (Math.random() * ((200 - 1) + 1)) + 1;
        String brand = faker.brand().car();
        String category = faker.cat().breed();
        return pgPool
                .preparedQuery("INSERT INTO product (pid, name, description, price, quantity, brand, category) " +
                        "VALUES ($1, $2, $3, $4, $5, $6, $7);")
                .execute(Tuple.of(pid, name, description, price, quantity, brand, category))
                .compose(records2 -> {
                    if (position < size) {
                        if (position % 1000 == 0) {
                            System.out.println("products created: " + position + "...");
                        }
                        return insertNextProduct(size, position + 1);
                    }
                    return Future.succeededFuture();
                });
    }

    @Override
    public Future<Product> insert(Product product) {
        return pgPool
                .preparedQuery("INSERT INTO product (pid, name, description, price, quantity, brand, category) " +
                        "VALUES ($1, $2, $3, $4, $5, $6, $7);")
                .execute(Tuple.of(product.pid(), product.name(), product.description(), product.price(),
                        product.quantity(), product.brand(), product.category()))
                .compose(res -> pgPool
                        .preparedQuery("SELECT * FROM product WHERE pid = $1")
                        .execute(Tuple.of(product.pid()))
                        .compose(records -> {
                            try {
                                Row row = records.iterator().next();
                                Product newProduct = new Product(
                                        row.getUUID("pid"), row.getString("name"),
                                        row.getString("description"),
                                        row.getDouble("price"), row.getInteger("quantity"),
                                        row.getString("brand"), row.getString("category"));
                                return Future.succeededFuture(newProduct);
                            } catch (Exception e) {
                                return Future.failedFuture(new IllegalArgumentException("error"));
                            }
                        }));
    }

    @Override
    public Future<Product> findProduct(UUID pid) {
        return pgPool
                .preparedQuery("SELECT * FROM product WHERE pid = $1;")
                .execute(Tuple.of(pid))
                .compose(records -> {
                    Row row = records.iterator().next();
                    Product product = new Product(
                            row.getUUID("pid"),
                            row.getString("name"),
                            row.getString("description"),
                            row.getDouble("price"),
                            row.getInteger("quantity"),
                            row.getString("brand"),
                            row.getString("category"));
                    return Future.succeededFuture(product);
                })
                .otherwiseEmpty();
    }

    // TODO: 7/7/23 DELETE
    @Override
    public Future<Product> findProduct(String name) {
        return pgPool
                .preparedQuery("SELECT * FROM product WHERE name = $1;")
                .execute(Tuple.of(name))
                .compose(records -> {
                    Row row = records.iterator().next();
                    Product product = new Product(
                            row.getUUID("pid"),
                            row.getString("name"),
                            row.getString("description"),
                            row.getDouble("price"),
                            row.getInteger("quantity"),
                            row.getString("brand"),
                            row.getString("category"));
                    return Future.succeededFuture(product);
                })
                .otherwiseEmpty();
    }

    @Override
    public Future<Void> deleteProduct(UUID pid) {
        return pgPool
                .preparedQuery("DELETE FROM product WHERE pid = $1")
                .execute(Tuple.of(pid))
                .compose(records -> Future.succeededFuture());
    }

    @Override
    public Future<Void> updateProduct(Product product) {
        return pgPool
                .preparedQuery("UPDATE product " +
                        "SET name = $1, " +
                        "description = $2, price = $3, " +
                        "quantity = $4, brand = $5, category = $6  " +
                        "WHERE pid = $7")
                .execute(Tuple.of(
                        product.name(), product.description(),
                        product.price(), product.quantity(),
                        product.brand(), product.category(),
                        product.pid()))
                .compose(records -> Future.succeededFuture());
    }

    @Override
    public Future<Void> insertImage(UUID pid, Buffer buffer) {
        return vertx.fileSystem()
                .writeFile("/home/souloukos@ad.itsaur.com/IdeaProjects/EshopAPI/src/main/resources/assets/" + pid + ".jpeg", buffer).onSuccess(v -> Future.succeededFuture());
//                .compose(v -> vertx.fileSystem()
//                        .open("/home/souloukos@ad.itsaur.com/IdeaProjects/EshopAPI/src/main/resources/assets/" + pid + ".jpeg", new OpenOptions())
//                        .compose(file -> file.write(buffer))
//                        .compose(result -> Future.succeededFuture()));
    }

    @Override
    public Future<Void> updateProducts(ArrayList<CartItem> items) {
//        return client
//                .preparedQuery("UPDATE product SET quantity = $2 WHERE pid = $1")
//                .execute(Tuple.of())
//                .compose(records -> Future.succeededFuture());

        return updateNextItem(items, 0)
                .compose(result -> Future.succeededFuture());
    }

    @Override
    public Future<Void> updateProducts(UUID uid) {
        return pgPool
//                .preparedQuery("UPDATE product SET quantity = quantity - (SELECT quantity FROM cartitem JOIN cart ON cartitem.cid = cart.cid WHERE uid = $1);")
                .preparedQuery("SELECT pid FROM cartitem JOIN cart ON cartitem.cid = cart.cid WHERE uid = $1;")
                .execute(Tuple.of(uid))
                .onSuccess(records -> records.forEach(row -> pgPool
                        .preparedQuery("UPDATE product SET quantity = quantity - (SELECT quantity " +
                                "FROM cartitem JOIN cart ON cartitem.cid = cart.cid " +
                                "WHERE pid = $1 AND uid = $2) " +
                                "WHERE pid = $1")
                        .execute(Tuple.of(row.getUUID("pid"), uid))
                        .compose(recs -> Future.succeededFuture())))
                .compose(res -> Future.succeededFuture());
    }

    @Override
    public Future<UUID> addRelatedProduct(UUID r_pid, UUID to_pid) {
        return pgPool
                .preparedQuery("INSERT INTO related_products (r_pid, to_pid) VALUES ($1, $2)")
                .execute(Tuple.of(r_pid, to_pid))
                .compose(records -> Future.succeededFuture(r_pid));
    }

    @Override
    public Future<UUID> findRelatedProduct(UUID r_pid, UUID to_pid) {
        return pgPool
                .preparedQuery("SELECT * FROM related_products WHERE r_pid = $1 AND to_pid = $2 " +
                        "UNION " +
                        "SELECT * FROM related_products WHERE r_pid = $2 AND to_pid = $1")
                .execute(Tuple.of(r_pid, to_pid))
                .compose(records -> {
                    UUID id = records.iterator().next().getUUID("id");
                    return Future.succeededFuture(id);
                })
                .otherwiseEmpty();
    }

    public Future<Void> updateNextItem(ArrayList<CartItem> items, int position) {
        return inStock(items.get(position).pid(), items.get(position).quantity())
                .compose(result -> pgPool
                        .preparedQuery("UPDATE product SET quantity = quantity - $1 WHERE pid = $2")
                        .execute(Tuple.of(items.get(position).quantity(), items.get(position).pid()))
                        .compose(records2 -> {
                            if (position + 1 < items.size()) {
                                return updateNextItem(items, position + 1);
                            }
                            return Future.succeededFuture();
                        }));
    }

    public Future<Void> inStock(UUID pid, int quantity) {
        return pgPool
                .preparedQuery("SELECT quantity FROM product WHERE pid = $1")
                .execute(Tuple.of(pid))
                .compose(records -> {
                    Row row = records.iterator().next();
                    if (row.getInteger("quantity") - quantity >= 0)
                        return Future.succeededFuture();
                    return Future.failedFuture("cannot buy such amount. Out of stock");
                });
    }
}
