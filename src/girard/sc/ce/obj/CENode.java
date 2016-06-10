package girard.sc.ce.obj;

import girard.sc.ce.awt.CEClientDisplayArrow;
import girard.sc.ce.awt.CEColor;
import girard.sc.ce.awt.CENetworkActionClientWindow;
import girard.sc.ce.awt.CENetworkActionExperimenterWindow;
import girard.sc.ce.awt.CENetworkActionObserverWindow;
import girard.sc.exnet.obj.NetworkComponent;
import girard.sc.exnet.obj.Node;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.wl.io.WLGeneralServerConnection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Is the node object for the CENetwork.
 * <p>
 * <br> Started: 07-23-2002
 * <br> Modified: 1-16-2003
 * <p>
 * @author Dudley Girard
 */

public class CENode extends Node
    {
    private boolean  	 m_display = true;
    private int          m_infoLevel = 10; // 10 -> All, 0 -> None
    private Hashtable    m_exptData = new Hashtable();

/*
  nvm - to know whether this node has exchanged any resources or not.
  This information is needed for externality calculation.  Intially I
  thot that we could calculate this by observing the difference in teh
  resources at the beginning and the end of the rounds. But there
  could be a case in a 3 line network, where the middle guy makes the
  exchanges without any profit or loss.
*/
	private boolean m_exchanged = false;

/**
 * For client stores adjusted x and y location along with type designation
 *(Me, Neighbor, Other).
 */
    private Hashtable    m_extraData = new Hashtable(); 

    public CENode ()
        {
        super();
        }
    public CENode (Node n)
        {
        super(n.getID(),n.getLabel(),n.getLoc());
        }
    public CENode (int ident,String lett, Point p)
        {
        super(ident,lett,p);
        }
    public CENode (String lett, Point p)
        {
        super(lett,p);
        }

    public void addExptData(CENetworkComponent obj)
        {
        m_exptData.put(obj.getComponentName(),obj);
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
            data.put("Node",this);
            data.put("Network",h.get("Network"));
            nc.applySettings(data);
            addExptData(nc);
            }

        m_display = ((Boolean)h.get("Display")).booleanValue();

        m_infoLevel = ((Integer)h.get("InfoLevel")).intValue();

        m_extraData = (Hashtable)h.get("ExtraData");
        }

    public Object clone()
        {
        CENode n = new CENode(m_id,m_label,new Point(m_loc.x,m_loc.y));

        Enumeration enm = m_exptData.keys();
        while (enm.hasMoreElements())
            {
            String key = (String)enm.nextElement();
            CENetworkComponent nc = (CENetworkComponent)m_exptData.get(key);
            CENetworkComponent nc2 = (CENetworkComponent)nc.clone();
            nc2.setNode(n);
            n.addExptData(nc2);
            }

        /* Need to copy over the object list too */

        return n;
        }

    public CENetworkComponent getExptData(String name)
        {
        return (CENetworkComponent)m_exptData.get(name);
        }
    public Object getExtraData(String name)
        {
        return m_extraData.get(name);
        }

	//nvm quick fix--- just for testing purpose. Delete this
	public Hashtable getExtraData(){ return m_extraData;}
    public String getLabel()
        {
        return m_label;
        }
    public int getID()
        {
        return m_id;
        }
    public int getInfoLevel()
        {
        return m_infoLevel;
        }
	public Point getLoc()
        {
        return m_loc;
        }

	//note this method is to be called whenever an exchange is completed
	public void setExchanged(){
	    System.err.println("ok....setting exchanged to true...");
	    m_exchanged = true;
	}

	public void resetExchanged(){
	    //nvm - set the exchangesPerformed to false
	// this value is set to true when exchanges are performed
	    System.err.println("ok...ok...resetting m_exchchanged...");
	    m_exchanged = false;
	}

	public boolean performedExchange(){
	    if(m_exchanged)
		System.err.println("returning true for node"+m_label);
	    else
		System.err.println("returning false "+m_label);
	    return m_exchanged;
	}

/* Returns the next CEStateAction to occur */
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
/* Find out which is the next possible state point to occur */
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
    public Hashtable getSettings()
        {
        Hashtable settings = super.getSettings();

        Vector types = new Vector();

        Vector exptData = new Vector();
        Enumeration enm = m_exptData.elements();
        while(enm.hasMoreElements())
            {
            NetworkComponent nc = (NetworkComponent)enm.nextElement();
            Hashtable data = nc.getSettings();
            String type = (String)data.get("Type");
            exptData.addElement(data);
            types.addElement(type);
            }
        settings.put("ExptData",exptData);
        settings.put("Types",types);
        settings.put("Display",new Boolean(m_display));
 
        settings.put("InfoLevel",new Integer(m_infoLevel));

        settings.put("ExtraData",m_extraData);

        return settings;
        }
    public int getXpos()
        {
        return m_loc.x;
        }
    public int getYpos()
        {
        return m_loc.y;
        }

    public void initializeNetwork()
        {
        if (m_infoLevel > 0)
            m_display = true;
        else
            m_display = false;

        Enumeration enm = m_exptData.elements();
        while (enm.hasMoreElements())
            {
            CENetworkComponent nc = (CENetworkComponent)enm.nextElement();
            nc.initializeNetwork();
            }
        }
    public void initializeStart()
        {
	    System.err.println("in the initializeNetwork method of CENode");
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

    public boolean isMe(int user,CENetwork network)
        {
        CEPeriod cep = network.getActivePeriod();
        if (m_id == cep.getUserNode(user))
            return true;
        else
            return false;
        }
    public boolean isNeighbor(int index, CENetwork network)
        {
        CEPeriod cep = network.getActivePeriod();
        CENode node = (CENode)network.getNode(cep.getUserNode(index));
        Enumeration enm = network.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CEEdge edge = (CEEdge)enm.nextElement();
            if ((edge.getNode1() == node.getID()) && (edge.getNode2() == m_id))
                return true;
            if ((edge.getNode2() == node.getID()) && (edge.getNode1() == m_id))
                return true;
            }
        return false;
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
        Hashtable nodeData = new Hashtable();

	System.err.println("printing in CENode retrieveData Method:printing the exptData");
	Enumeration myEnum = m_exptData.keys();
	while(myEnum.hasMoreElements()){
	    System.err.println("key:"+(String)myEnum.nextElement());
	}

        Enumeration enm = m_exptData.elements();
        while(enm.hasMoreElements())
            {
            NetworkComponent nc = (NetworkComponent)enm.nextElement();
            Hashtable data = nc.retrieveData(wlgsc,em,bdi);
            nodeData.put(nc.getComponentName(),data);
            }

        return nodeData;
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
    public void setExtraData(String key, Object obj)
        {
        if (m_extraData.containsKey(key))
            {
            m_extraData.remove(key);
            m_extraData.put(key,obj);
            }
        else
            {
            m_extraData.put(key,obj);
            }
        }
    public void setLabel(char lett)
        {
        m_label = new String(""+lett);
        }
    public void setLabel(String lett)
        {
        m_label = lett;
        }
    public void setID(int ident)
        {
        m_id = ident;
        }
    public void setInfoLevel(int value)
        {
        m_infoLevel = value;
        }
    public void setLoc(int x, int y)
        {
        m_loc.x = x;
        m_loc.y = y;
        }
    public void setLoc(Point p)
        {
        m_loc = p;
        }
    public void setXpos(int value)
        {
        m_loc.x = value;
        }
    public void setYpos(int value)
        { 
        m_loc.y = value;
        }

    public String toString()
        {
        String str1 = new String(""+m_id+", '"+m_label+"', "+m_loc.x+", "+m_loc.y);

        return str1;    
        }

    public void updateClientImage(Graphics g,Dimension dim,Point tl, Point br, CENetwork cen, CENetworkActionClientWindow CWApp)
        {
        if (!m_display)
            return;
	// <nvm> code
        int infoLevel = ((Integer)cen.getExtraData("InfoLevel")).intValue();
        String type = (String)getExtraData("Type");

        if ((infoLevel == 1) && (type.equals("Other")))
            return;

        if ((infoLevel <= 5) && (type.equals("Other"))){
		// Will display all nodes connected to me or my neighbors.
		// BENode me = (BENode)ben.getExtraData("Me");
		boolean dFlag = false;
		Enumeration enm = cen.getEdgeList().elements();
		while (enm.hasMoreElements()){
		    CEEdge edge = (CEEdge)enm.nextElement();
		    if ((m_id == edge.getNode1()) || (m_id == edge.getNode2())){
			String n1type = (String)((CENode)cen.getNode(edge.getNode1())).getExtraData("Type");
			String n2type = (String)((CENode)cen.getNode(edge.getNode2())).getExtraData("Type");
			if ((n1type.equals("Neighbor")) || (n2type.equals("Neighbor"))){
			    dFlag = true;
			    break;
			}
		    }
		}
		if (!dFlag)
		    return;
            }
	//</nvm>


        if ((m_loc.x > tl.x) && (m_loc.y > tl.y) && (m_loc.x < br.x) && (m_loc.y < br.y))
            {
            int width = br.x - tl.x;
            int height = br.y - tl.y;

        // How much to scale by.
            double xAdj = dim.width/(1.0*width);
            double yAdj = dim.height/(1.0*height);

        /* We need to move the old point such that its location is relative to
           the center of the viewing area, with the center of the viewing area
           being point 0,0. Scale the point, then move it back out.
        */
            Point newLoc = new Point();
            newLoc.x = (int)(xAdj*(m_loc.x - tl.x + 3));
            newLoc.y = (int)(yAdj*(m_loc.y - tl.y + 6));

            setExtraData("XLoc",new Integer(newLoc.x)); // Used by the display area to figure out where the node is.
            setExtraData("YLoc",new Integer(newLoc.y)); // Used by the display area to figure out where the node is.

            g.setColor(CEColor.NODE);

            String str = (String)m_extraData.get("Type");
            if (str.equals("Me"))
                {
                Font f1 = new Font("Monospaced",Font.BOLD,20);
        
                g.setColor(Color.white);
                g.fillRect(newLoc.x-1,newLoc.y-6,14,14);

                g.setFont(f1);
                g.setColor(Color.black);
                g.drawString(m_label.substring(0,1),newLoc.x,newLoc.y+6);
                
                }

            if (str.equals("Neighbor"))
                {
                CEClientDisplayArrow arrow = CWApp.getArrow();
                
                Font f1 = new Font("Monospaced",Font.PLAIN,16);

                g.setColor(Color.white);
                if (arrow.getToNode() != null)
                    {
                    if (arrow.getToNode().getID() == m_id)
                        {
                        g.setColor(Color.lightGray);
                        }
                    }
                g.fillRect(newLoc.x-3,newLoc.y-9,15,15);

                g.setFont(f1);
                g.setColor(Color.black);
                g.drawString(m_label.substring(0,1),newLoc.x,newLoc.y+3);

                g.setColor(Color.gray);
              // top
                g.drawLine(newLoc.x-3,newLoc.y-8,newLoc.x+11,newLoc.y-8);
                g.drawLine(newLoc.x-4,newLoc.y-9,newLoc.x+12,newLoc.y-9);
              // left side
                g.drawLine(newLoc.x-3,newLoc.y-8,newLoc.x-3,newLoc.y+6);
                g.drawLine(newLoc.x-4,newLoc.y-8,newLoc.x-4,newLoc.y+7);
         
                g.setColor(Color.black);
              // bottom
                g.drawLine(newLoc.x-2,newLoc.y+6,newLoc.x+11,newLoc.y+6);
                g.drawLine(newLoc.x-3,newLoc.y+7,newLoc.x+12,newLoc.y+7);
              // right side
                g.drawLine(newLoc.x+11,newLoc.y-7,newLoc.x+11,newLoc.y+6);
                g.drawLine(newLoc.x+12,newLoc.y-8,newLoc.x+12,newLoc.y+7);
                }

            if (str.equals("Other"))
                {
                Font f1 = new Font("Monospaced",Font.PLAIN,16);

                g.setColor(Color.white);
                g.fillRect(newLoc.x-3,newLoc.y-9,12,12);

                g.setFont(f1);
                g.setColor(Color.black);
                g.drawString(m_label.substring(0,1),newLoc.x,newLoc.y+5);
                }

            Vector locInfo = new Vector();
            locInfo.addElement(newLoc);
            locInfo.addElement(new Double(xAdj));
            locInfo.addElement(new Double(yAdj));
            locInfo.addElement(CWApp);
            Enumeration enm = m_exptData.elements();
            while (enm.hasMoreElements())
                {
                CENetworkComponent nc = (CENetworkComponent)enm.nextElement();
                nc.drawClient(g,locInfo);
                }
            }
        }
    public void updateExperimenterImage(Graphics g,Dimension dim,Point tl, Point br, CENetworkActionExperimenterWindow CWApp)
        {
        if (!m_display)
            return;

        if ((m_loc.x > tl.x) && (m_loc.y > tl.y) && (m_loc.x < br.x) && (m_loc.y < br.y))
            {
            Font f1 = new Font("Monospaced",Font.PLAIN,14);
            Font f2 = new Font("Monospaced",Font.PLAIN,12);

            int width = br.x - tl.x;
            int height = br.y - tl.y;

            double xAdj = dim.width/(1.0*width);
            double yAdj = dim.height/(1.0*height);

        /* We need to move the old point such that its location is relative to
           the center of the viewing area, with the center of the viewing area
           being point 0,0. Scale the point, then move it back out.
        */
            Point newLoc = new Point();
            newLoc.x = (int)(xAdj*(m_loc.x - tl.x + 4));
            newLoc.y = (int)(yAdj*(m_loc.y - tl.y + 4));

            g.setColor(CEColor.NODE);
            g.fillOval(newLoc.x,newLoc.y,6,6);

            g.setFont(f1);
            g.drawString(m_label,newLoc.x+7,newLoc.y+19);
            g.setFont(f2);
            g.drawString(String.valueOf(m_id),newLoc.x+7,newLoc.y);

            Vector locInfo = new Vector();
            locInfo.addElement(newLoc);
            Enumeration enm = m_exptData.elements();
            while (enm.hasMoreElements())
                {
                CENetworkComponent nc = (CENetworkComponent)enm.nextElement();
                nc.drawExpt(g,locInfo);
                }
            }
        }
    public void updateObserverImage(Graphics g,Dimension dim,Point tl, Point br, CENetworkActionObserverWindow CWApp)
        {
        if (!m_display)
            return;

        if ((m_loc.x > tl.x) && (m_loc.y > tl.y) && (m_loc.x < br.x) && (m_loc.y < br.y))
            {
            Font f1 = new Font("Monospaced",Font.PLAIN,14);
            Font f2 = new Font("Monospaced",Font.PLAIN,12);

            int width = br.x - tl.x;
            int height = br.y - tl.y;

            double xAdj = dim.width/(1.0*width);
            double yAdj = dim.height/(1.0*height);

        /* We need to move the old point such that its location is relative to
           the center of the viewing area, with the center of the viewing area
           being point 0,0. Scale the point, then move it back out.
        */
            Point newLoc = new Point();
            newLoc.x = (int)(xAdj*(m_loc.x - tl.x + 4));
            newLoc.y = (int)(yAdj*(m_loc.y - tl.y + 4));

            g.setColor(CEColor.NODE);
            g.fillOval(newLoc.x,newLoc.y,6,6);

            g.setFont(f1);
            g.drawString(m_label,newLoc.x+7,newLoc.y+19);
            g.setFont(f2);
            g.drawString(String.valueOf(m_id),newLoc.x+7,newLoc.y);

            Vector locInfo = new Vector();
            locInfo.addElement(newLoc);
            Enumeration enm = m_exptData.elements();
            while (enm.hasMoreElements())
                {
                CENetworkComponent nc = (CENetworkComponent)enm.nextElement();
                nc.drawObserver(g,locInfo);
                }
            }
        }
    }
