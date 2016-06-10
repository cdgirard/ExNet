package girard.sc.cc.obj;

import girard.sc.exnet.obj.Network;
import girard.sc.exnet.obj.NetworkComponent;

/** 
 * Is the base object class for adding additional functionality 
 * to the objects contained within the Network object class.
 * <p>
 * <br> Started: 5-25-2001
 * <br> Modified: 10-22-2002
 * <p>
 * @author Dudley Girard
 */


public abstract class CCNetworkComponent extends NetworkComponent
    {
    protected double m_statePoint = -1;
    protected CCEdge m_edge = null;
    protected CCNode m_node = null;

    public CCNetworkComponent()
        {
        }
    public CCNetworkComponent(double sp)
        {
        m_statePoint = sp;
        }
    public CCNetworkComponent(double sp, CCNetwork en)
        {
        super(en);
        m_statePoint = sp;
        }
    public CCNetworkComponent(double sp, String n)
        {
        super(n);
        m_statePoint = sp;
        }
    public CCNetworkComponent(double sp, CCNetwork en, String n)
        {
        super(en,n);
        m_statePoint = sp;
        }
    public CCNetworkComponent(CCNetwork en)
        {
        super((Network)en);
        }
    public CCNetworkComponent(CCNetwork en, String n)
        {
        super((Network)en,n);
        }
    public CCNetworkComponent(String n)
        {
        super(n);
        }

    public CCEdge getEdge()
        {
        return m_edge;
        }
    public CCNode getNode()
        {
        return m_node;
        }
    public CCStateAction getStateAction()
        {
        return null;
        }
    public double getStatePoint()
        {
        return m_statePoint;
        }

    public abstract void initializeStart();  // To be used at the begining of an experiment or period.

    public void setEdge(CCEdge edge)
        {
        m_edge = edge;
        }
    public void setNode(CCNode node)
        {
        m_node = node;
        }
    public void setStatePoint(double value)
        {
        m_statePoint = value;
        }
    }
