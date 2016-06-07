import Jama.*;
import java.util.ArrayList;

public class Simulation4{
	//represents all the connections between the buyers and sellers
	Matrix a;

	//represents the amount they are trading
	Matrix x;

	static ArrayList<Agent> buyers = new ArrayList<Agent>();
	static ArrayList<Agent> sellers = new ArrayList<Agent>();

	static double costCreate;
	static double costDissolve;

	public Simulation4(double c, double d, int numBuyers, int numSellers){
		costCreate = c;
		costDissolve = d;

		for(int i=0; i<numBuyers; i++){
			buyers.add(new Agent(Agent.Party.BUYER, i+1));
		}

		for(int i=0; i<numSellers; i++){
			sellers.add(new Agent(Agent.Party.SELLER, i+1));
		}

		a = new Matrix(numBuyers, numSellers);
		x = new Matrix(numBuyers + numSellers, numBuyers + numSellers);

		run();
	}


	public static void run(){
		//ArrayList<Agent> buyersTemp = clone(buyers);

		//Agent temp = 
	}


	private static ArrayList clone(ArrayList<Agent> obj){
		ArrayList<Agent> temp = new ArrayList<Agent>();
		
		for(int i=0; i<obj.size(); i++){
			temp.add(obj.get(i));
		}

		return temp;
	}

}