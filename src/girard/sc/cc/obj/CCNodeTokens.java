package girard.sc.cc.obj;
/* 
   Is the object class for attaching tokens to 
   a Node object in a CCNetwork.

   Author: Dudley Girard
   Started: 5-25-2001
   Modified: 7-16-2001
   Modified: 10-22-2002
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

public class CCNodeTokens extends CCNetworkComponent 
    {
    public static final double STATE_POINT = 1.2;

    protected Vector      m_tokens = new Vector(); // A list of people I can send tokens to.

    public CCNodeTokens()
        {
        super(STATE_POINT,"CCNodeTokens");
        }
    public CCNodeTokens(CCNode node)
        {
        super(STATE_POINT,"CCNodeTokens");
        m_node = node;
        }
    public CCNodeTokens(CCNode node, CCNetwork net)
        {
        super(STATE_POINT,net,"CCNodeTokens");
        m_node = node;
        }

    public void addToken(CCNodeToken nt)
        {
        m_tokens.addElement(nt);
        }

    public void applySettings(Hashtable h)
        {
        m_node = (CCNode)h.get("Node");
        m_network = (Network)h.get("Network");

        Vector tokens = (Vector)h.get("Tokens");
        Enumeration enm = tokens.elements();
        while(enm.hasMoreElements())
            {
            Hashtable data = (Hashtable)enm.nextElement();
            CCNodeToken token = new CCNodeToken();
            token.applySettings(data);
            addToken(token);
            }
        }

    public boolean canSendToken(int tn)
        {
        Enumeration enm = m_tokens.elements();
        while (enm.hasMoreElements())
            {
            CCNodeToken nt = (CCNodeToken)enm.nextElement();
            if ((nt.getToNode() == tn) && (!nt.getSent()))
                return true;
            }
        return false;
        }

    public Object clone()
        {
        CCNodeTokens ccnt = new CCNodeTokens();
       
        Enumeration enm = m_tokens.elements();
        while (enm.hasMoreElements())
            {
            CCNodeToken token = (CCNodeToken)enm.nextElement();
            ccnt.addToken((CCNodeToken)token.clone());
            }

        return ccnt;
        }

  // Draw info on a canvas using the graphic for the client screen.
    public void drawClient(Graphics g, Vector locInfo) {}

  // Draw info on a canvas using the graphic for the observer screen.
    public void drawObserver(Graphics g, Vector locInfo) {}

  // Draw info on a canvas using the graphic for the experimenter screen.
    public void drawExpt(Graphics g, Vector locInfo) {}

    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();

        settings.put("Type","DB-CCNodeTokens");

        Vector tokens = new Vector();
        Enumeration enm = m_tokens.elements();
        while (enm.hasMoreElements())
            {
            CCNodeToken token = (CCNodeToken)enm.nextElement();
            tokens.addElement(token.getSettings());
            }
        settings.put("Tokens",tokens);

        return settings;
        }
    public CCStateAction getStateAction()
        {
        return new CCNodeTokenWindowStateAction();
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
    public CCNodeToken getToken(int node)
        {
        Enumeration enm = m_tokens.elements();
        while (enm.hasMoreElements())
            {
            CCNodeToken nt = (CCNodeToken)enm.nextElement();
            if (nt.getToNode() == node)
                {
                return nt;
                }
            }
        return null;
        }  
    public Vector getTokens()
        {
        return m_tokens;
        }
/* Is this node in our list of nodes to send tokens to? */
    public boolean hasToken(int node)
        {
        Enumeration enm = m_tokens.elements();
        while (enm.hasMoreElements())
            {
            CCNodeToken nt = (CCNodeToken)enm.nextElement();
            if (nt.getToNode() == node)
                {
                return true;
                }
            }
        return false;
        }

    public void initializeNetwork()
        {
        Enumeration enm = m_tokens.elements();
        while (enm.hasMoreElements())
            {
            CCNodeToken token = (CCNodeToken)enm.nextElement();
            token.setSent(false);
            }
        }
    public void initializeStart()
        {
        Enumeration enm = m_tokens.elements();
        while (enm.hasMoreElements())
            {
            CCNodeToken token = (CCNodeToken)enm.nextElement();
            token.setSent(false);
            token.setTokens(0);
            }
        }

    public boolean isEdgeActive(CCEdge edge)
        {
        CCNetwork ccn = (CCNetwork)m_network;
        if (edge.getNode1() == m_node.getID())
            {
            CCNode node = (CCNode)ccn.getNode(edge.getNode2());
            CCNodeTokens nt = (CCNodeTokens)node.getExptData("CCNodeTokens");
            if ((!this.canSendToken(node.getID())) && (!nt.canSendToken(m_node.getID())))
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
            CCNodeTokens nt = (CCNodeTokens)node.getExptData("CCNodeTokens");
            if ((!this.canSendToken(node.getID())) && (!nt.canSendToken(m_node.getID())))
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

    public void removeToken(int node)
        {
        Enumeration enm = m_tokens.elements();
        while (enm.hasMoreElements())
            {
            CCNodeToken nt = (CCNodeToken)enm.nextElement();
            if (nt.getToNode() == node)
                {
                m_tokens.removeElement(nt);
                return;
                }
            }
        }

    public void reset() {}

    public Hashtable retrieveData(WLServerConnection wlsc, ExptMessage em, BaseDataInfo bdi)
        {
        try 
            {
            Hashtable ntData = new Hashtable();

            LoadDataResultsReq tmp = new LoadDataResultsReq("ccDB","CCExpt_Tokens_Data_T",bdi,wlsc,em);

            ResultSet rs = tmp.runQuery();

            Vector data = new Vector();

            if (rs == null)
                {
                ntData.put("Data",data);
                }
            else
                {
                while (rs.next())
                    {
                    CCTokensOutputObject too = new CCTokensOutputObject(rs);
                    data.addElement(too);
                    }
                ntData.put("Data",data);
                }

            LoadDataResultsReq tmp2 = new LoadDataResultsReq("ccDB","CCCoin_Toss_Data_T",bdi,wlsc,em);

            rs = tmp2.runQuery();

            Vector coin = new Vector();

            if (rs == null)
                {
                ntData.put("CoinToss",coin);
                return ntData;
                }

            while (rs.next())
                {
                CCCoinTossOutputObject too = new CCCoinTossOutputObject(rs);
                coin.addElement(too);
                }
            ntData.put("CoinToss",coin);
  
            return ntData;
            }
        catch(Exception e) 
            {
            return new Hashtable();
            }
        }

    public void tokenSent(int to, boolean msg)
        {
        Enumeration enm = m_tokens.elements();
        while (enm.hasMoreElements())
            {
            CCNodeToken nt = (CCNodeToken)enm.nextElement();
            if (nt.getToNode() == to)
                {
                nt.setSent(true);
                if (msg)
                    nt.setTokens(nt.getTokens() + 1);
                nt.setMsg(msg);
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
                    
            ResultSet rs = stmt.executeQuery("SELECT Object_ID_INT FROM Other_Objects_T WHERE Object_Name_VC = 'DB-CCNodeTokens'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Object_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Other_Objects_T SET Object_OBJ = ? WHERE Object_ID_INT = "+index);

                CCNodeTokens nt = new CCNodeTokens();

System.err.println(nt);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,nt,ps);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Desc, Network, id
                CallableStatement cs = con.prepareCall("{call up_insert_JOtherObject (?, ?, ?, ?)}");

                CCNodeTokens nt = new CCNodeTokens();

System.err.println(nt);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows a node to send a token to another node, at the end all the tokens are counted and a reward or penalty is issued.");

                cs.setString(1,"DB-CCNodeTokens");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,nt,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("CCNodeTokens Object ID: "+cs.getInt(4));
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
