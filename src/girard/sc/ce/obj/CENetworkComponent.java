package girard.sc.ce.obj;

import girard.sc.exnet.obj.Network;
import girard.sc.exnet.obj.NetworkComponent;

/* 
   Is the base object class for adding additional functionality 
   to the objects contained within the Network object class.

   Author: Dudley Girard
   Started: 07-23-2002
*/

public abstract class CENetworkComponent extends NetworkComponent
    {
    protected double m_statePoint = -1;
    protected CENode m_node = null;
    protected CEEdge m_edge = null;

    public CENetworkComponent()
        {
        }
    public CENetworkComponent(double sp)
        {
        m_statePoint = sp;
        }
    public CENetworkComponent(double sp, CENetwork en)
        {
        super(en);
        m_statePoint = sp;
        }
    public CENetworkComponent(double sp, String n)
        {
        super(n);
        m_statePoint = sp;
        }
    public CENetworkComponent(double sp, CENetwork en, String n)
        {
        super(en,n);
        m_statePoint = sp;
        }
    public CENetworkComponent(CENetwork en)
        {
        super((Network)en);
        }
    public CENetworkComponent(CENetwork en, String n)
        {
        super((Network)en,n);
        }
    public CENetworkComponent(String n)
        {
        super(n);
        }

    public CEStateAction getStateAction()
        {
        return null;
        }
    public double getStatePoint()
        {
        return m_statePoint;
        }
/**
 * To be used at the begining of an experiment or period.
 */
    public abstract void initializeStart();  

    public void setEdge(CEEdge e)
        {
        m_edge = e;
        }
    public void setNode(CENode n)
        {
        m_node = n;
        }
    public void setStatePoint(double value)
        {
        m_statePoint = value;
        }
    }
