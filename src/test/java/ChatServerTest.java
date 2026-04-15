import server.ChatServer;
import org.junit.jupiter.api.Test;
import user.UserController;
import user.UserDao;
import user.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class ChatServerTest {

    public static UserController init() {
        UserDao userDao = new UserDao();
        UserService userService = new UserService(userDao);
        UserController userController = new UserController(userService);

        return userController;
    }

    @Test
    void checkIfServerUp() throws Exception {
        // Fixme: Powinno sie to odnosić na inną baze danych.
        UserController controller = init();

        ChatServer chatServer = new ChatServer(0, controller);

        Thread serverThread = new Thread(chatServer);
        serverThread.start();

        Thread.sleep(200);

        assertNotNull(chatServer.getServerSocket());
        assertTrue(chatServer.getServerSocket().isBound());
    }
}
