package entrants.pacman.nidhi;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;

import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.internal.Node;

public class BFS 
{
	public int BFS_Run(int closestPillIndx,int pacmanPosIndx,Game game)
    {
    	//Get all nodes in Maze graph
		Node[] allNodes = game.getCurrentMaze().graph;
		// Color array to differentiate between explored (BLACK), seen but
		// exploration pending (GREY)
		// and unseen nodes (WHITE)
    	int[] color  = new int[allNodes.length];
    	
    	// Holding parent node of the child node
    	int[] parent = new int[allNodes.length];
    	
    	// Boolean to indicate that a path to the destination has been found or
    	// not
        boolean foundPath = false;
        //Queue holding nodes in graph
    	Queue<Integer> qFringe = new LinkedList<Integer>();
    	
    	//DEBUG
    	GameView.addLines(game,Color.blue, pacmanPosIndx, closestPillIndx);
    	
    	// Initialize parent, color arrays 
    	for(Node n : allNodes)
    	{
    		color[n.nodeIndex] = 0 ; //WHITE
    		parent[n.nodeIndex]= -1;    		
    	}
    	
    	// Start traversing from source node
    	// 1.) Initialize color of source node
    	color[pacmanPosIndx] = 1;
    	
    	//Enqueue source node in qFringe
    	qFringe.add(pacmanPosIndx);
    	
    	//Loop until queue is empty
    	while(!qFringe.isEmpty())
    	{
    		//Pop the head of the queue
    		int val = qFringe.remove();
    		
    		// If the destination node is reached then trace back the path
    		// to the node whose parent is the source node. Return the
    		// node index of the reached node as the next location to
    		// which Pacman needs to move.
    		
    		if(val==closestPillIndx)
    		{
    			foundPath = true;
    			while(!(parent[val]==pacmanPosIndx))
    			{
    				val = parent[val];    				
    			}
    			return val;
    		}
    		// Get the neighboring nodes to visit
    		int[] neighbors =  game.getNeighbouringNodes(val);
    		
    		//Loop through all neighbors
    		for (int adjindex:neighbors)
    		{
    			if(color[adjindex]==0)   //if unexplored
    			{   				
    				color[adjindex]=1;//GREY
    				parent[adjindex]=val;
    				qFringe.add(adjindex); //add the node in queue
    			}
    		}
    		//When node is fully explored, color is updated to BLACK
    		color[val] = 2;//BLACK
    	}
    	return -1;
    }   
}
