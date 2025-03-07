package com.thinking.machines.chess.server.handler;
import com.thinking.machines.chess.server.models.*;
import com.thinking.machines.chess.server.logic.*;
import com.thinking.machines.chess.common.Move;
public class MoveHandler
{
private void updateCastlingStatus(KingCastling kingCastling,byte piece)
{
if(piece==6 || piece==-6)//king
{
kingCastling.checkCastling=false;
kingCastling.kingMoved=true;
System.out.println("Castling became false and also it affected in (white)"+game.whiteKingCastling.checkCastling);
System.out.println("Castling became false and also it affected in (black)"+game.blackKingCastling.checkCastling);
}
else if(piece==4 || piece==-4)//rook
{
if((fromX==7 && fromY==0) || (fromX==0 && fromY==0))
{
kingCastling.leftRookMoved=true;
}
else if((fromX=7 && fromY==7) || (fromX==0 && fromY==7))
{
kingCastling.rightRookMoved=true;
}
}
if(kingCastling.leftRookMoved==true && kingCastling.rightRookMoved==true)
{
kingCastling.checkCastling=false;
System.out.println("Castling became false and also it affected in (white)"+game.whiteKingCastling.checkCastling);
System.out.println("Castling became false and also it affected in (black)"+game.blackKingCastling.checkCastling);
}
}
public static byte[][] getPossibleMoves(Game game,byte fromX,byte fromY)
{
KingCastling kingCastling;
if(game.board[fromX][fromY]==6)//white king
{
kingCastling=game.whiteKingCastling;
}else
{
kingCastling=game.blackKingCastling;
}
return CheckmateDetector.getPossibleMoves(game.board,fromX,fromY,kingCastling);
}
public static byte validateMove(Game game,Move move)
{
byte board[][]=game.board;
byte fromX=move.fromX;
byte fromY=move.fromY;
byte toX=move.toX;
byte toY=move.toY;

byte sourcePiece=board[fromX][fromY];

//after completion of move track whether castling can still be allowed or not 
//to track whether king or either rook moved or not
KingCastling kingCastling;
if(game.board[fromX][fromY]==6)//white king
{
kingCastling=game.whiteKingCastling;
}else
{
kingCastling=game.blackKingCastling;
}
if(game.kingCastling.checkCastling)//only when castling is possible
{
updateCastlingStatus(kingCastling,sourcePiece,fromX,fromY);
}
return 1;
}
}
