package com.thinking.machines.chess.client.history;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
public class MoveHistoryTableModel extends AbstractTableModel
{
private String[] title={"Move no.","White","Black"};
private List<String> whiteMoves;
private List<String> blackMoves;
MoveHistoryTableModel(byte firstTurnOfPlayer)
{
if(firstTurnOfPlayer==0)
{
title[1]="Black";
title[2]="White";
}
whiteMoves=new LinkedList<>();
blackMoves=new LinkedList<>();
}
public int getRowCount()
{
return Math.max(whiteMoves.size(),blackMoves.size());
}
public int getColumnCount()
{
return this.title.length;
}
public String getColumnName(int column)
{
return title[column];
}
public Object getValueAt(int row,int column)
{
if(column==0) return row+1;
if(column==1)
{ 
String data=(title[1].equals("White"))this.whiteMoves.get(row)?:this.blackMoves.get(row);
if(data==null) return "";
return data;
}
if(column==2)
{ 
String data=(title[1].equals("White"))this.whiteMoves.get(row)?:this.blackMoves.get(row);
if(data==null) return "";
return data;
}
}
public boolean isCellEditable(int row,int column)
{
return false;
}
public Class getColumnClass(int column)
{
return String.class;
}
public void addBlackMove(String move)
{
this.blackMoves.add(move);
fireTableDataChanged();
}
public void addWhiteMove(String move)
{
this.blackMoves.add(move);
fireTableDataChanged();
}
}/class ends
