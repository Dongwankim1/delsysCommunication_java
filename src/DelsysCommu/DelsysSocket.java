package DelsysCommu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class DelsysSocket {
	  private DelsysFilter filter;
	  private Socket commSock = null;
	  private Socket emgSock = null;
	  private Socket accSock = null;
	  private Socket imemgSock = null;
	  private String ip = "localhost";
	  private boolean running;

	  private Thread t;
	  private int sensor = 0;
	  private String[] typeList;

	
	  public DelsysSocket() {
		// TODO Auto-generated constructor stub

	}
	  
	  
	  public void onStart(String filepath,String optrList){
		  String[] optrListArray = optrList.split(",");
		  
		  int [] sensorList = new int[optrListArray.length];
		  for(int i =0;i<sensorList.length;i++) {
			  sensorList[i] = i;
		  }
		  typeList = new String[sensorList.length];
	        for(int j=0; j<sensorList.length; j++){
	        	typeList[j]="D";
	        }
	        
	      filter = new DelsysFilter(sensorList, typeList,filepath,optrListArray);
		  
		  try {
			commSock = new Socket(ip, 50040); // commands
			emgSock = new Socket(ip, 50041); // EMG data
			accSock = new Socket(ip, 50042); // ACC data
			imemgSock = new Socket(ip, 50043); // ACC data
	        PrintWriter writer = new PrintWriter(commSock.getOutputStream());
	       
	        // Read the reply
	        BufferedReader reader = new BufferedReader(new InputStreamReader(commSock.getInputStream()));
	       
	        String tmp;
	        String command = "";

	        
	        writer.write("ENDIAN BIG\r\nSTART\r\n\r\n"); // request the start of data streaming
	        writer.flush();
	        
	        while (!(tmp = reader.readLine()).contains("OK"))
	        {
	        	System.out.println("dddd--"+tmp);
	        	if (tmp.contains("CANNOT COMPLETE"))
	        	{
	        		// Retry, the last activity may not have fully stopped 
	        		try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    	        writer.write("ENDIAN BIG\r\nSTART\r\n\r\n"); // request the start of data streaming
	    	        writer.flush();
	        	}
	        	while (!reader.ready())
	        		;
	        }
	        
	        /*
	        writer.write("SENSOR 1 TYPE?\r\n\r\n"); // request the start of data streaming
			writer.flush();
		    
		      
		        try {
					while (true)
					{	
					
						System.out.println(reader.readLine());
						while (!reader.ready())
			        		;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			*/
	        //sendCommandWithResponse(writer,reader,command);
	        //System.out.println(command);
	        //command = "SENSOR 1 SERIAL?\r\n\r\n";
	        //System.out.println(command);
	        //sendCommandWithResponse(writer,reader,command);
	        
	 
	        
	        
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			// Notify the user of an error.
			
			return;
		} catch (IOException e1) {
			e1.printStackTrace();
			// Notify the user of an error.
	
			return;
		}
        
		// Launch a thread to read data from the TCP sockets.
		  /*
	 	for(int k =0;k<sensorList.length;k++) {
	 		
	        running = true;
	        Runnable r = new SocketRunnable(k);
	        Thread t1 = new Thread(r);
	        t1.setPriority(Thread.MIN_PRIORITY); // Prioritize touch handling and screen updates above network communication.
	        t1.start();
	      
	   
	 	}
	 	*/
	 	

		  running = true;
	        Runnable r = new SocketRunnable(sensorList);
	        Thread t1 = new Thread(r);
	        t1.setPriority(Thread.MIN_PRIORITY); // Prioritize touch handling and screen updates above network communication.
	        t1.start();
	        
	  }
	  
		private void readBytes(int[] sensorList)
		{
			final int ACC_BYTE_BUFFER = 384 * 4;
			// The number of bytes to read a complete set of data from all 16 sensors.
	    	// The ratio 1728:384 maintains the 27:2 sample ratio and 16:48 channel ratio between EMG and ACC data.
	    	final int EMG_BYTE_BUFFER = 1728 * 4;
	    	System.out.println("Start readBytes "+sensorList );
	    	// Allocate space to store data incoming from the network.
			byte[] emgBytes = new byte[EMG_BYTE_BUFFER];
			byte[] accBytes = new byte[ACC_BYTE_BUFFER];
			byte[] imemgBytes = new byte[EMG_BYTE_BUFFER];
			while(running)
			{

				
				try {
					// Wait until a complete set of data is ready on the socket.  The 
					while (running && 
						   (emgSock.getInputStream().available() < EMG_BYTE_BUFFER ||
						   accSock.getInputStream().available() < ACC_BYTE_BUFFER))
					{
						Thread.sleep(50);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			
				try {
					// Read a complete group of multiplexed samples
					emgSock.getInputStream().read(emgBytes, 0, EMG_BYTE_BUFFER);
					//accSock.getInputStream().read(accBytes, 0, ACC_BYTE_BUFFER);
					//imemgSock.getInputStream().read(emgBytes, 0, ACC_BYTE_BUFFER);
				} catch (IOException e) {
					e.printStackTrace();
				}
		
				// Demultiplex, parse the byte array, and add the appropriate samples to the history buffer.
				for (int i = 0; i < EMG_BYTE_BUFFER / 4; i++)
				{
					for(int sensor_num=0;sensor_num<sensorList.length;sensor_num++) {
					if (i % 16 == sensor_num)
					{
						
						
						//float f = ByteBuffer.wrap(emgBytes, 4 * i, 4).getFloat();
						float f = ByteBuffer.wrap(emgBytes, 4 * i, 4).getFloat();

						//filter.addEmgSample(sensor_num,f * 1000); // convert V -> mV
						filter.sensorHistoryToOut(f * 1000,sensor_num); // convert V -> mV
					}
					}
				}
				
			/*	for (int i = 0; i < ACC_BYTE_BUFFER / 4; i++)
				{
					if (i % 48 == sensor * 3)
					{
						float f = ByteBuffer.wrap(accBytes, 4 * i, 4).getFloat();

						filter.addAccSample(1, f);
					}
					if (i % 48 == sensor * 3 + 1)
					{
						float f = ByteBuffer.wrap(accBytes, 4 * i, 4).getFloat();

						filter.addAccSample(2, f);
					}
					if (i % 48 == sensor * 3 + 2)
					{
						float f = ByteBuffer.wrap(accBytes, 4 * i, 4).getFloat();

						filter.addAccSample(3, f);
					}
				}*/
				
				
			
			}	
		}
	  
	  class SocketRunnable implements Runnable{
		  private int [] sensorList ;
		  public SocketRunnable(int [] sensorList) {
			  this.sensorList = sensorList;
			// TODO Auto-generated constructor stub
		}
		  
		@Override
		public void run() {
			
			readBytes(sensorList);
			// TODO Auto-generated method stub
			
		}
		  
	  }
	  
	  private void sendCommandWithResponse(PrintWriter writer, BufferedReader reader,String command) {
		  String tmp = "";
		  System.out.println(command);
		  writer.write(command); // request the start of data streaming
		  writer.flush();
	        
	        try {
				while (!(tmp = reader.readLine()).contains("OK"))
				{	
					System.out.println("-----------------");
					System.out.println(tmp);
					while (!reader.ready())
		        		;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }
	
	  
}
