package com.thinking.machines.chess.client.logic;
import com.thinking.machines.nframework.client.*;
import com.thinking.machines.chess.common.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
public class Chess extends JPanel implements ActionListener
{
private NFrameworkClient client;
private GameInit gameInit;
String username;
class UNDOMove
{
public JButton tile1,tile2;
public String name1,name2;
public Color tileColor1;
public Color tileColor2;
public int row1,row2,column1,column2;
public boolean castling;
public boolean pawnPromotion;
}
private Map<Byte,String> pieceNamesMap;
private boolean whiteKingMoved=false;
private boolean rightWhiteRookMoved=false;
private boolean leftWhiteRookMoved=false;
private boolean blackKingMoved=false;
private boolean rightBlackRookMoved=false;
private boolean leftBlackRookMoved=false;
private boolean white=true;
private boolean black=false;
private ButtonPanel buttonPanel;
private JPanel boardPanel;
private JButton[][] tiles;
private byte[][] possibleMoves;
private ImageIcon blackTile;
private ImageIcon whiteTile;
private Container container;
private JButton sourceTile=null;
private JButton targetTile=null;
private boolean click=false;
private Color darkTileColor;
private Color lightTileColor;
private Border darkTileBorder;
private Border lightTileBorder;
private boolean undo=false;
private ImageIcon blackRookIcon;
private ImageIcon blackKnightIcon;
private ImageIcon blackBishopIcon;
private ImageIcon blackQueenIcon;
private ImageIcon blackKingIcon;
private ImageIcon blackPawnIcon;
private ImageIcon whiteRookIcon;
private ImageIcon whiteKnightIcon;
private ImageIcon whiteBishopIcon;
private ImageIcon whiteQueenIcon;
private ImageIcon whiteKingIcon;
private ImageIcon whitePawnIcon;
private UNDOMove undoMove;
private boolean undoMoveValid=false;
private byte startRowIndex,startColumnIndex,destinationRowIndex,destinationColumnIndex;
private javax.swing.Timer getOpponentMoveTimer;
private boolean canIPlay;
private void canIPlay()
{
try
{
this.canIPlay=(boolean)client.execute("/TMChessServer/canIPlay",gameInit.gameId,gameInit.playerColor);
System.out.println(canIPlay);
if(!canIPlay)
{
getOpponentMoveTimer.start();
System.out.println("Switched of canIPlayTimer and switched on getOpponentMoveTimer");
}
else
{
JOptionPane.showMessageDialog(Chess.this,"You are first "+username);
}
}catch(Throwable t)
{
JOptionPane.showMessageDialog(Chess.this,t.getMessage());
}

}
private void addActionListeners()
{
getOpponentMoveTimer=new javax.swing.Timer(1000,ev->{
try
{
Move move=(Move)client.execute("/TMChessServer/getOpponentMove",gameInit.gameId,username);
if(move==null)
{
return;
}
((javax.swing.Timer)ev.getSource()).stop();
String pieceName=getPieceName(move.piece);

SwingUtilities.invokeLater(()->{
movePiece(pieceName);
updateBoardState(move);
});
}catch(Throwable t)
{
JOptionPane.showMessageDialog(Chess.this,t.getMessage());
}
});
}
private String getPieceName(byte piece)
{
String pieceName=(piece>0)?"white":"black";
piece*=-1;
pieceName=piece+pieceNamesMap.get((byte)piece);
System.out.println("getPieceName method : (pieceName) "+pieceName);
return pieceName;
}
private void populateDataStructures()
{
this.pieceNamesMap=Map.ofEntries(
Map.entry((byte)1,"Pawn"),
Map.entry((byte)2,"Knight"),
Map.entry((byte)3,"Bishop"),
Map.entry((byte)4,"Rook"),
Map.entry((byte)5,"Queen"),
Map.entry((byte)6,"King")
);
}
public Chess(NFrameworkClient client,GameInit gameInit,String username)
{
addActionListeners();
populateDataStructures();
this.client=client;
this.gameInit=gameInit;
this.username=username;
undoMove=new UNDOMove();
tiles=new JButton[8][8];
boardPanel=new JPanel();
boardPanel.setLayout(new GridLayout(8,8));
setLayout(new BorderLayout());
JButton tile;
blackTile=new ImageIcon(this.getClass().getResource("/icons/lightBlack_tile.png"));
whiteTile=new ImageIcon(this.getClass().getResource("/icons/grey_tile.png"));
darkTileColor=new Color(70,70,70);
lightTileColor=new Color(240,240,240);
darkTileBorder=BorderFactory.createLineBorder(new Color(20,20,20),2);
lightTileBorder=BorderFactory.createLineBorder(new Color(255,255,255),2);
Color tileColor;
Border tileBorder;
blackRookIcon=new ImageIcon(this.getClass().getResource("/icons/black_rook.png"));
blackKnightIcon=new ImageIcon(this.getClass().getResource("/icons/black_knight.png"));
blackBishopIcon=new ImageIcon(this.getClass().getResource("/icons/black_bishop.png"));
blackQueenIcon=new ImageIcon(this.getClass().getResource("/icons/black_queen.png"));
blackKingIcon=new ImageIcon(this.getClass().getResource("/icons/black_king.png"));
blackPawnIcon=new ImageIcon(this.getClass().getResource("/icons/black_pawn.png"));


whiteRookIcon=new ImageIcon(this.getClass().getResource("/icons/white_rook.png"));
whiteKnightIcon=new ImageIcon(this.getClass().getResource("/icons/white_knight.png"));
whiteBishopIcon=new ImageIcon(this.getClass().getResource("/icons/white_bishop.png"));
whiteQueenIcon=new ImageIcon(this.getClass().getResource("/icons/white_queen.png"));
whiteKingIcon=new ImageIcon(this.getClass().getResource("/icons/white_king.png"));
whitePawnIcon=new ImageIcon(this.getClass().getResource("/icons/white_pawn.png"));



for(int e=0;e<8;e++)
{
for(int f=0;f<8;f++)
{
if(e%2==0)
{
if(f%2==0)
{
 tileColor=lightTileColor;
 tileBorder=darkTileBorder;
}
else
{
 tileColor=darkTileColor;
 tileBorder=lightTileBorder;
}
}
else
{
if(f%2==0) 
{
tileColor=darkTileColor;
tileBorder=lightTileBorder;
}
else 
{
tileColor=lightTileColor;
tileBorder=darkTileBorder;
}
}
tile=setupBoard(e,f,tileColor,tileBorder);
tile.addActionListener(this);
tiles[e][f]=tile;
boardPanel.add(tile);
}
}
Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
int width=650;
int height=600;
setSize(width,height);
int x=(d.width/2)-(width/2);
int y=(d.height/2)-(height/2);
buttonPanel=new ButtonPanel();
//container.add(boardPanel,BorderLayout.CENTER);
//container.add(buttonPanel,BorderLayout.EAST);
add(boardPanel,BorderLayout.CENTER);
add(buttonPanel,BorderLayout.EAST);
setLocation(x,y);
setVisible(true);


canIPlay();
}
private void reset()
{
this.click=false;
this.targetTile=null;
this.sourceTile=null;
startRowIndex=-1;
destinationRowIndex=-1;
startColumnIndex=-1;
destinationColumnIndex=-1;
if(possibleMoves!=null)
{
Color tileColor=null;
Border tileBorder=null;
for(int e=0;e<8;e++) 
{
for(int f=0;f<8;f++) 
{
if(e%2==0)
{
if(f%2==0)
{
 tileColor=lightTileColor;
 tileBorder=darkTileBorder;
}
else
{
 tileColor=darkTileColor;
 tileBorder=lightTileBorder;
}
}
else
{
if(f%2==0) 
{
tileColor=darkTileColor;
tileBorder=lightTileBorder;
}
else 
{
tileColor=lightTileColor;
tileBorder=darkTileBorder;
}
}
tiles[e][f].setBackground(tileColor);
tiles[e][f].setBorder(tileBorder);
//tiles[e][f].setBorder(UIManager.getBorder("Button.border"));
possibleMoves[e][f]=0;
}
}
}
/*
//resetting undomove
undoMove.name1="";
undoMove.name2="";
undoMove.tileColor1=null;
undoMove.tileColor2=null;
undoMove.row1=-1;
undoMove.row2=-1;
undoMove.column1=-1;
undoMove.column2=-1;
undoMove.castling=false;
undoMove.castling=false;
*/	
}
private JButton setupBoard(int e,int f,Color tileColor,Border tileBorder)
{
byte board[][]=this.gameInit.board;	
JButton tile=new JButton();
tile.setBackground(tileColor);
tile.setBorder(tileBorder);
tile.setLayout(new BorderLayout());
//generating black piece
if(board[e][f]<0)
{
//pawn
if(board[e][f]==-1)
{
tile.add(new JLabel(blackPawnIcon));
tile.setActionCommand("blackPawn");
}//knight
else if(board[e][f]==-2)
{
tile.setActionCommand("blackKnight");
tile.add(new JLabel(blackKnightIcon));
}//Bishop
else if(board[e][f]==-3)
{
tile.setActionCommand("blackBishop");
tile.add(new JLabel(blackBishopIcon));
}//Rook
else if(board[e][f]==-4)
{
JLabel blackRookLabel=new JLabel(blackRookIcon);
tile.setActionCommand("blackRook");
tile.add(blackRookLabel,BorderLayout.CENTER);
}//Queen
else if(board[e][f]==-5)
{
tile.setActionCommand("blackQueen");
tile.add(new JLabel(blackQueenIcon));
}//King
else if(board[e][f]==-6)
{
tile.setActionCommand("blackKing");
tile.add(new JLabel(blackKingIcon));
}
}// for black piece ends here
//for white piece starts here
else
{
//pawn
if(board[e][f]==1)
{
tile.add(new JLabel(whitePawnIcon));
tile.setActionCommand("whitePawn");
}//knight
else if(board[e][f]==2)
{
tile.setActionCommand("whiteKnight");
tile.add(new JLabel(whiteKnightIcon));
}//Bishop
else if(board[e][f]==3)
{
tile.setActionCommand("whiteBishop");
tile.add(new JLabel(whiteBishopIcon));
}//Rook
else if(board[e][f]==4)
{
JLabel whiteRookLabel=new JLabel(whiteRookIcon);
tile.setActionCommand("whiteRook");
tile.add(whiteRookLabel,BorderLayout.CENTER);
}//Queen
else if(board[e][f]==5)
{
tile.setActionCommand("whiteQueen");
tile.add(new JLabel(whiteQueenIcon));
}//King
else if(board[e][f]==6)
{
tile.setActionCommand("whiteKing");
tile.add(new JLabel(whiteKingIcon));
}
}

//generating white piece ends here
return tile;
}

public void actionPerformed(ActionEvent ev)
{
if(!canIPlay)
{
JButton button=(JButton)ev.getSource();
button.setBorder(UIManager.getBorder("Button.border"));//for making the button border as the default as system
button.setEnabled(false);//doing this trick to remove foucs from button
button.setEnabled(true);
return;
}
boolean found=false;
JButton tile=null;
byte e=0;
byte f=0;
for(e=0;e<8;e++)
{
for(f=0;f<8;f++)
{
tile=tiles[e][f];
if(tile==ev.getSource())
{
found=true;
break;
}
}
if(found) break;
}

//clicking again on the same piece
//clicking the empty tile
if(this.sourceTile==null && tile.getActionCommand().equals("")) 
{
tile.setEnabled(false);
tile.setEnabled(true);
return;
}
if(tile==this.sourceTile)
{
this.sourceTile.setBorder(UIManager.getBorder("Button.border"));//for making the button border as the default as system
sourceTile.setEnabled(false);//doing this trick to remove foucs from button
sourceTile.setEnabled(true);
reset();
return;
}


if(click==false) //clicking on the source tile
{
String pieceName=tile.getActionCommand();
if(pieceName.equals("")) 
{
reset();
return;
}
this.sourceTile=tile;
String pieceColor=this.sourceTile.getActionCommand().substring(0,5);
if(this.white && pieceColor.equals("black")) 
{
System.out.println("white turn,Not black");
return;
}
else if(this.black && pieceColor.equals("white")) 
{
System.out.println("black turn,Not white");
return;
}
startRowIndex=e;
startColumnIndex=f;
click=true;
this.sourceTile.setBorder(BorderFactory.createLineBorder(Color.GREEN,3));
this.sourceTile.setBackground(new Color(144,238,144));
if(pieceName.equals("whiteKing"))
{
}else if(pieceName.equals("blackKing"))
{
}


try
{
System.out.println("Getting possibleMoves");
possibleMoves=(byte[][])client.execute("/TMChessServer/getPossibleMoves",gameInit.gameId,(byte)startRowIndex,(byte)startColumnIndex);
}catch(Throwable t)
{
JOptionPane.showMessageDialog(Chess.this,t);
}


JButton validTile;
for(e=0;e<8;e++)
{
for(f=0;f<8;f++)
{
//System.out.print(possibleMoves[e][f]+"  ");
if(possibleMoves[e][f]==0) continue;
validTile=tiles[e][f];
//validTile.setBorder(BorderFactory.createDashedBorder(Color.RED,30,10));
Color c;
c=Color.GREEN;
//validTile.setBackground(c);
validTile.setBorder(BorderFactory.createLineBorder(c,3));
}
//System.out.println();
}

}
else //clicking on the target tile
{
destinationRowIndex=e;
destinationColumnIndex=f;
click=false;
this.targetTile=tile;


String sourceIconName=this.sourceTile.getActionCommand();
String targetIconName=this.targetTile.getActionCommand();

String sourceIconPieceColor=sourceIconName.substring(0,5);
String targetIconPieceColor="";
boolean capture=false;
int rowIndex=0;
int columnIndex=0;
if(targetIconName.equals("")==false)
{
capture=true;
rowIndex=destinationRowIndex;
columnIndex=destinationColumnIndex;
targetIconPieceColor=targetIconName.substring(0,5);
}
if(capture && targetIconPieceColor.equals(sourceIconPieceColor))
{
System.out.println("bye bye same color");
this.sourceTile.setBorder(UIManager.getBorder("Button.border"));
targetTile.setEnabled(false);
targetTile.setEnabled(true);
reset();
return;
}


//boolean validMove=validMovement(sourceIconName);

//place a call to server side method to get validation of the move
MoveResponse moveResponse=null;
byte validMove=0;
byte castlingType=0;
Move move=new Move();
move.player=gameInit.playerColor;
move.piece=gameInit.board[startRowIndex][startColumnIndex];
move.fromX=(byte)startRowIndex;
move.fromY=(byte)startColumnIndex;
move.toX=(byte)destinationRowIndex;
move.toY=(byte)destinationColumnIndex;
move.isLastMove=-1;
move.castlingType=0;

//now to check if user did castling
if(move.piece==6)
{
move.castlingType=(byte)getCastlingType((byte)6);
}else if(move.piece==-6)
{
move.castlingType=(byte)getCastlingType((byte)-6);
}


try
{
moveResponse=(MoveResponse)client.execute("/TMChessServer/submitMove",gameInit.gameId,move);
}catch(Throwable t)
{
JOptionPane.showMessageDialog(Chess.this,t.getMessage());
}
validMove=moveResponse.isValid;
castlingType=moveResponse.castlingType;
if(validMove==0)
{
this.sourceTile.setBorder(UIManager.getBorder("Button.border"));
targetTile.setEnabled(false);
targetTile.setEnabled(true);
reset();
return;
}
this.sourceTile.setBorder(BorderFactory.createLineBorder(Color.GREEN,3));
movePiece(sourceIconName);
move.castlingType=castlingType;
//if castling case yes, then updateBoardState will handle the byte[][] board
//as well as the updation of GUI
updateBoardState(move);

this.sourceTile.setBorder(UIManager.getBorder("Button.border"));


/*
//pawn promotion case
if(sourceIconName.equals("whitePawn") && this.destinationRowIndex==0)
{
undoMove.pawnPromotion=true;
promotePawn("white");
}
else if(sourceIconName.equals("blackPawn") && this.destinationRowIndex==7)
{
undoMove.pawnPromotion=true;
promotePawn("black");
}

//king castling
if(sourceIconName.equals("whiteKing"))
{
if(whiteKingMoved==false && destinationColumnIndex==6)
{
//king's side castling
undoMove.castling=true;
System.out.println("King's side castling case (white)");
this.startRowIndex=7;
this.startColumnIndex=7;
this.destinationRowIndex=7;
this.destinationColumnIndex=5;
this.sourceTile=tiles[startRowIndex][startColumnIndex];
this.targetTile=tiles[destinationRowIndex][destinationColumnIndex];
movePiece("whiteRook");
}
else if(whiteKingMoved==false && destinationColumnIndex==2)
{
//queen's side castling
undoMove.castling=true;
System.out.println("queen's side castling case (white)");
this.startRowIndex=7;
this.startColumnIndex=0;
this.destinationRowIndex=7;
this.destinationColumnIndex=3;
this.sourceTile=tiles[startRowIndex][startColumnIndex];
this.targetTile=tiles[destinationRowIndex][destinationColumnIndex];
movePiece("whiteRook");
}
}
else if(sourceIconName.equals("blackKing"))
{
if(blackKingMoved==false && destinationColumnIndex==6)
{
//king's side castling
undoMove.castling=true;
System.out.println("King's side castling case (black)");
this.startRowIndex=0;
this.startColumnIndex=7;
this.destinationRowIndex=0;
this.destinationColumnIndex=5;
this.sourceTile=tiles[startRowIndex][startColumnIndex];
this.targetTile=tiles[destinationRowIndex][destinationColumnIndex];
movePiece("blackRook");
}
else if(blackKingMoved==false && destinationColumnIndex==2)
{
//queen's side castling
undoMove.castling=true;
System.out.println("queen's side castling case (black)");
this.startRowIndex=0;
this.startColumnIndex=0;
this.destinationRowIndex=0;
this.destinationColumnIndex=3;
this.sourceTile=tiles[startRowIndex][startColumnIndex];
this.targetTile=tiles[destinationRowIndex][destinationColumnIndex];
movePiece("blackRook");
}
}
*/

/*
//switching black/white turn
if(white) 
{

if(whiteKingMoved==false)
{
if(tiles[7][4].getActionCommand().equals("whiteKing")==false)
{
System.out.println("White King moved : "+whiteKingMoved);
whiteKingMoved=true;
}
}
if(leftWhiteRookMoved==false)
{
if(tiles[7][0].getActionCommand().equals("whiteRook")==false)
{
System.out.println("Left White Rook moved : "+rightWhiteRookMoved);
leftWhiteRookMoved=true;
}
}
if(rightWhiteRookMoved==false)
{
if(tiles[7][7].getActionCommand().equals("whiteRook")==false)
{
System.out.println("Right White Rook moved : "+leftWhiteRookMoved);
rightWhiteRookMoved=true;
}
}
if(targetIconName.equals("blackKing"))
{
reset();
JOptionPane.showMessageDialog(this,"White wins!","Game over",JOptionPane.INFORMATION_MESSAGE);
setEnabled(false);
return;
}
//checking is king in danger or not
if(false)//CheckmateDetector.detectCheckmate(tiles,"black"))
{
reset();
JOptionPane.showMessageDialog(this,"White wins!","Game over",JOptionPane.INFORMATION_MESSAGE);
setEnabled(false);
return;
}
white=false;
black=true;
}
else
{
if(blackKingMoved==false)
{
if(tiles[0][4].getActionCommand().equals("blackKing")==false)
{
blackKingMoved=true;
}
}
if(leftBlackRookMoved==false)
{
if(tiles[0][0].getActionCommand().equals("blackRook")==false)
{
leftBlackRookMoved=true;
}
}
if(rightBlackRookMoved==false)
{
if(tiles[0][7].getActionCommand().equals("blackRook")==false)
{
leftBlackRookMoved=true;
}
}
if(targetIconName.equals("whiteKing"))
{
reset();
JOptionPane.showMessageDialog(this,"Black wins!","Game over",JOptionPane.INFORMATION_MESSAGE);
setEnabled(false);
return;
}
if(false)//CheckmateDetector.detectCheckmate(tiles,"white"))
{
reset();
JOptionPane.showMessageDialog(this,"Black wins!","Game over",JOptionPane.INFORMATION_MESSAGE);
setEnabled(false);
return;
}
white=true;
black=false;
}
*/
reset();

}

}
private byte getCastlingType(byte kingPiece)
{
byte castlingType=0;
if(kingPiece==6 && this.startRowIndex==7 && this.startColumnIndex==4 )
{
if(this.destinationRowIndex==7 && this.destinationColumnIndex==6)
{
castlingType=1;
}
else if(this.destinationRowIndex==7 && this.destinationColumnIndex==2)
{
castlingType=2;
}
}
else if(kingPiece==-6 && this.startRowIndex==0 && this.startColumnIndex==4)
{
if(this.destinationRowIndex==0 && this.destinationColumnIndex==6)
{
castlingType=3;
}
else if(this.destinationRowIndex==0 && this.destinationColumnIndex==2)
{
castlingType=4;
}
}
return castlingType;
}
private void updateBoardState(Move move)
{

byte fromX=move.fromX;
byte fromY=move.fromY;
byte toX=move.toX;
byte toY=move.toY;
byte castlingType=move.castlingType;
gameInit.board[fromX][fromY]=0;
gameInit.board[toX][toY]=move.piece;

if(castlingType!=0)
{
if(castlingType==1)//white king side castling 
{
fromX=7;
fromY=7;
toX=7;
toY=5;
}else if(castlingType==2)//white queen side castling
{
fromX=7;
toX=7;
fromY=0;
toY=3;
}else if(castlingType==3)//black king side castling
{
fromX=0;
toX=0;
fromY=7;
toY=5;
}else if(castlingType==4)//black queen side castling
{
fromX=0;
toX=0;
fromY=0;
toY=3;
}//now update the board state
byte piece=gameInit.board[fromX][fromY];
gameInit.board[fromX][fromY]=0;
gameInit.board[toX][toY]=piece;
this.startRowIndex=fromX;
this.startColumnIndex=fromY;
this.destinationRowIndex=toX;
this.destinationColumnIndex=toY;
this.sourceTile=tiles[startRowIndex][startColumnIndex];
this.targetTile=tiles[destinationRowIndex][destinationColumnIndex];
String pieceName=((castlingType>2)?"white":"black")+"Rook";
System.out.println("Castling Case : "+pieceName);
movePiece(pieceName);
}
}
private void undoMove()
{

if(white==false)
{
System.out.println("last turn was white's");
white=true;
black=false;
if(undoMoveValid==false)
{
undoMoveValid=true;
}
}
else{
System.out.println("last turn was black's");
white=false;
black=true;
if(undoMoveValid==true)
{
undoMoveValid=false;
}
}
JButton tile1=tiles[undoMove.row1][undoMove.column1];
JButton tile2=tiles[undoMove.row2][undoMove.column2];

undoMoveUpdateBoard(tile1,tile2);

if(undoMove.castling)
{
if(undoMove.row1==7 && undoMove.column1==4)
{
undoMove.name1="";
undoMove.name2="whiteRook";
//white king castling
if(undoMove.row2==7 && undoMove.column2==6)
{
System.out.println("white king undo king's side castling");
tile1=tiles[7][7];
tile2=tiles[7][5];
}
else
{
System.out.println("white king undo king's side castling");
tile1=tiles[7][0];
tile2=tiles[7][3];
}
}
else if(undoMove.row1==0 && undoMove.column1==4)
{
undoMove.name1="";
undoMove.name2="blackRook";
//black king castling
if(undoMove.row2==0 && undoMove.column2==6)
{
System.out.println("black king undo king's side castling");
tile1=tiles[0][7];
tile2=tiles[0][5];
}
else
{
System.out.println("black king undo king's side castling");
tile1=tiles[0][0];
tile2=tiles[0][3];
}
}
undoMoveUpdateBoard(tile2,tile1);
}//undo castling part ends here

}

private void undoMoveUpdateBoard(JButton tile1,JButton tile2)
{
tile1.removeAll();
tile1.setActionCommand("");
tile1.repaint();
tile1.revalidate();
tile2.removeAll();
tile2.setActionCommand("");
tile2.repaint();
tile2.revalidate();

ImageIcon pieceIcon1=getPieceIconByName(undoMove.name1);
ImageIcon pieceIcon2=null;
if(undoMove.name2.equals("")==false)
{
pieceIcon2=getPieceIconByName(undoMove.name2);
}
//tile1.setBackground(undoMove.tileColor1);
//tile2.setBackground(undoMove.tileColor2);
tile1.setLayout(new BorderLayout());
tile1.add(new JLabel(pieceIcon1));
tile1.setActionCommand(undoMove.name1);
if(pieceIcon2!=null)
{
tile2.setLayout(new BorderLayout());
tile2.setActionCommand(undoMove.name2);
tile2.add(new JLabel(pieceIcon2));
}
tile1.repaint();
tile1.revalidate();
tile2.repaint();
tile2.revalidate();
}

private ImageIcon getPieceIconByName(String iconName)
{
ImageIcon pieceIcon=null;
if(iconName.equals("blackPawn"))
{
pieceIcon=this.blackPawnIcon;
}else if(iconName.equals("whitePawn"))
{
pieceIcon=this.whitePawnIcon;
}else if(iconName.equals("blackRook"))
{
pieceIcon=this.blackRookIcon;
}else if(iconName.equals("whiteRook"))
{
pieceIcon=this.whiteRookIcon;
}else if(iconName.equals("blackBishop"))
{
pieceIcon=this.blackBishopIcon;
}else if(iconName.equals("whiteBishop"))
{
pieceIcon=this.whiteBishopIcon;
}else if(iconName.equals("blackKnight"))
{
pieceIcon=this.blackKnightIcon;
}else if(iconName.equals("whiteKnight"))
{
pieceIcon=this.whiteKnightIcon;
}else if(iconName.equals("blackQueen"))
{
pieceIcon=this.blackQueenIcon;
}else if(iconName.equals("whiteQueen"))
{
pieceIcon=this.whiteQueenIcon;
}else if(iconName.equals("blackKing"))
{
pieceIcon=this.blackKingIcon;
}else if(iconName.equals("whiteKing"))
{
pieceIcon=this.whiteKingIcon;
}
return pieceIcon;
}
private void movePiece(String sourceIconName)
{
Color sourceTileColor=this.sourceTile.getBackground();
Color targetTileColor=this.targetTile.getBackground();
this.sourceTile.removeAll();
this.sourceTile.setActionCommand("");
this.sourceTile.revalidate();
this.sourceTile.repaint();
this.sourceTile.setBackground(sourceTileColor);
this.targetTile.removeAll();
this.targetTile.setActionCommand("");
this.targetTile.revalidate();
this.targetTile.repaint();
this.targetTile.setBackground(targetTileColor);
ImageIcon pieceIcon=null;
String pieceName="";
if(sourceIconName.equals("blackPawn"))
{
pieceIcon=this.blackPawnIcon;
pieceName="blackPawn";
}else if(sourceIconName.equals("whitePawn"))
{
pieceIcon=this.whitePawnIcon;
pieceName="whitePawn";
}else if(sourceIconName.equals("blackRook"))
{
pieceIcon=this.blackRookIcon;
pieceName="blackRook";
}else if(sourceIconName.equals("whiteRook"))
{
pieceIcon=this.whiteRookIcon;
pieceName="whiteRook";
}else if(sourceIconName.equals("blackBishop"))
{
pieceIcon=this.blackBishopIcon;
pieceName="blackBishop";
}else if(sourceIconName.equals("whiteBishop"))
{
pieceIcon=this.whiteBishopIcon;
pieceName="whiteBishop";
}else if(sourceIconName.equals("blackKnight"))
{
pieceIcon=this.blackKnightIcon;
pieceName="blackKnight";
}else if(sourceIconName.equals("whiteKnight"))
{
pieceIcon=this.whiteKnightIcon;
pieceName="whiteKnight";
}else if(sourceIconName.equals("blackQueen"))
{
pieceIcon=this.blackQueenIcon;
pieceName="blackQueen";
}else if(sourceIconName.equals("whiteQueen"))
{
pieceIcon=this.whiteQueenIcon;
pieceName="whiteQueen";
}else if(sourceIconName.equals("blackKing"))
{
pieceIcon=this.blackKingIcon;
pieceName="blackKing";
}else if(sourceIconName.equals("whiteKing"))
{
pieceIcon=this.whiteKingIcon;
pieceName="whiteKing";
}

targetTile.setLayout(new BorderLayout());
targetTile.setActionCommand(pieceName);
targetTile.add(new JLabel(pieceIcon));
targetTile.setEnabled(false);
targetTile.setEnabled(true);
}
private boolean validMovement(String sourceIconName)
{
/*
if(sourceIconName.equals("blackKing") || sourceIconName.equals("whiteKing"))
{
KingCastling kingCastling=null;
if(sourceIconName.equals("whiteKing"))
{
kingCastling=whiteKingCastling;
}else if(sourceIconName.equals("blackKing"))
{
kingCastling=blackKingCastling;
}
return KingMoveValidator.validateMove(tiles,startRowIndex,startColumnIndex,destinationRowIndex,destinationColumnIndex,kingCastling);
//king validation part ends here
}else
if(sourceIconName.equals("whiteQueen") || sourceIconName.equals("blackQueen"))
{
return QueenMoveValidator.validateMove(startRowIndex,startColumnIndex,destinationRowIndex,destinationColumnIndex,tiles);
}else
if(sourceIconName.equals("whiteKnight") || sourceIconName.equals("blackKnight"))
{
return KnightMoveValidator.validateMove(startRowIndex,startColumnIndex,destinationRowIndex,destinationColumnIndex);
}else
if(sourceIconName.equals("whiteBishop") || sourceIconName.equals("blackBishop"))
{
return BishopMoveValidator.validateMove(startRowIndex,startColumnIndex,destinationRowIndex,destinationColumnIndex,tiles);
//Bishop validation ends here
}else
if(sourceIconName.equals("whiteRook") || sourceIconName.equals("blackRook"))
{
return RookMoveValidator.validateMove(startRowIndex,startColumnIndex,destinationRowIndex,destinationColumnIndex,tiles);
}else 
if(sourceIconName.equals("blackPawn") || sourceIconName.equals("whitePawn"))
{
return PawnMoveValidator.validateMove(startRowIndex,startColumnIndex,destinationRowIndex,destinationColumnIndex,tiles);
}
*/
return true;
}
private void promotePawn(String color)
{
String promoteTo[]={""};
ImageIcon image[]={null};
JPanel panel=new JPanel();
panel.setLayout(new GridLayout(4,1,5,0));
panel.setBackground(new Color(240,240,240));
JButton whiteQueen=new JButton(whiteQueenIcon);
whiteQueen.setBackground(Color.WHITE);
whiteQueen.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,2));
whiteQueen.setPreferredSize(new Dimension(80,50));

JButton whiteBishop=new JButton(whiteBishopIcon);
whiteBishop.setBackground(Color.WHITE);
whiteBishop.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,2));
whiteBishop.setPreferredSize(new Dimension(80,50));

JButton whiteRook=new JButton(whiteRookIcon);
whiteRook.setBackground(Color.WHITE);
whiteRook.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,2));
whiteRook.setPreferredSize(new Dimension(80,50));

JButton whiteKnight=new JButton(whiteKnightIcon);
whiteKnight.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,2));
whiteKnight.setBackground(Color.WHITE);
whiteKnight.setPreferredSize(new Dimension(80,50));

whiteQueen.addActionListener((ev)->{
promoteTo[0]="whiteQueen";
image[0]=whiteQueenIcon;
});

whiteBishop.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent ev)
{
promoteTo[0]="whiteBishop";
image[0]=whiteBishopIcon;
}
});

whiteRook.addActionListener((ev)->{
promoteTo[0]="whiteRook";
image[0]=whiteRookIcon;
});
whiteKnight.addActionListener((ev)->{
promoteTo[0]="whiteKnight";
image[0]=whiteKnightIcon;
});

JButton blackQueen=new JButton(blackQueenIcon);
blackQueen.setBackground(Color.WHITE);
blackQueen.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,2));
blackQueen.setPreferredSize(new Dimension(80,50));

JButton blackBishop=new JButton(blackBishopIcon);
blackBishop.setBackground(Color.WHITE);
blackBishop.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,2));
blackBishop.setPreferredSize(new Dimension(80,50));

JButton blackRook=new JButton(blackRookIcon);
blackRook.setBackground(Color.WHITE);
blackRook.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,2));
blackRook.setPreferredSize(new Dimension(80,50));

JButton blackKnight=new JButton(blackKnightIcon);
blackKnight.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,2));
blackKnight.setBackground(Color.WHITE);
blackKnight.setPreferredSize(new Dimension(80,50));

blackQueen.addActionListener((ev)->{
promoteTo[0]="blackQueen";
image[0]=blackQueenIcon;
});

blackBishop.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent ev)
{
promoteTo[0]="blackBishop";
image[0]=blackBishopIcon;
}
});

blackRook.addActionListener((ev)->{
promoteTo[0]="blackRook";
image[0]=blackRookIcon;
});
blackKnight.addActionListener((ev)->{
promoteTo[0]="blackKnight";
image[0]=blackKnightIcon;
});

	
if(color.equals("white"))
{
panel.add(whiteQueen);
panel.add(whiteBishop);
panel.add(whiteRook);
panel.add(whiteKnight);
promoteTo[0]="whiteQueen";
image[0]=whiteQueenIcon;
}
else
{
panel.add(blackQueen);
panel.add(blackBishop);
panel.add(blackRook);
panel.add(blackKnight);
promoteTo[0]="blackQueen";
image[0]=blackQueenIcon;
}


JOptionPane.showMessageDialog(this,panel,"Choose a piece",JOptionPane.PLAIN_MESSAGE);
JButton pawn=this.tiles[this.destinationRowIndex][this.destinationColumnIndex];
pawn.removeAll();
pawn.setActionCommand("");
pawn.repaint();
pawn.revalidate();
pawn.setActionCommand(promoteTo[0]);
pawn.setLayout(new BorderLayout());
pawn.add(new JLabel(image[0]));
pawn.repaint();
pawn.revalidate();
}

private class ButtonPanel extends JPanel implements ActionListener
{
private JButton undoButton;
private JButton doneButton;
ButtonPanel()
{
ImageIcon undoIcon=new ImageIcon(this.getClass().getResource("/icons/undo_32.png"));
undoButton=new JButton(undoIcon);
undoButton.setForeground(Color.BLACK);
undoButton.setBackground(new Color(220,240,220));
ImageIcon doneIcon=new ImageIcon(this.getClass().getResource("/icons/done_32.png"));
doneButton=new JButton(doneIcon);
undoButton.setBounds(100+200,20,50,50);
doneButton.setBackground(new Color(220,240,220));
doneButton.setForeground(Color.BLACK);

setBorder(BorderFactory.createEmptyBorder(20,10,20,10));
setBackground(new Color(240,240,240));
setLayout(new GridLayout(8,1));
undoButton.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
doneButton.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
add(new JLabel("       "));
add(new JLabel("       "));
add(new JLabel("       "));
add(new JLabel("       "));
add(new JLabel("       "));
add(new JLabel(" 	"));
add(undoButton);
add(doneButton);
undoButton.addActionListener(this);
doneButton.addActionListener(this);
}
public void setUNDOEnable(boolean enable)
{
undoButton.setEnabled(enable);
}
public void actionPerformed(ActionEvent ev)
{
if(ev.getSource()==undoButton)
{
Chess.this.undoMove();
undoButton.setEnabled(false);
}else if(ev.getSource()==doneButton)
{
System.out.println("DONE");
}
}
}
}
