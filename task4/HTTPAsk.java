import java.net.*;
import java.io.*;
import tcpclient.TCPClient;


public class HTTPAsk {
    public static void main( String[] args) throws IOException {
        // get the port number from command line arguments
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        // create a server socket on the given port

        while(true){
			ByteArrayOutputStream keepBuffer = new ByteArrayOutputStream();
	        Socket clientSocket = serverSocket.accept();
            // wait for a client to connect
			InputStream input = clientSocket.getInputStream();
			OutputStream output = clientSocket.getOutputStream();

			// create output stream for server response


			int b = 0;
			while((b = input.read()) != -1) { 
				keepBuffer.write(b);
				if (b =='\n') break;
			}

		
				
				// Request to exact parameters
				byte[] Btest = new byte[0];
				String[] pArr = keepBuffer.toString().split(" |\\?|=|&");

				String hostname = null;
				boolean shutdown = false;
				Integer timeout = null, limit = null;
				
				boolean asktest = false;
				boolean Gettest = false;
				
				int portNum = 0;
				
				for (int i = 0; i < pArr.length; i++) {
					//starts a loop to go through each parameter in the HTTP request.
				
					if (pArr[i].equals("hostname")) {
						hostname = pArr[i + 1];
					} else if (pArr[i].equals("port")) {
						portNum = Integer.parseInt(pArr[i + 1]);
					}
					
        				else if(pArr[i].equals("string")){
							Btest = (pArr[i+1] + "\n" ).getBytes();
        				}

						else if (pArr[i].equals("GET")) Gettest = true;

						else if (pArr[i].equals("/ask")) asktest = true;

						else if (pArr[i].equals("shutdown")) {
							shutdown = Boolean.parseBoolean(pArr[i+1]);
							System.out.print(" shutdown");
						}
						
						else if (pArr[i].equals("timeout")) {
							timeout = Integer.parseInt(pArr[i+1]);
							System.out.print(" timeout:" + timeout);
						}
        			}
									
					
					if(asktest == false) {
						byte[] errorMessage = "HTTP/1.1 404 Not Found\r\n\r\n".getBytes();
						output.write(errorMessage);
					}
					else if (hostname == null || portNum == 0 || Gettest == false) {
						byte[] errorMessage = "HTTP/1.1 400 Bad Request\r\n\r\n".getBytes();
						output.write(errorMessage);

					}
					else {
        			try{	
						TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);

						byte[] Trial = tcpClient.askServer(hostname,portNum,Btest);
						
						// connects to tcp server with info from request

						String stest = new String(Trial);


						String header  = "HTTP/1.1 200 OK\r\n\r\n";
						//successful connection

		        		output.write((header + stest +"\r\n").getBytes());
						System.out.println(stest + "\n");

	        		}
	        		catch( Exception e){
						System.err.println(e);
	        		}

					clientSocket.close();
					System.out.println("Closed");
					//closes connections
	        	}

        	}
        }
	}