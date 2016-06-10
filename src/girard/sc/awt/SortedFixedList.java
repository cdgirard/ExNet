package girard.sc.awt;

import java.awt.AWTEventMulticaster;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.ItemSelectable;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

public class SortedFixedList extends Panel implements ActionListener,ItemListener,ItemSelectable
    {
    public int last = -1; // index location of the last thing added.

    public final static int CENTER = 0;
    public final static int LEFT = 1;
    public final static int RIGHT = 2;

    protected List m_TheList;

    protected transient ActionListener m_actionListener;
    protected transient int m_actionListeners = 0;

    protected transient ItemListener m_itemListener;
    protected transient int m_itemListeners = 0;

    protected int m_Orientation; 
    protected int m_columns;     /* Number of Data Columns */
    protected int m_columnSize[];  /* Size of each column */ 
    protected Vector m_listItems = new Vector(); /* So you can retrive items by index and column */  
    protected boolean m_GhostEntry; // nvm - what is ghost entry?
    protected boolean m_ReplaceEntry = false;

    protected int[] m_sortOrder;
    protected boolean[] m_ascendingOrder;

    public SortedFixedList(int horz, boolean value, int n, int s)
        {
        m_TheList = new List(horz, value);

        int x;

        m_TheList.setFont(new Font("Monospaced",Font.PLAIN,14));
        m_columns = n;
        m_columnSize = new int[m_columns];
        for (x=0;x<m_columns;x++)
            m_columnSize[x] = s;
        m_Orientation = LEFT;
 
        m_sortOrder = new int[n];
        m_ascendingOrder = new boolean[n];

        for (x=0;x<n;x++)
            {
            m_sortOrder[x] = x;
            m_ascendingOrder[x] = true;
            }

        addGhostEntry();

        setLayout(new GridLayout(1,1));
        add(m_TheList);
        }

    public SortedFixedList(int horz, boolean value, int n, int s[])
        {
        m_TheList = new List(horz, value);

        int x;

        m_TheList.setFont(new Font("Monospaced",Font.PLAIN,14));
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
        m_Orientation = LEFT;


        m_sortOrder = new int[n];
        m_ascendingOrder = new boolean[n];

        for (x=0;x<n;x++)
            {
            m_sortOrder[x] = x;
            m_ascendingOrder[x] = true;
            }

        addGhostEntry();

        setLayout(new GridLayout(1,1));
        add(m_TheList);
        }

    public SortedFixedList(int horz, boolean value, int n, int s, int ori)
        {
        m_TheList = new List(horz, value);

        int x;

        m_TheList.setFont(new Font("Monospaced",Font.PLAIN,14));
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

        addGhostEntry();

        setLayout(new GridLayout(1,1));
        add(m_TheList);
        }

    public SortedFixedList(int horz, boolean value, int n, int s[], int ori)
        {
        m_TheList = new List(horz, value);

        int x;

        m_TheList.setFont(new Font("Monospaced",Font.PLAIN,14));
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

        addGhostEntry();

        setLayout(new GridLayout(1,1));
        add(m_TheList);
        }

    public void actionPerformed(ActionEvent e) 
        {
        if (e.getSource() instanceof List)
            {
            List theSource = (List)e.getSource();

            if (m_actionListeners > 0)
                m_actionListener.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,e.getActionCommand()));
            System.err.println("horrayyyy!!");
            }
        }

    public synchronized void add(String str[])
        {   
        if (str.length != m_columns)
            {
            /* Error, throw an exception */
            return;
            } 
        if (m_GhostEntry)
            {
            replaceItem(str,0);
            last = 0;
            }
        else
            {
            for (int x=0;x<m_TheList.getItemCount();x++)
                {
                if (isSortLocation(str,x))
                    {
                    m_TheList.addItem(BuildListEntry(str),x);
                    m_listItems.insertElementAt(str,x);
                    last = x;
                    return;
                    }
                } 

            int key = m_TheList.getItemCount();   
            m_TheList.addItem(BuildListEntry(str));
            m_listItems.insertElementAt(str,key);
            last = key;
            }
        }
    public synchronized void add(String str[],int loc)
        {   
        if (str.length != m_columns)
            {
            /* Error, throw an exception */
            return;
            }         
        if ((m_GhostEntry) && (loc == 0))
            {
            replaceItem(str,0);
            last = 0;
            }
        else 
            {
            if (m_GhostEntry)
                {
                replaceItem(str,0);
                last = 0;
                }
            else
                {
                for (int x=0;x<m_TheList.getItemCount();x++)
                    {
                    if (isSortLocation(str,x))
                        {
                        m_TheList.addItem(BuildListEntry(str),x);
                        m_listItems.insertElementAt(str,x);
                        last = x;
                        return;
                        }
                    } 

                int key = m_TheList.getItemCount();   
                m_TheList.addItem(BuildListEntry(str));
                m_listItems.insertElementAt(str,key);
                last = key;
                }
            }
        }
    public synchronized void addActionListener(ActionListener l)
        {
        m_TheList.addActionListener(this);

        m_actionListener = AWTEventMulticaster.add(m_actionListener, l);
        m_actionListeners++;
        }
    protected void addGhostEntry()
        {
        int x, k;
        StringBuffer hold = new StringBuffer("");
        
        if (m_TheList.getItemCount() > 0)
            return;

        for (k=0;k<m_columns;k++)
            {
            for (x=0;x<m_columnSize[k];x++)
                {
                hold.append(" ");
                }
            }
        m_TheList.addItem(hold.toString());
        m_GhostEntry = true;
        }

    public synchronized void addItem(String str[])
        {
        if (str.length != m_columns)
            {
            /* Error, throw an exception */
            return;
            } 
        if (m_GhostEntry)
            {
            replaceItem(str,0);
// System.err.println("OBJ: "+BuildListEntry(str));
            last = 0;
            }
        else
            {
            for (int x=0;x<m_TheList.getItemCount();x++)
                {
                if (isSortLocation(str,x))
                    {
                    m_TheList.addItem(BuildListEntry(str),x);
                    m_listItems.insertElementAt(str,x);
                    last = x;
                    return;
                    }
                } 

            int key = m_TheList.getItemCount();   
            m_TheList.addItem(BuildListEntry(str));
// System.err.println("OBJ: "+BuildListEntry(str)+" key: "+key+" IC: "+m_TheList.getItemCount());
            m_listItems.insertElementAt(str,key);
            last = key;
            }
        }
    public synchronized void addItem(String str[],int loc)
        {   
        if (str.length != m_columns)
            {
            /* Error, throw an exception */
            return;
            }         
        if ((m_GhostEntry) && (loc == 0))
            {
            replaceItem(str,0);
            last = 0;
            }
        else 
            {
            if (m_GhostEntry)
                {
                m_GhostEntry = false;
                replaceItem(str,0);
                m_listItems.insertElementAt(str,0);
                last = 0;
                }
            else
                {
                for (int x=0;x<m_TheList.getItemCount();x++)
                    {
                    if (isSortLocation(str,x))
                        {
                        m_TheList.addItem(BuildListEntry(str),x);
                        m_listItems.insertElementAt(str,x);
                        last = x;
                        return;
                        }
                    } 
                int key = m_TheList.getItemCount();   
                m_TheList.addItem(BuildListEntry(str));
                m_listItems.insertElementAt(str,key);
                last = key;
                }
            }   
        }
    public synchronized void addItemListener(ItemListener l)
        {
        m_TheList.addItemListener(this);

        m_itemListener = AWTEventMulticaster.add(m_itemListener, l);
        m_itemListeners++;
        }

    /*nvm -- quick and dirty hack. 
     * The original BuildListEntry returns the a string of the form 
     * "node-name number"
     * I just want the nodename in the string. 
     * */
    protected String BuildListEntryExternality(String[] str)
    {
    int x, k, m;
    StringBuffer hold = new StringBuffer("");

    for (x=0;x<m_columns;x++)
        {
        if (m_columnSize[x] > str[x].length())
            {
            if (m_Orientation == LEFT)
                {
                hold.append(str[x]);
                m = m_columnSize[x] - str[x].length();
                for (k=0;k<m;k++)
                    {
                    hold.append(" ");    
                    }
                }
            if (m_Orientation == CENTER)
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
            if (m_Orientation == RIGHT)
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

    
    
    protected String BuildListEntry(String[] str)
        {
        int x, k, m;
        StringBuffer hold = new StringBuffer("");

        for (x=0;x<m_columns;x++)
            {
            if (m_columnSize[x] > str[x].length())
                {
                if (m_Orientation == LEFT)
                    {
                    hold.append(str[x]);
                    m = m_columnSize[x] - str[x].length();
                    for (k=0;k<m;k++)
                        {
                        hold.append(" ");    
                        }
                    }
                if (m_Orientation == CENTER)
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
                if (m_Orientation == RIGHT)
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

    public void deselect(int value)
        {
        m_TheList.deselect(value);
        }

    public synchronized String[] getFixedItem(int loc)
        {
        if (m_GhostEntry)
            return null;
        return (String[])m_listItems.elementAt(loc);
        }
    public int getItemCount()
        {
        return m_listItems.size();
        }
    public synchronized int getSelectedIndex()
        {
        if (m_GhostEntry)
            return -1;
        return m_TheList.getSelectedIndex();
        }
    public synchronized String[] getSelectedFixedItem()
        {
        if (m_GhostEntry)
            return null;
        else
            { 
            int i = m_TheList.getSelectedIndex();
            if (i > -1)
                return this.getFixedItem(i);
            else
                return null;
            }
        }
    public synchronized int[] getSelectedIndexes()
        {
        if (getItemCount() == 0)
            return new int[0];

        Vector v = new Vector();
        for(int x=0;x<getItemCount();x++)
            {
            if (m_TheList.isIndexSelected(x))
                {
                v.addElement(new Integer(x));
                }
            }

        int[] indexes = new int[v.size()];
        for (int x=0;x<v.size();x++)
            {
            Integer index = (Integer)v.elementAt(x);
            indexes[x] = index.intValue();
            }

        return indexes;
        }
    public synchronized String getSelectedItem()
        {
        int loc = m_TheList.getSelectedIndex();
        String[] str = (String[])m_listItems.elementAt(loc);

        StringBuffer returnStr = new StringBuffer("");

        for (int x=0;x<str.length;x++)
            {
            returnStr.append(str[x]);
            }
        return returnStr.toString();
        }
    public synchronized Object[] getSelectedObjects()
        {
        if (getItemCount() == 0)
            return new Object[0];

        Vector v = new Vector();
        for(int x=0;x<getItemCount();x++)
            {
            if (m_TheList.isIndexSelected(x))
                {
                String[] str = (String[])m_listItems.elementAt(x);
                StringBuffer returnStr = new StringBuffer("");

                for (int y=0;y<str.length;y++)
                    {
                    returnStr.append(str[y]);
                    }
                v.addElement(returnStr);
                }
            }

        Object[] indexes = new Object[v.size()];
        for (int m=0;m<v.size();m++)
            {
            indexes[m] = v.elementAt(m);
            }

        return indexes;
        }
    public synchronized String getSelectedSubItem(int column)
        { 
        int loc = m_TheList.getSelectedIndex();
        String[] str = (String[])m_listItems.elementAt(loc);
        
        return str[column];
        }
    public String getSubItem(int loc, int column)
        {
        String[] str = (String[])m_listItems.elementAt(loc);
        
        return str[column];
        }

    public void itemStateChanged(ItemEvent e)
        {

        if (e.getSource() instanceof List)
            {
            List theSource = (List)e.getSource();

            if (m_itemListeners > 0)
                m_itemListener.itemStateChanged(new ItemEvent(this,ItemEvent.ITEM_STATE_CHANGED,e.getItem(),e.getStateChange()));
            }
        }

    public synchronized void replaceItem(String str[], int loc)
        {
        m_ReplaceEntry = true;

        if (str.length != m_columns)
            {
            /* Error, throw an exception */
            return;
            } 
        else if (m_GhostEntry)
            {
            m_GhostEntry = false;
            m_TheList.remove(0);
            m_TheList.addItem(BuildListEntry(str),0);
            m_listItems.insertElementAt(str,0);
            }
        else if (loc >= getItemCount())
            {
            /* Error, throw an exception */
            return;
            }
        else
            {
            remove(loc);
            addItem(str);
            }
        m_ReplaceEntry = false;
        }

    public synchronized void remove(int loc)
        {
        if ((!m_GhostEntry) && (loc < getItemCount()))
            {
            m_TheList.remove(loc);
            m_listItems.removeElementAt(loc);
            }
        if (m_listItems.size() == 0)
            addGhostEntry();
        }
    public synchronized void removeActionListener(ActionListener l)
        {
        m_TheList.removeActionListener(this);

        m_actionListener = AWTEventMulticaster.remove(m_actionListener, l);
        m_actionListeners--;
        }
    public synchronized void removeAll()
        {
        if (m_GhostEntry)
            return;
        m_TheList.removeAll();
        m_listItems.removeAllElements();
        addGhostEntry();
        }
    public synchronized void removeItemListener(ItemListener l)
        {
        m_TheList.removeItemListener(this);

        m_itemListener = AWTEventMulticaster.remove(m_itemListener, l);
        m_itemListeners--;
        }

    public void setAscendingOrder(boolean[] ascending)
        {
        m_ascendingOrder = ascending;
        }
    public void setAscendingOrder(int loc, boolean value)
        {
        m_ascendingOrder[loc] = value;
        }
    public void setFont(Font m)
        {
        m_TheList.setFont(m);
        }
    public void setSortOrder(int[] order)
        {
        m_sortOrder = order;
        }

    public void select(int value)
        {
        m_TheList.select(value);
        }

    private boolean isSortLocation(String[] str, int loc)
        {
        String[] entry = (String[])m_listItems.elementAt(loc);
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
    }


