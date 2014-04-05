package src;

//TODO: consider making subclass with cars as double for centroids
public class State {
	int carCount; //TODO: see if this is relevant, being updated
	Intersection[][] grid;
	int reward = 0;
	
	public State(int width, int height){
		grid = new Intersection[width][height];
	}
	
	/**
	 * Used by getNextState method
	 * @return a copy of the state
	 */
	public State copyState(){
		State newState = new State(this.grid.length, this.grid[0].length);
		newState.carCount = this.carCount;
		newState.grid = this.copyGrid();
		return newState;
	}
	
	public Intersection[][] copyGrid(){
		Intersection[][] newGrid = new Intersection[this.grid.length][this.grid[0].length];
		for(int i = 0; i < newGrid.length; i++){
			for(int j = 0; j < newGrid.length; j++){
				newGrid[i][j] = new Intersection(i, j, this.grid[i][j].NScars, this.grid[i][j].EWcars);
				newGrid[i][j].isNSInput = this.grid[i][j].isNSInput;
				newGrid[i][j].isEWInput = this.grid[i][j].isEWInput;
			}
		}
		return newGrid;
	}
	
	
	/**
	 * Changes car count and updates the reward
	 * @param increment
	 */
	public void increaseCount(int increment){
		carCount += increment;
	}
}
	