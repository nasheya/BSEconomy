/**
* Authors: Joshua Hayes and Nasheya Rahman
* A simulation concerning a buyer-seller exchange economy as part of Dr. Michael Kerckhove's summer research 2016. 
* The assumptions are that the exchange rates are the same, the cost is always divided in half, you begin with a disconnected
*  graph, and each player knows how many global players there are.
*/

import java.util.ArrayList;
import java.util.Random;
import Jama.*;

public class Simulation {
	static Random rng = new Random();

	//Holds all the connections between buyers and sellers
	static Matrix a;

	//Holds the buyers and sellers
	static ArrayList<Agent> buyers = new ArrayList<Agent>();
	static ArrayList<Agent> sellers = new ArrayList<Agent>();

	//Holds all the possible pair combinations
	static ArrayList< ArrayList<Integer> > pairs = new ArrayList< ArrayList<Integer> >();

	static double costCreate;
	static double costDissolve;
	static double prob;


	/**
	* Initalizes all the variables necessary for the simulation and then runs the simulation.
	* @param c 				Represents the cost of creating an edge
	* @param d 				Represents the cost of dissolving an edge
	* @param buyersNum 		Represents the number of inital buyers in the system
	* @param sellersNum 	Represents the number of initial sellers in the system
	* @param maxRounds 		Represents the maximum number of rounds the simulation should execute
	* @param probAdd		Represents the probability, between 0 and 1, that a buyer or seller would be added to the system
	*/
	public Simulation(double c, double d, int buyersNum, int sellersNum, double maxRounds, double probAdd){
		costCreate = c;
		costDissolve = d;

		if(probAdd>1){
			prob = 1;
		} else if(probAdd<0){
			prob = 0;
		} else {
			prob = probAdd;
		}

		a = new Matrix(buyersNum, sellersNum);

		//Initialize the list of buyers and sellers
		for(int i=0; i<a.getRowDimension(); i++){
			buyers.add(new Agent(Agent.Party.BUYER, i+1));
		}

		for(int i=0; i<a.getColumnDimension(); i++){
			sellers.add(new Agent(Agent.Party.SELLER, i+1));
		}

		//Obtains all the possible pairs
		for(int i=0; i<a.getRowDimension(); i++){
			for(int j=0; j<a.getColumnDimension(); j++){
				ArrayList<Integer> pairTemp = new ArrayList<Integer>();
				pairTemp.add(i+1);
				pairTemp.add(j+1);
				pairs.add(pairTemp);
			}
		}

		//Executes the simulation a certain number of times
		a.print(1,1);

		for(int i=1; i<=maxRounds; i++){
			System.out.println("Round "+i);
			playGame();
		}
	}


	/**
	* Executes one round of the game.
	*/
	public static void playGame(){
		addAgent();

		java.util.Collections.shuffle(pairs);

		int numPlayersLeft = buyers.size() + sellers.size();
		boolean[] playerTurns = new boolean[numPlayersLeft];

		for(int i=0; i<playerTurns.length; i++){
			playerTurns[i] = true;
		}

		//Represents the index of the pairs that you are accessing
		int k = 0;

		while(numPlayersLeft>0){
			ArrayList<Integer> pairTemp = pairs.get(k);
			int buyerID = pairTemp.get(0);
			int sellerID = pairTemp.get(1);

			System.out.println(buyerID+ " "+sellerID);

			k++;

			//Updating the numPlayersLeft flag
			if(playerTurns[buyerID-1]==true){
				playerTurns[buyerID-1] = false;
				numPlayersLeft--;
			}

			if(playerTurns[sellerID+buyers.size()-1]==true){
				playerTurns[sellerID+buyers.size()-1] = false;
				numPlayersLeft--;
			}

			//if they are connected
			if(a.get(buyerID-1, sellerID-1) == 1){
				if(true){ //choose to disconnect
					buyers.get(buyerID-1).numConnections--;
					sellers.get(sellerID-1).numConnections--;
					a.set(buyerID-1, sellerID-1, 0);
				} 
			} else { //if they are not connected
				if(true){ //choose to connect
					buyers.get(buyerID-1).numConnections++;
					sellers.get(sellerID-1).numConnections++;
					a.set(buyerID-1, sellerID-1, 1);
				} 
			}	
		}
		a.print(1,1);

		//trade();
	}


	/**
	* Adds an agent or not based on the probability of adding an agent.
	*/
	private static void addAgent(){
		if(rng.nextDouble()<=prob){
			if(rng.nextDouble()<0.5){
				buyers.add(new Agent(Agent.Party.BUYER, buyers.size()+1));
				
				for(int i=0; i<sellers.size(); i++){
					ArrayList<Integer> pairTemp = new ArrayList<Integer>();
					pairTemp.add(buyers.size());
					pairTemp.add(i+1);
					pairs.add(pairTemp);
				}

				a = addMatrixSection(true);
				a.print(1,1);

			} else {
				sellers.add(new Agent(Agent.Party.SELLER, sellers.size()+1));

				for(int i=0; i<buyers.size(); i++){
					ArrayList<Integer> pairTemp = new ArrayList<Integer>();
					pairTemp.add(i+1);
					pairTemp.add(sellers.size());
					pairs.add(pairTemp);
				}

				a = addMatrixSection(false);
			}
		}
	}


	/**
	* Resizes the matrix based on whether or not you are adding a buyer or seller.
	*/
	private static Matrix addMatrixSection(boolean buyer){
		Matrix temp;

		if(buyer){
			temp = new Matrix(a.getRowDimension()+1, a.getColumnDimension());

			for(int i=0; i<=a.getRowDimension(); i++){
				for(int j=0; j<a.getColumnDimension(); j++){
					if(i!=a.getRowDimension()){
						temp.set(i, j, a.get(i,j));
					} else {
						temp.set(i,j,0);
					}
				}
			}
		} else {
			temp = new Matrix(a.getRowDimension(), a.getColumnDimension()+1);

			for(int i=0; i<a.getRowDimension(); i++){
				for(int j=0; j<=a.getColumnDimension(); j++){
					if(j!=a.getColumnDimension()){
						temp.set(i, j, a.get(i,j));
					} else {
						temp.set(i,j,0);
					}
				}
			}
		}

		return temp;
	}
}