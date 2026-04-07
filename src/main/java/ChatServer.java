import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class ChatServer implements Runnable {
    private int port = 9000;
    private ServerSocket serverSocket;
    private ArrayList<Socket> clientSockets = new ArrayList<Socket>();

    private static final Logger logger = LogManager.getLogger();

    public ChatServer() {
    }

    public ChatServer(int port) {
        this.port = port;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    private User getOrCreateUser(String username) {
        return new User(username);
    }

    private void SendMessage(User user, String msg) {
        try {
            for (Socket client : clientSockets) {
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);

                out.println(user.getUsername() + " " + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {
        try {

            String msg;
            User user = null;

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("Login: /login username");
            while ((msg = in.readLine()) != null) {
                if (user != null) {
                    SendMessage(user, msg);
                }

                if (user == null) {
                    if (msg.toLowerCase().startsWith("/login")) {
                        String username = msg.substring(6).strip();
                        user = getOrCreateUser(username);
                        clientSockets.add(socket);
                        logger.info("Client sie połączył");
                    }
                }
            }

        } catch (IOException e) {
            clientSockets.remove(socket);
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);


            while (true) {
                Socket clientSocket = serverSocket.accept();

                Thread thread = new Thread(() -> handleClient(clientSocket));
                thread.start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
