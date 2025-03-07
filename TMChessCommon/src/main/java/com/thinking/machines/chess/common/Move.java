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
public byte castlingType=-1;
//-1 represents no castling
//0 for kingside castling
//1 for queenside castling
}
