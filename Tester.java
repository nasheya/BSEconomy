import Jama.*;

public class Tester {
	public static void main(String[] args){
		double[][] arr = {{1,1,0,0},{1,1,0,0},{1,1,0,0},{0,0,1,1},{0,0,1,1}};
		Matrix a = new Matrix(arr);
		//Simulation set = new Simulation(0.25, 0.2, 0.1, 2, 5, 7, 0.25);
		Simulation2 set2 = new Simulation2(a);
		//Seller b  = new Seller(a);
		//Buyer c  = new Buyer(a);
		// for(int i=0; i<c.myConnections.size(); i++){
		// 	System.out.println(c.myConnections.get(i));
		// }
	}
}