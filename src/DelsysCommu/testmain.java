package DelsysCommu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class testmain {
		
	

	 	
	public static void main(String[] args) {
			
			String sensorCount = args[0];
			String filepath = args[1];
			DelsysSocket socket = new DelsysSocket();
			socket.onStart();
	
	}
	
	
}