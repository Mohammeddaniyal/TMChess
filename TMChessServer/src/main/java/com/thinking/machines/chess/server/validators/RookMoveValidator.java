package com.thinking.machines.chess.server.validators;
import javax.swing.*;
public class RookMoveValidator
{
public static boolean validateMove(int startRowIndex,int startColumnIndex,int destinationRowIndex,int destinationColumnIndex,JButton[][] tiles)
{
if(startRowIndex!=destinationRowIndex && startColumnIndex!=destinationColumnIndex) return false;//restricting rook diagonal movement
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
return true;
}
}
