package girard.sc.cc.obj;
/* 
   Is the object class for defining fuzzies for 
   a Node object in a CCNetwork.

   Author: Dudley Girard
   Started: 5-25-2001
*/

import java.io.Serializable;
import java.util.Hashtable;

public class CCNodeFuzzy implements Cloneable,Serializable 
    {
    protected int         m_aboutNode = -1; // Who the fuzzy is about.
    protected int         m_toNode = -1;  // Who to send the fuzzy to.

    protected boolean     m_sent = false; // Have I sent off the fuzzy.
    protected boolean     m_msg = false; // true = good fuzzy, false = bad fuzzy.

    public CCNodeFuzzy()
        {
        }
    public CCNodeFuzzy (int an, int tn)
        {
        m_aboutNode = an;
        m_toNode = tn;
        }

    public void applySettings(Hashtable h)
        {
        m_aboutNode = ((Integer)h.get("AboutNode")).intValue();
        m_toNode = ((Integer)h.get("ToNode")).intValue();
        }

    public Object clone()
        {
        CCNodeFuzzy fuzzy = new CCNodeFuzzy(m_aboutNode,m_toNode);

        return fuzzy;
        }

    public int getAboutNode()
        {
        return m_aboutNode;
        }
    public boolean getMsg()
        {
        return m_msg;
        }
    public boolean getSent()
        {
        return m_sent;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();

        settings.put("AboutNode",new Integer(m_aboutNode));
        settings.put("ToNode",new Integer(m_toNode));

        return settings;
        }
    public int getToNode()
        {
        return m_toNode;
        }

    public void setMsg(boolean value)
        {
        m_msg = value;
        }
    public void setSent(boolean value)
        {
        m_sent = value;
        }
    }
