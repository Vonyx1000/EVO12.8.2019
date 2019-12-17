import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Checker {

	HashMap<RnaNode, Integer> CheckMap = new HashMap<>();

	private static Constants settings;

	public Checker(Constants constant) {
		settings = constant;
	}

	//Checks if correcting mutation has been found
	//Returns true when correcting mutation has been found
	public boolean foundCorrection(RnaNode<String> current, String bp1, int position) {
		//if (!settings.iterations()) {
			int pos2 = settings.positionV().get(settings.correctingPosition());
			int pos = settings.correctingPosition();
			if (position == pos || position == pos2) {

				String bp2;

				if (position == pos) {
					bp2 = settings.sequenceV().get(pos2);
				} else {
					bp2 = settings.originalMutationNucleotide();
				}

				if (settings.modelType() == 2) {
					return complexLocCheck(current);
				}
				else if (settings.modelType() == 3) {
					return unpairedCheck(current);
				}
				else {
					return LocCheck(bp1, bp2);
				}
			}

			else {
				return false;
			}
		}

//	    else{
//			if (CheckMap.isEmpty()) { //if HashMap has yet to be filled out (0th iteration), run as normal
//				int pos2 = settings.positionV().get(settings.correctingPosition());
//				int pos = settings.correctingPosition();
//				if (position == pos || position == pos2) {
//
//					String bp2;
//
//					if (position == pos) {
//						bp2 = settings.sequenceV().get(pos2);
//					} else {
//						bp2 = settings.originalMutationNucleotide();
//					}
//					if (settings.modelType() == 2) {
//						return complexLocCheck(current);
//					} else if (settings.modelType() == 3) {
//						return unpairedCheck(current);
//					} else {
//						return LocCheck(bp1, bp2);
//					}
//				} else {
//					return false;
//				}
//			}
//			else { //not on 0th iteration, so can check HashMap
	//MAY BE UNNCESSARY DUE TO TREEBUILDER STORING CORRECTING POSITION AND ORIGINAL MUTATION
//				for (Map.Entry mapElement : CheckMap()) { //iterate through HashMap until find mutation that has yet to be compensated for
//					if (mapElement.getValue() == "1"){ //if the current noncanonical has yet to be compensated for
//						String key = (String) mapElement.getKey();
//						System.out.println(key);
//						String[] dataPositionBoolean = key.split();
//						String noncanData = dataPositionBoolean[0];
//						String noncanPosition = dataPositionBoolean[1];
//					}
//
//
//
//				}
//			}
//		}
//		}
	//Viability of base pair follows watson-crick + GU base pairing
	//Returns true if the change is viable
	public boolean simpleMutation(RnaNode<String> current){
		
		String bp1 = current.getData();
		int pos2 = settings.positionV().get(current.getPosition());
		String bp2 = settings.sequenceV().get(pos2); 
		
		if(LocCheck(bp1, bp2)){
			return true;
		}else{
			
			if(pos2 == 0){
				current.setSBP();
				return true;
			}
			else{
				current.changed();
				//CheckMap.put(current, 1); // erroneous result, should be (if the mutation is noncanonical, then add it to the HashMap)
				return false; 
			}

		}

	}


	//Viability of based pair based on the bp dictionary or Watson-Crick + GU if position not in dictionary
	//Returns true if the change is viable
	public boolean complexMutation(RnaNode<String> current){
		
		
		//pos1 is current position, pos2 is matched position
		int pos1 = current.getPosition();
		int pos2 = settings.positionV().get(pos1);
		String bp1 = current.getData();
		String bp2 = settings.sequenceV().get(pos2);
		
		//bpdictionary is stored small:large therefore bp1 and bp2 needs to checked
		if(pos1 > pos2){
			pos1 = pos2; 
			bp1 = bp2; 
			
			pos2 = current.getPosition();
			bp2 = current.getData();
		}
		
		//use bpdictionary if positions are defined
		if(settings.bpDict().containsKey(pos1)){
			
			ArrayList<Integer> percentages = settings.bpDict().get(pos1);
			int pos = (0*this.encode(bp1)) + this.encode(bp2);
			
			//roll the die, if percentage rolled is less than percentage given by the dictionary than the mutation will be allowed
			int rand = (int) (Math.random()*100);
			
			if(rand < percentages.get(pos)){
				return true;
			}else{
				current.changed();
				return false; 
			}
			
		}
		else{
			
			return simpleMutation(current);
			
		}
		
	}
	
	//Viability of based pair based on bp dictionary + sbp dictionary or Watson-Crick + GU if position not in dictionary
	//Returns true if the change is viable
	public boolean unpairedCheck(RnaNode<String> current){
		
		//pos1 is current position, pos2 is matched position
		int pos1 = current.getPosition();
		int pos2 = settings.positionV().get(pos1);
		String bp2 = settings.sequenceV().get(pos2);
				
		//bpdictionary is stored small:large therefore bp1 and bp2 needs to checked
		if(pos1 > pos2){
			pos1 = pos2; 

			pos2 = current.getPosition();
			bp2 = current.getData();
		}
				
		//If position is unpaired check using sbpDICT
		if(pos1 == 0){
			
			current.setSBP();
			ArrayList<Double> percentages = settings.sbpDict().get(pos2); 
			int pos = this.encode(bp2);
			
			int rand = (int) (Math.random()*100); 
			
			//returns true if roll is less than given percentage
				if(rand < percentages.get(pos)){
					return true;
				}else{
					current.changed();
					return false; 
				}		
			}
		else{
			return complexLocCheck(current);
		}
	}
	
	//Checks for Watson-Crick and GU base pairs
	//Returns true if base pair is a match
	public boolean LocCheck(String bp1, String bp2){
		
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
	
	public boolean complexLocCheck(RnaNode<String> current){
		
		
		//pos1 is current position, pos2 is matched position
		int pos1 = current.getPosition();
		int pos2 = settings.positionV().get(pos1);
		String bp1 = current.getData();
		String bp2;   
	    if(pos2 == settings.correctingPosition()){
	    	bp2 = settings.originalMutationNucleotide();
	    }else{
	    		bp2 = settings.sequenceV().get(pos2);
	    		
	    }
		
		//bpdictionary is stored small:large therefore bp1 and bp2 needs to checked
		if(pos1 > pos2){
			pos1 = pos2; 
			bp1 = bp2; 
			
			pos2 = current.getPosition();
			bp2 = current.getData();
		}
		
		//use bpdictionary if positions are defined
		if(settings.bpDict().containsKey(pos1)){
			
			ArrayList<Integer> percentages = settings.bpDict().get(pos1); 
			int pos = (this.rencode(bp1,bp2));
			
			//roll the die, if percentage rolled is less than percentage given by the dictionary than the mutation will be allowed
			int rand = (int) (Math.random()*100); 
			if(rand < percentages.get(pos)){
				return true;
			}else{
				current.changed();
				return false; 
			}
			
		}
		else{
			
			return simpleMutation(current);
			
		}
		
	}
	
	//Encodes the letters for their number values for Dictionaries
	private Integer encode(String nuc){
		
		if(nuc.equals("A")){
			return 0;
		}
		else if(nuc.equals("C")){
			return 1;
		}
		else if(nuc.equals("G")){
			return 2;
		}
		else{
			return 3; 
		}
		
	}


	
	private Integer rencode(String nu1, String nu2){
		
		if(nu1.equals("A") && nu2.equals("A")){
			return 0;
		}
		else if(nu1.equals("A") && nu2.equals("C")){
			return 1;
		}	
		else if(nu1.equals("A") && nu2.equals("G")){
			return 2;
		}
		else if(nu1.equals("A") && nu2.equals("U")){
			return 3;
	    }
		else if(nu1.equals("C") && nu2.equals("A")){
			return 4;
		}
		else if(nu1.equals("C") && nu2.equals("C")){
			return 5;
		}
		else if(nu1.equals("C") && nu2.equals("G")){
			return 6;
		}
		else if(nu1.equals("C") && nu2.equals("U")){
			return 7;
		}
		else if(nu1.equals("G") && nu2.equals("A")){
			return 8;
		}
		else if(nu1.equals("G") && nu2.equals("C")){
			return 9;
		}
		else if(nu1.equals("G") && nu2.equals("G")){
			return 10;
		}
		else if(nu1.equals("G") && nu2.equals("U")){
			return 11;
		}
		else if(nu1.equals("U") && nu2.equals("A")){
			return 12;
		}
		else if(nu1.equals("U") && nu2.equals("C")){
			return 13;
		}
		else if(nu1.equals("U") && nu2.equals("G")){
			return 14;
		}
		else {
			return 15;
		}
	}
	}