package com.thinking.machines.chess.client.history;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
public class MoveHistoryPanel extends JPanel
{
private MoveHistoryTableModel moveHistoryTableModel;
private JTable moveHistoryTable;
private JScrollPane moveHistoryTableScrollPane;
public MoveHistoryPanel(byte firstPlayerColor)
{
moveHistoryTableModel=new MoveHistoryTableModel(firstPlayerColor);
moveHistoryTable=new JTable(moveHistoryTableModel);
moveHistoryTableScrollPane=new JScrollPane(moveHistoryTable,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
setLayout(new BorderLayout());
add(moveHistoryTableScrollPane);
setVisible(true);
}
public void addBlackMove(String move)
{
moveHistoryTableModel.addBlackMove(move);
}
public void addWhiteMove(String move)
{
moveHistoryTableModel.addWhiteMove(move);
}

}
