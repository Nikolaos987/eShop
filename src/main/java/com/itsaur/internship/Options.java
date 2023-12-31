package com.itsaur.internship;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "CRUD operations")
class Options {

    @Parameter(names = {"--image-path"}, description = "the file path of the product's image")
    public String imagePath;

    @Parameter(names = {"--description"}, description = "the description of the product")
    public String desc;

    @Parameter(names = {"--iid"}, description = "the UUID of the item")
    public String iid;

    @Parameter(names = {"--uid"}, description = "the UUID of the user")
    public String uid;

    @Parameter(names = {"--pid"}, description = "the UUID of the product")
    public String pid;

    @Parameter(names = {"--brand"}, description = "the brand name of the product")
    public String brand;

    @Parameter(names = {"--quantity"}, description = "the quantity of the product")
    public int quantity;

    @Parameter(names = {"--price"}, description = "the price of the product")
    public double price;

    @Parameter(names = {"--category"}, description = "the category of the product [smartphone / mobile phone]")
    public String category;

    @Parameter(names = {"--product"}, description = "the name of the product")
    public String name;

    @Parameter(names = {"--file", "-f"}, description = "the file to which the data will be saved")
    public String file;

    @Parameter(names = {"--method", "-m"}, required = true, description = "the method that will be used to send data [console / server]")
    public String method;

    @Parameter(names = {"--operation", "-o"}, description = "operation should be one of the following [login / register / delete / update]")
    public String operation;

    @Parameter(names = {"--username", "-u"}, description = "user's username")
    public String username;

    @Parameter(names = {"--password", "-p"}, hidden = true, description = "user's password")
    public String password;

    @Parameter(names = {"--new-password", "-np"}, description = "new password used to UPDATE a user's password")
    public String newPassword;

    @Parameter(names = "--port", description = "database's port. Default value = 5432")
    public int port = 5432;

    @Parameter(names = {"--host", "-h"}, description = "database's host name. Default value= localhost")
    public String host = "localhost";

    @Parameter(names = {"--database", "-d"}, description = "name of the database to connect to")
    public String database;

    @Parameter(names = "--user", description = "user of the database to connect to")
    public String user;

    @Parameter(names = {"--post-pass", "-pp"}, description = "password of the database to connect to")
    public String postPasword;

    @Parameter(names = {"--pool-size", "-ps"}, description = "pool size of the database. Default value = 5")
    public int poolSize = 5;

}
