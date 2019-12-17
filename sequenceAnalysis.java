import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Arrays;

public class sequenceAnalysis {

	public sequenceAnalysis(){
		
	}


		//Creates the individual analysis string for each correcting node
	public String analysis(RnaNode<String> current, int trialNum, int cellCount, HashMap <RnaNode, Integer> CheckMap){
		String output = "";


		RnaNode<String> root = current;
		String mutation = ""; //mutations made so far
		int sbpCounter = 0;//single-base paired counter
		
		//getting to the root of the tree
		while(root.hasParent()){
			String changeString = "";
			if (String.valueOf(root.isChange()) == "true"){
				changeString = "noncanonical";
			}
			else{
				changeString = "canonical";
			}
			mutation = mutation + root.getData()+ " " +root.getPosition() + " " + changeString + "/";
			
			if(root.sbp()){
				sbpCounter++;
			}
			
			root = root.getParent();
		}
		
		//mutation = mutation.substring(0,mutation.length()-1); //eliminates the last slash
		
		//iterating through the tree to count
		int cellCounter = 0;//cells sampled counter
		int deadCounter = 0;//dead cells counter
		
		LinkedList<RnaNode<String>> queue = new LinkedList<RnaNode<String>>();
		queue.add(root);

		//okay, so at this point root should be pointing to the root of the tree (has iterated up through all the echelons)
		
		while(!queue.isEmpty()){
			
			RnaNode<String> working = queue.remove();

			//Adds left and right child to the working list if left and right child exist
			if(working.getRight()!=null){
				queue.add(working.getRight());
			}
			
			if(working.getLeft()!=null){
				queue.add(working.getLeft());
			}
			
			if(working.dead()){
				deadCounter++;
			}

			int goodNode = working.changes();

			if (goodNode != 0){ //as the tree's nodes are iterated through, all those nodes
				                     //with change field values != 0 mediate the incrementation of the cellCounter
				                     //variable; this underrepresents the true value by 1 (root of the tree)
				                     //but this is compensated for outside of the while loop
				cellCounter++;
			}


		}

		cellCounter++; //compensate for root node of tree
		
		output = output+trialNum+","; //trial number
		if(current.isFound()){
			output = output+current.distanceFromRoot(current)+","; //generation number
		}else{
			output = output+"Not Found,"; //"Not found" if no compensating change is found
		}		
		output = output+root.getData()+","+root.getPosition()+","; //Mutation number and position of the original mutation
		output = output+current.changes()+","; //Number of mutations in current lineage
		output = output+cellCounter+","+deadCounter+","+sbpCounter+","; //Counts for total cells sampled, total dead cells, and single base paired mutated
		output = output+mutation; //list of mutations made


		String[] mutationArray = mutation.split("/");
		for (String s : mutationArray){
			RnaNode<String> newNode = new RnaNode<String>();
			String[] splitArray = s.split(" ");

			System.out.println(splitArray[2]);
			if (splitArray[2].equals("noncanonical")) {
				newNode.setData(splitArray[0], Integer.parseInt(splitArray[1]));
				CheckMap.put(newNode, 1);
			}
		}

		for (RnaNode key : CheckMap.keySet()){
			System.out.println(key.getData() + " " + key.getPosition() + " ");
		}


		return output;
	}

}
