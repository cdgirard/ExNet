package girard.sc.be.obj;
/* 
   Is the object class for attaching resources to 
   an object.

   Author: Dudley Girard
   Started: 4-30-2000
   Editted: 8-19-2000
*/

import java.io.Serializable;
import java.util.Hashtable;

public class BEResource implements Serializable,Cloneable
    {
    String m_name = "Points";
    double m_resource = 24;

    public BEResource ()
        {
        }
    public BEResource (String name)
        {
        m_name = name;
        }
    public BEResource (int value)
        {
        m_resource = value;
        }
    public BEResource (double value)
        {
        m_resource = value;
        }
    public BEResource (String name, int value)
        {
        m_name = name;
        m_resource = value;
        }
    public BEResource (String name, double value)  
        {
        m_name = name;
        m_resource = value;
        }

    public void applySettings(Hashtable h)
        {
        m_name = (String)h.get("Name");
        m_resource = ((Double)h.get("Resource")).doubleValue();
        }

    public Object clone()
        {
        return new BEResource(new String(m_name),m_resource);
        }

    public int getIntResource()
        {
        return (int)Math.round(m_resource);
        }
    public String getName()
        {
        return m_name;
        }
    public double getResource()
        {
        return m_resource;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();

        settings.put("Name",m_name);
        settings.put("Resource",new Double(m_resource));

        return settings;
        }
    
    public void setName(String name)
        {
        m_name = name;
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
