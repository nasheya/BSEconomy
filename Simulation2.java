import Jama.*;
import java.util.ArrayList;

public class Simulation2 {
	
	static Matrix a;

	static ArrayList< ArrayList<Agent> > components = new ArrayList< ArrayList<Agent> >();
	static ArrayList<Agent> buyers = new ArrayList<Agent>();
	static ArrayList<Agent> sellers = new ArrayList<Agent>();

	
	public Simulation2(Matrix temp){
		a = temp;

		for(int i=0; i<a.getRowDimension(); i++){
			buyers.add(new Agent(Agent.Party.BUYER, i+1));
		}

		for(int i=0; i<a.getColumnDimension(); i++){
			sellers.add(new Agent(Agent.Party.SELLER, i+1));
		}

		findConnectedComponents();
		printComponents();
	}


	/**
	* Things to deal with: only one connection (okay if other person has only one connection), the seller's side, 
	* testing, if a player has all the connections
	*/
	public static void findConnectedComponents(){
		//do it for the buyers first
		for(int i=0; i<buyers.size(); i++){
			//if the buyer is already in a network
			if(buyers.get(i).ccNum>0){
				continue;
			}

			ArrayList<Agent> ccTemp;

			double[] row1 = a.getMatrix(i, i, 0, sellers.size()-1).getRowPackedCopy();

			//if the row only has one connection, skip over it (we'll deal with it later)
			if(!moreThan1(row1)){
				continue;
			} else {
				ccTemp = new ArrayList<Agent>();
			}

			for(int j=i+1; j<buyers.size(); j++){
				if(buyers.get(j).ccNum==0 && i!=j){
					//check that the first and last element are the same before doing anything
					if(a.get(j, 0) == a.get(i, 0) && a.get(j, sellers.size()-1) == a.get(i, sellers.size()-1)){
						double[] row2 = a.getMatrix(j, j, 0, sellers.size()-1).getRowPackedCopy();

						if(sameArray(row1, row2)){
							buyers.get(j).ccNum = components.size() + 1;
							ccTemp.add(buyers.get(j));
						}
					}
				}
			}

			if(ccTemp.size()!=0){
				buyers.get(i).ccNum = components.size() + 1;
				ccTemp.add(0, buyers.get(i));
				addNeighbors(ccTemp);
				components.add(ccTemp);
			}
		}

		findOtherDisconnectedComponents();
	}

	
	/**
	* This method adds all the neighbors in a list where only the buyer or seller connected components
	* have been added.
	*/
	private static void addNeighbors(ArrayList<Agent> temp){
		if(temp.get(0).myType == Agent.Party.BUYER){
			for(int j=0; j<sellers.size(); j++){
				if(a.get(temp.get(0).myID-1, j)==1){
					temp.add(sellers.get(j));
				}
			}
		} else {
			for(int j=0; j<buyers.size(); j++){
				if(a.get(j, temp.get(0).myID-1)==1){
					temp.add(buyers.get(j));
				}
			}
		}
	}


	/**
	* This method counts if there's more than one connection for a player.
	*/
	private static boolean moreThan1(double[] arr){
		int sum = 0;

		for(int i=0; i<arr.length; i++){
			if(arr[i]==1)
				sum++;

			if(sum>1)
				return true;
		}

		return false;
	}


	/**
	* This method checks if two players have the exact same connections with no extra connections.
	*/
	private static boolean sameArray(double[] arr1, double[] arr2){
		if(arr1.length!=arr2.length)
			return false;

		for(int i=0; i<arr1.length; i++){
			if(arr1[i]!=arr2[i])
				return false;
		}

		return true;
	}


	public static void printComponents(){
		for(int i=0; i<components.size(); i++){
			System.out.print("Component #" + (i+1) +": ");

			for(int j=0; j<components.get(i).size(); j++){
				if(components.get(i).get(j).myType == Agent.Party.BUYER)
					System.out.print("B"+components.get(i).get(j).myID+" ");
				else 
					System.out.print("S"+components.get(i).get(j).myID+" ");
			}

			System.out.println();
		}
	}

}