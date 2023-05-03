import java.net.*;
import java.io.*;
import java.nio.charset.*;
import tcpclient.TCPClient;

public class HTTPAsk {

	public static void main( String[] args) throws IOException {
		ServerSocket incoming = new ServerSocket(Integer.parseInt(args[0]));
		while (true) {
			ByteArrayOutputStream keepBuffer = new ByteArrayOutputStream();
			Socket transmission = incoming.accept();
			InputStream rcev = transmission.getInputStream();
			OutputStream send = transmission.getOutputStream();

			boolean shutdown = false;
			Integer timeout = null;
			Integer limit = null;
			String hostname = null;
			int port = 0;
			byte[] optional = new byte[0];
			boolean resource = false;
			boolean onlyGet = false;


			int b = 0;
			while((b = rcev.read()) != -1) { 
				keepBuffer.write(b);
				if (b =='\n') break;
			}

			System.out.println(keepBuffer.toString());
			System.out.print("Arguments:");
			String[] splitRequest = keepBuffer.toString().split(" |\\?|=|&");
			for (int i = 0; i < splitRequest.length; i++) {
				String e = splitRequest[i];
				if (e.equals("hostname")) {
					hostname = splitRequest[i+1];
					System.out.print(" " + hostname);
				}
				else if (e.equals("port")) {
					port = Integer.parseInt(splitRequest[i+1]);
					System.out.print(" " + port);
				}
				else if (e.equals("/ask")) resource = true;
				else if (e.equals("GET")) onlyGet = true;
				else if (e.equals("shutdown")) {
					shutdown = Boolean.parseBoolean(splitRequest[i+1]);
					System.out.print(" shutdown");
				}
				else if (e.equals("timeout")) {
					timeout = Integer.parseInt(splitRequest[i+1]);
					System.out.print(" timeout:" + timeout);
				}
				else if (e.equals("string")) {
					optional = (splitRequest[i+1] + "\n").getBytes();
					System.out.print(" String:" + splitRequest[i+1]);
				}
			}
			System.out.println();

			if(resource == false) {
				byte[] errorMessage = "HTTP/1.1 404 Not Found\r\n\r\n".getBytes();
				send.write(errorMessage);
			}
			else if (hostname == null || port == 0 || onlyGet == false) {
				byte[] errorMessage = "HTTP/1.1 400 Bad Request\r\n\r\n".getBytes();
				send.write(errorMessage);
			}
			else {
				try {
					TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
					byte[] body  = tcpClient.askServer(hostname, port, optional);
					String bodyString = new String(body);
					String successMessage = "HTTP/1.1 200 OK\r\n\r\n";
					send.write((successMessage + bodyString + "\n").getBytes());
					System.out.println(bodyString + "\n");
				} catch(IOException ex) {
					System.err.println(ex);
				}

			}

			//incoming.close();
			transmission.close();

		}
	}

}
