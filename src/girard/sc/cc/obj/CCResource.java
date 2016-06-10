package girard.sc.cc.obj;
/* 
   Is the object class for attaching resources to 
   an object.

   Author: Dudley Girard
   Started: 06-20-2001
*/

import java.io.Serializable;
import java.util.Hashtable;

public class CCResource implements Serializable,Cloneable
    {
    int    m_node = -1; // Who got the resource.
    String m_name = "Points"; // The name of the resource.
    double m_resource = 24; // The amount of resource received.

    public CCResource ()
        {
        }
    public CCResource (String name)
        {
        m_name = name;
        }
    public CCResource (int value)
        {
        m_resource = value;
        }
    public CCResource (double value)
        {
        m_resource = value;
        }
    public CCResource (int value, int n)
        {
        m_resource = value;
        m_node = n;
        }
    public CCResource (double value, int n)
        {
        m_resource = value;
        m_node = n;
        }
    public CCResource (String name, int value)
        {
        m_name = name;
        m_resource = value;
        }
    public CCResource (String name, double value)  
        {
        m_name = name;
        m_resource = value;
        }
    public CCResource (String name, int value, int n)
        {
        m_name = name;
        m_resource = value;
        m_node = n;
        }
    public CCResource (String name, double value, int n)
        {
        m_name = name;
        m_resource = value;
        m_node = n;
        }

    public void applySettings(Hashtable h)
        {
        m_name = (String)h.get("Name");
        m_node = ((Integer)h.get("Node")).intValue();
        m_resource = ((Double)h.get("Resource")).doubleValue();
        }

    public Object clone()
        {
        return new CCResource(new String(m_name),m_resource);
        }

    public int getIntResource()
        {
        return (int)Math.round(m_resource);
        }
    public String getName()
        {
        return m_name;
        }
    public int getNode()
        {
        return m_node;
        }
    public double getResource()
        {
        return m_resource;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();

        settings.put("Name",m_name);
        settings.put("Node",new Integer(m_node));
        settings.put("Resource",new Double(m_resource));

        return settings;
        }
    
    public void setName(String name)
        {
        m_name = name;
        }
    public void setNode(int value)
        {
        m_node = value;
        }
    public void setResource(int value)
        {
        m_resource = value;
        }
    public void setResource(double value)
        {
        m_resource = value;
        }
    }