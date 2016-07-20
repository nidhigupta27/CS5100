package entrants.pacman.nidhi;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.internal.Node;

public class UniformCost 
{
	public int UniformCost_Run(int closestPillIndx, int pacmanPosIndx, Game game) {

		/* Declaration */
		// Maze graph nodes
		Node[] allNodes = game.getCurrentMaze().graph;
		// Color array to differentiate between explored (BLACK), seen but
		// exploration pending (GREY)
		// and unseen nodes (WHITE)
		int[] color = new int[allNodes.length];
		// Holding parent node of the child node
		int[] parent = new int[allNodes.length];
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

		// DEBUG
		GameView.addLines(game, Color.blue, pacmanPosIndx, closestPillIndx);


		// Initialize parent, color arrays and node queue
		// elements
		for (Node n : allNodes) {
			NodeQueue obj = new NodeQueue(n.nodeIndex, Integer.MAX_VALUE);
			allNodesInList.add(obj);
			parent[n.nodeIndex] = -1;
			color[n.nodeIndex] = 0; // WHITE
		}
		// Start traversing from source node
		// 1.) Initialize color of source node
		color[pacmanPosIndx] = 1;// GREY
		// 2.) Get the source Node
		NodeQueue sourceNode = allNodesInList.get(pacmanPosIndx);
	
		// 3.) Set score for the source Node element
		sourceNode.setFScore(0);
		// 4.) Add source to the Fringe queue
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
			
			// Loop through all neighbors
			for (int adjindex : neighbors) {
				if (color[adjindex] == 0) // WHITE: if node is unseen
				{
					NodeQueue newNode = new NodeQueue();

					newNode.setNodeIndex(adjindex);

					color[adjindex] = 1; // GREY
					//Update the parent of the new node 
					parent[adjindex] = val.getNodeIndex(); 
                    
					//Update the shortest distance to reach that node from source node
					int shortestDistToReachNode = val.getFScore() + game.getShortestPathDistance(val.getNodeIndex(),adjindex);
								
					//Update the score of the new node 
					newNode.setFScore(shortestDistToReachNode);
					//Enqueue the node in Priority Queue
					qFringe.add(newNode);

				} else if (color[adjindex] == 1) { //When the node is already in queue

					while (!qFringe.isEmpty()) {
						NodeQueue updElem = qFringe.poll();
						if (updElem.getNodeIndex() == adjindex) {
							//If the cost to reach the node already in queue is greater than the cosr of the same node
							//(but reached via a different path), then update the 
							// 1.cost of the node 
							// 2.the parent
						
							if (updElem.getFScore() > val.getFScore()
									+ game.getShortestPathDistance(
											val.getNodeIndex(), adjindex)) {
                           
								parent[adjindex] = val.getNodeIndex();
								updElem.setFScore(val.getFScore()
										+ game.getShortestPathDistance(
												val.getNodeIndex(), adjindex));
							}

						}
						qFringe_temp.add(updElem);
					}
					while (!qFringe_temp.isEmpty()) {
						NodeQueue updElem = qFringe_temp.poll();
						qFringe.add(updElem);
					}
				} else {
					continue;
				}
			}
			color[val.getNodeIndex()] = 2;// BLACK
		}
		return -1;
	}
  //Priority Queue implementation using Comparator to maintain elements in queue in increasing order of FScore
	public static Comparator<NodeQueue> distComparator = new Comparator<NodeQueue>() {
		public int compare(NodeQueue x, NodeQueue y) {
			return (int) (x.getFScore() - y.getFScore());
		}
	};


}
