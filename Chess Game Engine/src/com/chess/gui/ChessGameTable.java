package com.chess.gui;

import com.chess.engine.board.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.ai.MiniMax;
import com.chess.engine.player.ai.MoveStrategy;
import com.google.common.collect.Lists;
import static javax.swing.SwingUtilities.*;

//import static javax.swing.SwingUtilities.isRightMouseButton;
//import static javax.swing.SwingUtilities.isLeftMouseButton;
//import static javax.swing.SwingUtilities.invokeLater;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;



public class ChessGameTable extends Observable{
	
	private final JFrame chessGameFrame;
	private final VisualChessBoardPanel visualChessBoardPanel;
	private final ChessHistoryPanel chessHistoryPanel;
	private final TakenPiecesPanel takenPiecesPanel;
	private Board chessBoard;
	private final MoveLog moveLog;
	private final GameSetup gameSetup;
	
	private ChessTile sourceChessTile;
	private ChessTile destinationChessTile;
	private Piece humanMovedPiece;
	private BoardDirection boardDirection;
	
	private Move computerMove;
	
	private boolean highlightLegalMoves;
	
	private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
	private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(300, 400);
	private final static Dimension TILE_PANEL_DIMESION = new Dimension(20,20);
	private static String defaultPieceImagesPath = "artifacts/pieces/plain/";
   
	private Color lightTileColor = Color.decode("#ADD8E6");
    private Color darkTileColor = Color.decode("#FFFFFF");
    
    private static final ChessGameTable INSTANCE = new ChessGameTable();
    	
		private ChessGameTable(){
		this.chessGameFrame = new JFrame("Chess Board!");
		this.chessGameFrame.setLayout(new BorderLayout());
		final JMenuBar chessTableMenuBar = menuBarCreation();
		this.chessGameFrame.setJMenuBar(chessTableMenuBar);
		this.chessGameFrame.setSize(OUTER_FRAME_DIMENSION);
		this.chessBoard = Board.createStandardBoard();
		this.chessHistoryPanel = new ChessHistoryPanel();
		this.takenPiecesPanel = new TakenPiecesPanel();
		this.visualChessBoardPanel = new VisualChessBoardPanel();
		this.moveLog = new MoveLog();
		this.addObserver(new ChessTableGameAIWatcher());
		this.gameSetup = new GameSetup(this.chessGameFrame, true);
		this.boardDirection = BoardDirection.NORMAL;
		this.highlightLegalMoves = false;
		this.chessGameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
		this.chessGameFrame.add(visualChessBoardPanel, BorderLayout.CENTER);
		this.chessGameFrame.add(this.chessHistoryPanel, BorderLayout.EAST);
		this.chessGameFrame.setVisible(true);
	}
	public static ChessGameTable get(){
		return INSTANCE;
	}
	private GameSetup getGameSetup(){
		return this.gameSetup;
	}
	private Board getGameBoard(){
		return this.chessBoard;
	}
    public void show() {
    	ChessGameTable.get().getMoveLog().clear();
    	ChessGameTable.get().getGameHistoryPanel().redo(chessBoard, ChessGameTable.get().getMoveLog());
    	ChessGameTable.get().getTakenPiecesPanel().redo(ChessGameTable.get().getMoveLog());
    	ChessGameTable.get().getBoardPanel().drawBoard(ChessGameTable.get().getGameBoard());
        }
		
	private JMenuBar menuBarCreation() {
		final JMenuBar chessTableMenuBar = new JMenuBar();
		chessTableMenuBar.add(createFileMenu());
		chessTableMenuBar.add(createPreferenceMenu());
		chessTableMenuBar.add(creatOptionsMenu());
		return chessTableMenuBar;
	}
	private JMenu createFileMenu() {
		final JMenu fileMenu = new JMenu("File");
		final JMenuItem openPGN = new JMenuItem("Load PGN File");
		openPGN.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Open up that png File!");
			}
		});
		fileMenu.add(openPGN);
		fileMenu.addSeparator();
		final JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);
		return fileMenu;
	}
	private JMenu createPreferenceMenu(){
		final JMenu preferenceMenu = new JMenu("Preferences");
		final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
		flipBoardMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(final ActionEvent e){
				boardDirection = boardDirection.opposite();
				visualChessBoardPanel.drawBoard(chessBoard);
			}
		});
		preferenceMenu.add(flipBoardMenuItem);
		preferenceMenu.addSeparator();
		final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", false);
		legalMoveHighlighterCheckbox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
			}
		});
		preferenceMenu.add(legalMoveHighlighterCheckbox);
		return preferenceMenu;
	}
	public JMenu creatOptionsMenu(){
		final JMenu optionsMenu = new JMenu("Options");
		final JMenuItem GameMenuItemSetup = new JMenuItem("Game Setup");
		GameMenuItemSetup.addActionListener(new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e){
			ChessGameTable.get().getGameSetup().promptUser();
			ChessGameTable.get().setupUpdate(ChessGameTable.get().getGameSetup());
		}
	});
		optionsMenu.add(GameMenuItemSetup);
		return optionsMenu;
	}
	private void setupUpdate(final GameSetup gameSetup){
		setChanged();
		notifyObservers(gameSetup);
	}
	private static class ChessTableGameAIWatcher implements Observer{

		@Override
		public void update(final Observable o, final Object arg) {
			if(ChessGameTable.get().getGameSetup().isAIPlayer(ChessGameTable.get().getGameBoard().currentPlayer()) &&
					!ChessGameTable.get().getGameBoard().currentPlayer().isInCheckMate() && 
					!ChessGameTable.get().getGameBoard().currentPlayer().isInStaleMate()){
				//Create an AI thread
				//execute AI work
				
				final AIThinkTank thinkTank = new AIThinkTank();
				thinkTank.execute();
			}
			if(ChessGameTable.get().getGameBoard().currentPlayer().isInCheckMate()){
				System.out.println("Game Over" + ChessGameTable.get().getGameBoard().currentPlayer() + "is in Checkmate!");
			}
			if(ChessGameTable.get().getGameBoard().currentPlayer().isInStaleMate()){
				System.out.println("Game Over" + ChessGameTable.get().getGameBoard().currentPlayer() + "is in StaleMate!");
			}
		}
	}
	public void updateGameBoard(final Board board){
		this.chessBoard = board;
	}
	public void updateComputerMove(final Move move){
		this.computerMove = move;
	}
	private MoveLog getMoveLog(){
		return this.moveLog;
	}
	private ChessHistoryPanel getGameHistoryPanel(){
		return this.chessHistoryPanel;
	}
	private TakenPiecesPanel getTakenPiecesPanel(){
		return this.takenPiecesPanel;
	}
	private VisualChessBoardPanel getBoardPanel(){
		return this.visualChessBoardPanel;		
	}
	private void moveMadeUpdate(final PlayerType playerType){
		setChanged();
		notifyObservers(playerType);
	}
	private static class AIThinkTank extends SwingWorker<Move, String>{
		
		private AIThinkTank(){
			
		}
		
		@Override
		protected Move doInBackground() throws Exception{
			
			final MoveStrategy miniMax = new MiniMax(4);
			final Move bestMove = miniMax.execute(ChessGameTable.get().getGameBoard());
			return bestMove;
		}
		@Override
		public void done(){
			try {
				final Move bestMove = get();
				ChessGameTable.get().updateComputerMove(bestMove);
				ChessGameTable.get().updateGameBoard(ChessGameTable.get().getGameBoard().currentPlayer().makeMove(bestMove).getTransitionBoard());
				ChessGameTable.get().getMoveLog().addMove(bestMove);
				ChessGameTable.get().getGameHistoryPanel().redo(ChessGameTable.get().getGameBoard(), ChessGameTable.get().getMoveLog());
				ChessGameTable.get().getTakenPiecesPanel().redo(ChessGameTable.get().getMoveLog());
				ChessGameTable.get().getBoardPanel().drawBoard(ChessGameTable.get().getGameBoard());
				ChessGameTable.get().moveMadeUpdate(PlayerType.COMPUTER);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
	public static class MoveLog{
		private final List<Move> moves;
		MoveLog(){
			this.moves = new ArrayList<>();
		}
		public List<Move> getMoves(){
			return this.moves;
		}
		public void addMove(final Move move){
			this.moves.add(move);
		}
		public int size(){
			return this.moves.size();
		}
		public void clear(){
			this.moves.clear();
		}
		public Move removeMove(int index){
			return this.moves.remove(index);
		}
		public boolean removeMove(final Move move){
			return this.moves.remove(move);
		}
	}
	// Chess Board visual representation which add 64 ChessTiles to the Board
	@SuppressWarnings("serial")
	private class VisualChessBoardPanel extends JPanel{
		final List<VisualChessTilePanel> boardTiles;
			VisualChessBoardPanel(){
			super(new GridLayout(8,8));
			this.boardTiles = new ArrayList<>();
			for(int i = 0; i < BoardUtils.NUM_TILES; i++){
				final VisualChessTilePanel visualChessTilePanel = new VisualChessTilePanel(this, i);
				this.boardTiles.add(visualChessTilePanel);
				add(visualChessTilePanel);
			}
			setPreferredSize(BOARD_PANEL_DIMENSION);
			validate();
		}
		public  void drawBoard(final Board board){
			removeAll();
			for(final VisualChessTilePanel visualChessTilePanel : boardDirection.traverse (boardTiles)){
				visualChessTilePanel.drawTile(board);
				add(visualChessTilePanel);
			}
			validate();
			repaint();
		}
	}
	public enum BoardDirection{
		NORMAL {
			@Override
			List<VisualChessTilePanel>traverse (final List<VisualChessTilePanel> boardTiles){
				return boardTiles ;
			}
			@Override
			BoardDirection opposite(){
				return FLIPPED;
			}
		},
		FLIPPED {
			@Override
			List<VisualChessTilePanel> traverse (final List<VisualChessTilePanel> boardTiles){
				return Lists.reverse(boardTiles);
			}
			@Override
			BoardDirection opposite(){
				return NORMAL;
			}
		};
		abstract List<VisualChessTilePanel> traverse(final List<VisualChessTilePanel> boardTiles);
		abstract BoardDirection opposite();
	}
	
	enum PlayerType{
		HUMAN,
		COMPUTER
	}
	
	@SuppressWarnings("serial")
	private class VisualChessTilePanel extends JPanel{
		private final int chessTileId;
		VisualChessTilePanel(final VisualChessBoardPanel visualChessBoardPanel,
							 final int chessTileId){
			super(new GridBagLayout());
			this.chessTileId = chessTileId;
			setPreferredSize(TILE_PANEL_DIMESION);
			assignChessTileColor();
			assignTilePieceIcon(chessBoard);
			highlightLegals(chessBoard);
			addMouseListener(new MouseListener(){
				@Override
				public void mouseClicked(final MouseEvent e){
					if(isRightMouseButton(e)){
						//System.out.println("I Clicked Right Mouse Button!");
							sourceChessTile = null;
							destinationChessTile = null;
							humanMovedPiece = null;
							//First Click
					}else if(isLeftMouseButton(e)){ 
						//System.out.println("I Clicked Left Mouse Button!");
							 if(sourceChessTile == null){
								sourceChessTile = chessBoard.getTile(chessTileId);
								humanMovedPiece = sourceChessTile.getPiece();
								if(humanMovedPiece == null){
									sourceChessTile = null;
								}
							}else { 
								destinationChessTile = chessBoard.getTile(chessTileId);
								final Move move = Move.MoveFactory.createMove(chessBoard, sourceChessTile.getTileCoordinate(), 
																			  destinationChessTile.getTileCoordinate());
								final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
								if(transition.getMoveStatus().isDone()){
									chessBoard = transition.getTransitionBoard();
									moveLog.addMove(move);
								}
								sourceChessTile = null;
								destinationChessTile = null; 
								humanMovedPiece = null;
								}
							 //Update individual chess panels and the chess board when a move is made!
							invokeLater(new Runnable(){
								@Override
								public void run(){
									chessHistoryPanel.redo(chessBoard, moveLog);
									takenPiecesPanel.redo(moveLog);
									
									if(gameSetup.isAIPlayer(chessBoard.currentPlayer())){
										ChessGameTable.get().moveMadeUpdate(PlayerType.HUMAN);
									}
									visualChessBoardPanel.drawBoard(chessBoard);
								}
							});
						}  
				     }
				@Override
				public void mousePressed(final MouseEvent e) {
				}
				@Override
				public void mouseReleased(final MouseEvent e) {
				}
				@Override
				public void mouseEntered(final MouseEvent e) {
				}
				@Override
				public void mouseExited(final MouseEvent e) {
				}
			});
			validate();
		}
		public void drawTile(final Board board){
			assignChessTileColor();
			assignTilePieceIcon(board);
			highlightLegals(board);
			validate();
			repaint();
		}
		private void assignTilePieceIcon(final Board board){
			this.removeAll();
			if(board.getTile(this.chessTileId).isTileOcuppied()){
				try{
				final BufferedImage image = 
				ImageIO.read(new File(defaultPieceImagesPath + board.getTile(this.chessTileId).getPiece()
					   .getPieceAlliance().toString().substring(0,1) + board.getTile(this.chessTileId)
					   .getPiece().toString() + ".gif"));
				add(new JLabel(new ImageIcon(image)));
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		}
		private void highlightLegals(final Board board){
				if(highlightLegalMoves){
			    	    for(final Move move : pieceLegalMoves(board)){
			    		if(move.getDestinationCoordinate() == this.chessTileId){
			    			try{
			    				add(new JLabel(new ImageIcon(ImageIO.read(new File("artifacts/misc/green_dot.png")))));
			    			} catch(Exception e) {
			    				e.printStackTrace();
			    			}
			    		}
			    	}
			   }
		}		
		private Collection<Move> pieceLegalMoves(final Board board){
			if (humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()){
			return humanMovedPiece.calculateLegalMoves(board);
		}
		return Collections.emptyList();
		}
	// Colouring of Chess Board tiles 		
		private void assignChessTileColor() {
			if(BoardUtils.EIGHTH_RANK[this.chessTileId] ||
			   BoardUtils.SIXTH_RANK[this.chessTileId] ||
			   BoardUtils.FOURTH_RANK[this.chessTileId] ||
			   BoardUtils.SECOND_RANK[this.chessTileId]){
			   setBackground(this.chessTileId % 2 == 0 ? lightTileColor : darkTileColor);
			} else if(BoardUtils.SEVENTH_RANK[this.chessTileId] ||
					  BoardUtils.FIFTH_RANK[this.chessTileId] ||
					  BoardUtils.THIRD_RANK[this.chessTileId] ||
					  BoardUtils.FIRST_RANK[this.chessTileId]){
				setBackground(this.chessTileId % 2 != 0 ? lightTileColor : darkTileColor);
				
			}
		}
	}
}