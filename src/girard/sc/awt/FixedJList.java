package girard.sc.awt;

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class FixedJList extends JPanel implements MouseListener,ItemSelectable
    {
    public int last = -1; // index location of the last thing added.

    public final static int CENTER = 0;
    public final static int LEFT = 1;
    public final static int RIGHT = 2;

    protected JList m_TheList;
    protected JScrollPane m_ScrollManager;
    protected FixedListModel m_ListModel;
    private boolean m_Sorted = false;

    protected transient ActionListener m_actionListener;
    protected transient int m_actionListeners = 0;

    protected transient ItemListener m_itemListener;
    protected transient int m_itemListeners = 0;

    public FixedJList(int n, int s, int ori)
        {
        m_ListModel = new FixedListModel(n,s,ori);

        m_TheList = new JList(m_ListModel);

        m_TheList.setFont(new Font("Monospaced",Font.PLAIN,14));
        
        m_TheList.setPrototypeCellValue(m_ListModel.getGhostEntry());

        createPanel();
        }
/**
 *
 */
    public FixedJList(int n, int s[], int ori)
        {
        m_ListModel = new FixedListModel(n,s,ori);

        m_TheList = new JList(m_ListModel);
        
        m_TheList.setFont(new Font("Monospaced",Font.PLAIN,14));

        m_TheList.setPrototypeCellValue(m_ListModel.getGhostEntry());

        createPanel();
        }

    public void addItem(String str[])
        {
        if (m_Sorted)
            {
            last = m_ListModel.insertItem(str,m_ListModel.getSortIndex(str));
            }
        else
            {
            last = m_ListModel.addItem(str);
            }
        m_ListModel.update();
        }
    

    public synchronized void addActionListener(ActionListener l)
        {
        if ((m_actionListeners == 0) && (m_itemListeners == 0))
            m_TheList.addMouseListener(this);

        m_actionListener = AWTEventMulticaster.add(m_actionListener, l);
        m_actionListeners++;
        }
    public synchronized void addItemListener(ItemListener l)
        {
        if ((m_actionListeners == 0) && (m_itemListeners == 0))
            m_TheList.addMouseListener(this);

        m_itemListener = AWTEventMulticaster.add(m_itemListener, l);
        m_itemListeners++;
        }

    private void createPanel()
        {
        setLayout(new GridLayout(1,1));
        setBackground(Color.lightGray);

        m_TheList.setBackground(Color.white);
        m_ScrollManager = new JScrollPane(m_TheList);

        add(m_ScrollManager);
        }

    public Object getItem(int index)
        {
        return m_ListModel.getItem(index);
        }
    public int getItemCount()
        {
        return m_ListModel.getItemCount();
        }
    public Dimension getPreferredSize()
        {
        int scrollOffset = m_ScrollManager.getVerticalScrollBar().getPreferredSize().width + 5;
        Dimension sizeSet = new Dimension(m_TheList.getFixedCellWidth()+scrollOffset,m_TheList.getFixedCellHeight()*m_TheList.getVisibleRowCount());
        return sizeSet;
        }
    public int getSelectedIndex()
        {
        return m_TheList.getSelectedIndex();
        }
    public Object getSelectedItem()
        {
        return m_ListModel.getItem(m_TheList.getSelectedIndex());
        }
    public Object[] getSelectedItems()
        {
        return m_ListModel.getSelectedItems(m_TheList.getSelectedIndices());
        }
    public Object[] getSelectedObjects()
        {
        return m_ListModel.getSelectedItems(m_TheList.getSelectedIndices());
        }
    public synchronized String getSelectedSubItem(int column)
        { 
        int loc = m_TheList.getSelectedIndex();
        
        return m_ListModel.getSubItem(loc,column);
        }
    public String getSubItem(int loc, int column)
        {
        return m_ListModel.getSubItem(loc,column);
        }
    public boolean getSorted()
        {
        return m_Sorted;
        }

    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
        
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) 
        {
        if (e.getSource() instanceof JList)
            {
            JList theSource = (JList)e.getSource();

            if ((m_actionListeners > 0) && (e.getClickCount() == 2) && (m_TheList.getSelectedIndex() >=0))
                {
                m_actionListener.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"Double Click"));
                }

            if ((m_itemListeners > 0) && (e.getClickCount() == 1) && (m_TheList.getSelectedIndex() >= 0))
                {
                Object theItem = getSelectedItem();

                m_itemListener.itemStateChanged(new ItemEvent(this,ItemEvent.ITEM_STATE_CHANGED,theItem,ItemEvent.SELECTED));
                }
            }
        } 
    public void mouseDragged(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}
        

    public synchronized void removeActionListener(ActionListener l)
        {
        if ((m_actionListeners == 1) && (m_itemListeners == 0))
            m_TheList.removeMouseListener(this);

        m_actionListener = AWTEventMulticaster.remove(m_actionListener, l);
        m_actionListeners--;
        }
    public synchronized void removeAll()
        {
        m_ListModel.removeAll();
        m_ListModel.update();
        }
    public void removeItem(int value)
        {
        m_ListModel.removeItem(value);
        m_ListModel.update();
        }
   public synchronized void removeItemListener(ItemListener l)
        {
        if ((m_actionListeners == 0) && (m_itemListeners == 1)) 
        m_TheList.removeMouseListener(this);

        m_itemListener = AWTEventMulticaster.remove(m_itemListener, l);
        m_itemListeners--;
        }
    public void replaceItem(String[] str, int loc)
        {
        if (m_Sorted)
            {
            m_ListModel.removeItem(loc);
            last = m_ListModel.insertItem(str,m_ListModel.getSortIndex(str));
            }
        else
            {
            m_ListModel.replaceItem(str,loc);
            last = loc;
            }
        m_ListModel.update();
        }

    public void setAscendingOrder(boolean[] ascending)
        {
        m_ListModel.setAscendingOrder(ascending);
        }
    public void setAscendingOrder(int loc, boolean value)
        {
        m_ListModel.setAscendingOrder(loc,value);
        }
    public void setListFont(Font f)
        {
        m_TheList.setFont(f);
        m_TheList.setPrototypeCellValue(null);
        m_TheList.setPrototypeCellValue(m_ListModel.getGhostEntry());
        }
    public void setSelectionMode(int value)
        {
        m_TheList.setSelectionMode(value);
        }
    public void setSorted(boolean value)
        {
        m_Sorted = value;
        }
    public void setSortOrder(int[] order)
        {
        m_ListModel.setSortOrder(order);
        }
    public void setVisibleRowCount(int value)
        {
        m_TheList.setVisibleRowCount(value);
        }
    }

class FixedListModel extends AbstractListModel
    {
    Vector m_ListItems = new Vector();
    Vector m_DisplayItems = new Vector();
     

    int m_Orientation; 
    int m_columns;     /* Number of Data Columns */
    int m_columnSize[];  /* Size of each column */ 
    boolean m_GhostEntry = false;

    int[] m_sortOrder;
    boolean[] m_ascendingOrder;

    public FixedListModel(int n, int s, int ori)
        {
        int x;

        m_columns = n;
        
        m_columnSize = new int[m_columns];
        for (x=0;x<m_columns;x++)
            m_columnSize[x] = s;
        m_Orientation = ori;
 
        m_sortOrder = new int[n];
        m_ascendingOrder = new boolean[n];

        for (x=0;x<n;x++)
            {
            m_sortOrder[x] = x;
            m_ascendingOrder[x] = true;
            }
        }
    public FixedListModel(int n, int s[], int ori)
        {
        int x;

        m_columns = n;
        m_columnSize = new int[m_columns];
        if (s.length == m_columns)
            {
            for (x=0;x<m_columns;x++)
                m_columnSize[x] = s[x];
            }
        else
            {
            for (x=0;x<m_columns;x++)
                m_columnSize[x] = s[0];
            }
        m_Orientation = ori;

        m_sortOrder = new int[n];
        m_ascendingOrder = new boolean[n];

        for (x=0;x<n;x++)
            {
            m_sortOrder[x] = x;
            m_ascendingOrder[x] = true;
            }
        }

    public synchronized int addItem(String str[])
        {  
        if (str.length != m_columns)
            {
            /* Error, throw an exception */
            return -1;
            }         
        if (m_GhostEntry)
            {
            m_GhostEntry = false;
            m_DisplayItems.removeElementAt(0);
            m_ListItems.removeElementAt(0);
            m_ListItems.addElement(str);
            m_DisplayItems.addElement(buildListEntry(str));
            return 0;
            }
        else
            {
            m_DisplayItems.addElement(buildListEntry(str));
            m_ListItems.addElement(str);
            return m_DisplayItems.size() - 1;
            }   
        }

    protected String buildListEntry(String[] str)
        {
        int x, k, m;
        StringBuffer hold = new StringBuffer("");

        for (x=0;x<m_columns;x++)
            {
            if (m_columnSize[x] > str[x].length())
                {
                if (m_Orientation == FixedJList.LEFT)
                    {
                    hold.append(str[x]);
                    m = m_columnSize[x] - str[x].length();
                    for (k=0;k<m;k++)
                        {
                        hold.append(" ");    
                        }
                    }
                if (m_Orientation == FixedJList.CENTER)
                    {
                    m = (m_columnSize[x] - str[x].length())/2;
                    for (k=0;k<m;k++)
                        {
                        hold.append(" ");    
                        }
                    hold.append(str[x]);
                    m = m_columnSize[x] - (m + str[x].length());
                    for (k=0;k<m;k++)
                        {
                        hold.append(" ");    
                        }
                    }
                if (m_Orientation == FixedJList.RIGHT)
                    {  
                    m = m_columnSize[x] - str[x].length();
                    for (k=0;k<m;k++)
                        {
                        hold.append(" ");    
                        }
                    hold.append(str[x]);
                    }
                }
            else
                hold.append(str[x].substring(0,m_columnSize[x]));
            }
        return hold.toString();
        }

    public Object getElementAt(int value)
        {
        if ((value >= 0) && (value < m_DisplayItems.size()))
            {
            return m_DisplayItems.elementAt(value);
            }

        return null;
        }
    protected String getGhostEntry()
        {
        int x, k;
        StringBuffer hold = new StringBuffer("");

        for (k=0;k<m_columns;k++)
            {
            for (x=0;x<m_columnSize[k];x++)
                {
                hold.append("0");
                }
            }

        hold.append("0");

        return hold.toString();
        }
    public int getItemCount()
        {
        return m_ListItems.size();
        }
    public Object getItem(int value)
        {
        if ((value >= 0) && (value < m_ListItems.size()))
            {
            return m_ListItems.elementAt(value);
            }

        return null;
        }
    public Object[] getSelectedItems(int[] indices)
        {
        Object[] itemList = new Object[indices.length];

        for (int x=0;x<indices.length;x++)
            {
            itemList[x] = m_ListItems.elementAt(indices[x]);
            }

        return itemList;
        }
    public String getSubItem(int loc, int column)
        {
        String[] str = (String[])m_ListItems.elementAt(loc);
        
        return str[column];
        }
/**
 * Should be the size of the Vector we are using the store the actual elements
 * displayed in the list.
 */
    public int getSize()
        {
        return m_DisplayItems.size();
        }
    public int getSortIndex(String str[])
        {
        if (m_GhostEntry)
            return 0;

        for (int x=0;x<m_ListItems.size();x++)
            {
            if (isSortLocation(str,x))
                {
                return x;
                }
            } 
        return m_DisplayItems.size();
        }

    public synchronized int insertItem(String[] str, int loc)
        {
        if (m_GhostEntry)
            {
            m_GhostEntry = false;
            m_DisplayItems.removeElementAt(0);
            m_ListItems.removeElementAt(0);
            m_ListItems.addElement(str);
            m_DisplayItems.addElement(buildListEntry(str));
            return 0;
            }
        else
            {
            m_DisplayItems.insertElementAt(buildListEntry(str),loc);
            m_ListItems.insertElementAt(str,loc);
            return loc;
            }   
        }

    private boolean isSortLocation(String[] str, int loc)
        {
        String[] entry = (String[])m_ListItems.elementAt(loc);
        for (int x=0;x<m_columns;x++)
            {
            int cp = m_sortOrder[x];
            if (m_ascendingOrder[x])
                {
                if (entry[cp].compareTo(str[cp]) > 0)
                    return true;
                if (entry[cp].compareTo(str[cp]) < 0)
                    return false;
                }
            else
                {
                if (entry[cp].compareTo(str[cp]) < 0)
                    return true;
                if (entry[cp].compareTo(str[cp]) > 0)
                    return false;
                }
            }
        return false;
        }

    public synchronized void removeAll()
        {
        if (m_GhostEntry)
            return;
        m_DisplayItems.removeAllElements();
        m_ListItems.removeAllElements();
        }
    public void removeItem(int value)
        {
        if ((value >= 0) && (value < m_ListItems.size()))
            {
            m_DisplayItems.removeElementAt(value);
            m_ListItems.removeElementAt(value);
            }
        }

    public synchronized void replaceItem(String str[], int loc)
        {
        if (str.length != m_columns)
            {
            /* Error, throw an exception */
            return;
            } 
        else if (m_GhostEntry)
            {
            m_GhostEntry = false;
            m_DisplayItems.removeElementAt(0);
            m_ListItems.removeElementAt(0);
            m_DisplayItems.addElement(buildListEntry(str));
            m_ListItems.addElement(str);
            }
        else if (loc >= m_DisplayItems.size())
            {
            /* Error, throw an exception */
            return;
            }
        else
            {
            removeItem(loc);
            insertItem(str,loc);
            }
        }

    public void setAscendingOrder(boolean[] ascending)
        {
        m_ascendingOrder = ascending;
        }
    public void setAscendingOrder(int loc, boolean value)
        {
        m_ascendingOrder[loc] = value;
        }
    public void setSortOrder(int[] order)
        {
        m_sortOrder = order;
        }

    protected void update()
        {
        fireContentsChanged(this,0,m_ListItems.size());
        }
    protected void update(int value)
        {
        fireContentsChanged(this,value,value);
        }
    }