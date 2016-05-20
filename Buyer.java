import Jama.*;

public class Buyer extends Agent {
	public static int overallID = 1;

	public Buyer(Matrix a){
		myID = overallID;
		overallID++;

		for(int i=0; i<a.getColumnDimension(); i++){
			if(a.get(myID, i)==1){
				myConnections.add(i+1);
			}
		}
	}
}