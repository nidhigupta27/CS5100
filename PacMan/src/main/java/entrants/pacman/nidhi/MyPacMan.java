package entrants.pacman.nidhi;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.internal.Node;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class MyPacMan extends PacmanController {
	private MOVE myMove = MOVE.NEUTRAL;
	// private MOVE myMove = MOVE.RIGHT;
	private HashSet<Integer> eatenPillIndices = new HashSet<Integer>();
	private int prevLevel = -1;

	@SuppressWarnings("deprecation")
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
		// System.out.println("total pills+power pills"+total_pills.size());
		// System.out.println("power pills"+powerPills.length);
		// System.out.println("normal pills"+pills.length);
		
		//Target pills is the arraylist of pills not yet eaten by PacMan
		ArrayList<Integer> targetPills = new ArrayList<Integer>();
		
		//Eaten pill indices is a Hash set of pills eaten by the PacMan. Its reset each time a new game is
		//started or game enters a new level
		if (game.getCurrentLevel() != prevLevel) {
			eatenPillIndices.clear();

		}
		prevLevel = game.getCurrentLevel();
		
		//Each time PacMan eats a pill eaten pill collection is updated
		if (total_pills.contains(game.getPacmanCurrentNodeIndex())) {
			eatenPillIndices.add(game.getPacmanCurrentNodeIndex());
		}
        //target pills is a subset of total pills in Maze yet to be eaten 
		for (int i = 0; i < total_pills.size(); i++) {
			if (!eatenPillIndices.contains(total_pills.get(i))) {
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
		// System.out.println("The current pacman pos:"+current);
		// System.out.println("The nearest pill"+nearestPill);

		// int targetIndx = BFS(nearestPill,current,game);
		//Calculate path to reach the nearest pill using Breadth First Search
		//BFS bfsObj = new BFS();
		//int targetIndx = bfsObj.BFS_Run(nearestPill, current, game);
		
		ASTAR astarObj = new ASTAR();
		int targetIndx = astarObj.ASTAR_Run(nearestPill, current, game);
		// int targetIndx1 = ASTAR(nearestPill,current,game);
		// int [] targetindx2 = game.getAStarPath(current, nearestPill,
		// game.getPacmanLastMoveMade());

		// System.out.println("BFS position"+targetIndx+"ASTAR"+targetIndx1+"ASTAR1"+targetindx2[0]);
		// if((targetIndx1!=targetindx2[0]) || (targetIndx!=targetindx2[0]))
		// {
		// System.out.println("BFS position"+targetIndx+"ASTAR"+targetIndx1+"ASTAR1"+targetindx2[0]);
		// System.out.println("nearestPill"+nearestPill+"current"+current);
		// }

		// int targetIndx = ASTAR1(nearestPill,current,game);
		
		//If the target index computed by the algorithm is valid(not -1) calculate the next move 
		//otherwise keep going on the same path
		if (targetIndx != -1) {
			// System.out.println("Got indx from BFS"+targetIndx);
			// myMove = game.getNextMoveTowardsTarget(current, targetIndx,
			// myMove,DM.PATH);
			myMove = game
					.getNextMoveTowardsTarget(current, targetIndx, DM.PATH);
			// myMove = game.getNextMoveTowardsTarget(current,
			// targetIndx,DM.PATH);
			// System.out.println("Got direction from BFS"+ myMove);
		} else {
			myMove = MOVE.NEUTRAL;
			System.out.println("Sorry! Not a valid Move" + myMove);
		}
		return myMove;
	}

	/*
	 * public int BFS(int closestPillIndx,int pacmanPosIndx,Game game) { Node[]
	 * allNodes = game.getCurrentMaze().graph; int[] color = new
	 * int[allNodes.length]; int[] parent = new int[allNodes.length]; boolean
	 * foundPath = false; Queue<Integer> qFringe = new LinkedList<Integer>();
	 * GameView.addLines(game,Color.blue, pacmanPosIndx, closestPillIndx);
	 * 
	 * for(Node n : allNodes) { color[n.nodeIndex] = 0 ; //WHITE
	 * parent[n.nodeIndex]= -1; } color[pacmanPosIndx] = 1;
	 * qFringe.add(pacmanPosIndx); while(!qFringe.isEmpty()) { int val =
	 * qFringe.remove(); if(val==closestPillIndx) { foundPath = true;
	 * while(!(parent[val]==pacmanPosIndx)) { val = parent[val]; } return val; }
	 * int[] neighbors = game.getNeighbouringNodes(val); for (int
	 * adjindex:neighbors) { if(color[adjindex]==0) //if unexplored {
	 * color[adjindex]=1;//GREY parent[adjindex]=val; qFringe.add(adjindex); } }
	 * color[val] = 2;//BLACK } return -1; }
	 */

	/*public int ASTAR(int closestPillIndx, int pacmanPosIndx, Game game) {

		 Declaration 
		// Maze graph nodes
		Node[] allNodes = game.getCurrentMaze().graph;
		// Color array to differentiate between explored (BLACK), seen but
		// exploration pending (GREY)
		// and unseen nodes (WHITE)
		int[] color = new int[allNodes.length];
		// Holding parent node of the child node
		int[] parent = new int[allNodes.length];
		// Cost array - An array of cost of reaching a particular node from
		// source
		int[] gScore = new int[allNodes.length];
		// Cost array - An array of heuristic cost of reaching destination node
		// from a particular node
		int[] hScore = new int[allNodes.length];
		// Boolean to indicate that a path to the destination has been found or
		// not
		boolean foundPath = false;
		// Priority Queue for holding all seen but unexplored nodes in an
		// ascending order of cost
		Queue<NodeQueue> qFringe = new PriorityQueue<NodeQueue>(
				allNodes.length, distComparator);
		Queue<NodeQueue> qFringe_temp = new PriorityQueue<NodeQueue>(
				allNodes.length, distComparator);
		ArrayList<NodeQueue> allNodesInList = new ArrayList<NodeQueue>();

		// System.out.println("Entering ASTAR again");
		// int[] ghostIndices = new int[4];
		// int ghostIndx = 0;
		// boolean ghostFrightened = false;
		//

		//
		// for (GHOST ghost : GHOST.values()) {
		// ghostIndices[ghostIndx] = game.getGhostCurrentNodeIndex(ghost);
		// // if (game.getGhostEdibleTime(ghost) > 0 ) {
		// // ghostFrightened = true;
		// // }
		// ghostIndx++;
		// }

		// Initialize parent, gScore, hScore, color arrays and node queue
		// elements
		for (Node n : allNodes) {
			NodeQueue obj = new NodeQueue(n.nodeIndex, Integer.MAX_VALUE);
			allNodesInList.add(obj);
			parent[n.nodeIndex] = -1;
			gScore[n.nodeIndex] = Integer.MAX_VALUE;
			hScore[n.nodeIndex] = Integer.MAX_VALUE;
			color[n.nodeIndex] = 0; // WHITE
		}
		// Start traversing from source node
		// 1.) Initialize color of source node
		color[pacmanPosIndx] = 1;// GREY
		// 2.) Get heuristic distance (hScore) between source and destination
		// node
		int manhattanDist = game.getManhattanDistance(pacmanPosIndx,
				closestPillIndx);
		// 3.) Get the source Node
		NodeQueue sourceNode = allNodesInList.get(pacmanPosIndx);
		// 4.) Reset gScore and hScore for source node
		gScore[pacmanPosIndx] = 0;
		hScore[pacmanPosIndx] = manhattanDist;
		// 5.) Set FScore for the source Node element
		sourceNode.setFScore(gScore[pacmanPosIndx] + hScore[pacmanPosIndx]);
		// 6.) Add source to the Fringe queue
		qFringe.add(sourceNode);

		// Loop through nodes until fringe queue is empty
		while (!qFringe.isEmpty()) {
			NodeQueue val = qFringe.poll();
			// System.out.println("Head of queue"+val.getNodeIndex());
			int lastindex = val.getNodeIndex();
			// If the destination node is reached then trace back the path
			// to the node whose parent is the source node. Return the
			// node index of the reached node as the next location to
			// which Pacman needs to move.
			if (lastindex == closestPillIndx) {
				foundPath = true;
				while (!(parent[lastindex] == pacmanPosIndx)) {
					lastindex = parent[lastindex];
				}
				return lastindex;
			}
			// Get the neighboring nodes to visit
			int[] neighbors = game.getNeighbouringNodes(val.getNodeIndex());
			boolean ghostAtIndex = false;
			// Loop through all neighbors
			for (int adjindex : neighbors) {
				if (color[adjindex] == 0) // WHITE: if node is unseen
				{
					NodeQueue newNode = new NodeQueue();
					newNode.setNodeIndex(adjindex);
					color[adjindex] = 1; // GREY
					parent[adjindex] = val.getNodeIndex();
					// nq.setNodeParent(val.getNodeIndex());
					gScore[adjindex] = gScore[val.getNodeIndex()] + 1;
					// gScore[adjindex]= gScore[val.getNodeIndex()] +
					// game.getShortestPathDistance(val.getNodeIndex(),
					// adjindex);
					// int hValue = game.getManhattanDistance(adjindex,
					// closestPillIndx);
					int hValue = game.getShortestPathDistance(adjindex,
							closestPillIndx);
					hScore[adjindex] = hValue;
					newNode.setFScore(gScore[adjindex] + hScore[adjindex]);
					qFringe.add(newNode);
					// System.out.println("Inside WHITE");
				} else if (color[adjindex] == 1) {
					// System.out.println("Inside GREY");
					// for(NodeQueue updElem : qFringe)
					while (!qFringe.isEmpty()) {
						NodeQueue updElem = qFringe.poll();
						if (updElem.getNodeIndex() == adjindex) {
							if (gScore[adjindex] > gScore[val.getNodeIndex()] + 1)
							// if(gScore[adjindex] >
							// gScore[val.getNodeIndex()]+game.getShortestPathDistance(val.getNodeIndex(),
							// adjindex))
							{
								gScore[adjindex] = gScore[val.getNodeIndex()] + 1;
								// gScore[adjindex] =
								// gScore[val.getNodeIndex()]+game.getShortestPathDistance(val.getNodeIndex(),
								// adjindex);
								parent[adjindex] = val.getNodeIndex();
								updElem.setFScore(gScore[adjindex]
										+ hScore[adjindex]);
								// System.out.println("Inside GREY check");

								// NodeQueue newElem = new NodeQueue();
								// newElem.setNodeIndex(updElem.getNodeIndex());
								// newElem.setFScore(updElem.getFScore());
								// qFringe.remove(updElem);
								// Boolean state = qFringe.add(newElem);
								// System.out.println("State is"+ state);
								// for(NodeQueue iter:qFringe){
								// System.out.println("Queue Elements inside grey loop are"+
								// iter.getFScore()+" "+iter.getNodeIndex());
								// }
							}

						}
						qFringe_temp.add(updElem);
					}
					while (!qFringe_temp.isEmpty()) {
						NodeQueue updElem = qFringe_temp.poll();
						qFringe.add(updElem);
					}
					// System.out.println("Done with copying");
				} else {
					continue;
				}
				// Queue<NodeQueue> qFringeCopy = new
				// PriorityQueue<NodeQueue>(allNodes.length, distComparator);
				// qFringeCopy = qFringe;

				// for(NodeQueue iter:qFringe){
				// // NodeQueue nq1 = qFringeCopy.poll();
				//
				// System.out.println("Queue Elements inside loop are"+
				// iter.getFScore()+" "+iter.getNodeIndex());
				// }
				// System.out.println("**************************************************************");
			}
			color[val.getNodeIndex()] = 2;// BLACK
			

		}
		return -1;
	}

	public static Comparator<NodeQueue> distComparator = new Comparator<NodeQueue>() {
		public int compare(NodeQueue x, NodeQueue y) {
			return (int) (x.getFScore() - y.getFScore());
		}
	};*/

	/*public int ASTAR1(int closestPillIndx, int pacmanPosIndx, Game game) {

		 Declaration 
		// Maze graph nodes
		Node[] allNodes = game.getCurrentMaze().graph;
		// Color array to differentiate between explored (BLACK), seen but
		// exploration pending (GREY)
		// and unseen nodes (WHITE)
		int[] color = new int[allNodes.length];
		// Holding parent node of the child node
		int[] parent = new int[allNodes.length];
		// Cost array - An array of cost of reaching a particular node from
		// source
		int[] gScore = new int[allNodes.length];
		// Cost array - An array of heuristic cost of reaching destination node
		// from a particular node
		int[] hScore = new int[allNodes.length];
		// Boolean to indicate that a path to the destination has been found or
		// not
		boolean foundPath = false;
		int[] ghostIndices = new int[4];
		// Priority Queue for holding all seen but unexplored nodes in an
		// ascending order of cost
		Queue<NodeQueue> qFringe = new PriorityQueue<NodeQueue>(
				allNodes.length, distComparator);
		Queue<NodeQueue> qFringe_temp = new PriorityQueue<NodeQueue>(
				allNodes.length, distComparator);
		ArrayList<NodeQueue> allNodesInList = new ArrayList<NodeQueue>();

		System.out.println("Entering ASTAR again");
		// int[] ghostIndices = new int[4];
		int ghostIndx = 0;
		// boolean ghostFrightened = false;
		//

		System.out.println("the ghosts are in :");
		for (GHOST ghost : GHOST.values()) {
			ghostIndices[ghostIndx] = game.getGhostCurrentNodeIndex(ghost);
			System.out.print(" " + game.getGhostCurrentNodeIndex(ghost) + " "
					+ ghost);
			// if (game.getGhostEdibleTime(ghost) > 0 ) {
			// ghostFrightened = true;
			// }
			ghostIndx++;
		}

		// Initialize parent, gScore, hScore, color arrays and node queue
		// elements
		for (Node n : allNodes) {
			NodeQueue obj = new NodeQueue(n.nodeIndex, Integer.MAX_VALUE);
			allNodesInList.add(obj);
			parent[n.nodeIndex] = -1;
			gScore[n.nodeIndex] = Integer.MAX_VALUE;
			hScore[n.nodeIndex] = Integer.MAX_VALUE;
			color[n.nodeIndex] = 0; // WHITE
		}
		// Start traversing from source node
		// 1.) Initialize color of source node
		color[pacmanPosIndx] = 1;// GREY
		// 2.) Get heuristic distance (hScore) between source and destination
		// node
		int manhattanDist = game.getManhattanDistance(pacmanPosIndx,
				closestPillIndx);
		// 3.) Get the source Node
		NodeQueue sourceNode = allNodesInList.get(pacmanPosIndx);
		// 4.) Reset gScore and hScore for source node
		gScore[pacmanPosIndx] = 0;
		hScore[pacmanPosIndx] = manhattanDist;
		// 5.) Set FScore for the source Node element
		sourceNode.setFScore(gScore[pacmanPosIndx] + hScore[pacmanPosIndx]);
		// 6.) Add source to the Fringe queue
		qFringe.add(sourceNode);

		// Loop through nodes until fringe queue is empty
		while (!qFringe.isEmpty()) {
			NodeQueue val = qFringe.poll();
			System.out.println("Head of queue" + val.getNodeIndex());
			int lastindex = val.getNodeIndex();
			// If the destination node is reached then trace back the path
			// to the node whose parent is the source node. Return the
			// node index of the reached node as the next location to
			// which Pacman needs to move.
			if (lastindex == closestPillIndx) {
				foundPath = true;
				while (!(parent[lastindex] == pacmanPosIndx)) {
					lastindex = parent[lastindex];
				}
				return lastindex;
			}
			// Get the neighboring nodes to visit
			int[] neighbors = game.getNeighbouringNodes(val.getNodeIndex());
			boolean ghostAtIndex = false;
			// Loop through all neighbors
			for (int adjindex : neighbors) {
				int ghostCost = 0;
				for (int i = 0; i < 4; i++) {
					if (ghostIndices[i] == adjindex) {
						System.out.println("SAW GHOST");
						ghostCost = 10;
						break;
					}
				}
				if (color[adjindex] == 0) // WHITE: if node is unseen
				{
					NodeQueue newNode = new NodeQueue();
					newNode.setNodeIndex(adjindex);
					color[adjindex] = 1; // GREY
					parent[adjindex] = val.getNodeIndex();

					// nq.setNodeParent(val.getNodeIndex());
					// gScore[adjindex] = gScore[val.getNodeIndex()]+1;
					gScore[adjindex] = gScore[val.getNodeIndex()]
							+ game.getShortestPathDistance(val.getNodeIndex(),
									adjindex) + ghostCost;
					int hValue = game.getManhattanDistance(adjindex,
							closestPillIndx);
					hScore[adjindex] = hValue;

					newNode.setFScore(gScore[adjindex] + hScore[adjindex]);

					qFringe.add(newNode);
					System.out.println("Inside WHITE");
				} else if (color[adjindex] == 1) {
					System.out.println("Inside GREY");
					// for(NodeQueue updElem : qFringe)
					while (!qFringe.isEmpty()) {
						NodeQueue updElem = qFringe.poll();
						if (updElem.getNodeIndex() == adjindex) {
							// if(gScore[adjindex] >
							// gScore[val.getNodeIndex()]+1 )
							if (gScore[adjindex] > gScore[val.getNodeIndex()]
									+ game.getShortestPathDistance(
											val.getNodeIndex(), adjindex)
									+ ghostCost) {
								// gScore[adjindex] =
								// gScore[val.getNodeIndex()]+1;
								gScore[adjindex] = gScore[val.getNodeIndex()]
										+ game.getShortestPathDistance(
												val.getNodeIndex(), adjindex)
										+ ghostCost;
								parent[adjindex] = val.getNodeIndex();
								updElem.setFScore(gScore[adjindex]
										+ hScore[adjindex] + ghostCost);
								System.out.println("Inside GREY check");

								// NodeQueue newElem = new NodeQueue();
								// newElem.setNodeIndex(updElem.getNodeIndex());
								// newElem.setFScore(updElem.getFScore());
								// qFringe.remove(updElem);
								// Boolean state = qFringe.add(newElem);
								// System.out.println("State is"+ state);
								// for(NodeQueue iter:qFringe){
								// System.out.println("Queue Elements inside grey loop are"+
								// iter.getFScore()+" "+iter.getNodeIndex());
								// }
							}

						}
						qFringe_temp.add(updElem);
					}
					while (!qFringe_temp.isEmpty()) {
						NodeQueue updElem = qFringe_temp.poll();
						qFringe.add(updElem);
					}
					System.out.println("Done with copying");
				} else {
					continue;
				}
				// Queue<NodeQueue> qFringeCopy = new
				// PriorityQueue<NodeQueue>(allNodes.length, distComparator);
				// qFringeCopy = qFringe;

				// for(NodeQueue iter:qFringe){
				// // NodeQueue nq1 = qFringeCopy.poll();
				//
				// System.out.println("Queue Elements inside loop are"+
				// iter.getFScore()+" "+iter.getNodeIndex());
				// }
				System.out
						.println("**************************************************************");
			}
			color[val.getNodeIndex()] = 2;// BLACK
			
			 * for(NodeQueue iter:qFringe){
			 * System.out.println("Queue Elements are"+
			 * iter.getFScore()+" "+iter.getNodeIndex()); } System.out.println(
			 * "**************************************************************"
			 * );
			 

		}
		return -1;
	}*/
}
