import java.util.ArrayList;
import Jama.*;

public class Agent {
	private int myID;
	private double amtWheat;
	private double amtCash;
	private double delta;
	private double exponent;


	public Agent(){}

	public Agent(int id){
		myID = id;
		amtWheat = Math.random(); //usually replaced later
		amtCash = 1 - amtWheat; //usually replaced later
		delta = Math.random();
		//exponent = Math.round(Math.random()*10000.0)/10000.0;
		exponent = 0.5;
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
		return Math.pow(wheat, 1-exponent)*Math.pow(cash, exponent);
	}

	public double findChangeInUtility(double dCash, double dWheat, boolean buyer){
		if(buyer){
			return Math.pow(amtCash-dCash, exponent)*Math.pow(amtWheat+dWheat, 1-exponent);
		} else {
			return Math.pow(amtCash+dCash, exponent)*Math.pow(amtWheat-dWheat, 1-exponent);
		}
		
	}

	public double getDelta(){
		return delta;
	}

	public double getExponent(){
		return exponent;
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
		System.out.printf("Agent "+ this.getID() + " has %.4f amount of cash, %.4f amount of wheat, and a %.4f exponent.", this.getCash(), this.getWheat(), this.getExponent());
		System.out.println();
	}
}