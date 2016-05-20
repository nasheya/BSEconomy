import java.util.ArrayList;
import Jama.*;

public class Simulation {
	ArrayList<Buyer> buyers = new ArrayList<Buyer>();
	ArrayList<Seller> sellers = new ArrayList<Seller>();
	//ArrayList< ArrayList<Integer> > pairsOriginal = new ArrayList< ArrayList<Integer> >();
	ArrayList< ArrayList<Integer> > pairs = new ArrayList< ArrayList<Integer> >();
	int numBuyers;
	int numSellers;

	public Simulation(Matrix a, double c, double d){
		numBuyers = a.getRowDimension();
		numSellers = a.getColumnDimension();

		//Initialize the list of buyers and sellers
		for(int i=0; i<a.getRowDimension(); i++){
			buyers.add(new Buyer(a));
		}

		for(int i=0; i<a.getColumnDimension(); i++){
			sellers.add(new Seller(a));
		}

		for(int i=0; i<a.getRowDimension(); i++){
			for(int j=0; j<a.getColumnDimension(); j++){
				ArrayList<Integer> pairTemp = new ArrayList<Integer>();
				pairTemp.add(i);
				pairTemp.add(j);
				//pairsOriginal.add(pairTemp);
				pairs.add(pairTemp);
			}
		}

		java.util.Collections.shuffle(pairs);

		//playGame();
	}

	public static void playGame(){
		for(int i=0; i<pairs.size(); i++){
			if(buyers.get(pairs.get(i).get(0)).isConnected(pairs.get(i).get(1))){
				//if they are connected
			} else {
				//if they are not connected
			}
		}

		trade();
	}
}