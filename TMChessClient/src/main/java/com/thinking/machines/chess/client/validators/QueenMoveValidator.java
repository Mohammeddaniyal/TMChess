package com.thinking.machines.chess.client.validators;
import javax.swing.*;
public class QueenMoveValidator
{
private QueenMoveValidator(){};
public static boolean validateMove(int startRowIndex,int startColumnIndex,int destinationRowIndex,int destinationColumnIndex,JButton[][] tiles)
{
//for vertical and horizontal movement starts here
JButton tile;
String pieceName;
if(startColumnIndex==destinationColumnIndex)//vertical movement
{
if(startRowIndex<destinationRowIndex)	
{
for(int e=startRowIndex+1;e<destinationRowIndex;e++)
{
tile=tiles[e][startColumnIndex];
pieceName=tile.getActionCommand();
if(pieceName.equals("")==false) return false;
}
}else
{
for(int e=startRowIndex-1;e>destinationRowIndex;e--)
{
tile=tiles[e][startColumnIndex];
pieceName=tile.getActionCommand();
if(pieceName.equals("")==false) return false;
}
}
}else if(startRowIndex==destinationRowIndex)//horizontal movement
{
if(startColumnIndex<destinationColumnIndex)
{
for(int f=startColumnIndex+1;f<destinationColumnIndex;f++)
{
tile=tiles[startRowIndex][f];
pieceName=tile.getActionCommand();
if(pieceName.equals("")==false) return false;
}
}else
{
for(int f=startColumnIndex-1;f>destinationColumnIndex;f--)
{
tile=tiles[startRowIndex][f];
pieceName=tile.getActionCommand();
if(pieceName.equals("")==false) return false;
}
}
}
//for vertical and horizontal movement ends here
//for diagonal movement starts here
else
{
int d1=startRowIndex-destinationRowIndex;
int d2=startColumnIndex-destinationColumnIndex;
if(d1<0) d1=d1*(-1);
if(d2<0) d2=d2*(-1);
if(d1!=d2)
{
return false;
}
//validating path blocker

//path blocker for top-left
int e,f;
if(destinationRowIndex<startRowIndex && destinationColumnIndex<startColumnIndex)
{
for(e=startRowIndex-1,f=startColumnIndex-1;e>destinationRowIndex;e--,f--)
{
tile=tiles[e][f];
if(tile.getActionCommand().equals("")==false) 
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
tile=tiles[e][f];
if(tile.getActionCommand().equals("")==false)
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
tile=tiles[e][f];
if(tile.getActionCommand().equals("")==false) 
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
tile=tiles[e][f];
if(tile.getActionCommand().equals("")==false) 
{
return false;
}
}
//path blocker for bottom-right ends here
}
}
//for diagonal movement ends here
//movement validation part for Queen ends here
return true;
}
}
