package game;

import gui.BoardGrid;
import ai.GameTree;
import ai.MinMax;

public class Game {
	final static int SIZE = 8;
	private static BoardGrid board;
	private static GameState currentState;
	
	public static void main(String[] args) {
		
		int[][] gameBoard = initGameBoard();
		
		currentState = new GameState(gameBoard);
		board = new BoardGrid(SIZE);
		board.visualizeState(currentState);
		
		boolean turnComplete = false;
		boolean gameFinished = false;
		
		while(!gameFinished) {
			// players turn
			while(!turnComplete) {
				try{
					takeTurn();	
					turnComplete = true;
				} catch(Exception e) {
					turnComplete = false;
					//e.printStackTrace(System.err);
				}
			}
			
			if(isGameFinished()) break;
			
			// AIs turn
			GameTree gt = new GameTree(currentState);
			gt.createGameTree(5);
			MinMax mm = new MinMax(gt, 1);
			currentState = mm.getNextGameState();
			board.visualizeState(currentState);
			
			gameFinished = isGameFinished();
			turnComplete = false;
		}
		
	}
	
	private static boolean isGameFinished() {
		int[][] state = currentState.getState();
		int playersPieces = 0;
		int aisPieces = 0;
		
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < SIZE; j++) {
				if(state[i][j] == 1) 
					playersPieces++;
				else if(state[i][j] == 2)
					aisPieces++;
			}
		}
		
		if(playersPieces == 0 || aisPieces == 0)
			return true;
		else
			return false;
	}
	
	private static void takeTurn() throws Exception {
		Position oldPosition = null;
		Position newPosition = null;
		
		board.clearAllButtons();
		
		while(oldPosition == null || newPosition == null) {
			if(oldPosition == null) oldPosition = board.checkButtons();
			if(newPosition == null) newPosition = board.checkButtons();
		}
		if(isMoveValid(oldPosition.getRow(), oldPosition.getColumn(), newPosition.getRow(), newPosition.getColumn())) {
			changeBoardAndStateMove(oldPosition, newPosition);
		} else if(isJumpValid(oldPosition.getRow(), oldPosition.getColumn(), newPosition.getRow(), newPosition.getColumn())) {
			changeBoardAndStateJump(oldPosition, newPosition);
			if(isJumpPossible()) takeTurn();
		} else {
			throw new Exception("not a valid turn");
		}
	}
	
	private static void changeBoardAndStateMove(Position oldPosition, Position newPosition) {
		currentState.setPositionState(oldPosition.getRow(), oldPosition.getColumn(), 0);
		currentState.setPositionState(newPosition.getRow(), newPosition.getColumn(), 2);
		board.visualizeState(currentState);
	}
	
	private static void changeBoardAndStateJump(Position oldPosition, Position newPosition) {
		currentState.setPositionState(oldPosition.getRow(), oldPosition.getColumn(), 0);
		currentState.setPositionState(newPosition.getRow(), newPosition.getColumn(), 2);
		if(oldPosition.getColumn() < newPosition.getColumn()) {
			currentState.setPositionState(oldPosition.getRow() - 1, oldPosition.getColumn() + 1, 0);
		} else {
			currentState.setPositionState(oldPosition.getRow() - 1, oldPosition.getColumn() - 1, 0);
		}
		board.visualizeState(currentState);
	}
	
	private static int[][] initGameBoard() {
		int[][] gameBoard = new int[SIZE][SIZE];
		
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < SIZE; j++) {
				switch(i) {
				case 0: 										
				case 2:	if((j % 2) == 0) gameBoard[i][j] = 1;	//if number is even then set 1 (men)
						continue;	
				case 1: if((j % 2) != 0) gameBoard[i][j] = 1;	//if number is odd then set 1 (men)
						continue;
				//	0 = no piece at this position
				//  1 = men of player 1 (AI)
				//  2 = men of player 2 
				case 6: if((j % 2) == 0) gameBoard[i][j] = 2;	//if number is even then set 2 (men)
						continue;
				case 5:
				case 7: if((j % 2) != 0) gameBoard[i][j] = 2;	//if number is odd then set 2 (men)
						continue;
				}												//because all elements of the array are initialized 
																//with 0, there is no default necessary
			}
		}
		return gameBoard;
	}
	
	// users turn validation
	
	private static boolean isJumpValid(int oldRow, int oldColumn, int newRow, int newColumn) {
		if(!inRange(oldRow) || !inRange(oldColumn) || !inRange(newRow) || !inRange(newColumn)) return false;
		if(currentState.getPositionState(oldRow, oldColumn) == 2 && currentState.getPositionState(newRow, newColumn) == 0) {
			if(oldRow == (newRow + 2) && 2 == Math.abs(oldColumn - newColumn)) {
				if(newColumn > oldColumn) {
					if(currentState.getPositionState(oldRow - 1, oldColumn + 1) == 1) return true;
				} else {
					if(currentState.getPositionState(oldRow - 1, oldColumn - 1) == 1) return true;
				}
			}
		}
		return false;
	}
	
	private static boolean isMoveValid(int oldRow, int oldColumn, int newRow, int newColumn) {
		if(isJumpPossible()) return false;
		if(!inRange(oldRow) || !inRange(oldColumn) || !inRange(newRow) || !inRange(newColumn)) return false;
		if(1 != Math.abs(oldRow - newRow) || 1 != Math.abs(oldColumn - newColumn)) return false;
		if(currentState.getPositionState(oldRow, oldColumn) == 2 && currentState.getPositionState(newRow, newColumn) == 0) return true;
		return false;
	}
	
	private static boolean inRange(int x) {
		if(x >= 0 && x < SIZE) return true;
		else return false;
	}
	
	private static boolean isJumpPossible() {
		int[][] curState = currentState.getState();
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < SIZE; j++) {
				if(curState[i][j] == 2 && i >= 2 && j <= 5) {
					if(curState[i - 1][j + 1] == 1 && curState[i - 2][j + 2] == 0) 
						return true;
				} 
				if(curState[i][j] == 2 && i >= 2 && j >= 2) {
					if(curState[i - 1][j - 1] == 1 && curState[i - 2][j - 2] == 0) 
						return true;
				}
			}
		}
		return false;
	}
	
}
