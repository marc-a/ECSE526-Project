package src;

public class State {
	double reward;
	int carCount;
	int turn;
	Intersection[][] grid;
	
	public State(int i){
		this.turn = i;
	}
	
	/**
	 * Displays the current state with number of cars on each intersection and street directions
	 */
	public void printState(){
		
	}
	
	//TODO: see if useful
	public void setReward(int r){
		this.reward = r;
	}
	
	/**
	 * Used by getNextState method
	 * @return a copy of the state
	 */
	public State copyState(){
		try {
			return (State) this.clone(); //TODO: make sure that this copies every Intersection in grid
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}	
	}
	/**
	 * Changes car count and updates the reward
	 * @param increment
	 */
	public void increaseCount(int increment){
		carCount += increment;
		reward = - carCount;
	}
}
	