package server;

import user.UserController;
import user.UserDao;
import user.UserService;

public class Main {

    public static UserController init() {
        UserDao userDao = new UserDao();
        UserService userService = new UserService(userDao);
        UserController userController = new UserController(userService);

        return userController;
    }

    public static void main(String[] args) {
        UserController userController = init();

        ChatServer chatServer = new ChatServer(9000, userController);
        Thread serverThread = new Thread(chatServer, "chat-server");
        serverThread.start();
    }

}
