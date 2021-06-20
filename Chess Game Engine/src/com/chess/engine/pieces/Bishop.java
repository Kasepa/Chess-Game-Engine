package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.ChessTile;
import com.chess.engine.board.Move;
//import com.chess.engine.board.Move.AttackMove;
import com.chess.engine.board.Move.MajorMove;
import com.chess.engine.board.Move.MajorAttackMove;
import com.google.common.collect.ImmutableList;

public class Bishop extends Piece {

	private final static int[] CANDIDATE_MOVE_VETOR_COORDINATES = {-9, -7, 7, 9 };
	public Bishop(final Alliance pieceAlliance, final int piecePosition ) {
		super(PieceType.BISHOP, piecePosition, pieceAlliance, true);
	}
	
	public Bishop(final Alliance pieceAlliance, 
			      final int piecePosition, 
			      final boolean isFirstMove){
		super(PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove);
	}
	@Override
	public Collection<Move>calculateLegalMoves( final Board board){
		final List<Move> legalMoves = new ArrayList<>();
		for(final int candidateCoordinateOffset:CANDIDATE_MOVE_VETOR_COORDINATES ){
			int candidateDestinationCoordinate = this.piecePosition;
			while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
				
				if(isFirstColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset )||
				isEighthColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset )){
					break;
				}
				candidateDestinationCoordinate += candidateCoordinateOffset;
					if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
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
						break;
					}	
				}
			}
		}
			return ImmutableList.copyOf(legalMoves);
	}
	
	@Override
	public String toString(){
		return PieceType.BISHOP.toString();
	}
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
		return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9) || candidateOffset == 7;
	}
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7) || candidateOffset == 9;
	}
	@Override
	public Bishop movePiece(final Move move) {
		return new Bishop(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
	}
}
