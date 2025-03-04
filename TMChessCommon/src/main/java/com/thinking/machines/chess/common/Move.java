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
}