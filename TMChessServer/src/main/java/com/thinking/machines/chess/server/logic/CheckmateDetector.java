package com.thinking.machines.chess.server.logic;
import com.thinking.machines.chess.server.validators.*;
import com.thinking.machines.chess.server.models.*;
import javax.swing.*;
import java.util.*;
public class CheckmateDetector
{
public static boolean[][] getPossibleMoves(byte[][] board,int startRowIndex,int startColumnIndex,KingCastling kingCastling)
{
byte [][]possibleMoves=PossibleMoves.getPossibleMoves(board,startRowIndex,startColumnIndex,kingCastling);
PossibleMovesIndex pieceValidIndex;
ArrayList<PossibleMovesIndex> piecesValidIndexes=new ArrayList<>();
for(int e=0;e<8;e++)
{
for(int f=0;f<8;f++)
{
if(possibleMoves[e][f]==1)//can move to this index
{
pieceValidIndex=new PossibleMovesIndex();
pieceValidIndex.row=e;
pieceValidIndex.column=f;
pieceValidIndex.safe=true;
piecesValidIndexes.add(pieceValidIndex);
}
}
}//piece valid indexes loop ends here

if(piecesValidIndexes.size()==0) return possibleMoves;
byte [][]validPossibleMoves=possibleMoves;
//find index of king
byte sourcePiece=board[startRowIndex][startColumnIndex];
//+ve represents white pieces and vice versa 
// 6 represent king
byte kingPiece=(sourcePiece>0)?6:-6;

int kingRowIndex=0;
int kingColumnIndex=0;
boolean pieceIsKing=false;
if(sourcePiece==kingPiece)
//pieceName.equals("whiteKing") || pieceName.equals("blackKing"))
{
kingRowIndex=startRowIndex;
kingColumnIndex=startColumnIndex;
pieceIsKing=true;
}
else
{
for(int e=0;e<8;e++)
{
for(int f=0;f<8;f++)
{
if(board[e][f]==kingPiece) 
{
kingRowIndex=e;
kingColumnIndex=f;
break;
}
}
}
}//else ends

byte [][]dummyBoard=new byte[8][8];
byte dummyTile;
byte tile;
for(int e=0;e<8;e++)
{
for(int f=0;f<8;f++)
{
tile=board[e][f];
if(e==startRowIndex && f==startColumnIndex)
{
dummyTile=0;
}
else
{
dummyTile=tile;
}
dummyBoard[e][f]=dummyTile;
}
}//creating dummy tiles(D.S) ends here

int row;
int column;
ArrayList<PieceMoves> capturingPiecesMovesList;
byte pieceColor=(sourcePiece>0)?1:0;
for(PossibleMovesIndex pmi:piecesValidIndexes)
{
row=pmi.row;
column=pmi.column;
dummyTile=dummyBoard[row][column];
dummyBoard[row][column]=sourcePiece;
if(pieceIsKing==false)capturingPiecesMovesList=isPieceInDanger(dummyBoard,pieceColor,kingRowIndex,kingColumnIndex,false);
else capturingPiecesMovesList=isPieceInDanger(dummyBoard,pieceColor,row,column,false);
for(PieceMoves pieceMoves:capturingPiecesMovesList)
{
possibleMoves=pieceMoves.possibleMoves;
if(pieceIsKing==true)
{
if(possibleMoves[row][column]==1)
{
validPossibleMoves[row][column]=0;
break;
}
}
else
{
if(possibleMoves[kingRowIndex][kingColumnIndex]==1)
{
validPossibleMoves[row][column]=0;
break;
}
}//else ends 
}//for loop ends(possibleMovesCapture)
//put the piece on it's original position
//or reset the dummyBoard as before after simulating
dummyBoard[row][column]=dummyTile;
}
return validPossibleMoves;
}


public static ArrayList<PieceMoves> isPieceInDanger(byte [][]board,byte pieceColor,int rowIndex,int columnIndex,boolean includeAllValidPieces)
{
byte opponentPiece;
JButton opponentPiece;
String opponentPieceName;
String opponentPieceColor;
PieceMoves pieceMoves;
ArrayList<PieceMoves> piecesMoves;
piecesMoves=new ArrayList<>();
byte[][] possibleMoves;
KingCastling kingCastling=new KingCastling();
kingCastling.checkCastling=false;
for(int e=0;e<8;e++)
{
for(int f=0;f<8;f++)
{
opponentPiece=board[e][f];
if(opponentPiece==0)
{
continue;
}
opponentPieceColor=opponentPieceName.substring(0,5);

if((opponentPiece<0 && pieceColor==0) || (opponentPiece>0 && pieceColor==1))
{
//same piece , then skip
 continue;
}
possibleMoves=PossibleMoves.getPossibleMoves(board,e,f,kingCastling);
if(possibleMoves[rowIndex][columnIndex]==1)
{
pieceMoves=new PieceMoves();
pieceMoves.possibleMoves=possibleMoves;
pieceMoves.rowIndex=e;
pieceMoves.columnIndex=f;
piecesMoves.add(pieceMoves);
if(includeAllValidPieces==false)
{
return piecesMoves;
}
}
}
}
return piecesMoves;
}
public static boolean detectCheckmate(JButton[][] tiles,String color)
{
KingCastling kingCastling=new KingCastling();
kingCastling.checkCastling=false;
String k=color+"King";
int kingRowIndex=0;
int kingColumnIndex=0;
for(int e=0;e<8;e++)
{
for(int f=0;f<8;f++)
{
if(tiles[e][f].getActionCommand().equals(k))
{
kingRowIndex=e;
kingColumnIndex=f;
break;
}
}
}

JButton king=tiles[kingRowIndex][kingColumnIndex];
String kingName=king.getActionCommand();
String kingColor=kingName.substring(0,5);
ArrayList<PieceMoves> piecesMoves=isPieceInDanger(tiles,null,kingRowIndex,kingColumnIndex,true);
if(piecesMoves.size()==0) 
{
//System.out.println(kingName+" not in danger");
return false;
}

boolean [][]kingPossibleMoves=PossibleMoves.getPossibleMoves(tiles,kingRowIndex,kingColumnIndex,kingCastling);
PossibleMovesIndex kingValidIndex;
ArrayList<PossibleMovesIndex> kingValidIndexes=new ArrayList<>();
for(int e=0;e<8;e++)
{
for(int f=0;f<8;f++)
{
if(kingPossibleMoves[e][f]==true)
{
kingValidIndex=new PossibleMovesIndex();
//System.out.println("King can move to tile : "+e+"/"+f);
kingValidIndex.row=e;
kingValidIndex.column=f;
kingValidIndex.safe=true;
kingValidIndexes.add(kingValidIndex);
}
}
}//king valid indexes loop ends here



if(kingValidIndexes.size()==0 && piecesMoves.size()>1)
{
//if king dont have any valid move and king is in threat by more than 1 opponent piece
//System.out.println("Checkmate detected no safe tile and being attacked by more than 1 pieces");
return true;
}


PieceMoves attackingPieceMoves=piecesMoves.get(0);
boolean[][] possibleMoves;
//creating a dummy tiles
//without the king which is in danger
JButton[][] dummyTiles=new JButton[8][8];
JButton dummyTile;
JButton tile;
String tilePieceName;
for(int e=0;e<8;e++)
{
for(int f=0;f<8;f++)
{
tile=tiles[e][f];
tilePieceName=tile.getActionCommand();
dummyTile=new JButton();
dummyTiles[e][f]=dummyTile;
if(e==kingRowIndex && f==kingColumnIndex)
{
dummyTile.setActionCommand("");
}
else
{
dummyTile.setActionCommand(tilePieceName);
}
}
}//creating dummy tiles(D.S) ends here
int kingValidIndexesCount=kingValidIndexes.size();
int row,column;
ArrayList<PieceMoves> capturingPiecesMovesList;
ArrayList<PossibleMovesIndex> kingSafeIndexes=new ArrayList<>();
for(PossibleMovesIndex kvi:kingValidIndexes)
{
row=kvi.row;
column=kvi.column;
dummyTile=dummyTiles[row][column];
String s=dummyTile.getActionCommand();
dummyTile.setActionCommand(kingName);
capturingPiecesMovesList=isPieceInDanger(dummyTiles,null,row,column,true);
//System.out.println("Simulating king at : "+row+"/"+column+" size of capturing pieces : "+capturingPiecesMovesList.size());
for(PieceMoves pieceMoves:capturingPiecesMovesList)
{
possibleMoves=pieceMoves.possibleMoves;
//System.out.println("Attacking piece name : "+tiles[row][column].getActionCommand()+row+"/"+column);
//System.out.println("value at 2/6 "+possibleMoves[2][6]);
if(possibleMoves[row][column]==true)
{
//System.out.println("This tile is not safe for king : "+row+"/"+column);
kvi.safe=false;
break;
}
}
if(kvi.safe==true)
{
kingSafeIndexes.add(kvi);
}
dummyTile.setActionCommand(s);
s="";
}
boolean safeTile=true;
if(kingSafeIndexes.size()==0)
{
safeTile=false;
for(PossibleMovesIndex kvi:kingValidIndexes)
{
//System.out.println(kvi.row+"/"+kvi.column);
}
//System.out.println("Checkmate detected(no safe tile) to move");
}
else
{
//System.out.println("Safe tile available");
//System.out.println("King safe indexes");
for(PossibleMovesIndex kvi:kingSafeIndexes)
{
//System.out.println(kvi.row+"/"+kvi.column);
}
return false;
}
row=attackingPieceMoves.rowIndex;
column=attackingPieceMoves.columnIndex;
possibleMoves=attackingPieceMoves.possibleMoves;
String opponentPieceName=tiles[row][column].getActionCommand();
//System.out.println("Attacking piece name : "+opponentPieceName);
int attackingPieceRowIndex=row;
int attackingPieceColumnIndex=column;
boolean captureOpponentPiece=false;
boolean blockOpponentPiece=false;
boolean knightPiece=false;
PossibleMovesIndex attackingPiecePossibleMovesIndex;
ArrayList<PossibleMovesIndex> attackingPiecePossibleMovesIndexes=new ArrayList<>();
if(opponentPieceName.equals("blackKnight") || opponentPieceName.equals("whiteKinght"))
{
//System.out.println("Attacking piece is knight");
knightPiece=true;
}

else
{
for(int e=0;e<8;e++)
{
for(int f=0;f<8;f++)
{
if(possibleMoves[e][f]==true)
{
attackingPiecePossibleMovesIndex=new PossibleMovesIndex();
attackingPiecePossibleMovesIndex.row=e;
attackingPiecePossibleMovesIndex.column=f;
attackingPiecePossibleMovesIndexes.add(attackingPiecePossibleMovesIndex);
}
}//inner loop
}//outer loop
}//attacking piece (possible moves indexes)
int row1=0;
int column1=0;
for(int e=0;e<8;e++)
{
for(int f=0;f<8;f++)
{
tile=tiles[e][f];
tilePieceName=tile.getActionCommand();
if(tilePieceName.equals("")) continue;
if(tilePieceName.substring(0,5).equals(kingColor)==false) continue;//in case of oppoent piece
if(tilePieceName.equals(k)) continue;//k contains name of king like(blackKing / whiteKing)
possibleMoves=PossibleMoves.getPossibleMoves(tiles,e,f,kingCastling);
if(possibleMoves[attackingPieceRowIndex][attackingPieceColumnIndex]==true)
{
// the which is threating the king can be captured
//System.out.println("Threating piece can be captured by : "+tilePieceName);
captureOpponentPiece=true;
break;
}
//for blocking
if(knightPiece==false)
{
//creating dummyTiles
dummyTiles=generateDummyTiles(tiles,tilePieceName);
ArrayList<PossibleMovesIndex> friendlyPiecePossibleMovesIndexes=getPossibleMovesIndexesList(possibleMoves);
for(PossibleMovesIndex pmi:friendlyPiecePossibleMovesIndexes)
{
row1=pmi.row;
column1=pmi.column;
dummyTiles[row1][column1].setActionCommand(tilePieceName);

boolean[][] opponentPiecePossibleMoves=PossibleMoves.getPossibleMoves(dummyTiles,attackingPieceRowIndex,attackingPieceColumnIndex,kingCastling);
if(opponentPiecePossibleMoves[kingRowIndex][kingColumnIndex]==false)
{
//the piece is blocked
//System.out.println("the piece is blocked by : "+tilePieceName);
blockOpponentPiece=true;
break; 
}	
/*
ArrayList<PieceMoves> blockingPiece=isPieceInDanger(dummyTiles,kingRowIndex,kingColumnIndex);
if(blockingPiece.size()==0)
{
//the piece is blocked
blockOpponentPiece=true;
break; 
}
*/
dummyTiles[row1][column1].setActionCommand("");
}
if(blockOpponentPiece==true) break;//done
}
}
}// part of blocking or capturing end's here	
if(captureOpponentPiece==false && blockOpponentPiece==false ) 
{
//System.out.println("cannot be captured nor be blocked");
return true;
}
/*
if(blockOpponentPiece==false)
{
//System.out.println("cannot be blocked");
return true;
}
*/
//System.out.println("NO THREAT");
return false;
}
private static JButton[][] generateDummyTiles(JButton[][] tiles,String pieceNameNotToInclude)
{
JButton[][] dummyTiles=new JButton[8][8];
JButton dummyTile;
JButton tile;
String tilePieceName;
for(int e=0;e<8;e++)
{
for(int f=0;f<8;f++)
{
tile=tiles[e][f];
tilePieceName=tile.getActionCommand();
dummyTile=new JButton();
dummyTiles[e][f]=dummyTile;
if(tilePieceName.equals(pieceNameNotToInclude))
{
dummyTile.setActionCommand("");
}
else
{
dummyTile.setActionCommand(tilePieceName);
}
}
}//creating dummy tiles(D.S) ends here
return dummyTiles;
}
private static ArrayList<PossibleMovesIndex> getPossibleMovesIndexesList(boolean [][]possibleMoves)
{
ArrayList<PossibleMovesIndex> possibleMovesIndexes=new ArrayList<>();
PossibleMovesIndex possibleMovesIndex;
for(int e=0;e<8;e++)
{
for(int f=0;f<8;f++)
{
if(possibleMoves[e][f]==true)
{
possibleMovesIndex=new PossibleMovesIndex();
possibleMovesIndex.row=e;
possibleMovesIndex.column=f;
possibleMovesIndexes.add(possibleMovesIndex);
}
}//inner loop
}//outer loop
return possibleMovesIndexes;
}
}
