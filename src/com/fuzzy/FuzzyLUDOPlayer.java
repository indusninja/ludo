package com.fuzzy;

import com.ludo.LUDOSimulator.LUDOBoard;
import com.ludo.LUDOSimulator.LUDOPlayer;

public class FuzzyLUDOPlayer implements LUDOPlayer {

	LUDOBoard board;

	private boolean FirstInference;
	private InferenceSystem IS;

	public FuzzyLUDOPlayer(LUDOBoard b) {
		this.board = b;

		// Setup Fuzzy variables and inference system here
	}

	private void InitFuzzyEngine() throws Exception {

		// Linguistic labels (fuzzy sets) that compose the distances
		FuzzySet fsNear = new FuzzySet("Near", new TrapezoidalFunction(15, 50,
				TrapezoidalFunction.EdgeType.Right));
		FuzzySet fsMedium = new FuzzySet("Medium", new TrapezoidalFunction(15,
				50, 60, 100));
		FuzzySet fsFar = new FuzzySet("Far", new TrapezoidalFunction(60, 100,
				TrapezoidalFunction.EdgeType.Left));

		// Right Distance (Input)
		LinguisticVariable lvRight = new LinguisticVariable("RightDistance", 0,
				120);
		lvRight.AddLabel(fsNear);
		lvRight.AddLabel(fsMedium);
		lvRight.AddLabel(fsFar);

		// Left Distance (Input)
		LinguisticVariable lvLeft = new LinguisticVariable("LeftDistance", 0,
				120);
		lvLeft.AddLabel(fsNear);
		lvLeft.AddLabel(fsMedium);
		lvLeft.AddLabel(fsFar);

		// Front Distance (Input)
		LinguisticVariable lvFront = new LinguisticVariable("FrontalDistance",
				0, 120);
		lvFront.AddLabel(fsNear);
		lvFront.AddLabel(fsMedium);
		lvFront.AddLabel(fsFar);

		// Linguistic labels (fuzzy sets) that compose the angle
		FuzzySet fsVN = new FuzzySet("VeryNegative", new TrapezoidalFunction(
				-40, -35, TrapezoidalFunction.EdgeType.Right));
		FuzzySet fsN = new FuzzySet("Negative", new TrapezoidalFunction(-40,
				-35, -25, -20));
		FuzzySet fsLN = new FuzzySet("LittleNegative", new TrapezoidalFunction(
				-25, -20, -10, -5));
		FuzzySet fsZero = new FuzzySet("Zero", new TrapezoidalFunction(-10, 5,
				5, 10));
		FuzzySet fsLP = new FuzzySet("LittlePositive", new TrapezoidalFunction(
				5, 10, 20, 25));
		FuzzySet fsP = new FuzzySet("Positive", new TrapezoidalFunction(20, 25,
				35, 40));
		FuzzySet fsVP = new FuzzySet("VeryPositive", new TrapezoidalFunction(
				35, 40, TrapezoidalFunction.EdgeType.Left));

		// Angle
		LinguisticVariable lvAngle = new LinguisticVariable("Angle", -50, 50);
		lvAngle.AddLabel(fsVN);
		lvAngle.AddLabel(fsN);
		lvAngle.AddLabel(fsLN);
		lvAngle.AddLabel(fsZero);
		lvAngle.AddLabel(fsLP);
		lvAngle.AddLabel(fsP);
		lvAngle.AddLabel(fsVP);

		// The database
		Database fuzzyDB = new Database();
		fuzzyDB.AddVariable(lvFront);
		fuzzyDB.AddVariable(lvLeft);
		fuzzyDB.AddVariable(lvRight);
		fuzzyDB.AddVariable(lvAngle);

		// Creating the inference system
		IS = new InferenceSystem(fuzzyDB, new CentroidDefuzzifier(1000));

		// Going Straight
		IS.NewRule("Rule 1", "IF FrontalDistance IS Far THEN Angle IS Zero");
		// Going Straight (if can go anywhere)
		IS
				.NewRule(
						"Rule 2",
						"IF FrontalDistance IS Far AND RightDistance IS Far AND LeftDistance IS Far THEN Angle IS Zero");
		// Near right wall
		IS
				.NewRule(
						"Rule 3",
						"IF RightDistance IS Near AND LeftDistance IS Not Near THEN Angle IS LittleNegative");
		// Near left wall
		IS
				.NewRule(
						"Rule 4",
						"IF RightDistance IS Not Near AND LeftDistance IS Near THEN Angle IS LittlePositive");
		// Near front wall - room at right
		IS
				.NewRule("Rule 5",
						"IF RightDistance IS Far AND FrontalDistance IS Near THEN Angle IS Positive");
		// Near front wall - room at left
		IS
				.NewRule("Rule 6",
						"IF LeftDistance IS Far AND FrontalDistance IS Near THEN Angle IS Negative");
		// Near front wall - room at both sides - go right
		IS
				.NewRule(
						"Rule 7",
						"IF RightDistance IS Far AND LeftDistance IS Far AND FrontalDistance IS Near THEN Angle IS Positive");
	}

	public void play() {
		board.print("Fuzzy Logic (Prakash) player playing");

		int[] myBricksValue = new int[4];
		board.rollDice();
		float max = -1;
		int bestIndex = -1;
		for (int i = 0; i < 4; i++) {
			float value = analyzeBrickSituation(i);
			if (value > max && value > 0) {
				bestIndex = i;
				max = value;
			}
		}
		if (bestIndex != -1)
			board.moveBrick(bestIndex);
	}

	private float analyzeBrickSituation(int i) {
		
		// Fuzzify, apply rules and defuzzify to get the desirability value
		
		if (board.moveable(i)) {
			int[][] current_board = board.getBoardState();
			int[][] new_board = board.getNewBoardState(i, board.getMyColor(),
					board.getDice());
		}
		return 0;
	}

	// Run one epoch of the Fuzzy Inference System
	private void DoInference() {
		// Setting inputs
		// IS.SetInput( "RightDistance", Convert.ToDouble( txtRight.Text ) );
		// IS.SetInput( "LeftDistance", Convert.ToDouble( txtLeft.Text ) );
		// IS.SetInput( "FrontalDistance", Convert.ToDouble( txtFront.Text ) );

		// Setting outputs
		/*
		 * try { double NewAngle = IS.Evaluate( "Angle" ); txtAngle.Text =
		 * NewAngle.ToString( "##0.#0" ); Angle += NewAngle; } catch ( Exception
		 * ) { }
		 */
	}

	// Maybe try to fix this so that you're only comparing against the best
	// opponent
	private double getHomeHeuristics() {
		int opphome = 0, myhome = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (board.atHome(j, i)) {
					if (i == board.getMyColor())
						myhome++;
					else
						opphome++;
				}
			}
		}
		return myhome - opphome;
	}

	// figure out if what's the weighted value of bricks in the start area
	private double getStartAreaHeuristics() {
		int atstart = 0, athome=0;
		for(int i=0;i<4;i++){
			if(board.inStartArea(i, board.getMyColor()))
				atstart++;
			if(board.atHome(i, board.getMyColor()))
				athome++;
		}
		
		return atstart/(4-athome);
	}
	
	// Returns probability of being hit from the previous 6 spots
	// ranges from 1/6 to 1
	private double getDanger() {
		double count = 0;
		int[] myB = board.getBricks(board.getMyColor());
		int[] oppB = getOpponentBricks(board.getBoardState());
		
		for(int i=0;i<4;i++){
			for(int j=0;j<oppB.length;j++){
				if((myB[i]-oppB[j])<=6)
					count++;
			}
		}
		
		return count/6;
	}

	private double getSafety() {
		int atglobe=0, onbrick=0, almosthome =0;
		for (int i = 0; i < 4; i++) {
			/*if(board.atHome(i, board.getMyColor()))
				almosthome +=3;*/
			if(board.almostHome(i, board.getMyColor()))
				almosthome +=3;
			//else if(board.isStar(board.getMyBricks()[i]))
				
		}
		return 0;
	}

	private int[] getOpponentBricks(int[][] board_state) {
		int[] b = new int[12];
		int index = 0;

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				if (i != board.getMyColor()) {
					b[index] = board_state[i][j];
					index++;
				}

		return b;
	}
}
