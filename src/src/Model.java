package src;


public class Model {
	public State currentState;

	public enum Policy {NS, EW}; //TODO: could be replaced by boolean
	public enum Direction{N, S, E, W}
	public static Direction[] NSDir = {Direction.N, Direction.S};
	public static Direction[] EWDir = {Direction.E, Direction.W};	

	static final double TURN_PROB = 0.1;
	static final int MAX_CAR_STREET = 30;
	static final int MAX_CAR_PASS = 8;
	static final int MAX_CAR_IN = 5; //check relevance

	/**
	 * Builds initial state and assigns it to currentState 
	 */
	public State buildState(){
		//TODO: write this
		int NScars, EWcars;
		State s = new State(2, 2);
		for(int x = 0; x < s.grid.length ; x ++){
			for(int y = 0; y < s.grid[0].length ; y ++){
				NScars = generateCars();
				EWcars = generateCars();
				s.grid[x][y] = new Intersection(x, y, NScars, EWcars);
				s.increaseCount(NScars + EWcars);
				//TODO: check if below conditions are true for input/output node
				if(x == 0 && EWDir[y] == Direction.E ||  x == s.grid.length - 1 && EWDir[y] == Direction.W  ){
					s.grid[x][y].isEWInput = true;
				}
				if(y == 0 && NSDir[x] == Direction.S || y == s.grid[0].length - 1 && NSDir[x] == Direction.N){
					s.grid[x][y].isNSInput = true;
				}
			}
		}
		return s;
	}

	/**
	 * @param s starting state
	 * @param NSGreen policy applied to the state s: true if this intersection Green on the NS line
	 * @return
	 */
	public static State getNextState(State s, boolean[][] NSGreen){
		State newState = s.copyState(); //TODO: test out copy
		int carsPassed, carsTurned, carsFwd, maxFwd, maxTurned, maxPassed, carsCleared = 0;
		for(int i = 0; i < NSGreen.length ; i++){
			for(int j = 0; j < NSGreen[0].length ; j++){
				//Apply policy to each intersection of
				carsTurned = 0;
				carsPassed = 0;
				carsFwd = 0;
				maxFwd = 0;
				maxTurned = 0;
				maxPassed = 0;
				if(NSGreen[i][j] == true){
					//decrement car count
					maxPassed = Math.min(s.grid[i][j].NScars, MAX_CAR_PASS);
					
					//Apply changes to adjacent intersection as fit/decrement state carCount
					for(int k = 0; k < maxPassed; k++){
						if(Math.random() < TURN_PROB){ //TODO: check that this gives right distribution
							maxTurned++;
						}
					}
					maxFwd = maxPassed - maxTurned;
					//Apply change to adjacent intersections according to direction TODO: check if these are correct
					if(NSDir[i] == Direction.N){
						if(j > 0){
							carsFwd = Math.min(MAX_CAR_STREET - s.grid[i][j - 1].NScars, maxFwd);
							newState.grid[i][j - 1].NScars += carsFwd; //TODO: add car removal
						}else{
							carsFwd = maxFwd;
							carsCleared += maxFwd;
						}
					}else if(NSDir[i] == Direction.S){
						if(j < newState.grid[0].length - 1){
							carsFwd =  Math.min(MAX_CAR_STREET - s.grid[i][j + 1].NScars, maxFwd);
							newState.grid[i][j + 1].NScars += carsFwd;
						}else{
							carsFwd = maxFwd;
							carsCleared += maxFwd;
						}
					}
					if(EWDir[j] == Direction.E){
						if(i < newState.grid[0].length  - 1){
							carsTurned = Math.min(MAX_CAR_STREET - s.grid[i + 1][j].EWcars, maxTurned);
							newState.grid[i + 1][j].EWcars += carsTurned;
						}else{
							carsTurned = maxTurned;
							carsCleared += maxTurned;
						}
					}else if(EWDir[j] == Direction.W){
						if(i > 0){
							carsTurned = Math.min(MAX_CAR_STREET - s.grid[i - 1][j].EWcars, maxTurned);
							newState.grid[i - 1][j].EWcars += carsTurned;
						}else{
							carsTurned = maxTurned;
							carsCleared += carsTurned;
						}
					}
					carsPassed = carsTurned + carsFwd;
					System.out.println("For (" + i + "," + j + ") is " + carsTurned + " cars turned EW, " + carsFwd + " went forward NS. " + carsPassed + " passed in total.");//TODO: remove this
					newState.grid[i][j].NScars -= carsPassed; 
				}else{
					//Apply changes to adjacent intersection as fit/decrement state carCount
					maxPassed = Math.min(s.grid[i][j].EWcars, MAX_CAR_PASS);
					for(int k = 0; k < maxPassed; k++){
						if(Math.random() < TURN_PROB){ //TODO: check that this gives right distribution
							maxTurned++;
						}
					}
					maxFwd = maxPassed - maxTurned;
					if(NSDir[i] == Direction.N){ //the turning car should appear at the next intersection depending on the direction of the perpendicular
						if(j > 0){
							carsTurned = Math.min(MAX_CAR_STREET - s.grid[i][j - 1].NScars, maxTurned);
							newState.grid[i][j - 1].NScars += carsTurned;
						}else{
							carsCleared += maxTurned;
							carsTurned = maxTurned;
						}
					}else if(NSDir[i] == Direction.S ){
						if(j < newState.grid[0].length - 1){
							carsTurned = Math.min(MAX_CAR_STREET - s.grid[i][j + 1].NScars, maxTurned);
							newState.grid[i][j + 1].NScars += carsTurned;
						}else{
							carsCleared += maxTurned;
							carsTurned = maxTurned;
						}
					}
					if(EWDir[j] == Direction.E ){
						if(i < newState.grid[0].length  - 1){
							carsFwd = Math.min(MAX_CAR_STREET - s.grid[i + 1][j].EWcars, maxFwd);//TODO: check this
							newState.grid[i + 1][j].EWcars += carsFwd;
						}else{
							carsFwd = maxFwd;
							carsCleared += maxFwd;
						}
					}else if( EWDir[j] == Direction.W){
						if(i > 0){
							carsFwd = Math.min(MAX_CAR_STREET - s.grid[i - 1][j].EWcars, maxFwd);//TODO: check this
							newState.grid[i - 1][j].EWcars += carsFwd;
						}else{
							carsFwd = maxFwd;
							carsCleared += maxFwd;
						}
					}
					carsPassed = carsFwd + carsTurned;
					System.out.println("For (" + i + "," + j + ") is " + carsTurned + " cars turned NS, " + carsFwd + " went forward EW. " + carsPassed + " passed in total.");//TODO: remove this//TODO: remove this
					newState.grid[i][j].EWcars -= carsPassed;
				}
			}
		}
		System.out.println("Cars Cleared = " + carsCleared);
		newState.reward = carsCleared;
		// input cars into this next state and decrement reward by input failures
		inputCars(newState); 
		return newState;
	}

	
	/**
	 * Applies car inputs to the state according to chosen distribution policy
	 * @param s
	 */
	private static void inputCars(State s){
		int inputCount = 0;
		int input = 0, maxInput;
		for(int i = 0; i < s.grid.length ; i ++){
			for(int j = 0; j < s.grid[0].length ; j ++){
				if(s.grid[i][j].isNSInput){					
					maxInput =  (int)(Math.random()*(MAX_CAR_IN )); //TODO: refine this
					input = Math.min(MAX_CAR_STREET - s.grid[i][j].NScars, maxInput);
					s.grid[i][j].NScars += input;
					//decrement reward according to number of cars denied
					s.reward -= maxInput - input;
					System.out.println("cars in from NS (" + i + "," + j +") is " + input + " and reward was brought down by " + (maxInput - input));
				}
				if(s.grid[i][j].isEWInput){					
					maxInput = (int)(Math.random()*MAX_CAR_IN + 2);
					input = Math.min(MAX_CAR_STREET - s.grid[i][j].EWcars, maxInput);
					s.grid[i][j].EWcars += input;
					//decrement reward according to number of cars denied
					s.reward -= maxInput - input;
					System.out.println("cars in from EW (" + i + "," + j +") is " + input + " and reward was brought down by " + (maxInput - input));
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

	public static void printState(State s){
		int line = 2, column = 0, j = -1;
		boolean isHorStreet;
		System.out.print("===================================\n");
		for(int i=0; i < NSDir.length; i++){
			System.out.print("\t" + NSDir[i].toString());
		}
		System.out.print("\t\n");
		for(int i=0; i < NSDir.length; i++){
			System.out.print("\t|");
		}
		System.out.print("\t\n");
		for(; line < 3 * s.grid[0].length + 2; line++){
			isHorStreet = (line)% 3 == 0;
			if(isHorStreet) {
				j = line/3 - 1;
				System.out.print("-  ");
			}
			for(int i = 0; i < s.grid.length; i++){
				if(isHorStreet) { 	// Right at the intersection row
					if(EWDir[j] == Direction.E){
						System.out.print(" "+ s.grid[i][j].EWcars + "\t+ - " );
					}else{
						System.out.print("- \t+  " + s.grid[i][j].EWcars);
					}
				}else if(line % 3 == 2){ 	// Right above the intersection row
					System.out.print("\t");
					if(NSDir[i] == Direction.S){
						System.out.print(""+ s.grid[i][j + 1].NScars);
					}else{
						System.out.print("|");
					}
				}else{ // Right below the intersection row
					System.out.print("\t");
					if(NSDir[i] == Direction.N){
						System.out.print(""+ s.grid[i][j].NScars);
					}else{
						System.out.print("|");
					}
				}
			}
			if(isHorStreet){
				System.out.println(" - " + EWDir[j].toString());
			}
			System.out.print("\n");
		}
	}

	public void modelTest(){
		System.out.println("");
		System.out.println("=======Testing generateCars()=========");
		System.out.println("output: 1)" + generateCars() + " 2)" + generateCars() + " 3)" + generateCars());


		System.out.println("output: 1)" + generateCars() + " 2)" + generateCars() + " 3)" + generateCars());

		System.out.println("=======Testing getNextState()=========");
		currentState = buildState();
		printState(currentState);
		boolean[][] NSGreen1 = {{true, true}, {true, true}};
		currentState = getNextState(currentState, NSGreen1);
		printState(currentState);
		boolean[][] NSGreen2 = {{false, false}, {false, false}};
		currentState = getNextState(currentState, NSGreen2);
		printState(currentState);
		inputCars(currentState);
		printState(currentState);
		inputCars(currentState);
		printState(currentState);
		inputCars(currentState);
		printState(currentState);
		inputCars(currentState);
		printState(currentState);

	}
	
	public int  getWidth(){
		return NSDir.length;
	}

	public int  getHeight(){
		return EWDir.length;
	}
	
	public static void main(String[] args){
		Model model = new Model();
		model.modelTest();

	}

}

