package com.chess.engine.board;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class BoardUtils { // Utility class for static methods and constants
	
	 // initialization of the columns corresponding to index position
	public static final boolean[] FIRST_COLUMN = initColumn(0); 
	public static final boolean[] SECOND_COLUMN = initColumn(1); 
	public static final boolean[] SEVENTH_COLUMN = initColumn(6); 
	public static final boolean[] EIGHTH_COLUMN = initColumn(7);
	
	 // initialization of the Rows corresponding to index position
	public static final boolean[] EIGHTH_RANK  = initRow(0);
	public static final boolean[] SEVENTH_RANK = initRow(8);
	public static final boolean[] SIXTH_RANK = initRow(16);
	public static final boolean[] FIFTH_RANK = initRow(24);
	public static final boolean[] FOURTH_RANK = initRow(32);
	public static final boolean[] THIRD_RANK = initRow(40);
	public static final boolean[] SECOND_RANK = initRow(48);
	public static final boolean[] FIRST_RANK = initRow(56);
	
	public static final String[] ALGEBREIC_NOTATION = initializedAlgebreicNotation();
	public static final Map<String, Integer> POSITION_TO_COORDINATE = initializedPositionToCoordianteMap();
	
	public static final int NUM_TILES = 64;
	public static final int NUM_TILES_PER_ROW =8;
	
	private BoardUtils(){
		throw new RuntimeException("You cannot instantiate me!");
	}
	private static Map<String, Integer> initializedPositionToCoordianteMap() {
		final Map<String, Integer> positionToCoordinate = new HashMap<>();
		for(int i = 0; i < NUM_TILES; i++){
			positionToCoordinate.put(ALGEBREIC_NOTATION[i], i);
		}
		return ImmutableMap.copyOf(positionToCoordinate);
	}
	private static String[] initializedAlgebreicNotation() {
		return new String[]{
				 	"A8", "B8", "C8", "D8", "E8", "F8", "G8", "H8",
	                "A7", "B7", "C7", "D7", "E7", "F7", "G7", "H7",
	                "A6", "B6", "C6", "D6", "E6", "F6", "G6", "H6",
	                "A5", "B5", "C5", "D5", "E5", "F5", "G5", "H5",
	                "A4", "B4", "C4", "D4", "E4", "F4", "G4", "H4",
	                "A3", "B3", "C3", "D3", "E3", "F3", "G3", "H3",
	                "A2", "B2", "C2", "D2", "E2", "F2", "G2", "H2",
	                "A1", "B1", "C1", "D1", "E1", "F1", "G1", "H1"
		};
	}
	private static boolean[] initColumn(int colunmNumber) { 
		final boolean [] column = new boolean[NUM_TILES];
		do{
			column[colunmNumber] = true;
			colunmNumber += NUM_TILES_PER_ROW;
		}while(colunmNumber < NUM_TILES);
		return column;
	}
	private static boolean[] initRow(int rowNumber){
		final boolean[] row = new boolean[NUM_TILES];
		do{
			row[rowNumber] = true;
			rowNumber++;
		}while(rowNumber % NUM_TILES_PER_ROW != 0);
		return row;
	}
	public static boolean isValidTileCoordinate(final int coordinate) {
		// Check if Tile number is in-bounds or Out-bounds of the Chess Board
		return coordinate >=0 && coordinate < NUM_TILES;
	}
	public static int getPositionAtCoordinate(final String position){
		return POSITION_TO_COORDINATE.get(position);
	}
	public static String getPositionAtCoordinate(final int coordinate){
		return ALGEBREIC_NOTATION [coordinate];
	}

}
