package com.thinking.machines.chess.client.validators;
import javax.swing.*;
public class PawnMoveValidator
{
public static boolean validateMove(int startRowIndex,int startColumnIndex,int destinationRowIndex,int destinationColumnIndex,JButton[][] tiles)
{
String sourceIconName=tiles[startRowIndex][startColumnIndex].getActionCommand();
boolean capture=false;
if(sourceIconName.equals("blackPawn"))
{
if(destinationRowIndex<=startRowIndex)
{
// for restricting backward movement of the pawn
return false;
}

if(startRowIndex==1)//at begining allowing 2 steps
{
if(destinationRowIndex-startRowIndex==2)//2 square tile forward validation
{
//not allowing to jump from a piece if it is in path of the pawn
JButton piece=tiles[startRowIndex+1][startColumnIndex];
String pieceName=piece.getActionCommand();
if(pieceName.equals("")==false) return false;
if(tiles[startRowIndex+2][startColumnIndex].getActionCommand().equals("")==false)return false;
}
if((destinationRowIndex-startRowIndex!=2 && destinationRowIndex-startRowIndex!=1))
{
//for 2 steps at the first move of the pawn
return false;
}
//at begining allowing 2 steps ends here
}else if(startRowIndex!=1 && destinationRowIndex-startRowIndex!=1)
{
// restrictring more than one tile movement of the pawn
return false;
}
if(startRowIndex==destinationRowIndex-1 && startColumnIndex==destinationColumnIndex && tiles[startRowIndex+1][startColumnIndex].getActionCommand().equals("")==false)
{
//restricting movement if any piece is in the forward path
return false;
}
//capture  part of pawn(en passant)diagonal right or left
if( (startRowIndex+1==destinationRowIndex) && ((startColumnIndex-1==destinationColumnIndex) || (startColumnIndex+1==destinationColumnIndex)) )
{

String sourcePieceColor=tiles[startRowIndex][startColumnIndex].getActionCommand().substring(0,5);
String targetIconName=tiles[destinationRowIndex][destinationColumnIndex].getActionCommand();
String targetPieceColor="";
if(targetIconName.equals("")) return false;
targetPieceColor=targetIconName.substring(0,5);
if(sourcePieceColor.equals(targetPieceColor))
{
return false;
}
capture=true;
}
if(capture!=true && startColumnIndex!=destinationColumnIndex)
{
//restricting pawn diagonal movement other than en passant
return false;
}
//black pawn part ends here
}else if(sourceIconName.equals("whitePawn"))
{
if(startRowIndex<=destinationRowIndex)
{
// for restricting backward movement of the pawn
return false;
}
if(startRowIndex==6)//at begining allowing 2 steps
{
if(startRowIndex-destinationRowIndex==2)//2 square tile forward validation
{
//not allowing to jump from a piece if it is in path of the pawn
JButton piece=tiles[startRowIndex-1][startColumnIndex];
String pieceName=piece.getActionCommand();
//checking the path isn't blocked
if(pieceName.equals("")==false) return false;
if(tiles[startRowIndex-2][startColumnIndex].getActionCommand().equals("")==false)return false;
}
if(startRowIndex-destinationRowIndex!=2 && startRowIndex-destinationRowIndex!=1)
{
//for 2 steps at the first move of the pawn
return false;
}
//at begining allowing 2 steps ends here
}else
if(startRowIndex!=6 && startRowIndex-destinationRowIndex!=1)
{
// restrictring more than one tile movement of the pawn
return false;
}
//path blocking in forward move case of pawn
if(startRowIndex==destinationRowIndex+1 && startColumnIndex==destinationColumnIndex &&  tiles[startRowIndex-1][startColumnIndex].getActionCommand().equals("")==false)
{
return false;
}
//capture  enpassant
if( (startRowIndex-1==destinationRowIndex) && ((startColumnIndex-1==destinationColumnIndex) || (startColumnIndex+1==destinationColumnIndex)) )
{
String sourcePieceColor=tiles[startRowIndex][startColumnIndex].getActionCommand().substring(0,5);
String targetIconName=tiles[destinationRowIndex][destinationColumnIndex].getActionCommand();
String targetPieceColor="";
if(targetIconName.equals("")) return false;
targetPieceColor=targetIconName.substring(0,5);
if(sourcePieceColor.equals(targetPieceColor))
{
return false;
}
capture=true;
}
if(capture!=true && startColumnIndex!=destinationColumnIndex)
{
//not allowing diagonal movement of pawn other than en passant 
return false;
}

}//white pawn part ends here
return true;
}
}
 
