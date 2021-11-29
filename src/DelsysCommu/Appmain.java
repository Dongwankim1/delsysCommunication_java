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
			
	
			//String filepath = args[0];
			//String optrList = args[1];
			String filepath = "C:\\Safety\\Dumy\\47";
			String optrList = "0-aaaa,1-aaaa,2-aaaa,3-aaaa,4-aaaa,5-aaaa,6-aaaa,7-aaaa";
			DelsysSocket socket = new DelsysSocket();
			socket.onStart(filepath,optrList);
			
	}
	
	
}
