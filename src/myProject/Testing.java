package myProject;

import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.StreamEmgType;
import com.thalmic.myo.example.EmgDataCollector;

public class Testing {
	static final int WINDOWS=20;
	static final int NODE_NUM=8;
	static final int THRESHOLD_VAL=25;
	public static int analyzeData[][] = new int[NODE_NUM][WINDOWS];
	public static int maximum[] = new int[NODE_NUM];
	
	public static void main(String[] args)
	{
		String data;
		int sample[], windowsCount=0;
		String sampleStr[];
		
		try {
			Hub hub = new Hub("");
			System.out.println("Attempting to find a Myo...");
			Myo myo = hub.waitForMyo(10000);

			if (myo == null) {
				throw new RuntimeException("Unable to find a Myo!");
			}

			System.out.println("Connected to a Myo armband!");
			myo.setStreamEmg(StreamEmgType.STREAM_EMG_ENABLED);
			DeviceListener dataCollector = new EmgDataCollector();
			hub.addListener(dataCollector);

			while (true) {
				if(windowsCount==WINDOWS){ //find peak for each of node, Collect data 10 times 
					System.out.println();
					for(int i=0;i<NODE_NUM;i++){ //
						maximum[i] = findPeak(analyzeData[i]);
						System.out.print(i+": "+maximum[i]+"\t");
					}
					System.out.println("***************MAXIMUM**************");
					
					checkGesture(maximum);	//Check user gesture
					windowsCount=0;
				}
				hub.run(1000 / 20);
				//System.out.println(dataCollector); //orginal print stub string
				data = dataCollector.toString();
				if(data!="null"){
					data = data.replaceAll("\\[|\\]|\\s", "");
					sampleStr = data.split(",");
					sample = new int[sampleStr.length];
					for(int i=0;i<sampleStr.length;i++){
						sample[i] = Integer.parseInt(sampleStr[i]); //get sample data from each nodes
						if(sample[i]<0)
							sample[i]*=-1;
						analyzeData[i][windowsCount] = sample[i];
						//System.out.print(i+": "+sample[i]+"\t");
					}
					//System.out.println();
					windowsCount++;
				}
			}
		} catch (Exception e) {
			System.err.println("Error: ");
			e.printStackTrace();
			System.exit(1);
		}
		} 
	
	/** 
	 * find maximum values of signal in range 
	 * @param data samples
	 * @return peak of samples
	 */
	private static int findPeak(int dataSamp[])
	{
		int max = dataSamp[0];
		for(int i=1;i<dataSamp.length;i++)
			if(dataSamp[i]>max)
				max=dataSamp[i];
		return max;
	}
	
	/**
	 * check which gesture is right for you 
	 *
	 ** @param node
	 */
	static void checkGesture(int node[]){
		if(node[2]>THRESHOLD_VAL&&node[7]>THRESHOLD_VAL){	//signal is rather similar so..
			if(node[3]>THRESHOLD_VAL&&node[6]>THRESHOLD_VAL)
				System.out.println("Middle Finger");
			else
				System.out.println("Baby Finger");
		}
		else if(node[2]>THRESHOLD_VAL&&node[3]>THRESHOLD_VAL)
			System.out.println("Ring Finger");
		else if(node[3]>THRESHOLD_VAL&&node[7]>THRESHOLD_VAL)
			System.out.println("Index Finger");
		else
			System.out.println("NONE");
	}
}
