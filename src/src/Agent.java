package src;

import src.Model.Policy;

public class Agent {
	int turn = 0;
	final int CLUSTER_COUNT = 10;
	final int MAX_ITERATION = 2000;
	
	int policyCount;
	State[] centroids = new State[CLUSTER_COUNT]; // the list of centroid for each cluster
	int[] stateCount = new int[CLUSTER_COUNT]; // number of state subscribed to each cluster
	double[][] QValues; // 
	boolean[][][] policies = {{{false, false}, {false, false}},{{false, false}, {false, true}},{{false, false}, {true, false}},{{false, false}, {true, true}}, {{false, true}, {false, false}},{{false, true}, {false, true}},{{false, true}, {true, false}},{{false, true}, {true, true}},{{true, false}, {false, false}},{{true, false}, {true, false}},{{true, false}, {true, true}},{{true, true}, {false, false}},{{true, true}, {false, true}},{{true, true}, {true, false}},{{true, true}, {true, true}}};
	
	int width, height;
	final double EPS = 0.9;
	
	Model model;
	

	public Agent(int policyCount){
		model = new Model();
		model.buildState();
		
		width = model.getWidth();
		height = model.getHeight();
		
		this.policyCount = (int)Math.pow(2, width*height);
		
		QValues = new double[policyCount][CLUSTER_COUNT];
		System.out.println("number of policies = " + policies.length); //TODO: do this

	}

	public void doQLearning(){
		for(int i = 0; i < MAX_ITERATION; i ++){
			boolean[][] nextNSGreen = new boolean[width][height];
			int index = 0;
			int currentCluster = getBestClusterIndex(model.currentState);
			//Choose the desired policy
			if(Math.random() < EPS){//Be greedy and choose the optimal policy
				
				double maxValue = Double.NEGATIVE_INFINITY;
				for(int j = 0; j < QValues.length; j++){
					if(QValues[j][currentCluster] > maxValue){
						maxValue = QValues[j][currentCluster];
						index = j;
					}
				}
			}else{//Explore and choose a random action
				index = (int)Math.random()*policyCount;
			}
			// Extract the selected policy given the selection index
			nextNSGreen = policies[index];
			
			
		}
	}

	private Policy findOptPolicy(State s){
		//TODO: write this
		return null;
	}

	public void policyIteration(){
		//TODO: write this, figure out how to store each policy without creating an array slot for every possible state.
	}

	public void valueIteration(){
		//TODO: write this
	}

	public int getBestClusterIndex(State s){
		//TODO: See if there is need to save states to update the clusters
		double minDistance = Double.POSITIVE_INFINITY;
		double distance = 0;
		int index = 0;
		//TODO: check that this is done right
		for(int k = 0 ; k < centroids.length; k++){
			for(int i = 0; i < width ; i++){
				for(int j = 0; j < height ; j++){
					distance += Math.pow(s.grid[i][j].NScars, 2) - Math.pow(centroids[k].grid[i][j].NScars, 2);
					distance += Math.pow(s.grid[i][j].EWcars, 2) - Math.pow(centroids[k].grid[i][j].EWcars, 2);
				}
			}
			if(distance < minDistance){
				minDistance = distance;
				index = k;
			}
		}
		return index;
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
