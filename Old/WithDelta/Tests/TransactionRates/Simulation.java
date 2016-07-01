/**********
* MULTI-ROUND SIMULATION (NO COSTS, SAME UTILITY, TRANSACTION RATES)
* This simulation simulates haggling between a defined amount of agents. It first distributes an amount of cash and wheat 
* randomly but each agent has 1 unit of good and there is a total of 0.5*(number of agents) amount of cash and wheat within 
* the system. Then two agents are picked randomly and depending on if one has more wheat than they have utlity for and one 
* has more cash than they have utility for (dpeending on the Cobb-Douglas Utility function), they will randomly choose to 
* trade some amount depending on the Rubenstein haggling model.
*/


import Jama.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

public class Simulation{

	static Random rng = new Random();

	static ArrayList<Agent> agents = new ArrayList<Agent>();

	static int maxTimeForHaggling;
	static int maxRounds;

	static PrintWriter amts1;
	static PrintWriter rates;

	static int tracker = 0;

	static int amtTotal = 0;
	static int amtAgree = 0;

	public Simulation(int numAgents, int rounds, int maxTime){
		maxRounds = rounds;
		maxTimeForHaggling = maxTime;

		for(int i=0; i<numAgents; i++){
			//give each agent an id number starting from 0 to n-1
			agents.add(new Agent(i));
		}

		distributeCashAndWheat();

		try{
			BufferedWriter amounts1 = new BufferedWriter(new FileWriter("AgreementRates2.txt", true));
			amts1 = new PrintWriter(amounts1);
			BufferedWriter rates1 = new BufferedWriter(new FileWriter("Rates1.txt", true));
			rates = new PrintWriter(rates1);
		} catch(IOException e){
			System.out.println("Error error! Cannot find file.");
		}

		printAgentInfo();

		run();

		printAgentInfo();

		amts1.close();
		rates.close();
	}


	/**
	* Runs the simulation
	*/
	public static void run(){
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
				double cashAmt1 = one.getRoundedAmount(1, true);
				double cashAmt2 = two.getRoundedAmount(1, true);
				double wheatAmt1 = one.getRoundedAmount(1, false);
				double wheatAmt2 = two.getRoundedAmount(1, false);

				//if someone has more cash than wheat and the other person also has more wheat than cash, then trade
				if(cashAmt1 > wheatAmt1 && wheatAmt2 > cashAmt2){
					haggling(one, two);

					tracker++;
					amts1.println(tracker+" "+(double)amtAgree/amtTotal);

					maxUtility(particpants, one);
					maxUtility(particpants, two);

					removeAgentsFromHaggling(total, index, indexPair);

				} else if(cashAmt1 < wheatAmt1 && wheatAmt2 < cashAmt2){
					haggling(two, one);

					tracker++;
					amts1.println(tracker+" "+(double)amtAgree/amtTotal);

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
	public static void haggling(Agent buyer, Agent seller){
		System.out.println("Agent " + buyer.getID() + " is the buyer and Agent " + seller.getID() + " is the seller.");
		System.out.printf("The original payoffs are %.4f for the buyer and %.4f for the seller." , buyer.getUtility(), seller.getUtility());
		System.out.println();
		buyer.printInfo();
		seller.printInfo();
		
		double d1 = buyer.getDelta();
		double d2 = seller.getDelta();

		double cash = 0;
		double wheat = 0;
		int t = 1;
		boolean consensus = false;

		double surplusCash = buyer.getCash() - (buyer.getCash()+buyer.getWheat())/2;
		double surplusWheat = seller.getWheat() - (seller.getCash()+seller.getWheat())/2;

		amtTotal++;

		while(!consensus && t <= maxTimeForHaggling){
			cash = Math.random() * surplusCash;
			wheat = Math.random() * surplusWheat;

			//if it is an odd numbered time
			if(t%2 == 1){
				double bPayoff = buyer.findUtility(buyer.getCash()-cash*Math.pow(d1, t-1), buyer.getWheat()+wheat*Math.pow(d1, t-1));

				int i = 0;

				//keep on trying to find an amount that works for the buyer in terms of payoff
				while(bPayoff <= buyer.getUtility() && i<100){
					cash = Math.random() * surplusCash;
					wheat = Math.random() * surplusWheat;
					bPayoff = buyer.findUtility(buyer.getCash()-cash*Math.pow(d1, t-1), buyer.getWheat()+wheat*Math.pow(d1, t-1));
					i++;
				}
				
				//see if the amount works for the seller
				double sPayoff = seller.findUtility(seller.getCash()+cash*Math.pow(d2, t-1), seller.getWheat()-wheat*Math.pow(d2, t-1));

				if(sPayoff>seller.getUtility() && bPayoff>buyer.getUtility()){
					consensus = true;
				}

			} else {
				double sPayoff = seller.findUtility(seller.getCash()+cash*Math.pow(d2, t-1), seller.getWheat()-wheat*Math.pow(d2, t-1));

				int i = 0;

				//keep on trying to find an amount that works for the seller in terms of payoff
				while(sPayoff <= seller.getUtility() && i<100){
					cash = Math.random() * surplusCash;
					wheat = Math.random() * surplusWheat;
					sPayoff = seller.findUtility(seller.getCash()+cash*Math.pow(d2, t-1), seller.getWheat()-wheat*Math.pow(d2, t-1));
					i++;
				}
				
				//see if the amount works for the buyer
				double bPayoff = buyer.findUtility(buyer.getCash()-cash*Math.pow(d1, t-1), buyer.getWheat()+wheat*Math.pow(d1, t-1));

				if(sPayoff>seller.getUtility() && bPayoff>buyer.getUtility()){
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
			amtAgree++;
			rates.println(amtAgree+" "+cash/wheat);
		} else {
			System.out.println("The buyer and seller could not come to a consensus.");
		}

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
			double cashAmt = total.get(i).getRoundedAmount(1, true);
			double wheatAmt = total.get(i).getRoundedAmount(1, false);

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
		if(one.getRoundedAmount(1, true) == one.getRoundedAmount(1, false)){
			int index = particpants.indexOf(one);
			particpants.remove(index);

			System.out.println("Agent " + one.getID() + " got removed.");
			
			return true;
		}

		return false;
	}


	/**
	* This method distributes the amount of wheat and cash such that each agent 
	* has a total unit of good and that there is only 0.5 * numAgents amount of
	* cash and wheat in the entire system.
	*/
	private static void distributeCashAndWheat(){
		double totalWheat = 0.5 * agents.size();
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
			double amtToEach = (distributedWheat - totalWheat)/(agents.size());

			while(leftover>0){
				for(int i=0; i<agents.size(); i++){
					//make sure no one goes in the negatives
					if(agents.get(i).getWheat()>amtToEach){
						agents.get(i).setWheat(agents.get(i).getWheat() - amtToEach);
						leftover-=amtToEach;
					}
				}

				amtToEach = leftover/(agents.size());
			}
			
		} else if(distributedWheat - totalWheat < 0){
			double leftover = totalWheat - distributedWheat;
			double amtToEach = (totalWheat - distributedWheat)/(agents.size());

			while(leftover>0){
				for(int i=0; i<agents.size(); i++){
					//make sure no one gets more than 1
					if(agents.get(i).getWheat()+amtToEach<=1){
						agents.get(i).setWheat(agents.get(i).getWheat() + amtToEach);
						leftover-=amtToEach;
					}
				}

				amtToEach = leftover/(agents.size());
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
	* Prints the amount each agent has
	*/
	public static void printAgentInfo(){
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