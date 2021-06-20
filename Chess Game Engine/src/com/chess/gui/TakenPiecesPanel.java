package com.chess.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.google.common.primitives.Ints;

import static com.chess.gui.ChessGameTable.MoveLog;

@SuppressWarnings("serial")
public class TakenPiecesPanel extends JPanel{
    
    private final JPanel northPanel;
    private final JPanel southPanel;
    private static final Color PANEL_COLOR = Color.decode("0xFDF5E6");
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(100,100);
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder (EtchedBorder.RAISED);
    private static String defaultPieceImagesPath = "artifacts/pieces/plain/";
    
    public TakenPiecesPanel(){
        super(new BorderLayout());
        this.setBackground(PANEL_COLOR);
        this.setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(12, 4));
        this.southPanel = new JPanel(new GridLayout(12, 4));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);
        add(this.northPanel, BorderLayout.NORTH);
        add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSION);
        
    }
    public void redo(final MoveLog moveLog){
        this.southPanel.removeAll();
        this.northPanel.removeAll();
        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();
        for(final Move move : moveLog.getMoves()){
            if(move.isAttack()){
                final Piece takenPiece = move.getAttackedPiece();
                if(takenPiece.getPieceAlliance().isWhite()){
                    whiteTakenPieces.add(takenPiece);
                }else if(takenPiece.getPieceAlliance().isBlack()){
                    blackTakenPieces.add(takenPiece);
                }else {
                    throw new RuntimeException("should not reach here!");
                }
            }
        }
        Collections.sort(whiteTakenPieces, new Comparator<Piece>(){
            @Override
            public int compare(Piece o1, Piece o2){
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });
        Collections.sort(blackTakenPieces, new Comparator<Piece>(){
            @Override
            public int compare(Piece o1, Piece o2){
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });
        //Display the taken pieces
        for(final Piece takenPiece : whiteTakenPieces){
            try{
                File f = new File(defaultPieceImagesPath
                  +takenPiece.getPieceAlliance().toString().substring(0, 1) + "" + takenPiece.toString() + ".gif");
                final BufferedImage image = ImageIO.read(f);
                final ImageIcon icon = new ImageIcon(image); 
                final JLabel imageLable = new JLabel(icon);
                this.southPanel.add(imageLable);
            }catch(final IOException e){
                e.printStackTrace();
            }
        }
        for(final Piece takenPiece : blackTakenPieces){
            try{
                File f = new File(defaultPieceImagesPath
                  +takenPiece.getPieceAlliance().toString().substring(0, 1) + "" + takenPiece.toString() + ".gif");
                final BufferedImage image = ImageIO.read(f);
                final ImageIcon icon = new ImageIcon(image); 
                final JLabel imageLable = new JLabel(icon);
                this.southPanel.add(imageLable);
            }catch(final IOException e){
                e.printStackTrace();
            }
        }
        validate();
    }
}
