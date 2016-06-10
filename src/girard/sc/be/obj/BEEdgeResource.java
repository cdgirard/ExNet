package girard.sc.be.obj;

import girard.sc.be.awt.BEColor;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.expt.sql.LoadDataResultsReq;
import girard.sc.io.FMSObjCon;
import girard.sc.wl.io.WLServerConnection;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Point;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

/** 
 * Is the object class for attaching resources to 
 * a BEEdge object.
 * <p>
 * <br> Started: 4-30-2000
 * <br> Modified: 5-9-2001
 * <br> Modified: 10-02-2002
 * <p> 
 * @author: Dudley Girard
 */

public class BEEdgeResource extends BENetworkComponent 
    {
    public final static int NONE = 0;
    public final static int RED = 1;
    public final static int YELLOW = 2;
    public final static int GREEN = 3;
    public final static int COMPLETED = 4;

    protected BEResource  m_res = new BEResource("Points",24); // Max resources and resource type.
    protected int         m_n1InitialDemand = 12;
    protected int         m_n2InitialDemand = 12;
    protected boolean     m_n1ResetDemand = true;
    protected boolean     m_n2ResetDemand = true;
    protected BEResource  m_n1Keep = new BEResource("Points",0);     /* What node1 wants to keep */
    protected BEResource  m_n1Give = new BEResource("Points",0);     /* What node1 wants to give to node 2 */
    protected BEResource  m_n2Keep = new BEResource("Points",0);     /* What node2 wants to keep */
    protected BEResource  m_n2Give = new BEResource("Points",0);     /* What node2 wants to give node 1 */
    protected int         m_exchangeState = NONE;
    protected BEExchange  m_exchange = null;  /* What is the outcome of the exchange if it occurs. */
    protected boolean     m_displayResource = true;
    protected Point       m_oldN1 = new Point(0,0);
    protected Point       m_oldN2 = new Point(0,0);
     /** To Node Stuff **/
    protected Point       m_N1K = new Point(0,0);    /* Node 1 Keep Offer display location */
    protected Point       m_N1G = new Point(0,0);    /* Node 1 Give Offer display location */
    protected Point       m_N1A = new Point(0,0);    /* Node 1 Arrow display location */
  /** From Node Stuff **/
    protected Point       m_N2K = new Point(0,0);    /* Node 2 Keep Offer display location */
    protected Point       m_N2G = new Point(0,0);    /* Node 2 Give Offer display location */
    protected Point       m_N2A = new Point(0,0);    /* Node 2 Arrow display location */

    public BEEdgeResource()
        {
        }
    public BEEdgeResource (BEEdge edge)
        {
        m_edge = edge;
        }
 
    public void adjustEarnings(BENodeExchange n1, BENodeExchange n2)
        {
        if (m_exchange == null)
            return;

        if (!n1.getToKeep(m_edge.getNode2()))
            {
            m_exchange.getNode1().setResource(0);
            }
        if (!n2.getToKeep(m_edge.getNode1()))
            {
            m_exchange.getNode2().setResource(0);
            }
        }
   
    public void applySettings(Hashtable h)
        {
        m_network = (BENetwork)h.get("Network");
        m_edge = (BEEdge)h.get("Edge");
        m_displayResource = ((Boolean)h.get("DisplayResource")).booleanValue();
        m_res.applySettings((Hashtable)h.get("Res"));

        if (h.containsKey("N1InitialDemand"))
            m_n1InitialDemand = ((Integer)h.get("N1InitialDemand")).intValue();
        else
            m_n1InitialDemand = m_res.getIntResource()/2;

        if (h.containsKey("N2InitialDemand"))
            m_n2InitialDemand = ((Integer)h.get("N2InitialDemand")).intValue();
        else
            m_n2InitialDemand = m_res.getIntResource()/2;

        if (h.containsKey("N1ResetDemand"))
            m_n1ResetDemand = ((Boolean)h.get("N1ResetDemand")).booleanValue();
        else
            m_n1ResetDemand = true;

        if (h.containsKey("N2ResetDemand"))
            m_n2ResetDemand = ((Boolean)h.get("N2ResetDemand")).booleanValue();
        else
            m_n2ResetDemand = true;
        }

    public Object clone()
        {
/* Do we want/need to clone all the objects attached to this object...possibly so */

        BEEdgeResource beer = new BEEdgeResource();
        beer.setRes((BEResource)m_res.clone());
        beer.setN1InitialDemand(m_n1InitialDemand);
        beer.setN2InitialDemand(m_n2InitialDemand);
        beer.setN1ResetDemand(m_n1ResetDemand);
        beer.setN2ResetDemand(m_n2ResetDemand);
        beer.getN1Keep().setResource(m_n1Keep.getResource());
        beer.getN1Give().setResource(m_n1Give.getResource());
        beer.getN2Keep().setResource(m_n2Keep.getResource());
        beer.getN2Give().setResource(m_n2Give.getResource());
        beer.setDisplayResource(m_displayResource);
        if (m_exchange != null)
            beer.setExchange((BEExchange)m_exchange.clone());

        beer.setExchangeState(m_exchangeState);

        return beer;
        }

    public void completeExchange(int tt, long rt, int n1Keep, int n2Keep)
        {
        m_edge.setCompleted(true);
        m_edge.setActive(false);
        m_exchange = new BEExchange(tt,rt,n1Keep,n2Keep);
        m_n1Keep.setResource(n1Keep);
        m_n1Give.setResource(n2Keep);
        m_n2Keep.setResource(n2Keep);
        m_n2Give.setResource(n1Keep);
        m_exchangeState = COMPLETED;
        
        BENetwork ben = (BENetwork)m_network;
        String n1type = (String)((BENode)ben.getNode(m_edge.getNode1())).getExtraData("Type");
        String n2type = (String)((BENode)ben.getNode(m_edge.getNode2())).getExtraData("Type");

        if ((n1type != null) && (n2type != null))
            {
            if ((!m_n1ResetDemand) && (n1type.equals("Me"))) 
                m_n1InitialDemand = n1Keep;
            if ((!m_n2ResetDemand) && (n2type.equals("Me"))) 
                m_n2InitialDemand = n2Keep;
            }
        }

    public void computeDisplayPoints(double scale)
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

        locX1 = (int)((changeX*15*scale)/totalDistance);
        locX2 = (int)((changeX*10*scale)/totalDistance);
        locY1 = (int)((changeY*15*scale)/totalDistance);
        locY2 = (int)((changeY*10*scale)/totalDistance);

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

    // Node 1 
        m_N1K.x = rotX1 - (int)((changeX*20*scale)/totalDistance) + midX;
        m_N1K.y = rotY1 - (int)((changeY*20*scale)/totalDistance) + midY;

        m_N1G.x = rotX1 - (int)((changeX*42*scale)/totalDistance) + midX;
        m_N1G.y = rotY1 - (int)((changeY*42*scale)/totalDistance) + midY;  

        m_N1A.x = rotX1 - (int)((changeX*61*scale)/totalDistance) + midX;
        m_N1A.y = rotY1 - (int)((changeY*61*scale)/totalDistance) + midY; 

     //  Node  2
        m_N2K.x = rotX2 + (int)((changeX*20*scale)/totalDistance) + midX;
        m_N2K.y = rotY2 + (int)((changeY*20*scale)/totalDistance) + midY;

        m_N2G.x = rotX2 + (int)((changeX*42*scale)/totalDistance) + midX;
        m_N2G.y = rotY2 + (int)((changeY*42*scale)/totalDistance) + midY;  

        m_N2A.x = rotX2 + (int)((changeX*61*scale)/totalDistance) + midX;
        m_N2A.y = rotY2 + (int)((changeY*61*scale)/totalDistance) + midY; 
        }

  // Draw info on a canvas using the graphic for the client screen.
    public void drawClient(Graphics g, Vector locInfo) 
        {
        if ((!m_edge.getActive()) && (!m_edge.getCompleted()))
            return;

        BENetwork ben = (BENetwork)m_network;

        Boolean rr = (Boolean)ben.getExtraData("RoundRunning");

        if ((!rr.booleanValue()) && (!m_edge.getCompleted()))
            return;

        if ((m_edge.getCompleted()) && (m_n1Keep.getIntResource() == 0))
            return;

        int infoLevel = ((Integer)ben.getExtraData("InfoLevel")).intValue();
        String n1type = (String)((BENode)ben.getNode(m_edge.getNode1())).getExtraData("Type");
        String n2type = (String)((BENode)ben.getNode(m_edge.getNode2())).getExtraData("Type");

        if (infoLevel <= 3)
            {
     // Will display all negoiations connected to me.
            if ((!n1type.equals("Me")) && (!n2type.equals("Me")))
                return;
            }

        if (infoLevel == 6)
            {
     // Will display all negoiations connected to me.
            if ((!n1type.equals("Me")) && (!n2type.equals("Me")))
                return;
            }

        if (infoLevel <= 8)
            {
     // Will display all relations connected to me or my neighbors.
            if ((!n1type.equals("Neighbor")) && (!n2type.equals("Neighbor")))
                return;
            }


        Point n1 = (Point)locInfo.elementAt(0);
        Point n2 = (Point)locInfo.elementAt(1);
        Double xAdj = (Double)locInfo.elementAt(2);
        Double yAdj = (Double)locInfo.elementAt(3);
      
        Point midP = new Point((n1.x+n2.x)/2,(n1.y+n2.y)/2);

        double scale = (xAdj.doubleValue() + yAdj.doubleValue())/2.0;

        int fontSize = (int)(12*scale);

        if (fontSize < 6)
            fontSize = 6;
        if (fontSize > 14)
            fontSize = 14;

        int dotSize = fontSize/2 - 3;

        if (dotSize > 6)
            dotSize = 6;
        if (dotSize < 3)
            dotSize = 3;

        if ((n1.x != m_oldN1.x) || (n1.y != m_oldN1.y) || (n2.x != m_oldN2.x) || (n2.y != m_oldN2.y))
            {
            m_oldN1.x = n1.x;
            m_oldN1.y = n1.y;
            m_oldN2.x = n2.x;
            m_oldN2.y = n2.y;

            computeDisplayPoints(scale);
            }

        g.setFont(new Font("Monospaced",Font.PLAIN,fontSize));

        g.setColor(BEColor.ACTIVE_EDGE);

        if ((m_exchangeState == GREEN) || (m_edge.getCompleted()))
            g.setColor(BEColor.COMPLETE_EDGE);

        g.drawString(Integer.toString(m_n1Keep.getIntResource()), m_N1K.x,m_N1K.y);
        g.drawString(Integer.toString(m_n1Give.getIntResource()), m_N1G.x,m_N1G.y);
        g.fillOval(m_N1A.x-1,m_N1A.y-6,dotSize,dotSize);

        g.drawString(Integer.toString(m_n2Keep.getIntResource()), m_N2K.x,m_N2K.y);
        g.drawString(Integer.toString(m_n2Give.getIntResource()), m_N2G.x,m_N2G.y);
        g.fillOval(m_N2A.x-1,m_N2A.y-6,dotSize,dotSize);

        if (m_displayResource)
            {
            g.setFont(new Font("Monospaced",Font.BOLD,fontSize+1));
            g.drawString(""+m_res.getIntResource(),midP.x,midP.y);
            }
        }

  // Draw info on a canvas using the graphic for the observer screen.
    public void drawObserver(Graphics g, Vector locInfo)
        {
        Point n1 = (Point)locInfo.elementAt(0);
        Point n2 = (Point)locInfo.elementAt(1);
        Double xAdj = (Double)locInfo.elementAt(2);
        Double yAdj = (Double)locInfo.elementAt(3);
      
        Point midP = new Point((n1.x+n2.x)/2,(n1.y+n2.y)/2);

        double xScale = xAdj.doubleValue();
        double yScale = yAdj.doubleValue();

        double scale = (xAdj.doubleValue() + yAdj.doubleValue())/2.0;

        int fontSize = (int)(12*scale);

        if (fontSize < 6)
            fontSize = 6;
        if (fontSize > 14)
            fontSize = 14;

        int dotSize = fontSize/2 - 2;

        if (dotSize > 6)
            dotSize = 6;
        if (dotSize < 3)
            dotSize = 3;

        if ((n1.x != m_oldN1.x) || (n1.y != m_oldN1.y) || (n2.x != m_oldN2.x) || (n2.y != m_oldN2.y))
            {
            m_oldN1.x = n1.x;
            m_oldN1.y = n1.y;
            m_oldN2.x = n2.x;
            m_oldN2.y = n2.y;

            computeDisplayPoints(scale);
            }

        g.setFont(new Font("Monospaced",Font.PLAIN,fontSize));

        g.drawString(Integer.toString(m_n1Keep.getIntResource()), m_N1K.x,m_N1K.y);
        g.drawString(Integer.toString(m_n1Give.getIntResource()), m_N1G.x,m_N1G.y);
        g.fillOval(m_N1A.x-1,m_N1A.y-6,dotSize,dotSize);

        g.drawString(Integer.toString(m_n2Keep.getIntResource()), m_N2K.x,m_N2K.y);
        g.drawString(Integer.toString(m_n2Give.getIntResource()), m_N2G.x,m_N2G.y);
        g.fillOval(m_N2A.x-1,m_N2A.y-6,dotSize,dotSize);

        g.setFont(new Font("Monospaced",Font.BOLD,fontSize+1));
        g.drawString(""+m_res.getIntResource(),midP.x,midP.y);
        }

  // Draw info on a canvas using the graphic for the experimenter screen.
    public void drawExpt(Graphics g, Vector locInfo) 
        {
        Point n1 = (Point)locInfo.elementAt(0);
        Point n2 = (Point)locInfo.elementAt(1);
        Double xAdj = (Double)locInfo.elementAt(2);
        Double yAdj = (Double)locInfo.elementAt(3);
      
        Point midP = new Point((n1.x+n2.x)/2,(n1.y+n2.y)/2);

        double xScale = xAdj.doubleValue();
        double yScale = yAdj.doubleValue();

        double scale = (xAdj.doubleValue() + yAdj.doubleValue())/2.0;

        int fontSize = (int)(12*scale);

        if (fontSize < 6)
            fontSize = 6;
        if (fontSize > 14)
            fontSize = 14;

        int dotSize = fontSize/2 - 2;

        if (dotSize > 6)
            dotSize = 6;
        if (dotSize < 3)
            dotSize = 3;

        if ((n1.x != m_oldN1.x) || (n1.y != m_oldN1.y) || (n2.x != m_oldN2.x) || (n2.y != m_oldN2.y))
            {
            m_oldN1.x = n1.x;
            m_oldN1.y = n1.y;
            m_oldN2.x = n2.x;
            m_oldN2.y = n2.y;

            computeDisplayPoints(scale);
            }

        g.setFont(new Font("Monospaced",Font.PLAIN,fontSize));

        g.drawString(Integer.toString(m_n1Keep.getIntResource()), m_N1K.x,m_N1K.y);
        g.drawString(Integer.toString(m_n1Give.getIntResource()), m_N1G.x,m_N1G.y);
        g.fillOval(m_N1A.x-1,m_N1A.y-6,dotSize,dotSize);

        g.drawString(Integer.toString(m_n2Keep.getIntResource()), m_N2K.x,m_N2K.y);
        g.drawString(Integer.toString(m_n2Give.getIntResource()), m_N2G.x,m_N2G.y);
        g.fillOval(m_N2A.x-1,m_N2A.y-6,dotSize,dotSize);

        g.setFont(new Font("Monospaced",Font.BOLD,fontSize+1));
        g.drawString(""+m_res.getIntResource(),midP.x,midP.y);
        }

    public String getComponentName()
        {
        return "BEEdgeResource";
        }
  // Stick object info into a panel for displaying.
  // THINK ABOUT REMOVING!!!!
    public Panel getComponentPanelDisplay() 
        {
        return new Panel(new GridLayout(1,1));
        }
    public boolean getDisplayResource()
        {
        return m_displayResource;
        }
    public BEExchange getExchange()
        {
        return m_exchange;
        }
    public int getExchangeState()
        {
        return m_exchangeState;
        }
    public BEResource getN1Give()
        {
        return m_n1Give;
        }
    public int getN1InitialDemand()
        {
        return m_n1InitialDemand;
        }
    public BEResource getN1Keep()
        {
        return m_n1Keep;
        }
    public boolean getN1ResetDemand()
        {
        return m_n1ResetDemand;
        }
    public BEResource getN2Give()
        {
        return m_n2Give;
        }
    public int getN2InitialDemand()
        {
        return m_n2InitialDemand;
        }
    public BEResource getN2Keep()
        {
        return m_n2Keep;
        }
    public boolean getN2ResetDemand()
        {
        return m_n2ResetDemand;
        }
  // Return an array of strings explaining the state of the object data
    public String[] getObjInfo() 
        {
        String[] str = {"NULL"};
 
        return str;
        }
    public BEResource getRes()
        {
        return m_res;
        }
    
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();
        
        settings.put("Type","DB-BEEdgeResource");
        settings.put("Res",m_res.getSettings());
        settings.put("DisplayResource",new Boolean(m_displayResource));
        settings.put("N1InitialDemand",new Integer(m_n1InitialDemand));
        settings.put("N1ResetDemand",new Boolean(m_n1ResetDemand));
        settings.put("N2InitialDemand",new Integer(m_n2InitialDemand));
        settings.put("N2ResetDemand",new Boolean(m_n2ResetDemand));

        return settings;
        }

  // Initialize some or all of the network based on the data values of the object.
    public void initializeNetwork()
        {
        m_exchangeState = NONE;
        m_exchange = null;
        m_n1Keep.setResource(0);
        m_n1Give.setResource(0);
        m_n2Keep.setResource(0);
        m_n2Give.setResource(0);
        }

  // Reset object data to starting values.
    public void reset() {}

    public Hashtable retrieveData(WLServerConnection wlsc, ExptMessage em, BaseDataInfo bdi)
        {
        try 
            {
            Hashtable erData = new Hashtable();

            LoadDataResultsReq tmp = new LoadDataResultsReq("beDB","BEExpt_Offers_Data_T",bdi,wlsc,em);

            ResultSet rs = tmp.runQuery();

            Vector data = new Vector();

            if (rs == null)
                {
                erData.put("Data",data);
                return erData;
                }

            while (rs.next())
                {
                BEOfferOutputObject boo = new BEOfferOutputObject(rs);
                data.addElement(boo);
                }
            erData.put("Data",data);
  
            return erData;
            }
        catch(Exception e) 
            {
            wlsc.addToLog(e.getMessage());
            return new Hashtable();
            }
        }

    public void setDisplayResource(boolean value)
        {
        m_displayResource = value;
        }
    public void setExchange(BEExchange bee)
        {
        m_exchange = bee;
        }
    public void setExchangeState(int value)
        {
        m_exchangeState = value;
        }
    public void setN1InitialDemand(int value)
        {
        if ((value > 0) && (value < m_res.getIntResource()))
            m_n1InitialDemand = value;
        }
    public void setN1ResetDemand(boolean value)
        {
        m_n1ResetDemand = value;
        }
    public void setN2InitialDemand(int value)
        {
        if ((value > 0) && (value < m_res.getIntResource()))
            m_n2InitialDemand = value;
        }
    public void setN2ResetDemand(boolean value)
        {
        m_n2ResetDemand = value;
        }
    public void setRes(BEResource ber)
        {
        m_res = ber;
        m_n1Keep.setName(m_res.getName());
        m_n1Give.setName(m_res.getName());
        m_n2Keep.setName(m_res.getName());
        m_n2Give.setName(m_res.getName());
        }
    

    public static void updateDBEntry(Connection con)
        {
        try
            { 
            // get a Statement object from the Connection
            //
            // Place new Simulant Type objects.
            Statement stmt = con.createStatement();
                    
            ResultSet rs = stmt.executeQuery("SELECT Object_ID_INT FROM Other_Objects_T WHERE Object_Name_VC = 'DB-BEEdgeResource'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Object_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Other_Objects_T SET Object_OBJ = ? WHERE Object_ID_INT = "+index);

                BEEdgeResource er = new BEEdgeResource();

System.err.println(er);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,er,ps);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Desc, Object, id
                CallableStatement cs = con.prepareCall("{call up_insert_JOtherObject (?, ?, ?, ?)}");

                BEEdgeResource er = new BEEdgeResource();

System.err.println(er);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows you to set the amount of resources along an edge.");

                cs.setString(1,"DB-BEEdgeResource");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,er,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("BEEdgeResource Object ID: "+cs.getInt(4));
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
  //  public void updateNetwork(ExnetAction act) {}
    }
