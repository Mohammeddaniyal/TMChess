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
private void sendInvitation(String toUsername)
{
System.out.println("sending invitation to : "+toUsername);
try
{
client.execute("/TMChessServer/inviteUser",username,toUsername);
JOptionPane.showMessageDialog(this,"Invitation for game sent to : "+toUsername);
}catch(Throwable t)
{
JOptionPane.showMessageDialog(this,t.toString());
}
}
// inner classes starts here //
class AvailableMembersListModel extends AbstractTableModel
{
private java.util.List<String> members;
private java.util.List<JButton> inviteButtons;
private String[] title={"Members"," "};
private boolean awaitingInvitationReply;
AvailableMembersListModel()
{
awaitingInvitationReply=false;
members=new LinkedList<>();
inviteButtons=new LinkedList<>();
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
return this.inviteButtons.get(row);
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
if(awaitingInvitationReply) return;
this.members=members;
this.inviteButtons.clear();
for(int i=0;i<members.size();i++)
{
this.inviteButtons.add(new JButton("Invite"));
}
fireTableDataChanged();
}
public void setValueAt(Object data,int row,int column)
{
System.out.println("setValueAt gets called");
if(column==1)
{
JButton button=this.inviteButtons.get(row);
String text=(String)data;
button.setText(text);
if(text.equalsIgnoreCase("Invited"))
{
awaitingInvitationReply=true;
for(JButton inviteButton:inviteButtons) inviteButton.setEnabled(false);
this.fireTableDataChanged();
ChessUI.this.sendInvitation(this.members.get(row));
}else if(text.equalsIgnoreCase("invite"))
{
awaitingInvitationReply=false;
for(JButton inviteButton:inviteButtons) inviteButton.setEnabled(true);
this.fireTableDataChanged();
}

}
}
}
class AvailableMembersListButtonRenderer implements TableCellRenderer
{
public Component getTableCellRendererComponent(JTable table,Object value,boolean a,boolean b,int row,int column)
{
System.out.println("getTableCellRendererComponent gets called");
return (JButton)value;
}
}
class AvailableMembersListButtonCellEditor extends DefaultCellEditor
{
private boolean isClicked;
private ActionListener actionListener;
private int row,col;
AvailableMembersListButtonCellEditor()
{
super(new JCheckBox());//because of policy
this.actionListener=new ActionListener(){
public void actionPerformed(ActionEvent ev)
{
System.out.println("actionPerformed gets called");
fireEditingStopped();
}
};
}
public Component getTableCellEditorComponent(JTable table,Object value,boolean a,int row,int col)
{
System.out.println("getTableCellEditorComponent gets called");
this.row=row;
this.col=col;
JButton button=(JButton)availableMembersListModel.getValueAt(row,col);
button.removeActionListener(actionListener);
button.addActionListener(actionListener);
button.setOpaque(true);
button.setForeground(Color.black);
button.setBackground(UIManager.getColor("Button.background"));
button.setText("Invite");
isClicked=true;
return button;
}
public Object getCellEditorValue()
{
System.out.println("getCellEditor gets called");
return "Invited";
}
public boolean stopCellEditing()
{
System.out.println("stopCellEditing gets called");
isClicked=false;
return super.stopCellEditing();
}
public void fireEditingStopped()
{
//do whatever is required
System.out.println("fireEditingStopped gets called");
super.fireEditingStopped();
}
}
}//outer class ends