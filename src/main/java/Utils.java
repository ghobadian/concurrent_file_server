import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Utils {
    public static void sendSignal(Socket socket, String signal) throws IOException {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(signal);
        } catch (Exception e) {
            throw e;
        }
    }

    public static String receiveSignal(Socket socket) throws IOException {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            return in.readUTF();
        } catch (Exception e) {
            throw e;
        }
    }
}
