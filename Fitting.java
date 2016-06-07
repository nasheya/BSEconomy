

public class Fitting{
	public static void main(String[] args){
		double qj = Double.parseDouble(args[0]);
		double c = Double.parseDouble(args[1]); //this should be a number between 0 and 1

		for(double alpha = 0; alpha<=2; alpha+=0.05){
			double m = getMax(alpha, qj, c);
			System.out.println(m+", "+alpha);
		}
	}

	private static double getMax(double alpha, double qj, double c){
		return ((qj-1)*alpha*c +c)/(2*qj*qj);
	}
}