package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    public static int BUFFERSIZE = 1024;
    private boolean shutdown = false;
    private Integer timeout = null;
    //no upper limit for how long the client should wait, timeout is null
    private Integer limit = null;
    // no upper limit for how much data the client should receive, limit is null.

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {

        this.shutdown = shutdown;
        this.limit = limit;
        this.timeout = timeout;
    }

    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {
        // byte array of buffer of unfixed size for server response
        byte[] buffer = new byte[BUFFERSIZE];

        ByteArrayOutputStream Receive = new ByteArrayOutputStream();
        Socket clientSocket = new Socket(hostname, port);
        //Sending bytes to server
        clientSocket.getOutputStream().write(toServerBytes);

        try {
			if(this.timeout != null) clientSocket.setSoTimeout(timeout);
			if (this.limit != null) {
				int l = clientSocket.getInputStream().read(buffer);
				while(l != -1 && (Receive.size() + l <= limit)) {
					Receive.write(buffer, 0, l);
					l = clientSocket.getInputStream().read(buffer);
				}
				Receive.write(buffer, 0, l = (l == -1)? 0 : (this.limit - Receive.size()));
			}
			else {
				int l = clientSocket.getInputStream().read(buffer);
				while(l != -1) {
					Receive.write(buffer, 0, l);
					l = clientSocket.getInputStream().read(buffer);
				}
			}
        }catch(SocketTimeoutException ex) {
			System.out.println("timeout of " + timeout + " miliseconds reached!");
		}
            clientSocket.close();

            return Receive.toByteArray();
        }
    }
