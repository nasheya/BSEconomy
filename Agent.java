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
		return findUtility(amtWheat, amtCash);
	}

	public double findUtility(double wheat, double cash){
		return Math.sqrt(wheat)*Math.sqrt(cash);
	}

	public double getDelta(){
		return delta;
	}
}