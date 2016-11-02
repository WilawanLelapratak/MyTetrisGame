package com.oop.mytetrisgame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.oop.mytetrisgame.Shape.Tetrominos;

public class Board extends JPanel implements ActionListener {

	private static final int BOARD_WIDTH = 10;
	private static final int BOARD_HEIGHT = 22;
	private static final Color[] COLORS = { new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102), new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102,204, 204), new Color(218, 170, 0) };
	private Timer timer;
	private boolean isFallingFinished = false;
	private boolean isStarted = false;
	private boolean isPaused = false;
	private int numLinesRemoved = 0;
	private int curX = 0;
	private int curY = 0;
	private JLabel statusBar;
	private Shape curPiece;
	private Tetrominos[] board;
	
	public Board(Tetris parent) {
		setFocusable(true);
		curPiece = new Shape();
		timer = new Timer(400, this); //timer for lines down
		statusBar = parent.getStatusBar();
		board = new Tetrominos[BOARD_WIDTH * BOARD_HEIGHT];
		clearBoard();
	}
	
	public int squareWidth() {
		return (int) getSize().getWidth() / BOARD_WIDTH;
	}
	
	public int squareHeight() {
		return (int) getSize().getHeight() / BOARD_HEIGHT;
	}
	
	public Tetrominos shapeAt(int x, int y) {
		return board[y * BOARD_WIDTH + x];
	}
	
	public void clearBoard() {
		for(int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {
			board[i] = Tetrominos.NoShape;
		}
	}
	
	private void pieceDropped() {
		for(int i = 0; i < 4; i++) {
			int x = curX + curPiece.x(i);
			int y = curY  - curPiece.y(i);
			board[y * BOARD_WIDTH + x] = curPiece.getShape();
		}
		
		if(!isFallingFinished) {
			newPiece();
		}
	}
	
	public void newPiece() {
		curPiece.setRandomShape();
		curX = BOARD_WIDTH / 2 + 1;
		curY = BOARD_HEIGHT - 1 + curPiece.minY();
	}
	
	private void oneLineDown() {
		pieceDropped();
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		if(isFallingFinished) {
			isFallingFinished = false;
			newPiece();
		}else {
			oneLineDown();
		}
		
	}
	
	//Draw tetrominos
	
	private void drawSquare(Graphics g, int x, int y, Tetrominos shape) {
		Color color = COLORS[shape.ordinal()];
		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
		g.setColor(color.brighter());
		g.drawLine(x,  y + squareHeight() - 1, x, y);
		g.drawLine(x, y, x + squareWidth() - 1, y);
		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() -1);
		g.drawLine(x + squareWidth() -1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
	}
	
	//paint the board
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Dimension size = getSize();
		
		int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();
		
		for(int i = 0; i < BOARD_HEIGHT; i++) {
			for(int j = 0; j < BOARD_WIDTH; j++) {
				Tetrominos shape = shapeAt(j, BOARD_HEIGHT - i - 1);
				
				if(shape != Tetrominos.NoShape) {
					drawSquare(g, j * squareWidth(), boardTop + 1 * squareHeight(), shape);
				}
			}
		}
		
		if(curPiece.getShape() != Tetrominos.NoShape) {
			for(int i = 0; i < 4; ++i) {
				int x = curX + curPiece.x(i);
				int y = curY  - curPiece.y(i);
				drawSquare(g, x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), curPiece.getShape());
			}
		}
	}
}
