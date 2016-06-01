import Jama.*;
import java.util.Random;
import java.util.ArrayList;

public class Simulation3 {
	static Random rand = new Random();

	//adjacency matrix
	static Matrix a;

	//consumption plan matrix
	static Matrix x;

	//seller order matrix
	static Matrix y;

	//filling these orders matrix
	static Matrix z;

	//cash payments matrix
	static Matrix w;

	//vectors of prices willing to give/accept
	static ArrayList<Double> buyerPrices = new ArrayList<Double>();
	static ArrayList<Double> sellerPrices = new ArrayList<Double>();

	static ArrayList<Agent> buyers = new ArrayList<Agent>();
	static ArrayList<Agent> sellers = new ArrayList<Agent>();

	static double costCreate;
	static double costDissolve;
	static double costMaintain;

	public Simulation3(int buyersNum, int sellersNum, double c, double d, double m, int maxRounds){
		costCreate = c;
		costDissolve = d;
		costMaintain = m;

		Matrix a = new Matrix(buyersNum, sellersNum);
		Matrix x = new Matrix(buyersNum, sellersNum);
		Matrix y = new Matrix(sellersNum, buyersNum);
		Matrix z = new Matrix(buyersNum, sellersNum);
		Matrix w = new Matrix(buyersNum, sellersNum);

		for(int i=0; i<buyersNum; i++){
			buyers.add(new Agent(Agent.Party.BUYER, i+1));
			buyerPrices.add(rand.nextDouble());
		}

		for(int i=0; i<sellersNum; i++){
			sellers.add(new Agent(Agent.Party.SELLER, i+1));
			sellerPrices.add(rand.nextDouble());
		}

		for(int i=0; i<maxRounds; i++){
			makeConnections();
			trade();
		}
	}
	
	public static void makeConnections(){

	}

	public static void trade(){
		for(int i=0; i<a.getRowDimension(); i++){
			for(int j=0; j<a.getColumnDimension(); j++){
				if(a.get(i,j)==1){
					x.set(i,j,buyers.get(i).numConnections);
				}
			}
		}
	}

}