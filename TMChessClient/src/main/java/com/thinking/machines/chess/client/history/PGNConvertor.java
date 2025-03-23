package com.thinking.machines.chess.client.history;
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
pgn.append(new String(move.fromX+1));
}else if(move.ambiguityType==2){
//rank ambiguity
pgn.append(('a'+move.fromY));
}

//capture notation
if(isCapture==1)
{
pgn.append('x');
}

}

private char getPieceChar(byte piece)
{
byte piece=(byte)((piece<0)?piece*-1:piece);
return switch(piece){
case 1 -> ' ',//pawn no letter
case 2 -> 'N',
case 3 -> 'B',
case 4 -> 'R',
case 5 -> 'Q',
case 6 -> 'K'
};
}
}