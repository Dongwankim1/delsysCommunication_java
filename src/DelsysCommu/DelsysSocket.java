package DelsysCommu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

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
	  
	  
	  public void onStart(){
		  int [] sensorList = {0,1,2,3,4,5,6,7};
		  System.out.println("sensor -----------------");
		  typeList = new String[sensorList.length];
	        for(int j=0; j<sensorList.length; j++){
	        	typeList[j]="D";
	        }
	        
	      filter = new DelsysFilter(sensorList, typeList);
		  
		  try {
			commSock = new Socket(ip, 50040); // commands
			emgSock = new Socket(ip, 50041); // EMG data
			accSock = new Socket(ip, 50042); // ACC data
			imemgSock = new Socket(ip, 50043); // ACC data
	        PrintWriter writer = new PrintWriter(commSock.getOutputStream());
	        writer.write("ENDIAN BIG\r\nSTART\r\n\r\n"); // request the start of data streaming
	        writer.flush();
	        // Read the reply
	        BufferedReader reader = new BufferedReader(new InputStreamReader(commSock.getInputStream()));
	        System.out.println(reader.readLine());
	        String tmp;
	        while (!(tmp = reader.readLine()).contains("OK"))
	        {

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
	 	for(int k =0;k<sensorList.length;k++) {
	 		
	        running = true;
	        Runnable r = new SocketRunnable(k);
	        Thread t1 = new Thread(r);
	        t1.setPriority(Thread.MIN_PRIORITY); // Prioritize touch handling and screen updates above network communication.
	        t1.start();
	        /*
	        t = new Thread(new Runnable() {
	        	
	        	
	        	
				public void run() {
				
					readBytes(sensor);
				}
			});
			*/
	        //t.start();
	   
	 	}
	  }
	  
		private void readBytes(int sensor_num)
		{
			final int ACC_BYTE_BUFFER = 384 * 4;
			// The number of bytes to read a complete set of data from all 16 sensors.
	    	// The ratio 1728:384 maintains the 27:2 sample ratio and 16:48 channel ratio between EMG and ACC data.
	    	final int EMG_BYTE_BUFFER = 1728 * 4;
	    	System.out.println("Start readBytes "+sensor_num );
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
					//emgSock.getInputStream().read(emgBytes, 0, EMG_BYTE_BUFFER);
					//accSock.getInputStream().read(accBytes, 0, ACC_BYTE_BUFFER);
					imemgSock.getInputStream().read(emgBytes, 0, ACC_BYTE_BUFFER);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
				// Demultiplex, parse the byte array, and add the appropriate samples to the history buffer.
				for (int i = 0; i < EMG_BYTE_BUFFER / 4; i++)
				{
					if (i % 16 == sensor_num)
					{
						//float f = ByteBuffer.wrap(emgBytes, 4 * i, 4).getFloat();
						float f = ByteBuffer.wrap(emgBytes, 4 * i, 4).getFloat();
				
						filter.addEmgSample(sensor_num,f * 1000); // convert V -> mV
					
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
		  private int socketNum = 0;
		  public SocketRunnable(int socket_num) {
			  this.socketNum = socket_num;
			// TODO Auto-generated constructor stub
		}
		  
		@Override
		public void run() {
			
			readBytes(socketNum);
			// TODO Auto-generated method stub
			
		}
		  
	  }
	
	  
}
