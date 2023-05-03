package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
	private boolean shutdown;
	private Integer timeout;
	private Integer limit;
	static final int fixedSize = 1024;

	public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
		this.shutdown = shutdown;
		this.timeout = timeout;
		this.limit = limit;
	}

	public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
		byte[] readBuffer = new byte[fixedSize];
		Socket tcpSocket = new Socket(hostname, port);
		ByteArrayOutputStream keepBuffer = new ByteArrayOutputStream();

		tcpSocket.getOutputStream().write(toServerBytes);
		if (shutdown == true) tcpSocket.shutdownOutput();
		
		try {
			if(this.timeout != null) tcpSocket.setSoTimeout(timeout);
			if (this.limit != null) {
				int filledBytes = tcpSocket.getInputStream().read(readBuffer);
				while(filledBytes != -1 && (keepBuffer.size() + filledBytes <= limit)) {
					keepBuffer.write(readBuffer, 0, filledBytes);
					filledBytes = tcpSocket.getInputStream().read(readBuffer);
				}
				keepBuffer.write(readBuffer, 0, filledBytes = (filledBytes == -1)? 0 : (this.limit - keepBuffer.size()));
			}
			else {
				int filledBytes = tcpSocket.getInputStream().read(readBuffer);
				while(filledBytes != -1) {
					keepBuffer.write(readBuffer, 0, filledBytes);
					filledBytes = tcpSocket.getInputStream().read(readBuffer);
				}
			}

		}catch(SocketTimeoutException ex) {
			System.out.println("timeout of " + timeout + " miliseconds reached!");
		}

		tcpSocket.close();

		byte[] rtn = keepBuffer.toByteArray();
		return rtn;

	}
}
