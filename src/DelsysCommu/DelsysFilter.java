package DelsysCommu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DelsysFilter {
	private ArrayList<BufferedWriter> bufferWriters = null;
	private ArrayList<Number> sensorHistory0 = null;
	private ArrayList<Number> sensorHistory1 = null;
	private ArrayList<Number> sensorHistory2 = null;
	private ArrayList<Number> sensorHistory3 = null;
	private ArrayList<Number> sensorHistory4 = null;
	private ArrayList<Number> sensorHistory5 = null;
	private ArrayList<Number> sensorHistory6 = null;
	private ArrayList<Number> sensorHistory7 = null;
	private ArrayList<Number> sensorHistory8 = null;
	private ArrayList<Number> sensorHistory9 = null;
	private ArrayList<Number> sensorHistory10 = null;
	private ArrayList<Number> sensorHistory11 = null;
	private ArrayList<Number> sensorHistory12 = null;
	private ArrayList<Number> sensorHistory13 = null;
	private ArrayList<Number> sensorHistory14 = null;
	private ArrayList<Number> sensorHistory15 = null;
	//filtered samples for each emg sensor
	private ArrayList<Number> sensorOut0 = null;
	private ArrayList<Number> sensorOut1 = null;
	private ArrayList<Number> sensorOut2 = null;
	private ArrayList<Number> sensorOut3 = null;
	private ArrayList<Number> sensorOut4 = null;
	private ArrayList<Number> sensorOut5 = null;
	private ArrayList<Number> sensorOut6 = null;
	private ArrayList<Number> sensorOut7 = null;
	private ArrayList<Number> sensorOut8 = null;
	private ArrayList<Number> sensorOut9 = null;
	private ArrayList<Number> sensorOut10 = null;
	private ArrayList<Number> sensorOut11 = null;
	private ArrayList<Number> sensorOut12 = null;
	private ArrayList<Number> sensorOut13 = null;
	private ArrayList<Number> sensorOut14 = null;
	private ArrayList<Number> sensorOut15 = null;
	private int[] sensorList;
	private ArrayList<ArrayList<Number>> sensorInGroup = null;
	private double Fs1 = 2000;
	private double Fs2 = 2000; //default current sample rate

	private long emgSampleIdx = 0;					// Current EMG sample number
	
	
	public DelsysFilter(int[] sensorList, String[] typeList) {
		//test classes
		this.sensorList = sensorList;
		bufferWriters = new ArrayList<BufferedWriter>();
		
		//initialize all 16 arraylists
		sensorHistory0 = new ArrayList<Number>();
		sensorHistory1 = new ArrayList<Number>();
		sensorHistory2 = new ArrayList<Number>();
		sensorHistory3 = new ArrayList<Number>();
		sensorHistory4 = new ArrayList<Number>();
		sensorHistory5 = new ArrayList<Number>();
		sensorHistory6 = new ArrayList<Number>();
		sensorHistory7 = new ArrayList<Number>();
		sensorHistory8 = new ArrayList<Number>();
		sensorHistory9 = new ArrayList<Number>();
		sensorHistory10 = new ArrayList<Number>();
		sensorHistory11 = new ArrayList<Number>();
		sensorHistory12 = new ArrayList<Number>();
		sensorHistory13 = new ArrayList<Number>();
		sensorHistory14 = new ArrayList<Number>();
		sensorHistory15 = new ArrayList<Number>();

		sensorInGroup = new ArrayList<ArrayList<Number>>();		

		sensorOut0 = new ArrayList<Number>();
		sensorOut1 = new ArrayList<Number>();
		sensorOut2 = new ArrayList<Number>();
		sensorOut3 = new ArrayList<Number>();
		sensorOut4 = new ArrayList<Number>();
		sensorOut5 = new ArrayList<Number>();
		sensorOut6 = new ArrayList<Number>();
		sensorOut7 = new ArrayList<Number>();
		sensorOut8 = new ArrayList<Number>();
		sensorOut9 = new ArrayList<Number>();
		sensorOut10 = new ArrayList<Number>();
		sensorOut11 = new ArrayList<Number>();
		sensorOut12 = new ArrayList<Number>();
		sensorOut13 = new ArrayList<Number>();
		sensorOut14 = new ArrayList<Number>();
		sensorOut15 = new ArrayList<Number>();

		compileSensors();			

		for(int ii = 0; ii <sensorList.length; ii++)
		{
			final int temp = sensorList[ii];
			File csv = new File("C:\\Safety\\Dumy\\35\\"+temp+".csv");
			try {
				bufferWriters.add(new BufferedWriter(new FileWriter(csv,true)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(typeList[ii].equals("D"))
			{
				Thread t = new Thread(new Runnable() {
	
					public void run() {				
						filter(temp, Fs1);				
						//filterFFT();
					}
				});
				t.setPriority(Thread.MIN_PRIORITY); // Prioritize touch handling and screen updates above network communication.
				t.start();
			}
			if(typeList[ii].equals("L"))
			{
				Thread t = new Thread(new Runnable() {
	
					public void run() {				
						filter(temp, Fs2);				
						//filterFFT();
					}
				});
				t.setPriority(Thread.MIN_PRIORITY); // Prioritize touch handling and screen updates above network communication.
				t.start();
			}
			
		}
	}	

	public void addEmgSample(int channel, float f) {
		synchronized (this) {
			//each point will have a subset of x points where x is the number of sensors			
			switch (channel) {
			case 0:
				sensorInGroup.get(0).add(f);
				break;
			case 1:
				sensorInGroup.get(1).add(f);
				break;
			case 2:
				sensorInGroup.get(2).add(f);
				break;
			case 3:
				sensorInGroup.get(3).add(f);
				break;
			case 4:
				sensorInGroup.get(4).add(f);
				break;
			case 5:
				sensorInGroup.get(5).add(f);
				break;
			case 6:
				sensorInGroup.get(6).add(f);
				break;
			case 7:
				sensorInGroup.get(7).add(f);
				break;
			case 8:
				sensorInGroup.get(8).add(f);
				break;
			case 9:
				sensorInGroup.get(9).add(f);
				break;
			case 10:
				sensorInGroup.get(10).add(f);
				break;
			case 11:
				sensorInGroup.get(11).add(f);
				break;
			case 12:
				sensorInGroup.get(12).add(f);
				break;
			case 13:
				sensorInGroup.get(13).add(f);
				break;
			case 14:
				sensorInGroup.get(14).add(f);
				break;
			case 15:
				sensorInGroup.get(15).add(f);
				break;
			}
		}
	}
	//adds points to the out buffer
	public void sensorHistoryToOut(Number f, int channel) {
		synchronized (this) {
			String aData = String.valueOf(f);
			try {
				BufferedWriter bw = bufferWriters.get(channel);
				bw.write(aData);
				bw.newLine();
				 if (bw != null) {
					 bw.flush(); // 남아있는 데이터까지 보내 준다
		
	                }
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			switch (channel) {
			case 0:
				//sensorOut0.add(f);
				System.out.println("aaaa"+f+"bbbbb"+channel);
				break;
			case 1:
				sensorOut1.add(f);
				System.out.println("gggg"+f+"ggggg"+channel);
				break;
			case 2:
				sensorOut2.add(f);
				System.out.println("kkkk"+f+"kkkk"+channel);
				break;
			case 3:
				sensorOut3.add(f);
				break;
			case 4:
				sensorOut4.add(f);
				break;
			case 5:
				sensorOut5.add(f);
				break;
			case 6:
				sensorOut6.add(f);
				break;
			case 7:
				sensorOut7.add(f);
				break;
			case 8:
				sensorOut8.add(f);
				break;
			case 9:
				sensorOut9.add(f);
				break;
			case 10:
				sensorOut10.add(f);
				break;
			case 11:
				sensorOut11.add(f);
				break;
			case 12:
				sensorOut12.add(f);
				break;
			case 13:
				sensorOut13.add(f);
				break;
			case 14:
				sensorOut14.add(f);
				break;
			case 15:
				sensorOut15.add(f);
				break;
			}
			*/
		}
	}

	//This method takes all of the sensors in the sensorlist and adds the selected sensors to sensorOut
	public void compileSensors() {
		
		for (int ii = 0; ii < 16; ii++) {
			switch (ii) {
			case 0:
				sensorInGroup.add(sensorHistory0);
				break;
			case 1:
				sensorInGroup.add(sensorHistory1);
				break;
			case 2:
				sensorInGroup.add(sensorHistory2);
				break;
			case 3:
				sensorInGroup.add(sensorHistory3);
				break;
			case 4:
				sensorInGroup.add(sensorHistory4);
				break;
			case 5:
				sensorInGroup.add(sensorHistory5);
				break;
			case 6:
				sensorInGroup.add(sensorHistory6);
				break;
			case 7:
				sensorInGroup.add(sensorHistory7);
				break;
			case 8:
				sensorInGroup.add(sensorHistory8);
				break;
			case 9:
				sensorInGroup.add(sensorHistory9);
				break;
			case 10:
				sensorInGroup.add(sensorHistory10);
				break;
			case 11:
				sensorInGroup.add(sensorHistory11);
				break;
			case 12:
				sensorInGroup.add(sensorHistory12);
				break;
			case 13:
				sensorInGroup.add(sensorHistory13);
				break;
			case 14:
				sensorInGroup.add(sensorHistory14);
				break;
			case 15:
				sensorInGroup.add(sensorHistory15);
				break;
			}
		}
	}

	public void reset() {
		emgSampleIdx = 0;		
	}


	private void filter(int filter_Num, double Fs) {
		System.out.println("filter NUMBER = "+filter_Num);
		final double FS_half = Fs/2;

		//filter each thread seperately
		while (true) {	

			//all these used to be emgHistory
			while (sensorInGroup.get(filter_Num).size() < FS_half / 2) {
				//Thread.yield();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			synchronized (this) {
				
				
				float[] emgSamples = new float[sensorInGroup.get(filter_Num).size()];
				int i = 0;
				for (Number x : sensorInGroup.get(filter_Num)) {
					emgSamples[i] = x.floatValue();
					i++;
				}

				//FloatFFT_1D tmp = new FloatFFT_1D(emgSamples.length);
				//tmp.realForward(emgSamples);

				// zero out frequecies at 50 to 60 Hz.
				for (i = (int) ((emgSamples.length * 45) / FS_half); i <= (emgSamples.length * 65) / FS_half; i++) {
					emgSamples[i] = 0;
				}

				// kill DC offset
				emgSamples[0] = 0;

				// zero out frequencies above 450 Hz
				for (i = (int) ((emgSamples.length * 450) / FS_half); i < emgSamples.length; i++) {
					emgSamples[i] = 0;
				}

				// zero out frequencies below 20 Hz
				for (i = (int) ((emgSamples.length * 20) / FS_half); i >= 0; i--) {
					emgSamples[i] = 0;
				}

				//tmp.realInverse(emgSamples, true);

				// one twelfth-window overlap
				for (i = 0; i < FS_half / 12; i++) {
					sensorInGroup.get(filter_Num).remove(0);
				}

				// downsample by a factor of 2, compute rms
				boolean toggle = false;
				double sumSq = 0;
				for (float x : emgSamples) {
					if (toggle) {
						sumSq += x * x; 
					}
					toggle = !toggle;
				}

				//send filtered samples out
				sensorHistoryToOut((Math.sqrt(sumSq / emgSamples.length / 2)), filter_Num);			

			}
		}
	}
	
	public void setFs(double Fs1, double Fs2)
	{
		this.Fs1 = Fs1;
		this.Fs2= Fs2;
	}

	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////

	//Arrays hold emg sample history for each sensor

	
}
