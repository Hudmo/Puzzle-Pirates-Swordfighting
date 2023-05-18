package Swordfighting;


import java.awt.Color;
import java.awt.Graphics;

import static Swordfighting.Board.BOARD_HEIGHT;
import static Swordfighting.Board.BLOCK_SIZE;


public class Shape {
	// movement, position
	private int normal = 600;
	private int fast = 50;
	private int delayTimeForMovement = normal;
	private long beginTime;
	private int x = 4, y = 0;
	private int deltaX = 0;
	private boolean collision = false;
	
	private int[][] coords;
	private Board board;
	private Color color;
	
	public Shape(int[][] coords, Board board, Color color) {
		this.coords = coords;
		this.board = board;
		this.color = color;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void reset() {
		this.x = 4;
		this.y = 0;
		collision = false;
		this.speedDown();
	}
	 
	public void update() {
		if(collision) { // shape has reached bottom
			for(int row = 0; row < coords.length; row++) {
				for(int col = 0; col < coords[0].length; col++) {
					if(coords[row][col] != 0) {
						board.getBoard()[y + row][x + col] = color;
					}	
				}
			}
			checkLine();
			board.setCurrentShape();
			return;
		}
		
		boolean moveX = true;
		
		if(!(x + deltaX + coords[0].length > 10) && !(x + deltaX < 0)) {
			for(int row = 0; row < coords.length; row++) {
				for(int col = 0; col < coords[row].length; col++) {
					if(coords[row][col] != 0) {
						if(board.getBoard()[y + row][x + deltaX + col] != null) {
							moveX = false;
						}
					}
				}
			}
			
			if(moveX) {
				x += deltaX;
			}
		}
		
		deltaX = 0;
		
		if(System.currentTimeMillis() - beginTime > delayTimeForMovement) {
			// vert movement
			if(!(y + 1 + coords.length > BOARD_HEIGHT)) {
				for(int row = 0; row < coords.length; row++) {
					for(int col = 0; col < coords[row].length; col++) {
						if(coords[row][col] != 0) {
							if(board.getBoard()[y + 1 + row][x + deltaX + col] != null) {
								collision = true;
							}
						}
					}
				}
				
				if(!collision) {
					y++;
				}
				
			} else {
				collision = true;
			}
			
			beginTime = System.currentTimeMillis();
		}
	}

	// deletes line if full, appends line above to line below to remove (so shapes appear to shift down one line when it disappears)
	// needs to change if square elimination is to be implemented
	private void checkLine() {
		int bottomLine = board.getBoard().length - 1;
		
		for(int topLine = board.getBoard().length - 1; topLine > 0; topLine--) {
			int count = 0;
			
			for(int col = 0; col < board.getBoard()[0].length; col++) {
				if(board.getBoard()[topLine][col] != null) {
					count++;
				}
				
				board.getBoard()[bottomLine][col] = board.getBoard()[topLine][col];
			}
			
			if(count < board.getBoard()[0].length) {
				bottomLine--;
			}
		}
	}
	
	public void rotateShape() {
		int[][] rotatedShape = reverseRows(transposeMatrix(coords));
		
		if((x + rotatedShape[0].length > 10) || (y + rotatedShape.length > 20)) {
			return;
		}
		
		for(int row = 0; row < rotatedShape.length; row++) {
			for(int col = 0; col < rotatedShape[row].length; col++) {
				if(rotatedShape[row][col] != 0) {
					if(board.getBoard()[y + row][x + col] != null) {
						return;
					}
				}
			}
		}
		
		coords = rotatedShape;
	}
	
	private int[][] transposeMatrix(int[][] matrix) {
		int[][] temp = new int[matrix[0].length][matrix.length];
		
		for(int row = 0; row < matrix.length; row++) {
			for(int col = 0; col < matrix[0].length; col++) {
				temp[col][row] = matrix[row][col]; // error from ~20:50 in Tetris Game On Java Tutorial - Part 8, https://www.youtube.com/watch?v=q6C5QEb1dT4 ::
			}
		}
		
		return temp;
	}
	
	private int[][] reverseRows(int[][] matrix) {
		int middle = matrix.length / 2;
		
		for(int row = 0; row < middle; row++) {
			int[] temp = matrix[row];
			matrix[row] = matrix[matrix.length - row - 1];
			matrix[matrix.length - row - 1] = temp;
		}
		
		return matrix;
	}
	
	public void render(Graphics g) {
		// color shape on board
		for(int row  = 0; row < coords.length; row++) {
			for(int col = 0; col < coords[0].length; col++) {
				if(coords[row][col] != 0) {
					g.setColor(color);
					g.fillRect(col * BLOCK_SIZE + x * BLOCK_SIZE, row * BLOCK_SIZE + y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
				}
			}
		}
	}
	
	public void speedUp() {
		delayTimeForMovement = fast;
	}
	
	public void speedDown() {
		delayTimeForMovement = normal;
	}
	
	public void moveRight() {
		deltaX = 1;
	}
	
	public void moveLeft() {
		deltaX = -1;
	}
}
