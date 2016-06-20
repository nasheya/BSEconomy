/**********
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

	static int maxTimeForHaggling = 5;
	static int maxRounds = 50;
	static double delta1;
	static double delta2;

	
	static BufferedWriter amounts1;
	//static BufferedWriter amounts2;
	static PrintWriter amts1;
	//static PrintWriter amts2;
	
	
	public Simulation(int numAgents){
		for(int i=0; i<numAgents; i++){
			//give each agent an id number starting from 0 to n-1
			agents.add(new Agent(i));
		}

		agents.get(0).setWheat(0.1);
		agents.get(0).setCash(0.9);

		distributeCashAndWheat();

		try{
			amounts1 = new BufferedWriter(new FileWriter("Amounts1.1.txt", true));
			//amounts2 = new BufferedWriter(new FileWriter("Amounts2.1.txt", true));
			amts1 = new PrintWriter(amounts1);
			//amts2 = new PrintWriter(amounts2);
		} catch(IOException e){
			System.out.println("Error error! Cannot find file.");
		}

		// try{
		// 	BufferedWriter before = new BufferedWriter(new FileWriter("BeforeCashWheat.txt", true));
		// 	PrintWriter before1 = new PrintWriter(before);
		// 	BufferedWriter beforeU = new BufferedWriter(new FileWriter("BeforeUtility.txt", true));
		// 	PrintWriter beforeU1 = new PrintWriter(beforeU);

		// 	for(int i=0; i<agents.size(); i++){
		// 		before1.println(agents.get(i).getCash() + "\t" + agents.get(i).getWheat());
		// 		beforeU1.println((agents.get(i).getCash()*agents.get(i).getWheat()));
		// 	}

		// 	before1.close();
		// 	beforeU1.close();
		// } catch (IOException e) {
		// 	System.out.println("Error error!");
		// }

		printAgentAmounts();

		run();

		printAgentAmounts();

		// try{
		// 	BufferedWriter utility = new BufferedWriter(new FileWriter("AfterUtility.txt", true));
		// 	BufferedWriter amount = new BufferedWriter(new FileWriter("TotalAmount.txt", true));
		// 	BufferedWriter after = new BufferedWriter(new FileWriter("AfterCashWheat.txt", true));
		// 	PrintWriter utility1 = new PrintWriter(utility);
		// 	PrintWriter amount1 = new PrintWriter(amount);
		// 	PrintWriter after1 = new PrintWriter(after);

		// 	for(int i=0; i<agents.size(); i++){
		// 		utility1.println(agents.get(i).getCash()*agents.get(i).getWheat());
		// 		after1.println(agents.get(i).getCash() + "\t" + agents.get(i).getWheat());
		// 		amount1.println((agents.get(i).getCash() + agents.get(i).getWheat()));
		// 	}

		// 	utility1.close();
		// 	amount1.close();
		// 	after1.close();
		// } catch (IOException e) {
		// 	System.out.println("Error error!");
		// }
		amts1.close();
		//amts2.close();
	}


	/**
	* Runs the simulation
	*/
	public static void run(){
		ArrayList<Agent> total = cloneAgents(agents);
		ArrayList<Agent> particpants = cloneAgents(agents);

		for(int k=0; k<maxRounds && (particpants.size()>1 && tradeable(particpants)); k++){
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

				Agent one = total.get(0);
				Agent two = total.get(1);

				System.out.printf("Agent "+ one.getID() +" has %.4f cash and %.4f wheat, and Agent " + two.getID() + " has %.4f cash and %.4f wheat.", 
							one.getCash(), one.getWheat(), two.getCash(), two.getWheat());


				//if someone has more cash than wheat and the other person also has more wheat than cash, then trade
				if(getDivided(one, 1, true)>getDivided(one, 1, false) && getDivided(two, 1, false)>getDivided(two, 1, true)){
					haggling(one, two);

					maxUtility(particpants, one);
					maxUtility(particpants, two);

					total.remove(index);

					if(index<indexPair){
						total.remove(indexPair - 1);
					} else {
						total.remove(indexPair);
					}
					
				//or vice versa
				} else if(getDivided(one, 1, false)>getDivided(one, 1, true) && getDivided(two, 1, true)>getDivided(two, 1, false)){
					haggling(two, one);

					maxUtility(particpants, one);
					maxUtility(particpants, two);

					total.remove(index);

					if(index<indexPair){
						total.remove(indexPair - 1);
					} else {
						total.remove(indexPair);
					}
				} else {
					//System.out.printf("Agent "+ one.getID() +" has %.2f cash and %.2f wheat, and Agent " + two.getID() + " has %.2f cash and %.2f wheat.", 
					//		  one.getCash(), one.getWheat(), two.getCash(), two.getWheat());

					System.out.println();
					System.out.println();

					maxUtility(particpants, one);
					maxUtility(particpants, two);

					//if the set is not tradeable, then just break
					if(!tradeable(total)){
						break;
					}
				}

				if(one.getID() == 0){
					amts1.println((k+i) + " " + one.getCash() + " " + one.getWheat());
				} else if(two.getID() == 0){
					amts1.println((k+i) + " " + two.getCash() + " " + two.getWheat());
				}	

				i++;
			}

			total = cloneAgents(particpants);
		}
	}


	/**
	* This method determines if the set given can trade at all.
	*/
	private static boolean tradeable(ArrayList<Agent> total){
		boolean moreWheat = true;
		boolean moreCash = true;

		//if *everybody* has an abundance of wheat or cash, don't trade
		for(int i=0; i<total.size(); i++){
			if(getDivided(total.get(i), 2, false)<getDivided(total.get(i), 2, true)){
				moreWheat = false;
			}

			if(getDivided(total.get(i), 2, true)<getDivided(total.get(i), 2, false)){
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
	* This method just deep copies an arraylist to another arraylist.
	*/
	private static ArrayList<Agent> cloneAgents(ArrayList<Agent> temp){
		ArrayList<Agent> toReturn = new ArrayList<Agent>();

		for(int i=0; i<temp.size(); i++){
			toReturn.add(temp.get(i));
		}

		return toReturn;
	}


	/**
	* This handles all the haggling between a specific designated buyer and seller.
	* It follows a special case of the Rubenstein model.
	*/
	public static void haggling(Agent buyer, Agent seller){
		System.out.println("Agent " + buyer.getID() + " is the buyer and Agent " + seller.getID() + " is the seller.");
		//the deltas are randomly picked
		delta1 = Math.random();
		delta2 = Math.random();

		//these are the original values that the buyer and seller have
		double bPayoffOrig = buyer.getCash()*buyer.getWheat();
		double sPayoffOrig = seller.getCash()*seller.getWheat();

		System.out.printf("The original payoffs are %.2f for the buyer and %.2f for the seller." , bPayoffOrig, sPayoffOrig);
		System.out.println();
		// System.out.printf("The buyer has %.2f cash and %.2f wheat, and the seller has %.2f cash and %.2f wheat.", 
		// 				  buyer.getCash(), buyer.getWheat(), seller.getCash(), seller.getWheat());
		System.out.println();

		double cash = 0;
		double wheat = 0;

		boolean consensus = false;
		int t = 1;

		double surplusCash = buyer.getCash() - (buyer.getCash()+buyer.getWheat())/2;
		double surplusWheat = seller.getWheat() - (seller.getCash()+seller.getWheat())/2;

		while(!consensus && t <= maxTimeForHaggling){
			cash = Math.random() * surplusCash;
			wheat = Math.random() * surplusWheat;

			//if it is an odd numbered time
			if(t%2 == 1){
				double bPayoff = (buyer.getCash()-cash*Math.pow(delta1, t-1))*(buyer.getWheat()+wheat*Math.pow(delta1, t-1));

				int i = 0;

				//keep on trying to find an amount that works for the buyer in terms of payoff
				while(bPayoff <= bPayoffOrig && i<100){
					cash = Math.random() * surplusCash;
					wheat = Math.random() * surplusWheat;
					bPayoff = (buyer.getCash()-cash*Math.pow(delta1, t-1))*(buyer.getWheat()+wheat*Math.pow(delta1, t-1));
					i++;
				}
				
				//see if the amount works for the seller
				double sPayoff = (seller.getCash()+cash*Math.pow(delta2, t-1))*(seller.getWheat()-wheat*Math.pow(delta2, t-1));

				if(sPayoff>sPayoffOrig && bPayoff > bPayoffOrig){
					consensus = true;
				}

			} else {
				double sPayoff = (seller.getCash()+cash*Math.pow(delta2, t-1))*(seller.getWheat()-wheat*Math.pow(delta2, t-1));

				int i = 0;

				//keep on trying to find an amount that works for the seller in terms of payoff
				while(sPayoff <= sPayoffOrig && i<100){
					cash = Math.random() * surplusCash;
					wheat = Math.random() * surplusWheat;
					sPayoff = (seller.getCash()+cash*Math.pow(delta2, t-1))*(seller.getWheat()-wheat*Math.pow(delta2, t-1));
					i++;
				}
				
				//see if the amount works for the buyer
				double bPayoff = (buyer.getCash()-cash*Math.pow(delta1, t-1))*(buyer.getWheat()+wheat*Math.pow(delta1, t-1));

				if(sPayoff>sPayoffOrig && bPayoff>bPayoffOrig){
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
			System.out.printf("The buyer gave %.2f cash and the seller gave %.2f wheat to the buyer.", cash, wheat);
			System.out.println();
		} else {
			System.out.println("The buyer and seller could not come to a consensus.");
		}

		System.out.println();
	}


	/**
	* This method distributes the amount of wheat and cash such that each agent 
	* has a total unit of good and that there is only 0.5 * numAgents amount of
	* cash and wheat in the entire system.
	*/
	private static void distributeCashAndWheat(){
		double totalWheat = 0.5 * agents.size()-0.1;
		double distributedWheat = 0;

		//Randomly give each person an amount of wheat, not caring whether or 
		//not the total is greater or less than 1
		for(int i=1; i<agents.size(); i++){
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
				for(int i=1; i<agents.size(); i++){
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
				for(int i=1; i<agents.size(); i++){
					//make sure no one gets more than 1
					if(agents.get(i).getWheat()+amtToEach<=1){
						agents.get(i).setWheat(agents.get(i).getWheat() + amtToEach);
						leftover-=amtToEach;
					}
				}

				amtToEach = leftover/agents.size();
			}	
		}

		for(int i=1; i<agents.size(); i++){
			agents.get(i).setCash(1-agents.get(i).getWheat());
		}
	}


	/**
	* Prints the amounts each agent has
	*/
	public static void printAgentAmounts(){
		for(int i=0; i<agents.size(); i++){
			System.out.printf("Agent "+ agents.get(i).getID() + " has %.4f amount of cash and %.4f amount of wheat.", agents.get(i).getCash(), agents.get(i).getWheat());
			System.out.println();
		}

		System.out.println();
	}


	/**
	* Finds out if the agent given have reached its maximum utility, and if so, removes it from the list
	*/
	private static boolean maxUtility(ArrayList<Agent> particpants, Agent one){
		boolean removed = false;

		if(getDivided(one, 1, false) == getDivided(one, 1, true)){
			particpants.remove(particpants.indexOf(one));
			System.out.println("Agent " + one.getID() + " got removed.");
			removed = true;
		}

		return removed;
	}


	private static double getDivided(Agent temp, double numToDivide, boolean cash){
		if(cash){
			return ((double)Math.round(temp.getCash()/numToDivide*10000.0))/10000.0;
		} else {
			return ((double)Math.round(temp.getWheat()/numToDivide*10000.0))/10000.0;
		}
	}

}