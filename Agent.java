import java.util.ArrayList;
import Jama.*;

public class Agent {
	private int myID;
	private double amtWheat;
	private double amtCash;
	private double delta;


	public Agent(){}

	public Agent(int id){
		myID = id;
		amtWheat = Math.random();
		amtCash = 1 - amtWheat;
		delta = Math.random();
	}

	public void setWheat(double wheat){
		amtWheat = wheat;
	}

	public void setCash(double cash){
		amtCash = cash;
	}

	public double getWheat(){
		return amtWheat;
	}

	public double getCash(){
		return amtCash;
	}

	public int getID(){
		return myID;
	}

	public double getUtility(){
		return findUtility(amtCash, amtWheat);
	}

	public double findUtility(double cash, double wheat){
		return Math.sqrt(wheat)*Math.sqrt(cash);
	}

	public double getDelta(){
		return delta;
	}

	/**
	* This method gives you the rounded amount of some divided amount of 
	* cash or wheat (depending on the argument).
	* Ex. If you want half of the cash, it will give you the rounded amount 
	* of half of the cash
	*/
	public double getRoundedAmount(double numToDivide, boolean cash){
		if(cash){
			return ((double)Math.round(this.getCash()/numToDivide*10000.0))/10000.0;
		} else {
			return ((double)Math.round(this.getWheat()/numToDivide*10000.0))/10000.0;
		}
	}

	public void printInfo(){
		System.out.printf("Agent "+ this.getID() + " has %.4f amount of cash and %.4f amount of wheat.", this.getCash(), this.getWheat());
		System.out.println();
	}

}