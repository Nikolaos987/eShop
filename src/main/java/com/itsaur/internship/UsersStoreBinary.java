package com.itsaur.internship;

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
        return vertx.fileSystem().open(BIN_PATH, new OpenOptions().setAppend(true)) //.setTruncateExisting(true)
                .onSuccess(file -> {
                    Buffer buffer = Buffer.buffer();
                    buffer.appendByte(Integer.valueOf(user.username().length() + user.password().length()).byteValue());   // user total size
                    buffer.appendByte(Integer.valueOf(user.username().length()).byteValue());  // username size
                    buffer.appendBytes(user.username().getBytes());    // username data
                    buffer.appendBytes(user.password().getBytes());    // password data
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

//    @Override
//    public Future<Void> deleteUser(User user) {
//        System.out.println("\nfrom deleteUser");
//        return vertx.fileSystem().open(BIN_PATH, new OpenOptions().setAppend(false))
//                .onSuccess(file -> {
//                    Buffer buffer = Buffer.buffer();
//                    readNextUser2(file, 0, user.username(), buffer)
//                            .onSuccess(buff -> {
//                                vertx.fileSystem().delete(BIN_PATH);
//                                vertx.fileSystem().writeFile(BIN_PATH, buff);
//                            });
//                })
//                .mapEmpty();
//    }

    @Override
    public Future<User> updateUser(String username, String newPassword) {
        return vertx.fileSystem().open(BIN_PATH, new OpenOptions())
                .onSuccess(file -> {
                    Buffer buffer = Buffer.buffer();
                    readNextUser3(file, 0, username, newPassword, buffer)
                            .onSuccess(buff -> {
                                vertx.fileSystem().delete(BIN_PATH);
                                vertx.fileSystem().writeFile(BIN_PATH, buff);
                            });
                })
                .mapEmpty();
    }


    @Override
    public Future<Void> deleteUser(User user) {
        return vertx.fileSystem().open(BIN_PATH, new OpenOptions())
                .compose(v -> {
                    Buffer buffer = Buffer.buffer();
                    return readNextUser2(v, 0, user.username(), buffer);
                })
                .compose(buffer -> vertx.fileSystem().open(TEMP_PATH, new OpenOptions())
                        .compose(d -> d.write(buffer)))
                .compose(v -> vertx.fileSystem().delete(BIN_PATH))
                .compose(v -> vertx.fileSystem().copy(TEMP_PATH, BIN_PATH))
                .compose(v -> vertx.fileSystem().delete(TEMP_PATH));
    }


    //-------------------------METHODS-------------------------\\

    public Future<Void> copy(User user) {
        return vertx.fileSystem().open(TEMP_PATH, new OpenOptions().setAppend(true)) //.setTruncateExisting(true)
                .onSuccess(file -> {
                    Buffer buffer = Buffer.buffer();
                    buffer.appendByte(Integer.valueOf(user.username().length() + user.password().length()).byteValue());   // user total size
                    buffer.appendByte(Integer.valueOf(user.username().length()).byteValue());  // username size
                    buffer.appendBytes(user.username().getBytes());    // username data
                    buffer.appendBytes(user.password().getBytes());    // password data
                    file.write(buffer);
                    file.close();
                })
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

    public static Future<Buffer> readNextUser2(AsyncFile file, final int currentPosition, String username, Buffer buffer) {
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
                        if (readResult.username.equals(username))
                            return Future.succeededFuture(buffer);
                        else {
                            buffer.appendByte(Integer.valueOf(user.username().length() + user.password().length()).byteValue());   // user total size
                            buffer.appendByte(Integer.valueOf(user.username().length()).byteValue());  // username size
                            buffer.appendBytes(user.username().getBytes());    // username data
                            buffer.appendBytes(user.password().getBytes());    // password data
                            return Future.succeededFuture(buffer);
                        }
                    } else if (readResult.username.equals(username)) {
                        return readNextUser2(file, readResult.currentPosition, username, buffer);
                    } else {
//                        Buffer buffer = Buffer.buffer();
                        buffer.appendByte(Integer.valueOf(user.username().length() + user.password().length()).byteValue());   // user total size
                        buffer.appendByte(Integer.valueOf(user.username().length()).byteValue());  // username size
                        buffer.appendBytes(user.username().getBytes());    // username data
                        buffer.appendBytes(user.password().getBytes());    // password data
//                        file.write(buffer);
//                        file.close();
                        return readNextUser2(file, readResult.currentPosition, username, buffer);
                    }

                });
    }

//    public static Future<User> deleteNextUser(AsyncFile file, final int currentPosition, String username) {
//        return file.read(Buffer.buffer(), 0, currentPosition, 2)
//                .map(totalSizeBuf -> {
//                    ReadResult readResult = new ReadResult();
//                    readResult.startIndex = currentPosition;
//                    readResult.currentPosition = currentPosition + 2;
//                    readResult.totalLength = totalSizeBuf.getBytes()[0];
//                    readResult.usernameLength = totalSizeBuf.getBytes()[1];
//                    return readResult;
//                })
//                .compose(readResult -> file.read(Buffer.buffer(), 0, readResult.currentPosition, readResult.usernameLength).map(usernameBuf -> {
//                    readResult.currentPosition = readResult.currentPosition + readResult.usernameLength;
//                    readResult.username = new String(usernameBuf.getBytes());
//                    return readResult;
//                }))
//                .compose(readResult -> file.read(Buffer.buffer(), 0, readResult.currentPosition, readResult.totalLength - readResult.usernameLength).map(passwordBuf -> {
//                    readResult.currentPosition = readResult.currentPosition + (readResult.totalLength - readResult.usernameLength);
//                    readResult.password = new String(passwordBuf.getBytes());
//                    return readResult;
//                }))
//                .onSuccess(readResult -> {
//                    System.out.println(readResult.username + " " + readResult.password);
//                })
//                .compose(readResult -> {
//                    User user = new User(readResult.username, readResult.password);
//                    Buffer buffer = Buffer.buffer();
//
//                    if (readResult.currentPosition == file.sizeBlocking()) {
//
//                    } else if (username.equals(readResult.username)) {
//
//                    } else {
//                        vertx.fileSystem().open(TEMP_PATH, new OpenOptions().setAppend(true));
//                        buffer.appendByte(Integer.valueOf(user.username().length() + user.password().length()).byteValue());   // user total size
//                        buffer.appendByte(Integer.valueOf(user.username().length()).byteValue());  // username size
//                        buffer.appendBytes(user.username().getBytes());    // username data
//                        buffer.appendBytes(user.password().getBytes());    // password data
//                        file.write(buffer);
//                        file.close();
//                    }
//                });
//    }


    public static Future<Buffer> readNextUser3(AsyncFile file, final int currentPosition, String username, String newPassword, Buffer buffer) {
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
                        if (readResult.username.equals(username))
                            return Future.succeededFuture(buffer);
                        else {
                            buffer.appendByte(Integer.valueOf(user.username().length() + user.password().length()).byteValue());   // user total size
                            buffer.appendByte(Integer.valueOf(user.username().length()).byteValue());  // username size
                            buffer.appendBytes(user.username().getBytes());    // username data
                            buffer.appendBytes(user.password().getBytes());    // password data
                            return Future.succeededFuture(buffer);
                        }
                    } else if (readResult.username.equals(username)) {
                        buffer.appendByte(Integer.valueOf(user.username().length() + newPassword.length()).byteValue());   // user total size
                        buffer.appendByte(Integer.valueOf(user.username().length()).byteValue());  // username size
                        buffer.appendBytes(user.username().getBytes());    // username data
                        buffer.appendBytes(newPassword.getBytes());    // password data
                        return readNextUser3(file, readResult.currentPosition, username, newPassword, buffer);
                    } else {
//                        Buffer buffer = Buffer.buffer();
                        buffer.appendByte(Integer.valueOf(user.username().length() + user.password().length()).byteValue());   // user total size
                        buffer.appendByte(Integer.valueOf(user.username().length()).byteValue());  // username size
                        buffer.appendBytes(user.username().getBytes());    // username data
                        buffer.appendBytes(user.password().getBytes());    // password data
//                        file.write(buffer);
//                        file.close();
                        return readNextUser3(file, readResult.currentPosition, username, newPassword, buffer);
                    }
                });
    }
}
