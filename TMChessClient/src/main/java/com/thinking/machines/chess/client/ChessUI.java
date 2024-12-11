package com.thinking.machines.chess.client;
import com.thinking.machines.nframework.client.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
public class ChessUI extends JFrame
{
private String username;
private JList availableMembersList;
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
}
private void initComponents()
{
JPanel p1=new JPanel();
p1.setLayout(new BorderLayout());
p1.add(new JLabel("Members"),BorderLayout.NORTH);
this.client=new NFrameworkClient();
this.availableMembersList=new JList();
p1.add(availableMembersList);
container=getContentPane();
container.setLayout(new BorderLayout());
container.add(p1,BorderLayout.EAST);
}
private void setAppearence()
{
Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
int width=500;
int height=400;
setSize(width,height);
setLocation(d.width/2-width/2,d.height/2-height/2);
}
private void addListeners()
{
timer=new javax.swing.Timer(1000,new ActionListener(){
public void actionPerformed(ActionEvent ev)
{
try
{
java.util.List<String> members=(java.util.List<String>)client.execute("/TMChessServer/getMembers",username);
Vector v=new Vector();
for(String member:members)
{
v.add(member);
}
ChessUI.this.availableMembersList.setListData(v);
}catch(Throwable t)
{
JOptionPane.showMessageDialog(ChessUI.this,t.toString());
}
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
}