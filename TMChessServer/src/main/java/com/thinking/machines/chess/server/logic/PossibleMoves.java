package com.thinking.machines.chess.server.logic;
import com.thinking.machines.chess.server.validators.*;
import com.thinking.machines.chess.server.models.KingCastling;
import javax.swing.*;
public class PossibleMoves
{
public static boolean[][] getPossibleMoves(JButton[][] tiles,int startRowIndex,int startColumnIndex,KingCastling kingCastling)
{
boolean [][]possibleMoves=new boolean[8][8];
String pieceName;
pieceName=tiles[startRowIndex][startColumnIndex].getActionCommand();
String sourceIconName=tiles[startRowIndex][startColumnIndex].getActionCommand();
String targetIconName="";
int destinationRowIndex,destinationColumnIndex;
String sourceIconPieceColor=sourceIconName.substring(0,5);
boolean pawn=false;
if(sourceIconName.equals("blackPawn") || sourceIconName.equals("whitePawn") )
{
pawn=true;
} 
for(int e=0;e<8;e++)
{
destinationRowIndex=e;
for(int f=0;f<8;f++)
{
destinationColumnIndex=f;
targetIconName=tiles[destinationRowIndex][destinationColumnIndex].getActionCommand();
String targetIconPieceColor="";
boolean capture=false;
if(targetIconName.equals("")==false)
{
capture=true;
targetIconPieceColor=targetIconName.substring(0,5);
}
if(capture && targetIconPieceColor.equals(sourceIconPieceColor))
{
possibleMoves[destinationRowIndex][destinationColumnIndex]=false;
continue;
}
/*
if(tiles[destinationRowIndex][destinationColumnIndex].getActionCommand().equals("")==false)
{
possibleMoves[destinationRowIndex][destinationColumnIndex]=false;
continue;
}
*/
if(pieceName.equals("blackKing") || pieceName.equals("whiteKing"))
{
possibleMoves[destinationRowIndex][destinationColumnIndex]=KingMoveValidator.validateMove(tiles,startRowIndex,startColumnIndex,destinationRowIndex,destinationColumnIndex,kingCastling);
//king validation part ends here
}else
if(pieceName.equals("whiteQueen") || pieceName.equals("blackQueen"))
{
possibleMoves[destinationRowIndex][destinationColumnIndex]=QueenMoveValidator.validateMove(startRowIndex,startColumnIndex,destinationRowIndex,destinationColumnIndex,tiles);
}else
if(pieceName.equals("whiteKnight") || pieceName.equals("blackKnight"))
{
possibleMoves[destinationRowIndex][destinationColumnIndex]=KnightMoveValidator.validateMove(startRowIndex,startColumnIndex,destinationRowIndex,destinationColumnIndex);
}else
if(pieceName.equals("whiteBishop") || pieceName.equals("blackBishop"))
{
possibleMoves[destinationRowIndex][destinationColumnIndex]=BishopMoveValidator.validateMove(startRowIndex,startColumnIndex,destinationRowIndex,destinationColumnIndex,tiles);
//Bishop validation ends here
}else
if(pieceName.equals("whiteRook") || pieceName.equals("blackRook"))
{
possibleMoves[destinationRowIndex][destinationColumnIndex]=RookMoveValidator.validateMove(startRowIndex,startColumnIndex,destinationRowIndex,destinationColumnIndex,tiles);
}else 
if(pieceName.equals("blackPawn") || pieceName.equals("whitePawn"))
{
possibleMoves[destinationRowIndex][destinationColumnIndex]=PawnMoveValidator.validateMove(startRowIndex,startColumnIndex,destinationRowIndex,destinationColumnIndex,tiles);
}
}//inner loop ends
}//outer loop ends
return possibleMoves;
}
}
