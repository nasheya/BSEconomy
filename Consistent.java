// Programmer: Joshua Hayes
// Date: 2016-06-01
//
// Details: Checks the consistency of a connected bipartite
// exchange economy graph under certain restrictions, such as
// market clearing and equal exchange rates.


import java.util.Random;
import java.util.Scanner;
import java.util.LinkedList;
import java.io.FileReader;
import java.io.FileNotFoundException;


class Consistent {
    // Set up the matrix. Rows and cols here used for random init. only.
    private static final int rows = 8;
    private static final int cols = 6;
    public static int matrix[][] = new int[rows][cols];

    public static void init_rand(double density) {
    // Randomly initialize the matrix
    // Parameter density: range(0,1)
        Random rand = new Random();
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                double chance = rand.nextDouble();
                if(chance < density) {
                matrix[i][j] = 1;
                }
            }
        }
    }

    public static void init_from_file(String filename) {
    // Initialize matrix from file.
        int row_count = 0;
        int col_count = 0;

        Scanner test = null; 
        try {
            test = new Scanner(new FileReader(filename));
            while(test.hasNextLine()) {
                row_count++;
                String line = test.nextLine();
                line = line.trim();
                if(line.length() > col_count) {
                    col_count = line.length();
                }
            }
        test.close();
        } catch(FileNotFoundException e) {
            System.out.println("File [" + filename + "] not found.");
        }
        
        //System.out.println("rows: " + row_count);
        //System.out.println("cols: " + col_count);
        matrix = new int[row_count][col_count];

        Scanner in = null;
        try {
            in = new Scanner(new FileReader(filename));
            int i = 0;
            while(in.hasNextLine()) {
                String line = in.nextLine();
                line = line.trim();

                for(int j = 0; j < line.length(); j++) {
                    char c = line.charAt(j);
                    int bit = (int) c - 48; // convert "1"/"0" ascii to int
                    matrix[i][j] = bit;
                }
                i++; // increment row
            }
        in.close();
        } catch(FileNotFoundException e) {
            //System.out.println("File [" + filename + "] not found.");
        }
    }

    public static void print_matrix(int[][] matrix) {
    // Print the matrix
        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public static LinkedList<String> col_combination_list() {
    // Creates a list of the bitstrings that represent all
    // possible combinations of column choices.
        LinkedList<String> list = new LinkedList<String>();
        int min = 1;
        int max = (int) Math.pow(2.0, matrix[0].length) - 1;

        for(int i = min; i <= max; i++) {
            String bitstring = Integer.toBinaryString(i);
            bitstring = uniform_bitstring_length(bitstring, matrix[0].length);
            list.add(bitstring);
            //System.out.println(bitstring);
        }
        return list;
    }

    public static String uniform_bitstring_length(String bitstring, int desired_length) {
    // Pads the bitstring with zeroes to the left so they are of uniform length.
        while(bitstring.length() < desired_length) {
            bitstring = "0" + bitstring;
        }
        return bitstring;
    }

    public static int get_num_inconsistencies(LinkedList<String> combination_list, boolean should_fix) {
    // Determines whether the graph is consistent.
    // Over all combinations of columns, create a matrix where only
    // those columns are "chosen", zeroing out all other columns.
    // Compare ratio of non-zero-rows to chosen column TO the ratio of
    // total rows to total columns. This determines consistency.
        int num_inconsistencies = 0;
        continue_fixing:
        for(int c = 0; c < combination_list.size(); c++) {
            String combination = combination_list.get(c);
            int non_zero_rows = 0;
            int chosen_cols = 0;

            for(int i = 0; i < combination.length(); i++) {
                if(combination.charAt(i) == '1') {
                    chosen_cols++;
                }
            }

            int temp_matrix[][] = new int[matrix.length][matrix[0].length];
            for(int i = 0; i < matrix.length; i++) {
                int sum_val = 0;
                for(int j = 0; j < matrix[0].length; j++) {
                    int bit = (int) combination.charAt(j) - 48; // 0 or 1
                    int val = matrix[i][j] * bit;
                    sum_val += val;
                    temp_matrix[i][j] = val;
                }
                if(sum_val != 0) {
                   non_zero_rows++; 
                }
            }

            double local_ratio = (double) non_zero_rows / chosen_cols;
            double global_ratio = (double) matrix.length / matrix[0].length;
            if(local_ratio < global_ratio) {
                if(should_fix) {
                // Fix inconsistency by increasing number of non-zero-rows in chosen column combination
                    for(int j = 0; j < matrix[0].length; j++) {
                        char chosen = combination.charAt(j);
                        if(chosen == '1') {
                            for(int i = 0; i < matrix.length; i++) {
                                if(matrix[i][j] == 0) {
                                    matrix[i][j] = 1;
                                    continue continue_fixing;
                                }
                            }
                        }
                    }
                } else {
                    num_inconsistencies++;
                    System.out.println("problem col comb: " + combination); // Inconsistent combination of columns
                    //print_matrix(temp_matrix);
                }
            }


            //System.out.println("nzr, ccol: " + non_zero_rows + " " + chosen_cols);
        }
        return num_inconsistencies;
    }

    public static void main(String[] args) {
        //init_rand(0.5);
        init_from_file("in3");
        print_matrix(matrix);
        LinkedList<String> list = col_combination_list();
        boolean should_fix = false;
        int num_problems = get_num_inconsistencies(list, should_fix);
        print_matrix(matrix);
        System.out.println("Problems: " + num_problems);
    }
}
