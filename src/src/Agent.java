package src;

import src.Model.Policy;

public class Agent {
	int turn = 0, totalReward = 0;
	final int CLUSTER_COUNT = 200;
	final int MAX_ITERATION = 2000;
	
	int policyCount;
	State[] centroids = new State[CLUSTER_COUNT]; // the list of centroid for each cluster
	int[] stateCount = new int[CLUSTER_COUNT]; // number of state subscribed to each cluster
	double[][] QValues; // 
	boolean[][][] policies = {{{false, false}, {false, false}},{{false, false}, {false, true}},{{false, false}, {true, false}},{{false, false}, {true, true}}, {{false, true}, {false, false}},{{false, true}, {false, true}},{{false, true}, {true, false}},{{false, true}, {true, true}},{{true, false}, {false, false}},{{true, false}, {false, true}},{{true, false}, {true, false}},{{true, false}, {true, true}},{{true, true}, {false, false}},{{true, true}, {false, true}},{{true, true}, {true, false}},{{true, true}, {true, true}}};
	
	int width, height;
	final double EPS = 0.9, BETA = 0.1, GAMMA = 0.05; //TODO: review these values
	
	Model model;
	

	public Agent(){
		model = new Model();
		model.currentState = model.buildState();
		
		width = model.getWidth();
		height = model.getHeight();
		
		this.policyCount = (int)Math.pow(2, width*height);
		
		QValues = new double[policyCount][CLUSTER_COUNT];
		System.out.println("number of policies = " + policies.length); //TODO: do this

	}

	
	public void doQLearning(){
		//initialise clusters
		for(int i = 0; i < centroids.length ; i++){
			centroids[i] = model.buildState();
			stateCount[i] = 0;
		}
		
		for( turn = 0; turn < MAX_ITERATION; turn ++){
			boolean[][] nextNSGreen = new boolean[width][height];
			int index = 0;
			int currentCluster = getBestClusterIndex(model.currentState);
			//Choose the desired policy
			if(Math.random() < 1 - (EPS - (double)turn/(double)MAX_ITERATION*EPS)){//Be greedy and choose the optimal policy and decrease exploration with time
				
				double maxValue = Double.NEGATIVE_INFINITY; //TODO: review why i could have wanted to make it 0 instead
				for(int j = 0; j < QValues.length; j++){
					if(QValues[j][currentCluster] > maxValue){
						maxValue = QValues[j][currentCluster];
						index = j;
					}
				}
			}else{//Explore and choose a random action
				double temp =  Math.random();
				index = (int)(temp*policyCount);
			}
			// Extract the selected policy given the selection index
			nextNSGreen = policies[index];
			State nextState = Model.getNextState(model.currentState, nextNSGreen);
			int nextCluster = getBestClusterIndex(nextState);
			double optimalNextValue = 0;
			
			// Get the best Qvalue given the next state
			for(int j = 0; j < QValues.length; j++){
				if(QValues[j][nextCluster] > optimalNextValue){
					optimalNextValue = QValues[j][nextCluster];
				}
			}
			//Add the reward to the total reward 
			totalReward += nextState.reward;
			
			//Update QValue for the current state
			QValues[index][currentCluster] += BETA*(nextState.reward + GAMMA*optimalNextValue - QValues[index][currentCluster]);
			
			//Update currentState and clusters
			model.currentState = nextState;
			Model.printState(model.currentState);
			updateCluster(nextState, nextCluster);
			
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
		return index;
	}
	
	//TODO: make this scalable
	public void doNaiveStrategy(){
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
			
			//Update currentState
			model.currentState = nextState;
			Model.printState(model.currentState);
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
		Agent agent = new Agent();
	//	agent.testCentroidUpdate();
	//	agent.doNaiveStrategy();
		agent.doQLearning();
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
