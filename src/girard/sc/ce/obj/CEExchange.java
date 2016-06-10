package girard.sc.ce.obj;

import java.io.Serializable;

/**
 * Is the object class for attaching exchanges to 
 * a Edge object. Stored in the m_exchanges parameter.
 * <p>
 * <br> Started: 02-01-2003
 * <br> Modified: 02-12-2003
 * <p>
 * @author Dudley Girard
 */

public class CEExchange implements Cloneable,Serializable
    {
    public final static int NONE = 0;
    public final static int RED = 1;
    public final static int YELLOW = 2;
    public final static int GREEN = 3;
    public final static int COMPLETED = 4;

    protected int         m_TTime = -1;      // Ticker time when exchange completed
    protected long        m_RTime = -1;      // Time between ticks exchange was completed.
    protected int         m_exchangeState = NONE;
/**
 * How much node1 is getting, based on who the offer is from's utility system.
 */
    protected CEResource  m_node1 = new CEResource();
/**
 * How much node2 is getting, based on who the offer is from's utility system.
 */
    protected CEResource  m_node2 = new CEResource();

    public CEExchange ()
        {
        }
    public CEExchange(CEResource cer1, CEResource cer2)
        {
        m_node1 = cer1;
        m_node2 = cer2;
        }
    public CEExchange(int tt, long rt, CEResource cer1, CEResource cer2)
        {
        m_TTime = tt;
        m_RTime = rt;
        m_node1 = cer1;
        m_node2 = cer2;
        }
    
    public Object clone()
        {
        CEExchange tmp = new CEExchange();
        tmp.setTTime(m_TTime);
        tmp.setRTime(m_RTime);
        tmp.setNode1((CEResource)m_node1.clone());
        tmp.setNode2((CEResource)m_node2.clone());

        return tmp;
        }

    public boolean equals(CEExchange cee)
        {
        if (cee.getNode1().getResource() == m_node1.getResource())
            {

            if (cee.getNode1().getLabel().equals(m_node1.getLabel()))
                {
                if (cee.getNode2().getResource() == m_node2.getResource())
                    {
                    if (cee.getNode2().getLabel().equals(m_node2.getLabel()))
                        {
                        return true;
                        }
                    }
                }
            }
        return false;
        }

    public int getExchangeState()
        {
        return m_exchangeState;
        }
    public CEResource getNode1()
        {
        return m_node1;
        }
    public CEResource getNode2()
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

/**
 * Remember that what node 1 is getting is being provided from node 2's resources.
 */
    public boolean isValidExchange(CENode n1, CENode n2)
        {
        CENodeResource n1Res = (CENodeResource)n1.getExptData("CENodeResource");
        CEResource n1Cer = n1Res.getAvailableResources(m_node2.getLabel());
        if (n1Cer.getResource() < m_node2.getResource())
            return false;

        CENodeResource n2Res = (CENodeResource)n2.getExptData("CENodeResource");
        CEResource n2Cer = n2Res.getAvailableResources(m_node1.getLabel());
        if (n2Cer.getResource() < m_node1.getResource())
            return false;

        return true;
        }

    public void setExchangeState(int value)
        {
        m_exchangeState = value;
        }
    public void setNode1(CEResource cer)
        {
        m_node1 = cer;
        }
    public void setNode2(CEResource cer)
        {
        m_node2 = cer;
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