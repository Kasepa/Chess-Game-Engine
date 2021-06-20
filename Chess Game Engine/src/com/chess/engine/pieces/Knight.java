package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
//import java.util.Collections;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.ChessTile;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MajorAttackMove;
//import com.chess.engine.pieces.Piece.PieceType;
import com.google.common.collect.ImmutableList;

import static com.chess.engine.board.Move.*;


public class Knight extends Piece{
	//list of candidate destination for Knight based on offsets in the Array with respect to current position
	private final static int[] CANDIDATE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17}; 
	public Knight(final Alliance pieceAlliance,final int piecePosition) {
		super(PieceType.KINGHT, piecePosition, pieceAlliance, true);
		}
	public Knight(final Alliance pieceAlliance,
			      final int piecePosition, 
			      final boolean isFirstMove){
		super(PieceType.KINGHT, piecePosition, pieceAlliance, isFirstMove);
	}
	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {
		//int candidateDestinationCoordinate;
		final List<Move>legalMoves = new ArrayList<>();
		for(final int currentCandidateOffset:CANDIDATE_MOVE_COORDINATES ){
			final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset; //Candidate destination coordinate
			if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
				
				if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
						isSecondColumnExclusion(this.piecePosition, currentCandidateOffset) ||
						isSeventhColumnExclusion(this.piecePosition, currentCandidateOffset) ||
						isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)){
					continue;
					}
				final ChessTile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
				if(!candidateDestinationTile.isTileOcuppied()) {
					legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
				}else {
					final Piece pieceAtDestination = candidateDestinationTile.getPiece();
					final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
					if(this.pieceAlliance != pieceAlliance){
						// Add attacking move if the candidate alliance is not equal to the current alliance
						legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
					}
				}
			}
		}
		//return Collections.unmodifiableMap(emptyTileMap);
		return ImmutableList.copyOf(legalMoves);
	}
	
	@Override
	public String toString(){
		return PieceType.KINGHT.toString();
	}
	
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
		return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -17 || candidateOffset == -10 ||
		candidateOffset == 6 || candidateOffset == 15);
	}
	
	private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset){
		return BoardUtils.SECOND_COLUMN[currentPosition] && (candidateOffset == -10 || candidateOffset == 6);
	}
	
	private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset){
		return BoardUtils.SEVENTH_COLUMN[currentPosition] && (candidateOffset == -6 || candidateOffset == 10);
	}
	
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -15 || candidateOffset == -6 ||
		candidateOffset == 10 || candidateOffset == 17);
	}

	@Override
	public Knight movePiece(final Move move) {

		return new Knight(move.getMovedPiece().getPieceAlliance(),move.getDestinationCoordinate());
	}
}
