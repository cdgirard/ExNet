package girard.sc.exnet.obj;

import java.awt.Point;
import java.io.Serializable;
import java.util.Hashtable;

public class Node implements Serializable,Cloneable
    {
    protected int 	 m_id; /* Unique identifier of the node, was name */
    protected String	 m_label; /* Label for the node */
    protected Point      m_loc; /* Was xpos and ypos */

    public Node ()
        {
        m_id = -1;
        m_label = new String("");
        m_loc = new Point(-1,-1);
        }
    public Node (int ident,String lett, Point p)
        {
        m_id = ident;
        m_label = lett;
        m_loc = p;
        }
    public Node (String lett, Point p)
        {
        m_id = -1;
        m_label = lett;
        m_loc = p;
        }

    public void applySettings(Hashtable h)
        {
        m_id = ((Integer)h.get("ID")).intValue();
        m_label = (String)h.get("Label");
        m_loc = (Point)h.get("Loc");
        }

    public Object clone()
        {
        return new Node(m_id,new String(m_label),new Point(m_loc.x,m_loc.y));
        }

    public String getLabel()
        {
        return m_label;
        }
    public int getID()
        {
        return m_id;
        }
    public Point getLoc()
        {
        return m_loc;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();

        settings.put("ID",new Integer(m_id));
        settings.put("Label",m_label);
        settings.put("Loc",m_loc);

        return settings;
        }
    public int getXpos()
        {
        return m_loc.x;
        }
    public int getYpos()
        {
        return m_loc.y;
        }


    public void setLabel(char lett)
        {
        m_label = new String(""+lett);
        }
    public void setLabel(String lett)
        {
        m_label = lett;
        }
    public void setID(int ident)
        {
        m_id = ident;
        }
    public void setLoc(int x, int y)
        {
        m_loc.x = x;
        m_loc.y = y;
        }
    public void setLoc(Point p)
        {
        m_loc = p;
        }
    public void setXpos(int value)
        {
        m_loc.x = value;
        }
    public void setYpos(int value)
        { 
        m_loc.y = value;
        }

    public String toString()
        {
        String str1 = new String(""+m_id+", '"+m_label+"' , "+m_loc.x+", "+m_loc.y);

        return str1;    
        }

    }
