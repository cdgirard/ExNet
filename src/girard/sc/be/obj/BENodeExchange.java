package girard.sc.be.obj;

/* BENodeExchange: Used to set limits on how many exchanges a node can make.

Author: Dudley Girard
Started: ??-??-2000
LastModified: 5-1-2001
*/

import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.io.FMSObjCon;
import girard.sc.wl.io.WLServerConnection;

import java.awt.Graphics;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class BENodeExchange extends BENetworkComponent 
    {
    protected int m_Min = 1;
    protected int m_Max = 1;
    protected int m_exchanges = 0;

    public BENodeExchange()
        {
        }

    public BENodeExchange (BENode node)
        {
        m_node = node;
        }
    public BENodeExchange (BENode node, BENetwork net)
        {
        super(net);
        m_node = node;
        }
    
    public void applySettings(Hashtable h)
        {
        m_Min = ((Integer)h.get("Min")).intValue();
        m_Max = ((Integer)h.get("Max")).intValue();
        m_node = (BENode)h.get("Node");
        m_network = (BENetwork)h.get("Network");
        }

    public Object clone()
        {
        BENodeExchange bene = new BENodeExchange();
        bene.setMin(m_Min);
        bene.setMax(m_Max);
        bene.setExchanges(m_exchanges);

        return bene;
        }

  // Draw info on a canvas using the graphic for the client screen.
    public void drawClient(Graphics g, Vector locInfo) {}

  // Draw info on a canvas using the graphic for the observer screen.
    public void drawObserver(Graphics g, Vector locInfo) {}

  // Draw info on a canvas using the graphic for the experimenter screen.
    public void drawExpt(Graphics g, Vector locInfo) {}

    public String getComponentName()
        {
        return "BENodeExchange";
        }
    public boolean getToKeep(int toNode)
        {
        if (m_exchanges >= m_Min)
            return true;
        else
            return false;
        }

  // Stick object info into a panel for displaying.
  //  public Panel getComponentPanelDisplay() 
  //      {
  //      return new Panel(new GridLayout(1,1));
  //      }
    public int getExchanges()
        {
        return m_exchanges;
        }
    public int getMin()
        {
        return m_Min;
        }
    public int getMax()
        {
        return m_Max;
        }
    public double getResourcesEarned(BENetwork net)
        {
        double per = 0;
        Enumeration enm = net.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
            if ((edge.getNode1() == m_node.getID()) && (beer.getExchange() != null))
                {
                per = per + beer.getExchange().getNode1().getResource();
                }
            if ((edge.getNode2() == m_node.getID()) && (beer.getExchange() != null))
                {
                per = per + beer.getExchange().getNode2().getResource();
                }
            }
        return per;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();

        settings.put("Type","DB-BENodeExchange");
        settings.put("Min",new Integer(m_Min));
        settings.put("Max",new Integer(m_Max));
  
        return settings;
        }
    
  // Initialize some or all of the network based on the data values of the object.
    public void initializeNetwork() 
        {
        m_exchanges = 0;
        }
    public boolean isEdgeActive(BEEdge edge)
        {
        if (edge.getCompleted())
            return false;

        if ((edge.getNode1() == m_node.getID()) || (edge.getNode2() == m_node.getID()))
            {
            if (m_exchanges == m_Max)
                return false;
            else
                return true;
            }
        return true; 
        }

  // Reset object data to starting values.
    public void reset() 
        {
        m_exchanges = 0;
        }

    public Hashtable retrieveData(WLServerConnection wlsc, ExptMessage em, BaseDataInfo bdi)
        {
        return new Hashtable();
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

    public static void updateDBEntry(Connection con)
        {
        try
            { 
            // get a Statement object from the Connection
            //
            // Place new Simulant Type objects.
            Statement stmt = con.createStatement();
                    
            ResultSet rs = stmt.executeQuery("SELECT Object_ID_INT FROM Other_Objects_T WHERE Object_Name_VC = 'DB-BENodeExchange'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Object_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Other_Objects_T SET Object_OBJ = ? WHERE Object_ID_INT = "+index);

                BENodeExchange ne = new BENodeExchange();

System.err.println(ne);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,ne,ps);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Desc, Object, id
                CallableStatement cs = con.prepareCall("{call up_insert_JOtherObject (?, ?, ?, ?)}");

                BENodeExchange ne = new BENodeExchange();

System.err.println(ne);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows you to set minimum and maximum exchange limits on a node.");

                cs.setString(1,"DB-BENodeExchange");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,ne,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("BENodeExchange Object ID: "+cs.getInt(4));
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
        m_exchanges++;
        }
    }
