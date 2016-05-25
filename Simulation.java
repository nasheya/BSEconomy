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
	//static ArrayList<Agent> total = new ArrayList<Agent>();

	//Holds all the possible pair combinations
	static ArrayList< ArrayList<Integer> > pairs = new ArrayList< ArrayList<Integer> >();

	static double costCreate;
	static double costDissolve;
	static double costMaintain;
	static double prob;


	/**
	* Initalizes all the variables necessary for the simulation and then runs the simulation.
	* @param c 				Represents the cost of creating an edge
	* @param d 				Represents the cost of dissolving an edge
	* @param m 				Represents the cost to maintain an edge
	* @param buyersNum 		Represents the number of inital buyers in the system
	* @param sellersNum 	Represents the number of initial sellers in the system
	* @param maxRounds 		Represents the maximum number of rounds the simulation should execute
	* @param probAdd		Represents the probability, between 0 and 1, that a buyer or seller would be added to the system
	*/
	public Simulation(double c, double d, double m, int buyersNum, int sellersNum, double maxRounds, double probAdd){
		costCreate = c;
		costDissolve = d;
		costMaintain = m;

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

		//total.addAll(buyers);
		//total.addAll(sellers);

		//Obtains all the possible pairs
		for(int i=0; i<a.getRowDimension(); i++){
			for(int j=0; j<a.getColumnDimension(); j++){
				ArrayList<Integer> pairTemp = new ArrayList<Integer>();
				pairTemp.add(i+1);
				pairTemp.add(j+1);
				pairs.add(pairTemp);
			}
		}

		a.print(1,0);

		//Executes the simulation a certain number of times
		for(int i=1; i<=maxRounds; i++){
			System.out.println("Round "+i);
			playGame();
		}
	}


	/**
	* Executes one round of the game by choosing random players.
	*/
	// public static void playGame2(){
	// 	//addAgent();
		
	// 	java.util.Collections.shuffle(total);

	// 	for(int i=0; i<total.size(); i++){
	// 		int turnsTaken = 0;

			
	// 	}
	// }


	/**
	* Executes one round of the game by choosing pairs.
	*/
	public static void playGame(){
		addAgent();

		java.util.Collections.shuffle(pairs);

		int numPlayersLeft = buyers.size() + sellers.size();
		boolean[] playerTurns = new boolean[numPlayersLeft];

		for(int i=0; i<playerTurns.length; i++){
			playerTurns[i] = true;

			if(i<buyers.size()){
				buyers.get(i).resetCosts();
			} else {
				sellers.get(i-buyers.size()).resetCosts();
			}
		}

		//Represents the index of the pairs that you are accessing
		int k = 0;

		while(numPlayersLeft>0){
			ArrayList<Integer> pairTemp = pairs.get(k);
			int buyerID = pairTemp.get(0);
			int sellerID = pairTemp.get(1);

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

			//to connect or disconnect decisions
			if(a.get(buyerID-1, sellerID-1) == 1){
				if(shouldDisconnect(buyerID, sellerID)){ //choose to disconnect
					buyers.get(buyerID-1).numConnections--;
					sellers.get(sellerID-1).numConnections--;
					a.set(buyerID-1, sellerID-1, 0);
				} 
			} else {
				if(shouldConnect(buyerID, sellerID)){ //choose to connect
					buyers.get(buyerID-1).numConnections++;
					sellers.get(sellerID-1).numConnections++;
					a.set(buyerID-1, sellerID-1, 1);
				} 
			}	
		}

		a.print(1,0);

		trade();
	}


	/**
	* This method determines if a connected pair should disconnect or not.
	*/
	private static boolean shouldDisconnect(int buyerID, int sellerID){
		int numConnectB = buyers.get(buyerID-1).numConnections;
		int numConnectS = sellers.get(sellerID-1).numConnections;

		double amtS = 1/numConnectB - costMaintain/2;
		double amtB = 1/numConnectS - costMaintain/2;

		double dS = 0;
		double dS2 = 0;
		double dB = 0;
		double dB2 = 0;

		if(numConnectB>1){
			dS = 1/(numConnectB-1) - costDissolve;
			dS2 = 1/(numConnectB-1) - costDissolve/2;
		}

		if(numConnectS>1){
			dB = 1/(numConnectS-1) - costDissolve;
			dB2 = 1/(numConnectS-1) - costDissolve/2;
		}

		if(dS2>amtS && dB2>amtB){
			buyers.get(buyerID-1).addToCosts(costDissolve/2);
			sellers.get(sellerID-1).addToCosts(costDissolve/2);
		} else if(dS>amtS){
			sellers.get(sellerID-1).addToCosts(costDissolve);
		} else if(dB>amtB){
			buyers.get(buyerID-1).addToCosts(costDissolve);
		} else {
			sellers.get(sellerID-1).addToCosts(costMaintain/2);
			buyers.get(buyerID-1).addToCosts(costMaintain/2);
		}

		if((amtS<0||amtB<0)||(dS>amtS||dB>amtB)||(dS2>amtS && dB2>amtB)){
			return true;
		}

		return false;
	}


	/**
	* This method determines if a pair should connect or not.
	*/
	private static boolean shouldConnect(int buyerID, int sellerID){
		int numConnectB = buyers.get(buyerID-1).numConnections;
		int numConnectS = sellers.get(sellerID-1).numConnections;

		double amtS = 1.0/(numConnectB+1) - costCreate/2;
		double amtB = 1.0/(numConnectS+1) - costCreate/2;

		buyers.get(buyerID-1).addToCosts(costCreate/2);
		sellers.get(sellerID-1).addToCosts(costCreate/2);

		if(amtS>0 && amtB>0){
			return true;
		}
		
		return false;
	}


	/**
	* Carries out the trading done between the estabished connections.
	*/
	private static void trade(){
		for(int i=0; i<buyers.size(); i++){
			int connections = buyers.get(i).numConnections;

			if(connections!=0){
				double amtToEach = 1.0/connections;

				for(int j=0; j<sellers.size(); j++){
					if(a.get(i,j)==1){
						sellers.get(j).addToBackpack(amtToEach);
						System.out.println("Buyer " + (i+1) + " gave " + amtToEach + " to Seller " + (j+1));
					}
				}
			} else {
				continue;
			}
		}

		for(int i=0; i<sellers.size(); i++){
			int connections = sellers.get(i).numConnections;

			if(connections!=0){
				double amtToEach = 1.0/connections;

				for(int j=0; j<buyers.size(); j++){
					if(a.get(j,i)==1){
						buyers.get(j).addToBackpack(amtToEach);
						System.out.println("Seller " + (i+1) + " gave " + amtToEach + " to Buyer " + (j+1));
					}
				}
			} else {
				continue;
			}
		}

		subtractCosts();
	}


	/**
	* Subtract the costs at the end of the trading round.
	*/
	private static void subtractCosts(){
		for(int i=0; i<sellers.size(); i++){
			System.out.println("Before for Seller "+(i+1)+": "+sellers.get(i).myBackpack);
			sellers.get(i).subtractCosts();
			System.out.println("After for Seller "+(i+1)+": "+sellers.get(i).myBackpack);
		}

		for(int i=0; i<buyers.size(); i++){
			System.out.println("Before for Buyer "+(i+1)+": "+buyers.get(i).myBackpack);
			buyers.get(i).subtractCosts();
			System.out.println("After for Buyer "+(i+1)+": "+buyers.get(i).myBackpack);
		}
	}


	/**
	* Adds an agent or not based on the probability of adding an agent.
	*/
	private static void addAgent(){
		if(rng.nextDouble()<=prob){
			if(rng.nextDouble()<0.5){
				buyers.add(new Agent(Agent.Party.BUYER, buyers.size()+1));
				//total.add(buyers.get(buyers.size()-1));
				
				for(int i=0; i<sellers.size(); i++){
					ArrayList<Integer> pairTemp = new ArrayList<Integer>();
					pairTemp.add(buyers.size());
					pairTemp.add(i+1);
					pairs.add(pairTemp);
				}

				a = addMatrixSection(true);
			} else {
				sellers.add(new Agent(Agent.Party.SELLER, sellers.size()+1));
				//total.add(sellers.get(sellers.size()-1));

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