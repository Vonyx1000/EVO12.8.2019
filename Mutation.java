import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;
import java.util.Map;

public class Mutation {

	private int length;

	public Mutation(int lengthSequence) throws FileNotFoundException{
		length = lengthSequence;
	}
	
	public RnaNode<String> createNextNode(){
		
		RnaNode<String> mutation = new RnaNode<String>();
		
		//generates random position
		int position = ThreadLocalRandom.current().nextInt(1, length);


		//generates random nucleotide (GUAC)
		String temp = "#GUAC"; 
	    int spot = ThreadLocalRandom.current().nextInt(1, temp.length());
		String nucleotide = "" + temp.charAt(spot);
		mutation.setData(nucleotide, position);
		mutation.isChange();
		return mutation;
	}
	
}
