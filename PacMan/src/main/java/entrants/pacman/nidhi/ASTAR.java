package entrants.pacman.nidhi;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.internal.Node;

public class ASTAR 
{
	public int ASTAR_Run(int closestPillIndx, int pacmanPosIndx, Game game) {

		/* Declaration */
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
		
		//DEBUG
    	GameView.addLines(game,Color.blue, pacmanPosIndx, closestPillIndx);

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

				} else if (color[adjindex] == 1) {
					
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
				} else 
				{
					continue;
				}
			}
			color[val.getNodeIndex()] = 2;// BLACK
			/*
			 * for(NodeQueue iter:qFringe){
			 * System.out.println("Queue Elements are"+
			 * iter.getFScore()+" "+iter.getNodeIndex()); } System.out.println(
			 * "**************************************************************"
			 * );
			 */
		}
		return -1;
	}
	public static Comparator<NodeQueue> distComparator = new Comparator<NodeQueue>() {
		public int compare(NodeQueue x, NodeQueue y) {
			return (int) (x.getFScore() - y.getFScore());
		}
	};

}
