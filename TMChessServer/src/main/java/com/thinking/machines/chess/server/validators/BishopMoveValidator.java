package com.thinking.machines.chess.server.validators;
import javax.swing.*;
public class BishopMoveValidator
{
public static boolean validateMove(int startRowIndex,int startColumnIndex,int destinationRowIndex,int destinationColumnIndex,byte [][]board)
{
//restricting vertical and horizontal movement of the (bishop)
if((startRowIndex==destinationRowIndex && startColumnIndex!=destinationColumnIndex) || (startRowIndex!=destinationRowIndex && startColumnIndex==destinationColumnIndex)) return false; 
//validating movement of bishop diagonally on the same tile
int d1=startRowIndex-destinationRowIndex;
int d2=startColumnIndex-destinationColumnIndex;
if(d1<0) d1=d1*(-1);
if(d2<0) d2=d2*(-1);
if(d1!=d2)
{
return false;
}
//validating path blocker
byte tile;
int e,f;
if(destinationRowIndex<startRowIndex && destinationColumnIndex<startColumnIndex)
{
for(e=startRowIndex-1,f=startColumnIndex-1;e>destinationRowIndex;e--,f--)
{
tile=board[e][f];
if(tile!=0) 
{
return false;
}
}
//path blocker for top-left ends here
}else //path blocker for top-right
if(destinationRowIndex<startRowIndex && startColumnIndex<destinationColumnIndex)
{
for(e=startRowIndex-1,f=startColumnIndex+1;e>destinationRowIndex;e--,f++)
{
tile=board[e][f];
if(tile!=0)
{
return false;
}
}
//path blocker for top-right ends here
}else//path blocker for bottom-left
if(startRowIndex<destinationRowIndex && destinationColumnIndex<startColumnIndex)
{
for(e=startRowIndex+1,f=startColumnIndex-1;e<destinationRowIndex;e++,f--)
{
tile=board[e][f];
if(tile!=0) 
{
return false;
}
}
//path blocker for bottom-left ends here
}else//path blocker for bottom-right
if(startRowIndex<destinationRowIndex && startColumnIndex<destinationColumnIndex)
{
for(e=startRowIndex+1,f=startColumnIndex+1;e<destinationRowIndex;e++,f++)
{
tile=board[e][f];
if(tile!=0)
{
return false;
}
}
//path blocker for bottom-right ends here
}
return true;
}
}
