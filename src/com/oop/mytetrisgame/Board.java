package com.oop.mytetrisgame;

import Audio.AudioPlayer; // try to add background music

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.oop.mytetrisgame.Shape.Tetrominos;

public class Board extends JPanel implements ActionListener {

	private static final int BOARD_WIDTH = 10;
	private static final int BOARD_HEIGHT = 20;
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
	public AudioPlayer bgMusic; // try to add background music
	public HashMap<String, AudioPlayer> soundEffect; // try to add sound effect
	
	public Board(Tetris parent) {
		setFocusable(true);
		curPiece = new Shape();
		timer = new Timer(400, this); //timer for lines down
		statusBar = parent.getStatusBar();
		board = new Tetrominos[BOARD_WIDTH * BOARD_HEIGHT];
		clearBoard();
		addKeyListener(new MyTetrisGameAdapter());
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
		for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {
			board[i] = Tetrominos.NoShape;
		}
	}
	
	private void pieceDropped() {
		soundEffect = new HashMap<String, AudioPlayer>();
		soundEffect.put("dropped", new AudioPlayer("/SFX/Dropped.mp3"));
		for (int i = 0; i < 4; i++) {
			int x = curX + curPiece.x(i);
			int y = curY  - curPiece.y(i);
			board[y * BOARD_WIDTH + x] = curPiece.getShape();
		}
		
		removeFullLines();
		
		if (!isFallingFinished) {
			soundEffect.get("dropped").play();
			newPiece();
		}
	}
	
	public void newPiece() {
		curPiece.setRandomShape();
		curX = BOARD_WIDTH / 2 + 1;
		curY = BOARD_HEIGHT - 1 + curPiece.minY();
		

		if (!tryMove(curPiece, curX, curY - 1)) {
			curPiece.setShape(Tetrominos.NoShape);
			timer.stop();
			isStarted = false;
			statusBar.setText("Game Over! Your Score is " + String.valueOf(numLinesRemoved));
			stopAndPlayGameOverSound();
		}
	}
	
	public void stopAndPlayGameOverSound() {
		bgMusic.stop();
		bgMusic = new AudioPlayer("/Music/Game Over.mp3");
		bgMusic.play();
	}
	
	private void oneLineDown() {
		if (!tryMove(curPiece, curX, curY - 1)) {
			pieceDropped();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (isFallingFinished) {
			isFallingFinished = false;
			newPiece();
		} else {
			oneLineDown();
		}
		
	}
	
	//Draw tetrominos
	
	private void drawSquare(Graphics g, int x, int y, Tetrominos shape) {
		Color color = shape.color;
		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
		g.setColor(color.brighter());
		g.drawLine(x,  y + squareHeight() - 1, x, y);
		g.drawLine(x, y, x + squareWidth() - 1, y);
		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
		g.drawLine(x + squareWidth() -1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
	}
	
	//paint the board
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Dimension size = getSize();
		
		int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();
		
		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; ++j) {
				Tetrominos shape = shapeAt(j, BOARD_HEIGHT - i - 1);
				
				if (shape != Tetrominos.NoShape) {
					drawSquare(g, j * squareWidth(), boardTop + i * squareHeight(), shape); // Bottom line disappeared because I type 1 instead of i.
				}
			}
		}
		
		if (curPiece.getShape() != Tetrominos.NoShape) {
			for (int i = 0; i < 4; ++i) {
				int x = curX + curPiece.x(i);
				int y = curY  - curPiece.y(i);
				drawSquare(g, x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), curPiece.getShape());
			}
		}
	}
	
	public void start() {
		if (isPaused) {
			return;
		}
		
		isStarted = true;
		isFallingFinished = false;
		numLinesRemoved = 0;
		clearBoard();
		newPiece();
		timer.start(); //start java.swing.timer that will call actionPerformed each 400ms
	}
	
	public void pause() { //use to pause game
		if (!isStarted) {
			return;
		}
		
		isPaused = !isPaused;
		
		if (isPaused) {
			timer.stop();
			bgMusic.stop(); // try to add background music
			statusBar.setText("Paused");
		} else {
			timer.start();
			bgMusic.loop(); // try to add background music
			statusBar.setText(String.valueOf(numLinesRemoved));
		}
		
		repaint();
	}
	
	private boolean tryMove(Shape newPiece, int newX, int newY) {
		for (int i = 0; i < 4; ++i) {
			int x = newX + newPiece.x(i);
			int y = newY - newPiece.y(i);
			
			if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
				return false;
			}
			
			if (shapeAt(x, y) != Tetrominos.NoShape) {
				return false;
			}
		}
		
		curPiece = newPiece;
		curX = newX;
		curY = newY;
		repaint();
		
		return true;
	}
	
	private void removeFullLines() {
		int numFullLines = 0;
		
		for (int i = BOARD_HEIGHT - 1; i >= 0; --i) {
			boolean lineIsFull = true;
			for (int j = 0; j < BOARD_WIDTH; ++j) {
				if (shapeAt(j, i) == Tetrominos.NoShape) {
					lineIsFull = false;
					break;
				}
			}

			if (lineIsFull) {
				++numFullLines;
				
				for (int k = i; k < BOARD_HEIGHT - 1; ++k) {
					for (int j = 0; j < BOARD_WIDTH; ++j) {
						board[k * BOARD_WIDTH + j] = shapeAt(j, k + 1);
					}
				}
			}
			
			if (numFullLines > 0) {
				numLinesRemoved += numFullLines;
				statusBar.setText(String.valueOf(numLinesRemoved));
				isFallingFinished = true;
				curPiece.setShape(Tetrominos.NoShape);
				repaint();
			}
		}
	}
	
	//add drop down method
	private void dropDown() {
		int newY = curY;
		
		while (newY > 0) {
			if (!tryMove(curPiece, curX, newY -1)) {
				break;
			}
			
			--newY;
		}
		
		pieceDropped();
	}
	
	class MyTetrisGameAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent ke) {
			if (!isStarted || curPiece.getShape() == Tetrominos.NoShape) {
				return;
			}
			
			int keyCode = ke.getKeyCode();
			
			if (keyCode == 'P' || keyCode == 'p') {
				pause();
			}
			
			if (isPaused) {
				return;
			}
			
			switch (keyCode) {
			case KeyEvent.VK_LEFT :
				tryMove(curPiece, curX - 1, curY);
				break;
			case KeyEvent.VK_RIGHT :
				tryMove(curPiece, curX + 1, curY);
				break;
			case KeyEvent.VK_DOWN :
				oneLineDown();
				break;
			case KeyEvent.VK_UP :
				tryMove(curPiece.rotateShape(), curX, curY);
				break;
			case KeyEvent.VK_SPACE :
				dropDown();
				break;
			}
		}
	}
}
