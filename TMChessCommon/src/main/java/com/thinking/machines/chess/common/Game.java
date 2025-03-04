package com.thinking.machines.chess.common;
import java.util.*;
public class Game implements java.io.Serializable
{
public String id;
public String player1;
public String player2;
public byte[][] board;
public byte activePlayer;
public List<Move> moves;
}