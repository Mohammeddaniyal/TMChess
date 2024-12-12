package com.thinking.machines.chess.client;
import com.thinking.machines.nframework.client.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;
public class ChessUI extends JFrame
{
private String username;
private JTable availableMembersList;
private JScrollPane availableMembersListScrollPane;
private AvailableMembersListModel availableMembersListModel;
private javax.swing.Timer timer;
private Container container;
private NFrameworkClient client;
public ChessUI(String username)
{
super("Member : "+username);
this.username=username;
initComponents();
setAppearence();
addListeners();
Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
int width=500;
int height=400;
setSize(width,height);
setLocation(d.width/2-width/2,d.height/2-height/2);
}
private void initComponents()
{
this.availableMembersListModel=new AvailableMembersListModel();
this.availableMembersList=new JTable(availableMembersListModel);
this.availableMembersList.getColumn(" ").setCellRenderer(new AvailableMembersListButtonRenderer());
this.availableMembersList.getColumn(" ").setCellEditor(new AvailableMembersListButtonCellEditor());
this.availableMembersListScrollPane=new JScrollPane(this.availableMembersList,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
JPanel p1=new JPanel();
p1.setLayout(new BorderLayout());
p1.add(new JLabel("Members"),BorderLayout.NORTH);
this.client=new NFrameworkClient();
p1.add(availableMembersList);
container=getContentPane();
container.setLayout(new BorderLayout());
container.add(p1,BorderLayout.EAST);
}
private void setAppearence()
{
//do nothing right now
}
private void addListeners()
{
timer=new javax.swing.Timer(1000,new ActionListener(){
public void actionPerformed(ActionEvent ev)
{
timer.stop();
try
{
java.util.List<String> members=(java.util.List<String>)client.execute("/TMChessServer/getMembers",username);
availableMembersListModel.setMembers(members);
}catch(Throwable t)
{
JOptionPane.showMessageDialog(ChessUI.this,t.toString());
}
timer.start();
}
});
addWindowListener(new WindowAdapter(){
public void windowClosing(WindowEvent e)
{
try
{
client.execute("/TMChessServer/logout",username);
}catch(Throwable t)
{
JOptionPane.showMessageDialog(ChessUI.this,t.toString());
}
System.exit(0);
}
});

//now all setup, let us start the timer
timer.start();
}//add listeners ends
public void showUI()
{
this.setVisible(true);
}

// inner classes starts here //
class AvailableMembersListModel extends AbstractTableModel
{
private java.util.List<String> members;
private String[] title={"Members"," "};
private JButton inviteButton;
AvailableMembersListModel()
{
members=new LinkedList<>();
inviteButton=new JButton("Invite");
}
public int getRowCount()
{
return members.size();
}
public int getColumnCount()
{
return title.length;
}
public String getColumnName(int column)
{
return title[column];
}
public Object getValueAt(int row,int column)
{
if(column==0) return this.members.get(row);
return inviteButton;
}
public boolean isCellEditable(int row,int column)
{
if(column==1) return true;
return false;
}
public Class getColumnClass(int column)
{
if(column==0) return String.class;
return JButton.class;
}
public void setMembers(java.util.List<String> members)
{
this.members=members;
fireTableDataChanged();
}
}
class AvailableMembersListButtonRenderer implements TableCellRenderer
{
public Component getTableCellRendererComponent(JTable table,Object value,boolean a,boolean b,int row,int column)
{
return (JButton)value;
}
}
class AvailableMembersListButtonCellEditor extends DefaultCellEditor
{
private JButton button;
private boolean isClicked;
private int row,col;
AvailableMembersListButtonCellEditor()
{
super(new JCheckBox());//because of policy
button=new JButton();
button.setOpaque(true);
button.addActionListener(ev->{
fireEditingStopped();
});
}
public Component getTableCellEditorComponent(JTable table,Object value,boolean a,int row,int col)
{
this.row=row;
this.col=col;
button.setForeground(Color.black);
button.setBackground(UIManager.getColor("Button.background"));
button.setText("Invite");
isClicked=true;
return button;
}
public Object getCellEditorValue()
{
return "";
}
public boolean stopCellEditing()
{
isClicked=false;
return super.stopCellEditing();
}
public void fireEditingStopped()
{
//do whatever is required
super.fireEditingStopped();
}
}



}//outer class ends