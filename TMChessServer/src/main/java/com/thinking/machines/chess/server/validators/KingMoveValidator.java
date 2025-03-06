package com.thinking.machines.chess.server.validators;
import com.thinking.machines.chess.server.logic.*;
import com.thinking.machines.chess.server.models.*;
import javax.swing.*;
import java.util.*;
public class KingMoveValidator
{
private KingMoveValidator(){};
public static byte validateMove(byte[][] board,int startRowIndex,int startColumnIndex,int destinationRowIndex,int destinationColumnIndex,KingCastling kingCastling)
{
//castling part
if(kingCastling.checkCastling==true)
{
byte kingPiece=board[startRowIndex][startColumnIndex];
if(kingPiece==-6)//black king
{
//king's side castling move arrived
if(startRowIndex==0 && startColumnIndex==4 && destinationRowIndex==0 && destinationColumnIndex==6)
{
System.out.println("black (right)rook moved : "+kingCastling.rightRookMoved);
if(kingCastling.kingMoved==true || kingCastling.rightRookMoved==true)
{
return 0;
}
//checking if tiles are empty
if(board[0][5]!=0 || tiles[0][6]!=0 )
{
return 0;
}
//checking is king is in checkmate or not
//one of the rule of castling the king cannot be in check
//passing 0 means color is black
ArrayList<PieceMoves> piecesMoves=CheckmateDetector.isPieceInDanger(board,(byte)0,startRowIndex,startColumnIndex,false);
if(piecesMoves.size()!=0) 
{
return 0;
}
// Another rule of castling that the destination tile of king should not be in check
//now to check is tile f8 and g8 are not in any threat

//for tile f8
piecesMoves=CheckmateDetector.isPieceInDanger(board,(byte)0,0,5,false);
if(piecesMoves.size()!=0)
{
return 0;
}
//for tile g8
piecesMoves=CheckmateDetector.isPieceInDanger(board,(byte)0,0,6,false);
if(piecesMoves.size()!=0)
{
return 0;
}
return 1;
}// king's side castling ends here

//queen's side castling move arrived
if(startRowIndex==0 && startColumnIndex==4 && destinationRowIndex==0 && destinationColumnIndex==2)
{
if(kingCastling.kingMoved==true || kingCastling.leftRookMoved==true)
{
return 0;
}
//checking if tiles are empty
if(board[0][1]!=0 || board[0][2]!=0 || board[0][3]!=0)
{
return 0;
}
//checking is king is in checkmate or not
ArrayList<PieceMoves> piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"black",startRowIndex,startColumnIndex,false);
if(piecesMoves.size()!=0) 
{
return 0;
}
//now to check is tile c1 and d1 are not in any threat

piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"black",0,2,false);
if(piecesMoves.size()!=0)
{
return 0;
}
//for tile g8
piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"black",0,3,false);
if(piecesMoves.size()!=0)
{
return 0;
}
return 1;
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
return 0;
}
//checking if tiles are empty
if(tiles[7][5].getActionCommand().equals("")==false || tiles[7][6].getActionCommand().equals("")==false )
{
return 0;
}
//checking is king is in checkmate or not
ArrayList<PieceMoves> piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"white",startRowIndex,startColumnIndex,false);
if(piecesMoves.size()!=0) 
{
return 0;
}
//not to check is tile f8 and g8 are not in any threat

piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"white",7,5,false);
if(piecesMoves.size()!=0)
{
return 0;
}
//for tile g8
piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"white",7,6,false);
if(piecesMoves.size()!=0)
{
return 0;
}
return 1;
}// king's side castling ends here

//queen's side castling move arrived
if(startRowIndex==7 && startColumnIndex==4 && destinationRowIndex==7 && destinationColumnIndex==2)
{
if(kingCastling.kingMoved==true || kingCastling.leftRookMoved==true)
{
return 0;
}
//checking if tiles are empty
if(tiles[7][1].getActionCommand().equals("")==false || tiles[7][2].getActionCommand().equals("")==false || tiles[7][3].getActionCommand().equals("")==false )
{
return 0;
}
//checking is king is in checkmate or not
ArrayList<PieceMoves> piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"white",startRowIndex,startColumnIndex,false);
if(piecesMoves.size()!=0) 
{
return 0;
}
//now to check is tile c1 and d1 are not in any threat

piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"white",7,2,false);
if(piecesMoves.size()!=0)
{
return 0;
}
//for tile g8
piecesMoves=CheckmateDetector.isPieceInDanger(tiles,"white",7,3,false);
if(piecesMoves.size()!=0)
{
return 0;
}
return 1;
}// queen's side castling ends here


}//castling of white king part ends here
}//castling part ends here


int d=0;
if(startColumnIndex==destinationColumnIndex)//moving one step veritcally
{
d=(startRowIndex<destinationRowIndex)?destinationRowIndex-startRowIndex:startRowIndex-destinationRowIndex;
if(d!=1) return 0;
}else if(startRowIndex==destinationRowIndex)//moving one step horizontally
{
d=(startColumnIndex<destinationColumnIndex)?destinationColumnIndex-startColumnIndex:startColumnIndex-destinationColumnIndex;
if(d!=1) return 0;
}
else//moving one step diagonally
{
int d1=(startRowIndex<destinationRowIndex)?destinationRowIndex-startRowIndex:startRowIndex-destinationRowIndex;
int d2=(startColumnIndex<destinationColumnIndex)?destinationColumnIndex-startColumnIndex:startColumnIndex-destinationColumnIndex;
if(d1!=1 || d2!=1) 
{
return 0;
}
}
return 1;
}
}//class ends here (KingValidator)
