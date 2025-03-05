package com.thinking.machines.chess.server.validators;
import javax.swing.*;
public class RookMoveValidator
{
public static boolean validateMove(int startRowIndex,int startColumnIndex,int destinationRowIndex,int destinationColumnIndex,byte[][] board)
{
if(startRowIndex!=destinationRowIndex && startColumnIndex!=destinationColumnIndex) return false;//restricting rook diagonal movement
byte tile;
if(startColumnIndex==destinationColumnIndex)//vertical movement
{
if(startRowIndex<destinationRowIndex)	
{
for(int e=startRowIndex+1;e<destinationRowIndex;e++)
{
tile=board[e][startColumnIndex];
if(tile!=0) return false;
}
}else
{
for(int e=startRowIndex-1;e>destinationRowIndex;e--)
{
tile=board[e][startColumnIndex];
if(tile!=0) return false;
}
}
}else if(startRowIndex==destinationRowIndex)//horizontal movement
{
if(startColumnIndex<destinationColumnIndex)
{
for(int f=startColumnIndex+1;f<destinationColumnIndex;f++)
{
tile=board[startRowIndex][f];
if(tile!=0) return false;
}
}else
{
for(int f=startColumnIndex-1;f>destinationColumnIndex;f--)
{
tile=board[startRowIndex][f];
if(tile!=0) return false;
}
}
}
return true;
}
}
