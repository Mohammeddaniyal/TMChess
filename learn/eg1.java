import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
class MyModel extends AbstractTableModel
{
private Object data[][];
private String[] title={"A","B"};
MyModel()
{
data=new Object[2][2];
data[0][0]="One";
data[0][1]=new JButton("First button");
data[1][0]="Two";
data[1][1]=new JButton("Second button");
}
public int getRowCount()
{
return data.length;
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
return data[row][column];
}
public boolean isCellEditable(int row,int column)
{
if(column==1) return true;
return false;
}
public Class getColumnClass(int column)
{
return data[column].getClass();
}
public void setValueAt(Object data,int row,int column)
{
System.out.println(row+","+column+","+data);
}
}
class Whatever extends JFrame
{
private JTable table;
MyModel model;
Whatever()
{
model=new MyModel();
table=new JTable(model);
table.getColumn("B").setCellRenderer(new ButtonRenderer());
table.getColumn("B").setCellEditor(new ButtonCellEditor());
Container c=getContentPane();
c.setLayout(new BorderLayout());
c.add(table);
setLocation(10,10);
setSize(500,400);
setVisible(true);
}
class ButtonRenderer implements TableCellRenderer
{
public Component getTableCellRendererComponent(JTable table,Object value,boolean a,boolean b,int row,int column)
{
System.out.println("getTableCellRendererComponent gets called");
System.out.println(row+","+column);
return (JButton)value;
}
}
class ButtonCellEditor extends DefaultCellEditor
{
private JButton button;
private boolean isClicked;
private int row,col;
ButtonCellEditor()
{
super(new JCheckBox());//because of policy
button=new JButton();
button.setOpaque(true);
button.addActionListener(ev->{
System.out.println("Great");
fireEditingStopped();
});
}
public Component getTableCellEditorComponent(JTable table,Object value,boolean a,int row,int col)
{
System.out.println("getTableCellEditorComponent gets called");
this.row=row;
this.col=col;
button.setForeground(Color.black);
button.setBackground(UIManager.getColor("Button.background"));
button.setText("Whatever");
isClicked=true;
return button;
}
public Object getCellEditorValue()
{
System.out.println("getCellEditorValue got called");
return "cool";
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

}
class psp
{
public static void main(String args[])
{
Whatever w=new Whatever();
}
}
