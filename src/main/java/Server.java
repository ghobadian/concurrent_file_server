import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

public class Server extends Thread{
    private static final int SERVER_PORT = 1234;
    private static final HashMap<String, Boolean> fileLocks = new HashMap<>();
    private static final HashMap<Long, Socket> clients = new HashMap<>();
    private static final ExecutorService clientPool = Executors.newFixedThreadPool(10);
    private Scanner sc = new Scanner(System.in);
    private static final Random random = new SecureRandom();


    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("started server");
            Thread commandsThread = new Thread(this::tryHandleServerSideCommands);
            commandsThread.start();
            Socket clientSocket;
            do {
                clientSocket = serverSocket.accept();
                long clientIMEI = generateIMEI();
                System.out.println("client: " + clientIMEI + " connected");
                Utils.sendSignal(clientSocket, String.valueOf(clientIMEI));
                clients.put(clientIMEI, clientSocket);
                clientPool.execute(new ClientHandler(clientSocket, this, clientIMEI));

            } while (!clients.isEmpty());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clientPool.shutdown();
        }
    }

    private long generateIMEI() {
        return 1000000000L + (long)(random.nextDouble() * 9000000000L);
    }


    private void tryHandleServerSideCommands() {
        System.out.println("handling server side commands");
        while (true) {
            try {
                handleServerSideCommands();
            } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleServerSideCommands() {
        String input;
        do {
            input = sc.nextLine();
            String[] split = input.split("\\*");
            if (split.length < 3) {
                System.out.println("Invalid Input");
                return;
            }
            String clientIMEI = split[1];
            Socket clientSocket = findClient(Long.parseLong(clientIMEI));
            if (split[2].equals("POWEROFF")) {
                poweroffClient(clientSocket, clientIMEI);
                return;
            }

            if (split[2].equals("FIND")) {
                ringClient(clientSocket, clientIMEI);
                return;
            }

            System.out.println("Invalid Input");
        } while (!clients.isEmpty());
    }

    private static void ringClient(Socket clientSocket, String clientIMEI) {
        System.out.println("Finding client: " + clientIMEI);
        try {
            Utils.sendSignal(clientSocket, "RING");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void poweroffClient(Socket clientSocket, String clientIMEI) {
        System.out.println("Powering off client: " + clientIMEI);
        try {
            Utils.sendSignal(clientSocket, "POWEROFF");
            sleep(2000);
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean isFileLocked(String filename) {
        return fileLocks.getOrDefault(filename, false);
    }

    public synchronized void lockFile(String filename, long IMEI) {
        System.out.println("File " + filename + " is locked by client: " + IMEI);
        fileLocks.put(filename, true);
    }

    public synchronized void unlockFile(String filename, long IMEI) {
        System.out.println("File " + filename + " is unlocked by client: "+ IMEI);
        fileLocks.put(filename, false);
    }

    public Socket findClient(long IMEI) {
        return clients.getOrDefault(IMEI, null);
    }
}
