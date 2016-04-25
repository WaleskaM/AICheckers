package ai;

import java.util.Hashtable;

import game.GameState;

public class MinMax {
	private GameTree gameTree;
	private GameState nextState;
	private int step;
	
	public MinMax(GameTree gameTree, int step) {
		this.gameTree = gameTree;
		this.gameTree.getCurrentGameState().setRoot(true);
		this.step = step;
		nextState = null;
	}
	
	public GameState getNextGameState() {
		depthFirstSearch(gameTree, false, step, Float.MIN_VALUE, Float.MAX_VALUE);
		return nextState;
	}
	
	private float depthFirstSearch(GameTree currentTree, boolean minTurn, int currentStep, float alpha, float beta) {
		float retVal, curValue;
		Hashtable<Float, GameState> childValues = new Hashtable<Float, GameState>();
		if(currentTree.getChildreen().size() == 0) return currentTree.getCurrentGameState().evaluate(currentStep);
		
		if(!minTurn) {
			for(GameTree tree : currentTree.getChildreen()) {
				curValue = depthFirstSearch(tree, !minTurn, currentStep + 1, alpha, beta);
				if(curValue > alpha) alpha = curValue;
				if(alpha >= beta) break; // beta pruning
				childValues.put(curValue, tree.getCurrentGameState());		
			}
			retVal = alpha;
		} else {
			for(GameTree tree : currentTree.getChildreen()) {
				curValue = depthFirstSearch(tree, !minTurn, currentStep + 1, alpha, beta);
				if(curValue < beta) beta = curValue;
				if(beta <= alpha) break; // alpha pruning
				childValues.put(curValue, tree.getCurrentGameState());
			}
			retVal = beta;
		}
		
		if(currentTree.getCurrentGameState().isRoot()) {
			float curKey = Float.MIN_VALUE;
			for(Float f : childValues.keySet()) {
				if(isJumpPossible(currentTree.getCurrentGameState())) {
					if(isJump(currentTree.getCurrentGameState(), childValues.get(f)) && f > curKey) 
						curKey = f;
				} 	
			}
			if (curKey > Float.MIN_VALUE) 
				nextState = childValues.get(curKey);
			else 
				nextState = childValues.get(retVal);
		}
		return retVal;
	}
	
	private boolean isJump(GameState currentState, GameState nextState) {
		int[][] curState = currentState.getState();
		int[][] newState = nextState.getState();
		for(int i = 0; i < curState.length; i++) {
			for(int j = 0; j < curState.length; j++) {
				if(curState[i][j] != newState[i][j]) {
					if(curState[i][j] == 1 && newState[i][j] == 0 && i <= 5 && j <= 5) {
						if (newState[i + 2][j + 2] == 1) 
							return true;
					} 
					if(curState[i][j] == 1 && newState[i][j] == 0 && i <= 5 && j >= 2) {
						if (newState[i + 2][j - 2] == 1)
							return true;
					}
				}
			}
		}
		return false;	
	}
	
	private boolean isJumpPossible(GameState state) {
		int[][] curState = state.getState();
		for(int i = 0; i < curState.length; i++) {
			for(int j = 0; j < curState.length; j++) {
				if(curState[i][j] == 1 && i <= 5 && j <= 5) {
					if(curState[i + 1][j + 1] == 2 && curState[i + 2][j + 2] == 0) 
						return true;
				} 
				if(curState[i][j] == 1 && i <= 5 && j >= 2) {
					if(curState[i + 1][j - 1] == 2 && curState[i + 2][j - 2] == 0) 
						return true;
				}
			}
		}
		return false;
	}
	
}
