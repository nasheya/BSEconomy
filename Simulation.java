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

public class Simulation{

	static Random rng = new Random();

	static ArrayList<Agent> agents = new ArrayList<Agent>();

	static int maxTimeForHaggling = 10;
	static double delta1;
	static double delta2;

	public Simulation(int numAgents){
		for(int i=0; i<numAgents; i++){
			//give each agent an id number starting from 0 to n-1
			agents.add(new Agent(i));
		}

		distributeCashAndWheat();

		printAgentAmounts();

		run();

		printAgentAmounts();
	}


	public static void run(){
		ArrayList<Agent> total = cloneAgents(agents);

		int i = 1;

		while(total.size()>1){
			System.out.println("Round "+ i + " with " + total.size() + " agents");
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

			if(one.getCash()>0.5 && two.getCash()<0.5 && one.getWheat()<0.5 && two.getWheat()>0.5){
				haggling(one, two);
				total.remove(index);

				if(index<indexPair){
					total.remove(indexPair-1);
				} else {
					total.remove(indexPair);
				}
				
			} else if(one.getWheat()>0.5 && two.getWheat()<0.5 && one.getCash()<0.5 && two.getCash()>0.5){
				haggling(two, one);
				total.remove(index);

				if(index<indexPair){
					total.remove(indexPair-1);
				} else {
					total.remove(indexPair);
				}
			} else {
				System.out.printf("Agent "+ one.getID() +" has %.2f cash and %.2f wheat, and Agent " + two.getID() + " has %.2f cash and %.2f wheat.", 
						  one.getCash(), one.getWheat(), two.getCash(), two.getWheat());

				System.out.println();
				System.out.println();

				if(!tradeable(total)){
					break;
				}
			}

			i++;
		}

		
	}


	/**
	* This method determines if the set given can trade at all.
	*/
	private static boolean tradeable(ArrayList<Agent> total){
		boolean moreWheat = true;
		boolean moreCash= true;

		for(int i=0; i<total.size(); i++){
			if(total.get(i).getWheat()<0.5){
				moreWheat = false;
			}

			if(total.get(i).getCash()<0.5){
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
		System.out.printf("The buyer has %.2f cash and %.2f wheat, and the seller has %.2f cash and %.2f wheat.", 
						  buyer.getCash(), buyer.getWheat(), seller.getCash(), seller.getWheat());
		System.out.println();

		double cash = 0;
		double wheat = 0;

		boolean consensus = false;
		int t = 1;

		double surplusCash = buyer.getCash() - 0.5;
		double surplusWheat = seller.getWheat() - 0.5;

		while(!consensus && t <= maxTimeForHaggling){
			cash = Math.random() * surplusCash;
			wheat = Math.random() * surplusWheat;

			//if it is an odd numbered time
			if(t%2 == 1){
				double bPayoff = (buyer.getCash()-cash*Math.pow(delta1, t-1))*(buyer.getWheat()+wheat*Math.pow(delta1, t-1));

				while(bPayoff <= bPayoffOrig || cash == 0 || wheat == 0){
					cash = Math.random() * surplusCash;
					wheat = Math.random() * surplusWheat;
					bPayoff = (buyer.getCash()-cash*Math.pow(delta1, t-1))*(buyer.getWheat()+wheat*Math.pow(delta1, t-1));
				}
				
				double sPayoff = (seller.getCash()+cash*Math.pow(delta2, t-1))*(seller.getWheat()-wheat*Math.pow(delta2, t-1));

				if(sPayoff>sPayoffOrig){
					consensus = true;
				}

			} else {
				double sPayoff = (seller.getCash()+cash*Math.pow(delta2, t-1))*(seller.getWheat()-wheat*Math.pow(delta2, t-1));


				while(sPayoff <= sPayoffOrig || cash == 0 || wheat == 0){
					cash = Math.random() * surplusCash;
					wheat = Math.random() * surplusWheat;
					sPayoff = (seller.getCash()+cash*Math.pow(delta2, t-1))*(seller.getWheat()-wheat*Math.pow(delta2, t-1));
				}
				
				double bPayoff = (buyer.getCash()-cash*Math.pow(delta1, t-1))*(buyer.getWheat()+wheat*Math.pow(delta1, t-1));

				if(bPayoff>bPayoffOrig){
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
			System.out.println("They came to a consensus at time " + t);
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
			double amtToEach = (distributedWheat - totalWheat)/agents.size();

			while(leftover>0){
				for(int i=0; i<agents.size(); i++){
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
	* Prints the amounts each agent has
	*/
	public static void printAgentAmounts(){
		for(int i=0; i<agents.size(); i++){
			System.out.printf("Agent "+ agents.get(i).getID() + " has %.2f amount of cash and %.2f amount of wheat.", agents.get(i).getCash(), agents.get(i).getWheat());
			System.out.println();
		}

		System.out.println();
	}

}