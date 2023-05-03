package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    public static int BUFFERSIZE = 1024;
    public TCPClient() {}
    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

        // byte array of buffer of unfixed size for server response
        
        ByteArrayOutputStream Receive = new ByteArrayOutputStream();
        Socket clientSocket = new Socket(hostname, port);
        //Sending bytes to server
        clientSocket.getOutputStream().write(toServerBytes);

        // Getting data from server
        int l = 0;
        while((l = clientSocket.getInputStream().read()) != -1){
            Receive.write(l);
        }

        // while((clientSocket.getInputStream().read()) != -1){
        //     l = clientSocket.getInputStream().read();
        //     Receive.write(l);
        // }

        clientSocket.close();
        return Receive.toByteArray();


    }
}
