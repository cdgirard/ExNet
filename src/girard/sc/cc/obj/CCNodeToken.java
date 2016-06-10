package girard.sc.cc.obj;

/* 
   Is the object class for defining fuzzies for 
   a Node object in a CCNetwork.

   Author: Dudley Girard
   Started: 5-25-2001
   Modified: 7-16-2001
*/

import java.io.Serializable;
import java.util.Hashtable;

public class CCNodeToken implements Cloneable,Serializable 
    {
    protected int         m_toNode = -1;  // Who to send the token to.
    protected double      m_percent = 1; // How effective the token is.
    protected int         m_yesValue = 0; //  If the tokens do have an effect.
    protected int         m_noValue = 0; // If the tokens don't have an effect.

    protected boolean     m_sent = false; // Have I sent the token for this round yet.
    protected boolean     m_msg = false; // Did I send a token or not this round.
    protected int         m_tokens = 0; // How many tokens have I sent to this subject.

    public CCNodeToken()
        {
        }
    public CCNodeToken (int tn, double percent, int yes, int no)
        {
        m_toNode = tn;
        m_percent = percent;
        m_yesValue = yes;
        m_noValue = no;
        }

    public void applySettings(Hashtable h)
        {
        m_toNode = ((Integer)h.get("ToNode")).intValue();
        m_percent = ((Double)h.get("Percent")).doubleValue();
        m_yesValue = ((Integer)h.get("YesValue")).intValue();
        m_noValue = ((Integer)h.get("NoValue")).intValue();
        }

    public Object clone()
        {
        CCNodeToken token = new CCNodeToken(m_toNode,m_percent,m_yesValue,m_noValue);

        return token;
        }

    public boolean getMsg()
        {
        return m_msg;
        }
    public int getNoValue()
        {
        return m_noValue;
        }
    public double getPercent()
        {
        return m_percent;
        }
    public boolean getSent()
        {
        return m_sent;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();
 
        settings.put("ToNode",new Integer(m_toNode));
        settings.put("Percent",new Double(m_percent));
        settings.put("YesValue",new Integer(m_yesValue));
        settings.put("NoValue",new Integer(m_noValue));

        return settings;
        }
    public int getToNode()
        {
        return m_toNode;
        }
    public int getTokens()
        {
        return m_tokens;
        }
    public int getYesValue()
        {
        return m_yesValue;
        }

    public void setMsg(boolean value)
        {
        m_msg = value;
        }
    public void setNoValue(int value)
        {
        m_noValue = value;
        }
    public void setPercent(double value)
        {
        m_percent = value;
        }
    public void setSent(boolean value)
        {
        m_sent = value;
        }
    public void setTokens(int value)
        {
        m_tokens = value;
        }
    public void setYesValue(int value)
        {
        m_yesValue = value;
        }
    }
