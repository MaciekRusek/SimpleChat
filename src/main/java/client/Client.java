package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void sendMessage(String message) throws IOException {
        out.println(message);
    }

    public String loadMessage() throws IOException {
        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.startConnection("127.0.0.1", 9000);

        String msg;
        Scanner scanner = new Scanner(System.in);

        Thread thread = new Thread(() -> {
            try {
                String resp;

                while (true) {
                    resp = client.loadMessage();
                    System.out.println(resp);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();

        while (true) {
            msg = scanner.nextLine();
            client.sendMessage(msg);
        }

//        client.stopConnection();
    }
}
