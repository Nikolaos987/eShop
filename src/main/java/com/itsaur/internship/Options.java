package com.itsaur.internship;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "CRUD operations")
class Options {

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
