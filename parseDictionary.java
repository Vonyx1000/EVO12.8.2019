import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class parseDictionary {

	private HashMap<Integer, ArrayList<Integer>> key;
	private BufferedReader reader; 
	
	public parseDictionary(File dictionary) throws IOException{
		
		key = new HashMap<Integer, ArrayList<Integer>>();
		reader = new BufferedReader(new FileReader(dictionary));
	}
	
	public HashMap<Integer, ArrayList<Integer>> readDictionary() throws IOException{
		
		String current = reader.readLine();
		reader.readLine();
		
		while(reader.ready()){
			current = reader.readLine();
			
			int spot = 0; 
			StringBuilder key1 = new StringBuilder();
			StringBuilder key2 = new StringBuilder();
			
			StringBuilder map = new StringBuilder();
			
			String temp = "" + current.charAt(spot);
			
			while(!temp.equals(":")){
				key1.append(temp);
				spot++; 
				temp = "" + current.charAt(spot);
			}
			
			spot++;
			temp = ""+ current.charAt(spot);
			
			while(!temp.equals("[")){
				key2.append(temp);
				spot++;
				temp = "" + current.charAt(spot);
			}
			
			while(spot < current.length()){
				map.append(current.charAt(spot));
				spot++;
			}
			
			map.delete(0,2);
			StringBuilder nextValue = new StringBuilder();
			int count = 0;
			ArrayList<Integer> keyValues = new ArrayList<Integer>();
		
			while(count < 16){
				
				int counter = 2;
				int hundred = 100;
				
				
				while(counter < 4){
					nextValue.append(map.charAt(counter));
					counter++;
				}
			
				map.delete(0, 8);
				String value = nextValue.toString();
				if(value.equals("xx")){
					keyValues.add(hundred);
				}
				else{
					keyValues.add(Integer.parseInt(value));
				}
				count++;
				
				
				nextValue.delete(0,2);

			}
			
			
			key.put(Integer.parseInt(key1.toString()),keyValues);
			key.put(Integer.parseInt(key2.toString()),keyValues);
			
		}
		
		reader.close();
		return key;
	}
	
}
