package com.thinking.machines.chess.client.validators;
import com.thinking.machines.chess.client.model.*;
import com.thinking.machines.chess.client.logic.*;
import javax.swing.*;
import java.util.*;
public class KingMoveValidator
{
private KingMoveValidator(){};
public static boolean validateMove(JButton [][]tiles,int startRowIndex,int startColumnIndex,int destinationRowIndex,int destinationColumnIndex,KingCastling kingCastling)
{
//castling part
if(kingCastling.checkCastling==true)
{
String kingName=tiles[startRowIndex][startColumnIndex].getActionCommand();
if(kingName.equals("blackKing"))
{

//king's side castling move arrived
if(startRowIndex==0 && startColumnIndex==4 && destinationRowIndex==0 && destinationColumnIndex==6)
{
System.out.println("black (right)rook moved : "+kingCastling.rightRookMoved);
if(kingCastling.kingMoved==true || kingCastling.rightRookMoved==true)
{
return false;
}
//checking if tiles are empty
if(tiles[0][5].getActionCommand().equals("")==false || tiles[0][6].getActionCommand().equals("")==false )
{
return false;
}
//checking is king is in checkmate or not
ArrayList<PieceMoves> piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"black",startRowIndex,startColumnIndex,false);
if(piecesMoves.size()!=0) 
{
return false;
}
//not to check is tile f8 and g8 are not in any threat

piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"black",0,5,false);
if(piecesMoves.size()!=0)
{
return false;
}
//for tile g8
piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"black",0,6,false);
if(piecesMoves.size()!=0)
{
return false;
}
return true;
}// king's side castling ends here

//queen's side castling move arrived
if(startRowIndex==0 && startColumnIndex==4 && destinationRowIndex==0 && destinationColumnIndex==2)
{
if(kingCastling.kingMoved==true || kingCastling.leftRookMoved==true)
{
return false;
}
//checking if tiles are empty
if(tiles[0][1].getActionCommand().equals("")==false || tiles[0][2].getActionCommand().equals("")==false || tiles[0][3].getActionCommand().equals("")==false )
{
return false;
}
//checking is king is in checkmate or not
ArrayList<PieceMoves> piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"black",startRowIndex,startColumnIndex,false);
if(piecesMoves.size()!=0) 
{
return false;
}
//now to check is tile c1 and d1 are not in any threat

piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"black",0,2,false);
if(piecesMoves.size()!=0)
{
return false;
}
//for tile g8
piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"black",0,3,false);
if(piecesMoves.size()!=0)
{
return false;
}
return true;
}// queen's side castling ends here
}//castling of black king part ends here



if(kingName.equals("whiteKing"))
{
//king's side castling move arrived
if(startRowIndex==7 && startColumnIndex==4 && destinationRowIndex==7 && destinationColumnIndex==6)
{
System.out.println("white (right)rook moved : "+kingCastling.rightRookMoved);
if(kingCastling.kingMoved==true || kingCastling.rightRookMoved==true)
{
return false;
}
//checking if tiles are empty
if(tiles[7][5].getActionCommand().equals("")==false || tiles[7][6].getActionCommand().equals("")==false )
{
return false;
}
//checking is king is in checkmate or not
ArrayList<PieceMoves> piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"white",startRowIndex,startColumnIndex,false);
if(piecesMoves.size()!=0) 
{
return false;
}
//not to check is tile f8 and g8 are not in any threat

piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"white",7,5,false);
if(piecesMoves.size()!=0)
{
return false;
}
//for tile g8
piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"white",7,6,false);
if(piecesMoves.size()!=0)
{
return false;
}
return true;
}// king's side castling ends here

//queen's side castling move arrived
if(startRowIndex==7 && startColumnIndex==4 && destinationRowIndex==7 && destinationColumnIndex==2)
{
if(kingCastling.kingMoved==true || kingCastling.leftRookMoved==true)
{
return false;
}
//checking if tiles are empty
if(tiles[7][1].getActionCommand().equals("")==false || tiles[7][2].getActionCommand().equals("")==false || tiles[7][3].getActionCommand().equals("")==false )
{
return false;
}
//checking is king is in checkmate or not
ArrayList<PieceMoves> piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"white",startRowIndex,startColumnIndex,false);
if(piecesMoves.size()!=0) 
{
return false;
}
//now to check is tile c1 and d1 are not in any threat

piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"white",7,2,false);
if(piecesMoves.size()!=0)
{
return false;
}
//for tile g8
piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"white",7,3,false);
if(piecesMoves.size()!=0)
{
return false;
}
return true;
}// queen's side castling ends here


}//castling of white king part ends here
}//castling part ends here


int d=0;
if(startColumnIndex==destinationColumnIndex)//moving one step veritcally
{
d=(startRowIndex<destinationRowIndex)?destinationRowIndex-startRowIndex:startRowIndex-destinationRowIndex;
if(d!=1) return false;
}else if(startRowIndex==destinationRowIndex)//moving one step horizontally
{
d=(startColumnIndex<destinationColumnIndex)?destinationColumnIndex-startColumnIndex:startColumnIndex-destinationColumnIndex;
if(d!=1) return false;
}
else//moving one step diagonally
{
int d1=(startRowIndex<destinationRowIndex)?destinationRowIndex-startRowIndex:startRowIndex-destinationRowIndex;
int d2=(startColumnIndex<destinationColumnIndex)?destinationColumnIndex-startColumnIndex:startColumnIndex-destinationColumnIndex;
if(d1!=1 || d2!=1) 
{
return false;
}
}
return true;
}
}//class ends here (KingValidator)
