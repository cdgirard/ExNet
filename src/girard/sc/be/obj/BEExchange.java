package girard.sc.be.obj;
/* 
   Is the object class for attaching exchanges to 
   a Edge object. Stored in the m_exchanges parameter.

   Author: Dudley Girard
   Started: 4-30-2000
*/

import java.io.Serializable;

public class BEExchange implements Cloneable,Serializable
    {
    int       m_TTime = -1;      // Ticker time when exchange completed
    long      m_RTime = -1;      // Time between ticks exchange was completed.
    BEResource  m_node1 = new BEResource();
    BEResource  m_node2 = new BEResource();

    public BEExchange ()
        {
        }
    public BEExchange(int n1, int n2)
        {
        m_node1.setResource(n1);
        m_node2.setResource(n2);
        }
    public BEExchange(int tt, long rt, int n1, int n2)
        {
        m_TTime = tt;
        m_RTime = rt;
        m_node1.setResource(n1);
        m_node2.setResource(n2);
        }
    
    public Object clone()
        {
        BEExchange tmp = new BEExchange();
        tmp.setTTime(m_TTime);
        tmp.setRTime(m_RTime);
        tmp.setNode1((BEResource)m_node1.clone());
        tmp.setNode2((BEResource)m_node2.clone());

        return tmp;
        }

    public BEResource getNode1()
        {
        return m_node1;
        }
    public BEResource getNode2()
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
    
    public void setNode1(BEResource ber)
        {
        m_node1 = ber;
        }
    public void setNode2(BEResource ber)
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
