package girard.sc.be.obj;

import girard.sc.be.awt.BEColor;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.expt.sql.LoadDataResultsReq;
import girard.sc.io.FMSObjCon;
import girard.sc.wl.io.WLServerConnection;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This object allows for subnetworks to be defined within a network for each
 * individual node.  Additionally a specific ordering can applied to exchanges
 * in the network on a nodal basis.
 * <p>
 * <br> Started: 09-17-2002
 * <br> Modified: 06-30-2003
 * <p>
 * @author Dudley Girard
 */

public class BENodeOrSubNet extends BENodeExchange 
    {
    public static final double COAL_VOTE_STATE = 0.50;
    public static final double SN_INFO_WIN_STATE = 1.10;
    public static final double COAL_ZAP_STATE = 1.55;

    protected Vector      m_ordering = new Vector();
    protected Hashtable   m_subnetworks = new Hashtable();
    protected boolean     m_showInfoWindow = false;
    protected boolean     m_showSubInfo = true;
    protected BECoalition m_coalition = new BECoalition();

    public BENodeOrSubNet()
        {
        m_statePoint = 1.1;
        }

    public BENodeOrSubNet (BENode node)
        {
        super(node);
        m_statePoint = SN_INFO_WIN_STATE;
        }
    public BENodeOrSubNet (BENode node, BENetwork net)
        {
        super(node,net);
        m_statePoint = SN_INFO_WIN_STATE;
        }
    public BENodeOrSubNet (BENode node, Vector nodes)
        {
        super(node);
        m_statePoint = SN_INFO_WIN_STATE;

        int i = 0;
        Enumeration enm = nodes.elements();
        while (enm.hasMoreElements())
            {
            int n = ((Integer)enm.nextElement()).intValue();
            m_coalition.setShareList(n,false);
            m_subnetworks.put(new Integer(i),new BEOrSubnetwork(i,n));
            Vector v = new Vector();
            v.addElement(new Integer(-1));
            m_ordering.addElement(v);
            i++;
            }
        }
    public BENodeOrSubNet (BENode node, Vector nodes, BENetwork net)
        {
        super(node,net);
        m_statePoint = 1.1;

        int i = 0;
        Enumeration enm = nodes.elements();
        while (enm.hasMoreElements())
            {
            int n = ((Integer)enm.nextElement()).intValue();
            m_coalition.setShareList(n,false);
            m_subnetworks.put(new Integer(i),new BEOrSubnetwork(i,n));
            Vector v = new Vector();
            v.addElement(new Integer(-1));
            m_ordering.addElement(v);
            i++;
            }
        }
    
    public void addOrdering(Vector v)
        {
        m_ordering.addElement(v);
        }
    public void addSubnetwork(BEOrSubnetwork sn)
        {
        m_subnetworks.put(new Integer(sn.getSubnetwork()),sn);
        Vector v = new Vector();
        v.addElement(new Integer(-1));
        m_ordering.addElement(v);
        }
    public void addSubnetwork(int[] nodes)
        {
        int index = m_subnetworks.size();
        m_subnetworks.put(new Integer(index),new BEOrSubnetwork(index,nodes));
        Vector v = new Vector();
        v.addElement(new Integer(-1));
        m_ordering.addElement(v);
        }

    public void applySettings(Hashtable h)
        {
        super.applySettings(h);

        m_showInfoWindow = ((Boolean)h.get("ShowInfoWindow")).booleanValue(); 
        Vector subnetworks = (Vector)h.get("Subnetworks");
        Enumeration enm = subnetworks.elements();
        while (enm.hasMoreElements())
            {
            Hashtable data = (Hashtable)enm.nextElement();
            BEOrSubnetwork sn = new BEOrSubnetwork();
            sn.applySettings(data);
            addSubnetwork(sn);
            }
        m_ordering = (Vector)h.get("Ordering");
        if (h.containsKey("Coalition"))
            m_coalition.applySettings((Hashtable)h.get("Coalition"));
        }

    public boolean containsNode(int node)
        {
        Enumeration enm = m_subnetworks.elements();
        while (enm.hasMoreElements())
            {
            BEOrSubnetwork s = (BEOrSubnetwork)enm.nextElement();
            if (s.containsNode(node))
                return true;
            }
        return false;
        }

    public void cleanUp()
        {
        m_exchanges = 0;
        m_Max = 1;
        m_Min = 1;
        Enumeration enm = m_subnetworks.keys();
        while (enm.hasMoreElements())
            {
            Object obj = enm.nextElement();
            if (obj instanceof Integer)
                {
                BEOrSubnetwork sn = (BEOrSubnetwork)m_subnetworks.get(obj);
                sn.cleanUp();
                }
            }
        m_subnetworks.clear();
        m_ordering.removeAllElements();
        }

    public Object clone()
        {
        BENodeOrSubNet bens = new BENodeOrSubNet(m_node);
        bens.setMin(m_Min);
        bens.setMax(m_Max);
        bens.setExchanges(m_exchanges);
        bens.setShowInfoWindow(m_showInfoWindow);

        Enumeration enm = m_subnetworks.keys();
        while (enm.hasMoreElements())
            {
            Object obj = enm.nextElement();
            if (obj instanceof Integer)
                {
                BEOrSubnetwork sn = (BEOrSubnetwork)m_subnetworks.get(obj);
                BEOrSubnetwork snNew = (BEOrSubnetwork)sn.clone();
                bens.addSubnetwork(snNew);
                }
            }

        Vector ordering = new Vector();
        enm = m_ordering.elements();
        while (enm.hasMoreElements())
            {
            Vector orderSet = new Vector();
            Vector v = (Vector)enm.nextElement();
            Enumeration enum2 = v.elements();
            while (enum2.hasMoreElements())
                {
                Integer nn = (Integer)enum2.nextElement();
                orderSet.addElement(new Integer(nn.intValue()));
                }
            ordering.addElement(orderSet);
            }
        bens.setOrdering(ordering);
        bens.setCoalition((BECoalition)m_coalition.clone());

        return bens;
        }

    public Point computeDisplayPoint(Point me, Point them, Point loc, double xAdj, double yAdj)
        {
        double changeX = 0;
        double changeY = 0;
        double totalDistance = 0;
        Point newLoc = new Point(0,0);

        changeX = them.x - me.x;
        changeY = them.y - me.y;

        totalDistance = Math.sqrt(changeX*changeX + changeY*changeY);  // The total length of the line.

        newLoc.x = (int)(loc.x + (15.0*changeX*xAdj)/(1.0*totalDistance));
        newLoc.y = (int)(loc.y + (15.0*changeY*yAdj)/(1.0*totalDistance));

        return newLoc;
        }

  // Draw info on a canvas using the graphic for the client screen.
    public void drawClient(Graphics g, Vector locInfo) 
        {
        BENetwork ben = (BENetwork)m_network;

        if (!m_showSubInfo)
            return;

        int infoLevel = ((Integer)ben.getExtraData("InfoLevel")).intValue();
        String type = (String)((BENode)ben.getNode(m_node.getID())).getExtraData("Type");

        if ((infoLevel <= 3) && (type.equals("Other")))
            return;

        if ((infoLevel == 6) && (type.equals("Other")))
            return;

        if ((infoLevel <= 8) && (type.equals("Other")))
            {
        // Will display all nodes connected to me or my neighbors.
            // BENode me = (BENode)ben.getExtraData("Me");

            boolean dFlag = false;
            Enumeration enm = ben.getEdgeList().elements();
            while (enm.hasMoreElements())
                {
                BEEdge edge = (BEEdge)enm.nextElement();
                
                if ((m_node.getID() == edge.getNode1()) || (m_node.getID() == edge.getNode2()))
                    {
                    String n1type = (String)((BENode)ben.getNode(edge.getNode1())).getExtraData("Type");
                    String n2type = (String)((BENode)ben.getNode(edge.getNode2())).getExtraData("Type");

                    if ((n1type.equals("Neighbor")) || (n2type.equals("Neighbor")))
                        {
                        dFlag = true;
                        break;
                        }
                    }
                }
            if (!dFlag)
                return;
            }

        Point nodeLoc = (Point)locInfo.elementAt(0);
        Point loc = new Point(nodeLoc.x,nodeLoc.y);
        double xAdj = ((Double)locInfo.elementAt(1)).doubleValue();
        double yAdj = ((Double)locInfo.elementAt(2)).doubleValue();
        double scale = (xAdj + yAdj)/2.0;
        
        loc.y = loc.y - (int)(12*yAdj);

        int fontSize = (int)(10*scale);

        if (fontSize < 6)
            fontSize = 6;
        if (fontSize > 12)
            fontSize = 12;

        int dotSize = fontSize;

        if (dotSize > 12)
            dotSize = 12;
        if (dotSize < 6)
            dotSize = 6;

        int letterAdjX = (2*dotSize)/12;
        int letterAdjY = (2*dotSize)/12;

        Font f2 = new Font("Monospaced",Font.PLAIN,fontSize);
        g.setFont(f2);

        BENode me = (BENode)ben.getExtraData("Me");
        BENodeOrSubNet nos = (BENodeOrSubNet)me.getExptData("BENodeExchange");

        if (nos.getCoalition().getCoalitionInfoLevel() == 2)
            {
            m_coalition.drawCoalitionInfo(g, locInfo, ben);
            }
        else if (nos.getCoalition().getCoalitionInfoLevel() == 1)
            {
            if (nos.getCoalition().getCoalitionType().equals(m_coalition.getCoalitionType()))
                {
                if (nos.getCoalition().getCoalition() == m_coalition.getCoalition())
                    {
                    m_coalition.drawCoalitionInfo(g, locInfo, ben);
                    }
                }
            }
        // g.setColor(BEColor.black);
        // g.drawString(""+m_Max+"/"+m_Min,loc.x-4,15+(int)(loc.y+15*yAdj));

        g.setColor(this.getDisplayColor(m_Max,m_Min));
        g.fillOval(loc.x,15+(int)(loc.y+15*yAdj),dotSize,dotSize);

        Enumeration enm = m_network.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();

            if (edge.getNode1() == m_node.getID())
                {
                BENodeOrSubNet ns = (BENodeOrSubNet)m_node.getExptData("BENodeExchange");
                Point newLoc = computeDisplayPoint(edge.getN1Anchor(),edge.getN2Anchor(),loc,xAdj,yAdj);
                g.setColor(ns.getDisplayColor(edge));
                g.fillOval(newLoc.x,newLoc.y,dotSize,dotSize);
                g.setColor(BEColor.NS_LETT);
                g.drawString(""+ns.getSubnetworkID(edge),newLoc.x+letterAdjX,newLoc.y+dotSize-letterAdjY);
                }
            if (edge.getNode2() == m_node.getID())
                {
                BENodeOrSubNet ns = (BENodeOrSubNet)m_node.getExptData("BENodeExchange");
                Point newLoc = computeDisplayPoint(edge.getN2Anchor(),edge.getN1Anchor(),loc,xAdj,yAdj);
                g.setColor(ns.getDisplayColor(edge));
                g.fillOval(newLoc.x,newLoc.y,dotSize,dotSize);
                g.setColor(BEColor.NS_LETT);
                g.drawString(""+ns.getSubnetworkID(edge),newLoc.x+letterAdjX,newLoc.y+dotSize-letterAdjY);
                }
            }
        }
    // Draw info on a canvas using the graphic for the experimenter screen.
    public void drawExpt(Graphics g, Vector locInfo) 
        {
        Point loc = (Point)locInfo.elementAt(0);
        double xAdj = ((Double)locInfo.elementAt(1)).doubleValue();
        double yAdj = ((Double)locInfo.elementAt(2)).doubleValue();
        double scale = (xAdj + yAdj)/2.0;
        
        loc.y = loc.y - (int)(4*yAdj);

        int fontSize = (int)(10*scale);

        if (fontSize < 6)
            fontSize = 6;
        if (fontSize > 12)
            fontSize = 12;

        int dotSize = fontSize;

        if (dotSize > 12)
            dotSize = 12;
        if (dotSize < 6)
            dotSize = 6;

        int letterAdjX = (2*dotSize)/12;
        int letterAdjY = (2*dotSize)/12;

        Font f2 = new Font("Monospaced",Font.PLAIN,fontSize);
        g.setColor(BEColor.black);
        g.setFont(f2);
        g.drawString(""+m_Max+"/"+m_Min,loc.x,15+(int)(loc.y+15*yAdj));

        Enumeration enm = m_network.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
 
            if (edge.getNode1() == m_node.getID())
                {
                BENode node = (BENode)m_network.getNode(edge.getNode2());
                BENodeOrSubNet ns = (BENodeOrSubNet)node.getExptData("BENodeExchange");
                Point newLoc = computeDisplayPoint(edge.getN1Anchor(),edge.getN2Anchor(),loc,xAdj,yAdj);
                g.setColor(ns.getDisplayColor(edge));
                g.fillOval(newLoc.x,newLoc.y,dotSize,dotSize);
                g.setColor(BEColor.NS_LETT);
                g.drawString(""+ns.getSubnetworkID(edge),newLoc.x+letterAdjX,newLoc.y+dotSize-letterAdjY);
                }
            if (edge.getNode2() == m_node.getID())
                {
                BENode node = (BENode)m_network.getNode(edge.getNode1());
                BENodeOrSubNet ns = (BENodeOrSubNet)node.getExptData("BENodeExchange");
                Point newLoc = computeDisplayPoint(edge.getN2Anchor(),edge.getN1Anchor(),loc,xAdj,yAdj);
                g.setColor(ns.getDisplayColor(edge));
                g.fillOval(newLoc.x,newLoc.y,dotSize,dotSize);
                g.setColor(BEColor.NS_LETT);
                g.drawString(""+ns.getSubnetworkID(edge),newLoc.x+letterAdjX,newLoc.y+dotSize-letterAdjY);
                }
            }
        }
  // Draw info on a canvas using the graphic for the observer screen.
    public void drawObserver(Graphics g, Vector locInfo) 
        {
        Point loc = (Point)locInfo.elementAt(0);
        double xAdj = ((Double)locInfo.elementAt(1)).doubleValue();
        double yAdj = ((Double)locInfo.elementAt(2)).doubleValue();
        double scale = (xAdj + yAdj)/2.0;
        
        loc.y = loc.y - (int)(4*yAdj);

        int fontSize = (int)(10*scale);

        if (fontSize < 6)
            fontSize = 6;
        if (fontSize > 12)
            fontSize = 12;

        int dotSize = fontSize;

        if (dotSize > 12)
            dotSize = 12;
        if (dotSize < 6)
            dotSize = 6;

        int letterAdjX = (2*dotSize)/12;
        int letterAdjY = (2*dotSize)/12;

        Font f2 = new Font("Monospaced",Font.PLAIN,fontSize);
        g.setColor(BEColor.black);
        g.setFont(f2);
        g.drawString(""+m_Max+"/"+m_Min,loc.x,(int)(loc.y+15*yAdj));
   
        Enumeration enm = m_network.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            
            if (edge.getNode1() == m_node.getID())
                {
                BENode node = (BENode)m_network.getNode(edge.getNode2());
                BENodeOrSubNet ns = (BENodeOrSubNet)node.getExptData("BENodeExchange");
                Point newLoc = computeDisplayPoint(edge.getN1Anchor(),edge.getN2Anchor(),loc,xAdj,yAdj);
                g.setColor(ns.getDisplayColor(edge));
                g.fillOval(newLoc.x,newLoc.y,dotSize,dotSize);
                g.setColor(BEColor.NS_LETT);
                g.drawString(""+ns.getSubnetworkID(edge),newLoc.x+letterAdjX,newLoc.y+dotSize-letterAdjY);
                }
            if (edge.getNode2() == m_node.getID())
                {
                BENode node = (BENode)m_network.getNode(edge.getNode1());
                BENodeOrSubNet ns = (BENodeOrSubNet)node.getExptData("BENodeExchange");
                Point newLoc = computeDisplayPoint(edge.getN2Anchor(),edge.getN1Anchor(),loc,xAdj,yAdj);
                g.setColor(ns.getDisplayColor(edge));
                g.fillOval(newLoc.x,newLoc.y,dotSize,dotSize);
                g.setColor(BEColor.NS_LETT);
                g.drawString(""+ns.getSubnetworkID(edge),newLoc.x+letterAdjX,newLoc.y+dotSize-letterAdjY);
                }
            }
        }
  
    public BECoalition getCoalition()
        {
        return m_coalition;
        }
    public String getComponentName()
        {
        return "BENodeExchange";
        }
    public Color getDisplayColor(BEEdge edge)
        {
        int type = getExchangeType(edge);

        if (type == 0)
            return BEColor.INACTIVE_EDGE;
        if (type == 1)
            return BEColor.COMPLETE_EDGE;
        if (type == 2)
            return BEColor.INCLUSIVE;
        if (type == 3)
            return BEColor.NULL;
        if (type == 4)
            return BEColor.EXCLUSIVE;
        if (type == 5)
            return BEColor.INCLUSIVE_EXCLUSIVE;
        if (type == 6)
            return BEColor.NULL_INCLUSIVE;
        if (type == 7)
            return BEColor.SINGLE_CONN;

        return BEColor.black;
        }
    public Color getDisplayColor(int max, int min)
        {
        int type = getExchangeType(max,min);

        if (type == 0)
            return BEColor.INACTIVE_EDGE;
        if (type == 1)
            return BEColor.COMPLETE_EDGE;
        if (type == 2)
            return BEColor.INCLUSIVE;
        if (type == 3)
            return BEColor.NULL;
        if (type == 4)
            return BEColor.EXCLUSIVE;
        if (type == 5)
            return BEColor.INCLUSIVE_EXCLUSIVE;
        if (type == 6)
            return BEColor.NULL_INCLUSIVE;
        if (type == 7)
            return BEColor.SINGLE_CONN;

        return BEColor.black;
        }
// 0 -> inactive, 1 -> completed, 2 -> inclusive, 3-> null, 4 -> exclusive, 5 -> inclusive and exclusive
    public int getExchangeType(BEEdge edge)
        {
        if (edge.getCompleted())
            return 1;

        if (!edge.getActive())
            return 0;

        int theirNode = 0;
        if (edge.getNode1() == m_node.getID())
            theirNode = edge.getNode2();
        else
            theirNode = edge.getNode1();

        Enumeration enm = m_subnetworks.elements();
        while (enm.hasMoreElements())
            {
            BEOrSubnetwork sn = (BEOrSubnetwork)enm.nextElement();
            if (sn.containsNode(theirNode))
                {
                if (sn.getActive())
                    {
                    return sn.getExchangeType(theirNode);
                    }
                 else
                    {
                    if (sn.getSubnetworks() == null)
                        {
                        int max = m_subnetworks.size();
                        if ((max == 1) && (m_Min == 1))
                            return 7; // Single Connected
                        else if ((m_Max == max) && (m_Min == 1))
                            return 3; // Null
                        else if ((m_Max == max) && (m_Min > 1) && (m_Min != m_Max))
                            return 6; // Null-inclusive
                        else if ((m_Max < max) && (m_Min > 1))
                            return 5; // exlusive and inclusive
                        else if ((m_Max < max) && (m_Min == 1))
                            return 4; // exclusive
                        else
                            return 2; // must be inclusive.
                        }
                    else
                        {
                        int max = sn.getSubnetworks().size();
                        if ((max == 1) && (m_Min == 1))
                            return 7; // Single Connected
                        else if ((sn.getMax() == max) && (sn.getMin() == 1))
                            return 3; // Null
                        else if ((sn.getMax() == max) && (sn.getMin() > 1) && (sn.getMin() != max))
                            return 6; // Null-Inclusive
                        else if ((sn.getMax() < max) && (sn.getMin() > 1))
                            return 5; // exlusive and inclusive
                        else if ((sn.getMax() < max) && (sn.getMin() == 1))
                            return 4; // exclusive
                        else
                            return 2; // must be inclusive.
                        }
                    }
                }
            }
        return 0; 
        }
    public int getExchangeType(int max, int min)
        {
        if (m_subnetworks.size() == 1)
            return 7; // Single Connected
        else if ((max == m_subnetworks.size()) && (min == 1))
            return 3; // Null
        else if ((max == m_subnetworks.size()) && (min > 1) && (min != max))
            return 6;  // Null-inclusive.
        else if ((max < m_subnetworks.size()) && (min > 1))
            return 5; // exlusive and inclusive
        else if ((max < m_subnetworks.size()) && (m_Min == 1))
            return 4; // exclusive
        else
            return 2; // must be inclusive.
        }
    public Vector getOrdering()
        {
        return m_ordering;
        }
    public double getResourcesEarned(BENetwork net)
        {
        double per = 0;

        if ((m_coalition.getSharing()) && (m_coalition.getFormed()) && (m_coalition.getJoined()))
            {
            double coalitionRes = 0;
            int members = 0;
            Enumeration enm = net.getNodeList().elements();
            while (enm.hasMoreElements())
                {
                BENode nTmp = (BENode)enm.nextElement();
                BENodeOrSubNet nos = (BENodeOrSubNet)nTmp.getExptData("BENodeExchange");
                if (m_coalition.getCoalitionType().equals(nos.getCoalition().getCoalitionType()))
                    {
                    if (m_coalition.getCoalition() == nos.getCoalition().getCoalition())
                        {
                        if (nos.getCoalition().getJoined())
                            {
                            coalitionRes = coalitionRes + m_coalition.getEarnedCoalResources(nos,net);
                            members++;
                            }
                        }
                    }
                }
            int resEarned = (int)Math.round((coalitionRes*1.0)/members);

            m_coalition.setCoalEarnings(resEarned);

            enm = net.getEdgeList().elements();
            while (enm.hasMoreElements())
                {
                BEEdge edge = (BEEdge)enm.nextElement();
                BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
                if ((edge.getNode1() == m_node.getID()) && (beer.getExchange() != null))
                    {
                    boolean share = m_coalition.getShareList(edge.getNode2());
                    if ((!share) && (beer.getExchange().getNode1().getResource() > 0))
                        resEarned = resEarned + beer.getExchange().getNode1().getIntResource();
                    }
                if ((edge.getNode2() == m_node.getID()) && (beer.getExchange() != null))
                    {
                    boolean share = m_coalition.getShareList(edge.getNode1());
                    if ((!share) && (beer.getExchange().getNode2().getResource() > 0))
                        resEarned = resEarned + beer.getExchange().getNode2().getIntResource();
                    }
                }
            per = resEarned;
            }
        else
            {
            per = super.getResourcesEarned(net);

            if ((m_coalition.getFormed()) && (m_coalition.getJoined()))
                {
                m_coalition.setCoalEarnings(m_coalition.getEarnedCoalResources(this,net));
                }
            }
        return per;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = super.getSettings();

        settings.put("Type","DB-BENodeOrSubNet");

        settings.put("ShowInfoWindow",new Boolean(m_showInfoWindow));
        Vector subnetworks = new Vector();
        Enumeration enm = m_subnetworks.elements();
        while (enm.hasMoreElements())
            {
            BEOrSubnetwork sn = (BEOrSubnetwork)enm.nextElement();
            subnetworks.addElement(sn.getSettings());
            }
        settings.put("Subnetworks",subnetworks);
        settings.put("Ordering",m_ordering);
        settings.put("Coalition",m_coalition.getSettings());

        return settings;
        }
    public boolean getShowInfoWindow()
        {
        return m_showInfoWindow;
        }
    public BEStateAction getStateAction(BENetwork ben)
        {
        double currentState = ((Double)ben.getExtraData("CurrentState")).doubleValue();
        Boolean rr = (Boolean)ben.getExtraData("RoundRunning");

        if (currentState < BEPeriod.START_ROUND_STATE)
            return new BEJoinCoalitionWindowStateAction();
        else if ((currentState < BEPeriod.END_ROUND_STATE) && (rr.booleanValue()))
            {
            if (m_showInfoWindow)
                return new BENodeOrSubnetWindowStateAction();
            }
        else if  ((currentState < BEPeriod.END_ROUND_STATE) && (!rr.booleanValue()))
            return new BEZapWindowStateAction();
        else
            return null;

        return null;
        }
    public double getStatePoint(BENetwork ben)
        {
        double currentState = ((Double)ben.getExtraData("CurrentState")).doubleValue();
        Boolean rr = (Boolean)ben.getExtraData("RoundRunning");

        if (currentState < BEPeriod.START_ROUND_STATE)
            return COAL_VOTE_STATE;
        else if ((currentState < BEPeriod.END_ROUND_STATE) && (rr.booleanValue()))
            {
            if (m_showInfoWindow)
                return SN_INFO_WIN_STATE;
            }
        else if  ((currentState < BEPeriod.END_ROUND_STATE) && (!rr.booleanValue()))
            return COAL_ZAP_STATE;
        else
            return 100;

        return 100;
        }
    public BEOrSubnetwork getSubnetwork(int value)
        {
        return (BEOrSubnetwork)m_subnetworks.get(new Integer(value));
        }
    public String getSubnetworkID(BEEdge edge)
        {
        if (edge.getCompleted())
            return "";

        if (!edge.getActive())
            return "";

        int theirNode = 0;
        if (edge.getNode1() == m_node.getID())
            theirNode = edge.getNode2();
        else
            theirNode = edge.getNode1();

        Enumeration enm = m_subnetworks.elements();
        while (enm.hasMoreElements())
            {
            BEOrSubnetwork sn = (BEOrSubnetwork)enm.nextElement();
            if (sn.containsNode(theirNode))
                {
                if (sn.getActive())
                    {
                    int sub = sn.getSubnetwork() + 1;
                    String str = new String(""+sub);
                    return sn.getSubnetworkID(edge.getNode2(),str);
                    }
                else
                    {
                    int sub = sn.getSubnetwork() + 1;
                    return new String(""+sub);
                    }
                }
            }
        return "";
        }
    public Hashtable getSubnetworks()
        {
        return m_subnetworks;
        }
    public boolean getToKeep(int toNode)
        {
        if (m_exchanges >= m_Min)
            {
            Enumeration enm = m_subnetworks.keys();
            while (enm.hasMoreElements())
                {
                Object obj = enm.nextElement();
                if (obj instanceof Integer)
                    {
                    BEOrSubnetwork sn = (BEOrSubnetwork)m_subnetworks.get(obj);
                    if (sn.containsNode(toNode))
                        return sn.getToKeep(toNode);
                    }
                }
            return true; // This should not get called.
            }
        else
            return false;
        }
    
  // Initialize some or all of the network based on the data values of the object.
    public void initializeNetwork() 
        {
        m_exchanges = 0;
        Enumeration enm = m_subnetworks.keys();
        while (enm.hasMoreElements())
            {
            Object obj = enm.nextElement();
            if (obj instanceof Integer)
                {
                BEOrSubnetwork sn = (BEOrSubnetwork)m_subnetworks.get(obj);
                sn.initializeNetwork();
                }
            }
        BENetwork ben = (BENetwork)m_network;

        Vector v = (Vector)m_ordering.elementAt(0);
        Integer n = (Integer)v.elementAt(0);

        if (n.intValue() == -1)
            return;

        enm = ben.getEdgeList().elements(); 
        while (enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();

            if ((edge.getNode1() == m_node.getID()) || (edge.getNode2() == m_node.getID()))
                {            
                boolean flag = true;

                Enumeration enum2 = v.elements();
                while (enum2.hasMoreElements())
                    {
                    n = (Integer)enum2.nextElement();

                    if ((edge.getNode1() == n.intValue()) || (edge.getNode2() == n.intValue()))
                        {
                        flag = false;
                        break;
                        }
                    }
                if (flag)
                    edge.setActive(false);
                }        
            }
        }

    public boolean isEdgeActive(BEEdge edge)
        {
        if (edge.getCompleted())
            return false;

        if (edge.getNode1() == m_node.getID())
            {
            Enumeration enm = m_subnetworks.elements();
            while (enm.hasMoreElements())
                {
                BEOrSubnetwork sn = (BEOrSubnetwork)enm.nextElement();
                if (sn.containsNode(edge.getNode2()))
                    {
                    if (sn.getActive())
                        {
                        return sn.isEdgeActive(edge.getNode2());
                        }
                    else
                        {
                        if (m_exchanges == m_Max)
                            return false;
                        else
                            {
                            Vector v = (Vector)m_ordering.elementAt(m_exchanges);
                            if (v.contains(new Integer(-1)))
                                return true;
                            if (v.contains(new Integer(edge.getNode2())))
                                return true;
                            return false;
                            }
                        }
                    }
                }
            }
        if (edge.getNode2() == m_node.getID())
            {
            Enumeration enm = m_subnetworks.elements();
            while (enm.hasMoreElements())
                {
                BEOrSubnetwork sn = (BEOrSubnetwork)enm.nextElement();
                if (sn.containsNode(edge.getNode1()))
                    {
                    if (sn.getActive())
                        {
                        return sn.isEdgeActive(edge.getNode1());
                        }
                    else
                        {
                        if (m_exchanges == m_Max)
                            return false;
                        else
                            {
                            Vector v = (Vector)m_ordering.elementAt(m_exchanges);
                            if (v.contains(new Integer(-1)))
                                return true;
                            if (v.contains(new Integer(edge.getNode1())))
                                return true;
                            return false;
                            }
                        }
                    }
                }
            }
        return true; 
        }

  // Reset object data to starting values.
    public void reset() 
        {
        Hashtable tmp = new Hashtable();

        m_exchanges = 0;
        m_Max = 1;
        m_Min = 1;
        int i = 0;
        Enumeration enm = m_subnetworks.keys();
        while (enm.hasMoreElements())
            {
            Object obj = enm.nextElement();
            if (obj instanceof Integer)
                {
                BEOrSubnetwork sn = (BEOrSubnetwork)m_subnetworks.get(obj);
                int[] nodes = sn.getNodes();
                for (int x=0;x<nodes.length;x++)
                    {
                    tmp.put(new Integer(i),new BEOrSubnetwork(i,nodes[x]));
                    i++;
                    }
                sn.cleanUp();
                }
            }
        m_subnetworks.clear();
        m_subnetworks = tmp;
        }

    public Hashtable retrieveData(WLServerConnection wlsc, ExptMessage em, BaseDataInfo bdi)
        {
        try 
            {
            Hashtable erData = new Hashtable();

     // First get the join votes data.
            LoadDataResultsReq tmp = new LoadDataResultsReq("beDB","BEExpt_StaticVJ_Data_T",bdi,wlsc,em);

            ResultSet rs = tmp.runQuery();

            Vector data = new Vector();
            erData.put("VJ",data);

            if (rs != null)
                {
                while (rs.next())
                    {
                    BEStaticVJOutputObject svj = new BEStaticVJOutputObject(rs);
                    data.addElement(svj);
                    }
                }
  
    // Next get the proposed offer data.
            tmp = new LoadDataResultsReq("beDB","BEExpt_StaticOff_Data_T",bdi,wlsc,em);

            rs = tmp.runQuery();

            data = new Vector();
            erData.put("Off",data);

            if (rs != null)
                {
                while (rs.next())
                    {
                    BEStaticOffOutputObject soff = new BEStaticOffOutputObject(rs);
                    data.addElement(soff);
                    }
                }

    // Next get the zap vote data.
            tmp = new LoadDataResultsReq("beDB","BEExpt_StaticVZ_Data_T",bdi,wlsc,em);

            rs = tmp.runQuery();

            data = new Vector();
            erData.put("VZ",data);

            if (rs != null)
                {
                while (rs.next())
                    {
                    BEStaticVZOutputObject svz = new BEStaticVZOutputObject(rs);
                    data.addElement(svz);
                    }
                }

    // Next get the earnings data.
            tmp = new LoadDataResultsReq("beDB","BEExpt_CoalNE_Data_T",bdi,wlsc,em);

            rs = tmp.runQuery();

            data = new Vector();
            erData.put("NE",data);

            if (rs != null)
                {
                while (rs.next())
                    {
                    BECoalNEOutputObject sne = new BECoalNEOutputObject(rs);
                    data.addElement(sne);
                    }
                }
            return erData;
            }
        catch(Exception e) 
            {
            wlsc.addToLog(e.getMessage());
            return new Hashtable();
            }
        }

    public void setCoalition(BECoalition bec)
        {
        m_coalition = bec;
        }
    public void setExchanges(int value)
        {
        m_exchanges = value;
        }
    public void setMax(int value)
        {
        m_Max = value;
        }
    public void setMin(int value)
        {
        m_Min = value;
        }
    protected void setOrdering(Vector v)
        {
        m_ordering = v;
        }
    public void setShowInfoWindow(boolean value)
        {
        m_showInfoWindow = value;
        }
    public static void updateDBEntry(Connection con)
        {
        try
            { 
            // get a Statement object from the Connection
            //
            // Place new Simulant Type objects.
            Statement stmt = con.createStatement();
                    
            ResultSet rs = stmt.executeQuery("SELECT Object_ID_INT FROM Other_Objects_T WHERE Object_Name_VC = 'DB-BENodeOrSubNet'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Object_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Other_Objects_T SET Object_OBJ = ? WHERE Object_ID_INT = "+index);

                BENodeOrSubNet ns = new BENodeOrSubNet();

		System.err.println(ns);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,ns,ps);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Desc, Network, id
                CallableStatement cs = con.prepareCall("{call up_insert_JOtherObject (?, ?, ?, ?)}");

                BENodeOrSubNet ns = new BENodeOrSubNet();

System.err.println(ns);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows you to divide up a network into ordered subnetworks from the perspective of the node.");

                cs.setString(1,"DB-BENodeOrSubNet");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,ns,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("BENodeOrSubNet Object ID: "+cs.getInt(4));
                FMSObjCon.cleanUp(v);
                }
            }
        catch( Exception e ) 
            {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(0);
            }
        }
  // Update network data based on the action info for this data object.
    public void updateNetwork(Object obj) 
        {
        int toNode = -1;
        BEEdge edge = (BEEdge)obj;
        if (edge.getNode1() == m_node.getID())
            {
            toNode = edge.getNode2();
            }
        else
            {
            toNode = edge.getNode1();
            }
        Enumeration enm = m_subnetworks.keys();
        while (enm.hasMoreElements())
            {
            Object obj2 = enm.nextElement();
            if (obj2 instanceof Integer)
                {
                BEOrSubnetwork sn = (BEOrSubnetwork)m_subnetworks.get(obj2);
                if (sn.containsNode(toNode))
                    {
                    if (sn.getActive())
                        {
                        sn.updateNetwork(toNode);
                        }
                    else
                        {
                        m_exchanges++;
                        sn.setActive(true);
                        sn.updateNetwork(toNode);
                        }
                    return;
                    }
                }
            }
        }
    }
