import java.util.ArrayList;
import Jama.*;

public class Agent {
	public enum Party { BUYER, SELLER }

	ArrayList<Integer> myConnections = new ArrayList<Integer>();

	public int myID;
	double myAmount = 1;
	Party myType;
	public int numConnections;
	double myBackpack;
	double costsToPay;

	//This is the connected component number
	public int ccNum;

	public Agent(){}

	public Agent(Party type, int id){
		myID = id;
		myType = type;
		numConnections = 0;
		myBackpack = 0;
		costsToPay = 0;
		ccNum = 0;
	}


	/**
	* Adds the amount to the backpack
	*/
	public void addToBackpack(double amt){
		myBackpack+=amt;
	}


	/**
	* Adds to the costs the agent has to pay
	*/
	public void addToCosts(double amt){
		costsToPay+=amt;
	}


	/**
	* Sets the costs the agent has to pay
	*/
	public void resetCosts(){
		costsToPay = 0;
	}


	/**
	* Sets the costs the agent has to pay
	*/
	public void subtractCosts(){
		myBackpack-=costsToPay;
	}


	/**
	* This method finds if the agent is connected to the other agent given.
	*/
	public boolean isConnected(int agentID){
		return this.bSearch(0, myConnections.size()-1, agentID);
	}


	/**
	* This method finds the agent connection by a binary search.
	*/
	private boolean bSearch(int start, int end, int id){
		if(start==end){
			if(myConnections.get(start)==id)
				return true;
			return false;
		}

		int mid  = (start+end)/2;

		if(myConnections.get(mid)==id){
			return true;
		} else {
			if(myConnections.get(mid)>id){
				return bSearch(start, mid-1, id);
			} else {
				return bSearch(mid+1, end, id);
			}
		}
	}
}