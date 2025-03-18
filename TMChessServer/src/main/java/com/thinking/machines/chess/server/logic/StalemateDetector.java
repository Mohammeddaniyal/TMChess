package com.thinking.machines.chess.server.logic;
import java.util.*;
import com.thinking.machines.chess.server.models.*;
public class StalemateDetector
{
public static byte detectStalemate(Game game,byte player)
{
List<byte[]> playerPiecesIndexes;
playerPiecesIndexes=new LinkedList<>();
byte piece;
byte index[];
for(byte e=0;e<8;e++)
{
for(byte f=0;f<8;f++)
{
piece=game.board[e][f];
//not player piece case
if((piece>0 && player==0) || (piece<0 && player==1)) continue;
index=new byte[2];
index[0]=e;
index[1]=f;
playerPiecesIndexes.add(index);
}
}
byte isStalemate=0;
KingCastling kingCastling;
if(player==1)//white king
{
kingCastling=game.whiteKingCastling;
}else
{
kingCastling=game.blackKingCastling;
}
//since it's stalemate we dont need to check for castling scenario
kingCastling.checkCastling=false;
for(byte []_index:playerPiecesIndexes)
{
byte fromX=_index[0];
byte fromY=_index[1];
//passing 1 as called from stalemate detection
byte [][]possibleMoves=CheckmateDetector.getPossibleMoves(game.board,fromX,fromY,kingCastling,(byte)1);
if(possibleMoves==null) isStalemate=1;
else 
{
//since there's even a single legal move avilable so it means no stalemate
isStalemate=0;
break;
}
}

return isStalemate;
}
}