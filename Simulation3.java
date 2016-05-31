import Jama.*;
import java.util.Random;

public class Simulation3 {
	Random rand = new Random();

	//adjacency matrix
	Matrix a;

	//consumption plan matrix
	Matrix x;

	//seller order matrix
	Matrix y;

	//filling these orders matrix
	Matrix z;

	//cash payments matrix
	Matrix w;

	//vectors of prices willing to give/accept
	ArrayList<Integer> buyerPrices = new ArrayList<Integer>();
	ArrayList<Integer> sellerPrices = new ArrayList<Integer>();

	ArrayList<Agent> buyers = new ArrayList<Agent>();
	ArrayList<Agent> sellers = new ArrayList<Agent>();

	public Simulation3(int buyers, int sellers){
		Matrix a = new Matrix(buyers, sellers);
		Matrix x = new Matrix(buyers, sellers);
		Matrix y = new Matrix(sellers, buyers);
		Matrix z = new Matrix(buyers, sellers);
		Matrix w = new Matrix(buyers, sellers);

		for(int i=0; i<buyers; i++){
			buyers.add(Agent.Party.BUYER, i+1);
			buyerPrices.add(rand.nextDouble());
		}

		for(int i=0; i<sellers; i++){
			sellers.add(Agent.Party.SELLER, i+1);
			sellerPrices.add(rand.nextDouble());
		}

		makeConnections();
		trade();
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