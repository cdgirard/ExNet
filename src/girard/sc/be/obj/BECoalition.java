package girard.sc.be.obj;

import girard.sc.be.awt.BEColor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This object allows for coalitions to be defined within a BE Network.
 * <p>
 * <br> Started: 07-01-2003
 * <p>
 * @author Dudley Girard
 */

public class BECoalition implements Cloneable,Serializable 
    {
    protected String    m_coalitionType = "None";
    protected int       m_coalition = 1;
    protected int       m_idColor = 0;
    protected boolean   m_sharing = false;
/**
 * Keeps track of which relations are part of the public resource pool and private
 * resource pool for this node.
 */
    protected Hashtable m_shareList = new Hashtable();
/**
 * How much information the player has; 0 means no information, 1 means
 * information only about his coalition, 2 means information about all
 * coalitions.
 */
    protected int       m_coalitionInfoLevel = 0;
    protected boolean   m_zapping = false;
/**
 * Stored as a number between 0 and 1 that represents the precentage of
 * coalition resources that must be spent to zap free-riders.
 */
    protected double    m_zapCost = 0;
/**
 * Stored as a number between 0 and 1 that represents the precentage of
 * free-rider resources that are removed if they are zapped.
 */
    protected double    m_zapAmount = 0;
/**
 * How many coalition members need to vote to get one zap vote, if set to
 * zero then all need to vote yes.
 */
    protected int       m_numNeededToZap = 1;
/**
 * How many free-riders are zapped per zap vote; free-riders are zapped
 * based on amount of earnings; if set to 0 then all are zapped.
 */
    protected int       m_numZapped = 1;
/**
 * How many people need to want to join the coalition so it will form; If value
 * is zero then need everyone to join for coalition to form.
 */
    protected int       m_numNeededToJoin = 0;
/**
 * Used to determine what report method will be used when telling players how
 * many tried to join the coalition, if set to 0 then reports the actual value.
 */
    protected int       m_reportMethod = 0;

// Experiment variables
/**
 * Did the coalition form the last round.
 */
    protected boolean m_formed = false;
/**
 * Which coalition do they belong to within the dynamic coalition group that
 * they are in.
 */
    protected int       m_dynamicCoal = -1;
/**
 * How much did you earn as part of the coalition last round.
 */
    protected int m_coalEarnings = 0;
/**
 * Did you join the coalition last round.
 */
    protected boolean m_joined = false;
/**
 * Did I zap someone last round.
 */
    protected boolean m_zapped = false;
/**
 * How much did I want to offer as part of the coalition this round.
 */
    protected int m_coalOffer = 0;


    public BECoalition()
        {
        }
    public BECoalition(int coal)
        {
        m_coalition = coal;
        }

    public void applySettings(Hashtable h)
        {
        m_coalitionType = (String)h.get("Coalition Type");
        m_coalition = ((Integer)h.get("Coalition")).intValue();
        m_idColor = ((Integer)h.get("ID Color")).intValue();
        m_sharing = ((Boolean)h.get("Sharing")).booleanValue();
        m_shareList = (Hashtable)h.get("ShareList");
        m_coalitionInfoLevel = ((Integer)h.get("Info Level")).intValue();
        m_zapping = ((Boolean)h.get("Zapping")).booleanValue();
        m_zapCost = ((Double)h.get("Zap Cost")).doubleValue();
        m_zapAmount = ((Double)h.get("Zap Amount")).doubleValue();
        m_numNeededToZap = ((Integer)h.get("Number per Zap")).intValue();
        m_numZapped = ((Integer)h.get("Number Zapped")).intValue();
        m_numNeededToJoin = ((Integer)h.get("Needed to Join")).intValue();
        m_reportMethod = ((Integer)h.get("Report Method")).intValue();
        }

    public boolean areZappableFreeRiders(BENodeOrSubNet nos, BENetwork net)
        {
        boolean flag;

        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode tmpNode = (BENode)enm.nextElement();
            BENodeOrSubNet tmpNOS = (BENodeOrSubNet)tmpNode.getExptData("BENodeExchange");

            if (nos.getCoalition().getCoalitionType().equals(tmpNOS.getCoalition().getCoalitionType()))
                {
                if ((nos.getCoalition().getCoalition() == tmpNOS.getCoalition().getCoalition()) && (nos.getCoalition().getFormed()))
                    {
                    if ((!tmpNOS.getCoalition().getJoined()) && (getEarnedCoalResources(tmpNOS,net) > 0))
                        {
                        return true;
                        }
                    }
                }
            }
        return false;
        }

/**
 * Assess the costs for zapping free riders.
 */
    public void assessCosts(BENodeOrSubNet nos, BENetwork net)
        {
        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode tmpNode = (BENode)enm.nextElement();
            BENodeOrSubNet tmpNOS = (BENodeOrSubNet)tmpNode.getExptData("BENodeExchange");

            if (m_coalitionType.equals(tmpNOS.getCoalition().getCoalitionType()))
                {
                if (m_coalition == tmpNOS.getCoalition().getCoalition())
                    {
                    if (tmpNOS.getCoalition().getJoined())
                        {
                        payForCosts(tmpNOS,net);
                        }
                    }
                }
            }
        }
/**
 * Zaps the earnings of the free riders.
 */
    public void assessZaps(Vector freeRiders, int numZapped, BENetwork net)
        {
        for (int x=0;x<numZapped;x++)
            {
            BENode node = (BENode)freeRiders.elementAt(x);
            reduceFreeRiderEarnings(node,net);
            }
        }

    public Object clone()
        {
        BECoalition tmpCoal = new BECoalition();
        
        tmpCoal.applySettings(getSettings());

        tmpCoal.setShareList(new Hashtable());
        Enumeration enm = m_shareList.keys();
        while (enm.hasMoreElements())
            {
            Integer n = (Integer)enm.nextElement();
            Boolean sv = (Boolean)m_shareList.get(n);
            tmpCoal.setShareList(n.intValue(),sv.booleanValue());
            }
        tmpCoal.setFormed(m_formed);
        tmpCoal.setDynamicCoal(m_dynamicCoal);
        tmpCoal.setCoalEarnings(m_coalEarnings);
        tmpCoal.setJoined(m_joined);
        tmpCoal.setZapped(m_zapped);
        tmpCoal.setCoalOffer(m_coalOffer);

        return tmpCoal;
        }

    public void drawCoalitionInfo(Graphics g, Vector locInfo, BENetwork ben)
        {
        Point nodeLoc = (Point)locInfo.elementAt(0);
        Point loc = new Point(nodeLoc.x,nodeLoc.y);

        Boolean rr = (Boolean)ben.getExtraData("RoundRunning");
        
        if ((rr.booleanValue()) && (m_formed) && (m_joined))
            {
            g.setColor(getDisplayColor());
            g.drawOval(loc.x-5,loc.y - 15,20,20);
            g.drawOval(loc.x-4,loc.y - 14,18,18);
            }
        }

    public int getCoalEarnings()
        {
        return m_coalEarnings;
        }
    public int getCoalition()
        {
        return m_coalition;
        }
    public int getCoalitionInfoLevel()
        {
        return m_coalitionInfoLevel;
        }
    public int getCoalOffer()
        {
        return m_coalOffer;
        }
    public int getCoalOfferAmt(BENetwork net)
        {
        int offerAmt = 0;

        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode nTmp = (BENode)enm.nextElement();
            BENodeOrSubNet nos = (BENodeOrSubNet)nTmp.getExptData("BENodeExchange");
            if (nos.getCoalition().getCoalitionType().equals(m_coalitionType))
                {
                if ((nos.getCoalition().getCoalition() == m_coalition) && (nos.getCoalition().getJoined()))
                    {
                    offerAmt = offerAmt + nos.getCoalition().getCoalOffer();
                    }
                }
            }
        return offerAmt;
        }
    public String getCoalitionType()
        {
        return m_coalitionType;
        }
    public Color getDisplayColor()
        {
        if (m_idColor == 0)
            return BEColor.INACTIVE_EDGE;
        if (m_idColor == 1)
            return BEColor.COMPLETE_EDGE;
        if (m_idColor == 2)
            return BEColor.INCLUSIVE;
        if (m_idColor == 3)
            return BEColor.NULL;
        if (m_idColor == 4)
            return BEColor.EXCLUSIVE;
        if (m_idColor == 5)
            return BEColor.INCLUSIVE_EXCLUSIVE;
        if (m_idColor == 6)
            return BEColor.NULL_INCLUSIVE;
        if (m_idColor == 7)
            return BEColor.SINGLE_CONN;

        return BEColor.black;
        }
    public Color getDisplayColor(int type)
        {
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
    public int getEarnedCoalResources(BENodeOrSubNet nos, BENetwork net)
        {
        BENode me = nos.getNode();
        int resTotal = 0;

        Enumeration enm = net.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
            if ((edge.getNode1() == me.getID()) && (beer.getExchange() != null))
                {
                boolean share = nos.getCoalition().getShareList(edge.getNode2());
System.err.println("SH1: "+share);
                if ((share) && (beer.getExchange().getNode1().getResource() > 0))
                    resTotal = resTotal + beer.getExchange().getNode1().getIntResource();
                }
            if ((edge.getNode2() == me.getID()) && (beer.getExchange() != null))
                {
                boolean share = nos.getCoalition().getShareList(edge.getNode1());
System.err.println("SH2: "+share);
                if ((share) && (beer.getExchange().getNode2().getResource() > 0))
                    resTotal = resTotal + beer.getExchange().getNode2().getIntResource();
                }
            }
        return resTotal;
        }

    public boolean getFormed()
        {
        return m_formed;
        }
    public int getIDColor()
        {
        return m_idColor;
        }
    public boolean getJoined()
        {
        return m_joined;
        }
    public int getNumCoalMembers(BENetwork net)
        {
        int members = 0;

        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode nTmp = (BENode)enm.nextElement();
            BENodeOrSubNet nos = (BENodeOrSubNet)nTmp.getExptData("BENodeExchange");
            if (nos.getCoalition().getCoalitionType().equals(m_coalitionType))
                {
                if (nos.getCoalition().getCoalition() == m_coalition)
                    {
                    members++;
                    }
                }
            }
        return members;
        }
    public int getNumJoinedCoalMembers(BENetwork net)
        {
        int members = 0;

        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode nTmp = (BENode)enm.nextElement();
            BENodeOrSubNet nos = (BENodeOrSubNet)nTmp.getExptData("BENodeExchange");
            if (nos.getCoalition().getCoalitionType().equals(m_coalitionType))
                {
                if ((nos.getCoalition().getCoalition() == m_coalition) && (nos.getCoalition().getJoined()))
                    {
                    members++;
                    }
                }
            }
        return members;
        }
    public int getNumJoinVotes(BENetwork net)
        {
        int votes = 0;
        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode nTmp = (BENode)enm.nextElement();
            BENodeOrSubNet nos = (BENodeOrSubNet)nTmp.getExptData("BENodeExchange");
            if (nos.getCoalition().getCoalitionType().equals(m_coalitionType))
                {
                if ((nos.getCoalition().getCoalition() == m_coalition) && (nos.getCoalition().getJoined()))
                    {
                    votes++;
                    }
                }
            }
        return votes;
        }
    public int getNumZapVotes(BENetwork net)
        {
        int votes = 0;
        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode nTmp = (BENode)enm.nextElement();
            BENodeOrSubNet nos = (BENodeOrSubNet)nTmp.getExptData("BENodeExchange");
            if (nos.getCoalition().getCoalitionType().equals(m_coalitionType))
                {
                if ((nos.getCoalition().getCoalition() == m_coalition) && (nos.getCoalition().getZapped()))
                    {
                    votes++;
                    }
                }
            }
        return votes;
        }
    public int getNumNeededToJoin()
        {
        return m_numNeededToJoin;
        }
    public int getNumNeededToZap()
        {
        return m_numNeededToZap;
        }
    public int getNumZapped()
        {
        return m_numZapped;
        }
    public int getNumberZapped(int votes, int members, int max)
        {
        if (m_numNeededToZap == 0)
            {
            if (votes == members)
                {
                if (m_numZapped == 0)
                    {
                    return max;
                    }
                else
                    {
                    if (max > m_numZapped)
                        return m_numZapped;
                    else
                        return max;
                    }
                }
            else
                {
                return 0;
                }
            }
        else
            {
            int zaps = (int)Math.floor((1.0*votes)/m_numNeededToZap);

            if (zaps == 0)
                return 0;

            if (m_numZapped == 0)
                {
                return max;
                }
            else
                {
                int numZapped = zaps*m_numZapped;
                if (numZapped > max)
                    {
                    return max;
                    }
                else
                    {
                    return numZapped;
                    }
                }
            }
        }
    public int getReportMethod()
        {
        return m_reportMethod;
        }
    public Hashtable getSettings()
        {
        Hashtable h = new Hashtable();

        h.put("Coalition Type",m_coalitionType);
        h.put("Coalition",new Integer(m_coalition));
        h.put("ID Color",new Integer(m_idColor));
        h.put("Sharing",new Boolean(m_sharing));
        h.put("ShareList",m_shareList);
        h.put("Info Level",new Integer(m_coalitionInfoLevel));
        h.put("Zapping",new Boolean(m_zapping));
        h.put("Zap Cost",new Double(m_zapCost));
        h.put("Zap Amount",new Double(m_zapAmount));
        h.put("Number per Zap",new Integer(m_numNeededToZap));
        h.put("Number Zapped",new Integer(m_numZapped));
        h.put("Needed to Join",new Integer(m_numNeededToJoin));
        h.put("Report Method",new Integer(m_reportMethod));

        return h;
        }
    public boolean getSharing()
        {
        return m_sharing;
        }
    public Hashtable getShareList()
        {
        return m_shareList;
        }
    public boolean getShareList(Integer value)
        {
        Boolean tmp = (Boolean)m_shareList.get(value);
        return tmp.booleanValue();
        }
    public boolean getShareList(int value)
        {
        Boolean tmp = (Boolean)m_shareList.get(new Integer(value));
        return tmp.booleanValue();
        }
    public boolean getZapped()
        {
        return m_zapped;
        }
    public boolean getZapping()
        {
        return m_zapping;
        }
    public double getZapAmount()
        {
        return m_zapAmount;
        }
    public double getZapCost()
        {
        return m_zapCost;
        }
    public int getZapCost(BENodeOrSubNet nos, BENetwork net)
        {
        if (nos.getCoalition().getSharing())
            {
            int totalRes = 0;
            int members = 0;

            Enumeration enm = net.getNodeList().elements();
            while (enm.hasMoreElements())
                {
                BENode tmpNode = (BENode)enm.nextElement();
                BENodeOrSubNet tmpNOS = (BENodeOrSubNet)tmpNode.getExptData("BENodeExchange");

                if (nos.getCoalition().getCoalitionType().equals(tmpNOS.getCoalition().getCoalitionType()))
                    {
                    if (nos.getCoalition().getCoalition() == tmpNOS.getCoalition().getCoalition())
                        {
                        if (tmpNOS.getCoalition().getJoined())
                            {
                            totalRes = totalRes + getEarnedCoalResources(tmpNOS,net);
                            members++;
                            }
                        }
                    }
                }
            int resEarned = (int)Math.round((totalRes*1.0)/members);
            int zapCost = (int)Math.round(resEarned*nos.getCoalition().getZapCost());

            return zapCost;
            }
        else
            {
            int zapCost = (int)Math.round(getEarnedCoalResources(nos,net)*nos.getCoalition().getZapCost());
            return zapCost;
            }
        }

    public void payForCosts(BENodeOrSubNet nos, BENetwork net)
        {
        BENode myNode = nos.getNode();
        double adjuster = 1.0 - nos.getCoalition().getZapCost();

        Enumeration enm = net.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
            if ((edge.getNode1() == myNode.getID()) && (beer.getExchange() != null))
                {
                boolean share = nos.getCoalition().getShareList(edge.getNode2());
                if ((share) && (beer.getExchange().getNode1().getResource() > 0))
                    beer.getExchange().getNode1().setResource(Math.round(beer.getExchange().getNode1().getResource()*adjuster));
                }
            if ((edge.getNode2() == myNode.getID()) && (beer.getExchange() != null))
                {
                boolean share = nos.getCoalition().getShareList(edge.getNode1());
                if ((share) && (beer.getExchange().getNode2().getResource() > 0))
                    beer.getExchange().getNode2().setResource(Math.round(beer.getExchange().getNode2().getResource()*adjuster));
                }
            }
        }

    public void reduceFreeRiderEarnings(BENode node, BENetwork net)
        {
        BENodeOrSubNet nos = (BENodeOrSubNet)node.getExptData("BENodeExchange");
        double adjuster = 1.0 - nos.getCoalition().getZapAmount();

        Enumeration enm = net.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
            if ((edge.getNode1() == node.getID()) && (beer.getExchange() != null))
                {
                boolean share = nos.getCoalition().getShareList(edge.getNode2());
                if ((share) && (beer.getExchange().getNode1().getResource() > 0))
                    beer.getExchange().getNode1().setResource(Math.round(beer.getExchange().getNode1().getResource()*adjuster));
                }
            if ((edge.getNode2() == node.getID()) && (beer.getExchange() != null))
                {
                boolean share = nos.getCoalition().getShareList(edge.getNode1());
                if ((share) && (beer.getExchange().getNode2().getResource() > 0))
                    beer.getExchange().getNode2().setResource(Math.round(beer.getExchange().getNode2().getResource()*adjuster));
                }
            }
        }

    public void setCoalEarnings(int value)
        {
        m_coalEarnings = value;
        }
    public void setCoalition(int value)
        {
        m_coalition = value;
        }
    public void setCoalitionInfoLevel(int value)
        {
        m_coalitionInfoLevel = value;
        }
    public void setCoalOffer(int value)
        {
        m_coalOffer = value;
        }
    public void setCoalitionType(String value)
        {
        m_coalitionType = value;
        }
    public void setDynamicCoal(int value)
        {
        m_dynamicCoal = value;
        }
    public void setFormed(boolean value)
        {
        m_formed = value;
        }
    public void setFormed(int votes, int members)
        {
        	
        if (getNumNeededToJoin() == 0)
            {
            if (votes == members)
                {
                m_formed = true;
                }
            else
                {
                m_formed = false;
                }
            }
        else
            {
            if (votes >= getNumNeededToJoin())
                {
                m_formed = true;
                }
            else
                {
                m_formed = false;
                }
            }
        //-kar-
        /*	System.err.println("m_coalition: "+m_coalition);
        	System.err.println("m_coalitionType: "+m_coalitionType);
        	System.err.println("m_formed: "+m_formed);
        	System.err.println("m_joined: "+m_joined);
        	System.err.println("votes: "+votes);
        	System.err.println("members: "+members);
        	System.err.println("getNumNeededToJoin(): "+getNumNeededToJoin());
        */	//-kar-
        }
    public void setIDColor(int value)
        {
        m_idColor = value;
        }
    public void setJoined(boolean value)
        {
        m_joined = value;
        }
    public void setNumNeededToJoin(int value)
        {
        m_numNeededToJoin = value;
        }
    public void setNumNeededToZap(int value)
        {
        m_numNeededToZap = value;
        }
    public void setNumZapped(int value)
        {
        m_numZapped = value;
        }
    public void setReportMethod(int value)
        {
        m_reportMethod = value;
        }
    public void setSharing(boolean value)
        {
        m_sharing = value;
        }
    public void setShareList(Hashtable newList)
        {
        m_shareList = newList;
        }
    public void setShareList(int node, boolean publicResource)
        {
        m_shareList.put(new Integer(node),new Boolean(publicResource));
        }
    public void setZapped(boolean value)
        {
        m_zapped = value;
        }
    public void setZapping(boolean value)
        {
        m_zapping = value;
        }
    public void setZapAmount(double value)
        {
        m_zapAmount = value;
        }
    public void setZapCost(double value)
        {
        m_zapCost = value;
        }

    public void toggleShareListItem(int value)
        {
        Boolean tmp = (Boolean)m_shareList.get(new Integer(value));

        if (tmp.booleanValue())
            {
            m_shareList.put(new Integer(value),new Boolean(false));
            }
        else
            {
            m_shareList.put(new Integer(value),new Boolean(true));
            }
        }
    }
