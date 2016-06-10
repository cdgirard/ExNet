package girard.sc.ce.obj;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Is the object class for representing commodities.
 * <p>
 * <br> Started: 07-23-2002
 * <br> Modified: 01-16-2003
 * <p>
 * @author Dudley Girard
 */


public class CEResource implements Serializable,Cloneable
    {
/**
 * Who the resource belongs to.
 */
    Hashtable    m_nodes = new Hashtable(); 
/**
 * The name of the resource.
 */
    String m_name = "Points"; 
/**
 * The label that is displayed to represent the resource.
 */
    String  m_label = new String("NONE");
/**
 * The amount of resource owned.
 */
    double m_resource = 24;
/**
 * Point value of the resource for that node.
 */
    double m_value = 1; 

    public CEResource ()
        {
        }
    public CEResource (String name, String label, int res, int value)
        {
        m_name = name;
        m_label = label;
        m_resource = res;
        m_value = value;
        }
    public CEResource (String name, String label, double res, double value)
        {
        m_name = name;
        m_label = label;
        m_resource = res;
        m_value = value;
        }
    public CEResource (int n, String name, String label, int res, int value)
        {
        m_nodes.put(""+n,new Integer(n));
        m_name = name;
        m_label = label;
        m_resource = res;
        m_value = value;
        }
    public CEResource (int n, String name, String label, double res, double value)
        {
        m_nodes.put(""+n,new Integer(n));
        m_name = name;
        m_label = label;
        m_resource = res;
        m_value = value;
        }
    public CEResource (Hashtable n, String name, String label, double res, double value)
        {
        m_nodes = n;
        m_name = name;
        m_label = label;
        m_resource = res;
        m_value = value;
        }

    public void applySettings(Hashtable h)
        {
        m_name = (String)h.get("Name");
        m_label = (String)h.get("Label");
        m_nodes = (Hashtable)h.get("Nodes");
        m_resource = ((Double)h.get("Resource")).doubleValue();
        m_value = ((Double)h.get("Value")).doubleValue();
        }

    public Object clone()
        {
        Hashtable h = new Hashtable();
        Enumeration enm = m_nodes.elements();
        while (enm.hasMoreElements())
            {
            Integer n = (Integer)enm.nextElement();
            h.put(n.toString(),new Integer(n.intValue()));
            }
        return new CEResource(h,new String(m_name),new String(m_label),m_resource,m_value);
        }

    public int getIntResource()
        {
        return (int)Math.round(m_resource);
        }
    public int getIntValue()
        {
        return (int)Math.round(m_value);
        }
    public String getLabel()
        {
        return m_label;
        }
    public String getName()
        {
        return m_name;
        }
    public int getNode(int n)
        {
        if (m_nodes.containsKey(""+n))
            {
            return ((Integer)m_nodes.get(""+n)).intValue();
            }
        return -1;
        }
    public Hashtable getNodes()
        {
        return m_nodes;
        }
    public double getProfit()
        {
        return m_resource*m_value;
        }
    public double getResource()
        {
        return m_resource;
        }
    public double getValue()
        {
        return m_value;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();

        settings.put("Name",m_name);
        settings.put("Label",m_label);
        settings.put("Nodes",m_nodes);
        settings.put("Resource",new Double(m_resource));
        settings.put("Value",new Double(m_value));

        return settings;
        }
    
    public void removeNode(int value)
        {
        if (m_nodes.containsKey(""+value))
            {
            m_nodes.remove(""+value);
            }
        }

    public void setLabel(String label)
        {
        m_label = label;
        }
    public void setName(String name)
        {
        m_name = name;
        }
    public void setNode(int value)
        {
        m_nodes.put(""+value,new Integer(value));
        }
    public void setResource(int value)
        {
        m_resource = value;
        }
    public void setResource(double value)
        {
        m_resource = value;
        }
    public void setValue(int value)
        {
        m_value = value;
        }
    public void setValue(double value)
        {
        m_value = value;
        }
    }