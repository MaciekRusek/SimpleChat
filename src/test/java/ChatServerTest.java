import server.ChatServer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChatServerTest {

    @Test
    void checkIfServerUp() throws Exception {
        ChatServer chatServer = new ChatServer(0);

        Thread serverThread = new Thread(chatServer);
        serverThread.start();

        Thread.sleep(200);

        assertNotNull(chatServer.getServerSocket());
        assertTrue(chatServer.getServerSocket().isBound());
    }
}
