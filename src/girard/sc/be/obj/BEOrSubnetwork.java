package girard.sc.be.obj;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/** 
 * BEOrSubnetwork: A subnetwork of exchanges defined for a node.
 * <p>
 * <br>Started: 09-17-2002
 * <p>
 * @author Dudley Girard
 */


public class BEOrSubnetwork implements Cloneable,Serializable
    {
    protected int       m_subnetwork = -1;
    protected int       m_Max = 1;
    protected int       m_Min = 1;
    protected int       m_exchanges = 0;
    protected int[]     m_nodes;
    protected Vector    m_ordering = new Vector();
    protected boolean   m_active = false;
    protected Hashtable m_subnetworks = null;

    public BEOrSubnetwork ()
        {
        }
    public BEOrSubnetwork (int subnetwork, int node)
        {
        m_subnetwork = subnetwork;
        m_nodes = new int[1];
        m_nodes[0] = node;
        }
    public BEOrSubnetwork (int subnetwork, int[] nodes)
        {
        m_subnetwork = subnetwork;
        m_nodes = nodes;
        }

    public void addOrdering(Vector v)
        {
        m_ordering.addElement(v);
        }
    public void addSubnetwork(BEOrSubnetwork sn)
        {
        if (m_subnetworks == null)
            m_subnetworks = new Hashtable();

        m_subnetworks.put(new Integer(sn.getSubnetwork()),sn);
        Vector v = new Vector();
        v.addElement(new Integer(-1));
        m_ordering.addElement(v);
        }
    public void addSubnetwork(int[] nodes)
        {
        if (m_subnetworks == null)
            m_subnetworks = new Hashtable();

        int index = m_subnetworks.size();
        m_subnetworks.put(new Integer(index),new BEOrSubnetwork(index,nodes));
        Vector v = new Vector();
        v.addElement(new Integer(-1));
        m_ordering.addElement(v);
        }

    public void applySettings(Hashtable h)
        {
        m_subnetwork = ((Integer)h.get("Subnetwork")).intValue();
        m_Min = ((Integer)h.get("Min")).intValue();
        m_Max = ((Integer)h.get("Max")).intValue();
        m_exchanges = ((Integer)h.get("Exchanges")).intValue();
        m_nodes = (int[])h.get("Nodes");
        m_active = ((Boolean)h.get("Active")).booleanValue();

        if (h.get("Subnetworks") instanceof Vector)
            {
            Vector subnetworks = (Vector)h.get("Subnetworks");
            Enumeration enm = subnetworks.elements();
            while (enm.hasMoreElements())
                {
                Hashtable data = (Hashtable)enm.nextElement();
                BEOrSubnetwork sn = new BEOrSubnetwork();
                sn.applySettings(data);
                addSubnetwork(sn);
                }
            }
        m_ordering = (Vector)h.get("Ordering");
        }

    public void cleanUp()
        {
        m_exchanges = 0;
        m_Max = 1;
        m_Min = 1;
        if (m_subnetworks != null)
            {
            Enumeration enm = m_subnetworks.keys();
            while (enm.hasMoreElements())
                {
                Object obj = enm.nextElement();
                if (obj instanceof Integer)
                    {
                    BEOrSubnetwork sn = (BEOrSubnetwork)m_subnetworks.get(obj);
                    sn.cleanUp();
                    }
                }
            m_ordering.removeAllElements();
            m_subnetworks.clear();
            m_subnetworks = null;
            }
        }

    public Object clone()
        {
        int[] nodes = new int[m_nodes.length];
        for (int x=0;x<nodes.length;x++)
            {
            nodes[x] = m_nodes[x];
            }
        BEOrSubnetwork sn = new BEOrSubnetwork(m_subnetwork,nodes);
        if (m_subnetworks != null)
            {
            Enumeration enm = m_subnetworks.keys();
            while (enm.hasMoreElements())
                {
                Object obj = enm.nextElement();
                if (obj instanceof Integer)
                    {
                    BEOrSubnetwork sn2 = (BEOrSubnetwork)m_subnetworks.get(obj);
                    sn.addSubnetwork((BEOrSubnetwork)sn2.clone());
                    }
                }
            }

        Vector ordering = new Vector();
        Enumeration enm = m_ordering.elements();
        while (enm.hasMoreElements())
            {
            Vector orderSet = new Vector();
            Vector v = (Vector)enm.nextElement();
            Enumeration enum2 = v.elements();
            while (enum2.hasMoreElements())
                {
                Integer nn = (Integer)enum2.nextElement();
                orderSet.addElement(new Integer(nn.intValue()));
                }
            ordering.addElement(orderSet);
            }
        sn.setOrdering(ordering);

        sn.setMin(m_Min);
        sn.setMax(m_Max);
        sn.setExchanges(m_exchanges);
        sn.setActive(m_active);
        return sn;
        }

    public boolean containsNode(int value)
        {
        for (int x=0;x<m_nodes.length;x++)
            {
            if (m_nodes[x] == value)
                return true;
            }
        return false;
        }

    public boolean getActive()
        {
        return m_active;
        }
    public int getExchanges()
        {
        return m_exchanges;
        }
    public int getExchangeType(int node)
        {
        if (m_subnetworks == null)
            {
            int max = m_nodes.length;
            if ((max == 1) && (m_Min == 1))
                return 7; // Single Connected
            else if ((m_Max == max) && (m_Min == 1))
                return 3; // Null
            else if ((m_Max == max) && (m_Min > 1) && (m_Min != m_Max))
                return 6; // Null-Inclusive
            else if ((m_Max < max) && (m_Min > 1))
                return 5; // exlusive and inclusive
            else if ((m_Max < max) && (m_Min == 1))
                return 4; // exclusive
            else
                return 2; // must be inclusive.
            }
        else
            {
            Enumeration enm = m_subnetworks.elements();
            while (enm.hasMoreElements())
                {
                BEOrSubnetwork sn = (BEOrSubnetwork)enm.nextElement();
                if (sn.containsNode(node))
                    {
                    if (sn.getActive())
                        {
                        return sn.getExchangeType(node);
                        }
                    else
                        {
                        int max = m_subnetworks.size();
                        if ((max == 1) && (m_Min == 1))
                            return 7; // Single Connected
                        else if ((m_Max == max) && (m_Min == 1))
                            return 3; // Null
                        else if ((m_Max == max) && (m_Min > 1) && (m_Min != m_Max))
                            return 6; // Null
                        else if ((m_Max < max) && (m_Min > 1))
                            return 5; // exlusive and inclusive
                        else if ((m_Max < max) && (m_Min == 1))
                            return 4; // exclusive
                        else
                            return 2; // must be inclusive.
                        }
                    }
                }
            }
        return 0; // Should not normally get called.
        }
    public int getMax()
        {
        return m_Max;
        }
    public int getMin()
        {
        return m_Min;
        }
    public int[] getNodes()
        {
        return m_nodes;
        }
    public Vector getOrdering()
        {
        return m_ordering;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();
 
        settings.put("Subnetwork",new Integer(m_subnetwork));
        settings.put("Min",new Integer(m_Min));
        settings.put("Max",new Integer(m_Max));
        settings.put("Exchanges",new Integer(m_exchanges));
        settings.put("Nodes",m_nodes);
        settings.put("Active",new Boolean(m_active));
        
        if (m_subnetworks == null)
            {
            settings.put("Subnetworks","None");
            settings.put("Ordering",m_ordering);
            }
        else
            {
            Vector subnetworks = new Vector();
            Enumeration enm = m_subnetworks.elements();
            while (enm.hasMoreElements())
                {
                BEOrSubnetwork sn = (BEOrSubnetwork)enm.nextElement();
                subnetworks.addElement(sn.getSettings());
                }
            settings.put("Subnetworks",subnetworks);
            settings.put("Ordering",m_ordering);
            }
        
        return settings;
        }
    public int getSubnetwork()
        {
        return m_subnetwork;
        }
    public String getSubnetworkID(int node, String str)
        {
        if (m_subnetworks == null)
            {
            return str;
            }
        else
            {
            Enumeration enm = m_subnetworks.elements();
            while (enm.hasMoreElements())
                {
                BEOrSubnetwork sn = (BEOrSubnetwork)enm.nextElement();
                if (sn.containsNode(node))
                    {
                    if (sn.getSubnetworks() == null)
                        {
                        return str;
                        }
                    else if (sn.getActive())
                        {
                        // str = new String(str+"."+sn.getSubnetwork());
                        int sub = sn.getSubnetwork() + 1;
                        str = new String(""+sub);
                        return sn.getSubnetworkID(node,str);
                        }
                    else
                        {
                        // return new String(str+"."+sn.getSubnetwork());
                        int sub = sn.getSubnetwork() + 1;
                        return new String(""+sub);
                        }
                    }
                }
            }
        return str; // Should not normally get called.
        }
    public BEOrSubnetwork getSubnetwork(int value)
        {
        return (BEOrSubnetwork)m_subnetworks.get(new Integer(value));
        }
    public Hashtable getSubnetworks()
        {
        return m_subnetworks;
        }
    public boolean getToKeep(int toNode)
        {
        if (m_exchanges >= m_Min)
            {
            if (m_subnetworks != null)
                {
                Enumeration enm = m_subnetworks.keys();
                while (enm.hasMoreElements())
                    {
                    Object obj = enm.nextElement();
                    if (obj instanceof Integer)
                        {
                        BEOrSubnetwork sn = (BEOrSubnetwork)m_subnetworks.get(obj);
                        if (sn.containsNode(toNode))
                            {
                            return sn.getToKeep(toNode);
                            }
                        }
                    }
                return true; // This should not get called.
                }
            else
                {
                return true; // This could get called.
                }
            }
        else
            {
            return false;
            }
        }

    public void initializeNetwork()
        {
        m_exchanges = 0;
        m_active = false;
        if (m_subnetworks != null)
            {
            Enumeration enm = m_subnetworks.keys();
            while (enm.hasMoreElements())
                {
                Object obj = enm.nextElement();
                if (obj instanceof Integer)
                    {
                    BEOrSubnetwork sn = (BEOrSubnetwork)m_subnetworks.get(obj);
                    sn.initializeNetwork();
                    }
                }
            }
        }
    public void initializeSubnetworks()
        {
        m_subnetworks = new Hashtable();
        for (int i=0;i<m_nodes.length;i++)
            {
            m_subnetworks.put(new Integer(i),new BEOrSubnetwork(i,m_nodes[i]));
            Vector v = new Vector();
            v.addElement(new Integer(-1));
            m_ordering.addElement(v);
            }
        }

    public boolean isEdgeActive(int node)
        {
        if (m_subnetworks == null)
            {
            if (m_exchanges == m_Max)
                return false;
            else
                return true;
            }
        else
            {
            Enumeration enm = m_subnetworks.elements();
            while (enm.hasMoreElements())
                {
                BEOrSubnetwork sn = (BEOrSubnetwork)enm.nextElement();
                if (sn.containsNode(node))
                    {
                    if (sn.getActive())
                        {
                        return sn.isEdgeActive(node);
                        }
                    else
                        {
                        if (m_exchanges == m_Max)
                            return false;
                        else
                            {
                            Vector v = (Vector)m_ordering.elementAt(m_exchanges);
                            if (v.contains(new Integer(-1)))
                                return true;
                            if (v.contains(new Integer(node)))
                                return true;
                            return false;
                            }
                        }
                    }
                }
            }
        return true; // Should not normally get called.
        }

    public void setActive(boolean value)
        {
        m_active = true;
        }
    public void setExchanges(int value)
        {
        m_exchanges = value;
        }
    public void setMax(int value)
        {
        m_Max = value;
        }
    public void setMin(int value)
        {
        m_Min = value;
        }
    protected void setOrdering(Vector v)
        {
        m_ordering = v;
        }

  // Update network data with the idea an exchange just took place with this node.
    public void updateNetwork(int toNode) 
        {
        if (m_subnetworks == null)
            {
            m_exchanges++;
            return;
            }
        Enumeration enm = m_subnetworks.keys();
        while (enm.hasMoreElements())
            {
            Object obj = enm.nextElement();
            if (obj instanceof Integer)
                {
                BEOrSubnetwork sn = (BEOrSubnetwork)m_subnetworks.get(obj);
                if (sn.containsNode(toNode))
                    {
                    if (sn.getActive())
                        {
                        sn.updateNetwork(toNode);
                        }
                    else
                        {
                        m_exchanges++;
                        sn.setActive(true);
                        sn.updateNetwork(toNode);
                        }
                    return;
                    }
                }
            }
        }
    }
