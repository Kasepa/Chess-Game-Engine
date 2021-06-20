package com.chess.engine.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chess.engine.Alliance;
import com.chess.engine.pieces.Bishop;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Knight;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Queen;
import com.chess.engine.pieces.Rook;
import com.chess.engine.player.Player;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class Board {
    
    private final List<ChessTile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    private final WhitePlayer WhitePlayer;
    private final BlackPlayer BlackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;
            
    private Board(final Builder builder){
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;
        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);
        this.WhitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.BlackPlayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.WhitePlayer, this.BlackPlayer);
    }
    // Printing out the Board.
    @Override   
    public String toString(){
        final StringBuilder builder = new StringBuilder();
        for(int i = 0; i < BoardUtils.NUM_TILES; i++){
            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s", tileText));
            if((i + 1) % BoardUtils.NUM_TILES_PER_ROW == 0){
                builder.append("\n");
            }
        }
        return builder.toString();
    }
    public Player WhitePlayer(){
        return this.WhitePlayer;
    }
    public Player BlackPlayer(){
        return this.BlackPlayer;
    }
    public Pawn getEnPassantPawn(){
        return this.enPassantPawn;
    }
    public Collection<Piece> getBlackPieces(){
        return this.blackPieces;
    }
    public Collection<Piece> getWhitePieces(){
        return this.whitePieces;
    }
    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();
         for(final Piece piece :pieces){
             legalMoves.addAll(piece.calculateLegalMoves(this));
        }        
         return ImmutableList.copyOf(legalMoves);
    }
    private static Collection<Piece> calculateActivePieces(final List<ChessTile> gameBoard,
            final Alliance alliance) {
                final List<Piece> activePieces = new ArrayList<>();
                for(final ChessTile tile : gameBoard){
                    if(tile.isTileOcuppied()){
                        final Piece piece = tile.getPiece();
                        if(piece.getPieceAlliance() == alliance){
                            activePieces.add(piece);
                }
            }
        }
        return ImmutableList.copyOf(activePieces);
    }
    public Player currentPlayer(){
        return this.currentPlayer;
    }
    public ChessTile getTile(final int tileCoordinate){
        return gameBoard.get(tileCoordinate);
    }
    private static List<ChessTile> createGameBoard(final Builder builder){
        final ChessTile[] tiles = new ChessTile[BoardUtils.NUM_TILES];
        for(int i = 0; i < BoardUtils.NUM_TILES; i++){
            tiles[i] =ChessTile.createTile(i, builder.boardConfig.get(i));
        }
        return ImmutableList.copyOf(tiles);
    }
    public static Board createStandardBoard(){
        final Builder builder = new Builder();
        // Black Layout
        builder.setPiece(new Rook(Alliance.BLACK, 0));
        builder.setPiece(new Knight(Alliance.BLACK, 1));
        builder.setPiece(new Bishop(Alliance.BLACK, 2));
        builder.setPiece(new Queen(Alliance.BLACK, 3));
        builder.setPiece(new King(Alliance.BLACK, 4, true, true));
        builder.setPiece(new Bishop(Alliance.BLACK, 5));
        builder.setPiece(new Knight(Alliance.BLACK, 6));
        builder.setPiece(new Rook(Alliance.BLACK, 7));
        builder.setPiece(new Pawn(Alliance.BLACK, 8));
        builder.setPiece(new Pawn(Alliance.BLACK, 9));
        builder.setPiece(new Pawn(Alliance.BLACK, 10));
        builder.setPiece(new Pawn(Alliance.BLACK, 11));
        builder.setPiece(new Pawn(Alliance.BLACK, 12));
        builder.setPiece(new Pawn(Alliance.BLACK, 13));
        builder.setPiece(new Pawn(Alliance.BLACK, 14));
        builder.setPiece(new Pawn(Alliance.BLACK, 15));
        //White Layout
        builder.setPiece(new Pawn(Alliance.WHITE, 48));
        builder.setPiece(new Pawn(Alliance.WHITE, 49));
        builder.setPiece(new Pawn(Alliance.WHITE, 50));
        builder.setPiece(new Pawn(Alliance.WHITE, 51));
        builder.setPiece(new Pawn(Alliance.WHITE, 52));
        builder.setPiece(new Pawn(Alliance.WHITE, 53));
        builder.setPiece(new Pawn(Alliance.WHITE, 54));
        builder.setPiece(new Pawn(Alliance.WHITE, 55));
        builder.setPiece(new Rook(Alliance.WHITE, 56));
        builder.setPiece(new Knight(Alliance.WHITE, 57));
        builder.setPiece(new Bishop(Alliance.WHITE, 58));
        builder.setPiece(new Queen(Alliance.WHITE, 59));
        builder.setPiece(new King(Alliance.WHITE, 60, true, true));
        builder.setPiece(new Bishop(Alliance.WHITE, 61));
        builder.setPiece(new Knight(Alliance.WHITE, 62));
        builder.setPiece(new Rook(Alliance.WHITE, 63));
        //White to Move
        builder.setMoveMaker(Alliance.WHITE);
            return builder.build();
    }
    public Iterable<Move> getAllLegalMoves() {
                return Iterables.unmodifiableIterable(Iterables.concat(this.WhitePlayer.getLegalMoves(), 
                                                               this.BlackPlayer.getLegalMoves()));
    }
    public static class Builder{
        Map<Integer, Piece> boardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;
        Move transitionMove;
    public Builder() {
            this.boardConfig = new HashMap<>();
        }
    public Builder setPiece (final Piece piece){
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }
    public Builder setMoveMaker(final Alliance nextMoveMaker){
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }
    public Board build(){
            return new Board(this);
        }
    public void setEnPassantPawn(final Pawn enPassantPawn) {
        this.enPassantPawn = enPassantPawn;
        }
        
    }
    
    
}
