package com.itsaur.internship;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;

public class UsersStoreBinary implements UsersStore {

    Vertx vertx = Vertx.vertx();
    final String BIN_PATH = "/home/souloukos@ad.itsaur.com/IdeaProjects/EshopAPI/src/main/resources/bin.txt";
    final String TEMP_PATH = "/home/souloukos@ad.itsaur.com/IdeaProjects/EshopAPI/src/main/resources/temp.txt";

    @Override
    public Future<Void> insert(User user) {
        return vertx.fileSystem().open(BIN_PATH, new OpenOptions().setAppend(true))
                .onSuccess(file -> {
                    Buffer buffer = Buffer.buffer();
                    buffer.appendByte(Integer.valueOf(user.username().length() + user.password().length()).byteValue());
                    buffer.appendByte(Integer.valueOf(user.username().length()).byteValue());
                    buffer.appendBytes(user.username().getBytes());
                    buffer.appendBytes(user.password().getBytes());
                    file.write(buffer);
                    file.close();
                })
                .mapEmpty();
    }

    @Override
    public Future<User> findUser(String username) {
        System.out.println("from findUser");
        return vertx.fileSystem().open(BIN_PATH, new OpenOptions())
                .compose(file -> readNextUser(file, 0, username));
    }

    @Override
    public Future<Void> deleteUser(User user) {
        return CompositeFuture.all(vertx.fileSystem().open(BIN_PATH, new OpenOptions()), vertx.fileSystem().open(TEMP_PATH, new OpenOptions().setAppend(true)))
                .compose(temp -> copyTo(temp.resultAt(0), temp.resultAt(1),  0, user.username()))
                .onSuccess(v -> vertx.fileSystem().delete(BIN_PATH))
                .onSuccess(v -> vertx.fileSystem().copy(TEMP_PATH, BIN_PATH))
                .onSuccess(v -> vertx.fileSystem().delete(TEMP_PATH))
                .mapEmpty();
    }


    public static Future<User> readNextUser(AsyncFile file, final int currentPosition, String username) {
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
                .onSuccess(readResult -> {
                    System.out.println(readResult.username + " " + readResult.password);
                })
                .compose(readResult -> {
                    User user = new User(readResult.username, readResult.password);
                    if (readResult.username.equals(username)) {
                        return Future.succeededFuture(user);
                    } else if (readResult.currentPosition == file.sizeBlocking()) {
                        return Future.failedFuture(new IllegalArgumentException("User not found"));
                    } else {
                        return readNextUser(file, readResult.currentPosition, username);
                    }
                });
    }

    public static Future<User> copyTo(AsyncFile file, AsyncFile temp, final int currentPosition, String username) {
        return file.read(Buffer.buffer(), 0, currentPosition, 2)
                .map(totalSizeBuf -> {
                    ReadResult readResult = new ReadResult();
                    readResult.startIndex = currentPosition;
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
                .onSuccess(readResult -> {
                    System.out.println(readResult.username + " " + readResult.password);
                })
                .compose(readResult -> {
                    User user = new User(readResult.username, readResult.password);
                    if (readResult.currentPosition == file.sizeBlocking()) {
                        if (!readResult.username.equals(username)) {
                            Buffer buffer = Buffer.buffer();
                            buffer.appendByte(Integer.valueOf(user.username().length() + user.password().length()).byteValue());
                            buffer.appendByte(Integer.valueOf(user.username().length()).byteValue());
                            buffer.appendBytes(user.username().getBytes());
                            buffer.appendBytes(user.password().getBytes());
                            temp.write(buffer);
                        }
                        return Future.succeededFuture(user);
                    } else if (readResult.username.equals(username)) {
                        return copyTo(file, temp, readResult.currentPosition, username);
                    } else {
                        Buffer buffer = Buffer.buffer();
                        buffer.appendByte(Integer.valueOf(user.username().length() + user.password().length()).byteValue());
                        buffer.appendByte(Integer.valueOf(user.username().length()).byteValue());
                        buffer.appendBytes(user.username().getBytes());
                        buffer.appendBytes(user.password().getBytes());
                        temp.write(buffer);
                        return copyTo(file, temp, readResult.currentPosition, username);
                    }
                });
    }
}
