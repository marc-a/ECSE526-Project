package src;

import src.Intersection.Direction;

public class Model {
	public State currentState;
	
	public enum Policy {NS, EW};
	public Direction[] NSDir = {Direction.N, Direction.S, Direction.N, Direction.S};
	public Direction[] EWDir = {Direction.E, Direction.W, Direction.W, Direction.E};	
	
	static final double TURN_PROB = 0.1;
	static final int MAX_CAR_STREET = 10;
	static final int MAX_CAR_PASS = 5;
	
	/**
	 * Builds initial state and assigns it to currentState 
	 */
	public void buildState(){
		//TODO: write this
		int NScars, EWcars;
		State s = new State(0);
		for(int x = 0; x < s.grid.length ; x ++){
			for(int y = 0; y < s.grid[0].length ; y ++){
					NScars = generateCars();
					EWcars = generateCars();
					s.grid[x][y] = new Intersection(x, y, NScars, EWcars, NSDir[x], EWDir[y]);
					s.increaseCount(NScars + EWcars);
					//TODO: check if below conditions are true for input/output node
					if(x == 0 && EWDir[y] == Direction.W || y == 0 && NSDir[x] == Direction.N || x == s.grid.length - 1 && EWDir[y] == Direction.E || y == s.grid[0].length - 1 && EWDir[x] == Direction.S){
						s.grid[x][y].isOutput = true;
					}else if(x == 0 && EWDir[y] == Direction.E || y == 0 && NSDir[x] == Direction.S || x == s.grid.length - 1 && EWDir[y] == Direction.W || y == s.grid[0].length - 1 && EWDir[x] == Direction.N){
						s.grid[x][y].isInput = true;
					}
			}
		}
		currentState = s;
	}

	/**
	 * @param s starting state
	 * @param policy applied to the state s
	 * @return
	 */
	public static State getNextState(State s, Policy[][] policy){
		State newState = s.copyState();
		newState.turn += 1;
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
	private static void inputCars(State s){
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
	
	
	/**
	 * generates a random amount of cars
	 * @return
	 */
	private int generateCars(){
		int carCount = (int)( (MAX_CAR_STREET + 1)*Math.random());
		return carCount;
	}
	
	public void modelTest(){
		System.out.println("");
		System.out.println("=======Testing generateCars()=========");
		System.out.println("output: 1)" + generateCars() + " 2)" + generateCars() + " 3)" + generateCars());
		
	}
}

