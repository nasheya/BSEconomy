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

	//These are probability of how much the seller would be willing to lower their price.
	//It is based on a normal distribution.
	static ArrayList<Double> sellerNeg = new ArrayList<Double>();

	//list of buyers and sellers
	static ArrayList<Agent> buyers = new ArrayList<Agent>();
	static ArrayList<Agent> sellers = new ArrayList<Agent>();

	//costs of the edges
	static double costCreate;
	static double costDissolve;
	static double costMaintain;

	//the number of "turns" a player gets each round, aka the number of connections 
	//they're allowed to disconnect and connect, total
	static int turns = 2;

	public Simulation3(int buyersNum, int sellersNum, double c, double d, double m, int maxRounds){
		costCreate = c;
		costDissolve = d;
		costMaintain = m;

		Matrix a = new Matrix(buyersNum, sellersNum);
		Matrix x = new Matrix(buyersNum, sellersNum);
		Matrix y = new Matrix(buyersNum, sellersNum);
		Matrix z = new Matrix(buyersNum, sellersNum);
		Matrix w = new Matrix(buyersNum, sellersNum);

		for(int i=0; i<buyersNum; i++){
			buyers.add(new Agent(Agent.Party.BUYER, i+1));
			buyerPrices.add(rand.nextDouble());
		}

		for(int i=0; i<sellersNum; i++){
			sellers.add(new Agent(Agent.Party.SELLER, i+1));
			sellerPrices.add(rand.nextDouble());
			//sellerNeg.add(Math.abs(fRandom.nextGaussian()));
		}

		for(int i=0; i<maxRounds; i++){
			makeConnections();
			trade();
		}
	}
	
	public static void makeConnections(){
		Matrix benefitMatrix = new Matrix(buyers.size() + sellers.size(), buyers.size() + sellers.size());

		ArrayList<Agent> total = new ArrayList<Agent>();
		total.addAll(buyers);
		//total.addAll(sellers);

		java.util.Collections.shuffle(total);

		for(int i=0; i<total.size(); i++){
			Agent temp = total.get(i);
			double[] net;

			if(temp.myType == Agent.Party.BUYER){
				net = new double[sellers.size()];

				for(int j=0; j<sellers.size(); j++){
					if(benefitMatrix.get(i,buyers.size()+j) != 0){
						net[j] = benefitMatrix.get(i,buyers.size()+j);
					} else {
						if(a.get(i,j) == 1){
							net[j] = connect(temp.myID, sellers.get(j).myID, Agent.Party.BUYER);
						} else {
							net[j] = disconnect(temp.myID, sellers.get(j).myID, Agent.Party.BUYER);
						}
					}	
				}

				updateBenefitMatrix(temp, benefitMatrix, findMaximums(net), net);
			} else {
				net = new double[buyers.size()];
			}
		}

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

	private static void updateBenefitMatrix(Agent player, Matrix matrix, ArrayList<Integer> maxes, double[] netBenefit){
		if(player.myType == Agent.Party.BUYER){
			for(int i=0; i<sellers.size(); i++){
				if(maxes.indexOf(i)!=-1){
					matrix.set(player.myID-1, buyers.size()+i, netBenefit[i]);
				} else {
					matrix.set(player.myID-1, buyers.size()+i, -1);
				}
			}
		} else {

		}
	}


	private static double connect(int buyerID, int sellerID, Agent.Party type){
		if(buyerPrices.get(buyerID-1)<sellerPrices.get(sellerID-1)){
			return -1;
			//later renegotiate
		}

		double alpha = 0.5;//nashBargain();
		double amount = 0;

		if(type == Agent.Party.BUYER){
			double toReturn = amount - alpha*costCreate;
			if(toReturn <= 0){
				return -1;
			} else {
				return toReturn;
			}
		} else {
			double toReturn = amount - (1-alpha)*costCreate;
			if(toReturn <= 0){
				return -1;
			} else {
				return toReturn;
			}
		}
	}

	private static double disconnect(int buyerID, int sellerID, Agent.Party type){
		return -2;
	}


	private static ArrayList<Integer> findMaximums(double[] arr){
		ArrayList<Integer> toReturn = new ArrayList<Integer>();
		int i = 0;
		int index = 0;
		double next = -2;
		
		while(i<turns && next!=-1){
			for(int j=0; j<arr.length; j++){
				if(arr[j]>next && toReturn.indexOf(j) == -1){
					index = j;
					next = arr[j];
				}
			}

			if(next!=-1){
				toReturn.add(index);
			}

			next = -2;

			i++;
		}

		return toReturn;
	}

}