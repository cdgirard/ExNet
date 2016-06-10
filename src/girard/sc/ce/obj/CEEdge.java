package girard.sc.ce.obj;

import girard.sc.ce.awt.CEColor;
import girard.sc.ce.awt.CENetworkActionClientWindow;
import girard.sc.ce.awt.CENetworkActionExperimenterWindow;
import girard.sc.ce.awt.CENetworkActionObserverWindow;
import girard.sc.exnet.obj.Edge;
import girard.sc.exnet.obj.NetworkComponent;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.wl.io.WLGeneralServerConnection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This is the edge object for the CENetwork that connects two CENodes together
 * via a relation.
 * <p>
 * <br> Started: 07-23-2002
 * <br> Modified: 01-16-2003
 * <p>
 * @author Dudley Girard
 */

public class CEEdge extends Edge
    {
    private boolean       m_active = false;
    private boolean       m_inUse = false;
    private boolean       m_completed = false;
    private boolean       m_display = true;
// Where all the CENetworkComponents are kept
    private Hashtable     m_exptData = new Hashtable();
// gets added in after cloning.
    private Hashtable     m_extraData = new Hashtable();
// The network this edge is attached to.
    private int           m_infoLevel = 10; // 10 -> All, 0 -> None

    public CEEdge()
        {
        }
    public CEEdge (Edge e)
        {
        m_node1 = e.getNode1();
        m_node2 = e.getNode2();
        m_n1Anchor = new Point(e.getN1Anchor().x,e.getN1Anchor().y);
        m_n2Anchor = new Point(e.getN2Anchor().x,e.getN2Anchor().y);
        }

    public void addExptData(NetworkComponent nc)
        {
        m_exptData.put(nc.getComponentName(),nc);
        }

    public void applySettings(Hashtable h)
        {
        super.applySettings(h);

        Hashtable objects = (Hashtable)h.get("Objects");

        Vector exptData = (Vector)h.get("ExptData");
        Enumeration enm = exptData.elements();
        while (enm.hasMoreElements())
            {
            Hashtable data = (Hashtable)enm.nextElement();
            String type = (String)data.get("Type");
            CENetworkComponent nc = (CENetworkComponent)((CENetworkComponent)objects.get(type)).clone();
            data.put("Network",h.get("Network"));
            data.put("Edge",this);
            nc.applySettings(data);
            addExptData(nc);
            }

        m_display = ((Boolean)h.get("Display")).booleanValue();
        m_active = ((Boolean)h.get("Active")).booleanValue();
        m_completed = ((Boolean)h.get("Completed")).booleanValue();

        m_infoLevel = ((Integer)h.get("InfoLevel")).intValue();

        m_extraData = (Hashtable)h.get("ExtraData");
        }

    public Object clone()
        {
        CEEdge tmp = new CEEdge((Edge)this);
        tmp.setActive(m_active);
        tmp.setCompleted(m_completed);
        tmp.setDisplay(m_display);

        Enumeration enm = m_exptData.keys();
        while (enm.hasMoreElements())
            {
  // We can't just clone the object cause it would have the old edge object.
            String key = (String)enm.nextElement();
            CENetworkComponent nc = (CENetworkComponent)m_exptData.get(key);
            CENetworkComponent nc2 = (CENetworkComponent)nc.clone();
            nc2.setEdge(tmp);
            tmp.addExptData(nc2);
            }
        tmp.setInfoLevel(m_infoLevel);

        return tmp;
        }

    public boolean getActive()
        {
        return m_active;
        }
    public boolean getCompleted()
        {
        return m_completed;
        }
    public boolean getDisplay()
        {
        return m_display;
        }
    public CENetworkComponent getExptData(String str)
        {
        return (CENetworkComponent)m_exptData.get(str);
        }
    public boolean getInUse()
        {
        return m_inUse;
        }
    public CEStateAction getNextState(CENetwork network)
        {
        CENetworkComponent tmpNC = null;
        double currentState = ((Double)network.getExtraData("CurrentState")).doubleValue();

        Enumeration enm = m_exptData.elements();
        while (enm.hasMoreElements())
            {
            CENetworkComponent nc = (CENetworkComponent)enm.nextElement();

            if ((nc.getStatePoint() >  currentState) && (nc.getStatePoint() > 0))
                {
                if (tmpNC == null)
                    {
                    tmpNC = nc;
                    }
                else if (nc.getStatePoint() < tmpNC.getStatePoint())
                    {
                    tmpNC = nc;
                    }
                }
            }
        if (tmpNC != null)
            return tmpNC.getStateAction();
        return null;
        }
    public double getNextValidState(CENetwork network)
        {
        CENetworkComponent tmpNC = null;
        double currentState = ((Double)network.getExtraData("CurrentState")).doubleValue();

        Enumeration enm = m_exptData.elements();
        while (enm.hasMoreElements())
            {
            CENetworkComponent nc = (CENetworkComponent)enm.nextElement();

            if ((nc.getStatePoint() >  currentState) && (nc.getStatePoint() > 0))
                {
                if (tmpNC == null)
                    {
                    tmpNC = nc;
                    }
                else if (nc.getStatePoint() < tmpNC.getStatePoint())
                    {
                    tmpNC = nc;
                    }
                }
            }
        if (tmpNC != null)
            return tmpNC.getStatePoint();
        else
            return 100;
        }
/**
 * We don't store the network because we need to point to a specific network.
 */
    public Hashtable getSettings()
        {
        Hashtable settings = super.getSettings();

        Vector types = new Vector();

        Vector exptData = new Vector();
        Enumeration enm = m_exptData.elements();
        while (enm.hasMoreElements())
            {
            NetworkComponent nc = (NetworkComponent)enm.nextElement();
            Hashtable data = nc.getSettings();
            String type = (String)data.get("Type");
            exptData.addElement(data);
            types.addElement(type);
            }
        settings.put("ExptData",exptData);
        settings.put("Types",types);
        settings.put("Active",new Boolean(m_active));
        settings.put("Completed",new Boolean(m_completed));
        settings.put("Display",new Boolean(m_display));
        settings.put("InfoLevel",new Integer(m_infoLevel));
        settings.put("ExtraData",m_extraData);

        return settings;
        }

/**
 * We don't initialize m_active here because we should do it ahead of time.
 * This is so any network components can adjust it to false if they need to
 * without fear of it being turned back on.
 */
    public void initializeNetwork()
        {
        m_completed = false;
        m_inUse = false;
// Why do we even have the m_infoLevel here if we don't use m_display efficiently?
        if (m_infoLevel > 0)
            m_display = true;
        else
            m_display = false;
        Enumeration enm = m_exptData.elements();
        while (enm.hasMoreElements())
            {
            NetworkComponent nc = (NetworkComponent)enm.nextElement();
            nc.initializeNetwork();
            }
        }
    public void initializeStart()
        {
        m_completed = false;
        if (m_infoLevel > 0)
            m_display = true;
        else
            m_display = false;
        Enumeration enm = m_exptData.elements();
        while (enm.hasMoreElements())
            {
            CENetworkComponent nc = (CENetworkComponent)enm.nextElement();
            nc.initializeStart();
            }
        }

    public void removeExptData(String key)
        {
        m_exptData.remove(key);
        }
    public void removeExtraData(String key)
        {
        m_extraData.remove(key);
        }

    public Hashtable retrieveData(WLGeneralServerConnection wlgsc, ExptMessage em, BaseDataInfo bdi)
        {
        Hashtable edgeData = new Hashtable();

	System.err.println("printing in CEEdge retrieveData Method:printing the exptData");
	Enumeration myEnum = m_exptData.keys();
	while(myEnum.hasMoreElements()){
	    System.err.println("key:"+(String)myEnum.nextElement());
	}

        Enumeration enm = m_exptData.elements();
        while(enm.hasMoreElements())
            {
            NetworkComponent nc = (NetworkComponent)enm.nextElement();
            Hashtable data = nc.retrieveData(wlgsc,em,bdi);
            edgeData.put(nc.getComponentName(),data);
            }

        return edgeData;
        }

    public void setActive(boolean value)
        {
        m_active = value;
        }
    public void setCompleted(boolean value)
        {
        m_completed = value;
        }
    public void setComponentNetwork(CENetwork net)
        {
        Enumeration enm = m_exptData.keys();
        while (enm.hasMoreElements())
            {
            String key = (String)enm.nextElement();
            NetworkComponent nc = (NetworkComponent)m_exptData.get(key);
            nc.setNetwork(net);
            }
        }
    public void setDisplay(boolean value)
        {
        m_display = value;
        }
    public void setInfoLevel(int value)
        {
        m_infoLevel = value;

        if (m_infoLevel > 0)
            m_display = true;
        else
            m_display = false;
        }
    public void setInUse(boolean value)
        {
        m_inUse = value;
        }

    public String toString()
        {
        String str1 = new String(""+m_node1+", "+m_node2);

        return str1;    
        }

    public void updateClientImage(Graphics g,Dimension dim,Point tl, Point br, CENetwork cen, CENetworkActionClientWindow CWApp)
        {
	    
        if (!m_display)
            return;

	//nvm - added the following code
	int infoLevel = ((Integer)cen.getExtraData("InfoLevel")).intValue();
        String n1type = (String)((CENode)cen.getNode(m_node1)).getExtraData("Type");
        String n2type = (String)((CENode)cen.getNode(m_node2)).getExtraData("Type");
	
        if (infoLevel == 1){
	    // Will display all relations connected to me.
            if ((!n1type.equals("Me")) && (!n2type.equals("Me")))
                return;
	}

        if (infoLevel <= 5){
	    // Will display all relations connected to me or my neighbors.
            if ((!n1type.equals("Neighbor")) && (!n2type.equals("Neighbor")))
                return;
	}
	//</nvm>

        if ((m_n1Anchor.x > tl.x) && (m_n1Anchor.y > tl.y) && (m_n1Anchor.x < br.x) && (m_n1Anchor.y < br.y))
            {
            if ((m_n2Anchor.x > tl.x) && (m_n2Anchor.y > tl.y) && (m_n2Anchor.x < br.x) && (m_n2Anchor.y < br.y))
                {
                int width = br.x - tl.x;
                int height = br.y - tl.y;

                double xAdj = dim.width/(width*1.0);
                double yAdj = dim.height/(height*1.0);


           /* We need to move the old point such that its location is relative to
           the center of the viewing area, with the center of the viewing area
           being point 0,0. Scale the point, then move it back out.
           */
                Point newN1 = new Point();
                newN1.x = (int)(xAdj*(m_n1Anchor.x - tl.x));
                newN1.y = (int)(yAdj*(m_n1Anchor.y - tl.y));

                Point newN2 = new Point();
                newN2.x = (int)(xAdj*(m_n2Anchor.x - tl.x));
                newN2.y = (int)(yAdj*(m_n2Anchor.y - tl.y));

                if (m_inUse)
                    g.setColor(CEColor.ACTIVE_EDGE);
                else
                    g.setColor(Color.lightGray);

                if (m_completed)
                    g.setColor(CEColor.COMPLETE_EDGE);
                else if (!m_active)
                    g.setColor(CEColor.INACTIVE_EDGE);

                g.drawLine(newN1.x,newN1.y,newN2.x,newN2.y);

		//                CENetwork cen = CWApp.getNetwork();
                CENode node = (CENode)cen.getExtraData("Me");

                if ((node.getID() == m_node1) || (node.getID() == m_node2))
                    {
                    if (newN1.y == newN2.y)
                        {
                        g.drawLine(newN1.x,newN1.y+1,newN2.x,newN2.y+1);
                        }
                    else
                        {
                        g.drawLine(newN1.x+1,newN1.y,newN2.x+1,newN2.y);
                        }
                    }

                Vector locInfo = new Vector();
                locInfo.addElement(newN1);
                locInfo.addElement(newN2);
                locInfo.addElement(new Double(xAdj));
                locInfo.addElement(new Double(yAdj));
                locInfo.addElement(CWApp);
                Enumeration enm = m_exptData.elements();
                while (enm.hasMoreElements())
                    {
                    NetworkComponent nc = (NetworkComponent)enm.nextElement();
                    nc.drawClient(g,locInfo);
                    }
                }
            }
        }
    public void updateExperimenterImage(Graphics g,Dimension dim,Point tl, Point br, CENetworkActionExperimenterWindow CWApp)
        {
        if (!m_display)
            return;

        if ((m_n1Anchor.x > tl.x) && (m_n1Anchor.y > tl.y) && (m_n1Anchor.x < br.x) && (m_n1Anchor.y < br.y))
            {
            if ((m_n2Anchor.x > tl.x) && (m_n2Anchor.y > tl.y) && (m_n2Anchor.x < br.x) && (m_n2Anchor.y < br.y))
                {
                int width = br.x - tl.x;
                int height = br.y - tl.y;

                double xAdj = dim.width/(width*1.0);
                double yAdj = dim.height/(height*1.0);

        /* We need to move the old point such that its location is relative to
           the center of the viewing area, with the center of the viewing area
           being point 0,0. Scale the point, then move it back out.
        */
                Point newN1 = new Point();
                newN1.x = (int)(xAdj*(m_n1Anchor.x - tl.x));
                newN1.y = (int)(yAdj*(m_n1Anchor.y - tl.y));

                Point newN2 = new Point();
                newN2.x = (int)(xAdj*(m_n2Anchor.x - tl.x));
                newN2.y = (int)(yAdj*(m_n2Anchor.y - tl.y));

                if (m_inUse)
                    g.setColor(CEColor.ACTIVE_EDGE);
                else
                    g.setColor(Color.lightGray);

                if (m_completed)
                    g.setColor(CEColor.COMPLETE_EDGE);
                else if (!m_active)
                    g.setColor(CEColor.INACTIVE_EDGE);

                g.drawLine(newN1.x,newN1.y,newN2.x,newN2.y);

                Vector locInfo = new Vector();
                locInfo.addElement(newN1);
                locInfo.addElement(newN2);
                locInfo.addElement(new Double(xAdj));
                locInfo.addElement(new Double(yAdj));
                locInfo.addElement(CWApp);
                Enumeration enm = m_exptData.elements();
                while (enm.hasMoreElements())
                    {
                    NetworkComponent nc = (NetworkComponent)enm.nextElement();
                    nc.drawExpt(g,locInfo);
                    }
                }
            }
        }
    public void updateObserverImage(Graphics g,Dimension dim,Point tl, Point br,  CENetworkActionObserverWindow CWApp)
        {
        if (!m_display)
            return;

        if ((m_n1Anchor.x > tl.x) && (m_n1Anchor.y > tl.y) && (m_n1Anchor.x < br.x) && (m_n1Anchor.y < br.y))
            {
            if ((m_n2Anchor.x > tl.x) && (m_n2Anchor.y > tl.y) && (m_n2Anchor.x < br.x) && (m_n2Anchor.y < br.y))
                {
                int width = br.x - tl.x;
                int height = br.y - tl.y;

                double xAdj = dim.width/(width*1.0);
                double yAdj = dim.height/(height*1.0);

        /* We need to move the old point such that its location is relative to
           the center of the viewing area, with the center of the viewing area
           being point 0,0. Scale the point, then move it back out.
        */
                Point newN1 = new Point();
                newN1.x = (int)(xAdj*(m_n1Anchor.x - tl.x));
                newN1.y = (int)(yAdj*(m_n1Anchor.y - tl.y));

                Point newN2 = new Point();
                newN2.x = (int)(xAdj*(m_n2Anchor.x - tl.x));
                newN2.y = (int)(yAdj*(m_n2Anchor.y - tl.y));

                if (m_inUse)
                    g.setColor(CEColor.ACTIVE_EDGE);
                else
                    g.setColor(Color.lightGray);

                if (m_completed)
                    g.setColor(CEColor.COMPLETE_EDGE);
                else if (!m_active)
                    g.setColor(CEColor.INACTIVE_EDGE);

                g.drawLine(newN1.x,newN1.y,newN2.x,newN2.y);

                Vector locInfo = new Vector();
                locInfo.addElement(newN1);
                locInfo.addElement(newN2);
                locInfo.addElement(new Double(xAdj));
                locInfo.addElement(new Double(yAdj));
                locInfo.addElement(CWApp);
                Enumeration enm = m_exptData.elements();
                while (enm.hasMoreElements())
                    {
                    NetworkComponent nc = (NetworkComponent)enm.nextElement();
                    nc.drawObserver(g,locInfo);
                    }
                }
            }
        }
    }
