package girard.sc.exnet.obj;

import java.awt.Point;
import java.io.Serializable;
import java.util.Hashtable;

public class Edge implements Serializable,Cloneable
    {
    protected int       m_node1;
    protected int       m_node2;
    protected Point     m_n1Anchor = new Point(0,0);     /* Should be the location for node 1 */
    protected Point     m_n2Anchor = new Point(0,0);     /* Should be the location for node 2 */

    public Edge ()
        {
        m_node1 = -1;
        m_node2 = -1;
        }
    public Edge (int NT1, int NT2, Point p1, Point p2)
        {
        m_node1 = NT1;
        m_node2 = NT2;
        m_n1Anchor = p1;
        m_n2Anchor = p2;
        }

    public void applySettings(Hashtable h)
        {
        m_node1 = ((Integer)h.get("Node1")).intValue();
        m_node2 = ((Integer)h.get("Node2")).intValue();
        m_n1Anchor = (Point)h.get("N1Anchor");
        m_n2Anchor = (Point)h.get("N2Anchor");
        }

    public Object clone()
        {
        return new Edge(m_node1,m_node2,new Point(m_n1Anchor.x,m_n1Anchor.y),new Point(m_n2Anchor.x,m_n2Anchor.y));
        }

    public Point getN1Anchor()
        {
        return m_n1Anchor;
        }
    public Point getN2Anchor()
        {
        return m_n2Anchor;
        }
    public Point getMidAnchor()
        {
        Point p = new Point();
 
        p.x = (m_n1Anchor.x + m_n2Anchor.x)/2;
        p.y = (m_n1Anchor.y + m_n2Anchor.y)/2;
 
        return p;
        }
    public int getNode1()
        {
        return m_node1;
        }
    public int getNode2()
        {
        return m_node2;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();
 
        settings.put("Node1",new Integer(m_node1));
        settings.put("Node2",new Integer(m_node2));
        settings.put("N1Anchor",m_n1Anchor);
        settings.put("N2Anchor",m_n2Anchor);

        return settings;
        }

    public void setN1Anchor(Point p)
        {
        m_n1Anchor = new Point(p.x,p.y);
        }
    public void setN1Anchor(int x, int y)
        {
        m_n1Anchor = new Point(x,y);
        }
    public void setN2Anchor(Point p)
        {
        m_n2Anchor = new Point(p.x,p.y);
        }
    public void setN2Anchor(int x, int y)
        {
        m_n2Anchor = new Point(x,y);
        }
    public void setNode1(int NT1)
        {
        m_node1 = NT1;
        }
    public void setNode2(int NT2)
        {
        m_node2 = NT2;
        }

    public String toString()
        {
        String str1 = new String(""+m_node1+", "+m_node2);

        return str1;    
        }
    }
