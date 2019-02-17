package com.ludo.LUDOSimulator;

import com.genetic.Chromosome;
import com.neural.SemiSmartLUDOTrainer;
import com.neural.prprNeuralLUDOPlayer;
import com.neural.prprTDLUDOPlayer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main class the LUDO simulator - "controllers" the game. This is where you
 * Decide how many games to play and if the graphical interface should be
 * visible.
 * 
 * @author David Johan Christensen
 * 
 * @version 0.9
 */
public class LUDO extends Frame implements ActionListener {
	private static final long serialVersionUID = 1L;

	static LUDOBoard board;

	public LUDO() {
		super("LUDO Simulator");
		setBackground(Color.white);
		board = new LUDOBoard();
		add(board, BorderLayout.CENTER);

		Menu optionsMenu = new Menu("Options", true);
		optionsMenu.add("Reset Game");
		optionsMenu.addActionListener(this);

		MenuBar mbar = new MenuBar();
		mbar.add(optionsMenu);

		setMenuBar(mbar); // Add the menu bar to the frame.
		setBounds(30, 50, 1000, 800); // Set size and position of window.

		setResizable(false);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				LUDO.this.dispose();
				System.exit(0);

			}
		});
		setVisible(visual);
		// train();
		// play();
	}

	public int playGenetic(Chromosome c, int gameCount) {
		int[] result = new int[4];
		prprNeuralLUDOPlayer p = new prprNeuralLUDOPlayer(board, c);
		board.setPlayer(p, LUDOBoard.YELLOW);
		board.setPlayer(new SemiSmartLUDOPlayer(board), LUDOBoard.RED);
		board.setPlayer(new SemiSmartLUDOPlayer(board), LUDOBoard.BLUE);
		board.setPlayer(new SemiSmartLUDOPlayer(board), LUDOBoard.GREEN);

		try {
			for (int i = 0; i < gameCount; i++) {
				board.play();
				board.kill();

				result[0] += board.getPoints()[0];
				result[1] += board.getPoints()[1];
				result[2] += board.getPoints()[2];
				result[3] += board.getPoints()[3];

				board.reset();
				p.reset(board);
				board.setPlayer(p, LUDOBoard.YELLOW);
				board.setPlayer(new SemiSmartLUDOPlayer(board), LUDOBoard.RED);
				board.setPlayer(new SemiSmartLUDOPlayer(board), LUDOBoard.BLUE);
				board
						.setPlayer(new SemiSmartLUDOPlayer(board),
								LUDOBoard.GREEN);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result[0];
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand() == "Reset Game") {
			board.kill();
		}
	}

	public static boolean visual = false;

	private void train() {
		System.out.println("Training Ludo");
		SemiSmartLUDOTrainer t = new SemiSmartLUDOTrainer(board,
				"RecordedData.xml");
		board.setPlayer(t, LUDOBoard.YELLOW);
		board.setPlayer(new SemiSmartLUDOPlayer(board), LUDOBoard.RED);
		board.setPlayer(new SemiSmartLUDOPlayer(board), LUDOBoard.BLUE);
		board.setPlayer(new SemiSmartLUDOPlayer(board), LUDOBoard.GREEN);

		// Record Data here for training
		try {
			for (int i = 0; i < 1000; i++) {
				board.play();
				board.kill();

				board.reset();
				t.reset(board);
				board.setPlayer(t, LUDOBoard.YELLOW);
				board.setPlayer(new SemiSmartLUDOPlayer(board), LUDOBoard.RED);
				board.setPlayer(new SemiSmartLUDOPlayer(board), LUDOBoard.BLUE);
				board
						.setPlayer(new SemiSmartLUDOPlayer(board),
								LUDOBoard.GREEN);
				if ((i % 50) == 0)
					System.out.print(".");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		t.endRecording();
		System.out.println("Training is finished!");
	}

	/**
	 * Plays a number of games, which are useful when all players are automatic.
	 * Remember to set the "visual" field to speed up the simulation time.
	 * 
	 */
	public void play() {
		System.out.println("Playing Ludo");
		long time = System.currentTimeMillis();
		int[] result = new int[4];
		int iter = 50000;
		/*
		 * prprNeuralLUDOPlayer p = new prprNeuralLUDOPlayer(board,
		 * "RecordedData.xml");
		 */
		prprTDLUDOPlayer p = new prprTDLUDOPlayer(board);
		p.eGreedy = 0.75;
		board.setPlayer(p, LUDOBoard.YELLOW);
		// board.setPlayer(new RandomLUDOPlayer(board), LUDOBoard.YELLOW);
		board.setPlayer(new RandomLUDOPlayer(board), LUDOBoard.RED);
		board.setPlayer(new RandomLUDOPlayer(board), LUDOBoard.BLUE);
		board.setPlayer(new RandomLUDOPlayer(board), LUDOBoard.GREEN);

		try {
			// for (int j = 0; j < iter / 100; j++) {
			// result = new int[4];
			for (int i = 0; i < iter; i++) {
				board.play();
				board.kill();

				result[0] += board.getPoints()[0];
				result[1] += board.getPoints()[1];
				result[2] += board.getPoints()[2];
				result[3] += board.getPoints()[3];

				p.play();
				board.reset();
				p.reset(board);

				if (i < iter * 0.5)
					p.eGreedy = 0.75;
				else if (i < iter * 0.8)
					p.eGreedy = 0.9;
				else if (i < iter * 0.9)
					p.eGreedy = 0.98;

				board.setPlayer(p, LUDOBoard.YELLOW);
				board.setPlayer(new RandomLUDOPlayer(board), LUDOBoard.RED);
				board.setPlayer(new RandomLUDOPlayer(board), LUDOBoard.BLUE);
				board
						.setPlayer(new RandomLUDOPlayer(board),
								LUDOBoard.GREEN);
				if ((i % 100) == 0 && i != 0) {
					// System.out.print(".");
					// System.out.println();

					System.out.println(result[0]);

					// System.out.println("RED    Player: " + result[1]);
					// System.out.println("BLUE   Player: " + result[2]);
					// System.out.println("GREEN  Player: " + result[3]);
					result = new int[4];
					// p.Debug();
				}
			}
			/*
			 * System.out.println("NN Player (YELLOW) RESULT @ " + j + " = " +
			 * result[0]);
			 */
			// }
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// p.Save("TDNetworkConfig");

		System.out.println(result[0]);

		p.Debug();
		// System.out.println();
		System.out.println("Simulation took "
				+ (System.currentTimeMillis() - time) + " milliseconds");
		/*
		 * System.out.println("RESULT:"); System.out.println("YELLOW Player: " +
		 * result[0]); System.out.println("RED    Player: " + result[1]);
		 * System.out.println("BLUE   Player: " + result[2]);
		 * System.out.println("GREEN  Player: " + result[3]);
		 */
	}

	public static void main(String[] args) {
		LUDO l = new LUDO();
		l.play();
	}
}