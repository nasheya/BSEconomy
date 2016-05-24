/**
* Authors: Joshua Hayes and Nasheya Rahman
* A simulation concerning a buyer-seller exchange economy as part of Dr. Michael Kerckhove's summer research 2016. 
* 
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
	* @param probAdd		Represents the probability that a buyer or seller would be added to the system, must be between 0 and 1
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
		for(int i=0; i<maxRounds; i++){
			playGame();
		}
	}


	/**
	* Executes one round of the game.
	*/
	public static void playGame(){
		//addAgent();

		java.util.Collections.shuffle(pairs);

		int numPlayersLeft = buyers.size() + sellers.size();
		boolean[] playerTurns = new boolean[numPlayersLeft];

		//Represents the index of the pairs that you are accessing
		int k = 0;

		while(numPlayersLeft>0){
			ArrayList<Integer> pairTemp = pairs.get(k);
			k++;


		}

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
					pairTemp.add(i);
					pairs.add(pairTemp);
				}

				//add row in matrix

			} else {
				sellers.add(new Agent(Agent.Party.SELLER, sellers.size()+1));

				for(int i=0; i<buyers.size(); i++){
					ArrayList<Integer> pairTemp = new ArrayList<Integer>();
					pairTemp.add(i);
					pairTemp.add(sellers.size(););
					pairs.add(pairTemp);
				}

				//add column in matrix
			}
		}
	}
}