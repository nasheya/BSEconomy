/**********
* MULTI-ROUND SIMULATION (NO COSTS)
* This simulation simulates haggling between a defined amount of agents. It first distributes an amount of cash and wheat 
* randomly but each agent has 1 unit of good and there is a total of (1-x) * numAgents amount of cash and x * numAgents amount 
* of wheat in the entire system (0<x<1). Then two agents are picked randomly and depending on if one has more wheat than they 
* have utlity for and one has more cash than they have utility for (dpeending on the Cobb-Douglas Utility function), they will 
* randomly choose to trade some amount depending on the Rubenstein haggling model.
*/


import java.util.ArrayList;
import java.util.Random;
import java.io.*;

public class Simulation{

	static Random rng = new Random();

	ArrayList<Agent> agents = new ArrayList<Agent>();

	static int maxRounds;

	// Transaction rates
	static PrintWriter amts1;
	static PrintWriter rates;

	// Transaction rates
	int amtAgree = 0;
	int amtTotal = 0;
	int tracker = 0;

	public Simulation(int numAgents, int rounds, double x, int integer){
		maxRounds = rounds;

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
				double wc1 = round(one.getRoundedAmount(1, false)/one.getRoundedAmount(1, true), 4);
				double exp1 = round(one.getExponent()/(1-one.getExponent()) , 4);
				double wc2 = round(two.getRoundedAmount(1, false)/two.getRoundedAmount(1, true), 4);
				double exp2 = round(two.getExponent()/(1-two.getExponent()) , 4);
				double ratio1 = round(wc1*exp1, 4);
				double ratio2 = round(wc2*exp2, 4);

				//if someone has more cash than wheat and the other person also has more wheat than cash, then trade
				if(ratio1 < ratio2){
					haggling(one, two);

					removeAgentsFromHaggling(total, index, indexPair);

				} else if(ratio1 > ratio2){
					haggling(two, one);

					removeAgentsFromHaggling(total, index, indexPair);

				} else {
					System.out.println("Agents " + one.getID() + " and " + two.getID() + " could not trade.");
					one.printInfo();
					two.printInfo();
					System.out.println();

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

		double totalCash = round(buyer.getCash() + seller.getCash(), 4);
		double totalWheat = round(buyer.getWheat() + seller.getWheat(), 4);

		double originalBC = buyer.getCash();
		double originalSW = seller.getWheat();
		boolean consensus = false;

		//assuming it's the same utility curve so it doesn't matter what you choose
		if(buyer.getUtility()+seller.getUtility()<buyer.findUtility(totalCash, totalWheat)){
			double bu = buyer.findUtility(round(buyer.getCash()/totalCash, 4), round(buyer.getWheat()/totalWheat, 4));
			double su = seller.findUtility(round(seller.getCash()/totalCash, 4), round(seller.getWheat()/totalWheat, 4));

			double buA = buyer.findUtility(round((1+bu-su)*totalCash/2, 4), round((1+bu-su)*totalWheat/2, 4));
			double suA = seller.findUtility(round((1+su-bu)*totalCash/2, 4), round((1+su-bu)*totalWheat/2, 4));

			if(buA > buyer.getUtility() && suA > seller.getUtility()){
				double rate = (originalBC-round((1+bu-su)*totalCash/2, 4))/(originalSW-round((1+su-bu)*totalWheat/2, 4));

				if(!(Double.isNaN(rate) || rate==0)){
					consensus = true;

					buyer.setCash(round((1+bu-su)*totalCash/2, 4));
					buyer.setWheat(round((1+bu-su)*totalWheat/2, 4));

					seller.setCash(round((1+su-bu)*totalCash/2, 4));
					seller.setWheat(round((1+su-bu)*totalWheat/2, 4));

					amtAgree++;
					rates.println(amtAgree+" "+rate);
				}
			}

		} else {
			consensus = false;
		}


		if(consensus == false){
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
		double wc = round(total.get(0).getRoundedAmount(1, false)/total.get(0).getRoundedAmount(1, true), 4);
		double exp = round(total.get(0).getExponent()/(1-total.get(0).getExponent()) , 4);
		double ratio = round(wc*exp, 4);

		//if *everybody* has an abundance of wheat or cash, don't trade
		for(int i=1; i<total.size(); i++){
			double wc1 = round(total.get(i).getRoundedAmount(1, false)/total.get(i).getRoundedAmount(1, true), 4);
			double exp1 = round(total.get(i).getExponent()/(1-total.get(i).getExponent()) , 4);
			double ratio1 = round(wc1*exp1, 4);

			if(ratio1!=ratio){
				return true;
			}
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