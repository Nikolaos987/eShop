package com.itsaur.internship;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.io.*;
import java.util.*;


public class TestVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws IOException {

        Users users = new Users();
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        JsonArray jsonArray = new JsonArray();
        List<JsonObject> array = new ArrayList<>();
        final String USERS_PATH = "/home/souloukos@ad.itsaur.com/IdeaProjects/EshopAPI/src/main/resources/test.txt";
        final String BIN_PATH = "/home/souloukos@ad.itsaur.com/IdeaProjects/EshopAPI/src/main/resources/users.txt";
        FileSystem fs = vertx.fileSystem();
        String letters = "qwertyuiopasdfghjklzxcvbnm";
        String numbers = "1234567890";


        router.route().handler(BodyHandler.create());

        router.post("/write").handler(ctx -> {
            String username = ctx.body().asJsonObject().getValue("username").toString();
            String password = ctx.body().asJsonObject().getValue("password").toString();

            OpenOptions options = new OpenOptions().setAppend(true);


            //Binary File
            vertx.fileSystem().open(BIN_PATH, new OpenOptions().setAppend(true))
                .onSuccess(h -> {
                    vertx.executeBlocking(v -> {
                        //user size
                        //username size
                        //username data
                        //password data
                        byte totalSize = Integer.valueOf(2 + username.length() + password.length()).byteValue();
                        Buffer buffer = Buffer.buffer();
                        buffer.appendByte(totalSize);
                        buffer.appendByte(Integer.valueOf(username.length()).byteValue());
                        buffer.appendBytes(username.getBytes());
                        buffer.appendBytes(password.getBytes());
                        h.write(buffer);
                        h.close();
                        System.out.println("Finished");
                        ctx.response().setStatusCode(200).setStatusMessage("SUCCESS").end("registered successfully!");
                    });
                }).onFailure(fail -> {
                  ctx.response().setStatusCode(500).setStatusMessage("FAILURE").end("Could not open file");
              });

//            vertx.fileSystem()
//                    .open(USERS_PATH, options)
//                    .onSuccess(e -> {
//
//                        final Buffer newUser = new JsonObject().put("username", username).put("password", password).toBuffer();
//                        final Buffer newLine = Buffer.buffer(new String("\n").getBytes());
//
//                        Buffer buffer = Buffer.buffer();
//                        e.handler(b -> buffer.appendBuffer(b));
//                        e.endHandler(c -> {
//                            String allUsers = new String(buffer.getBytes());
//                            String[] separatedUsers = allUsers.split("\n");
//                            boolean exists = Arrays.stream(separatedUsers)
//                                    .map(s -> new JsonObject(s))
//                                    .anyMatch(userJson -> userJson.getString("username").equals(username) &&
//                                            userJson.getString("password").equals(password));
//                            if (!exists) {
//                                System.out.println(newLine.length() + newUser.length());
//                                e.write(newUser);
//                                e.write(newLine);
//                                e.close();
//                                ctx.response().setStatusCode(200).setStatusMessage("success").end("user written!");
//                            } else {
//                                ctx.response().setStatusCode(500).setStatusMessage("Failure").end("User already exists!");
//                            }
//                        });
//
//
//                    })
//                    .onFailure(f -> {
//                        ctx.response().setStatusCode(500).setStatusMessage("Could not read file").end("could not read file");
//                    });
        });
        server.requestHandler(router).listen(8089);



    }

//    public static String generateRandom(String characters) {
//        Random random = new Random();
//
//        int size = random.nextInt(10, 20);
//        StringBuilder builder = new StringBuilder();
//
//        IntStream.range(0, size)
//                .forEach(i -> {
//                    int character = random.nextInt(0, characters.length());
//                    builder.append(characters.charAt(character));
//                });
//
//        return builder.toString();
//    }

}
