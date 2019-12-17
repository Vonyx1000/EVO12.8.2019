import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Constants {
	
	private Integer length; //stores the length of the sequence
	private ArrayList<String> sequenceV; //stores the nucleotides in order
	private ArrayList<Integer> positionV; //stores the matching nucleotide positions
	private String originalMutationNucleotide; //stores the nucleotide of the original (root) mutation
	private Integer correctingPosition; //stores the position that should be checked for the compensating change 
	private Integer maxLevel; //max level of tree, non inclusive (root level is 0)
	private Integer modelType; //determines what model to use
	private Integer fitness; //stores the number of possible mismatches
	private boolean details; //if true runDetails is generated
	private boolean endRun; //if true the run ends after the first compensating change is found
	private HashMap<Integer, ArrayList<Integer>> bpDICT; //stores the base paired Dictionary
	private HashMap<Integer, ArrayList<Double>> sbpDICT; //stores the single-base paired dictionary
	private boolean iterations; //stores whether or not to run iterations, true if to run
	private Integer iterationCount;
	private Integer iterationCountB;
	private boolean type; //if true mutation is randomly generated
	private RnaNode<String> starter;
	
	public Constants() throws IOException{
		
		length = 0;
		sequenceV = new ArrayList<String>();
		positionV = new ArrayList<Integer>(); 
		correctingPosition = 0; 
		maxLevel = 0; 
		originalMutationNucleotide = "";
		endRun = true; //By default program stops at first mutation found
		modelType = 1; //Default model type is model 1
		fitness = 0; //Every sequence starts with 1 (original) mismatch
		details = false; //Run details report does not run by default
		iterations = false; //set to false as default
		type = true; //By default
		starter = new RnaNode<String>();
		iterationCount = 0; //number of iteration trials allowed
		
	}
	
	public void bpDictionary(File bpDictionary) throws IOException{
		bpDICT = new parseDictionary(bpDictionary).readDictionary();
	}
	
	public void sbpDictionary(File sbpDictionary) throws FileNotFoundException{
		sbpDICT = new parseUnpaired(sbpDictionary).showDICT();
	}
	
	//METHODS TO SET
	public void setLength(int setLength){
		length = setLength;
	}
	
	public void setPositionV(ArrayList<Integer> setPositionV){
		positionV = setPositionV; 
	}
	
	public void setSequenceV(ArrayList<String> setSequenceV){
		sequenceV = setSequenceV; 
	}
	
	public void setCorrectingPosition(int setCorrectingPosition){
		correctingPosition = setCorrectingPosition;
	}
	
	public void setoriginalMutationNucleotide(String setoriginalMutationNucleotide){
		originalMutationNucleotide = setoriginalMutationNucleotide;
	}
	
	public void setIterations(boolean setIterations){
		iterations = setIterations;
	}
	
	public void setMaxLevel(int setMaxLevel){
		maxLevel = setMaxLevel;
	}
	
	public void setModelType(int setModelType){
		modelType = setModelType;
	}
	
	public void setFitness(double setFitness){
		if(modelType == 3){
			fitness = (int) (setFitness*sequenceV.size());
		}else{
			fitness = (int) (setFitness*length);
		}
	}
	
	public void setDetails(boolean setDetails){
		details = setDetails; 
	}
	
	public void setEndRun(boolean setEndRun){
		endRun = setEndRun;
	}
	
	public void setType(boolean setType){
		type = setType;
	}
	
	public void setStarter(RnaNode<String> setStarter){
		starter = setStarter;
	}
	
	public void setIterationCount(int count){
		iterationCount = count;
	}
	
	public void setIterationCountB(int counting){
		iterationCountB = counting;
	}
	
	//METHODS TO RETURN
	public Integer length(){
		return length; 
	}
		
	public ArrayList<Integer> positionV(){
		return positionV;
	}
	
	public ArrayList<String> sequenceV(){
		return sequenceV; 
	}
	
	public Integer correctingPosition(){
		return correctingPosition;
	}
	
	public String originalMutationNucleotide(){
		return originalMutationNucleotide;
	}
	
	public Integer maxLevel(){
		return maxLevel; 
	}
	
	public Integer modelType(){
		return modelType;
	}
	
	public Integer fitness(){
		return fitness; 
	}
	
	public boolean details(){
		return details; 
	}
	
	public boolean endRun(){
		return endRun;
	}
	
	public HashMap<Integer, ArrayList<Integer>> bpDict(){
		return bpDICT;
	}
	
	public HashMap<Integer, ArrayList<Double>> sbpDict(){
		return sbpDICT;
	}
	
	public boolean iterations(){
		return iterations;
	}
	
	public boolean type(){
		return type;
	}
	
	public RnaNode<String> starterNode(){
		return starter; 
	}
	
	public Integer iterationCount(){
		return iterationCount;
	}

}
