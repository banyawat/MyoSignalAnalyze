package myProject;

import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.StreamEmgType;
import com.thalmic.myo.example.EmgDataCollector;

public class TestFuzzy {
	public static int rules[][] = {{0,0,0,1,0,0,0,1},
									{0,0,1,1,0,0,1,1},
									{0,0,1,1,0,0,0,0},
									{0,0,1,0,0,0,1,1}};
	
	
	public static void main(String[] args)
	{
		String data;
		int sample[], answerGesture;
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
				answerGesture = 0;
				hub.run(1000 / 20);
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
					//windowsCount++;
				}
			}
		} catch (Exception e) {
			System.err.println("Error: ");
			e.printStackTrace();
			System.exit(1);
		}
		} 	
	
	public static double sensorInactive(int data){
		if(data<=32)
			return 1;
		else if(data >96)
			return 0;
		else
			return (data-32)/(-64)+1;
	}
	
	public static double sensorActive(int data){
		if(data<=32)
			return 0;
		else if(data >96)
			return 1;
		else
			return (data-32)/64;
	}
}
	
