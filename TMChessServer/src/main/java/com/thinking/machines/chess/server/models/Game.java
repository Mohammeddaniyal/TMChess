package com.thinking.machines.chess.server.models;
import com.thinking.machines.chess.common.Move;
import java.util.*;
public class Game implements java.io.Serializable
{
public String id;
public String player1;
public String player2;
public byte[][] board;
public byte[][] possibleMoves;
public byte activePlayer;
public List<Move> moves;
public KingCastling whiteKingCastling;
public KingCastling blackKingCastling;
}
