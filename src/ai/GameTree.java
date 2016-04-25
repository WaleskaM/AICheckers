package ai;

import java.util.LinkedList;
import java.util.List;

import game.GameState;

public class GameTree {
	private GameState currentGameState;
	private List<GameTree> childreen;
	private GameTree parent;
	
	public GameTree(GameState gameState) {
		currentGameState = gameState;
		childreen = new LinkedList<GameTree>();
		parent = null;
	}
	
	GameTree(GameState gameState, GameTree parent) {
		currentGameState = gameState;
		childreen = new LinkedList<GameTree>();
		this.parent = parent;
	}
	
	private void createAllPossibleNextStates(boolean opponentMove) {
		int turn = 1;
		if(opponentMove) turn = 2;
		for(int i = 0 ; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(currentGameState.getPositionState(i, j) == turn) {
					// check for jumping moves
					if(checkJumps(i, j, currentGameState, opponentMove)) {
						//all possible states are already created in checkJumps
					} else {
						// check for regular move
						if(checkMoveLeft(i, j, opponentMove)) {
							GameState newChild = new GameState(8);
							newChild.copy(currentGameState);
							newChild.setPositionState(i, j, 0);
							newChild.setPositionState(getNextMovePositionRow(i, opponentMove), (j - 1), turn);
							addChild(newChild);
						}
						if(checkMoveRight(i, j, opponentMove)) {
							GameState newChild = new GameState(8);
							newChild.copy(currentGameState);
							newChild.setPositionState(i, j, 0);
							newChild.setPositionState(getNextMovePositionRow(i, opponentMove), (j + 1), turn);
							addChild(newChild);
						}
					}
				}
			}
		}
	}
	
	// Recursively checking if a jump is possible
	// adding a new child GameState for each jump combination
	private boolean checkJumps(int row, int column, GameState state, boolean opponent) {
		boolean retVal = false;
		int turn = 1;
		if(opponent) turn = 2;
		GameState nextStateLeftJump = new GameState(8);
		nextStateLeftJump.copy(state);
		GameState nextStateRightJump = new GameState(8);
		nextStateRightJump.copy(state);
		
		if(checkJumpLeft(row, column, opponent)) {
			// set old location to 0
			nextStateLeftJump.setPositionState(row, column, 0);
			// set new location
			nextStateLeftJump.setPositionState(getNextJumpPositionRow(row, opponent), (column - 2), turn);
			// set location of kicked piece to 0
			nextStateLeftJump.setPositionState(getKickedPositionRow(row, opponent), (column - 1), 0);
			if(!checkJumps(getNextJumpPositionRow(row, opponent), (column - 2), nextStateLeftJump, opponent))
				addChild(nextStateLeftJump);
			retVal = true;
		} 
		if(checkJumpRight(row, column, opponent)) {
			// set old location to 0
			nextStateRightJump.setPositionState(row, column, 0);
			// set new location
			nextStateRightJump.setPositionState(getNextJumpPositionRow(row, opponent), (column + 2), turn);
			// set location of kicked piece to 0
			nextStateRightJump.setPositionState(getKickedPositionRow(row, opponent), (column + 1), 0);
			if(!checkJumps(getNextJumpPositionRow(row, opponent), (column + 2), nextStateRightJump, opponent))
				addChild(nextStateRightJump);
			retVal = true;
		}	
		return retVal;
	}
	
	private boolean checkJumpLeft(int row, int column, boolean opponent) {
		boolean retVal = false;
		int opponentNumber = 2;
		if(opponent) opponentNumber = 1;
		// check if target location is in bound and empty 
		if(column >= 2 && checkRowBoundariesJump(opponent, row) && currentGameState.getPositionState(getNextJumpPositionRow(row, opponent), (column - 2)) == 0) {
			// check if opponent piece is next to own piece
			if(currentGameState.getPositionState(getKickedPositionRow(row, opponent), (column - 1)) == opponentNumber)
				retVal = true;
		}
		return retVal;
	}
	
	private boolean checkJumpRight(int row, int column, boolean opponent) {
		boolean retVal = false;
		int opponentNumber = 2;
		if(opponent) opponentNumber = 1;
		// check if target location is in bound and empty 
		if(column <= 5 && checkRowBoundariesJump(opponent, row) && currentGameState.getPositionState(getNextJumpPositionRow(row, opponent), (column + 2)) == 0) {
			// check if opponent piece is next to own piece
			if(currentGameState.getPositionState(getKickedPositionRow(row, opponent), (column + 1)) == opponentNumber)
				retVal = true;
		}
		return retVal;
	}
	
	private boolean checkRowBoundariesJump(boolean opponent, int row) {
		if(opponent) {
			if(row <= 2) return false;
		} else {
			if(row >= 5) return false;
		}
		return true;
	}
	
	private boolean checkRowBoundariesMove(boolean opponent, int row) {
		if(opponent) {
			if(row == 0) return false;
		} else {
			if(row == 7) return false;
		}
		return true;
	}
	
	private boolean checkMoveLeft(int row, int column, boolean opponent) {
		boolean retVal = false;
		//check if target location is in bound and empty
		if(column > 0 && checkRowBoundariesMove(opponent, row) && currentGameState.getPositionState(getNextMovePositionRow(row, opponent), (column - 1)) == 0)
			retVal = true;
		return retVal;
	}
	
	private boolean checkMoveRight(int row, int column, boolean opponent) {
		boolean retVal = false;
		//check if target location is in bound and empty
		if(column < 7 && checkRowBoundariesMove(opponent, row) && currentGameState.getPositionState(getNextMovePositionRow(row, opponent), (column + 1)) == 0)
			retVal = true;
		return retVal;
	}
	
	private int getNextMovePositionRow(int current, boolean opponent) {
		if(!opponent)
			return (current + 1);
		else
			return (current - 1);
	}
	
	private int getNextJumpPositionRow(int current, boolean opponent) {
		if(!opponent)
			return (current + 2);
		else
			return (current - 2);
	}
	
	private int getKickedPositionRow(int current, boolean opponent) {
		if(!opponent)
			return (current + 1);
		else
			return (current - 1);
	}
	
	public void addChild(GameState gameState) {
		childreen.add(new GameTree(gameState, this));
	}
	
	public GameTree getParent() {
		return parent;
	}
	
	public List<GameTree> getChildreen() {
		return childreen;
	}
	
	public GameState getCurrentGameState() {
		return currentGameState;
	}
	
	// Recursively creating the Game Tree for given depth
	public void createGameTree(int depth) {
		createNextLayer(depth, false);
	}
	
	private void createNextLayer(int depth, boolean opponent) {
		if(depth <= 0) return;
		createAllPossibleNextStates(opponent);
		for(GameTree child : childreen) {
			child.createNextLayer((depth - 1), !opponent);
		}	
	}
	
}
