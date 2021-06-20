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
//import com.chess.engine.pieces.Piece.PieceType;
import com.google.common.collect.ImmutableList;

public class Rook extends Piece{

	private final static int[] CANDIDATE_MOVE_VETOR_COORDINATES = {-8, -1, 1, 8 };
	
	public Rook(final Alliance pieceAlliance,final int piecePosition ) {
		super(PieceType.ROOK, piecePosition, pieceAlliance, true);
	}
	public Rook(final Alliance pieceAlliance,
			    final int piecePosition, 
			    final boolean isFirstMove){
		super(PieceType.ROOK, piecePosition, pieceAlliance, isFirstMove);
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
		return PieceType.ROOK.toString();
	}
	
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
		return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1);
	}
	
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == 1);
	}
	@Override
	public Rook movePiece(final Move move) {

		return new Rook(move.getMovedPiece().getPieceAlliance(),move.getDestinationCoordinate());
	}

}
