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

		run();
	}

	public static void run(){
		ArrayList<Agent> total = cloneAgents(agents);

		//boolean equilibrium = false;
		int i = 0;

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
			}

			i++;
		}

		
	}


	private static ArrayList<Agent> cloneAgents(ArrayList<Agent> temp){
		ArrayList<Agent> toReturn = new ArrayList<Agent>();

		for(int i=0; i<temp.size(); i++){
			toReturn.add(temp.get(i));
		}

		return toReturn;
	}


	public static void haggling(Agent buyer, Agent seller){
		System.out.println("Agent " + buyer.getID() + " is the buyer and Agent " + seller.getID() + " is the seller.");
		//the deltas are randomly picked
		delta1 = Math.random();
		delta2 = Math.random();

		//these are the original values that the buyer and seller have
		double bPayoffOrig = buyer.getCash()*buyer.getWheat();
		double sPayoffOrig = seller.getCash()*seller.getWheat();

		System.out.println("These are the respective original payoffs: "+ bPayoffOrig + ", " + sPayoffOrig);

		double cash = 0;
		double wheat = 0;

		boolean consensus = false;
		int t = 1;

		while(!consensus && t <= maxTimeForHaggling){
			double surplusCash = buyer.getCash() - 0.5;
			double surplusWheat = seller.getWheat() - 0.5;

			cash = Math.random() * surplusCash;
			wheat = Math.random() * surplusWheat;

			//if it is an odd numbered time
			if(t%2 == 1){
				double bPayoff = (buyer.getCash()-cash*Math.pow(delta1, t-1))*(buyer.getWheat()+wheat*Math.pow(delta1, t-1));

				while(bPayoff <= bPayoffOrig){
					cash = Math.random() * surplusCash;
					wheat = Math.random() * surplusWheat;
				}
				
				double sPayoff = (seller.getCash()+cash*Math.pow(delta2, t-1))*(seller.getWheat()-wheat*Math.pow(delta2, t)-1);

				if(sPayoff>sPayoffOrig){
					consensus = true;
				}

			} else {
				double sPayoff = (seller.getCash()+cash*Math.pow(delta2, t-1))*(seller.getWheat()-wheat*Math.pow(delta2, t-1));


				while(sPayoff <= sPayoffOrig){
					cash = Math.random() * surplusCash;
					wheat = Math.random() * surplusWheat;
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
		}
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
			double amtToEach = (distributedWheat - totalWheat)/agents.size();

			for(int i=0; i<agents.size(); i++){
				agents.get(i).setWheat(agents.get(i).getWheat() - amtToEach);
				agents.get(i).setCash(1-agents.get(i).getWheat());
			}
		} else if(distributedWheat - totalWheat < 0){
			double amtToEach = (totalWheat - distributedWheat)/agents.size();

			for(int i=0; i<agents.size(); i++){
				agents.get(i).setWheat(agents.get(i).getWheat() + amtToEach);
				agents.get(i).setCash(1-agents.get(i).getWheat());
			}
		}
	}

}