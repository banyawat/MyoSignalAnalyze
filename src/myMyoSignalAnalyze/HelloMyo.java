package myMyoSignalAnalyze;

import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.example.DataCollector;

/*
 * JVM Arguments to help debug.
 -Xcheck:jni
 -XX:+ShowMessageBoxOnError
 -XX:+UseOSErrorReporting
 */
public class HelloMyo {
	public static void main(String[] args) {
		try {
			Hub hub = new Hub("com.example.hello-myo");

			System.out.println("Attempting to find a Myo...");
			Myo myo = hub.waitForMyo(10000);

			if (myo == null) {
				throw new RuntimeException("Unable to find a Myo!");
			}

			System.out.println("Connected to a Myo armband!");
			DeviceListener dataCollector = new DataCollector();
			PrintMyoEvents pMyo = new PrintMyoEvents();
			hub.addListener(pMyo);
			//hub.addListener(dataCollector);

			/*hub.addListener( new PrintMyoEvents(){
				
			});*/
			
			while (true) {
				hub.run(1000 / 20);
				//pMyo.onPose(myo, timestamp, pose);
				//System.out.print(dataCollector);
			}
		} catch (Exception e) {
			System.err.println("Error: ");
			e.printStackTrace();
			System.exit(1);
		}
	}
}