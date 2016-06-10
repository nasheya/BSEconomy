import Jama.*;
import java.util.ArrayList;
import java.util.Random;

public class Simulation{

	static Random rng = new Random();

	static ArrayList<Agent> agents = new ArrayList<Agent>();

	static double maxTimeForHaggling = 10;

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

		while(total.size()>1){
			//pick an agent index number based on a uniform distribution
			int index = Random.nextInt(total.size());

			//pick agent here based on an uniform distribution but conditional probability function later
			int indexPair = Random.nextInt(total.size());

			//do not let them be the same number
			while(indexPair == index){
				indexPair = Random.nextInt(total.size());
			}

			Agent one = total.get(index);
			Agent two = total.get(indexPair);

			if(one.getCash()>two.getCash() || two.getWheat()>one.getWheat()){
				haggling(one, two);
			} else {
				haggling(two, one);
			}
		}
	}


	public static ArrayList<Agent> cloneAgents(ArrayList<Agent> temp){
		ArrayList<Agent> toReturn = new ArrayList<Agent>();

		for(int i=0; i<temp.size(); i++){
			toReturn.add(temp.get(i));
		}

		return toReturn;
	}


	public static void haggling(Agent buyer, Agent seller){
		boolean consensus = false;
		double t = 0;

		while(!consensus && t < maxTimeForHaggling){

		}
	}


	/**
	* This method distributes the amount of wheat and cash such that each agent 
	* has a total unit of good and that there is only 0.5 * numAgents amount of
	* cash and wheat in the entire system.
	*/
	private static distributeCashAndWheat(){
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