import java.net.*;


public class ConcHTTPAsk {
	
	public static void main( String[] args)  {
		try{

			// Create a new server socket listening on the port specified
			ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));

			
		// Continuously accept client connections
		while (true) {
			Socket clientSocket  = serverSocket.accept();
			//new thread to handle the client connection
			new Thread(new ConcHTTP(clientSocket)).start();
	}
}catch(Exception error){
		System.out.println(error);
}
	
	}
}