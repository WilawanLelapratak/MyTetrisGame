package com.oop.mytetrisgame;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Tetris extends JFrame {

	private JLabel statusBar;
	
	public Tetris() {
		statusBar = new JLabel("0"); //to display lines number
		add(statusBar, BorderLayout.SOUTH);
		Board board = new Board(this);
		add(board);
		board.start(); //start line down
		setSize(200, 400);
		setTitle("Tetris Game");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public JLabel getStatusBar() {
		return statusBar;
	}
	
	public static void main(String[] args) {
		Tetris myTetris = new Tetris();
		myTetris.setLocationRelativeTo(null); //center
		myTetris.setVisible(true);
	}
}
