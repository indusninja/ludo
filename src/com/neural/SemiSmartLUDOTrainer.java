package com.neural;

import com.ludo.LUDOSimulator.LUDOBoard;
import com.ludo.LUDOSimulator.LUDOPlayer;

import java.io.*;
import java.util.Random;

/**
 * Example of automatic LUDO player
 * 
 * @author David Johan Christensen
 * 
 * @version 0.9
 * 
 */
public class SemiSmartLUDOTrainer implements LUDOPlayer {

	LUDOBoard board;
	Random rand;
	String filename = "trainerRecords.xml";
	OutputStream fout;
	OutputStream bout;
	OutputStreamWriter out;
	int records, recordlimit = 50000;

	public SemiSmartLUDOTrainer(LUDOBoard board, String filename) {
		reset(board);
		this.filename = filename;
		startRecording();
		records = 1;
	}

	public void reset(LUDOBoard board) {
		this.board = board;
		rand = new Random();
	}

	private void startRecording() {
		try {
			fout = new FileOutputStream(filename);
			bout = new BufferedOutputStream(fout);
			out = new OutputStreamWriter(bout, "8859_1");
			out.write("<?xml version=\"1.0\" ");
			out.write("encoding=\"ISO-8859-1\"?>\r\n");
			out
					.write("<BackPropagation LearningRate=\"0.2\" Momentum=\"0.2\" Records=\""
							+ recordlimit + "\">\r\n");

		} catch (UnsupportedEncodingException e) {
			System.out
					.println("This VM does not support the Latin-1 character set.");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void endRecording() {
		try {
			out.write("</BackPropagation>\r\n");
			out.flush(); // Don't forget to flush!
			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void play() {
		board.print("Semi Smart trainer playing");

		board.rollDice();
		int[] myBricksValue = new int[4];

		float max = -1;
		int bestIndex = -1;
		for (int i = 0; i < 4; i++) {
			float value = analyzeBrickSituation(i);
			if (value > max && value > 0) {
				bestIndex = i;
				max = value;
			}
		}
		if (bestIndex != -1) {
			board.moveBrick(bestIndex);
		}
	}

	public float analyzeBrickSituation(int i) {
		if (board.moveable(i)) {
			int[][] current_board = board.getBoardState();
			int[][] new_board = board.getNewBoardState(i, board.getMyColor(),
					board.getDice());
			float ret;
			// Save the next board parameter satisfaction
			if (records <= recordlimit)
				recordInputPattern(current_board, new_board, i);
			// save the normalized output without random between 0 and 5
			if (hitOpponentHome(current_board, new_board)) {
				ret = 5f;
				if (records <= recordlimit)
					saveDesiredOutput(ret);
				return ret;
			} else if (hitMySelfHome(current_board, new_board)) {
				ret = 0.1f;
				if (records <= recordlimit)
					saveDesiredOutput(ret);
				return ret;
			} else if (board.isStar(new_board[board.getMyColor()][i])) {
				ret = 4f;
				if (records <= recordlimit)
					saveDesiredOutput(ret);
				return ret;
			} else if (moveOut(current_board, new_board)) {
				ret = 3f;
				if (records <= recordlimit)
					saveDesiredOutput(ret);
				return ret;
			} else if (board.atHome(new_board[board.getMyColor()][i], board
					.getMyColor())) {
				ret = 2f;
				if (records <= recordlimit)
					saveDesiredOutput(ret);
				return ret;
			} else {
				ret = 1f;
				if (records <= recordlimit)
					saveDesiredOutput(ret);
				return ret;
			}
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

	private void recordInputPattern(int[][] _current, int[][] _next, int _token) {
		try {
			int input1 = (moveOut(_current, _next)) ? 1 : 0;
			int input2 = (board.atHome(_next[board.getMyColor()][_token], board
					.getMyColor())) ? 1 : 0;
			int input3 = (hitOpponentHome(_current, _next)) ? 1 : 0;
			int input4 = (board.isStar(_next[board.getMyColor()][_token])) ? 1
					: 0;
			int input5 = (board.isGlobe(_next[board.getMyColor()][_token])) ? 1
					: 0;
			int input6 = (board.almostHome(_next[board.getMyColor()][_token],
					board.getMyColor())) ? 1 : 0;
			int input7 = (hitMySelfHome(_current, _next)) ? 1 : 0;

			out.write("<Pattern id=\"" + records
					+ "\" inputs=\"7\" output=\"1\">\r\n");
			out.write("\t<Input>" + input1 + "</Input>\r\n");
			out.write("\t<Input>" + input2 + "</Input>\r\n");
			out.write("\t<Input>" + input3 + "</Input>\r\n");
			out.write("\t<Input>" + input4 + "</Input>\r\n");
			out.write("\t<Input>" + input5 + "</Input>\r\n");
			out.write("\t<Input>" + input6 + "</Input>\r\n");
			out.write("\t<Input>" + input7 + "</Input>\r\n");

		} catch (Exception e) {// Catch exception if any
			System.out.println(e.getMessage());
		}
	}

	private void saveDesiredOutput(float _sat) {
		try {
			float brick = _sat;
			brick /= 5;
			out.write("\t<Output>" + brick + "</Output>\r\n");
			out.write("</Pattern>\r\n");
			records++;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
