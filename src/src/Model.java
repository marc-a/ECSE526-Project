package src;
public class Model {
	public State currentState;
	public enum Policy {NS, EW};
	final int MAX_CAR_PASS = 5;
	/**
	 * Builds initial state and assigns it to currentState 
	 */
	public void buildState(){
		//TODO: write this
	}
	
	
	public State getNextState(Policy[][] policy){
		State newState = currentState.copyState();
		for(int i = 0; i < policy.length ; i ++){
			for(int j = 0; j < policy[0].length ; j ++){
				//TODO: apply policy to each intersection of 
				if(policy[i][j] == Policy.NS){
					//decrement car count
					newState.grid[i][j].NScars = Math.max(0, newState.grid[i][j].NScars - MAX_CAR_PASS); 
					//TODO: apply changes to adjacent intersection as fit/decrement state carCount
				}else{
					newState.grid[i][j].EWcars = Math.max(0, newState.grid[i][j].EWcars - MAX_CAR_PASS);
					//TODO: apply changes to adjacent intersection as fit/decrement state carCount
				}
			}
		}
		// input cars into this next state
		inputCars(newState);
		return newState;
	}
	
	/**
	 * Applies car inputs to the state according to chosen distribution policy
	 * @param s
	 */
	private void inputCars(State s){
		int inputCount = 0;
		for(int i = 0; i < s.grid.length ; i ++){
			for(int j = 0; j < s.grid[0].length ; j ++){
				if(s.grid[i][j].isInput){					
					//TODO: add input policy by looping through all input intersections
				}
			}
		}
		// increment total car count
		s.increaseCount(inputCount);
	}
}

