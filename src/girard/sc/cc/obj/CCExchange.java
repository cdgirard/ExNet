package girard.sc.cc.obj;
/* 
   Is the object class for attaching exchanges to 
   a Edge object. Stored in the m_exchanges parameter.

   Author: Dudley Girard
   Started: 06-20-2000
*/

import java.io.Serializable;

public class CCExchange implements Cloneable,Serializable
    {
    public final static int NONE = 0;
    public final static int RED = 1;
    public final static int YELLOW = 2;
    public final static int GREEN = 3;
    public final static int COMPLETED = 4;

    protected int         m_TTime = -1;      // Ticker time when exchange completed
    protected long        m_RTime = -1;      // Time between ticks exchange was completed.
    protected int         m_exchangeState = NONE;
    protected CCResource  m_node1 = new CCResource();  // The node the completed exchange is attached to or who an offer was from.
    protected CCResource  m_node2 = new CCResource(); // The node the exchange was completed with or who the offer was to(which is the node the offer is attached to).

    public CCExchange ()
        {
        }
    public CCExchange(int n1, int n2)
        {
        m_node1.setResource(n1);
        m_node2.setResource(n2);
        }
    public CCExchange(int v1, int n1, int v2, int n2)
        {
        m_node1.setResource(v1);
        m_node1.setNode(n1);
        m_node2.setResource(v2);
        m_node2.setNode(n2);
        }
    public CCExchange(double v1, int n1, double v2, int n2)
        {
        m_node1.setResource(v1);
        m_node1.setNode(n1);
        m_node2.setResource(v2);
        m_node2.setNode(n2);
        }
    public CCExchange(int tt, long rt, int n1, int n2)
        {
        m_TTime = tt;
        m_RTime = rt;
        m_node1.setResource(n1);
        m_node2.setResource(n2);
        }
    public CCExchange(int tt, long rt, int v1, int n1, int v2, int n2)
        {
        m_TTime = tt;
        m_RTime = rt;
        m_node1.setResource(v1);
        m_node1.setNode(n1);
        m_node2.setResource(v2);
        m_node2.setNode(n2);
        }
    
    public Object clone()
        {
        CCExchange tmp = new CCExchange();
        tmp.setTTime(m_TTime);
        tmp.setRTime(m_RTime);
        tmp.setNode1((CCResource)m_node1.clone());
        tmp.setNode2((CCResource)m_node2.clone());

        return tmp;
        }

    public int getExchangeState()
        {
        return m_exchangeState;
        }
    public CCResource getNode1()
        {
        return m_node1;
        }
    public CCResource getNode2()
        {
        return m_node2;
        }
    public long getRTime()
        {
        return m_RTime;
        }
    public int getTTime()
        {
        return m_TTime;
        }
    
    public void setExchangeState(int value)
        {
        m_exchangeState = value;
        }
    public void setNode1(CCResource ber)
        {
        m_node1 = ber;
        }
    public void setNode2(CCResource ber)
        {
        m_node2 = ber;
        }
    public void setRTime(long value)
        {
        m_RTime = value;
        }
    public void setTTime(int value)
        {
        m_TTime = value;
        }
    }
