package com.oop.mytetrisgame;

import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Font;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLabel;

import Audio.AudioPlayer;

public class Tetris extends JFrame {

	private JLabel statusBar;
	
	public Tetris() {
		statusBar = new JLabel("Score : 0"); //to display lines number
		statusBar.setFont(new Font("Score : 0", Font.BOLD, 22));
		add(statusBar, BorderLayout.SOUTH);
		Board board = new Board(this);
		add(board);
		board.start(); //start line down
		setSize(400,800);
		setTitle("Tetris Game");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		board.bgMusic = new AudioPlayer("/Music/Techno - Tetris (Remix).mp3");
		board.bgMusic.loop();
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
