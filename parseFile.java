import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

public class parseFile{
	
	private ArrayList<Integer> positionV;
	private ArrayList<String> sequenceV;
	private Integer length;
	
	/**Creates a parsed file to be accepted by the main method
	 * @throws FileNotFoundException **/
	public parseFile(File newFile) throws FileNotFoundException{
				
		positionV = new ArrayList<Integer>(); 
		positionV.add(0); //fixes indexing 
		sequenceV = new ArrayList<String>();
		sequenceV.add("#"); //fixes indexing
		ArrayList<String> initArray = new ArrayList<String>(); 
		
		Scanner reader = new Scanner(newFile);
		
		//eliminates initial 4 lines of non-sequence related data
		reader.nextLine();
		reader.nextLine();
		reader.nextLine();
		reader.nextLine();
		
		//Begins sorting through sequence data
		//this gets rid of whitespace (ex spaces, tabs, etc.)
		while(reader.hasNext()){
			String ln = reader.nextLine();
			if(ln.matches("\\d+\\s+\\w\\s+\\d+")){
				String[] sa = ln.split("\\s+");
				String base = sa[1];
				int pos = Integer.valueOf(sa[2]);

				positionV.add(pos);
				sequenceV.add(base);
			}
			//initArray.add(reader.nextLine());
		}
		
//		int count = 0;
//		while(count < initArray.size()){
//			StringBuilder temp = new StringBuilder(initArray.get(count));
//
//			//deletes bp position and all spaces in each line
//			int inCount = 0;
//			while(temp.charAt(inCount) != ' '){
//				temp.deleteCharAt(inCount);
//			}
//			temp.deleteCharAt(0);
//
//			sequenceV.add(""+temp.charAt(0));
//
//			//deletes nucleotide and space after
//			temp.delete(0, 2);
//
//			//puts matching location sequence into a new arraylist called position
//			positionV.add(new Integer(temp.toString()));
//
//			count++;
//		}
		
		reader.close();		
	}
	
	//remvoes all noncanonicals from the sequence
	public void removeNonCanonicals(){
		
		int count = 0; 
		ArrayList<Integer> positionTemp = new ArrayList<Integer>(); 			
		//Cycles through the stored sequence
		while(count < positionV.size()){
				
			//If position is paired...
			Integer x;
			if((x = positionV.get(count)) != 0){
					
					//if base pairing follows Watson-Crick add pairing back into paired region
					if(LocCheck(sequenceV.get(count), sequenceV.get(x))){
						positionTemp.add(x);
					}
					else{
						
						//If position does not follow Watson-Crick, it is removed as a base-paired region
						positionTemp.add(0);
					}
					
			}else{
					
					//Adds zero if original position does not have pairing
					positionTemp.add(0);
				}
			
				count++;
			}
		
		positionV = positionTemp;
	
	}
	
	
	//returns created sequence vector
	public ArrayList<String> sequences(){
		return sequenceV; 
	}
	
	//returns created position vector
	public ArrayList<Integer> positions(){
		return positionV;
	}
	
	//returns length of the sequence 
	public int length(){
		
		int count = 0;
		for(int i = 0; i < positionV.size(); i++){
			if(positionV.get(i)!=0){
				count++;
			}
		}
		return positionV.size();
	}
	
	private boolean LocCheck(String bp1, String bp2){
		
		if(bp1.equals("C") && bp2.equals("G")){
            return true; 
        }
        else if(bp1.equals("G") && (bp2.equals("C") || bp2.equals("U"))){
        	return true; 
        }
        else if(bp1.equals("A") && bp2.equals("U")){
        	return true; 
        }
        else if(bp1.equals("U") && (bp2.equals("A") || bp2.equals("G"))){
        	return true;
        }
        else{
        	return false;
        }
	}
}