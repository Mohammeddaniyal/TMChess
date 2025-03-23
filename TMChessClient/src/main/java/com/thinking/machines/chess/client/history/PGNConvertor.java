package com.thinking.machines.chess.client.history;
import com.thinking.machines.chess.common.Move;
public class PGNConvertor
{
public static String convertMoveToPGN(Move move,byte isCapture)
{
StringBuilder pgn=new StringBuilder();

if(move.castlingType==1 || move.castlingType==3) return "O-O";
else if(move.castlingType==1 || move.castlingType==3) return "O-O-O";

//determine piece notation
char pieceChar=getPieceChar(move.piece);

if(pieceChar!=' ') pgn.append(pieceChar);

//handling ambiguity case
//file ambiguity
if(move.ambiguityType==1)
{
pgn.append(move.fromX+1);
}else if(move.ambiguityType==2){
//rank ambiguity
pgn.append(('a'+move.fromY));
}

//capture notation
if(isCapture==1)
{
pgn.append('x');
}

//destination part notation
pgn.append(('a'+move.toX));
pgn.append(8-move.fromY);

// promotion notation
if(move.pawnPromotionTo!=0 && move.pawnPromotionTo!=1 && move.pawnPromotionTo!=-1)
{
pgn.append('=').append(getPieceChar(move.pawnPromotionTo));
}

// for checkmate
if(move.isLastMove==1)
{
pgn.append('#');
}
return pgn.toString();
}

private static char getPieceChar(byte piece)
{
piece=(byte)((piece<0)?piece*-1:piece);
return switch(piece){
case 1 -> ' ';//pawn no letter
case 2 -> 'N';
case 3 -> 'B';
case 4 -> 'R';
case 5 -> 'Q';
case 6 -> 'K';
default -> ' ';
};
}
}