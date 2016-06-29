/**********
* MULTI-ROUND SIMULATION (FIXED COSTS, NASH BARGAINING)
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
	static double govtWheat = 0;
	static double govtCash = 0;

	static double cost = 0.025;

	public Simulation(int numAgents){
		for(int i=0; i<numAgents; i++){
			//give each agent an id number starting from 0 to n-1
			agents.add(new Agent(i));
		}

		distributeCashAndWheat();

		try{
			BufferedWriter before = new BufferedWriter(new FileWriter("BeforeCashWheat.txt", true));
			PrintWriter before1 = new PrintWriter(before);
			BufferedWriter beforeU = new BufferedWriter(new FileWriter("BeforeUtility.txt", true));
			PrintWriter beforeU1 = new PrintWriter(beforeU);

			for(int i=0; i<agents.size(); i++){
				before1.println(agents.get(i).getCash() + "\t" + agents.get(i).getWheat());
				beforeU1.println(agents.get(i).getUtility());
			}

			before1.close();
			beforeU1.close();
		} catch (IOException e) {
			System.out.println("Error error!");
		}

		printAgentAmounts();

		run();

		printAgentAmounts();

		try{
			BufferedWriter utility = new BufferedWriter(new FileWriter("AfterUtility.txt", true));
			BufferedWriter amount = new BufferedWriter(new FileWriter("TotalAmount.txt", true));
			BufferedWriter after = new BufferedWriter(new FileWriter("AfterCashWheat.txt", true));
			BufferedWriter govt = new BufferedWriter(new FileWriter("GovtCashWheat.txt", true));
			PrintWriter utility1 = new PrintWriter(utility);
			PrintWriter amount1 = new PrintWriter(amount);
			PrintWriter after1 = new PrintWriter(after);
			PrintWriter govt1 = new PrintWriter(govt);

			for(int i=0; i<agents.size(); i++){
				utility1.println(agents.get(i).getUtility());
				after1.println(agents.get(i).getCash() + "\t" + agents.get(i).getWheat());
				amount1.println((agents.get(i).getCash() + agents.get(i).getWheat()));
			}

			govt1.println(govtCash + " " + govtWheat);

			utility1.close();
			amount1.close();
			after1.close();
			govt1.close();
		} catch (IOException e) {
			System.out.println("Error error!");
		}

		System.out.println(govtCash+ " " + govtWheat);
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

				Agent one = total.get(index);
				Agent two = total.get(indexPair);

				//if someone has more cash than wheat and the other person also has more wheat than cash, then trade
				if(one.getDivided(1, true)>one.getDivided(1, false) && two.getDivided(1, false)>two.getDivided(1, true)){
					haggling(one, two);

					maxUtility(particpants, one);
					maxUtility(particpants, two);

					total.remove(index);

					if(index<indexPair){
						total.remove(indexPair-1);
					} else {
						total.remove(indexPair);
					}
				//or vice versa
				} else if(one.getDivided(1, false)>one.getDivided(1, true) && two.getDivided(1, true)>two.getDivided(1, false)){
					haggling(two, one);

					maxUtility(particpants, one);
					maxUtility(particpants, two);

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
			if(total.get(i).getDivided(2, false)<total.get(i).getDivided(2, true)){
				moreWheat = false;
			}

			if(total.get(i).getDivided(2, true)<total.get(i).getDivided(2, false)){
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
		double delta1 = buyer.getDelta();
		double delta2 = seller.getDelta();

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
		double alpha = 0;

		boolean consensus = false;
		int t = 1;

		double surplusCash = buyer.getCash() - (buyer.getCash()+buyer.getWheat())/2;
		double surplusWheat = seller.getWheat() - (seller.getCash()+seller.getWheat())/2;

		while(!consensus && t <= maxTimeForHaggling){
			cash = Math.random() * surplusCash;
			wheat = Math.random() * surplusWheat;
			alpha = maxAlpha(buyer, seller, cash, wheat, t);

			//if it is an odd numbered time
			if(t%2 == 1){
				double bPayoff = (buyer.getCash()-cash*Math.pow(delta1, t-1))*(buyer.getWheat()+wheat*Math.pow(delta1, t-1)-alpha*cost*(wheat/cash));

				int i = 0;

				//keep on trying to find an amount that works for the buyer in terms of payoff
				while(bPayoff <= bPayoffOrig && i<100){
					cash = Math.random() * surplusCash;
					wheat = Math.random() * surplusWheat;
					alpha = maxAlpha(buyer, seller, cash, wheat, t);
					bPayoff = (buyer.getCash()-cash*Math.pow(delta1, t-1))*(buyer.getWheat()+wheat*Math.pow(delta1, t-1)-alpha*cost*(wheat/cash));
					i++;
				}
				
				//see if the amount works for the seller
				double sPayoff = (seller.getCash()+cash*Math.pow(delta2, t-1)-(1-alpha)*cost)*(seller.getWheat()-wheat*Math.pow(delta2, t-1));

				if(sPayoff>sPayoffOrig && bPayoff > bPayoffOrig){
					consensus = true;
				}

			} else {
				double sPayoff = (seller.getCash()+cash*Math.pow(delta2, t-1)-(1-alpha)*cost)*(seller.getWheat()-wheat*Math.pow(delta2, t-1));

				int i = 0;

				//keep on trying to find an amount that works for the seller in terms of payoff
				while(sPayoff <= sPayoffOrig && i<100){
					cash = Math.random() * surplusCash;
					wheat = Math.random() * surplusWheat;
					alpha = maxAlpha(buyer, seller, cash, wheat, t);
					sPayoff = (seller.getCash()+cash*Math.pow(delta2, t-1)-(1-alpha)*cost)*(seller.getWheat()-wheat*Math.pow(delta2, t-1));
					i++;
				}
				
				//see if the amount works for the buyer
				double bPayoff = (buyer.getCash()-cash*Math.pow(delta1, t-1))*(buyer.getWheat()+wheat*Math.pow(delta1, t-1)-alpha*cost*(wheat/cash));

				if(sPayoff>sPayoffOrig && bPayoff>bPayoffOrig){
					consensus = true;
				}
			}

			t++;
		}

		if(consensus == true){
			buyer.setCash(buyer.getCash() - cash);
			buyer.setWheat(buyer.getWheat() + wheat -alpha*cost*(wheat/cash));
			govtWheat += alpha*cost*(wheat/cash);
			seller.setCash(seller.getCash() + cash -  (1-alpha)*cost);
			seller.setWheat(seller.getWheat() - wheat);
			govtCash += (1-alpha)*cost;
			System.out.println("They came to a consensus at time " + (t-1));
			System.out.printf("The buyer gave %.2f cash and the seller gave %.2f wheat to the buyer.", cash, wheat);
			System.out.println();
			System.out.println("Alpha was "+ alpha);
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
	* Prints the amounts each agent has
	*/
	public static void printAgentAmounts(){
		for(int i=0; i<agents.size(); i++){
			agents.get(i).printAmounts();
		}

		System.out.println();
	}


	/**
	* Finds out if the agent given have reached its maximum utility, and if so, removes it from the list
	*/
	private static boolean maxUtility(ArrayList<Agent> particpants, Agent one){
		boolean removed = false;

		if(one.getDivided(1, false) == one.getDivided(1, true)){
			int index = particpants.indexOf(one);
			particpants.remove(particpants.indexOf(one));
			System.out.println("Agent " + one.getID() + " got removed.");
			removed = true;
		}

		return removed;
	}


	private static double maxAlpha(Agent one, Agent two, double cash, double wheat, int time){
		double alpha = 0;
		double maxVal = Integer.MIN_VALUE;

		for(double i=0; i<=1; i+=0.05){
			double val = equation(one, two, cash, wheat, time, i);
			if(maxVal<val){
				maxVal = val;
				alpha = i;
			}
		}

		return alpha;
	}


	private static double equation(Agent one, Agent two, double cash, double wheat, int time, double alpha){
		double part11 = one.getCash()-Math.pow(one.getDelta(), time-1)*cash;
		double part12 = one.getWheat()+Math.pow(one.getDelta(), time-1)*wheat - alpha*cost*(wheat/cash);
		double part13 = Math.sqrt(part11*part12);
		double part14 = Math.sqrt(one.getCash()*one.getWheat());
		double part15 = part13 - part14;

		double part21 = two.getCash()+Math.pow(two.getDelta(), time-1)*cash - (1-alpha)*cost;
		double part22 = two.getWheat()-Math.pow(two.getDelta(), time-1)*wheat;
		double part23 = Math.sqrt(part21*part22);
		double part24 = Math.sqrt(two.getCash()*two.getWheat());
		double part25 = part23 - part24;

		return part15*part25;
	}

}