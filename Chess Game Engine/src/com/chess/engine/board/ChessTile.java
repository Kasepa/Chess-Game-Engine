

						/*	UNICAF UNIVERSITY - COMPUTER SCIENCE	*
						 * 											*
						 * 											*
						 * 			MICHAEL MUMBI KASEPA			*
						 * 											*
						 * 				             				*
						 * 											*
						 * UU-COM-4005 PROJECT I - PROJECT PROPOSAL	*
						 */

// Chess Tile board to capture all 64 square boxes( though it runs from 0 to 63)

/* A class Should be declared abstract if at least one method is abstract because 
 * it will become  * compulsory for all child classes to implement all the methods*
 * By declaring abstract methods in the parent class we can provide guidelines to the child classes such
 * which method compulsory the child class has to implement*/

//If a Class contains at least one abstract method then the implementation is not complete and hence, it is not recommended to create object. 
//To restrict object instantiation / creation compulsory we should declare class as abstract. 


package com.chess.engine.board;

//import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;

public abstract class ChessTile { 
	
	protected final int tileCoordinate; // Member field to represent tile number
	private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleTiles();
	private ChessTile(final int tileCoordinate){
		this.tileCoordinate = tileCoordinate;
	}
	private static Map<Integer, EmptyTile> createAllPossibleTiles() {
		final Map<Integer, EmptyTile>  emptyTileMap = new HashMap<>();
		for(int i = 0; i<BoardUtils.NUM_TILES;i++){
			emptyTileMap.put(i, new EmptyTile(i));
		}
		//return Collections.unmodifiableMap(emptyTileMap);
		return ImmutableMap.copyOf(emptyTileMap);
	}
	public static ChessTile createTile(final int tileCoordinate, final Piece piece){
			return piece != null ? new OccupiedTile(tileCoordinate, piece): EMPTY_TILES_CACHE.get(tileCoordinate);
	}
	public abstract boolean isTileOcuppied();
	public abstract Piece getPiece();
	public int getTileCoordinate(){
		return this.tileCoordinate;
	}
	/* Child class of ChessTile that implements the an unoccupied chess board*/
	public static final class EmptyTile extends ChessTile{
		EmptyTile(final int coordinate){
			super(coordinate);
		}
	@Override
	public String toString(){
		return "-";
	}
	@Override
	public boolean isTileOcuppied(){
		return false;
	}
	@Override
	public Piece getPiece(){
		return null;
	}
}
	/* Child class of ChessTile that implements the an occupied chess board*/
	public static final class OccupiedTile extends ChessTile{
		private final Piece pieceOnTile;
		private OccupiedTile(int tileCoordinate, final Piece pieceOnTile){
			super(tileCoordinate);
			this.pieceOnTile = pieceOnTile;
		}
	@Override
	public String toString(){
		return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase():
			getPiece().toString();
	}
	@Override
	public boolean isTileOcuppied(){
		return true;
	}
	@Override
	public Piece getPiece(){
		return this.pieceOnTile;
	}
  }
	
}
	

