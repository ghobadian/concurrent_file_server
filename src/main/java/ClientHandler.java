import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private Server server;
    private boolean connected = true;
    private long IMEI;
    public ClientHandler(Socket clientSocket, Server server, Long IMEI) {
        this.clientSocket = clientSocket;
        this.IMEI = IMEI;
        this.server = server;
    }

    @Override
    public void run() {
        while(connected) {
            tryHandleClient();
        }
    }

    private void tryHandleClient() {
        try {
            handleClient();
        } catch (SocketException | EOFException e) {
            connected = false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void handleClient() throws IOException {
        String request = Utils.receiveSignal(clientSocket);
        System.out.println(IMEI + ": " + request);
        if (request.isEmpty()) {
            System.out.println("ERROR: empty request");
            return;
        }

        if (request.startsWith("READ")) {
            String filename = request.split(" ")[1];
            readFromFile(filename);
            return;
        }

        if (request.startsWith("WRITE")) {
            String[] requestSplited = request.split(" ",3);
            String filename = requestSplited[1];
            String text = requestSplited[2];
            writeToFile(filename, text);
            return;
        }



        String[] split = request.split("\\*");
        if (split[1].equals("HEALTH")) {
            printHealthStatus(split);
            return;
        }

        if (split[1].equals("UD")) {
            printLocation(split);
        }

    }

    private void printLocation(String[] split) {
        String[] locations = split[2].split(",");
        String latitude = locations[0].trim();
        String longtitude = locations[1].trim();
        String text = "client: " + IMEI + " is at latitude: " + latitude + " and longtitude: " + longtitude;
        System.out.println(text);
        try {
            writeToFile("logs.txt", text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printHealthStatus(String[] split) {
        String[] healthProperties = split[2].split(",");
        String health = healthProperties[0].trim();
        String lowBloodPressure = healthProperties[1].trim();
        String highBloodPressure = healthProperties[2].trim();
        String text = "client: " + IMEI + " has health: " + health +
                " with low blood pressure: " + lowBloodPressure +
                " and high blood pressure: " + highBloodPressure;
        System.out.println(text);
        try {
            writeToFile("logs.txt", text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeToFile(String fileName, String text) throws IOException {
        if (server.isFileLocked(fileName)) {
            reportFileLocked(fileName);
            return;
        }
        server.lockFile(fileName, this.IMEI);
        tryWriteToFile(fileName, text);
        server.unlockFile(fileName, this.IMEI);
    }

    private void reportFileLocked(String fileName) throws IOException {
        Utils.sendSignal(clientSocket, "ERROR: file " + fileName + " is locked by client: " + IMEI);
    }

    private void tryWriteToFile(String fileName, String text) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            bw.write(text);
            bw.newLine();
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFromFile(String fileName) throws IOException {
        if (server.isFileLocked(fileName)) {
            reportFileLocked(fileName);
            return;
        }
        server.lockFile(fileName, this.IMEI);
        tryReadFromFile(fileName);
        server.unlockFile(fileName, this.IMEI);
    }

    private void tryReadFromFile(String fileName) {
        try(Scanner fileReader = new Scanner(new File(fileName));) {
            StringBuilder data = new StringBuilder();
            while (fileReader.hasNextLine()) {
                data.append(fileReader.nextLine()).append("\n");
            }
            Utils.sendSignal(clientSocket, data.toString());
            sleep(5000);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}