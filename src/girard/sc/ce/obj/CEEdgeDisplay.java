package girard.sc.ce.obj;

import girard.sc.ce.awt.CEColor;
import girard.sc.ce.awt.CENetworkActionClientWindow;
import girard.sc.ce.awt.CENetworkActionExperimenterWindow;
import girard.sc.ce.awt.CENetworkActionObserverWindow;
import girard.sc.exnet.obj.Network;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.io.FMSObjCon;
import girard.sc.wl.io.WLServerConnection;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

/** 
 * Used to display offers being sent within a CENetwork.
 * <p>
 * <br> Started: 07-23-2002
 * <br> Modified: 01-16-2003
 * <p>
 * @author Dudley Girard
 */

public class CEEdgeDisplay extends CENetworkComponent 
    {
    public final static int NONE = 0;
    public final static int RED = 1;
    public final static int YELLOW = 2;
    public final static int GREEN = 3;
    public final static int COMPLETED = 4;

/* index 0 is keep, index 1 is give, index 2 is type */
    protected String[]    m_n1Display = new String[3];     /* What node1 is offering to node2 */
    protected String[]    m_n2Display = new String[3];     /* What node2 is offering to node1 */
    protected int         m_exchangeState1 = NONE;  // Node 1's exchange state, usually the same as node 2.
    protected int         m_exchangeState2 = NONE;  // Node 2's exchange state, usually the same as node 1.
    
    protected Point       m_oldN1 = new Point(0,0);
    protected Point       m_oldN2 = new Point(0,0);
     /** To Node Stuff **/
     protected Point      m_N1 = new Point(0,0);    /* Node 1 Offer display location */
     protected double     m_theta = 0;  /* How much to rotate the offers. */

  /** From Node Stuff **/
    protected Point       m_N2 = new Point(0,0);    /* Node 2 Offer display location */
    protected int         m_n1Dir = 0;  /* N1 Offer send Right = 0, Left = 1 */
    protected int         m_n2Dir = 0;  /* N2 Offer send Right = 0, Left = 1 */


    public CEEdgeDisplay()
        {
        super("CEEdgeDisplay");
        }
    public CEEdgeDisplay(CEEdge edge)
        {
        super("CEEdgeDisplay");
        m_edge = edge;
        }
    public CEEdgeDisplay (CEEdge edge,CENetwork net)
        {
        super(net,"CEEdgeDisplay");
        m_edge = edge;
        }
   
    public void applySettings(Hashtable h)
        {
        m_network = (Network)h.get("Network");
        m_edge = (CEEdge)h.get("Edge");
        }

    public Object clone()
        {
/* Do we want/need to clone all the objects attached to this object...possibly so */

        CEEdgeDisplay ceed = new CEEdgeDisplay(m_edge,(CENetwork)m_network);
        for (int i=0;i<3;i++)
            {
            ceed.setN1Display(i,m_n1Display[i]);
            ceed.setN2Display(i,m_n1Display[i]);
            }

        ceed.setExchangeState1(m_exchangeState1);
        ceed.setExchangeState2(m_exchangeState2);

        return ceed;
        }

    public void computeDisplayPoints(double xScale, double yScale)
        {
        int changeX, changeY, totalDistance;
        int locX1, locX2;
        int locY1, locY2;
        int rotX1, rotY1;
        int rotX2, rotY2;
        int midX, midY;

        double scale = (xScale + yScale)/2.0;

        changeX = m_oldN1.x - m_oldN2.x;
        changeY = m_oldN1.y - m_oldN2.y;
 
        midX = (m_oldN1.x + m_oldN2.x)/2;
        midY = (m_oldN1.y + m_oldN2.y)/2;

        totalDistance = (int)Math.sqrt(changeX*changeX + changeY*changeY);

        locX1 = (int)((changeX*3*scale)/totalDistance);
        locX2 = (int)((changeX*20*scale)/totalDistance);
        locY1 = (int)((changeY*3*scale)/totalDistance);
        locY2 = (int)((changeY*20*scale)/totalDistance);

        double tX = (changeX*1.0)/(totalDistance*1.0);
        double tY = (-changeY*1.0)/(totalDistance*1.0);

        m_theta = Math.toDegrees(Math.acos(tX));
        
        if (tY < 0)
            {
            m_theta = 360 - m_theta;
            }

System.err.println("MTD1: "+m_theta+" "+tX+" "+tY+" "+m_edge.getNode1()+" "+m_edge.getNode2());

        if ((m_theta >= 0) && (m_theta <= 90))
            {
            m_theta = 360 - m_theta;

            m_n1Dir = 0;
            m_n2Dir = 1;

          // Node 1
            m_N1.x = midX - (int)((changeX*90*scale)/totalDistance) - locY1;
            m_N1.y = midY - (int)((changeY*90*scale)/totalDistance) + locX1; 

          //  Node  2
            m_N2.x = midX + (int)((changeX*15*scale)/totalDistance) + locY2;
            m_N2.y = midY + (int)((changeY*15*scale)/totalDistance) - locX2;
// System.err.println("T: "+m_N2.y+" "+m_N1.y+" "+midY+" "+changeY);
            }
        else if ((m_theta > 90) && (m_theta < 180))
            {
            m_theta = 180 - m_theta;
            m_n1Dir = 1;
            m_n2Dir = 0;

         // Node 1
            m_N1.x = midX - (int)((changeX*15*scale)/totalDistance) - locY2;
            m_N1.y = midY - (int)((changeY*15*scale)/totalDistance) + locX2; 

        //  Node  2
            m_N2.x = midX + (int)((changeX*90*scale)/totalDistance) + locY1;
            m_N2.y = midY + (int)((changeY*90*scale)/totalDistance) - locX1;
            }
        else if ((m_theta >= 180) && (m_theta < 270))
            {
            m_theta =  360 - (m_theta - 180);
            m_n1Dir = 1;
            m_n2Dir = 0;

        // Node 1
            m_N1.x = midX - (int)((changeX*15*scale)/totalDistance) - locY2;
            m_N1.y = midY - (int)((changeY*15*scale)/totalDistance) + locX2; 

        //  Node  2
            m_N2.x = midX + (int)((changeX*90*scale)/totalDistance) + locY1;
            m_N2.y = midY + (int)((changeY*90*scale)/totalDistance) - locX1;
            }
        else
            {
            m_theta = 360 - m_theta;

            m_n1Dir = 0;
            m_n2Dir = 1;

          // Node 1
            m_N1.x = midX - (int)((changeX*90*scale)/totalDistance) - locY1;
            m_N1.y = midY - (int)((changeY*90*scale)/totalDistance) + locX1; 

          //  Node  2
            m_N2.x = midX + (int)((changeX*15*scale)/totalDistance) + locY2;
            m_N2.y = midY + (int)((changeY*15*scale)/totalDistance) - locX2;
            }
// System.err.println("MTD2: "+m_theta+" "+m_n1Dir+" "+m_n2Dir);
        m_theta = Math.toRadians(m_theta);
        }

    private BufferedImage createRotatedImage(Color c, String[] m_info, Font font, int dir) 
	  {
	  /*
	   * Get fontmetrics and calculate position.
	   */
	  FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);

        String title = new String ("NULL");

        if (dir == 1)
            {
            if (m_info[2].equals("DOT"))
                {
                title = new String(m_info[0]+"/"+m_info[1]+"  ");
                }
            else
                {
                title = new String(m_info[0]+"/"+m_info[1]+" "+m_info[2]);
                }
            }
        else
            {
            if (m_info[2].equals("DOT"))
                {
                title = new String("  "+m_info[1]+"/"+m_info[0]);
                }
            else
                {
                title = new String(m_info[2]+" "+m_info[1]+"/"+m_info[0]);
                }
            }

        int width = fm.stringWidth(title);
        int charWidth = fm.stringWidth("D");
        int height = fm.getHeight() - 2;
        int ascent = fm.getMaxAscent();
        int charAscent = fm.getAscent();
	  int leading = fm.getLeading();

	  /*
	   * Create the image.
	   */
        BufferedImage image = new BufferedImage(width + 8, height, BufferedImage.TYPE_3BYTE_BGR);
	    
	  /*
	   * Set graphics attributes and draw the string.
	   */
        Graphics2D gr = image.createGraphics();
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

	  gr.setColor(Color.white);
	  gr.fillRect(0, 0, image.getWidth(), image.getHeight());

	  gr.setFont(font);

// System.err.println("G: "+height+" "+ascent+" "+leading+" "+fm.getLineMetrics("D",gr).getHeight());
	    
        gr.setColor(c);
	  gr.drawString(title, 4, ascent + leading - 1);

        if (dir == 1)
            {
            if (m_info[2].equals("DOT"))
                {
                int[] xPts = new int[3];
                int[] yPts = new int[3];
                xPts[0] = (int)(width - charWidth);
                yPts[0] = (charAscent + leading)/2;
                xPts[1] = (int)(width);
                yPts[1] = yPts[0] + (charAscent + leading)/4;
                xPts[2] = xPts[0];
                yPts[2] = charAscent + leading;
                gr.fillPolygon(xPts,yPts,3);
                }
            }
        else
            {
            if (m_info[2].equals("DOT"))
                {
                int[] xPts = new int[3];
                int[] yPts = new int[3];
                xPts[0] = (int)(4 + charWidth);
                yPts[0] = (charAscent + leading)/2;
                xPts[1] = (int)(4);
                yPts[1] = yPts[0] + (charAscent + leading)/4;
                xPts[2] = xPts[0];
                yPts[2] = charAscent + leading;
                gr.fillPolygon(xPts,yPts,3);
                }
            }

        return image;
        }

  // Draw info on a canvas using the graphic for the client screen.
    public void drawClient(Graphics g, Vector locInfo) 
        {

	    //nvm - the CE client window is displaying exchange information even when its not supposed to...
	    CENetwork cen = (CENetwork)m_network;

	    Boolean rr = (Boolean)cen.getExtraData("RoundRunning");

	    //	    if ((!rr.booleanValue())  && (!m_edge.getCompleted())) - what is m_edge.getCompleted doing?
	    if ((!rr.booleanValue()))  //&& (!m_edge.getCompleted())) - what is m_edge.getCompleted doing?
	    	return;
	    
	    //	    if ((m_edge.getCompleted()) && (m_n1Keep.getIntResource() == 0))
	    //	return;

	    int infoLevel = ((Integer)cen.getExtraData("InfoLevel")).intValue();
	    String n1type = (String)((CENode)cen.getNode(m_edge.getNode1())).getExtraData("Type");
	    String n2type = (String)((CENode)cen.getNode(m_edge.getNode2())).getExtraData("Type");
	    
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

	    // </nvm>
	    
	Point n1 = (Point)locInfo.elementAt(0);
	Point n2 = (Point)locInfo.elementAt(1);
        Double xAdj = (Double)locInfo.elementAt(2);
        Double yAdj = (Double)locInfo.elementAt(3);
        CENetworkActionClientWindow CWApp = (CENetworkActionClientWindow)locInfo.elementAt(4);
      
        Graphics2D g2 = (Graphics2D)g;

        double scale = (xAdj.doubleValue() + yAdj.doubleValue())/2.0;

        int fontSize = (int)(14*scale);

        if (fontSize < 10)
            fontSize = 10;
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

            computeDisplayPoints(xAdj.doubleValue(),yAdj.doubleValue());
            }

        if (m_exchangeState1 != NONE)
            {
            Font font = new Font("Monospaced",Font.BOLD,fontSize);

            BufferedImage offerImg = createRotatedImage(getStateColor(m_exchangeState1),m_n1Display,font,m_n1Dir);

            AffineTransform at = new AffineTransform();
            at.translate(m_N1.x,m_N1.y);
            at.rotate(m_theta);
            g2.drawImage(offerImg,at,null);
            }

        if (m_exchangeState2 != NONE)
            {
            Font font = new Font("Monospaced",Font.BOLD,fontSize);

            BufferedImage offerImg = createRotatedImage(getStateColor(m_exchangeState2),m_n2Display,font,m_n2Dir);

            AffineTransform at = new AffineTransform();
            at.translate(m_N2.x,m_N2.y);
            at.rotate(m_theta,0,0);

            g2.drawImage(offerImg,at,null);
            }
        }

  // Draw info on a canvas using the graphic for the experimenter screen.
    public void drawExpt(Graphics g, Vector locInfo) 
        {
        Point n1 = (Point)locInfo.elementAt(0);
        Point n2 = (Point)locInfo.elementAt(1);
        Double xAdj = (Double)locInfo.elementAt(2);
        Double yAdj = (Double)locInfo.elementAt(3);
        CENetworkActionExperimenterWindow CWApp = (CENetworkActionExperimenterWindow)locInfo.elementAt(4);
      
        Graphics2D g2 = (Graphics2D)g;

        double xScale = xAdj.doubleValue();
        double yScale = yAdj.doubleValue();

        double scale = (xAdj.doubleValue() + yAdj.doubleValue())/2.0;

        int fontSize = (int)(14*scale);

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

            computeDisplayPoints(xAdj.doubleValue(),yAdj.doubleValue());
            }

        if (m_exchangeState1 != NONE)
            {
            Font font = new Font("Monospaced",Font.BOLD,fontSize);

            BufferedImage offerImg = createRotatedImage(getStateColor(m_exchangeState1),m_n1Display,font,m_n1Dir);

            AffineTransform at = new AffineTransform();
            at.translate(m_N1.x,m_N1.y);
            at.rotate(m_theta);
            g2.drawImage(offerImg,at,null);
            }

        if (m_exchangeState2 != NONE)
            {
            Font font = new Font("Monospaced",Font.BOLD,fontSize);

            BufferedImage offerImg = createRotatedImage(getStateColor(m_exchangeState2),m_n2Display,font,m_n2Dir);

            AffineTransform at = new AffineTransform();
            at.translate(m_N2.x,m_N2.y);
            at.rotate(m_theta,0,0);

            g2.drawImage(offerImg,at,null);
            }
        }

  // Draw info on a canvas using the graphic for the observer screen.
    public void drawObserver(Graphics g, Vector locInfo)
        {
        Point n1 = (Point)locInfo.elementAt(0);
        Point n2 = (Point)locInfo.elementAt(1);
        Double xAdj = (Double)locInfo.elementAt(2);
        Double yAdj = (Double)locInfo.elementAt(3);
        CENetworkActionObserverWindow CWApp = (CENetworkActionObserverWindow)locInfo.elementAt(4);

        Graphics2D g2 = (Graphics2D)g;
      
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

            computeDisplayPoints(xScale,yScale);
            }

        g.setFont(new Font("Monospaced",Font.PLAIN,fontSize));

        if (m_exchangeState1 != NONE)
            {
            Font font = new Font("Monospaced",Font.BOLD,fontSize);

            BufferedImage offerImg = createRotatedImage(getStateColor(m_exchangeState1),m_n1Display,font,m_n1Dir);

            AffineTransform at = new AffineTransform();
            at.translate(m_N1.x,m_N1.y);
            at.rotate(m_theta);
            g2.drawImage(offerImg,at,null);
            }

        if (m_exchangeState2 != NONE)
            {
            Font font = new Font("Monospaced",Font.BOLD,fontSize);

            BufferedImage offerImg = createRotatedImage(getStateColor(m_exchangeState2),m_n2Display,font,m_n2Dir);

            AffineTransform at = new AffineTransform();
            at.translate(m_N2.x,m_N2.y);
            at.rotate(m_theta,0,0);

            g2.drawImage(offerImg,at,null);
            }
        }

    public String getComponentName()
        {
        return "CEEdgeDisplay";
        }
/**
 * Stick object info into a panel for displaying.
 * THINK ABOUT REMOVING!!!!
 */
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
        
        settings.put("Type","DB-CEEdgeDisplay");

        return settings;
        }
/* Returns the proper color to display a given state in */
    public Color getStateColor(int state)
        {
        if (state == RED)
            return CEColor.edgeRed;
        if (state == YELLOW)
            return CEColor.edgeYellow;
        if (state == GREEN)
            return CEColor.edgeGreen;
        if (state == COMPLETED)
            return CEColor.edgeGreen;

        return CEColor.edgeBlack;
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
    public void reset()
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
                    
            ResultSet rs = stmt.executeQuery("SELECT Object_ID_INT FROM Other_Objects_T WHERE Object_Name_VC = 'DB-CEEdgeDisplay'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Object_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Other_Objects_T SET Object_OBJ = ? WHERE Object_ID_INT = "+index);

                CEEdgeDisplay ed = new CEEdgeDisplay();

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

                CEEdgeDisplay ed = new CEEdgeDisplay();

System.err.println(ed);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows you to display the resources being offered between nodes.");

                cs.setString(1,"DB-CEEdgeDisplay");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,ed,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("CEEdgeDisplay Object ID: "+cs.getInt(4));
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
