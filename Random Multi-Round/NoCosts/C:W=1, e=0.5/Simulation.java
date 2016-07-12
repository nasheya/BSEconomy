/**********
* MULTI-ROUND SIMULATION (NO COSTS)
* This simulation simulates haggling between a defined amount of agents. It first distributes an amount of cash and wheat 
* randomly but each agent has 1 unit of good and there is a total of (1-x) * numAgents amount of cash and x * numAgents amount 
* of wheat in the entire system (0<x<1). Then two agents are picked randomly and depending on if one has more wheat than they 
* have utlity for and one has more cash than they have utility for (dpeending on the Cobb-Douglas Utility function), they will 
* randomly choose to trade some amount depending on the Rubenstein haggling model.
*/


import Jama.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

public class Simulation{

	static Random rng = new Random();

	ArrayList<Agent> agents = new ArrayList<Agent>();

	static int maxTimeForHaggling;
	static int maxRounds;

	// Transaction rates
	static PrintWriter amts1;
	static PrintWriter rates;

	// Transaction rates
	int amtAgree = 0;
	int amtTotal = 0;
	int tracker = 0;

	public Simulation(int numAgents, int rounds, int maxTime, double x, int integer){
		maxRounds = rounds;
		maxTimeForHaggling = maxTime;

		for(int i=0; i<numAgents; i++){
			//give each agent an id number starting from 0 to n-1
			agents.add(new Agent(i));
		}

		distributeCashAndWheat(x);

		try{
			PrintWriter before1 = new PrintWriter(new BufferedWriter(new FileWriter("DataFiles/BeforeCashWheat.txt", true)));
			PrintWriter beforeU1 = new PrintWriter(new BufferedWriter(new FileWriter("DataFiles/BeforeUtility.txt", true)));

			for(int i=0; i<agents.size(); i++){
				before1.println(agents.get(i).getCash() + "\t" + agents.get(i).getWheat());
				beforeU1.println(agents.get(i).getUtility());
			}

			before1.close();
			beforeU1.close();
		} catch (IOException e) {
			System.out.println("Error error!");
		}

		// Transaction rates
		try{
			String fileAmts = "DataFiles/Agree/AgreementRates"+integer+".txt";
			String fileRates = "DataFiles/Rates/Rates"+integer+".txt";
			amts1 = new PrintWriter(new BufferedWriter(new FileWriter(fileAmts, true)));
			rates = new PrintWriter(new BufferedWriter(new FileWriter(fileRates, true)));
		} catch(IOException e){
			System.out.println("Error error! Cannot find file.");
		}

		printAgentInfo();

		run();

		printAgentInfo();

		try{
			PrintWriter utility1 = new PrintWriter(new BufferedWriter(new FileWriter("DataFiles/AfterUtility.txt", true)));
			PrintWriter amount1 = new PrintWriter(new BufferedWriter(new FileWriter("DataFiles/TotalAmount.txt", true)));
			PrintWriter after1 = new PrintWriter(new BufferedWriter(new FileWriter("DataFiles/AfterCashWheat.txt", true)));

			for(int i=0; i<agents.size(); i++){
				utility1.println(agents.get(i).getUtility());
				after1.println(agents.get(i).getCash() + "\t" + agents.get(i).getWheat());
				amount1.println((agents.get(i).getCash() + agents.get(i).getWheat()));
			}

			utility1.close();
			amount1.close();
			after1.close();
		} catch (IOException e) {
			System.out.println("Error error!");
		}

		// Transaction rates
		 amts1.close();
		 rates.close();
	}


	/**
	* Runs the simulation
	*/
	public void run(){
		ArrayList<Agent> total = cloneArraylist(agents);
		ArrayList<Agent> particpants = cloneArraylist(agents);

		//run until you either hit your max number of rounds, the size of your participants 
		//less than one, or the participants are not tradeable
		for(int k=1; k<=maxRounds && (particpants.size()>1 && tradeable(particpants)); k++){
			System.out.println();
			System.out.println("ROUND "+ k + " WITH " + particpants.size() + " AGENTS");
			
			int i = 0;

			while(total.size()>1){
				System.out.println("Round "+ i);

				//pick an agent index number based on a uniform distribution
				int index = rng.nextInt(total.size());

				//pick agent here based on an uniform distribution but conditional probability function later
				int indexPair = rng.nextInt(total.size());

				//do not let them be the same number
				while(indexPair == index){
					indexPair = rng.nextInt(total.size());
				}

				Agent one = total.get(index);
				Agent two = total.get(indexPair);

				//Have to get the rounded amounts of the cash and wheat or else it may mess up
				double cashAmt1 = round(one.getRoundedAmount(1, true)*(1-one.getExponent()) , 4);
				double cashAmt2 = round(two.getRoundedAmount(1, true)*(1-two.getExponent()) , 4);
				double wheatAmt1 = round(one.getRoundedAmount(1, false)*one.getExponent() , 4);
				double wheatAmt2 = round(two.getRoundedAmount(1, false)*two.getExponent() , 4);

				//if someone has more cash than wheat and the other person also has more wheat than cash, then trade
				if(cashAmt1 > wheatAmt1 && wheatAmt2 > cashAmt2){
					haggling(one, two);

					maxUtility(particpants, one);
					maxUtility(particpants, two);

					removeAgentsFromHaggling(total, index, indexPair);

				} else if(wheatAmt1 > cashAmt1 && cashAmt2 > wheatAmt2){
					haggling(two, one);

					maxUtility(particpants, one);
					maxUtility(particpants, two);

					removeAgentsFromHaggling(total, index, indexPair);

				} else {
					System.out.println("Agents " + one.getID() + " and " + two.getID() + " could not trade.");
					one.printInfo();
					two.printInfo();
					System.out.println();

					if(maxUtility(particpants, one)){
						total.remove(total.indexOf(one));
					}
					
					if(maxUtility(particpants, two)){
						total.remove(total.indexOf(two));
					}

					//if the set is not tradeable, then just break
					if(!tradeable(total)){
						break;
					}
				}

				i++;
			}

			total = cloneArraylist(particpants);
		}
	}


	/**
	* This handles all the haggling between a specific designated buyer and seller.
	* It follows a special case of the Rubenstein model.
	*/
	public void haggling(Agent buyer, Agent seller){
		System.out.println("Agent " + buyer.getID() + " is the buyer and Agent " + seller.getID() + " is the seller.");
		System.out.printf("The original payoffs are %.4f for the buyer and %.4f for the seller." , buyer.getUtility(), seller.getUtility());
		System.out.println();
		buyer.printInfo();
		seller.printInfo();

		double cash = 0;
		double wheat = 0;
		int t = 1;
		boolean consensus = false;

		double surplusCash = buyer.getCash() - (buyer.getCash()+buyer.getWheat())*buyer.getExponent();
		double surplusWheat = seller.getWheat() - (seller.getCash()+seller.getWheat())*(1-seller.getExponent());

		while(!consensus && t <= maxTimeForHaggling){
			cash = Math.random() * surplusCash;
			wheat = Math.random() * surplusWheat;

			//if it is an odd numbered time
			if(t%2 == 1){
				int i = 0;

				//keep on trying to find an amount that works for the buyer in terms of payoff
				while(buyer.findChangeInUtility(cash, wheat, true) <= buyer.getUtility() && i<100){
					cash = Math.random() * surplusCash;
					wheat = Math.random() * surplusWheat;
					i++;
				}

				if(seller.findChangeInUtility(cash, wheat, false)>seller.getUtility() && buyer.findChangeInUtility(cash, wheat, true)>buyer.getUtility()){
					consensus = true;
				}

			} else {
				int i = 0;

				//keep on trying to find an amount that works for the buyer in terms of payoff
				while(seller.findChangeInUtility(cash, wheat, false) <= seller.getUtility() && i<100){
					cash = Math.random() * surplusCash;
					wheat = Math.random() * surplusWheat;
					i++;
				}

				if(seller.findChangeInUtility(cash, wheat, false)>seller.getUtility() && buyer.findChangeInUtility(cash, wheat, true)>buyer.getUtility()){
					consensus = true;
				}
			}

			t++;
		}

		if(consensus == true){
			buyer.setCash(buyer.getCash() - cash);
			buyer.setWheat(buyer.getWheat() + wheat);

			seller.setCash(seller.getCash() + cash);
			seller.setWheat(seller.getWheat() - wheat);

			System.out.println("They came to a consensus at time " + (t-1));
			System.out.printf("The buyer gave %.4f cash and the seller gave %.4f wheat to the buyer.", cash, wheat);
			System.out.println();
			
			//Transaction rates
			amtAgree++;
			rates.println(amtAgree+" "+cash/wheat);
		} else {
			System.out.println("The buyer and seller could not come to a consensus.");
		}

		//Transaction rates
		amtTotal++;
		tracker++;
		amts1.println(tracker + " " + (double)amtAgree/amtTotal);

		System.out.println();
	}


	/**
	* This method determines if the set given can trade at all. They are not tradeable 
	* only if everyone has more cash or more wheat than they want.
	*/
	private static boolean tradeable(ArrayList<Agent> total){
		boolean moreWheat = true;
		boolean moreCash = true;

		//if *everybody* has an abundance of wheat or cash, don't trade
		for(int i=0; i<total.size(); i++){
			double cashAmt = round(total.get(i).getRoundedAmount(1, true)*(1-total.get(i).getExponent()), 4);
			double wheatAmt = round(total.get(i).getRoundedAmount(1, false)*total.get(i).getExponent(), 4);

			if(wheatAmt < cashAmt){
				moreWheat = false;
			}

			if(cashAmt < wheatAmt){
				moreCash = false;
			}
		}

		if(moreCash || moreWheat){
			return false;
		} else {
			return true;
		}
	}


	/**
	* Finds out if the agent given have reached its maximum utility, and if so, removes it from the list
	* NOTE: MUST ALWAYS ROUND OR WILL NOT RECOGNIZE AS AMOUNTS BEING EQUAL
	*/
	private static boolean maxUtility(ArrayList<Agent> particpants, Agent one){
		if(round((1-one.getExponent())*one.getRoundedAmount(1, true), 4) == round(one.getRoundedAmount(1, false)*one.getExponent(), 4)){
			int index = particpants.indexOf(one);
			particpants.remove(index);

			System.out.println("Agent " + one.getID() + " got removed.");
			
			return true;
		}

		return false;
	}


	/**
	* This method distributes the amount of wheat and cash such that each agent 
	* has a total unit of good and that there is only (1-x) * numAgents amount of
	* cash and x * numAgents amount of wheat in the entire system.
	*/
	private void distributeCashAndWheat(double x){
		double totalWheat = x * agents.size();
		double distributedWheat = 0;

		//Randomly give each person an amount of wheat, not caring whether or 
		//not the total is greater or less than 1
		for(int i=0; i<agents.size(); i++){
			double wheat = Math.random();
			agents.get(i).setWheat(wheat);
			distributedWheat += wheat;
		}
		
		//adjust the amount of wheat and complement the amount of wheat with cash
		//so that the total amount is equal to 0.5 * numAgents and each agent has
		//at most 1 unit of good
		if(distributedWheat - totalWheat > 0){
			double leftover = distributedWheat - totalWheat;
			double amtToEach = (distributedWheat - totalWheat)/agents.size();

			while(leftover>0){
				for(int i=0; i<agents.size(); i++){
					//make sure no one goes in the negatives
					if(agents.get(i).getWheat()>amtToEach){
						agents.get(i).setWheat(agents.get(i).getWheat() - amtToEach);
						leftover-=amtToEach;
					}
				}

				amtToEach = leftover/agents.size();
			}
			
		} else if(distributedWheat - totalWheat < 0){
			double leftover = totalWheat - distributedWheat;
			double amtToEach = (totalWheat - distributedWheat)/agents.size();

			while(leftover>0){
				for(int i=0; i<agents.size(); i++){
					//make sure no one gets more than 1
					if(agents.get(i).getWheat()+amtToEach<=1){
						agents.get(i).setWheat(agents.get(i).getWheat() + amtToEach);
						leftover-=amtToEach;
					}
				}

				amtToEach = leftover/agents.size();
			}	
		}

		for(int i=0; i<agents.size(); i++){
			agents.get(i).setCash(1-agents.get(i).getWheat());
		}
	}


	/**
	* Given an arraylist (which is presumably the list for the specific round), this method will 
	* remove the two agents based on their indicies.
	*/
	private static void removeAgentsFromHaggling(ArrayList<Agent> total, int index, int indexPair){
		total.remove(index);

		if(index<indexPair){
			total.remove(indexPair-1);
		} else {
			total.remove(indexPair);
		}
	}


	/**
	* This method just deep copies an arraylist to another arraylist.
	*/
	private static ArrayList<Agent> cloneArraylist(ArrayList<Agent> temp){
		ArrayList<Agent> toReturn = new ArrayList<Agent>();

		for(int i=0; i<temp.size(); i++){
			toReturn.add(temp.get(i));
		}

		return toReturn;
	}


	/**
	* Prints the amount each agent has, along with the exponent
	*/
	public void printAgentInfo(){
		for(int i=0; i<agents.size(); i++){
			agents.get(i).printInfo();
		}

		System.out.println();
	}


	/**
	* Rounds the number to the given number of decimal places
	*/
	private static double round(double num, int places){
		return (Math.round(num*Math.pow(10, places))/Math.pow(10, places));
	}

}