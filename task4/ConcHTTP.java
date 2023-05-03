import java.net.*;
import java.io.*;
import tcpclient.TCPClient;


public class ConcHTTP implements Runnable {

	private Socket socket;

	public ConcHTTP(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			InputStream input = socket.getInputStream();
			OutputStream output = socket.getOutputStream();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			//storage from input stream

			int b;
			while ((b = input.read()) != -1) {
				buffer.write(b);
				if (b == '\n') {
					//each read will be written to buffer
					break;
				}
			}

			String request = buffer.toString();
            byte[] response = null;
			String[] params = request.split("[ \\?=&]");
            byte[] Btest = new byte[0];
            String forlat = "";
			String hostname = null;
			boolean shutdown = false;
			Integer timeout = null;
			Integer portNum = null;
            Integer limit = null;
			boolean asktest = false;
			boolean Gettest = false;
            boolean httptest = false;

			if (params[params.length - 1].equals("HTTP/1.1\r\n")) httptest = true;
			//final elemnt is "HTTP/1.1\r\n"

            if (params[0].equals("GET")) Gettest = true;
			//first element is "GET"
			if (params[1].equals("/ask")) asktest = true;
			//second element of is equal to the string "/ask"

			for (int i = 0; i < params.length; i++) {
				//loops through all itesm in param array
				if (params[i].equals("hostname")) {
					hostname = params[i + 1];
				} else if (params[i].equals("port")) {
					portNum = Integer.parseInt(params[i + 1]);
					
				}else if (params[i].equals("string")) {
                    Btest = (params[i+1] + "\n" ).getBytes();

				}else if (params[i].equals("shutdown")) {
					shutdown = Boolean.parseBoolean(params[i + 1]);

				} else if (params[i].equals("timeout")) {
					timeout = Integer.parseInt(params[i + 1]);

				} else if (params[i].equals("limit")) {
                    limit = Integer.parseInt(params[i+1]);

				}else if(params[i].contains("HTTP")||params[i].contains("http"))
				// if contains http
				httptest = true;
				
			}

			if (!asktest) {
                // when false
                output.write("HTTP/1.1 404 Not found\r\n\r\n".getBytes());
                
			} else if (hostname == null || portNum == null || !Gettest|| !httptest) {
				//if the above parameters are missing, return 400 bad request
				output.write("HTTP/1.1 400 Bad request\r\n\r\n".getBytes());

			} else {
                try {
                    TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
				    response = tcpClient.askServer(hostname, portNum, Btest);
                } catch(IOException ex) {
                    System.err.println(ex);
                }

                if (response != null) forlat = new String(response); 
				output.write(("HTTP/1.1 200 OK\r\n\r\n" + forlat+ "\n").getBytes());
				output.write(response);

        }
			socket.close();


		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main( String[] args) throws IOException {
        
    }
}