package com.thinking.machines.chess.server.handler;
import com.thinking.machines.chess.server.models.*;
import com.thinking.machines.chess.server.logic.*;
import com.thinking.machines.chess.common.Move;
import com.thinking.machines.chess.common.MoveResponse;
public class MoveHandler
{
private static void updateCastlingStatus(KingCastling kingCastling,Game game,byte piece,byte fromX,byte fromY)
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
else if((fromX==7 && fromY==7) || (fromX==0 && fromY==7))
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
			//passing zero 	means no need to check for stalemate part
byte [][]possibleMoves=CheckmateDetector.getPossibleMoves(game.board,fromX,fromY,kingCastling,(byte)0);
game.possibleMoves=possibleMoves;
return possibleMoves;
}
public static MoveResponse validateMove(Game game,Move move)
{

MoveResponse moveResponse=new MoveResponse();
byte fromX=move.fromX;
byte fromY=move.fromY;
byte toX=move.toX;
byte toY=move.toY;
byte validMove=game.possibleMoves[toX][toY];

if(validMove==0) 
{
moveResponse.isValid=0;
moveResponse.castlingType=0;
moveResponse.pawnPromotionTo=0;
return moveResponse;
}

moveResponse.isValid=1;
moveResponse.castlingType=0;
moveResponse. pawnPromotionTo=0;

KingCastling kingCastling;
if(game.board[fromX][fromY]==6)//white king
{
kingCastling=game.whiteKingCastling;
}else
{
kingCastling=game.blackKingCastling;
}


//if move is valid update the current board state
byte sourcePiece=game.board[fromX][fromY];

//check for pawn promotion
if(sourcePiece==1 && toX==0) //white pawn
{
//now change the value of sourcePiece
if(move.pawnPromotionTo==0)
{
moveResponse.isValid=0;
return moveResponse;
}
sourcePiece=move.pawnPromotionTo;
moveResponse.pawnPromotionTo=move.pawnPromotionTo;
System.out.println("PAWN PROMOTING TO : "+sourcePiece);
}else if(sourcePiece==-1 && toX==7)//black pawn
{
if(move.pawnPromotionTo==0)
{
moveResponse.isValid=0;
return moveResponse;
}
sourcePiece=move. pawnPromotionTo;
moveResponse. pawnPromotionTo=move. pawnPromotionTo;
System.out.println("PAWN PROMOTING TO : "+sourcePiece);
}
game.board[fromX][fromY]=0;
game.board[toX][toY]=sourcePiece;


//now check if the move was castling
if(move.castlingType==0)//no castling happened
{
//do nothing
}else if(kingCastling.checkCastling)//checking if castling is possible or not
{
kingCastling.checkCastling=false;
kingCastling.kingMoved=true;
if(move.castlingType==1)//white king side castling 
{
fromX=7;
fromY=7;
toX=7;
toY=5;
moveResponse.castlingType=1;
kingCastling.rightRookMoved=true;
}else if(move.castlingType==2)//white queen side castling
{
fromX=7;
toX=7;
fromY=0;
toY=3;
moveResponse.castlingType=2;
kingCastling.leftRookMoved=true;
}else if(move.castlingType==3)//black king side castling
{
fromX=0;
toX=0;
fromY=7;
toY=5;
moveResponse.castlingType=3;
kingCastling.rightRookMoved=true;
}else if(move.castlingType==4)//black queen side castling
{
fromX=0;
toX=0;
fromY=0;
toY=3;
moveResponse.castlingType=4;
kingCastling.leftRookMoved=true;
}//now update the board state
move.castlingType=moveResponse.castlingType;
sourcePiece=game.board[fromX][fromY];
game.board[fromX][fromY]=0;
game.board[toX][toY]=sourcePiece;
return moveResponse;
}

//in case of castling no need to check this because we know that king and either rook is moved
//so i handled that on castlingCheck 
//after completion of move track whether castling can still be allowed or not 
//to track whether king or either rook moved or not
if(kingCastling.checkCastling)//only when castling is possible
{
//remove game after testing

updateCastlingStatus(kingCastling,game,sourcePiece,fromX,fromY);
}

return moveResponse;
}
public static byte detectCheckmate(Game game)
{
byte opponent=(byte)((game.activePlayer==1)?0:1);
System.out.println("Active player : "+game.activePlayer);
System.out.println("Opponent : "+opponent);
boolean isCheckmate=CheckmateDetector.detectCheckmate(game.board,opponent);
System.out.println("Is checkmate : "+isCheckmate);
return (byte)(isCheckmate?1:0);
}
}
