package com.thinking.machines.chess.common;
import java.util.*;
public class Move implements java.io.Serializable
{
public byte player;
public byte piece;
public byte fromX;
public byte fromY;
public byte toX;
public byte toY;
public byte isLastMove;
public byte castlingType=0;
//0 represents no castling
//1 white kingside
//2 white queenside castling
//3 black king side
//4 black queen side
public byte pawnPromotionTo=0;
//0 represents no promotion
//1 promote to Queen
//2 promote to Rook
//3 promote to Bishop
//4 promote to Knight
}
