package server;

import auth.PasswordService;
import user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import user.UserController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class ChatServer implements Runnable {
    private static final Logger logger = LogManager.getLogger();

    private int port = 9000;
    private ServerSocket serverSocket;
    private UserController controller;
    private ArrayList<Socket> clientSockets = new ArrayList<Socket>();

    public ChatServer(int port, UserController userController) {
        this.port = port;
        this.controller = userController;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
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

            while ((msg = in.readLine()) != null) {
                if (msg.toLowerCase().startsWith("/")) {
                    ArrayList<String> result = new ArrayList<>();
                    String[] data = msg.toLowerCase().replace("/", "").split(" ");
                    String path = data[0];
                    String[] restArray = Arrays.copyOfRange(data, 1, data.length);

                    switch (path) {
                        case "login":
                            if (user != null) {
                                result.add("You are already logged in");
                                break;
                            }

                            if (restArray.length == 2) {
                                String username = restArray[0];
                                String password = restArray[1];

                                Optional<User> userOptional = controller.getUser(username, password);

                                if (userOptional.isPresent()) {
                                    user = userOptional.get();
                                    clientSockets.add(socket);
                                } else {
                                    result.add("Login failed. Please enter the correct password!");
                                    break;
                                }
                            } else {
                                result.add("Login failed. Please enter the correct password!");
                            }
                            break;
                        case "register":
                            if (user != null) {
                                result.add("You are already logged in");
                                break;
                            }

                            if (restArray.length == 2) {
                                String username = restArray[0];
                                String password = restArray[1];

                                Optional<User> userOptional = controller.createUser(username, password);

                                if (userOptional.isPresent()) {
                                    user = userOptional.get();
                                    clientSockets.add(socket);
                                } else {
                                    result.add("Register failed. Please enter the correct password!");
                                    break;
                                }

                            } else {
                                result.add("Register failed. Please enter the correct password!");
                            }
                            break;
                        case "logout":
                            user = null;
                            clientSockets.remove(socket);
                            result.add("You have been logged out");
                            break;
                        case "help":
                            result.add("/login - Login into your account, example: /login Janek Password123");
                            result.add("/register - Create an account, example: /register Olek Password321");
                            result.add("/logout - logout account");
                            break;
                        case null, default:
                            result.add("Invalid command!");
                            break;
                    }

                    if (result != null) {
                        result.forEach(out::println);
                    }
                } else {
                    if (user != null) {
                        SendMessage(user, msg);
                    } else {
                        out.println("Please login or register...");
                    }
                }
            }
        } catch (IOException e) {
            clientSockets.remove(socket);
            logger.error(e.toString());
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
