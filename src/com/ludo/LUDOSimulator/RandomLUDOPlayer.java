package com.ludo.LUDOSimulator;
import java.util.Random;
/**
 * Example of automatic LUDO player
 * @author David Johan Christensen
 * 
 * @version 0.9
 *
 */
public class RandomLUDOPlayer implements LUDOPlayer{
	LUDOBoard board;
	Random rand;
	public RandomLUDOPlayer(LUDOBoard board)
	{
		this.board = board;
		rand = new Random();
	}
	public void play() {
		board.print("Random player playing");
		board.rollDice();
		int nr=-1;
		double best = 0;
		for(int i=0;i<4;i++) // find a random moveable brick
		{
			if(board.moveable(i)) {
				double temp = rand.nextDouble(); 
				 if(temp>best) {
					 best = temp;
					 nr = i;
				 }
			}
		}
		if(nr!=-1) board.moveBrick(nr);
		//else nothing to do - no moveable bricks
	}
	public synchronized void delay() {
		try {
			wait(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
