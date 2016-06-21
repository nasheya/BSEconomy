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
		amtWheat = Math.random();
		amtCash = 1 - amtWheat;
		delta = Math.random();
		exponent = Math.round(Math.random()*10000.0)/10000.0;
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

	public double getDelta(){
		return delta;
	}

	public double getExponent(){
		return exponent;
	}

	public double getDivided(double numToDivide, boolean cash){
		if(cash){
			return ((double)Math.round(this.getCash()/numToDivide*10000.0))/10000.0;
		} else {
			return ((double)Math.round(this.getWheat()/numToDivide*10000.0))/10000.0;
		}
	}

	public void printAmounts(){
		System.out.printf("Agent "+ this.getID() + " has %.4f amount of cash, %.4f amount of wheat, and a %.4f exponent.", this.getCash(), this.getWheat(), this.getExponent());
		System.out.println();
	}

}