import java.util.ArrayList;
import java.io.*;

public class Tester {
	public static void main(String[] args){
		// PrintStream original    = System.out;

		// PrintStream dummyStream    = new PrintStream(new OutputStream(){
		//     public void write(int b) {
		//         //NO-OP
		//     }
		// });
		// System.setOut(dummyStream);

		for(int i=1; i<=50; i++){
			Simulation newSet = new Simulation(1000, 500, 0.75, i);

			// System.setOut(original);
			System.out.println(i+" simulations done!");
			// System.setOut(dummyStream);

			// try{
			// 	File file = new File("Output/Output"+i+".txt");
			// 	FileOutputStream fis = new FileOutputStream(file);
			// 	PrintStream out = new PrintStream(fis);
			// 	System.setOut(out);
			// 	Simulation newSet = new Simulation(1000, 500, 0.7, i);
			// } catch(IOException e){
			// 	System.err.println("Error error! Cannot find output folder.");
			// }
			
		}
		
	}
}