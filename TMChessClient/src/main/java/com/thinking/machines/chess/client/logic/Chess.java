package com.thinking.machines.chess.client.logic;
import com.thinking.machines.nframework.client.*;
import com.thinking.machines.chess.client.history.*;
import com.thinking.machines.chess.client.ChessUI;
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
private ChessUI chessUI;
String username;
private MoveHistoryPanel moveHistoryPanel;
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
private javax.swing.Timer isOpponentLeftTheGameTimer;
private boolean canIPlay;
private byte firstTurnOfPlayerColor;
private void canIPlay()
{
try
{
this.canIPlay=(boolean)client.execute("/TMChessServer/canIPlay",gameInit.gameId,gameInit.playerColor);
if(canIPlay) firstTurnOfPlayerColor=gameInit.playerColor;
else firstTurnOfPlayerColor=(gameInit.playerColor==1)?0:1;
System.out.println(canIPlay);
if(!canIPlay)
{
//a little delay so the ui gets updated before showing the dialog
try{Thread.sleep(100);}catch(Exception e){}
JOptionPane.showMessageDialog(Chess.this,"Opponent is first");
getOpponentMoveTimer.start();
}
else
{
//a little delay so the ui gets updated before showing the dialog
try{Thread.sleep(100);}catch(Exception e){}
JOptionPane.showMessageDialog(Chess.this,"You are first "+username);
}
}catch(Throwable t)
{
JOptionPane.showMessageDialog(Chess.this,t.getMessage());
}

}
public void StopisOpponentLeftTheGameTimer()
{
isOpponentLeftTheGameTimer.stop();
}
private void addActionListeners()
{

isOpponentLeftTheGameTimer=new javax.swing.Timer(1000,ev->{
try
{
byte isOpponentLeftTheGame=(byte)client.execute("/TMChessServer/isOpponentLeftTheGame",gameInit.gameId,username);
if(isOpponentLeftTheGame==-1 || isOpponentLeftTheGame==0) return; // did n't left the game
if(isOpponentLeftTheGame==1)//means left the game
{
((javax.swing.Timer)ev.getSource()).stop();
//also left the game
try
{
client.execute("/TMChessServer/leftGame",username);
}catch(Throwable t)
{
JOptionPane.showMessageDialog(Chess.this,t.getMessage());
}

//show a dialog saying that opponent has left the game and you win by default
JOptionPane.showMessageDialog(this,"Opponent has left the game\nYou Won","Game Over",JOptionPane.INFORMATION_MESSAGE);
SwingUtilities.invokeLater(()->{
chessUI.resetFrame();
});
}
}catch(Throwable t)
{
JOptionPane.showMessageDialog(this,t.getMessage());
}
});

//start this timer isOpponentLeftTheGameTimer

isOpponentLeftTheGameTimer.start(); //it will run until either the game ends or someone left the game


getOpponentMoveTimer=new javax.swing.Timer(1000,ev->{
try
{
Move move=(Move)client.execute("/TMChessServer/getOpponentMove",gameInit.gameId,gameInit.playerColor);
if(move==null)
{
try{Thread.sleep(100);}catch(Exception e){}
return;
}

((javax.swing.Timer)ev.getSource()).stop();
String pieceName=getPieceName(move.piece);

SwingUtilities.invokeLater(()->{
this.sourceTile=tiles[move.fromX][move.fromY];
this.targetTile=tiles[move.toX][move.toY];
this.startRowIndex=move.fromX;
this.startColumnIndex=move.fromY;
this.destinationRowIndex=move.toX;
this.destinationColumnIndex=move.toY;
updateBoardState(move);
movePiece(pieceName);
if(move.pawnPromotionTo!=0)
{
String promoteToName=getPieceName(move.pawnPromotionTo);
ImageIcon promoteToIcon=getPieceIconByName(promoteToName); 
PawnPromotionDialog pawnPromotionDialog=new PawnPromotionDialog(promoteToName,promoteToIcon);
pawnPromotionDialog.promotePawn();
}
System.out.println("Last move : "+move.isLastMove);
if(move.isLastMove==1)
{
isOpponentLeftTheGameTimer.stop();
reset();

JOptionPane.showMessageDialog(this,"You Lost!","Game over",JOptionPane.INFORMATION_MESSAGE);
SwingUtilities.invokeLater(()->{
chessUI.resetFrame();
});
try{client.execute("/TMChessServer/leftGame",username);}catch(Throwable t){JOptionPane.showMessageDialog(this,t.getMessage());}
return;
}
});
if(move.isLastMove==1) return;

reset();
canIPlay=true;

//now its player turn to play now time to check whther this player have any legal moves left or not

try
{
byte isStalemate=(byte)client.execute("/TMChessServer/isStalemate",gameInit.gameId,gameInit.playerColor);
if(isStalemate==1)
{
isOpponentLeftTheGameTimer.stop();
reset();
JOptionPane.showMessageDialog(this,"No legal move left to make","Draw",JOptionPane.INFORMATION_MESSAGE);
SwingUtilities.invokeLater(()->{
chessUI.resetFrame();
});
try{client.execute("/TMChessServer/leftGame",username);}catch(Throwable t){JOptionPane.showMessageDialog(this,t.getMessage());}
return;
}
}catch(Throwable t)
{
JOptionPane.showMessageDialog(this,t.getMessage());
}

}catch(Throwable t)
{
JOptionPane.showMessageDialog(Chess.this,t.getMessage());
}
});
}
private String getPieceName(byte piece)
{
String pieceName=(piece>0)?"white":"black";
if(piece<0)piece*=-1;
pieceName=pieceName+pieceNamesMap.get((byte)piece);
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
public Chess(ChessUI chessUI,NFrameworkClient client,GameInit gameInit,String username)
{
addActionListeners();
populateDataStructures();
this.client=client;
this.chessUI=chessUI;
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
//add(buttonPanel,BorderLayout.EAST);
setLocation(x,y);
setVisible(true);
canIPlay();
moveHistoryPanel=new MoveHistoryPanel(firstTurnOfPlayerColor);
add(moveHistoryPanel,BorderLayout.EAST);
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
if(gameInit.playerColor==1 && pieceColor.equals("black")) 
{
System.out.println("you're white,Not black");
return;
}
else if(this.gameInit.playerColor==0 && pieceColor.equals("white")) 
{
System.out.println("you're white,Not white");
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
if(possibleMoves[e][f]==0) continue;
validTile=tiles[e][f];
//validTile.setBorder(BorderFactory.createDashedBorder(Color.RED,30,10));
Color c;
c=Color.GREEN;
validTile.setBorder(BorderFactory.createLineBorder(c,3));
}
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


//place a call to server side method to get validation of the move
MoveResponse moveResponse=null;
byte validMove=0;
byte castlingType=0;
byte pawnPromotionTo=0;
byte isLastMove=0;
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

PawnPromotionDialog pawnPromotionDialog=null;

//now to check pawn promotion case
//pawn promotion case
if(move.piece==1 && this.destinationRowIndex==0 && possibleMoves[move.toX][move.toY]==1)
{
pawnPromotionDialog=new PawnPromotionDialog("white");
move.pawnPromotionTo=pawnPromotionDialog.getSelectedPiece();
}
else if(move.piece==-1 && this.destinationRowIndex==7 && possibleMoves[move.toX][move.toY]==1)
{
pawnPromotionDialog=new PawnPromotionDialog("black");
move.pawnPromotionTo=(byte)pawnPromotionDialog.getSelectedPiece();
}


try
{
moveResponse=(MoveResponse)client.execute("/TMChessServer/submitMove",move,gameInit.gameId);
}catch(Throwable t)
{
System.out.println("SUBMIT MOVE EXCEPTION");
JOptionPane.showMessageDialog(Chess.this,t.getMessage());
}
validMove=moveResponse.isValid;
if(validMove==0)
{
this.sourceTile.setBorder(UIManager.getBorder("Button.border"));
targetTile.setEnabled(false);
targetTile.setEnabled(true);
reset();
return;
}
castlingType=moveResponse.castlingType;
pawnPromotionTo=moveResponse.pawnPromotionTo;
isLastMove=moveResponse.isLastMove;
this.sourceTile.setBorder(BorderFactory.createLineBorder(Color.GREEN,3));
movePiece(sourceIconName);
move.castlingType=castlingType;
move.pawnPromotionTo=pawnPromotionTo;
//if castling case yes, then updateBoardState will handle the byte[][] board
//as well as the updation of GUI

updateBoardState(move);
if(pawnPromotionTo!=0 && pawnPromotionDialog!=null) pawnPromotionDialog.promotePawn();
this.sourceTile.setBorder(UIManager.getBorder("Button.border"));


//checking if this was the last move
if(isLastMove==1)
{
isOpponentLeftTheGameTimer.stop();
reset();
JOptionPane.showMessageDialog(this,"You won!","Game over",JOptionPane.INFORMATION_MESSAGE);
SwingUtilities.invokeLater(()->{
chessUI.resetFrame();
});
try{client.execute("/TMChessServer/leftGame",username);}catch(Throwable t){JOptionPane.showMessageDialog(this,t.getMessage());}
return;
}

//since move is submitted and assuming after submitting process upto now
//the opponent recieved the move 
//and checked for the whether the opponent left with any legal move or not
//so we will also check whether stalemate occurs or not

try
{
byte stalemateOccur=(byte)client.execute("/TMChessServer/stalemateOccur",gameInit.gameId);
if(stalemateOccur==1)
{
isOpponentLeftTheGameTimer.stop();
reset();
JOptionPane.showMessageDialog(this,"Oppoent left with No legal move left to make","Draw",JOptionPane.INFORMATION_MESSAGE);
SwingUtilities.invokeLater(()->{
chessUI.resetFrame();
});
try{client.execute("/TMChessServer/leftGame",username);}catch(Throwable t){JOptionPane.showMessageDialog(this,t.getMessage());}
return;
}
}catch(Throwable t)
{
JOptionPane.showMessageDialog(this,t.getMessage());
}



canIPlay=false;
getOpponentMoveTimer.start();


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
byte pawnPromotionTo=move.pawnPromotionTo;
if(pawnPromotionTo!=0)
{
gameInit.board[fromX][fromY]=0;
gameInit.board[toX][toY]=pawnPromotionTo;
return;
}
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
System.out.println("KING CASTLING rook : "+piece);
gameInit.board[fromX][fromY]=0;
gameInit.board[toX][toY]=piece;
this.startRowIndex=fromX;
this.startColumnIndex=fromY;
this.destinationRowIndex=toX;
this.destinationColumnIndex=toY;
this.sourceTile=tiles[startRowIndex][startColumnIndex];
this.targetTile=tiles[destinationRowIndex][destinationColumnIndex];
String pieceName=((castlingType>2)?"black":"white")+"Rook";
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

private class PawnPromotionDialog
{
String promoteToName;
ImageIcon promoteToIcon;
byte selectedPiece;
PawnPromotionDialog(String promoteToName,ImageIcon promoteToIcon)
{
this.promoteToName=promoteToName;
this.promoteToIcon=promoteToIcon;
}
PawnPromotionDialog(String color)
{
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
promoteToName="whiteQueen";
promoteToIcon=whiteQueenIcon;
selectedPiece=5;
});

whiteBishop.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent ev)
{
promoteToName="whiteBishop";
promoteToIcon=whiteBishopIcon;
selectedPiece=3;
}
});

whiteRook.addActionListener((ev)->{
promoteToName="whiteRook";
promoteToIcon=whiteRookIcon;
selectedPiece=4;
});
whiteKnight.addActionListener((ev)->{
promoteToName="whiteKnight";
promoteToIcon=whiteKnightIcon;
selectedPiece=2;
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
promoteToName="blackQueen";
promoteToIcon=blackQueenIcon;
selectedPiece=-5;
});

blackBishop.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent ev)
{
promoteToName="blackBishop";
promoteToIcon=blackBishopIcon;
selectedPiece=-3;
}
});

blackRook.addActionListener((ev)->{
promoteToName="blackRook";
promoteToIcon=blackRookIcon;
selectedPiece=-4;
});
blackKnight.addActionListener((ev)->{
promoteToName="blackKnight";
promoteToIcon=blackKnightIcon;
selectedPiece=-2;
});


if(color.equals("white"))
{
panel.add(whiteQueen);
panel.add(whiteBishop);
panel.add(whiteRook);
panel.add(whiteKnight);
//default value
promoteToName="whiteQueen";
promoteToIcon=whiteQueenIcon;
selectedPiece=5;
}
else
{
panel.add(blackQueen);
panel.add(blackBishop);
panel.add(blackRook);
panel.add(blackKnight);
//default value
promoteToName="blackQueen";
promoteToIcon=blackQueenIcon;
selectedPiece=-5;
}

JOptionPane.showMessageDialog(Chess.this,panel,"Choose a piece",JOptionPane.PLAIN_MESSAGE);
}

public byte getSelectedPiece()
{
return this.selectedPiece;
}

public void promotePawn()
{
JButton pawn=Chess.this.tiles[Chess.this.destinationRowIndex][Chess.this.destinationColumnIndex];
pawn.removeAll();
pawn.setActionCommand("");
pawn.repaint();
pawn.revalidate();
pawn.setActionCommand(promoteToName);
pawn.setLayout(new BorderLayout());
pawn.add(new JLabel(promoteToIcon));
pawn.repaint();
pawn.revalidate();
}

}// pawnPromotionDialog class ends



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
