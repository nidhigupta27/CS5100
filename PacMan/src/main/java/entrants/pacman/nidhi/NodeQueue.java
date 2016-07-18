package entrants.pacman.nidhi;

public class NodeQueue {
	private int nodeIndx;
	private int parent;
	private int gscore;
	private int hscore;
	private int fscore;
	public NodeQueue(int n,int f)
	{
		nodeIndx = n;
		fscore = f;
	}
	public NodeQueue()
	{
	
	}
	public int getNodeIndex()
	{
		return nodeIndx;
	}
	public void setNodeIndex(int n)
	{
		nodeIndx = n;
	}
	
	/*public int getGScore(){
		return gscore;
	}
	public void setGScore(int g)
	{
		gscore = g;
	}
	public int getHScore(){
		return hscore;
	}
	public void setHScore(int h){
		hscore = h;		
	}*/
	public int getFScore(){
		return fscore;
	}
	public void setFScore(int f){
		fscore = f;		
	}
}
