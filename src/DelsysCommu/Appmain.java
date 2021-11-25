package DelsysCommu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Appmain {
		

	public static void main(String[] args) {
			
	
			String filepath = args[0];
			String optrList = args[1];
			DelsysSocket socket = new DelsysSocket();
			socket.onStart(filepath,optrList);
			
	}
	
	
}
