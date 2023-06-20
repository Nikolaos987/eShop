package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;

import java.util.ArrayList;
import java.util.UUID;

public class UsersStoreBinary implements UsersStore {

    private final Vertx vertx = Vertx.vertx();
    private final String BIN_PATH;
    private final String TEMP_PATH;
    public UsersStoreBinary(String binFile, String tempFile) {
        this.BIN_PATH = binFile;
        this.TEMP_PATH = tempFile;
    }


    @Override
    public Future<Void> insert(User user) {
        return vertx.fileSystem().open(BIN_PATH, new OpenOptions().setAppend(true))
                .onSuccess(file -> {
                    writeTo(file, user).compose(v -> Future.succeededFuture());
                    file.close();
                })
                .mapEmpty();
    }

    @Override
    public Future<User> findUser(String username) {
        return vertx.fileSystem().open(BIN_PATH, new OpenOptions())
                .compose(file -> readUser(file, 0, username));
    }

    @Override
    public Future<Void> deleteUser(User user) {
        return Future.all(vertx.fileSystem().open(BIN_PATH, new OpenOptions()),
                        vertx.fileSystem().open(TEMP_PATH, new OpenOptions().setAppend(true)))
                .compose(temp -> copyDeletedTo(temp.resultAt(0), temp.resultAt(1),  0, user.username()))
                .compose(v -> vertx.fileSystem().delete(BIN_PATH))
                .compose(v -> vertx.fileSystem().copy(TEMP_PATH, BIN_PATH))
                .compose(v -> vertx.fileSystem().delete(TEMP_PATH))
                .mapEmpty();
    }

    @Override
    public Future<Void> updateUser(String username, String password) {
        return Future.all(vertx.fileSystem().open(BIN_PATH, new OpenOptions()),
                        vertx.fileSystem().open(TEMP_PATH, new OpenOptions().setAppend(true)))
                .compose(temp -> copyModifiedTo(temp.resultAt(0), temp.resultAt(1),  0, username, password))
                .compose(v -> vertx.fileSystem().delete(BIN_PATH))
                .compose(v -> vertx.fileSystem().copy(TEMP_PATH, BIN_PATH))
                .compose(v -> vertx.fileSystem().delete(TEMP_PATH))
                .mapEmpty();
    }

    @Override
    public Future<Product> findProduct(String name) {
        return null;
    }

    @Override
    public Future<ArrayList<Product>> filter(double price, String category) {
        return null;
    }

    @Override
    public Future<Void> addToCart(UUID id, int quantity) {
        return null;
    }

    @Override
    public Future<Void> checkQuantity(UUID id, int quantity) {
        return null;
    }

    @Override
    public Future<Void> buy() {
        return null;
    }


    public static Future<User> readUser(AsyncFile file, final int currentPosition, String username) {
        return next(file, currentPosition)
                .compose(readResult -> {
                    User user = new User(readResult.username, readResult.password);
                    if (readResult.username.equals(username)) {
                        return Future.succeededFuture(user);
                    } else if (readResult.currentPosition == file.sizeBlocking()) {
                        return file.close().compose(v -> Future.failedFuture(new IllegalArgumentException("User not found")));
                    } else {
                        return readUser(file, readResult.currentPosition, username);
                    }
                });
    }

    public static Future<User> copyModifiedTo(AsyncFile file, AsyncFile temp, final int currentPosition, String username, String password) {
        return next(file, currentPosition)
                .compose(readResult -> {
                    User user = new User(readResult.username, readResult.password);
                    if (readResult.currentPosition != file.sizeBlocking()) {
                        if (!readResult.username.equals(username)) {
                            writeTo(temp, user).compose(v -> Future.succeededFuture());
                        } else {
                            writeTo(temp, password, user).compose(v -> Future.succeededFuture());
                        }
                        return copyModifiedTo(file, temp, readResult.currentPosition, username, password);
                    } else {
                        if (!readResult.username.equals(username)) {
                            writeTo(temp, user).compose(v -> Future.succeededFuture());
                        } else {
                            writeTo(temp, password, user).compose(v -> Future.succeededFuture());
                        }
                        return file.close().compose(v -> Future.succeededFuture());
                    }
                });
    }

    public static Future<User> copyDeletedTo(AsyncFile file, AsyncFile temp, final int currentPosition, String username) {
        return next(file, currentPosition)
                .compose(readResult -> {
                    User user = new User(readResult.username, readResult.password);
                    if (readResult.currentPosition != file.sizeBlocking()) {
                        if (!readResult.username.equals(username)) {
                            writeTo(temp, user).compose(v -> Future.succeededFuture());
                        }
                        return copyDeletedTo(file, temp, readResult.currentPosition, username);
                    } else {
                        if (!readResult.username.equals(username)) {
                            writeTo(temp, user).compose(v -> Future.succeededFuture());
                        }
                        return file.close().compose(v -> Future.succeededFuture());
                    }
                });
    }

    public static Future<ReadResult> next(AsyncFile file, final int currentPosition) {
        return file.read(Buffer.buffer(), 0, currentPosition, 2)
                .map(totalSizeBuf -> {
                    ReadResult readResult = new ReadResult();
                    readResult.currentPosition = currentPosition + 2;
                    readResult.totalLength = totalSizeBuf.getBytes()[0];
                    readResult.usernameLength = totalSizeBuf.getBytes()[1];
                    return readResult;
                })
                .compose(readResult -> file.read(Buffer.buffer(), 0, readResult.currentPosition, readResult.usernameLength).map(usernameBuf -> {
                    readResult.currentPosition = readResult.currentPosition + readResult.usernameLength;
                    readResult.username = new String(usernameBuf.getBytes());
                    return readResult;
                }))
                .compose(readResult -> file.read(Buffer.buffer(), 0, readResult.currentPosition, readResult.totalLength - readResult.usernameLength).map(passwordBuf -> {
                    readResult.currentPosition = readResult.currentPosition + (readResult.totalLength - readResult.usernameLength);
                    readResult.password = new String(passwordBuf.getBytes());
                    return readResult;
                }))
                .onSuccess(readResult -> System.out.println(readResult.username + " " + readResult.password));
    }


    private static Future<Void> writeTo(AsyncFile file, User user) {
        String password = user.password();
        return writeTo(file, password, user);
    }

    private static Future<Void> writeTo(AsyncFile file, String password, User user) {
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(Integer.valueOf(user.username().length() + password.length()).byteValue());
        buffer.appendByte(Integer.valueOf(user.username().length()).byteValue());
        buffer.appendBytes(user.username().getBytes());
        buffer.appendBytes(password.getBytes());
        return file.write(buffer);
    }
}
