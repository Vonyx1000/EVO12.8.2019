import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;


public class parseUnpaired {
	
	private HashMap<Integer,ArrayList<Double>>  unpairedKey; 
		
		public parseUnpaired(File dict) throws FileNotFoundException{
			
			Scanner reader = new Scanner(dict);
			
			//creates the matrix format of the key
			unpairedKey = new HashMap<Integer,ArrayList<Double>>();
			
			while(reader.hasNext()){
				
				String current = reader.nextLine();
				ArrayList<Double> line = new ArrayList<Double>();
				
				//splits sbp_dict by values
				String[] split = current.split(",");
				
				//splits the arraylist by values
				for(int spot = 1; spot<split.length; spot++){
					
					line.add(Double.parseDouble(split[spot]));
					
				}
				
				unpairedKey.put(Integer.parseInt(split[0]),line);
	
			}
			
			reader.close();
		}
	
		public HashMap<Integer,ArrayList<Double>> showDICT(){
			return unpairedKey;
		}
			
		}

