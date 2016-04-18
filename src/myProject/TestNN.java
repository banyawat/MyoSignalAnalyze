package myProject;

import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.StreamEmgType;
import com.thalmic.myo.example.EmgDataCollector;

public class TestNN {
	static final int INPUT_NUM = 8;
	static final int HIDDEN_NUM = 9;
	static final int OUTPUT_NUM = 4;
	static final double INIT_WEIGHT = .5;
	static final double LEARNING_RATE = .5;
	public static double[][] weightH = new double[HIDDEN_NUM][INPUT_NUM];
	public static double[][] weightO = new double[OUTPUT_NUM][HIDDEN_NUM];
	
	
	public static void main(String[] args)
	{
		String data;
		int sample;
		double input[];
		String sampleStr[];
		
		double net, totalError, totalErrorHidden;
		double[] outH = new double[HIDDEN_NUM];
		double[] outO = new double[OUTPUT_NUM];
		double[] targetO = {1, 0.1, 0.1, 0.1};
		double biasL1 = .35, biasL2 = .2;
		
		//Assign initial weight to each layers
		for(int i=0;i<HIDDEN_NUM;i++){
			for(int j=0;j<INPUT_NUM;j++){
				weightH[i][j] = INIT_WEIGHT;
			}
		}
		
		for(int i=0;i<OUTPUT_NUM;i++){
			for(int j=0;j<HIDDEN_NUM;j++){
				weightO[i][j] = INIT_WEIGHT;
			}
		}
		
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
				data = dataCollector.toString();
				if(data!="null"){
					data = data.replaceAll("\\[|\\]|\\s", "");
					sampleStr = data.split(",");
					input = new double[sampleStr.length];
					for(int i=0;i<sampleStr.length;i++){
						sample = Integer.parseInt(sampleStr[i]); //get sample data from each nodes
						if(sample<0)
							sample*=-1;
						//System.out.print(i+": "+sample[i]+"\t");
						input[i]=(double)sample/128; //map number range as 0-1 from 0-128
						//System.out.println("Input: "+input[i]);
					}
					//do something - Forward Pass
					for(int i=0;i<HIDDEN_NUM;i++){
						net = 0;
						for(int j=0;j<INPUT_NUM;j++){
							net += weightH[i][j]*input[j];
						}
						net += biasL1;
						outH[i] = 1/(1+Math.exp(-net));
						//System.out.println("outH["+i+"]: "+outH[i]);
					}
					System.out.println();
					
					for(int i=0;i<OUTPUT_NUM;i++){
						net = 0;
						for(int j=0;j<HIDDEN_NUM;j++){
							net += weightO[i][j]*outH[j];
						}
						net+=biasL2;
						outO[i] = 1/(1+Math.exp(-net));
						//System.out.println("outO["+i+"]: "+outO[i]);
					}
					
					//Calculating total error
					totalError = 0;
					for(int i=0;i<OUTPUT_NUM;i++){
						totalError += (0.5)*Math.pow((targetO[i]-outO[i]),2);
					}
					
					// NOW BACKWARD PASS START WITH OUTPUT LAYER
					for(int i=0;i<OUTPUT_NUM;i++){
						for(int j=0;j<HIDDEN_NUM;j++){
							weightO[i][j] = weightO[i][j]-LEARNING_RATE*((outO[i]-targetO[i])*(outO[i]*(1-outO[i])*outH[i]));
							System.out.println("Output Weight[H"+j+"][O"+i+"]: "+weightO[i][j]);
						} //adjusting weight for output weight
						System.out.println();
					}
					
					System.out.println(" ****** HIDDEN LAYER ******");
					//START ADJUST HIDDEN LAYER
					for(int i=0;i<HIDDEN_NUM;i++){
						totalErrorHidden = 0;
						for(int k=0;k<OUTPUT_NUM;k++){
							totalErrorHidden += (outO[k]-targetO[k])*outO[k]*(1-outO[k])*weightO[k][i];
						}
						for(int j=0;j<INPUT_NUM;j++){
							weightH[i][j] = weightH[i][j]-LEARNING_RATE*(outH[i]*(1-outH[i])*input[j]);
							System.out.println("Hidden Weight[I"+j+"][H"+i+"]: "+weightH[i][j]);
						}
						System.out.println();
					} //adjusting weight for each hidden layer weight
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
