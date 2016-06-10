package girard.sc.be.obj;

import girard.sc.be.awt.BEColor;
import girard.sc.exnet.obj.Network;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.io.FMSObjCon;
import girard.sc.wl.io.WLServerConnection;

import java.awt.Color;
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
 * Used to display offers being sent within a BENetwork.
 * <p>
 * <br> Started: 09-19-2002
 * <p>
 * @author Dudley Girard
 */


public class BEEdgeDisplay extends BENetworkComponent 
    {
    public final static int NONE = 0;
    public final static int RED = 1;
    public final static int YELLOW = 2;
    public final static int GREEN = 3;
    public final static int COMPLETED = 4;
    public final static int BLACK = 5;

/* index 0 is keep, index 1 is give, index 2 is type */
    protected String[]    m_n1Display = new String[3];     /* What node1 is offering to node2 */
    protected String[]    m_n2Display = new String[3];     /* What node2 is offering to node1 */
    protected int         m_exchangeState1 = NONE;  // Node 1's exchange state, usually the same as node 2.
    protected int         m_exchangeState2 = NONE;  // Node 2's exchange state, usually the same as node 1.
    
    protected Point       m_oldN1 = new Point(0,0);
    protected Point       m_oldN2 = new Point(0,0);
     /** To Node Stuff **/
    protected Point       m_N1K = new Point(0,0);    /* Node 1 Keep Offer display location */
    protected Point       m_N1G = new Point(0,0);    /* Node 1 Give Offer display location */
    protected Point       m_N1T = new Point(0,0);    /* Node 1 Type display location */
  /** From Node Stuff **/
    protected Point       m_N2K = new Point(0,0);    /* Node 2 Keep Offer display location */
    protected Point       m_N2G = new Point(0,0);    /* Node 2 Give Offer display location */
    protected Point       m_N2T = new Point(0,0);    /* Node 2 Type display location */

    public BEEdgeDisplay()
        {
        super("BEEdgeDisplay");
        }
    public BEEdgeDisplay (BEEdge edge)
        {
        super("BEEdgeDisplay");
        m_edge = edge;
        }
    public BEEdgeDisplay (BEEdge edge,BENetwork net)
        {
        super(net,"BEEdgeDisplay");
        m_edge = edge;
        }
   
    public void applySettings(Hashtable h)
        {
        m_network = (Network)h.get("Network");
        m_edge = (BEEdge)h.get("Edge");
        }

    public Object clone()
        {
/* Do we want/need to clone all the objects attached to this object...possibly so */

        BEEdgeDisplay beed = new BEEdgeDisplay();
        for (int i=0;i<3;i++)
            {
            beed.setN1Display(i,m_n1Display[i]);
            beed.setN2Display(i,m_n1Display[i]);
            }

        beed.setExchangeState1(m_exchangeState1);
        beed.setExchangeState2(m_exchangeState2);

        return beed;
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

        locX1 = (int)((changeX*10*scale)/totalDistance);
        locX2 = (int)((changeX*15*scale)/totalDistance);
        locY1 = (int)((changeY*10*scale)/totalDistance);
        locY2 = (int)((changeY*15*scale)/totalDistance);

        if (locY1 < 0)
            {
            rotX1 = -locY1;
            rotX2 = locY2;
            }
        else
            {
            rotX1 = -locY2;
            rotX2 = locY1;
            }

        if (locX1 < 0)
            {
            rotY1 = locX1;
            rotY2 = -locX2;
            }
        else
            {
            rotY1 = locX2;
            rotY2 = -locX1;
            }       

    // Node 1 
        m_N1K.x = rotX1 - (int)((changeX*20*scale)/totalDistance) + midX;
        m_N1K.y = rotY1 - (int)((changeY*20*scale)/totalDistance) + midY;

        m_N1G.x = rotX1 - (int)((changeX*42*scale)/totalDistance) + midX;
        m_N1G.y = rotY1 - (int)((changeY*42*scale)/totalDistance) + midY;  

        m_N1T.x = rotX1 - (int)((changeX*61*scale)/totalDistance) + midX;
        m_N1T.y = rotY1 - (int)((changeY*61*scale)/totalDistance) + midY; 

     //  Node  2
        m_N2K.x = rotX2 + (int)((changeX*20*scale)/totalDistance) + midX;
        m_N2K.y = rotY2 + (int)((changeY*20*scale)/totalDistance) + midY;

        m_N2G.x = rotX2 + (int)((changeX*42*scale)/totalDistance) + midX;
        m_N2G.y = rotY2 + (int)((changeY*42*scale)/totalDistance) + midY;  

        m_N2T.x = rotX2 + (int)((changeX*61*scale)/totalDistance) + midX;
        m_N2T.y = rotY2 + (int)((changeY*61*scale)/totalDistance) + midY; 
        }

  // Draw info on a canvas using the graphic for the client screen.
    public void drawClient(Graphics g, Vector locInfo) 
        {
        Point n1 = (Point)locInfo.elementAt(0);
        Point n2 = (Point)locInfo.elementAt(1);
        Double xAdj = (Double)locInfo.elementAt(2);
        Double yAdj = (Double)locInfo.elementAt(3);
      
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

        g.setFont(new Font("Monospaced",Font.BOLD,fontSize));

        if (m_exchangeState1 != NONE)
            {
            g.setColor(getStateColor(m_exchangeState1));
            g.drawString(m_n1Display[0], m_N1K.x,m_N1K.y);
            g.drawString(m_n1Display[1], m_N1G.x,m_N1G.y);
            if (m_n1Display[2].equals("DOT"))
                g.fillOval(m_N1T.x-1,m_N1T.y-6,4,4);
            else
                g.drawString(m_n1Display[2], m_N1T.x,m_N1T.y);
            }

        if (m_exchangeState2 != NONE)
            {
            g.setColor(getStateColor(m_exchangeState2));
            g.drawString(m_n2Display[0], m_N2K.x,m_N2K.y);
            g.drawString(m_n2Display[1], m_N2G.x,m_N2G.y);
            if (m_n2Display[2].equals("DOT"))
                g.fillOval(m_N2T.x-1,m_N2T.y-6,4,4);
            else
                g.drawString(m_n2Display[2], m_N2T.x,m_N2T.y);
            }
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

        if (m_exchangeState1 != NONE)
            {
            g.setColor(getStateColor(m_exchangeState1));
            g.drawString(m_n1Display[0], m_N1K.x,m_N1K.y);
            g.drawString(m_n1Display[1], m_N1G.x,m_N1G.y);
            g.drawString(m_n1Display[2], m_N1T.x,m_N1T.y);
            }

        if (m_exchangeState2 != NONE)
            {
            g.setColor(getStateColor(m_exchangeState2));
            g.drawString(m_n2Display[0], m_N2K.x,m_N2K.y);
            g.drawString(m_n2Display[1], m_N2G.x,m_N2G.y);
            g.drawString(m_n2Display[2], m_N2T.x,m_N2T.y);
            }
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

        if (m_exchangeState1 != NONE)
            {
            g.setColor(getStateColor(m_exchangeState1));
            g.drawString(m_n1Display[0], m_N1K.x,m_N1K.y);
            g.drawString(m_n1Display[1], m_N1G.x,m_N1G.y);
            g.drawString(m_n1Display[2], m_N1T.x,m_N1T.y);
            }

        if (m_exchangeState2 != NONE)
            {
            g.setColor(getStateColor(m_exchangeState2));
            g.drawString(m_n2Display[0], m_N2K.x,m_N2K.y);
            g.drawString(m_n2Display[1], m_N2G.x,m_N2G.y);
            g.drawString(m_n2Display[2], m_N2T.x,m_N2T.y);
            }
        }

    public String getComponentName()
        {
        return "BEEdgeDisplay";
        }
  // Stick object info into a panel for displaying.
  // THINK ABOUT REMOVING!!!!
    public Panel getComponentPanelDisplay() 
        {
        return new Panel(new GridLayout(1,1));
        }
    public int getExchangeState1()
        {
        return m_exchangeState1;
        }
    public int getExchangeState2()
        {
        return m_exchangeState2;
        }
    public String getN1Display(int index)
        {
        return m_n1Display[index];
        }
    public String getN2Display(int index)
        {
        return m_n2Display[index];
        }
  // Return an array of strings explaining the state of the object data
    public String[] getObjInfo() 
        {
        String[] str = {"NULL"};
 
        return str;
        }
// May not need this guy...
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();
        
        settings.put("Type","DB-BEEdgeDisplay");

        return settings;
        }
/* Returns the proper color to display a given state in */
    public Color getStateColor(int state)
        {
        if (state == RED)
            return BEColor.edgeRed;
        if (state == YELLOW)
            return BEColor.edgeYellow;
        if (state == GREEN)
            return BEColor.edgeGreen;
        if (state == COMPLETED)
            return BEColor.edgeGreen;
        if (state == BLACK)
            return BEColor.edgeBlack;

        return BEColor.edgeBlack;
        }

  // Initialize some or all of the network based on the data values of the object.
    public void initializeNetwork()
        {
        m_exchangeState1 = NONE;
        m_exchangeState2 = NONE;
        m_n1Display[0] = "";
        m_n1Display[1] = "";
        m_n1Display[2] = "";
        m_n2Display[0] = "";
        m_n2Display[1] = "";
        m_n2Display[2] = "";
        }
  // Initialize some or all of the network based on the data values of the object.
    public void initializeStart()
        {
        m_exchangeState1 = NONE;
        m_exchangeState2 = NONE;
        m_n1Display[0] = "";
        m_n1Display[1] = "";
        m_n1Display[2] = "";
        m_n2Display[0] = "";
        m_n2Display[1] = "";
        m_n2Display[2] = "";
        }


  // Reset object data to starting values.
    public void reset() {}

    public Hashtable retrieveData(WLServerConnection wlsc, ExptMessage em, BaseDataInfo bdi)
        {
        return new Hashtable();
        }

    public void setExchangeState(int value)
        {
        m_exchangeState1 = value;
        m_exchangeState2 = value;
        }
    public void setExchangeState1(int value)
        {
        m_exchangeState1 = value;
        }
    public void setExchangeState2(int value)
        {
        m_exchangeState2 = value;
        }
    public void setN1Display(int index, String str)
        {
        m_n1Display[index] = str;
        }
    public void setN2Display(int index, String str)
        {
        m_n2Display[index] = str;
        }

    public static void updateDBEntry(Connection con)
        {
        try
            { 
            // get a Statement object from the Connection
            //
            // Place new Simulant Type objects.
            Statement stmt = con.createStatement();
                    
            ResultSet rs = stmt.executeQuery("SELECT Object_ID_INT FROM Other_Objects_T WHERE Object_Name_VC = 'DB-BEEdgeDisplay'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Object_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Other_Objects_T SET Object_OBJ = ? WHERE Object_ID_INT = "+index);

                BEEdgeDisplay ed = new BEEdgeDisplay();

System.err.println(ed);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,ed,ps);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Desc, Object, id
                CallableStatement cs = con.prepareCall("{call up_insert_JOtherObject (?, ?, ?, ?)}");

                BEEdgeDisplay ed = new BEEdgeDisplay();

System.err.println(ed);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows you to display the resources being offered between nodes.");

                cs.setString(1,"DB-BEEdgeDisplay");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,ed,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("BEEdgeDisplay Object ID: "+cs.getInt(4));
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
