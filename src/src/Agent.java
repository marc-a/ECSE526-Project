package src;

import src.Model.Policy;

public class Agent {
	int turn = 0, totalReward = 0;
	double averageReward = 0;
	final int CLUSTER_COUNT = 2000; // 6561 = 3^8
	final int MAX_ITERATION = 20000;
	final int AVG_WINDOW_SIZE = 50;

	int policyCount;
	State[] centroids = new State[CLUSTER_COUNT]; // the list of centroid for each cluster
	int[] stateCount = new int[CLUSTER_COUNT]; // number of state subscribed to each cluster
	double[][] QValues; // 
	int[][] QVisitCount; //How many times has each QValue been visited
	boolean[][][] policies = {{{false, false}, {false, false}},{{false, false}, {false, true}},{{false, false}, {true, false}},{{false, false}, {true, true}}, {{false, true}, {false, false}},{{false, true}, {false, true}},{{false, true}, {true, false}},{{false, true}, {true, true}},{{true, false}, {false, false}},{{true, false}, {false, true}},{{true, false}, {true, false}},{{true, false}, {true, true}},{{true, true}, {false, false}},{{true, true}, {false, true}},{{true, true}, {true, false}},{{true, true}, {true, true}}};

	int width, height;
	final double EPS = 0.9, BETA = 0.8, GAMMA = 0.2; //TODO: review these values

	Model model;


	public Agent(){
		model = new Model();
		model.currentState = model.buildState();

		width = model.getWidth();
		height = model.getHeight();

		this.policyCount = (int)Math.pow(2, width*height);

		QValues = new double[policyCount][CLUSTER_COUNT];
		QVisitCount = new int[policyCount][CLUSTER_COUNT];
	}


	private void updateAvgReward(int r){
		double temp = averageReward*Math.min(turn, AVG_WINDOW_SIZE) + r;
		averageReward = (temp/ (Math.min(turn,  AVG_WINDOW_SIZE ) + 1));
	}

	public void doQLearning(){
		//initialise clusters
		
		for(int i = 0; i < centroids.length ; i++){
			centroids[i] = model.buildState();
			stateCount[i] = 0;
		}
		//initCentroids();

		for( turn = 0; turn < MAX_ITERATION; turn ++){
			boolean[][] nextNSGreen = new boolean[width][height];
			int index = 0;
			int currentCluster = getBestClusterIndex(model.currentState);
			//Choose the desired policy
			if(Math.random() < 1 - (EPS - (double)turn/(double)MAX_ITERATION*EPS)){//Be greedy and choose the optimal policy and decrease exploration with time

				double maxValue = Double.NEGATIVE_INFINITY;
				for(int j = 0; j < QValues.length; j++){
					if(QValues[j][currentCluster] > maxValue){
						maxValue = QValues[j][currentCluster];
						index = j;
					}
				}
			}else{//Explore and choose the policy with the smallest visit count
				int temp = Integer.MAX_VALUE;
				for (int i = 0; i < QVisitCount.length; i++) {
					if (QVisitCount[i][currentCluster] < temp) {
						temp = QVisitCount[i][currentCluster];
						index = i;
						if (temp == 0) break; // break because we did not visit this policy yet
					}
				}
			}
			// Extract the selected policy given the selection index
			nextNSGreen = policies[index];
			State nextState = Model.getNextState(model.currentState, nextNSGreen);
			int nextCluster = getBestClusterIndex(nextState);
			double optimalNextValue = Double.NEGATIVE_INFINITY;

			// Get the best Qvalue given the next state
			for(int j = 0; j < QValues.length; j++){
				if(QValues[j][nextCluster] > optimalNextValue){
					optimalNextValue = QValues[j][nextCluster];
				}
			}
			//Add the reward to the total reward and update average reward
			totalReward += nextState.reward;				
			updateAvgReward(nextState.reward);
			
			//Update QValue for the current state
			QVisitCount[index][currentCluster]++;
			QValues[index][currentCluster] += (BETA/(double)QVisitCount[index][currentCluster])*(nextState.reward + GAMMA*optimalNextValue - QValues[index][currentCluster]);
			//System.out.println("Went for "+ nextState.reward);
			//Update currentState and clusters
			model.currentState = nextState;
			//Model.printState(model.currentState);
			
			//System.out.println(""   + averageReward);
			updateCluster(nextState, nextCluster);
		}
		System.out.println("======================");
	}

	private void initCentroids() {
		// Model.MAX_CAR_STREET;
		int centroidsPerDim = (int)Math.floor(Math.pow(CLUSTER_COUNT, 0.125));

		int fullSpace = (int)Math.ceil((double)Model.MAX_CAR_STREET / (double)centroidsPerDim);
		
		int halfSpace = (int)Math.ceil(fullSpace / 2);
		
		int[][][] numberOfCars = new int[width][height][2];
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				numberOfCars[i][j][0] = halfSpace;
				numberOfCars[i][j][1] = halfSpace;
			}
		}
		
		int centroidIndex = 0;
		
		
		for (int i1 = halfSpace; i1 < Model.MAX_CAR_STREET; i1 += fullSpace) {
			for (int i2 = halfSpace; i2 < Model.MAX_CAR_STREET; i2 += fullSpace) {
				for (int i3 = halfSpace; i3 < Model.MAX_CAR_STREET; i3 += fullSpace) {
					for (int i4 = halfSpace; i4 < Model.MAX_CAR_STREET; i4 += fullSpace) {
						for (int i5 = halfSpace; i5 < Model.MAX_CAR_STREET; i5 += fullSpace) {
							for (int i6 = halfSpace; i6 < Model.MAX_CAR_STREET; i6 += fullSpace) {
								for (int i7 = halfSpace; i7 < Model.MAX_CAR_STREET; i7 += fullSpace) {
									for (int i8 = halfSpace; i8 < Model.MAX_CAR_STREET; i8 += fullSpace) {										
										numberOfCars[0][0][0] = i1;	
										numberOfCars[0][0][1] = i2;
										numberOfCars[0][1][0] = i3;
										numberOfCars[0][1][1] = i4;
										numberOfCars[1][0][0] = i5;
										numberOfCars[1][0][1] = i6;
										numberOfCars[1][1][0] = i7;
										numberOfCars[1][1][1] = i8;
										centroids[centroidIndex] = new State(width, height, numberOfCars);
										centroidIndex++;
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void updateCluster(State newState, int clusterIndex){
		int count = stateCount[clusterIndex];
		if(count == 0){
			centroids[clusterIndex] = newState;
		}else{
			for(int i =0; i < newState.grid.length; i++){
				for(int j =0; j < newState.grid[0].length; j++){
					//TODO: make sure that this is correct averaging and updating of centroid
					centroids[clusterIndex].grid[i][j].NScars = (count*centroids[clusterIndex].grid[i][j].NScars + newState.grid[i][j].NScars)/(count + 1) + 1; //TODO: added + 1, maybe its good to round up, maybe its not.. Check!
					centroids[clusterIndex].grid[i][j].EWcars = (count*centroids[clusterIndex].grid[i][j].EWcars + newState.grid[i][j].EWcars)/(count + 1) + 1; //TODO: see above
				}
			}
		}
		stateCount[clusterIndex]++;
	}

	public int getBestClusterIndex(State s){
		//TODO: See if there is need to save states to update the clusters
		double minDistance = Double.POSITIVE_INFINITY;
		double distance = 0;
		int index = 0;
		//TODO: check that this is done right
		for(int k = 0 ; k < centroids.length; k++){
			distance = 0;
			for(int i = 0; i < width ; i++){
				for(int j = 0; j < height ; j++){
					distance += Math.pow(s.grid[i][j].NScars - centroids[k].grid[i][j].NScars, 2);
					distance += Math.pow(s.grid[i][j].EWcars - centroids[k].grid[i][j].EWcars, 2);
				}
			}
			if(distance < minDistance){
				minDistance = distance;
				index = k;
			}
		}
		//System.out.println("Distance: " + minDistance);
		return index;
	}

	//TODO: make this scalable
	public void doIntuitiveStrategy(){
		for(turn = 0; turn < MAX_ITERATION; turn++){
			State nextState;
			if(turn % 2 == 0){
				boolean[][] NSGreen = {{true, false} , {false, true}}; 
				nextState = Model.getNextState(model.currentState, NSGreen);
			}else{
				boolean[][] NSGreen = {{false, true} , {true, false}};
				nextState = Model.getNextState(model.currentState, NSGreen);

			}
			//Add the reward to the total reward 
			totalReward += nextState.reward;
			updateAvgReward(nextState.reward);
			

			//Update currentState
			model.currentState = nextState;
			//Model.printState(model.currentState);
	//		System.out.println(""   + averageReward);
		}
		
		System.out.println("=========================================");
	}


	public void doRandomStrategy(){
		for(turn = 0; turn < MAX_ITERATION; turn++){
			State nextState;
			double temp =  Math.random();
			int index = (int)(temp*policyCount);
			boolean[][] NSGreen = policies[index]; 
			nextState = Model.getNextState(model.currentState, NSGreen);
			//Add the reward to the total reward 
			totalReward += nextState.reward;

			//Update currentState
			model.currentState = nextState;
			//Model.printState(model.currentState);
		}
	}

	public void testCentroidUpdate(){
		centroids[0] = model.buildState();
		State testState  = model.buildState();
		System.out.println("centroid is at " + centroids[0].grid[0][0].NScars + " with " + stateCount[0] + " states and testState is at " + testState.grid[0][0].NScars);
		updateCluster(testState, 0);
		testState = model.buildState();
		System.out.println("centroid is at " + centroids[0].grid[0][0].NScars + " with " + stateCount[0] + " states and testState is at " + testState.grid[0][0].NScars);
		updateCluster(testState, 0);
		testState = model.buildState();
		System.out.println("centroid is at " + centroids[0].grid[0][0].NScars + " with " + stateCount[0] + " states and testState is at " + testState.grid[0][0].NScars);
		updateCluster(testState, 0);
		testState = model.buildState();
		System.out.println("centroid is at " + centroids[0].grid[0][0].NScars + " with " + stateCount[0] + " states and testState is at " + testState.grid[0][0].NScars);

	}


	public static void main(String[] args){
		Agent agent1 = new Agent();
		Agent agent2 = new Agent();
		Agent agent3 = new Agent();
		//	agent.testCentroidUpdate();
		agent2.doIntuitiveStrategy();
		agent1.doQLearning();
		agent3.doRandomStrategy();
		//	agent.testCentroidUpdate();
		//	agent.doNaiveStrategy();

		System.out.println("Total Q reward is " + agent1.totalReward);
		System.out.println("Total intuitive reward is " + agent2.totalReward);
		System.out.println("Total random reward is " + agent3.totalReward);
		
		for (int i = 0; i < agent1.QVisitCount.length; i++) {
			//System.out.println(Arrays.toString(agent1.QVisitCount[i]));
		}
	}
	//TODO: make this scalable in the future
	public void populatePolcies(){
		/*	policies = new boolean[policyCount][width][height];
		for(int k = 0; k < policyCount; k ++){
			for(int i = 0; i < width; i++){
				for(int j = 0; j < height ; j++){
					policies[k][i][j] = k % ;
				}
			}
		}

		 */
	}

}
