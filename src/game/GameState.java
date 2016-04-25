package game;

public class GameState {
	private int[][] state;
	private float value;
	// information for minmax algorithm
	private boolean root = false;
	
	public GameState(int size) {
		state = new int[size][size];
	}
	
	public GameState(int[][] state) {
		this.state = state;
	}
	
	public void copy(GameState toCopy) {
		for(int i = 0; i < toCopy.getState().length; i++) {
			for(int j = 0; j < toCopy.getState()[0].length; j++) {
				state[i][j] = toCopy.getPositionState(i, j);			
			}
		}
	}

	public int getPositionState(int row, int column) {
		return state[row][column];
	}

	public void setPositionState(int row, int column, int value) {
		state[row][column] = value;
	}
	
	public int[][] getState() {
		return state;
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	public boolean isRoot() {
		return root;
	}
	
	public void setRoot(boolean root) {
		this.root = root;
	}
	
	// Evaluation function for the current game state
	public float evaluate(int step) {
		int pieceDifference = 0;
		int weightedPieceDifference = 0;
		int weight;
		boolean won = true;
		
		// compute pieceDifference and weightedPieceDifference
		for(int i = 0; i < state.length; i++) {
			for(int j = 0; j < state[0].length; j++) {
				weight = computeWeight(i, j);
				if(state[i][j] == 1) {
					pieceDifference++;
					weightedPieceDifference += 1 * weight; 
				}
				else if(state[i][j] == 2) {
					won = false;
					pieceDifference--;
					weightedPieceDifference -= 1 * weight; 
				}
			}
		}
		int payoff; 
		if(won) 
			payoff = 50 + 10 * weightedPieceDifference + 100;
		else
			payoff = 50 + 10 * weightedPieceDifference;
		float control = 0.042f * pieceDifference;
		float terminal = -0.002f * (50 - step) + 0.083f;
		float payoffStability = 0.341f;
		float controlStability = 0.659f;
		
		value = terminal * payoff + (1 - terminal) * ((50 + 50 * control) * controlStability + payoff * payoffStability);
		return value;
	}
	
	private int computeWeight(int row, int column) {
		int weight = 1;
		int areaRange = state.length / 4;
		if(inArea(areaRange - 1, row, column))
			weight = 4;
		else if(inArea((areaRange - 1) * 2, row, column))
			weight = 3;
		else if(inArea((areaRange - 1) * 3, row, column))
			weight = 2;
		return weight;
	}
	
	private boolean inArea(int areaRange, int row, int column) {
		if(row <= areaRange || row >= ((state.length - 1) - areaRange))
			return true;
		if(column <= areaRange || column >= ((state.length - 1) - areaRange))
			return true;
		return false;
	}
	
	
	public void print() {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				System.out.print(state[i][j]);
			}
			System.out.println();
		}
		System.out.println();
		System.out.println();
	}
	
}
