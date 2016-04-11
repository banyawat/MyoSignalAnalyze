package myProject;

import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.StreamEmgType;
import com.thalmic.myo.example.EmgDataCollector;

public class TestNN {
	static final int WINDOWS=20;
	static final int NODE_NUM=8;
	static final int THRESHOLD_VAL=25;
	public static int analyzeData[][] = new int[NODE_NUM][WINDOWS];
	public static int maximum[] = new int[NODE_NUM];
	
	
	public static void main(String[] args)
	{
		String data;
		int sample[];
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
						//System.out.print(i+": "+sample[i]+"\t");
					}
					System.out.println();
				}
			}
		} catch (Exception e) {
			System.err.println("Error: ");
			e.printStackTrace();
			System.exit(1);
		}
		} 
}
