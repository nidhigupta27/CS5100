package entrants.pacman.nidhi;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Random;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Ghost;

public class AlphaBetaPruning 
{
	private Game originalGame;

	public AlphaBetaPruning(Game game) 
	{
		originalGame = game;
		
	}

	public MOVE AlphaBetaPruningEval(Game game) 
	{
		int bestGameScore = Integer.MIN_VALUE; //Max node with best game score initially very small
		//Initialize the array holing moves from PacMan and four ghost in game
		MOVE[] movesAllAgents = new MOVE[] {MOVE.NEUTRAL, MOVE.NEUTRAL,MOVE.NEUTRAL, MOVE.NEUTRAL, MOVE.NEUTRAL};	
		
		Game ToGame = game.copy();
		//Get the PacMan current position
		int pacManPos = game.getPacmanCurrentNodeIndex();
		//Get all possible PacMan moves
		MOVE[] possiblePacManMoves = game.getPossibleMoves(pacManPos);
		//Initialize the action to consider (returned as the PacMan move eventually) to null
		MOVE actionToConsider = null;
		//Initialize the alpha,beta values for pruning
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
        //loop over all possible PacMan moves
		for (MOVE m : possiblePacManMoves) {
			reset(movesAllAgents);
			movesAllAgents[0] = m;
			//For each PacMan move call Min to evaluate the ghost moves starting with first ghost.
			int score = Min(ToGame, 0, movesAllAgents, getGhost(1), alpha, beta);
			//if the current move score is higher than best of among all  previous moves score, the best
			// score is then the current move score
			if (score > bestGameScore) 
			{
				bestGameScore = score;
				actionToConsider = m;
			//if the current move score is equals than best of among all  previous moves score, randomly select 
			// either the current move score or the best move score
			} 
			else if (score == bestGameScore) 
			{
				Random rand = new Random();
				int randomNum = rand.nextInt(2);
				if (randomNum == 0) {
					bestGameScore = score;
					actionToConsider = m;
				}
			}
			//Apply alpha beta pruning to prune the moves that do not impact the best game score reached using Minimax algorithm
			if (bestGameScore >= beta)
				return actionToConsider;
			if (bestGameScore > alpha) {
				alpha = bestGameScore;
			}
		}
		// Return the best action for PacMan
		return actionToConsider;
	}
	/***********************************************
	 * Method returns the maximum score for all possible moves that PacMan can
	 * make at a give ply(depth) level.
	 * It uses alpha beta pruning to prune the branches(moves) that do not impact the
	 * best game score reached using MiniMax algorithm
	 ************************************************/
	
	private int Max(Game ToGame, int ply, MOVE[] movesAllAgents, int alpha,
			int beta) 
	{
		//Evaluate the game state reached when the game ply has reached the level of 4 or more OR 
		//PacMan gets eaten OR
		//Game reaches the next level
		if ((ply >= 4)|| ToGame.wasPacManEaten()|| ((ToGame.getCurrentLevel() != originalGame.getCurrentLevel()))) 
		{
			return evaluationAtLeaf(originalGame, ToGame);
		}
        //Initialize the best score in Max function with a minimum integer val
		int bestScore = Integer.MIN_VALUE;
		//Gets the current PacMan index
		int pacmanCurrPos = ToGame.getPacmanCurrentNodeIndex();
		//Gets all possible PacMan move
		MOVE[] possibleMoves = ToGame.getPossibleMoves(pacmanCurrPos,
				ToGame.getPacmanLastMoveMade());
        //For each move call Min function for the first ghost
		for (MOVE move : possibleMoves) 
		{
			movesAllAgents[0] = move;
			int score = Min(ToGame, ply, movesAllAgents, getGhost(1), alpha,
					beta);
			//if the current move score is higher than best  among all  previous moves score, the best
			// score is then the current move score
			if (score > bestScore)
				bestScore = score;
			//Apply alpha beta pruning at Max node by checking if the current best score is already greater than
			// the beta value. If true, prune else evaluate other moves too
			if (bestScore >= beta)
				return bestScore;
			//Update alpha with the best score for max node along its path to root
			if (bestScore > alpha) {
				alpha = bestScore;
			}
		}
		//Returns the best score for Max node
		return bestScore;
	}

	/***********************************************
	 * Loops through possible moves of all ghosts and selects the move that
	 * minimizes pacman score (Recursive Function)
	 ************************************************/
	private int Min(Game ToGame, int ply, MOVE[] movesAllAgents, GHOST ghost,
			int alpha, int beta) {
		// Variable to store the best move for ghost agent
		int bestScore = Integer.MAX_VALUE;
		int score = 0;
		// Get the node for the passed game
		int curGhostIndex = ToGame.getGhostCurrentNodeIndex(ghost);

		// If the ghost lies in the LAIR then do not loop through
		// possible moves of the ghost. Either - skip to the next
		// ghost or call the Max function for next ply with the
		// updated game
		if (ToGame.getGhostLairTime(ghost) > 0) {
			int ghostVal = getGhostValue(ghost);
			if (ghostVal < 4) {
				MOVE[] movesAllAgents_copy = movesAllAgents.clone();
				score = Min(ToGame, ply, movesAllAgents_copy,
						getGhost(ghostVal + 1), alpha, beta);
			} else {
				EnumMap<GHOST, MOVE> allghostsMove = createGostsMoveMap(movesAllAgents);
				Game copy_of_game = ToGame.copy();
				copy_of_game.advanceGame(movesAllAgents[0], allghostsMove);
				MOVE[] movesAllAgents_copy = movesAllAgents.clone();
				score = Max(copy_of_game, ply + 1, movesAllAgents_copy, alpha,
						beta);
			}
			//if the current move score is lower than the best among all  previous moves score, the best
			// score is then the current move score
			if (score < bestScore)
				bestScore = score;
			//Apply alpha beta pruning at Min node by checking if the current best score is already smaller than or equal to
			// the beta value. If true, prune else evaluate other moves too
			if (bestScore <= alpha)
				return bestScore;
			//Update beta with the best score for min node along its path to root
			if (bestScore < beta) {
				
				beta = bestScore;
			}
		} else { //When ghostLiar time equals zero
			// Get all possible moves for ghost
			MOVE[] possibleMoves = ToGame.getPossibleMoves(curGhostIndex,
					ToGame.getGhostLastMoveMade(ghost));
			int ghostVal = getGhostValue(ghost);
			//For each move of the first ghost call Min for the next ghost until Min has been
			//called for four ghost
			
			for (MOVE move : possibleMoves) {
				movesAllAgents[ghostVal] = move;
				if (ghostVal < 4) {
					MOVE[] movesAllAgents_copy = movesAllAgents.clone();
					score = Min(ToGame, ply, movesAllAgents_copy,
							getGhost(ghostVal + 1), alpha, beta);
				} else 
				{
					//When there is a possible move for all ghost do the following:
					// 1.advance game copy to the next game state
					// 2.Call Max on the new game state and pass the incremented ply level
					EnumMap<GHOST, MOVE> allghostsMove = createGostsMoveMap(movesAllAgents);
					Game copy_of_game = ToGame.copy();
					MOVE[] movesAllAgents_copy = movesAllAgents.clone();
					copy_of_game.advanceGame(movesAllAgents[0], allghostsMove);
					score = Max(copy_of_game, ply + 1, movesAllAgents_copy,
							alpha, beta);
				}
				//if the current move score is lower than the best among all  previous moves score, the best
				// score is then the current move score
				if (score < bestScore)
					bestScore = score;
				//Apply alpha beta pruning at Min node by checking if the current best score is already smaller than or equal to
				// the beta value. If true, prune else evaluate other moves too
				if (bestScore <= alpha)
					return bestScore;
				//Update beta with the best score for min node along its path to root
				if (bestScore < beta) {
					beta = bestScore;
				}
			}
		}

		return bestScore;
	}

	/***********************************************
	 * Loops through possible moves of all ghosts and selects the move that
	 * minimizes pacman score (Recursive Function)
	 ************************************************/
	/***********************************************
	 * Resets MOVE Array (used for storing all agents move) to null
	 ************************************************/
	private MOVE[] reset(MOVE[] movesAllAgents) {
		for (int i = 0; i < movesAllAgents.length; i++) {
			movesAllAgents[i] = null;
		}
		return movesAllAgents;
	}

	/***********************************************
	 * Resets MOVE Array (used for storing all agents move) to null
	 ************************************************/
	/***********************************************
	 * Converts an input MOVES array for (Ghost4, Ghost3, Ghost2, Ghost1,
	 * Pacman) to an EnumMap<GHOST, MOVE> to pass to one of the existing game
	 * functions
	 ************************************************/
	private EnumMap<GHOST, MOVE> createGostsMoveMap(MOVE[] movesAllAgents) {
		EnumMap<GHOST, MOVE> allghostsMove = new EnumMap<GHOST, MOVE>(
				GHOST.class);
		int i = 0;
		for (MOVE m : movesAllAgents) {
			if (i == 0) {
				i++;
				continue;
			}
			allghostsMove.put(getGhost(i), m);
			i++;
		}
		return allghostsMove;
	}

	/***********************************************
	 * Converts an input MOVES array for (Ghost4, Ghost3, Ghost2, Ghost1,
	 * Pacman) to an EnumMap<GHOST, MOVE> to pass to one of the existing game
	 * functions
	 ************************************************/

	/***********************************************
	 * Map ghost type to an integer value It's an inverse of the getGhost
	 * function
	 ************************************************/
	private int getGhostValue(GHOST g) {
		if (g == g.BLINKY)
			return 1;
		else if (g == g.INKY)
			return 2;
		else if (g == g.PINKY)
			return 3;
		else
			return 4;

	}

	/***********************************************
	 * Map ghost type to an integer value
	 ************************************************/
	/***********************************************
	 * // Map an integer value (used inside program) // provided to the
	 * corresponding GHOST type
	 ************************************************/
	private GHOST getGhost(int val) {
		if (val == 1)
			return GHOST.BLINKY;
		else if (val == 2)
			return GHOST.INKY;
		else if (val == 3)
			return GHOST.PINKY;
		else
			return GHOST.SUE;
	}

	/***********************************************
	 * // Map an integer value (used inside program) // provided to the
	 * corresponding GHOST type
	 ************************************************/
   /******************************************************
    * Evaluates the possible game state and assign score based on
    * various heuristic with each  heuristic assigned a weight to attain a 
    * maximum possible game score 
    * @param currentGame
    * @param wouldBeGame
    * @return
    */
	private int evaluationAtLeaf(Game currentGame, Game wouldBeGame) {
		//Initialize the score with zero
		int scoreForMove = 0;
		//Number of active pills in current game state
		int numActivePillsInCurrrentState = currentGame
				.getNumberOfActivePills();
		//Number of active pills in next possible game state
		int numActivePillsInWouldBeState = wouldBeGame.getNumberOfActivePills();
		//Number of pills eaten by PacMan as the game advances from current to next possible game state
		int pillsEaten = numActivePillsInCurrrentState
				- numActivePillsInWouldBeState;
		//Number of active power pills in current game state
		int numActivePowerPillsInCurrrentState = currentGame
				.getNumberOfActivePowerPills();
		//Number of active power pills in next possible game state
		int numActivePowerPillsInWouldBeState = wouldBeGame
				.getNumberOfActivePowerPills();
		//Number of power pills eaten by PacMan as the game advances from current to next possible game state
		int powerPillsEaten = numActivePowerPillsInCurrrentState
				- numActivePowerPillsInWouldBeState;

		// Making the targets array of active pills in current Game state
		int[] pills = currentGame.getPillIndices();
		int[] powerPills = currentGame.getPowerPillIndices();

		ArrayList<Integer> targets = new ArrayList<Integer>();

		for (int i = 0; i < pills.length; i++) {
			// check which pills are available
			Boolean pillStillAvailable = currentGame.isPillStillAvailable(i);
			if (pillStillAvailable == null)
				continue;
			if (currentGame.isPillStillAvailable(i)) {
				targets.add(pills[i]);
			}
		}

		for (int i = 0; i < powerPills.length; i++) { // check with power pills
														// are available
			Boolean pillStillAvailable = currentGame.isPillStillAvailable(i);
			if (pillStillAvailable == null)
				continue;
			if (currentGame.isPowerPillStillAvailable(i)) {
				targets.add(powerPills[i]);
			}
		}
       //Converts the array list into array
		int targetsArray[] = new int[targets.size()];
		if (!targets.isEmpty()) {
			for (int i = 0; i < targets.size(); i++) {
				targetsArray[i] = targets.get(i);
			}
		}
        //Gets the nearest pill in the current game state
		int nearestPill = currentGame.getClosestNodeIndexFromNodeIndex(
				currentGame.getPacmanCurrentNodeIndex(), targetsArray, DM.PATH);
		//Get the PacMan distance from the nearest pill in next possible game state
		int distOfPacManWouldBeGameFromNearestPill = wouldBeGame
				.getShortestPathDistance(
						wouldBeGame.getPacmanCurrentNodeIndex(), nearestPill);
		//Get the PacMan distance from the nearest pill in current game state
		int distOfPacManCurrentGameFromNearestPill = currentGame
				.getShortestPathDistance(
						currentGame.getPacmanCurrentNodeIndex(), nearestPill);
		//If the current game state level is different from the next possible game state then assign a score of 2000 to that game state
		if (wouldBeGame.getCurrentLevel() != currentGame.getCurrentLevel()) {
			scoreForMove += 2000;
		//If the Pac Man has not eaten any active pills or active power pills in the next possible game state
		//the check if the distance of PacMan from nearest pill had reduced in next possible game state
		//compared to with the distance in current game state
		} else if ((powerPillsEaten + pillsEaten) == 0) {

			if (distOfPacManWouldBeGameFromNearestPill <= distOfPacManCurrentGameFromNearestPill) {
				//Increase the score by delta distance moved by PacMan to reach its new position in next possible game state
				scoreForMove += (distOfPacManCurrentGameFromNearestPill - distOfPacManWouldBeGameFromNearestPill);
			} else {
				//Decrease the score by delta distance moved by PacMan(away from nearest pill) to reach its new position in next possible game state
				scoreForMove -= distOfPacManWouldBeGameFromNearestPill;
			}
		} else {
			//If some pills or power pills are eaten by PacMan in this transition from current game state to next possible game state
			//increase the game by 500 points for each power pill eaten
			//increase the game by 50 points for each power pill eaten
			for (int i = 1; i <= powerPillsEaten; i++) {
				scoreForMove += 500;
			}

			for (int i = 1; i <= pillsEaten; i++) {
				scoreForMove += 50;
			}
		}
        // For each ghost in ghost array do the following
		// 1. If the distance between ghost and PacMan in the next possible game state is less than 12 and ghostLair time is zero
		//    then if the ghost edible time is also zero, then if the distance between ghost and PacMan in the future game state 
		//    is greater than the distance between ghost and PacMan in the current game state, then reduce the score by 4000
		//    If the ghost edible time is more than zero then  if the distance between ghost and PacMan in the future game state 
		//    is less than the distance between ghost and PacMan in the current game state, then increase the score by 1000
		if (wouldBeGame.getCurrentLevel() == currentGame.getCurrentLevel()) {
			int pacManPosInWouldBeGame = wouldBeGame
					.getPacmanCurrentNodeIndex();
			for (GHOST ghost : GHOST.values()) {
				int ghostPosInWouldBeGame = wouldBeGame
						.getGhostCurrentNodeIndex(ghost);

				if ((wouldBeGame.getShortestPathDistance(ghostPosInWouldBeGame,
						pacManPosInWouldBeGame) < 12)
						&& !(wouldBeGame.getGhostLairTime(ghost) > 0)) {
					if (!(wouldBeGame.getGhostEdibleTime(ghost) > 0)) {
						int distFromGhostCurrGame = currentGame
								.getShortestPathDistance(currentGame
										.getGhostCurrentNodeIndex(ghost),
										currentGame.getPacmanCurrentNodeIndex());

						int distFromGhostWouldBeGame = currentGame
								.getShortestPathDistance(wouldBeGame
										.getGhostCurrentNodeIndex(ghost),
										wouldBeGame.getPacmanCurrentNodeIndex());

						if (distFromGhostWouldBeGame < distFromGhostCurrGame) {
							scoreForMove -= 4000;
						}
					} else {
						int distFromGhostCurrGame = currentGame
								.getShortestPathDistance(currentGame
										.getGhostCurrentNodeIndex(ghost),
										currentGame.getPacmanCurrentNodeIndex());

						int distFromGhostWouldBeGame = currentGame
								.getShortestPathDistance(wouldBeGame
										.getGhostCurrentNodeIndex(ghost),
										wouldBeGame.getPacmanCurrentNodeIndex());

						if (distFromGhostWouldBeGame < distFromGhostCurrGame) {
							scoreForMove += 1000;
						}

					}
				}
			}
		}
		return scoreForMove;
	}
}
