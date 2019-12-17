import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SequenceBuilder {

	private RnaNode<String> node; 
	private Constants settings;
	private ArrayList<String> sequence;
	
	public SequenceBuilder(RnaNode<String> current, Constants constant){
	
		node = current; 
		settings = constant;
		sequence = new ArrayList<String>();

	}
	
	//rebuilds the sequence from tree nodes
	public ArrayList<String> build(){
		
		//Master Sequence
		sequence.addAll(settings.sequenceV());
		
		while(node.hasParent()){
			
			//Updates master sequence with mutations
			if(node.lastChange()){
				sequence.set(node.getPosition(), node.getData());
			}
			
			node = node.getParent();
		}
		
		return sequence;
	}
	
	public File buildBPSEQ(ArrayList<Integer> positions) throws FileNotFoundException{
		
		File bpseq = new File("Temporary_bpseq.txt"); 
		PrintWriter printer = new PrintWriter(bpseq);
			
		printer.println("x");
		printer.println("x");
		printer.println("x");
		printer.println("x");
		
		int count = 0; 
				
		while(count < sequence.size()){
			
			int temp = count;
			printer.println(temp+" "+sequence.get(count)+ " "+positions.get(temp));
			count++;
		}
		
		printer.close();
		return bpseq;
	}
}
