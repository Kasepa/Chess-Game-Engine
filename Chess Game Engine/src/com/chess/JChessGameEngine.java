package com.chess;
import com.chess.gui.ChessGameTable;
import com.chess.engine.board.Board;

// Driver Class!
public class JChessGameEngine {

	public static void main(String[] args){
		
		Board board = Board.createStandardBoard();
		System.out.println(board);
		ChessGameTable.get().show();
	}
	
}
