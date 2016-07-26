
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
public class MyPacManII extends PacmanController 
{
	//private MOVE myMove = MOVE.NEUTRAL;
	private HashSet<Integer> eatenPillIndices = new HashSet<Integer>();
	private int prevLevel = -1;
	//algorithmSelection determines what algorithm to be used:
	// A value of 1 selects MinMax
	// A value of 2 selects AlphaBeta Pruning
    private int algorithmSelection = 2;
    
    private int targetIndx = -1;

	public MOVE getMove(Game game, long timeDue) 
	{
		if (algorithmSelection == 1)
		{
			MinMax mm = new MinMax(game);
			return mm.MinMaxEval(game);	
		}
		else
		{
			AlphaBetaPruning abp = new AlphaBetaPruning(game);
			return abp.AlphaBetaPruningEval(game);	
		}
			
	}
}
