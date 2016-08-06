package entrants.pacman.nidhi;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class MyPacMan extends PacmanController 
{
	private MOVE myMove = MOVE.NEUTRAL;

	private HashSet<Integer> eatenPillIndices = new HashSet<Integer>();
	private int prevLevel = -1;
	//algorithmSelection determines what algorithm to be used:
	// A value of 1 selects BFS
	// A value of 2 selects ASTAR
	// A value of 3 selects UniformCost
    private int algorithmSelection = 1;
    
    private int targetIndx = -1;

	public MOVE getMove(Game game, long timeDue) 
	{
		//Gets the current position of PacMan
		int current = game.getPacmanCurrentNodeIndex();
		
		//Gets all pill indices in Maze
		int[] pills = game.getCurrentMaze().pillIndices;
		
		//Gets all power pill indices in Maze
		int[] powerPills = game.getCurrentMaze().powerPillIndices;
				
		//totalPills is sum of pills and powerpills
		ArrayList<Integer> total_pills = new ArrayList<Integer>();
		for (int pill : pills) {
			total_pills.add(pill);
		}
		for (int ppill : powerPills) {
			total_pills.add(ppill);
		}
		
		//Target pills is the arraylist of pills not yet eaten by PacMan
		ArrayList<Integer> targetPills = new ArrayList<Integer>();
		
		//Eaten pill indices is a Hash set of pills eaten by the PacMan. Its reset each time a new game is
		//started or game enters a new level
		if (game.getCurrentLevel() != prevLevel) {
			eatenPillIndices.clear();

		}
		prevLevel = game.getCurrentLevel();
		
		//Each time PacMan eats a pill eaten pill collection is updated
		if (total_pills.contains(game.getPacmanCurrentNodeIndex())) 
		{
			eatenPillIndices.add(game.getPacmanCurrentNodeIndex());
		}
        //target pills is a subset of total pills in Maze yet to be eaten 
		for (int i = 0; i < total_pills.size(); i++) 
		{
			if (!eatenPillIndices.contains(total_pills.get(i)))
			{
				targetPills.add(total_pills.get(i));
			}
		}
       //Convert from ArrayList to an array
		int[] targetsArray = new int[targetPills.size()]; 
		if (!targetPills.isEmpty()) {
			for (int i = 0; i < targetsArray.length; i++) {
				targetsArray[i] = targetPills.get(i);
			}
		}
		//Find the pill closest to the current position of PacMan from the targets array created above
		int nearestPill = game.getClosestNodeIndexFromNodeIndex(current,
				targetsArray, DM.PATH);
	   if(algorithmSelection == 1)
	   {
		//Calculate path to reach the nearest pill using Breadth First Search
		 BFS bfsObj = new BFS();
		 targetIndx = bfsObj.BFS_Run(nearestPill, current, game);
	   }
	   else if(algorithmSelection == 2)
	   {		
		//Find the path to the nearest pill using ASTAR
		  ASTAR astarObj = new ASTAR();
		  targetIndx = astarObj.ASTAR_Run(nearestPill,current,game);
	   }
	   else if(algorithmSelection == 3)
	   {
		//Find the path to the nearest pill using Uniform Cost Algorithm
		UniformCost uniCost = new UniformCost();
		targetIndx = uniCost.UniformCost_Run(nearestPill, current, game);
			
	   }
		
		//If the target index computed by the algorithm is valid(not -1) calculate the next move 
		//otherwise keep going on the same path
		if (targetIndx != -1) 
		{
			myMove = game.getNextMoveTowardsTarget(current, targetIndx, DM.PATH);
			
		} else 
		{
			myMove = MOVE.NEUTRAL;
			System.out.println("Sorry! Not a valid Move" + myMove);
		}
		return myMove;
	}
}