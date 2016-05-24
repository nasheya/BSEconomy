import Jama.*;

public class Tester {
	public static void main(String[] args){
		double[][] arr = {{0,0,0},{1,1,1}};
		Matrix a = new Matrix(arr);
		Simulation set = new Simulation(0.25, 0.3, 0.25, 5, 5, 7, 0.6);
		//Seller b  = new Seller(a);
		//Buyer c  = new Buyer(a);
		// for(int i=0; i<c.myConnections.size(); i++){
		// 	System.out.println(c.myConnections.get(i));
		// }
	}
}