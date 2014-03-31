
public class State {
	double reward;
	int carCount;
	int turn;
	Intersection[][] grid;
	
	

	//TODO: build the grid 
	public Intersection[][] buildGrid(){
		return null;
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
}
	