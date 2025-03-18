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
}
}
byte isStalemate=0;

for(byte []_index:playerPiecesIndexes)
{
}

return isStalemate;
}
}