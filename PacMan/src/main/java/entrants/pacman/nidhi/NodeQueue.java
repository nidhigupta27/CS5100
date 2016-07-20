package entrants.pacman.nidhi;

public class NodeQueue 
{
	private int nodeIndx;
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
	
	public int getFScore()
	{
		return fscore;
	}
	public void setFScore(int f)
	{
		fscore = f;		
	}
}
