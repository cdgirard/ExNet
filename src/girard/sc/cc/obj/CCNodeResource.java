package girard.sc.cc.obj;

import girard.sc.cc.awt.CCColor;
import girard.sc.cc.awt.CCNetworkActionClientWindow;
import girard.sc.exnet.obj.Network;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.expt.sql.LoadDataResultsReq;
import girard.sc.io.FMSObjCon;
import girard.sc.wl.io.WLServerConnection;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
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
 * Is the object class for attaching resources to 
 * a Node object in a CCNetwork.
 * <p>
 * <br>Started: 5-25-2001
 * <br>Modified: 7-16-2001
 *
 * @author Dudley Girard
 */


public class CCNodeResource extends CCNetworkComponent 
    {
    protected int         m_bank = 0; // How many points does this node start with.
    protected int         m_pointPool = 0;  // How many points can this node send each round.
    protected Vector      m_pointTraders = new Vector(); // Who can I send my points to.
    protected int         m_max = 0;  // Maximum number of exchanges that can be made.

    protected int         m_availablePoints = 0;  // How many points I have to offer others.
    protected int         m_activeBank = 0;
    protected int         m_exchanges = 0; // Number of exchanges made so far.
    protected Hashtable   m_completedExchanges = new Hashtable(); // The completed exchanges, node1 is the node the component is attached to and node2 is the node the exchange was completed with.
    
    protected Hashtable   m_offers = new Hashtable(); // Present offers from other users, node1 is the from node, node2 is the to node(which is also the node this component is attached to).

    public CCNodeResource()
        {
        super("CCNodeResource");
        }
    public CCNodeResource (CCNode node)
        {
        super("CCNodeResource");
        m_node = node;
        }
    public CCNodeResource (CCNode node, CCNetwork net)
        {
        super(net,"CCNodeResource");
        m_node = node;
        }

    public void addCompletedExchange(CCExchange cce)
        {
        m_exchanges++;
        m_availablePoints = m_availablePoints + cce.getNode1().getIntResource(); // My available points is now what I got from the exchange.
        String name = new String(""+cce.getNode1().getNode()+"-"+cce.getNode2().getNode());
        m_completedExchanges.put(name,cce);

        Enumeration enm = m_network.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge edge = (CCEdge)enm.nextElement();
            
            if ((edge.getNode1() == cce.getNode1().getNode()) && (edge.getNode2() == cce.getNode2().getNode()))
                {
                edge.setCompleted(true);
                edge.setActive(false);
                break;
                }
            if ((edge.getNode2() == cce.getNode1().getNode()) && (edge.getNode1() == cce.getNode2().getNode()))
                {
                edge.setCompleted(true);
                edge.setActive(false);
                break;
                }
            }
        }
    public void addOffer(CCExchange cce)
        {
        String name = new String(""+cce.getNode1().getNode()+"-"+cce.getNode2().getNode());
        m_offers.put(name,cce);
        }
    public void addTradePartener(int node)
        {
        m_pointTraders.addElement(new Integer(node));
        if (m_max == 0)
            {
            m_max = 1;
            }
        }

/* Have to adjust both my copy of the exchange and the copy the node I made the
   exchange with.
*/
    public void adjustEarnings()
        {
        m_activeBank = m_activeBank + m_availablePoints;
        m_availablePoints = 0;
        }

    public void applySettings(Hashtable h)
        {
        m_bank = ((Integer)h.get("Bank")).intValue();
        m_pointPool = ((Integer)h.get("PointPool")).intValue();
        m_max = ((Integer)h.get("Max")).intValue();
        m_node = (CCNode)h.get("Node");
        m_network = (Network)h.get("Network");

        m_pointTraders = (Vector)h.get("PointTraders");
        }

/* Round is now running can I make offers to this node. */
    public boolean canNegoiateWith(int node)
        {
        CCNode toNode = (CCNode)m_network.getNode(node);
        CCNodeResource toCcnr = (CCNodeResource)toNode.getExptData("CCNodeResource");

// If I can trade with him or he can trade with me then maybe we can negoiate.
        if ((canTradeWith(node)) || (toCcnr.canTradeWith(m_node.getID())))
            {
            if (m_exchanges < m_max)
                return true;
            else
                return false;
            }
        return false;
        }
/* Is this node one of my trading parteners? */
    public boolean canTradeWith(int node)
        {
        Enumeration enm = m_pointTraders.elements();
        while (enm.hasMoreElements())
            {
            Integer tmp = (Integer)enm.nextElement();
            if (tmp.intValue() == node)
                return true;
            }
        return false;
        }

    public Object clone()
        {
        CCNodeResource ccnr = new CCNodeResource();

        ccnr.setBank(m_bank);
        ccnr.setPointPool(m_pointPool);
        ccnr.setMax(m_max);

        Enumeration enm = m_pointTraders.elements();
        while (enm.hasMoreElements())
            {
            Integer node = (Integer)enm.nextElement();
            ccnr.addTradePartener(node.intValue());
            }

        return ccnr;
        }

    public void completeExchange(int tt, long rt, int fromKeep, int from, int toKeep, int to)
        {
        CCExchange cce = new CCExchange(tt,rt,fromKeep,from,toKeep,to);
        cce.setExchangeState(CCExchange.COMPLETED);
        addCompletedExchange(cce);
        }

  // Draw info on a canvas using the graphic for the client screen.
    public void drawClient(Graphics g, Vector locInfo) 
        {
        Point nodeLoc = (Point)locInfo.elementAt(0);
        
        double xAdj = ((Double)locInfo.elementAt(1)).doubleValue();
        double yAdj = ((Double)locInfo.elementAt(2)).doubleValue();
        CCNetworkActionClientWindow CWApp = (CCNetworkActionClientWindow)locInfo.elementAt(3);

        Point loc = new Point(nodeLoc.x,nodeLoc.y);
        double scale = (xAdj + yAdj)/2.0;

        loc.y = loc.y - (int)(12*yAdj);

        int fontSize = (int)(10*scale);

        if (fontSize < 6)
            fontSize = 6;
        if (fontSize > 12)
            fontSize = 12;

        if (m_pointPool > 0)
            {
            Font f2 = new Font("Monospaced",Font.BOLD,fontSize);
            double bagSize = (fontSize*1.0)/12.0;
            int boxHeight = (int)(22*bagSize);
            int boxWidth = (int)(40*bagSize);
            Image img = CWApp.getImage("Money Bag");
            int imgWidth = (int)(img.getWidth(null)*bagSize);
            int imgHeight = (int)(img.getHeight(null)*bagSize);

            g.setColor(CCColor.black);
            g.drawRect(loc.x,11+(int)(loc.y+10*scale),boxWidth,boxHeight);
            g.drawImage(img,loc.x+2,13+(int)(loc.y+10*scale),imgWidth,imgHeight,null);
            g.setFont(f2);
            g.drawString(""+m_pointPool,4+loc.x+imgWidth,15+loc.y+(int)(10*scale + 10*bagSize));
            }
        }

  // Draw info on a canvas using the graphic for the observer screen.
    public void drawObserver(Graphics g, Vector locInfo) {}

  // Draw info on a canvas using the graphic for the experimenter screen.
    public void drawExpt(Graphics g, Vector locInfo) {}

    public int getActiveBank()
        {
        return m_activeBank;
        }
    public int getAvailablePoints()
        {
        return m_availablePoints;
        }
    public int getBank()
        {
        return m_bank;
        }
    public CCExchange getCompletedExchange(int node)
        {
        String name = new String(""+m_node.getID()+"-"+node);
        if (m_completedExchanges.containsKey(name))
            return (CCExchange)m_completedExchanges.get(name);
        return null;
        }
    public CCExchange getOffer(int node)
        {
        String name = new String(""+node+"-"+m_node.getID());
        if (m_offers.containsKey(name))
            return (CCExchange)m_offers.get(name);
        return null;
        }
    public Hashtable getOffers()
        {
        return m_offers;
        }
    public int getMax()
        {
        return m_max;
        }
    public int getPointPool()
        {
        return m_pointPool;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();

        settings.put("Type","DB-CCNodeResource");

        settings.put("Bank",new Integer(m_bank));
        settings.put("PointPool",new Integer(m_pointPool));
        settings.put("PointTraders",m_pointTraders);
        settings.put("Max",new Integer(m_max));

        return settings;
        }

/*
we can look at both nodes on an edge's list of trade parteners and then decide
if the edge is active or not.  since all nodes will do this check we can disable
all edges that should be.
*/
    public void initializeNetwork()
        {
        m_exchanges = 0;
        m_availablePoints = 0;
        m_completedExchanges.clear();
        m_offers.clear();

        CCNetwork ccn = (CCNetwork)m_network;

        Enumeration enm = ccn.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge edge = (CCEdge)enm.nextElement();
            if (m_node.getID() == edge.getNode1())
                {
                CCNode node = (CCNode)ccn.getNode(edge.getNode2());
                CCNodeResource nr = (CCNodeResource)node.getExptData("CCNodeResource");
                if ((!this.canNegoiateWith(node.getID())) && (!nr.canNegoiateWith(m_node.getID())))
                    {
                    edge.setActive(false);
                    }
                }
            if (m_node.getID() == edge.getNode2())
                {
                CCNode node = (CCNode)ccn.getNode(edge.getNode1());
                CCNodeResource nr = (CCNodeResource)node.getExptData("CCNodeResource");
                if ((!this.canNegoiateWith(node.getID())) && (!nr.canNegoiateWith(m_node.getID())))
                    {
                    edge.setActive(false);
                    }
                }
            }
        }
    public void initializeStart()
        {
        CCNetwork ccn = (CCNetwork)m_network;
        m_activeBank = m_bank;
        if (((String)m_node.getExtraData("Type")).equals("Me"))
            {
            ccn.setExtraData("PntEarnedPeriod",new Double(m_activeBank));
            Double pen = (Double)ccn.getExtraData("PntEarnedNetwork");
            ccn.setExtraData("PntEarnedNetwork",new Double(m_activeBank + pen.doubleValue()));
            }
        }
    public boolean isEdgeActive(CCEdge edge)
        {
        if (edge.getCompleted())
            return false;

        CCNetwork ccn = (CCNetwork)m_network;
        if (edge.getNode1() == m_node.getID())
            {
            CCNode node = (CCNode)ccn.getNode(edge.getNode2());
            CCNodeResource nr = (CCNodeResource)node.getExptData("CCNodeResource");
            if ((!this.canNegoiateWith(node.getID())) || (!nr.canNegoiateWith(m_node.getID())))
                {
                 return false;
                }
            else
                {
                return true;
                }
            }
        if (edge.getNode2() == m_node.getID())
            {
            CCNode node = (CCNode)ccn.getNode(edge.getNode1());
            CCNodeResource nr = (CCNodeResource)node.getExptData("CCNodeResource");
            if ((!this.canNegoiateWith(node.getID())) || (!nr.canNegoiateWith(m_node.getID())))
                {
                return false;
                }
            else
                {
                return true;
                }
            }
        return true;
        }

    public void removeOffer(int fromNode)
        {
        String str = new String(""+fromNode+"-"+m_node.getID());
        if (m_offers.containsKey(str))
            {
            m_offers.remove(str);
            }
        }
    public void removeTradePartener(int node)
        {
        Enumeration enm = m_pointTraders.elements();
        while (enm.hasMoreElements())
            {
            Integer tmp = (Integer)enm.nextElement();
            if (tmp.intValue() == node)
                {
                m_pointTraders.removeElement(tmp);
                if (m_pointTraders.size() > 0)
                    {
                    m_max = 1;
                    }
                else
                    {
                    m_max = 0;
                    }
                return;
                }
            }
        }

/* Should reset the component to it's initial values.  Not sure
   if we want to do this here or what the "initial values" are.
*/
    public void reset()
        {
        m_activeBank = m_bank;
        }

    public Hashtable retrieveData(WLServerConnection wlsc, ExptMessage em, BaseDataInfo bdi)
        {
        try 
            {
            Hashtable nrData = new Hashtable();

            LoadDataResultsReq tmp = new LoadDataResultsReq("ccDB","CCExpt_Offers_Data_T",bdi,wlsc,em);

            ResultSet rs = tmp.runQuery();

            Vector data = new Vector();

            if (rs == null)
                {
                nrData.put("Data",data);
                return nrData;
                }

            while (rs.next())
                {
                CCOffersOutputObject ooo = new CCOffersOutputObject(rs);
                data.addElement(ooo);
                }
            nrData.put("Data",data);
  
            return nrData;
            }
        catch(Exception e) 
            {
            return new Hashtable();
            }
        }

    public void setActiveBank(int value)
        {
        m_activeBank = value;
        }
    public void setAvailablePoints(int value)
        {
        m_availablePoints = value;
        }
    public void setBank(int value)
        {
        m_bank = value;
        }
    public void setMax(int value)
        {
        m_max = value;
        }
    public void setPointPool(int value)
        {
        m_pointPool = value;
        }

    public boolean tradeCompletedWith(int node)
        {
        String name = new String(""+m_node.getID()+"-"+node);
        if (m_completedExchanges.containsKey(name))
            return true;
        return false;
        }

    public static void updateDBEntry(Connection con)
        {
        try
            { 
            // get a Statement object from the Connection
            //
            // Place new Simulant Type objects.
            Statement stmt = con.createStatement();
                    
            ResultSet rs = stmt.executeQuery("SELECT Object_ID_INT FROM Other_Objects_T WHERE Object_Name_VC = 'DB-CCNodeResource'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Object_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Other_Objects_T SET Object_OBJ = ? WHERE Object_ID_INT = "+index);

                CCNodeResource nr = new CCNodeResource();

System.err.println(nr);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,nr,ps);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Desc, Network, id
                CallableStatement cs = con.prepareCall("{call up_insert_JOtherObject (?, ?, ?, ?)}");

                CCNodeResource nr = new CCNodeResource();

System.err.println(nr);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Stores the resources a node as obtained during a cc experiment.");

                cs.setString(1,"DB-CCNodeResource");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,nr,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("CCNodeResource Object ID: "+cs.getInt(4));
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
    }
