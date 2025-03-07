package com.thinking.machines.chess.common;
public class MoveResponse implements java.io.Serializable
{
public byte isValid;
public byte castlingType=0;
//0 represents no castling
//1 white kingside
//2 white queenside castling
//3 black king side
//4 black queen side
}