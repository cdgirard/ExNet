package girard.sc.exnet.obj;

import girard.sc.exnet.awt.NetworkBuilderWindow;
import girard.sc.expt.web.ExptOverlord;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class NetworkBuilder
    {
    ExptOverlord m_EOApp;
    NetworkBuilderWindow m_NBWApp;

    public static final int WIDTH = 1000;
    public static final int HEIGHT = 1000;

    Network  m_activeNetwork = new Network();

    public String m_UserName;
  
    public NetworkBuilder(ExptOverlord app, NetworkBuilderWindow nbw)
        {
        m_EOApp = app;
        m_NBWApp = nbw;
        }
    
    public void addEdge(Edge Etemp)
        {
        m_activeNetwork.addEdge(Etemp);
        }
    public void addNode(Node Ntemp)
        {
        m_activeNetwork.addNode(Ntemp);
        }

  /* This checks to make sure all the nodes have at least one edge
     connected to them. */

    public boolean Connected()
        {
        Node Ntemp;
        Edge Etemp;

        Enumeration en = m_activeNetwork.getNodeList().elements();
        while (en.hasMoreElements()) 
            {
            Ntemp = (Node)en.nextElement();
            boolean flag = true;
            Enumeration ee = m_activeNetwork.getEdgeList().elements();
            while (ee.hasMoreElements())
                {
                Etemp = (Edge)ee.nextElement();
                if ((Ntemp.getID() == Etemp.getNode1()) || (Ntemp.getID() == Etemp.getNode2()))
                    {
                    flag = false;
                    break;
                    }
                }
            if (flag)
                return false;
            }
        return true;
        }

    public Network getActiveNetwork()
        {
        return m_activeNetwork;
        }
    public Vector getEdgeList()
        {
        return m_activeNetwork.getEdgeList();
        }
    public ExptOverlord getEO()
        {
        return m_EOApp;
        }
    public NetworkBuilderWindow getNBW()
        {
        return m_NBWApp;
        }
    public Hashtable getNodeList()
        {
        return m_activeNetwork.getNodeList();
        }
    public int getNumEdges()
        {
        return m_activeNetwork.getEdgeList().size();
        }
    public int getNumNodes()
        {
        return m_activeNetwork.getNodeList().size();
        }
    
    public void removeEdge(Edge Etemp)
        {
        m_activeNetwork.removeEdge(Etemp);
        }
    public void removeNode(Node Ntemp)
        {
        m_activeNetwork.removeNode(Ntemp);
        }

   public void reset()
       {
       Enumeration enm = m_activeNetwork.getNodeList().elements();
       while(enm.hasMoreElements())
           {
           Node n = (Node)enm.nextElement();
           m_activeNetwork.removeNode(n);
           }
       m_activeNetwork.setCounter(1);
       }

    public void setActiveNetwork(Network en)
        {
        m_activeNetwork = en;
        }

    public boolean validNetwork()
        {
        if (getNumNodes() > 1)
            {
            if (Connected())
                {
                return true;
                }
            }
        return false;
        }
    }
