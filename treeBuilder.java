import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.File; 
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;
import java.util.Map;

public class treeBuilder {

	private RnaNode<String> root; //bases of the tree, also contains the original mutation
	private Mutation mutator; //mutator being used by the class
	public Checker check; //Checker used by the entire class, public for HashMap access in startTester; can be returned to private and replaced with method
	private BufferedWriter output; //Final output writer (for run details)
	private Constants settings; //Settings file that holds all the information for the current run
	private boolean endRun; //stops the loop when first compensating change is found when setting is selected

	private static int cellCount = 0; //counts the number of nodes that have been added to the tree

	public int cellCountGet() {
		return this.cellCount;
	}

	public treeBuilder(Constants constant, BufferedWriter finalFile, HashMap <RnaNode, Integer> CheckMap) throws NullPointerException, IOException {

		settings = constant;
		output = finalFile;
		endRun = false; //default, (I believe this makes it such that it stops at first complementary mutation, if found)

		//Checker with all the keys
		this.check = new Checker(settings);

		//Mutator with correct sequences
		mutator = new Mutation(settings.length());

		if (settings.type()) {

			//creates root of the tree, containing the first mutation and saves mutation data
			root = mutator.createNextNode();

			//checks original mutation is base paired, if not picks a new position
			int spot = root.getPosition();

			while (settings.positionV().get(spot) == 0) {
				spot = ThreadLocalRandom.current().nextInt(1, settings.length());
			}

			//checks if original position was actually mutated
			String nuc = root.getData();
			root.setData(nuc, spot);
			while (check.simpleMutation(root)) {
				String temp = "#GUAC";
				int count = ThreadLocalRandom.current().nextInt(1, temp.length());
				nuc = "" + temp.charAt(count);
				root.setData(nuc, spot);
			}
			root.setData(nuc, spot);

		}
		else {
			root = settings.starterNode();
		}

		int counter = 1;
		//Counts amount of changes in the sequence (including root)

		for (int i = 0; i < settings.positionV().size(); i++) {

			int pos2 = settings.positionV().get(i);
			String bp1 = settings.sequenceV().get(i);
			String bp2 = settings.sequenceV().get(pos2);

			if (pos2 != 0) {
				if (!check.LocCheck(bp1, bp2)) {
					counter++;
				}
			}
		}

		root.sumChanges(counter);

		//stores the position where the correction mutation pair is

		if (CheckMap.size() == 0) {
			settings.setCorrectingPosition(root.getPosition());
			settings.setoriginalMutationNucleotide(root.getData());
		}
		else {
			if (!CheckMap.containsValue(1)) {
				settings.setCorrectingPosition(root.getPosition());
				settings.setoriginalMutationNucleotide(root.getData());
			}
			else {
				for (Map.Entry<RnaNode, Integer> entry : CheckMap.entrySet()) { //iterate through HashMap until find mutation that has yet to be compensated for
					if (entry.getValue() == 1) { //if the current noncanonical has yet to be compensated for
						settings.setCorrectingPosition(entry.getKey().getPosition());
						settings.setoriginalMutationNucleotide(entry.getKey().getData());
						CheckMap.put(entry.getKey(), 0);

					}
					break;
				}
			}
		}

		createLeftChild(root);
		createRightChild(root);


		if (settings.details()) {
			this.writeNode(root);
		}


	}
	
	public RnaNode<String> buildTree() throws FileNotFoundException, IOException{

		//list of all the nodes in the current level of the tree
		ArrayList<RnaNode<String>> currentLevel = new ArrayList<>(); 
		currentLevel.add(root.getLeft());
		currentLevel.add(root.getRight());
		
		//Holders for node that is currently being worked on
		RnaNode<String> currentNode = new RnaNode<String>();
		RnaNode<String> finalNode = new RnaNode<String>();
		
		int level = 0; //spots the current generation

		while(currentLevel.size() > 0 && level < settings.maxLevel()){
			
			ArrayList<RnaNode<String>> nextLevel = new ArrayList<RnaNode<String>>(); //holder for viable nodes in the next level
			
			int i = 0;
			while(i < currentLevel.size() && !endRun){
				currentNode = currentLevel.get(i); 
								
				//if children are possible make left and right child
				if(childrenPossible(currentNode)){
					createLeftChild(currentNode);
					this.cellCount++;
					createRightChild(currentNode);
					this.cellCount++;

					//holds the identity of the compensation node
					if(endRun){
						finalNode = currentNode;
					}
					else{
						nextLevel.add(currentNode.getLeft());
						nextLevel.add(currentNode.getRight());
					}
				}
				
				i++;
			}
			
			//prints all details for current level
			if(settings.details()){
				for(int x = 0; x < currentLevel.size(); x++){
					this.writeNode(currentLevel.get(x));
				}
			}
			
			currentLevel = nextLevel; 
			level++;
			
			if(settings.endRun() && endRun){
				currentLevel = new ArrayList<>();
			}
			
			
		}
		
		if(finalNode.isFound()){
			return finalNode;
		}else{
			return currentNode; 
		}
	}
	
	//creates left child for the current node
	private void createLeftChild(RnaNode<String> current){
		
		RnaNode<String> nextNode = mutator.createNextNode();
		nextNode.setParent(current); 
		
		//saves mutation data for checkers
		current.setLeft(nextNode);

	}
	
	//Create right child for the current node
	private void createRightChild(RnaNode<String> current){
		
		RnaNode<String> nextNode = mutator.createNextNode(); 
		nextNode.setParent(current);
		
		//saves mutation data for checkers
		current.setRight(nextNode);

	}
	
	//determines whether or not further children can be created in the lineage
	//return false if no more children possible/necessary
	private boolean childrenPossible(RnaNode<String> current) throws FileNotFoundException, IOException{
		
		//Checks if the current change has happened previously in the tree
		RnaNode<String> temp = current.getParent(); 
		int position = current.getPosition();
		
		while(temp.hasParent()){			
			
			if(temp.getPosition() == position){
				temp.setLastChange(false);//sets all previous positions to not last change at position in lineage
			}
			temp = temp.getParent();
		}
		
		//Checks if correcting mutation is found
		if(check.foundCorrection(current, current.getData(), current.getPosition())){
			current.found();
			//current.changed(); //erroneously delegates the compensatory change as noncanonical
			current.sumChanges(current.getParent().changes() - 1);
			endRun = true;
			return false;
		}
		//Check if lineage is fit to continue
		else{

			int count = root.changes(); //amount of mutations contained in current lineage
			//Counts up how many mismatches are in lineage so far
			temp = current.getParent(); //temporary holder while scanning through tree
			while(temp.hasParent()){
				
				if(temp.isChange() && temp.lastChange()){
					if(settings.modelType() == 3){
						if(temp.sbp()){
							count++;
						}else{
							count++;
						}
					}else{
					count++;
					}
				}
				
				temp = temp.getParent();
			}
			
			//Model 1
			if(settings.modelType() == 1){
				
				//only increments count if a mismatch is created
				if(!check.simpleMutation(current)){
					count++;
				}
				
			}
			//Model 2
			else if(settings.modelType() == 2){
				//only increments count if a mismatch is created
				if(!check.complexMutation(current)){
					count++;
				}
			}
			//Model 3
			else{
				//only increments count if a mismatch is created
				if(!check.unpairedCheck(current)){
					if(current.sbp()){
						count++;
					}else{
						count++;
					}
				}
			}
			
			if(count < settings.fitness()){
				current.sumChanges(count);
				return true;
			}
			else{
				current.sumChanges(count);
				current.died(); 
				return false; 
			}
		}
		
	}
	
	
	private void writeNode(RnaNode<String> current) throws IOException{
		output.write(current.distanceFromRoot(current)+" "+ current.getData()+" "+current.getPosition()+" "+current.dead()+" "+current.isChange()+" "+current.isFound()
		+" "+current.sbp()+" "+current.changes());

		output.newLine();
	}
}

