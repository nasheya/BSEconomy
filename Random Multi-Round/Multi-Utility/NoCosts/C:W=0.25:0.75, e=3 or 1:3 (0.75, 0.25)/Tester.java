import Jama.*;
import java.util.ArrayList;
import java.io.*;

public class Tester {
	public static void main(String[] args){
		//double[][] arr = {{1,1,0,0},{1,1,0,0},{1,1,0,0},{0,0,1,1},{0,0,1,1}};
		//double[] arr2 = {-1,-1,-1,2};//{3,4,6,1,2,3,-1,6,7,-1};
		//Matrix a = new Matrix(arr);
		//Simulation set = new Simulation(0.25, 0.2, 0.1, 2, 5, 7, 0.25);
		//Simulation2 set2 = new Simulation2(a);
		//Simulation3 set3 = new Simulation3(5,5,2,2,2,2);

		//Simulation newSet = new Simulation(50, 50, 6, 0.5, 0);

		for(int i=1; i<=Integer.parseInt(args[0]); i++){
			try{
				File file = new File("Output/Output"+i+".txt");
				FileOutputStream fis = new FileOutputStream(file);
				PrintStream out = new PrintStream(fis);
				System.setOut(out);
				Simulation newSet = new Simulation(50, 500, 6, 0.75, i);
			} catch(IOException e){
				System.err.println("Error error!");
			}
			
		}
		
	}
}