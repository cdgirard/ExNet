package girard.sc.cc.obj;
/* 
   Is the object class for attaching fuzzies to 
   a Node object in a CCNetwork.

   Author: Dudley Girard
   Started: 5-29-2001
   Modified: 6-28-2001
   Modified: 7-16-2001
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

public class CCNodeFuzzies extends CCNetworkComponent 
    {
    public static final double STATE_POINT = 1.1;

    protected Vector      m_fuzzies = new Vector(); // A list of people to send fuzzies about other people.

    public CCNodeFuzzies()
        {
        super(STATE_POINT,"CCNodeFuzzies");
        }
    public CCNodeFuzzies(CCNode node)
        {
        super(STATE_POINT,"CCNodeFuzzies");
        m_node = node;
        }
    public CCNodeFuzzies(CCNode node, CCNetwork net)
        {
        super(STATE_POINT,net,"CCNodeFuzzies");
        m_node = node;
        }

    public void addFuzzy(CCNodeFuzzy nf)
        {
        m_fuzzies.addElement(nf);
        }

   public void applySettings(Hashtable h)
        {
        m_node = (CCNode)h.get("Node");
        m_network = (Network)h.get("Network");

        Vector fuzzies = (Vector)h.get("Fuzzies");
        Enumeration enm = fuzzies.elements();
        while(enm.hasMoreElements())
            {
            Hashtable data = (Hashtable)enm.nextElement();
            CCNodeFuzzy fuzzy = new CCNodeFuzzy();
            fuzzy.applySettings(data);
            addFuzzy(fuzzy);
            }
        }

/* If we can we send a fuzzy to this node, 
   have we sent all we can?
*/
    public boolean canSendFuzzy(int tn)
        {
        Enumeration enm = m_fuzzies.elements();
        while (enm.hasMoreElements())
            {
            CCNodeFuzzy nf = (CCNodeFuzzy)enm.nextElement();
            if ((nf.getToNode() == tn) && (!nf.getSent()))
                return true;
            }
        return false;
        }

    public Object clone()
        {
        CCNodeFuzzies ccnf = new CCNodeFuzzies();
       
        Enumeration enm = m_fuzzies.elements();
        while (enm.hasMoreElements())
            {
            CCNodeFuzzy fuzzy = (CCNodeFuzzy)enm.nextElement();
            ccnf.addFuzzy((CCNodeFuzzy)fuzzy.clone());
            }

        return ccnf;
        }

  // Draw info on a canvas using the graphic for the client screen.
    public void drawClient(Graphics g, Vector locInfo) {}

  // Draw info on a canvas using the graphic for the observer screen.
    public void drawObserver(Graphics g, Vector locInfo) {}

  // Draw info on a canvas using the graphic for the experimenter screen.
    public void drawExpt(Graphics g, Vector locInfo) {}

    public void fuzzySent(int to, int about, boolean msg)
        {
        Enumeration enm = m_fuzzies.elements();
        while (enm.hasMoreElements())
            {
            CCNodeFuzzy nf = (CCNodeFuzzy)enm.nextElement();
            if ((nf.getAboutNode() == about) && (nf.getToNode() == to))
                {
                nf.setSent(true);
                nf.setMsg(msg);
                }
            }
        }

    public Vector getFuzzies()
        {
        return m_fuzzies;
        }
    public CCNodeFuzzy getFuzzy(int an, int tn)
        {
        Enumeration enm = m_fuzzies.elements();
        while (enm.hasMoreElements())
            {
            CCNodeFuzzy nf = (CCNodeFuzzy)enm.nextElement();
            if ((nf.getAboutNode() == an) && (nf.getToNode() == tn))
                return nf;
            }
        return null;
        }
    public Vector getFuzzyList(int tn)
        {
        Vector v = new Vector();

        Enumeration enm = m_fuzzies.elements();
        while (enm.hasMoreElements())
            {
            CCNodeFuzzy nf = (CCNodeFuzzy)enm.nextElement();
            if (nf.getToNode() == tn)
                v.addElement(nf);
            }
        return v;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();

        settings.put("Type","DB-CCNodeFuzzies");

        Vector fuzzies = new Vector();
        Enumeration enm = m_fuzzies.elements();
        while (enm.hasMoreElements())
            {
            CCNodeFuzzy fuzzy = (CCNodeFuzzy)enm.nextElement();
            fuzzies.addElement(fuzzy.getSettings());
            }
        settings.put("Fuzzies",fuzzies);

        return settings;
        }
    public CCStateAction getStateAction()
        {
        return new CCNodeFuzzyWindowStateAction();
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

    public boolean hasFuzzy(int tn)
        {
        Enumeration enm = m_fuzzies.elements();
        while (enm.hasMoreElements())
            {
            CCNodeFuzzy nf = (CCNodeFuzzy)enm.nextElement();
            if (nf.getToNode() == tn)
                return true;
            }
        return false;
        }
    public boolean hasFuzzy(int an, int tn)
        {
        Enumeration enm = m_fuzzies.elements();
        while (enm.hasMoreElements())
            {
            CCNodeFuzzy nf = (CCNodeFuzzy)enm.nextElement();
            if ((nf.getAboutNode() == an) && (nf.getToNode() == tn))
                return true;
            }
        return false;
        }

    public void initializeNetwork()
        {
        Enumeration enm = m_fuzzies.elements();
        while (enm.hasMoreElements())
            {
            CCNodeFuzzy fuzzy = (CCNodeFuzzy)enm.nextElement();
            fuzzy.setSent(false);
            }
        }
    public void initializeStart()
        {
        Enumeration enm = m_fuzzies.elements();
        while (enm.hasMoreElements())
            {
            CCNodeFuzzy fuzzy = (CCNodeFuzzy)enm.nextElement();
            fuzzy.setSent(false);
            }
        }

/*
we can look at both nodes on an edge's list of trade parteners and then decide
if the edge is active or not.  since all nodes will do this check we can disable
all edges that should be.
*/
    public boolean isEdgeActive(CCEdge edge)
        {
        CCNetwork ccn = (CCNetwork)m_network;
        if (edge.getNode1() == m_node.getID())
            {
            CCNode node = (CCNode)ccn.getNode(edge.getNode2());
            CCNodeFuzzies nf = (CCNodeFuzzies)node.getExptData("CCNodeFuzzies");

            if ((!this.canSendFuzzy(node.getID())) && (!nf.canSendFuzzy(m_node.getID())))
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
            CCNodeFuzzies nf = (CCNodeFuzzies)node.getExptData("CCNodeFuzzies");

            if ((!this.canSendFuzzy(node.getID())) && (!nf.canSendFuzzy(m_node.getID())))
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

    public void removeFuzzies(int tn)
        {
        Enumeration enm = m_fuzzies.elements();
        while (enm.hasMoreElements())
            {
            CCNodeFuzzy nf = (CCNodeFuzzy)enm.nextElement();
            if (nf.getToNode() == tn)
                m_fuzzies.removeElement(nf);
            }
        }

    public void reset() {}

    public Hashtable retrieveData(WLServerConnection wlsc, ExptMessage em, BaseDataInfo bdi)
        {
        try 
            {
            Hashtable nfData = new Hashtable();

            LoadDataResultsReq tmp = new LoadDataResultsReq("ccDB","CCExpt_Fuzzies_Data_T",bdi,wlsc,em);

            ResultSet rs = tmp.runQuery();

            Vector data = new Vector();

            if (rs == null)
                {
                nfData.put("Data",data);
                return nfData;
                }

            while (rs.next())
                {
                CCFuzziesOutputObject foo = new CCFuzziesOutputObject(rs);
                data.addElement(foo);
                }
            nfData.put("Data",data);
  
            return nfData;
            }
        catch(Exception e) 
            {
            return new Hashtable();
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
                    
            ResultSet rs = stmt.executeQuery("SELECT Object_ID_INT FROM Other_Objects_T WHERE Object_Name_VC = 'DB-CCNodeFuzzies'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Object_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Other_Objects_T SET Object_OBJ = ? WHERE Object_ID_INT = "+index);

                CCNodeFuzzies nf = new CCNodeFuzzies();

System.err.println(nf);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,nf,ps);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Desc, Network, id
                CallableStatement cs = con.prepareCall("{call up_insert_JOtherObject (?, ?, ?, ?)}");

                CCNodeFuzzies nf = new CCNodeFuzzies();

System.err.println(nf);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows nodes permission to send information about nodes in the network to other nodes.");

                cs.setString(1,"DB-CCNodeFuzzies");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,nf,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("CCNodeFuzzies Object ID: "+cs.getInt(4));
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