package gui;

import game.GameState;
import game.Position;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class BoardGrid {
	JFrame frame = new JFrame(); 
    JButton[][] grid; 
    boolean[][] buttonPressed;
    int size;
    
    public BoardGrid(int size){
    	this.size = size;
    	buttonPressed = new boolean[size][size];
        frame.setLayout(new GridLayout(size, size)); 
        grid = new JButton[size][size]; 
        for(int i=0; i < size; i++) {
        	for(int j = 0; j < size; j++) {
        		grid[i][j] = new JButton("" + i + "" + j); 
        		grid[i][j].addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						String id = ((JButton) e.getSource()).getText();
						char[] idChar = id.toCharArray(); 
						buttonPressed[Character.getNumericValue(idChar[0])][Character.getNumericValue(idChar[1])] = true;
					}
        			
        		} );
                frame.add(grid[i][j]); 
                buttonPressed[i][j] = false;
            }
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); 
        frame.setVisible(true); 
    }
    
    public void visualizeState(GameState state) {
    	int[][] curState = state.getState();
    	for(int i = 0; i < size; i++) {
    		for(int j = 0; j < size; j++) {
    			if(curState[i][j] == 0) grid[i][j].setBackground(Color.white);
    			else if(curState[i][j] == 1) grid[i][j].setBackground(Color.red);
    			else grid[i][j].setBackground(Color.black);
    		}
    	}
    	SwingUtilities.updateComponentTreeUI(frame);
    }
    
    public void clearAllButtons() {
    	for(int i = 0; i < size; i++) {
    		for(int j = 0; j < size; j++) {
    			buttonPressed[i][j] = false;
    		}
    	}
    }
    
	public Position checkButtons() {
    	for(int i = 0; i < size; i++) {
    		for(int j = 0; j < size; j++) {
    			if(buttonPressed[i][j]) {
    				buttonPressed[i][j] = false;
    				return new Position(i, j);
    			}
    		}
    	}
    	return null;
    }
}
