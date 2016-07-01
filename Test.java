import java.io.*;

public class Test {
    public static void main(String [] args) {

        // The name of the file to open.
        String fileName = "DataFiles/BeforeUtility.txt";
        String fileName2 = "DataFiles/AfterUtility.txt";

        // This will reference one line at a time
        String line = null;
        String line2 = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            FileReader fileReader2 = 
                new FileReader(fileName2);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader2 = 
                new BufferedReader(fileReader2);

            while((line = bufferedReader.readLine()) != null && (line2 = bufferedReader2.readLine()) != null) {
                if(Double.parseDouble(line2)<Double.parseDouble(line)){
                    System.out.println(line+" "+line2);
                }
            }   

            // Always close files.
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
    }
}