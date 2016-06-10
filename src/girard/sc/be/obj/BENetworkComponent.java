package girard.sc.be.obj;

import girard.sc.exnet.obj.Network;
import girard.sc.exnet.obj.NetworkComponent;

/** 
 * Is the base object class for adding additional functionality 
 * to the objects contained within the Network object class.
 * <p>
 * Started: 4-30-2000
 * <p>
 *
 * @author Dudley Girard
 */

public abstract class BENetworkComponent extends NetworkComponent
    {
    protected double m_statePoint = -1;
    protected BENode m_node = null;
    protected BEEdge m_edge = null;

    public BENetworkComponent()
        {
        }
    public BENetworkComponent(double sp)
        {
        m_statePoint = sp;
        }
    public BENetworkComponent(double sp, String str)
        {
        super(str);
        m_statePoint = sp;
        }
    public BENetworkComponent(double sp, BENetwork en, String str)
        {
        super((Network)en,str);
        m_statePoint = sp;
        }
    public BENetworkComponent(BENetwork en)
        {
        super((Network)en);
        }
    public BENetworkComponent(BENetwork en, double sp)
        {
        super((Network)en);
        m_statePoint = sp;
        }
    public BENetworkComponent(BENetwork en, String n)
        {
        super((Network)en,n);
        }
    public BENetworkComponent(String n)
        {
        super(n);
        }

    public BEEdge getEdge()
        {
        return m_edge;
        }
    public BENode getNode()
        {
        return m_node;
        }
/**
 * @deprecated replaced by <code>getStateAction(BENetwork)</code>.
 */
    public BEStateAction getStateAction()
        {
        return null;
        }
    public BEStateAction getStateAction(BENetwork ben)
        {
        return getStateAction();
        }
/**
 * @deprecated replaced by <code>getStatePoint(BENetwork)</code>.
 */
    public double getStatePoint()
        {
        return m_statePoint;
        }
    public double getStatePoint(BENetwork ben)
        {
        return getStatePoint();
        }

    public void setEdge(BEEdge edge)
        {
        m_edge = edge;
        }
    public void setNode(BENode node)
        {
        m_node = node;
        }
    public void setStatePoint(double value)
        {
        m_statePoint = value;
        }
    }
