package girard.sc.awt;

import java.awt.AWTEventMulticaster;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.ItemSelectable;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Used to display a list of String arrays in a fixed column format.  The
 * columns can be left, right, or center justified.  Uses a java.awt.List as
 * the display for the list.
 * <p>
 * <br> Started: 1999
 * <br> Modified: 07-15-2002
 * <br> Modified: 10-23-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1  
 */

public class FixedList extends Panel implements ActionListener,ItemListener,ItemSelectable
    {
/**
 * The setting for center justified columns.
 */
    public final static int CENTER = 0;
/**
 * The setting for left justified columns.
 */
    public final static int LEFT = 1;
/**
 * The setting for right justified columns.
 */
    public final static int RIGHT = 2;

/**
 * The List object that displays the specially formatted list.
 */
    protected List m_TheList = null;

/**
 * The ActionListener for the FixedList, needed if the user wants to listen for 
 * ActionEvents.
 */
    protected transient ActionListener m_actionListener;
/**
 * Keeps track of how many people are listening.
 */
    protected transient int m_actionListeners = 0;

/**
 * The ItemListener for the FixedList, needed if the user wants to listen for 
 * ItemEvents.
 */
    protected transient ItemListener m_itemListener;
/**
 * Keeps track of how many people are listening.
 */
    protected transient int m_itemListeners = 0;

/**
 * The variable that keeps track of whether the columns are center, right, or
 * left justified; Defaluts to LEFT.
 */
    protected int m_Orientation = 1;
/**
 * How many columns in each row.
 */
    protected int m_columns;
/**
 * The width of each column.
 */
    protected int m_columnSize[];
/**
 * So you can retrive items by index and column.
 */
    protected Hashtable m_listItems = new Hashtable();
/**
 * If there are no entries in the list yet we use a blank entry as a place holder,
 * this tells us if the place holder is there or not.
 */
    protected boolean m_GhostEntry;
/**
 * Are we in the midst of replacing one of the entries in the list.
 */
    protected boolean m_ReplaceEntry = false;

/**
 * The constructor.
 *
 * @param horz How many rows in m_TheList.
 * @param value Is m_TheList multi-selectable or not.
 * @param n How many columns.
 * @param s The width of each column.
 */
    public FixedList(int horz, boolean value, int n, int s)
        {
        m_TheList = new List(horz, value);

        int x;

        m_TheList.setFont(new Font("Monospaced",Font.PLAIN,14));
        m_columns = n;
        m_columnSize = new int[m_columns];
        for (x=0;x<m_columns;x++)
            m_columnSize[x] = s;
        m_Orientation = LEFT;

        addGhostEntry();

        setLayout(new GridLayout(1,1));
        add(m_TheList);
        }

/**
 * The constructor.
 *
 * @param horz How many rows in m_TheList.
 * @param value Is m_TheList multi-selectable or not.
 * @param n How many columns.
 * @param s[] The widths of each column.
 */
    public FixedList(int horz, boolean value, int n, int s[])
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

        addGhostEntry();

        setLayout(new GridLayout(1,1));
        add(m_TheList);
        }

/**
 * The constructor.
 *
 * @param horz How many rows in m_TheList.
 * @param value Is m_TheList multi-selectable or not.
 * @param n How many columns.
 * @param s The width of each column.
 * @param ori The justification for the columns; RIGHT, LEFT, or CENTER.
 */
    public FixedList(int horz, boolean value, int n, int s, int ori)
        {
        m_TheList = new List(horz, value);

        int x;

        m_TheList.setFont(new Font("Monospaced",Font.PLAIN,14));
        m_columns = n;
        m_columnSize = new int[m_columns];
        for (x=0;x<m_columns;x++)
            m_columnSize[x] = s;
        m_Orientation = ori;

        addGhostEntry();

        setLayout(new GridLayout(1,1));
        add(m_TheList);
        }

/**
 * The constructor.
 *
 * @param horz How many rows in m_TheList.
 * @param value Is m_TheList multi-selectable or not.
 * @param n How many columns.
 * @param s[] The widths of each column.
 * @param ori The justification for the columns; RIGHT, LEFT, or CENTER.
 */
    public FixedList(int horz, boolean value, int n, int s[], int ori)
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

        addGhostEntry();

        setLayout(new GridLayout(1,1));
        add(m_TheList);
        }
/**
 * Process any ActionEvents.  All it does is foward the ActionEvent to any other
 * objects if anyone is listening.
 */
    public void actionPerformed(ActionEvent e) 
        {
        if (e.getSource() instanceof List)
            {
            List theSource = (List)e.getSource();

            if (m_actionListeners > 0)
                m_actionListener.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,e.getActionCommand()));
            }
        }

/**
 * Adds an entry into the m_TheList.  Checks to make sure the String array length is the
 * same size as the m_columns.  Otherwise it doesn't add the entry to the list.
 *
 * @param str[] The String array that makes up the item to be added.
 */
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
            }
        else
            {  
            Integer key = new Integer(m_TheList.getItemCount());   
            m_TheList.addItem(BuildListEntry(str));
            m_listItems.put(key,str);
            }
        }
/**
 * Adds an entry into the m_TheList.  Checks to make sure the String array length is the
 * same size as the m_columns.  Otherwise it doesn't add the entry to the list.
 *
 * @param str[] The String array that makes up the item to be added.
 * @param loc The location in the m_TheList to put the item.
 */
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
            }
        else 
            {
            if (m_GhostEntry)
                {
                replaceItem(str,0);
                }
            else
                {
                if ((loc > m_TheList.getItemCount()) || (loc < 0))
                    {
                    loc = m_TheList.getItemCount();
                    }
                for (int x=m_TheList.getItemCount()-1;x>=loc;x--)
                    {
                    Object obj = m_listItems.get(new Integer(x));
                    m_listItems.put(new Integer(x+1),obj);
                    }
                m_listItems.put(new Integer(loc),str);
                m_TheList.addItem(BuildListEntry(str),loc);
                }
            }   
        }
/**
 * Adds an ActionListener to the FixedList.  Any action events generated
 * by the m_TheList are forwarded through the ActionListner of the FixedList
 * to the approiate object(s).
 *
 * @param l The ActionListener that is being added.
 */
    public synchronized void addActionListener(ActionListener l)
        {
        if (m_actionListeners == 0)
            m_TheList.addActionListener(this);

        m_actionListener = AWTEventMulticaster.add(m_actionListener, l);
        m_actionListeners++;
        }
/**
 * Adds a blank entry to the m_TheList.  Done only if the list is empty and acts
 * merely as a place holder.
 */
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
/**
 * Adds an entry into the m_TheList.  Checks to make sure the String array length is the
 * same size as the m_columns.  Otherwise it doesn't add the entry to the list.
 *
 * @param str[] The String array that makes up the item to be added.
 */
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
            }
        else
            {  
            Integer key = new Integer(m_TheList.getItemCount());   
            m_TheList.addItem(BuildListEntry(str));
            m_listItems.put(key,str);
            }
        }
/**
 * Adds an entry into the m_TheList.  Checks to make sure the String array length is the
 * same size as the m_columns.  Otherwise it doesn't add the entry to the list.
 *
 * @param str[] The String array that makes up the item to be added.
 * @param loc The location in the m_TheList to put the item.
 */
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
            }
        else 
            {
            if (m_GhostEntry)
                {
                m_GhostEntry = false;
                replaceItem(str,0);
                m_listItems.put(new Integer(0),str);
                }
            else
                {
                if ((loc > m_TheList.getItemCount()) || (loc < 0))
                    {
                    loc = m_TheList.getItemCount();
                    }
                for (int x=m_TheList.getItemCount()-1;x>=loc;x--)
                    {
                    Object obj = m_listItems.get(new Integer(x));
                    m_listItems.put(new Integer(x+1),obj);
                    }
                m_listItems.put(new Integer(loc),str);

                m_TheList.addItem(BuildListEntry(str),loc);                
                }
            }   
        }
/**
 * Adds an ItemListener to the FixedList.  Any ItemEvents generated
 * by the m_TheList are forwarded through the ItemListner of the FixedList
 * to the approiate object(s).
 *
 * @param l The ItemListener that is being added.
 */
    public synchronized void addItemListener(ItemListener l)
        {
        if (m_itemListeners == 0)
            m_TheList.addItemListener(this);

        m_itemListener = AWTEventMulticaster.add(m_itemListener, l);
        m_itemListeners++;
        }

/**
 * Deslects an index of m_TheList.
 *
 * @param value The index of the list item to be deselected.
 */
    public void deselect(int value)
        {
        m_TheList.deselect(value);
        }

/**
 * Returns the item at that location in the list.
 *
 * @return Returns a String array containing the list item.
 * @param loc The index to get the item from.
 */
    public synchronized String[] getFixedItem(int loc)
        {
        if (m_GhostEntry)
            return null;
        return (String[])m_listItems.get(new Integer(loc));
        }
/**
 * Returns the number of items in the m_TheList.
 *
 * @return Returns the number of items in the m_TheList.
 */
    public int getItemCount()
        {
        return m_listItems.size();
        }
/**
 * Returns the index value of the presently selected index.
 *
 * @return Returns the index value of the presently selected index.
 */
    public synchronized int getSelectedIndex()
        {
        if (m_GhostEntry)
            return -1;
        return m_TheList.getSelectedIndex();
        }
/**
 * Returns the presently selected item in the form of an array of Strings.
 *
 * @return Returns the presently selected item.
 */
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
/**
 * Returns all the index values of any presently selected indexes.
 *
 * @return Returns all the index values of any presently selected indexes.
 */
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
/**
 * Returns the presently selected item in the form of a String as it is displayed
 * in the m_TheList.
 *
 * @return Returns the presently selected item.
 */
    public synchronized String getSelectedItem()
        {
        int loc = m_TheList.getSelectedIndex();
        String[] str = (String[])m_listItems.get(new Integer(loc));

        StringBuffer returnStr = new StringBuffer("");

        for (int x=0;x<str.length;x++)
            {
            returnStr.append(str[x]);
            }
        return returnStr.toString();
        }
/**
 * Returns all the selected items in the list as an array of Objects.
 * Each index of the array contains a String consiting of the String
 * array item appended together.
 *
 * @return Returns all the selected items in the list as an array of Objects.
 */
    public synchronized Object[] getSelectedObjects()
        {
        if (getItemCount() == 0)
            return new Object[0];

        Vector v = new Vector();
        for(int x=0;x<getItemCount();x++)
            {
            if (m_TheList.isIndexSelected(x))
                {
                String[] str = (String[])m_listItems.get(new Integer(x));
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
/**
 * Returns a specific column entry from the selected index in the list.
 *
 * @param column Which column entry to get.
 * @return Returns a specific column entry from the selected index in the list.
 */ 
    public synchronized String getSelectedSubItem(int column)
        { 
        int loc = m_TheList.getSelectedIndex();
        String[] str = (String[])m_listItems.get(new Integer(loc));
        
        return str[column];
        }

/**
 * Returns a specific column entry from an index in the list.
 *
 * @param loc Which index to get the column entry from.
 * @param column Which column entry to get.
 * @return Returns a specific column entry from an index in the list.
 */ 
    public String getSubItem(int loc, int column)
        {
        String[] str = (String[])m_listItems.get(new Integer(loc));
        
        return str[column];
        }

/**
 * Process any ItemEvents.  All it does is foward the ItemEvent to any other
 * objects if anyone is listening.
 */
    public void itemStateChanged(ItemEvent e)
        {

        if (e.getSource() instanceof List)
            {
            List theSource = (List)e.getSource();

            if (m_itemListeners > 0)
                m_itemListener.itemStateChanged(new ItemEvent(this,ItemEvent.ITEM_STATE_CHANGED,e.getItem(),e.getStateChange()));
            }
        }

/**
 * Replaces an index in the list with a new entry.
 *
 * @param str[] The new entry to be added.
 * @param loc The index to place the entry at.
 */
    public synchronized void replaceItem(String str[], int loc)
        {
        m_ReplaceEntry = true;

        if ((str.length != m_columns) || (loc >= m_TheList.getItemCount()))
            {
            /* Error, throw an exception */
            return;
            } 
        if (m_GhostEntry)
            {
            m_GhostEntry = false;
            m_TheList.remove(0);
            m_TheList.addItem(BuildListEntry(str),0);
            m_listItems.put(new Integer(0),str);
            }
        else
            { 
            m_TheList.replaceItem(BuildListEntry(str),loc);
            m_listItems.put(new Integer(loc),str);
            }
        m_ReplaceEntry = false;
        }

/**
 * Removes an entry from the list at the specified index.
 *
 * @param loc The index of the entry to be removed.
 */
    public synchronized void remove(int loc)
        {
        Hashtable tmp = new Hashtable();

        if ((!m_GhostEntry) && (loc < getItemCount()))
            {
            m_TheList.remove(loc);
            m_listItems.remove(new Integer(loc));
            if (!m_ReplaceEntry)
                {
                int x = m_listItems.size() + 1;
                for (int i=loc+1;i<x;i++)
                    {
                    Object obj = m_listItems.get(new Integer(i));
                    tmp.put(new Integer(i-1),obj);
                    m_listItems.remove(new Integer(i));
                    }
                for (int i=loc;i<x-1;i++)
                    {
                    Object obj = tmp.get(new Integer(i));
                    m_listItems.put(new Integer(i),obj);
                    tmp.remove(new Integer(i));
                    }
                }
            }
        if (m_listItems.size() == 0)
            addGhostEntry();
        }
/**
 * Removes a specific Object from listening for ActionEvents on the FixedList.
 *
 * @param l The ActionListener to be removed from m_actionListener.
 */
    public synchronized void removeActionListener(ActionListener l)
        {
        m_actionListener = AWTEventMulticaster.remove(m_actionListener, l);
        m_actionListeners--;

        if (m_actionListeners <= 0)
            m_TheList.removeActionListener(this);
        }
/**
 * Removes all items from the list.
 */
    public synchronized void removeAll()
        {
        if (m_GhostEntry)
            return;
        m_TheList.removeAll();
        m_listItems.clear();
        addGhostEntry();
        }
/**
 * Removes a specific Object from listening for ItemEvents on the FixedList.
 *
 * @param l The ItemListener to be removed from m_itemListener.
 */
    public synchronized void removeItemListener(ItemListener l)
        {
        m_itemListener = AWTEventMulticaster.remove(m_itemListener, l);
        m_itemListeners--;

        if (m_itemListeners <= 0)
            m_TheList.removeItemListener(this);
        }

/**
 * Change the display Font for m_TheList.
 *
 * @param m The new display Font for m_TheList.
 */
    public void setFont(Font m)
        {
        m_TheList.setFont(m);
        }
/**
 * Change the display area size requested by FixedList.
 *
 * @param d The new display area dimensions requested.
 */
    public void setSize(Dimension d)
        {
        super.setSize(d);
        m_TheList.setSize(d);
        }

/**
 * Selectes a specified index in the m_TheList.
 *
 * @param value The index to be selected.
 */
    public void select(int value)
        {
        m_TheList.select(value);
        }

/**
 * Turns the String array into the String entry for m_TheList.  If any String
 * in the array is too big for its column then it is truncated.  Extra spaces are
 * added depending on if the justification has been set to LEFT, RIGHT, or CENTER.
 *
 * @param str The String array that is to be formatted. 
 */
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
    }