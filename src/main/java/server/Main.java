package server;

public class Main {

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer(9000);

        Thread serverThread = new Thread(chatServer);
        serverThread.start();
    }

}
