package girard.sc.cc.obj;
/* 
   Is the object class for attaching sanctions to 
   a Node object in a CCNetwork.

   Author: Dudley Girard
   Started: 5-29-2001
   Modified: 7-16-2001
   Modified: 9-4-2001
*/

import girard.sc.exnet.obj.Network;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.expt.sql.LoadDataResultsReq;
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

public class CCNodeSanctions extends CCNetworkComponent 
    {
    public static final double STATE_POINT = 1.3;

    protected CCNode      m_node = null; // Which edge the resource is tied to.
    protected Vector      m_sanctions = new Vector(); // A list of people I can sanction.

    public CCNodeSanctions()
        {
        super(STATE_POINT,"CCNodeSanctions");
        }
    public CCNodeSanctions(CCNode node)
        {
        super(STATE_POINT,"CCNodeSanctions");
        m_node = node;
        }
    public CCNodeSanctions(CCNode node, CCNetwork net)
        {
        super(STATE_POINT,net,"CCNodeSanctions");
        m_node = node;
        }

    public void addSanction(CCNodeSanction ns)
        {
        m_sanctions.addElement(ns);
        }

    public void applySettings(Hashtable h)
        {
        m_node = (CCNode)h.get("Node");
        m_network = (Network)h.get("Network");

        Vector Sanctions = (Vector)h.get("Sanctions");
        Enumeration enm = Sanctions.elements();
        while(enm.hasMoreElements())
            {
            Hashtable data = (Hashtable)enm.nextElement();
            CCNodeSanction sanction = new CCNodeSanction();
            sanction.applySettings(data);
            addSanction(sanction);
            }
        }

    public boolean canSendSanction(int tn)
        {
        Enumeration enm = m_sanctions.elements();
        while (enm.hasMoreElements())
            {
            CCNodeSanction ns = (CCNodeSanction)enm.nextElement();
            if ((ns.getToNode() == tn) && (!ns.getSent()))
                return true;
            }
        return false;
        }

    public Object clone()
        {
        CCNodeSanctions ccns = new CCNodeSanctions();
       
        Enumeration enm = m_sanctions.elements();
        while (enm.hasMoreElements())
            {
            CCNodeSanction Sanction = (CCNodeSanction)enm.nextElement();
            ccns.addSanction((CCNodeSanction)Sanction.clone());
            }

        return ccns;
        }

  // Draw info on a canvas using the graphic for the client screen.
    public void drawClient(Graphics g, Vector locInfo) {}

  // Draw info on a canvas using the graphic for the observer screen.
    public void drawObserver(Graphics g, Vector locInfo) {}

  // Draw info on a canvas using the graphic for the experimenter screen.
    public void drawExpt(Graphics g, Vector locInfo) {}

    public CCNodeSanction getSanction(int node)
        {
        Enumeration enm = m_sanctions.elements();
        while (enm.hasMoreElements())
            {
            CCNodeSanction ns = (CCNodeSanction)enm.nextElement();
            if (ns.getToNode() == node)
                return ns;
            }
        return null;
        }
    public Vector getSanctions()
        {
        return m_sanctions;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();

        settings.put("Type","DB-CCNodeSanctions");

        Vector sanctions = new Vector();
        Enumeration enm = m_sanctions.elements();
        while (enm.hasMoreElements())
            {
            CCNodeSanction sanction = (CCNodeSanction)enm.nextElement();
            sanctions.addElement(sanction.getSettings());
            }
        settings.put("Sanctions",sanctions);

        return settings;
        }
    public CCStateAction getStateAction()
        {
        return new CCNodeSanctionWindowStateAction();
        }
    public double getStatePoint()
        {
        CCNetwork ccn = (CCNetwork)m_network;
        Boolean rr = (Boolean)ccn.getExtraData("RoundRunning");
        if (rr.booleanValue())
            return -1;
        else
            return m_statePoint;
        }

    public boolean hasSanction(int node)
        {
        Enumeration enm = m_sanctions.elements();
        while (enm.hasMoreElements())
            {
            CCNodeSanction ns = (CCNodeSanction)enm.nextElement();
            if (ns.getToNode() == node)
                return true;
            }
        return false;
        }

    public void initializeNetwork()
        {
        Enumeration enm = m_sanctions.elements();
        while (enm.hasMoreElements())
            {
            CCNodeSanction sanction = (CCNodeSanction)enm.nextElement();
            sanction.setSent(false);
            }
        }
    public void initializeStart()
        {
        Enumeration enm = m_sanctions.elements();
        while (enm.hasMoreElements())
            {
            CCNodeSanction sanction = (CCNodeSanction)enm.nextElement();
            sanction.setSent(false);
            }
        }

    public boolean isEdgeActive(CCEdge edge)
        {
        CCNetwork ccn = (CCNetwork)m_network;
        if (edge.getNode1() == m_node.getID())
            {
            CCNode node = (CCNode)ccn.getNode(edge.getNode2());
            CCNodeSanctions ns = (CCNodeSanctions)node.getExptData("CCNodeSanctions");

            if ((!this.canSendSanction(node.getID())) && (!ns.canSendSanction(m_node.getID())))
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
            CCNodeSanctions ns = (CCNodeSanctions)node.getExptData("CCNodeSanctions");

            if ((!this.canSendSanction(node.getID())) && (!ns.canSendSanction(m_node.getID())))
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

    public void removeSanction(int node)
        {
        Enumeration enm = m_sanctions.elements();
        while (enm.hasMoreElements())
            {
            CCNodeSanction ns = (CCNodeSanction)enm.nextElement();
            if (ns.getToNode() == node)
                m_sanctions.removeElement(ns);
            }
        }

    public void reset() {}

    public Hashtable retrieveData(WLServerConnection wlsc, ExptMessage em, BaseDataInfo bdi)
        {
        try 
            {
            Hashtable nsData = new Hashtable();

            LoadDataResultsReq tmp = new LoadDataResultsReq("ccDB","CCExpt_Sanctions_Data_T",bdi,wlsc,em);

            ResultSet rs = tmp.runQuery();

            Vector data = new Vector();

            if (rs == null)
                {
                nsData.put("Data",data);
                return nsData;
                }

            while (rs.next())
                {
                CCSanctionsOutputObject soo = new CCSanctionsOutputObject(rs);
                data.addElement(soo);
                }
            nsData.put("Data",data);
  
            return nsData;
            }
        catch(Exception e) 
            {
            return new Hashtable();
            }
        }

    public void sanctionSent(int to, boolean msg)
        {
        Enumeration enm = m_sanctions.elements();
        while (enm.hasMoreElements())
            {
            CCNodeSanction ns = (CCNodeSanction)enm.nextElement();
            if (ns.getToNode() == to)
                {
                ns.setSent(true);
                ns.setMsg(msg);
                }
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
                    
            ResultSet rs = stmt.executeQuery("SELECT Object_ID_INT FROM Other_Objects_T WHERE Object_Name_VC = 'DB-CCNodeSanctions'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Object_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Other_Objects_T SET Object_OBJ = ? WHERE Object_ID_INT = "+index);

                CCNodeSanctions ns = new CCNodeSanctions();

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

                CCNodeSanctions ns = new CCNodeSanctions();

System.err.println(ns);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows a node to send a sanction or reward to another node every round.");

                cs.setString(1,"DB-CCNodeSanctions");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,ns,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("CCNodeSanctions Object ID: "+cs.getInt(4));
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