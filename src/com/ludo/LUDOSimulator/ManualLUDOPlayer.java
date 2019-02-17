package com.ludo.LUDOSimulator;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
/**
 * Example of automatic LUDO player
 * @author David Johan Christensen
 * 
 * @version 0.9
 *
 */
public class ManualLUDOPlayer implements LUDOPlayer, MouseListener{

	LUDOBoard board;
	
	public ManualLUDOPlayer(LUDOBoard board) {
		this.board = board;
	}
	boolean playing = false;
	public void play() {
		//do nothing wait for clicks
		board.addMouseListener(this);
		playing = true;
		while(playing&&!board.killed) try{Thread.currentThread().sleep(10);}catch(Exception e){}
		board.removeMouseListener(this);
	}
	public void mouseClicked(MouseEvent mEvent) {
		if(!playing) return;
		if(mEvent.getX()>760&&mEvent.getX()<(760+100)&&mEvent.getY()>330&&mEvent.getY()<(330+100)) {
			board.rollDice();
			if(board.nothingToDo()) playing = false; 
		}
		else {
			int[] myBricks = board.getMyBricks();
			double min = 10000;
			int nr = -1;
			for(int i=0;i<4;i++) {
				int[] xy = board.index2Pixel(myBricks[i],board.getMyColor());
				double temp = (xy[0]-mEvent.getX())*(xy[0]-mEvent.getX())+(xy[1]-mEvent.getY())*(xy[1]-mEvent.getY());
				if(temp<min) {
					min= temp;
					nr = i;
				}
			}
			if(nr!=-1&&min<250) {
				if(board.moveBrick(nr))	playing = false;
				else board.print("Unable to move selected brick..."); 
			}
			else {
				board.print("You must click one of your bricks...");
			}
		}
	}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
}
