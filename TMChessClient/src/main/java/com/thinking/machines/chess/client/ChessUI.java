package com.thinking.machines.chess.client;
import com.thinking.machines.nframework.client.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;
import com.thinking.machines.chess.common.*;
public class ChessUI extends JFrame
{
private java.util.List<Message> pendingMessages;
private final long TIMEOUT_DURATION=30*1000;
private String username;
private JTable availableMembersList;
private JScrollPane availableMembersListScrollPane;
private AvailableMembersListModel availableMembersListModel;
private JTable invitationsList;
private InvitationsListModel invitationsListModel;
private javax.swing.Timer timer;
private javax.swing.Timer invitationsTimer;
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
this.pendingMessages=new java.util.LinkedList<>();
this.availableMembersListModel=new AvailableMembersListModel();
this.availableMembersList=new JTable(availableMembersListModel);
this.availableMembersList.getColumn(" ").setCellRenderer(new AvailableMembersListButtonRenderer());
this.availableMembersList.getColumn(" ").setCellEditor(new AvailableMembersListButtonCellEditor());
this.availableMembersListScrollPane=new JScrollPane(this.availableMembersList,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

this.invitationsListModel=new InvitationsListModel();
this.invitationsList=new JTable(invitationsListModel);
this.invitationsList.getColumn(" ").setCellRenderer(new InvitationsListButtonRenderer());
this.invitationsList.getColumn(" ").setCellEditor(new InvitationsListButtonCellEditor());
this.invitationsList.getColumn("  ").setCellRenderer(new InvitationsListButtonRenderer());
this.invitationsList.getColumn("  ").setCellEditor(new InvitationsListButtonCellEditor());


JButton availableMembersButton=new JButton("Members");
JButton invitationsInboxButton=new JButton("Invitation");
JPanel p1=new JPanel();
p1.setLayout(new GridLayout(5,1));
p1.add(new JLabel("Members"),BorderLayout.NORTH);
this.client=new NFrameworkClient();
p1.add(new JLabel("		"));
p1.add(new JLabel("		"));
p1.add(availableMembersList);
//p1.add(availableMembersButton);
p1.add(new JLabel("		"));
p1.add(invitationsList);
container=getContentPane();
container.setLayout(new BorderLayout());
container.add(p1,BorderLayout.EAST);

/*
JDialog availableMembersDialog=new JDialog(ChessUI.this,"Available Members",true);
availableMembersDialog.setSize(250,300);
availableMembersDialog.setVisible(false);
//availableMembersDialog.setLocationRelativeTo(ChessUI.this);
availableMembersDialog.setLayout(new BorderLayout());
availableMembersDialog.add(ChessUI.this.availableMembersList,BorderLayout.CENTER);
availableMembersButton.addActionListener(ev->{
availableMembersDialog.setLocationRelativeTo(ChessUI.this);
availableMembersDialog.setVisible(true);
});
JDialog invitationsListDialog=new JDialog(ChessUI.this,"Invitations",true);
invitationsListDialog.setSize(250,300);
invitationsListDialog.setVisible(false);
//invitationsListDialog.setLocationRelativeTo(ChessUI.this);
invitationsListDialog.setLayout(new BorderLayout());
invitationsListDialog.add(ChessUI.this.invitationsList,BorderLayout.CENTER);

invitationsInboxButton.addActionListener(ev->{
invitationsListDialog.setLocationRelativeTo(ChessUI.this);
invitationsListDialog.setVisible(true);
});
*/

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

invitationsTimer=new javax.swing.Timer(1000,new ActionListener(){
public void actionPerformed(ActionEvent ev)
{
invitationsTimer.stop();
try
{
java.util.List<Message> messages=(java.util.List<Message>)client.execute("/TMChessServer/getMessages",username);
if(messages==null) 
{
invitationsTimer.start();
return;
}

for(Message message:messages)
{
ChessUI.this.pendingMessages.add(message);
}


invitationsListModel.setMessages(messages);

javax.swing.Timer pendingInvitationMessagesTimer=new javax.swing.Timer(1000,(ev1)->{
int i=0;
for(Message m:ChessUI.this.pendingMessages)
{
if(m.type==MESSAGE_TYPE.CHALLENGE)
{
long receivedTime=m.inviteTimeStamp;
long currentTime=System.currentTimeMillis();
if(currentTime-receivedTime>=TIMEOUT_DURATION)
{
System.out.println("Removing invitation request of user "+m.fromUsername);
ChessUI.this.pendingMessages.remove(i);
invitationsListModel.removeInvitationOfUser(m.fromUsername);
}
}
i++;
}
if(ChessUI.this.pendingMessages.size()==0) ((javax.swing.Timer)ev1.getSource()).stop();
});
pendingInvitationMessagesTimer.start();

}catch(Throwable t)
{
JOptionPane.showMessageDialog(ChessUI.this,t.toString());
}
invitationsTimer.start();
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
invitationsTimer.start();
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

// start a timer to get the update on what happened with invitation
//done
javax.swing.Timer getInvitationStatusTimer=new javax.swing.Timer(1000,(ev)->{
try
{
System.out.println("HELLO");
Message message=(Message)client.execute("/TMChessServer/getInvitationStatus",username,toUsername);
System.out.println("HI");
if(message==null) return;
if(message.type==MESSAGE_TYPE.CHALLENGE_IGNORED)
{
JOptionPane.showMessageDialog(this,"Invitation rejected of user : "+message.toUsername+" which was sent to the -> "+message.fromUsername);
System.out.println("Invitation rejected of user : "+message.toUsername+" which was sent to the -> "+message.fromUsername);
System.out.println("Stopping the timer (getInvitationStatusTimer)");
((javax.swing.Timer)ev.getSource()).stop();
System.out.println("timer stopped (getInvitationStatusTimer)");
availableMembersListModel.enableInviteButtons();
}
}catch(Throwable t)
{
JOptionPane.showMessageDialog(this,t.toString());
}
});
getInvitationStatusTimer.start();
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
//System.out.println("get value at : "+row+","+column);
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
//System.out.println("firing table availablemembers");
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
public void enableInviteButtons()
{
this.awaitingInvitationReply=false;
for(JButton inviteButton:inviteButtons) 
{
inviteButton.setText("Invite");
inviteButton.setEnabled(true);
}
fireTableDataChanged();
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


class InvitationsListModel extends AbstractTableModel
{
private java.util.List<String> members;
private java.util.List<JButton> acceptButtons;
private java.util.List<JButton> rejectButtons;
private String[] title={"Members"," ","  "};
private boolean awaitingInvitationReply;
InvitationsListModel()
{
awaitingInvitationReply=false;
members=new LinkedList<>();
acceptButtons=new LinkedList<>();
rejectButtons=new LinkedList<>();
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
System.out.println(row+","+column);
if(column==0) return this.members.get(row);
if(column==1) return this.acceptButtons.get(row);
return this.rejectButtons.get(row);
}
public boolean isCellEditable(int row,int column)
{
if(column==1 || column==2) return true;
return false;
}
public Class getColumnClass(int column)
{
if(column==0) return String.class;
return JButton.class;
}
public void setMessages(java.util.List<Message> messages)
{
//this.messages=messages;
if(messages.size()==0) 
{
return;	
}
//this.acceptButtons.clear();
//this.rejectButtons.clear();
for(Message message:messages)
{
if(message.type==MESSAGE_TYPE.CHALLENGE)
{
System.out.println(message.fromUsername);
this.members.add(message.fromUsername);
this.acceptButtons.add(new JButton("Accept"));
this.rejectButtons.add(new JButton("Reject"));
}
}
fireTableDataChanged();
System.out.println("fire tabled : "+members.size());
}
public void setValueAt(Object data,int row,int column)
{
System.out.println("setValueAt gets called");

if(column==1)
{
JButton button=this.acceptButtons.get(row);
String text=(String)data;
button.setText(text);

if(text.equalsIgnoreCase("Accepted"))
{
for(JButton acceptButton:acceptButtons) acceptButton.setEnabled(false);
for(JButton rejectButton:rejectButtons) rejectButton.setEnabled(false);
this.fireTableDataChanged();
}else if(text.equalsIgnoreCase("Accept"))
{
for(JButton acceptButton:acceptButtons) acceptButton.setEnabled(true);
for(JButton rejectButton:rejectButtons) rejectButton.setEnabled(true);
this.fireTableDataChanged();
}

}//accept part ends here



if(column==2)
{
JButton button=this.rejectButtons.get(row);
String text=(String)data;
button.setText(text);

if(text.equalsIgnoreCase("Rejected"))
{
for(JButton acceptButton:acceptButtons) acceptButton.setEnabled(true);
for(JButton rejectButton:rejectButtons) rejectButton.setEnabled(true);
members.remove(row);
acceptButtons.remove(row);
rejectButtons.remove(row);
//ChessUI.this.sendInvitationReply(MESSAGE_TYPE.REJECTED);
this.fireTableDataChanged();
}else if(text.equalsIgnoreCase("Reject"))
{
for(JButton acceptButton:acceptButtons) acceptButton.setEnabled(true);
for(JButton rejectButton:rejectButtons) rejectButton.setEnabled(true);
this.fireTableDataChanged();
}

}
}
public void removeInvitationOfUser(String username)
{
int row;
for(row=0;row<members.size();row++)
{
if(members.get(row).equals(username)) break;
}
System.out.println("FOUND AT ROW : "+row);
this.members.remove(row);
this.acceptButtons.remove(row);
this.rejectButtons.remove(row);
this.fireTableDataChanged();
}
}
class InvitationsListButtonRenderer implements TableCellRenderer
{
public Component getTableCellRendererComponent(JTable table,Object value,boolean a,boolean b,int row,int column)
{
System.out.println("getTableCellRendererComponent gets called");
return (JButton)value;
}
}
class InvitationsListButtonCellEditor extends DefaultCellEditor
{
private ActionListener actionListener;
private int row,col;
private boolean acceptClicked=false;
private boolean rejectClicked=false;
InvitationsListButtonCellEditor()
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
JButton button=(JButton)invitationsListModel.getValueAt(row,col);
button.removeActionListener(this.actionListener);
button.addActionListener(actionListener);
button.setOpaque(true);
button.setForeground(Color.black);
button.setBackground(UIManager.getColor("Button.background"));
if(col==1)
{
System.out.println("button.setText(Accept)");
button.setText("Accepted");
acceptClicked=true;
}
else if(col==2)
{
System.out.println("REJECTED clicked");
button.setText("Rejected");
rejectClicked=true;
}
return button;
}
public Object getCellEditorValue()
{
System.out.println("getCellEditor gets called");
if(acceptClicked) return "Accepted";
return "Rejected";
}
public boolean stopCellEditing()
{
System.out.println("stopCellEditing gets called");
acceptClicked=rejectClicked=false;
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