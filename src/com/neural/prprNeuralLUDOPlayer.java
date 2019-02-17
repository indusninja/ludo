/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neural;

import com.genetic.Chromosome;
import com.ludo.LUDOSimulator.LUDOBoard;
import com.ludo.LUDOSimulator.LUDOPlayer;

public class prprNeuralLUDOPlayer implements LUDOPlayer {

	ActivationNetwork net;
	BackPropagationTrainer teacher;
	LUDOBoard board;
	int[] neurons = { 4, 2, 1 };

	public prprNeuralLUDOPlayer(LUDOBoard _board) {
		board = _board;
		
		net = new ActivationNetwork(7, neurons);

		net.Load("OptimizedNet4Prakash.xml");
	}
	
	// Run backpropagation training before playing
	public prprNeuralLUDOPlayer(LUDOBoard _board, String filename) {
		board = _board;
		net = new ActivationNetwork(7, neurons);

		teacher = new BackPropagationTrainer(net);
		System.out.println("Back-propagation training has begun...");
		teacher.Load(filename);
		System.out.println("Neural Network is Ready to play!");
		//net.Load("network.xml");
	}
	
	// Play from the GA evolved chromosome
	public prprNeuralLUDOPlayer(LUDOBoard _board, Chromosome c) {
		board = _board;
		net = new ActivationNetwork(c.getNetworkInputCount(), c.getNetworkConfig());

		net.Load(c);
	}

	public void reset(LUDOBoard _board) {
		board = _board;
	}
	
	public void play() {
		board.print("Back-propagation Player is playing (for Prakash Prasad)");
		board.rollDice();
		double max = -1;
		int bestIndex = -1;
		for (int i = 0; i < 4; i++) {
			double value = analyzeBrickSituation(i);
			if (value > max && value > 0) {
				bestIndex = i;
				max = value;
			}
		}
		if (bestIndex != -1) {
			board.moveBrick(bestIndex);
		}
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

		return net.Compute(inputs)[0];
	}
}
