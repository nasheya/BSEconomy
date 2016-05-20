import Jama.*;

public class Seller extends Agent {
	public static int overallID = 1;

	public Seller(Matrix a){
		myID = overallID;
		overallID++;

		for(int i=0; i<a.getRowDimension(); i++){
			if(a.get(i, myID)==1){
				myConnections.add(i+1);
			}
		}
	}
}