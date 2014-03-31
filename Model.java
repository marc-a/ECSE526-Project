package src;


public class Model {
	public State currentState;
	public enum Policy {NS, EW};
	
	/**
	 * Builds initial state and assigns it to currentState 
	 */
	public void buildState(){
		//TODO: write this
	}
	
	
	public State getNextState(Policy[][] policy){
		State newState = currentState.copyState();
		for(int i = 0; i < policy.length ; i ++){
			for(int j = 0; i < policy[0].length ; i ++){
				//TODO: apply policy to each intersection of 
			}
		}
		return newState;
	}
}

