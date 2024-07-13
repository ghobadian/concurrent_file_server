import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;

public class Client {

    private static final Scanner sc = new Scanner(System.in);
    private Socket serverSocket;

    private static boolean connected = true;
    private Thread sender;

    public static void main(String[] args) {
        Client client = new Client();
        client.tryConnectToServer();
    }

    private void receiver() {
        System.out.println("started receiver thread");
        while (connected) {
            tryReceive();
        }
        sender.interrupt();
    }

    private void tryReceive() {
        String msg;
        try {
            msg = Utils.receiveSignal(serverSocket);
            System.out.println(msg);
            handleServerResponse(msg);
        } catch (EOFException | SocketException e) {
            connected = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleServerResponse(String msg) {
        if (msg.startsWith("RING")) {
            System.out.println("Jingle bells, jingle bells, Jingle all the way");
        }

        if (msg.startsWith("POWEROFF")) {
            System.out.println("Powering off");
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sender() {
        System.out.println("started sender thread");
        while (connected) {
            trySend();
        }
    }

    private void trySend() {
        String input;
        input = sc.nextLine();
        try {
            Utils.sendSignal(serverSocket, input);
        } catch (SocketException e) {
            System.out.println("Sorry, server is not available");
            connected = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tryConnectToServer() {
        try {
            connectToServer();
            startCommunication();
            serverSocket.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void startCommunication() throws InterruptedException {
        printIMEI();
        sender = new Thread(this::sender);
        Thread receiver = new Thread(this::receiver);

        sender.start();
        receiver.start();

        sender.join();
        receiver.join();
    }

    private void printIMEI() {
        String IMEI;
        try {
            IMEI = Utils.receiveSignal(serverSocket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("client IMEI: " + IMEI);
    }

    public void connectToServer() throws IOException {
        System.out.println("connecting to server");
        int serverPort = 1234;
        serverSocket = new Socket("localhost", serverPort);
    }
}
