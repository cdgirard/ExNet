package girard.sc.exnet.obj;

import girard.sc.exnet.awt.NetworkBuilderWindow;
import girard.sc.expt.awt.ActionBuilderWindow;
import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.io.FMSObjCon;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Network extends BaseAction
    {
    protected static final String OBJ_NAME = new String("Network");
    protected static final String DB = new String("exnetDB");
    protected static final String DB_TABLE = new String("Networks_T");

    protected int         m_counter = 1;
    protected Hashtable   m_nodeList = new Hashtable();
    protected Vector      m_edgeList = new Vector();
  
    public Network()
        {
        super(OBJ_NAME,DB,DB_TABLE);
        }
    public Network(String name, String db, String dbTable)
        {
        super(name,db,dbTable);
        }
  
    public void addEdge(Edge obj)
        {
        m_edgeList.addElement(obj);
        }
    public void addNode(Node en)
        {
        if (en.getID() == -1)
            {
            en.setID(m_counter++);
            m_nodeList.put(new Integer(en.getID()),en);
            }
        else
            {
            m_nodeList.put(new Integer(en.getID()),en);
            if (m_counter <= en.getID())
                m_counter = en.getID()+1;
            }
        }

    public void applySettings(Hashtable h)
        {
        super.applySettings(h);

        Vector v = (Vector)h.get("Edges");
        Enumeration enm = v.elements();
        while (enm.hasMoreElements())
            {
            Hashtable data = (Hashtable)enm.nextElement();
            Edge e = new Edge();
            e.applySettings(data);
            addEdge(e);
            }

        v = (Vector)h.get("Nodes");
        enm = v.elements();
        while (enm.hasMoreElements())
            {
            Hashtable data = (Hashtable)enm.nextElement();
            Node n = new Node();
            n.applySettings(data);
            addNode(n);
            }
        }

    public Object clone()
        {
        Network n = new Network();

        n.applySettings(this.getSettings());

        return n;
        }

    public void formatAction(ExptOverlord app, ActionBuilderWindow abw)
        {
        new NetworkBuilderWindow(app,abw,this);
        }

    public Vector getEdgeList()
        {
        return m_edgeList;
        }
    public String getInsertFormat()
        {
    /* ID_INT, App_ID, App_Name_VC, Name_VC, Desc_INT, Settings_OBJ */
        return new String("{call up_insert_JNetworks (?, ?, ?, ?, ?, ?)}");
        }
    public Node getNode(int value)
        {
        return (Node)m_nodeList.get(new Integer(value));
        }
    public Node getNode(Integer value)
        {
        return (Node)m_nodeList.get(value);
        }
    public Hashtable getNodeList()
        {
        return m_nodeList;
        }
    public int getNumEdges()
        {
        return m_edgeList.size();
        }
    public int getNumNodes()
        {
        return m_nodeList.size();
        }
    public Hashtable getSettings()
        {
        Hashtable settings = super.getSettings();


        Vector edges = new Vector();
        Enumeration enm = m_edgeList.elements();
        while (enm.hasMoreElements())
            {
            Edge e = (Edge)enm.nextElement();
            edges.addElement(e.getSettings());
            }
        settings.put("Edges",edges);

        Vector nodes = new Vector();
        enm = m_nodeList.elements();
        while (enm.hasMoreElements())
            {
            Node n = (Node)enm.nextElement();
            nodes.addElement(n.getSettings());
            }
        settings.put("Nodes",nodes);
       
        return settings;
        }

    public void removeEdge(Edge ee)
        {
        m_edgeList.removeElement(ee);
        }
    public void removeNode(Node en)
        {
        m_nodeList.remove(new Integer(en.getID()));

        // Cleanup Edge List
        Enumeration enm = m_edgeList.elements();
        while (enm.hasMoreElements())
            {
            Edge Etemp = (Edge)enm.nextElement();

            if ((Etemp.getNode1() == en.getID()) || (Etemp.getNode2() == en.getID()))
                {
                removeEdge(Etemp);
                enm = m_edgeList.elements();
                }
            }
        }

    public void setCounter(int value)
        {
        m_counter = value;
        }
    public void setEdgeAnchorPoints()
        {
        Enumeration e = m_edgeList.elements();
        while (e.hasMoreElements())
            {
            Edge ebe = (Edge)e.nextElement();
   
            Node ebn = (Node)m_nodeList.get(new Integer(ebe.getNode1()));
            ebe.setN1Anchor(ebn.getLoc().x+6,ebn.getLoc().y+6);

            ebn = (Node)m_nodeList.get(new Integer(ebe.getNode2()));
            ebe.setN2Anchor(ebn.getLoc().x+6,ebn.getLoc().y+6);
            }
        }
    public void setFileName(String fn)
        {
        m_fileName = fn;
        }
    public void setUserID(int value)
        {
        m_userID = value;
        }

    public static void updateDBEntry(Connection con)
        {
        try
            { 
            // get a Statement object from the Connection
            //
            // Place new Simulant Type objects.
            Statement stmt = con.createStatement();
                    
            ResultSet rs = stmt.executeQuery("SELECT Base_Action_Type_ID_INT FROM Base_Actions_Type_T WHERE Base_Action_Name_VC = 'Network'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Base_Action_Type_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Base_Actions_Type_T SET Base_Action_OBJ = ? WHERE Base_Action_Type_ID_INT = "+index);

                Network net = new Network();

System.err.println(net+" "+net.getName());
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,net,ps);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Desc, Network, id
                CallableStatement cs = con.prepareCall("{call up_insert_JBaseActionType (?, ?, ?, ?)}");

                Network net = new Network();

System.err.println(net);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows you to build a simple network of nodes and relations.");

                cs.setString(1,"Network");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,net,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("Network Object ID: "+cs.getInt(4));
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
