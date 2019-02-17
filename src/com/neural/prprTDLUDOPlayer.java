package com.neural;

import com.ludo.LUDOSimulator.LUDOBoard;
import com.ludo.LUDOSimulator.LUDOPlayer;

public class prprTDLUDOPlayer implements LUDOPlayer {

	ActivationNetwork net1, net2, net3, net4;
	BackPropagationTrainer teacher1, teacher2, teacher3, teacher4;
	LUDOBoard board;
	double Gamma = 0.6d;
	double[][] brickInputs;
	double[] previousInputs;
	double previousQValue;
	int[] previousBrickPositions;
	int previousMoveIndex;
	public int iterationCount = 100;
	int turn;
	public double eGreedy = 0.75d;

	// double[] reward;

	public prprTDLUDOPlayer(LUDOBoard _board) {
		board = _board;
		int[] neurons = { 10, 1 };
		net1 = new ActivationNetwork(7, neurons);
		net2 = new ActivationNetwork(7, neurons);
		net3 = new ActivationNetwork(7, neurons);
		net4 = new ActivationNetwork(7, neurons);

		// net.Load("OptimizedNet4Prakash.xml");

		teacher1 = new BackPropagationTrainer(net1);
		teacher1.LearningRate = 0.2;
		//teacher1.Momentum = 1;
		
		teacher2 = new BackPropagationTrainer(net2);
		teacher2.LearningRate = 0.2;
		//teacher2.Momentum = 1;
		
		teacher3 = new BackPropagationTrainer(net3);
		teacher3.LearningRate = 0.2;
		//teacher3.Momentum = 1;
		
		teacher4 = new BackPropagationTrainer(net4);
		teacher4.LearningRate = 0.2;
		//teacher4.Momentum = 1;

		previousBrickPositions = board.getMyBricks();
		brickInputs = new double[4][7];
		previousInputs = new double[7];
		// reward = new double[4];
		previousQValue = 0;
		turn = 0;
		previousMoveIndex = -1;
	}

	public void reset(LUDOBoard _board) {
		this.play();
		board = _board;
		previousBrickPositions = board.getMyBricks();
		turn = 0;
		previousQValue = 0;
		previousInputs = new double[7];
		brickInputs = new double[4][7];
		// reward = new double[4];
		previousMoveIndex = -1;
	}

	public void Save(String filename) {
		net1.Save(filename + "1.xml");
		net2.Save(filename + "2.xml");
		net3.Save(filename + "3.xml");
		net4.Save(filename + "4.xml");
	}

	public void Load(String filename) {
		net1.Load(filename + "1.xml");
		net2.Load(filename + "2.xml");
		net3.Load(filename + "3.xml");
		net4.Load(filename + "4.xml");
	}

	public void play() {
		turn++;
		/*
		 * System.out.print("Previous Input (State) = ");
		 * System.out.print(previousInputs[0] + "|");
		 * System.out.print(previousInputs[1] + "|");
		 * System.out.print(previousInputs[2] + "|");
		 * System.out.print(previousInputs[3] + "|");
		 * System.out.print(previousInputs[4] + "|");
		 * System.out.print(previousInputs[5] + "|");
		 * System.out.print(previousInputs[6] + "|");
		 * System.out.println("; Previous Q-value = " + previousQValue); board
		 * .print("Temporal Difference Player is playing (for Prakash Prasad)");
		 */
		board.rollDice();
		double max = 0;
		int bestIndex = -1;
		// Apple e-Greedy logic to explore once in a while
		if (RandomGenerator.generator.nextDouble() <= eGreedy) {
			for (int i = 0; i < 4; i++) {
				double value = analyzeBrickSituation(i);
				if (value >= max && value >= 0) {
					bestIndex = i;
					max = value;
				}
			}
		} else {
			for (int i = 0; i < 4; i++) // find a random moveable brick
			{
				if (board.moveable(i)) {
					double temp = RandomGenerator.generator.nextDouble();
					if (temp > max) {
						max = temp;
						bestIndex = i;
					}
				}
			}
			if (bestIndex != -1)
				max = analyzeBrickSituation(bestIndex);
		}
		if (previousMoveIndex != -1)
			UpdateQValue(max);
		if (bestIndex != -1) {
			board.moveBrick(bestIndex);
			/*
			 * System.out.println("Moving brick # " + bestIndex + " on turn # "
			 * + turn);
			 */
			previousMoveIndex = bestIndex;
			previousInputs = brickInputs[bestIndex];
			previousQValue = max;
			previousBrickPositions = board.getMyBricks();
		}
	}

	private void UpdateNetwork(double[] inputs, double outputs, int brick) {
		// TODO Auto-generated method stub

		double[][] desiredOutput = new double[1][1];
		desiredOutput[0][0] = outputs;
		double[][] pattern = new double[1][];
		pattern[0] = inputs;
		int iterations = 0, iterationLimit = iterationCount;
		double error = 50, errorLimit = 0.01;

		while (iterations < iterationLimit && error > errorLimit) {
			switch (brick) {
			case 0:
				error = teacher1.RunEpoch(pattern, desiredOutput);
				break;
			case 1:
				error = teacher2.RunEpoch(pattern, desiredOutput);
				break;
			case 2:
				error = teacher3.RunEpoch(pattern, desiredOutput);
				break;
			case 3:
				error = teacher4.RunEpoch(pattern, desiredOutput);
				break;
			}
			iterations++;
		}
		/*
		 * System.out.println("Updating Q-Table NN for turn = " + (turn - 1) +
		 * "; to value = " + outputs + ". It Took " + iterations +
		 * " iterations to get upto error of " + error);
		 */
	}

	double maxVal = Double.MIN_VALUE, minVal = Double.MAX_VALUE;

	private void UpdateQValue(double maxValue) {

		/*
		 * if (maxValue <= 0) return;
		 */

		double value = getImmediateReward() + (Gamma * maxValue);

		
		//if (value > 1) value = 1;
		

		if (value >= maxVal)
			maxVal = value;
		if (value <= minVal)
			minVal = value;
		/*
		 * System.out.println(getImmediateReward() + " + (" + Gamma + " * " +
		 * maxValues + ") - " + previousQValue + " = " + value);
		 */

		if (previousMoveIndex != -1) {
			UpdateNetwork(previousInputs, value, previousMoveIndex);
		}
	}

	public void Debug() {
		System.out.println("Maximum adjustment to desired value = " + maxVal);
		System.out.println("Minimum adjustment to desired value = " + minVal);
		maxVal = Double.MIN_VALUE;
		minVal = Double.MAX_VALUE;
		System.out.println("Maximum immediate reward value = " + maxReward);
		System.out.println("Minimum immediate reward value = " + minReward);
		maxReward = Double.MIN_VALUE;
		minReward = Double.MAX_VALUE;
	}

	double maxReward = Double.MIN_VALUE, minReward = Double.MAX_VALUE;

	private double getImmediateReward() {
		// TODO This method will check if rewards and penalties need to be
		// provided for specific events
		// for eg - did I kill an opponent brick? Did I get killed

		double reward = (previousInputs[0] * 0.3d) + (previousInputs[1] * 0.2d)
				- (previousInputs[2] * 0d) + (previousInputs[3] * 0.2d)
				+ (previousInputs[4] * 0.1d) + (previousInputs[5] * 0.1d)
				+ (previousInputs[6] * 0);

		// reward /= 10d;

		if (reward >= maxReward)
			maxReward = reward;
		else if (reward <= minReward)
			minReward = reward;

		return reward;
	}

	public double analyzeBrickSituation(int i) {
		if (board.moveable(i)) {
			int[][] current_board = board.getBoardState();
			int[][] new_board = board.getNewBoardState(i, board.getMyColor(),
					board.getDice());
			double desirablity = EvaluateDesirability(current_board, new_board,
					i);
			return desirablity;
		} else {
			return 0;
		}
	}

	private boolean moveOut(int[][] current_board, int[][] new_board) {
		for (int i = 0; i < 4; i++) {
			if (board.inStartArea(current_board[board.getMyColor()][i], board
					.getMyColor())
					&& !board.inStartArea(new_board[board.getMyColor()][i],
							board.getMyColor())) {
				return true;
			}
		}
		return false;
	}

	private boolean hitOpponentHome(int[][] current_board, int[][] new_board) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (board.getMyColor() != i) {
					if (board.atField(current_board[i][j])
							&& !board.atField(new_board[i][j])) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean hitMySelfHome(int[][] current_board, int[][] new_board) {
		for (int i = 0; i < 4; i++) {
			if (!board.inStartArea(current_board[board.getMyColor()][i], board
					.getMyColor())
					&& board.inStartArea(new_board[board.getMyColor()][i],
							board.getMyColor())) {
				return true;
			}
		}
		return false;
	}

	private double EvaluateDesirability(int[][] current_board,
			int[][] new_board, int brick_index) {

		double[] inputs = new double[7];

		inputs[0] = (moveOut(current_board, new_board)) ? 1 : 0;
		inputs[1] = (board.atHome(new_board[board.getMyColor()][brick_index],
				board.getMyColor())) ? 1 : 0;
		inputs[2] = (hitOpponentHome(current_board, new_board)) ? 1 : 0;
		inputs[3] = (board.isStar(new_board[board.getMyColor()][brick_index])) ? 1
				: 0;
		inputs[4] = (board.isGlobe(new_board[board.getMyColor()][brick_index])) ? 1
				: 0;
		inputs[5] = (board.almostHome(
				new_board[board.getMyColor()][brick_index], board.getMyColor())) ? 1
				: 0;
		inputs[6] = (hitMySelfHome(current_board, new_board)) ? 1 : 0;

		brickInputs[brick_index] = inputs;

		double output = 0;

		switch (brick_index) {
		case 0:
			output = net1.Compute(inputs)[0];
			break;
		case 1:
			output = net2.Compute(inputs)[0];
			break;
		case 2:
			output = net3.Compute(inputs)[0];
			break;
		case 3:
			output = net4.Compute(inputs)[0];
			break;
		}

		/*
		 * for (int j = 0; j < brickInputs[brick_index].length; j++)
		 * System.out.print((int)brickInputs[brick_index][j]);
		 * System.out.println(" - " + output);
		 */

		return output;
	}
}
