

public class RnaNode<T> {

	private String mydata; 
	private Integer myPosition; 
	private RnaNode<String> parent;
	private RnaNode<String> left; 
	private RnaNode<String> right;
	private Integer changes; //Number of recorded *noncanonical* changes up to this point
	private boolean found; //True if this results in a compensating change
	private boolean isChange; //True if the node is noncanonical mutation
	private boolean SBP; // True if it's located in a single-base pair region
	private boolean dead; // True if lineage dies after position
	private boolean lastChange; //True if it's the last change @ position
	
	//creates an empty node
	public RnaNode(){
		mydata = null;
		myPosition = null;
		parent = null;
		left = null; 
		right = null; 
		isChange = false; //all new nodes should be false until checked by treeBuilder
		SBP = false;
		dead = false; 
		lastChange = true; 
		changes = 0; //defaults starts at 0
		found = false;
	}
	
	//sets the position and mutation for this node
	public void setData(String data, Integer pos){
		mydata = data; 
		myPosition = pos; 
	}
	
	//sets the left pointer to a children node
	public void setLeft(RnaNode<String> myleft){
		left = myleft; 
	}
	
	//sets the right pointer to a children node
	public void setRight(RnaNode<String> myRight){
		right = myRight; 
	}
	
	//sets the parent
	public void setParent(RnaNode<String> previous){
		parent = previous; 
	}
	
	//marks the node as a correcting mutation
	public void found(){
		found = true;
	}
	
	//sets the node as the last change at the specific location
	public void setLastChange(boolean setChange){
		lastChange = setChange;
	}
	
	//sets the node as the killing mutation
	public void died(){
		dead = true;
	}
	
	//sets the changes to the counted number of changes
	public void sumChanges(int count){
		changes = count;
	}
	
	//sets the current as a noncanonical mutation
	public void changed(){
		isChange = true;
	}
	
	//sets the current node as a sbp
	public void setSBP(){
		SBP = true;
	}
	
	//returns the right node
	public RnaNode<String> getRight(){
		return right; 
	}
	
	//returns the left node
	public RnaNode<String> getLeft(){
		return left;
	}
	
	//returns the data in the current node
	public String getData(){
		return mydata;
	}
	
	//returns position
	public Integer getPosition(){
		return myPosition;
	}
	
	//returns True if there is a left or right child
	public boolean hasChildren(){
		return left != null || right != null;
	}
	
	//returns False if node is the root of the tree
	public boolean hasParent(){
		return parent != null;
	}
	
	//returns pointer to the parent Node
	public RnaNode<String> getParent(){
		return parent; 
	}
	
	//returns True if mutation resulted in a bad mutation
	public boolean isChange(){
		return isChange; 
	}
	
	//returns True if mutation was located in a single-base paired region
	public boolean sbp(){
		return SBP; 
	}
	
	//returns True if mutation causes the lineage to "die" by fitness conditions
	public boolean dead(){
		return dead; 
	}
	
	//returns True if mutation is the last mutation that happens at the location
	public boolean lastChange(){
		return lastChange; 
	}
	
	//returns whether or not it's a found "mark"
	public boolean isFound(){
		return found; 
	}
	
	//returns number of changes up to this point
	public Integer changes(){
		return changes;
	}
	
	//recursive method to determine distance from the root of tree (gives the "generation" number)
	public int distanceFromRoot(RnaNode<String> current){
	
		return distanceFromRoot(current,0);
	}
	
	private int distanceFromRoot(RnaNode<String> current, int num){
		
		if (!current.hasParent()){
			return num;
		}
		
		return distanceFromRoot(current.getParent(), ++num);
	}
}
