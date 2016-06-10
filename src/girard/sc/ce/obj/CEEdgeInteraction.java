package girard.sc.ce.obj;

import girard.sc.exnet.obj.Network;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.expt.sql.LoadDataResultsReq;
import girard.sc.io.FMSObjCon;
import girard.sc.wl.io.WLServerConnection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

/** 
 * Is the object class for setting and managing the number of interactions on
 * a given CEEdge in a CENetwork.
 * <p>
 * <br> Started: 07-23-2002
 * <br> Modified: 01-21-2003
 * <p>
 * @author Dudley Girard
 */

public class CEEdgeInteraction extends CENetworkComponent 
    {
/**
 * 0 -> inifinte exchanges per edge, 1+ -> multiple exchanges per edge up to
 * value.
 */
    protected int         m_continuous = 0;
/**
 * Number of exchanges made so far.
 */
    protected int         m_exchanges = 0;
/**
 * The completed exchanges, node1 is the node the component is attached 
 * to and node2 is the node the exchange was completed with.
 */
    protected Hashtable   m_completedExchanges = new Hashtable(); 
/**
 * Present offers from other users, node1 is the from node, node2 is the
 * to node(which is also the node this component is attached to).
 */
    protected Hashtable   m_offers = new Hashtable();

    protected Point       m_oldN1 = new Point(0,0);
    protected Point       m_oldN2 = new Point(0,0);
    protected Point       m_buttonLoc = new Point(0,0);
    protected int         m_dotSize = 6;
/**
 * Is this the edge I am viewing profit information from.
 */
    protected int         m_active = -1;

    public CEEdgeInteraction()
        {
        super("CEEdgeInteraction");
        }
    public CEEdgeInteraction(CEEdge edge)
        {
        super("CEEdgeInteraction");
        m_edge = edge;
        }
    public CEEdgeInteraction(CEEdge edge, CENetwork net)
        {
        super(net,"CEEdgeInteraction");
        m_edge = edge;
        }

    private void addCompletedExchange(CEExchange cee1, CEExchange cee2)
        {
        m_exchanges++;
        String name1 = new String(""+m_edge.getNode1()+"-"+m_edge.getNode2());
        String name2 = new String(""+m_edge.getNode2()+"-"+m_edge.getNode1());
        Hashtable h = new Hashtable();
        h.put(name1,cee1);
        h.put(name2,cee2);
        m_completedExchanges.put(new Integer(m_exchanges),h);

        CENode n1 = (CENode)m_network.getNode(m_edge.getNode1());
        CENodeResource nr1 = (CENodeResource)n1.getExptData("CENodeResource");
        nr1.updateAvailableResources(cee1.getNode1(),cee1.getNode2());
        nr1.setExchanges(nr1.getExchanges() + 1);
        CENode n2 = (CENode)m_network.getNode(m_edge.getNode2());
        CENodeResource nr2 = (CENodeResource)n2.getExptData("CENodeResource");
        nr2.updateAvailableResources(cee2.getNode2(),cee2.getNode1());
        nr2.setExchanges(nr2.getExchanges() + 1);

        m_edge.setCompleted(true);
        m_edge.setActive(false);
        }
/**
 * Display can be "client" for a CEClientWindow, "experimenter" for a CEExperimenterDisplay,
 * and "observer" for a CEObserverDisplay. Type can be "offer" for an CEOfferMsg or "accept"
 * for a CEAcceptOfferMsg.
 */
    public void addOffer(int fromNode, int toNode, CEExchange cee, String display, String type)
        {
        String name = new String(""+fromNode+"-"+toNode);
        m_offers.put(name,cee);

        m_edge.setInUse(true);

        CEEdgeDisplay ceed = (CEEdgeDisplay)m_edge.getExptData("CEEdgeDisplay");

        if ((fromNode == m_edge.getNode1()) && (toNode == m_edge.getNode2()))
            {
            ceed.setN1Display(0,""+cee.getNode1().getIntResource()+":"+cee.getNode1().getLabel());
            ceed.setN1Display(1,""+cee.getNode2().getIntResource()+":"+cee.getNode2().getLabel());
            ceed.setN1Display(2,"DOT");
            if (type.equals("offer"))
                {
                ceed.setExchangeState1(CEEdgeDisplay.RED);
                }
            else
                {
                ceed.setExchangeState1(CEEdgeDisplay.GREEN);
                ceed.setExchangeState2(CEEdgeDisplay.YELLOW);
                }
            }
        else
            {
            ceed.setN2Display(0,""+cee.getNode2().getIntResource()+":"+cee.getNode2().getLabel());
            ceed.setN2Display(1,""+cee.getNode1().getIntResource()+":"+cee.getNode1().getLabel());
            ceed.setN2Display(2,"DOT");
            if (type.equals("offer"))
                {
                ceed.setExchangeState2(CEEdgeDisplay.RED);
                }
            else
                {
                ceed.setExchangeState2(CEEdgeDisplay.GREEN);
                ceed.setExchangeState1(CEEdgeDisplay.YELLOW);
                }
            }
        }

    public void applySettings(Hashtable h)
        {
        m_continuous = ((Integer)h.get("Continuous")).intValue();
        m_exchanges = ((Integer)h.get("Exchanges")).intValue();
        m_edge = (CEEdge)h.get("Edge");
        m_network = (Network)h.get("Network");
        }
    public Object clone()
        {
        CEEdgeInteraction ceei = new CEEdgeInteraction(m_edge,(CENetwork)m_network);

        ceei.setContinuous(m_continuous);
        ceei.setExchanges(m_exchanges);

        return ceei;
        }

    public void completeExchange(int from, int to, CEExchange ceeF, String display)
        {
        m_edge.setInUse(false);
        CEExchange ceeT = (CEExchange)ceeF.clone();
        CENode toNode = (CENode)m_network.getNode(to);
        CENodeResource toNR = (CENodeResource)toNode.getExptData("CENodeResource");
        CEEdgeDisplay ceed = (CEEdgeDisplay)m_edge.getExptData("CEEdgeDisplay");
        if (m_edge.getNode1() == toNode.getID())
            {
            CEResource cerGain = toNR.getAvailableResources(ceeT.getNode1().getLabel());
            CEResource cerLost = toNR.getAvailableResources(ceeT.getNode2().getLabel());
            ceeT.getNode1().setValue(cerGain.getValue());
            ceeT.getNode2().setValue(cerLost.getValue());
            addCompletedExchange(ceeT,ceeF);

            ceed.setN2Display(0,""+ceeT.getNode2().getIntResource()+":"+cerLost.getLabel());
            ceed.setN2Display(1,""+ceeT.getNode1().getIntResource()+":"+cerGain.getLabel());
            ceed.setN2Display(2,"DOT");
            ceed.setExchangeState2(CEEdgeDisplay.COMPLETED);
            ceed.setN1Display(0,""+ceeT.getNode1().getIntResource()+":"+cerGain.getLabel());
            ceed.setN1Display(1,""+ceeT.getNode2().getIntResource()+":"+cerLost.getLabel());
            ceed.setN1Display(2,"DOT");
            ceed.setExchangeState1(CEEdgeDisplay.COMPLETED);
            }
        else
            {
            CEResource cerGain = toNR.getAvailableResources(ceeT.getNode2().getLabel());
            CEResource cerLost = toNR.getAvailableResources(ceeT.getNode1().getLabel());
            ceeT.getNode2().setValue(cerGain.getValue());
            ceeT.getNode1().setValue(cerLost.getValue());
            addCompletedExchange(ceeF,ceeT);

            ceed.setN1Display(0,""+ceeT.getNode1().getIntResource()+":"+cerLost.getLabel());
            ceed.setN1Display(1,""+ceeT.getNode2().getIntResource()+":"+cerGain.getLabel());
            ceed.setN1Display(2,"DOT");
            ceed.setExchangeState1(CEEdgeDisplay.COMPLETED);
            ceed.setN2Display(0,""+ceeT.getNode2().getIntResource()+":"+cerGain.getLabel());
            ceed.setN2Display(1,""+ceeT.getNode1().getIntResource()+":"+cerLost.getLabel());
            ceed.setN2Display(2,"DOT");
            ceed.setExchangeState2(CEEdgeDisplay.COMPLETED);
            }
        }

    public void computeDisplayPoint(double scale)
        {
        int changeX, changeY, totalDistance;
        int locX1, locX2;
        int locY1, locY2;
        int rotX1, rotY1;
        int rotX2, rotY2;
        int midX, midY;

        changeX = m_oldN1.x - m_oldN2.x;
        changeY = m_oldN1.y - m_oldN2.y;
 
        midX = (m_oldN1.x + m_oldN2.x)/2;
        midY = (m_oldN1.y + m_oldN2.y)/2;

        totalDistance = (int)Math.sqrt(changeX*changeX + changeY*changeY);

        locX1 = (int)((changeX*10*scale)/totalDistance);
        locX2 = (int)((changeX*5*scale)/totalDistance);
        locY1 = (int)((changeY*10*scale)/totalDistance);
        locY2 = (int)((changeY*5*scale)/totalDistance);

        if (locY1 < 0)
            {
            rotX1 = locY1;
            rotX2 = -locY2;
            }
        else
            {
            rotX1 = locY2;
            rotX2 = -locY1;
            }

        if (locX1 < 0)
            {
            rotY1 = -locX1;
            rotY2 = locX2;
            }
        else
            {
            rotY1 = -locX2;
            rotY2 = locX1;
            }       
 
        m_buttonLoc.x = rotX1 - (int)((changeX*10*scale)/totalDistance) + midX;
        m_buttonLoc.y = rotY1 - (int)((changeY*10*scale)/totalDistance) + midY;
        }

  // Draw info on a canvas using the graphic for the client screen.
    public void drawClient(Graphics g, Vector locInfo) 
        {
        Point n1 = (Point)locInfo.elementAt(0);
        Point n2 = (Point)locInfo.elementAt(1);
        Double xAdj = (Double)locInfo.elementAt(2);
        Double yAdj = (Double)locInfo.elementAt(3);
      
        double xScale = xAdj.doubleValue();
        double yScale = yAdj.doubleValue();

        double scale = (xAdj.doubleValue() + yAdj.doubleValue())/2.0;

        m_dotSize = (int)(15*scale);

        if (m_dotSize > 15)
            m_dotSize = 15;
        if (m_dotSize < 6)
            m_dotSize = 6;

        int halfDot = m_dotSize/2;

        if ((n1.x != m_oldN1.x) || (n1.y != m_oldN1.y) || (n2.x != m_oldN2.x) || (n2.y != m_oldN2.y))
            {
            m_oldN1.x = n1.x;
            m_oldN1.y = n1.y;
            m_oldN2.x = n2.x;
            m_oldN2.y = n2.y;

            computeDisplayPoint(scale);
            }

        g.setColor(Color.gray);
        g.fillOval(m_buttonLoc.x-halfDot,m_buttonLoc.y-halfDot,m_dotSize,m_dotSize);
        g.setColor(Color.white);
        if (m_active == 1)
            g.setColor(Color.black);
        g.fillOval(m_buttonLoc.x-halfDot+1,m_buttonLoc.y-halfDot+1,m_dotSize-2,m_dotSize-2);
        }
  // Draw info on a canvas using the graphic for the experimenter screen.
    public void drawExpt(Graphics g, Vector locInfo)
        {
        Point n1 = (Point)locInfo.elementAt(0);
        Point n2 = (Point)locInfo.elementAt(1);
        Double xAdj = (Double)locInfo.elementAt(2);
        Double yAdj = (Double)locInfo.elementAt(3);
      
        double xScale = xAdj.doubleValue();
        double yScale = yAdj.doubleValue();

        double scale = (xAdj.doubleValue() + yAdj.doubleValue())/2.0;

        m_dotSize = (int)(15*scale);

        if (m_dotSize > 15)
            m_dotSize = 15;
        if (m_dotSize < 6)
            m_dotSize = 6;

        int halfDot = m_dotSize/2;

        if ((n1.x != m_oldN1.x) || (n1.y != m_oldN1.y) || (n2.x != m_oldN2.x) || (n2.y != m_oldN2.y))
            {
            m_oldN1.x = n1.x;
            m_oldN1.y = n1.y;
            m_oldN2.x = n2.x;
            m_oldN2.y = n2.y;

            computeDisplayPoint(scale);
            }

        g.setColor(Color.gray);
        g.fillOval(m_buttonLoc.x-halfDot,m_buttonLoc.y-halfDot,m_dotSize,m_dotSize);
        g.setColor(Color.white);
        if (m_active == 1)
            g.setColor(Color.black);
        g.fillOval(m_buttonLoc.x-halfDot+1,m_buttonLoc.y-halfDot+1,m_dotSize-2,m_dotSize-2);
        }

  // Draw info on a canvas using the graphic for the observer screen.
    public void drawObserver(Graphics g, Vector locInfo)
        {
        Point n1 = (Point)locInfo.elementAt(0);
        Point n2 = (Point)locInfo.elementAt(1);
        Double xAdj = (Double)locInfo.elementAt(2);
        Double yAdj = (Double)locInfo.elementAt(3);
      
        double xScale = xAdj.doubleValue();
        double yScale = yAdj.doubleValue();

        double scale = (xAdj.doubleValue() + yAdj.doubleValue())/2.0;

        int dotSize = (int)(15*scale);

        if (dotSize > 15)
            dotSize = 15;
        if (dotSize < 6)
            dotSize = 6;

        int halfDot = dotSize/2;

        if ((n1.x != m_oldN1.x) || (n1.y != m_oldN1.y) || (n2.x != m_oldN2.x) || (n2.y != m_oldN2.y))
            {
            m_oldN1.x = n1.x;
            m_oldN1.y = n1.y;
            m_oldN2.x = n2.x;
            m_oldN2.y = n2.y;

            computeDisplayPoint(scale);
            }

        g.setColor(Color.gray);
        g.fillOval(m_buttonLoc.x-halfDot,m_buttonLoc.y-halfDot,dotSize,dotSize);
        g.setColor(Color.black);
        g.fillOval(m_buttonLoc.x-halfDot+1,m_buttonLoc.y-halfDot+1,dotSize-2,dotSize-2);
        }

    public int getActive()
        {
        return m_active;
        }
    public Point getButtonLoc()
        {
        return m_buttonLoc;
        }
    public Hashtable getCompletedExchange(int exch)
        {
        if (m_completedExchanges.containsKey(new Integer(exch)))
            return (Hashtable)m_completedExchanges.get(new Integer(exch));
        return null;
        }
    public int getContinuous()
        {
        return m_continuous;
        }
    public int getDotSize()
        {
        return m_dotSize;
        }
    public int getExchanges()
        {
        return m_exchanges;
        }
    public CEExchange getOffer(String key)
        {
        if (m_offers.containsKey(key))
            return (CEExchange)m_offers.get(key);
        return null;
        }
    public CEExchange getOffer(int fromNode, int toNode)
        {
        String name = new String(""+fromNode+"-"+toNode);
        return getOffer(name);
        }
    public Hashtable getOffers()
        {
        return m_offers;
        }
    public int getOfferProfit(int node, int fromNode, int toNode)
        {
        int profit = 0;
        if (m_offers.containsKey(""+fromNode+"-"+toNode))
            {
            CEExchange cee = getOffer(""+fromNode+"-"+toNode);
            CENode tmpNode = (CENode)m_network.getNode(node);
            CENodeResource nr = (CENodeResource)tmpNode.getExptData("CENodeResource");
            CEResource cer1 = nr.getAvailableResources(cee.getNode1().getLabel());
            CEResource cer2 = nr.getAvailableResources(cee.getNode2().getLabel());
            if (node == m_edge.getNode1())
                {
                profit = (int)(cer1.getValue()*cee.getNode1().getResource() - cer2.getValue()*cee.getNode2().getResource());
                }
            else
                {
                profit = (int)(cer2.getValue()*cee.getNode2().getResource() - cer1.getValue()*cee.getNode1().getResource());
                }
            }
        return profit;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();

        settings.put("Type","DB-CEEdgeInteraction");

        settings.put("Continuous",new Integer(m_continuous));
        settings.put("Exchanges",new Integer(m_exchanges));

        return settings;
        }

/**
 *
 */
    public void initializeNetwork()
        {
        m_exchanges = 0;
        m_completedExchanges.clear();
        m_offers.clear();
        }
    public void initializeStart()
        {
        m_exchanges = 0;
        m_completedExchanges.clear();
        m_offers.clear();
        }

    public boolean isEdgeStillUsable()
        {
        if ((m_continuous == 0) || (m_continuous > m_exchanges))
            {
            CENode n1 = (CENode)m_network.getNode(m_edge.getNode1());
            CENode n2 = (CENode)m_network.getNode(m_edge.getNode2());
        
            CENodeResource nr1 = (CENodeResource)n1.getExptData("CENodeResource");
            CENodeResource nr2 = (CENodeResource)n2.getExptData("CENodeResource");

            if ((nr1.getExchanges() < nr1.getMax()) && (nr2.getExchanges() < nr2.getMax()))
                return true;
            }

        return false;
        }

    public void reactivateEdge()
        {
        m_edge.setCompleted(false);
        m_edge.setActive(true);
        m_offers.clear();

        CEEdgeDisplay ceed = (CEEdgeDisplay)m_edge.getExptData("CEEdgeDisplay");
        ceed.reset();
        }

    public void removeOffer(int fromNode, int toNode)
        {
        String str = new String(""+fromNode+"-"+toNode);
        if (m_offers.containsKey(str))
            {
            m_offers.remove(str);
            }
        }

/* Should reset the component to it's initial values.  Not sure
   if we want to do this here or what the "initial values" are.
*/
    public void reset()
        {
        m_exchanges = 0;
        }

    public Hashtable retrieveData(WLServerConnection wlsc, ExptMessage em, BaseDataInfo bdi)
        {
        try 
            {
            Hashtable erData = new Hashtable();

            LoadDataResultsReq tmp = new LoadDataResultsReq("ccDB","CE_Offers_Data_T",bdi,wlsc,em);

            ResultSet rs = tmp.runQuery();

            Vector data = new Vector();

            if (rs == null)
                {
                erData.put("Offer Data",data);
                return erData;
                }

            while (rs.next())
                {
                CEOfferOutputObject coo = new CEOfferOutputObject(rs);
                data.addElement(coo);
                }
            erData.put("Offer Data",data);
  
            return erData;
            }
        catch(Exception e) 
            {
            wlsc.addToLog(e.getMessage());
            return new Hashtable();
            }
        }

    public void setActive(int value)
        {
        m_active = value;
        }
    public void setContinuous(int value)
        {
        m_continuous = value;
        }
    public void setExchanges(int value)
        {
        m_exchanges = value;
        }

    public void updateActiveState()
        {

        m_edge.setActive(true);

        if (m_edge.getCompleted())
            {
            m_offers.clear();
            m_edge.setActive(false);
            return;
            }
        CEEdgeDisplay ceed = (CEEdgeDisplay)m_edge.getExptData("CEEdgeDisplay");

        if ((m_exchanges >= m_continuous) && (m_continuous > 0))
            {
            m_offers.clear();
            ceed.reset();
            m_edge.setActive(false);
            return;
            }

        CENode n1 = (CENode)m_network.getNode(m_edge.getNode1());
        CENode n2 = (CENode)m_network.getNode(m_edge.getNode2());

        CENodeResource nr1 = (CENodeResource)n1.getExptData("CENodeResource");
        CENodeResource nr2 = (CENodeResource)n2.getExptData("CENodeResource");

        if ((nr1.isEdgeActive(m_edge)) && (nr2.isEdgeActive(m_edge)))
            {
            m_edge.setActive(true);
            }
        else
            {
            m_offers.clear();
            ceed.reset();
            m_edge.setActive(false);
            return;
            }
        }
    public static void updateDBEntry(Connection con)
        {
        try
            { 
            // get a Statement object from the Connection
            //
            // Place new Simulant Type objects.
            Statement stmt = con.createStatement();
                    
            ResultSet rs = stmt.executeQuery("SELECT Object_ID_INT FROM Other_Objects_T WHERE Object_Name_VC = 'DB-CEEdgeInteraction'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Object_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Other_Objects_T SET Object_OBJ = ? WHERE Object_ID_INT = "+index);

                CEEdgeInteraction ei = new CEEdgeInteraction();

System.err.println(ei);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,ei,ps);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Desc, Network, id
                CallableStatement cs = con.prepareCall("{call up_insert_JOtherObject (?, ?, ?, ?)}");

                CEEdgeInteraction ei = new CEEdgeInteraction();

System.err.println(ei);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Stores the interactions between two nodes during a CENetworkAction.");

                cs.setString(1,"DB-CEEdgeInteraction");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,ei,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("CEEdgeInteraction Object ID: "+cs.getInt(4));
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
    public void updateOffers()
        {
        CEEdgeDisplay ceed = (CEEdgeDisplay)m_edge.getExptData("CEEdgeDisplay");

        CENode n1 = (CENode)m_network.getNode(m_edge.getNode1());
        CENode n2 = (CENode)m_network.getNode(m_edge.getNode2());

        CENodeResource nr1 = (CENodeResource)n1.getExptData("CENodeResource");
        CENodeResource nr2 = (CENodeResource)n2.getExptData("CENodeResource");

        CEExchange cee1 = getOffer(n1.getID(),n2.getID());
        if (cee1 != null)
            {
System.err.println("Here 1: "+cee1.isValidExchange(n1,n2));
            if (!cee1.isValidExchange(n1,n2))
                {
                removeOffer(n1.getID(),n2.getID());
                ceed.setN1Display(0,"");
                ceed.setN1Display(1,"");
                ceed.setN1Display(2,"");
                ceed.setExchangeState1(CEEdgeDisplay.NONE);
                }
            }

        CEExchange cee2 = getOffer(n2.getID(),n1.getID());
        if (cee2 != null)
            {
System.err.println("Here 2: "+cee2.isValidExchange(n1,n2));
            if (!cee2.isValidExchange(n1,n2))
                {
                removeOffer(n2.getID(),n1.getID());
                ceed.setN2Display(0,"");
                ceed.setN2Display(1,"");
                ceed.setN2Display(2,"");
                ceed.setExchangeState2(CEEdgeDisplay.NONE);
                }
            }
        }
    }
