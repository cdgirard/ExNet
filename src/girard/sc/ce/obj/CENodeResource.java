package girard.sc.ce.obj;

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

/**
 * Is the object class for attaching resources to 
 * a Node object in a CENetwork.
 * <p>
 * <br> Started: 07-23-2002
 * <br> Modified: 01-16-2003
 * <p>
 * @author Dudley Girard
 */

public class CENodeResource extends CENetworkComponent 
    {

/**
 * Which resources I have to offer others.
 */
    protected Hashtable m_availableResources = new Hashtable();
/**
 * Which resources I start with every round.
 */
    protected Hashtable m_initialResources = new Hashtable();
/**
 * Maximum number of exchanges that can be made.
 */ 
    protected int       m_max = 1;
/**
 * Number of exchanges made so far.
 */
    protected int       m_exchanges = 0;

    public CENodeResource()
        {
        super("CENodeResource");
        }
    public CENodeResource (CENode node)
        {
        super("CENodeResource");
        m_node = node;
        addInitialResource(new CEResource(m_node.getID(),"Apples","A",0,0));
        addInitialResource(new CEResource(m_node.getID(),"Oranges","O",0,0));
        }
    public CENodeResource (CENode node, CENetwork net)
        {
        super(net,"CENodeResource");
        m_node = node;
        addInitialResource(new CEResource(m_node.getID(),"Apples","A",0,0));
        addInitialResource(new CEResource(m_node.getID(),"Oranges","O",0,0));
        }

    public void addAvailableResource(CEResource cer)
        {
        m_availableResources.put(cer.getLabel(),cer);
        }
    public void addInitialResource(CEResource cer)
        {
        m_initialResources.put(cer.getLabel(),cer);
        } 

    public void adjustEarnings()
        {
        }

    public void applySettings(Hashtable h)
        {
        m_node = (CENode)h.get("Node");
        m_network = (Network)h.get("Network");
        m_max = ((Integer)h.get("Max")).intValue();
        m_exchanges = ((Integer)h.get("Exchanges")).intValue();

        Vector ar = (Vector)h.get("AvailableResources");
        Enumeration enm = ar.elements();
        while (enm.hasMoreElements())
            {
            Hashtable r = (Hashtable)enm.nextElement();
            CEResource cer = new CEResource();
            cer.applySettings(r);
            addAvailableResource(cer);
            }

        Vector ir = (Vector)h.get("InitialResources");
        enm = ir.elements();
        while (enm.hasMoreElements())
            {
            Hashtable r = (Hashtable)enm.nextElement();
            CEResource cer = new CEResource();
            cer.applySettings(r);
            addInitialResource(cer);
            }
        }

    public Object clone()
        {
        CENodeResource cenr = new CENodeResource();
        cenr.setNode(m_node);

        Enumeration enm = m_availableResources.elements();
        while (enm.hasMoreElements())
            {
            CEResource cer = (CEResource)enm.nextElement();
            cenr.addAvailableResource((CEResource)cer.clone());
            }

        enm = m_initialResources.elements();
        while (enm.hasMoreElements())
            {
            CEResource cer = (CEResource)enm.nextElement();
            cenr.addInitialResource((CEResource)cer.clone());
            }

        cenr.setMax(m_max);
        cenr.setExchanges(m_exchanges);

        return cenr;
        }

  // Draw info on a canvas using the graphic for the client screen.
    public void drawClient(Graphics g, Vector locInfo) {}

  // Draw info on a canvas using the graphic for the experimenter screen.
    public void drawExpt(Graphics g, Vector locInfo) {}

  // Draw info on a canvas using the graphic for the observer screen.
    public void drawObserver(Graphics g, Vector locInfo) {}

    public Hashtable getAvailableResources()
        {
        return m_availableResources;
        }
    public CEResource getAvailableResources(String name)
        {
        if (m_availableResources.containsKey(name))
            {
            return (CEResource)m_availableResources.get(name);
            }
        return null;
        }
    public double getAvailableWorth()
        {
        double worth = 0;
        Enumeration enm = m_availableResources.elements();
        while (enm.hasMoreElements())
            {
            CEResource cer = (CEResource)enm.nextElement();
            worth = worth + cer.getValue()*cer.getResource();
            }
        return worth;
        }

	//nvm - quick and dirty hack...  
	// gives the difference in the value of resources at the
	// beginning of the expt and the instant at which this
	// function is called
	public double getNetProfit(){
	    double worth = 0;
	    Enumeration en = m_initialResources.elements();
	    while(en.hasMoreElements()){
		CEResource cer = (CEResource)en.nextElement();
		worth+= cer.getValue()*cer.getResource();
	    }
	    return getAvailableWorth()-worth;
	}
    public int getExchanges()
        {
        return m_exchanges;
        }
    public Hashtable getInitialResources()
        {
        return m_initialResources;
        }
    public CEResource getInitialResources(String name)
        {
        if (m_initialResources.containsKey(name))
            {
            return (CEResource)m_initialResources.get(name);
            }
        return null;
        }
    public CENode getNode()
        {
        return m_node;
        }
    public int getMax()
        {
        return m_max;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();

        settings.put("Type","DB-CENodeResource");

        settings.put("Max",new Integer(m_max));
        settings.put("Exchanges",new Integer(m_exchanges));

        Vector ar = new Vector();
        Enumeration enm = m_availableResources.elements();
        while (enm.hasMoreElements())
            {
            CEResource cer = (CEResource)enm.nextElement();
            ar.addElement(cer.getSettings());
            }
        settings.put("AvailableResources",ar);

        Vector ir = new Vector();
        enm = m_initialResources.elements();
        while (enm.hasMoreElements())
            {
            CEResource cer = (CEResource)enm.nextElement();
            ir.addElement(cer.getSettings());
            }
        settings.put("InitialResources",ir);

        return settings;
        }

/**
 * We can look at both nodes on an edge's list of trade parteners and then decide
 * if the edge is active or not.  Since all nodes will do this check we can disable
 * all edges that should be.
 */
    public void initializeNetwork()
        {
        m_exchanges = 0;
        m_availableResources.clear();
        Enumeration enm = m_initialResources.elements();
        while (enm.hasMoreElements())
            {
            CEResource cer = (CEResource)enm.nextElement();
            addAvailableResource((CEResource)cer.clone());
            }
        }
    public void initializeStart()
        { 
        m_availableResources.clear();
        Enumeration enm = m_initialResources.elements();
        while (enm.hasMoreElements())
            {
            CEResource cer = (CEResource)((CEResource)enm.nextElement()).clone();
            addAvailableResource((CEResource)cer.clone());
            }
        }
    public boolean isEdgeActive(CEEdge edge)
        {
        if (edge.getCompleted())
            return false;

        if (m_max == 0)
            return true;

        if (m_exchanges >= m_max)
            return false;

        return true;
        }

    public void removeAvailableResource(String key)
        {
        m_availableResources.remove(key);
        }
    public void removeInitialResource(String key)
        {
        m_initialResources.remove(key);
        } 
/**
 * Should reset the component to it's initial values.
 */
    public void reset()
        {
        m_availableResources.clear();
        Enumeration enm = m_initialResources.elements();
        while (enm.hasMoreElements())
            {
            CEResource cer = (CEResource)((CEResource)enm.nextElement()).clone();
            addAvailableResource((CEResource)cer.clone());
            }
        }

    public Hashtable retrieveData(WLServerConnection wlsc, ExptMessage em, BaseDataInfo bdi)
        {
        try 
            {
            Hashtable erData = new Hashtable();

            LoadDataResultsReq tmp = new LoadDataResultsReq("ccDB","CE_Resource_Data_T",bdi,wlsc,em);

            ResultSet rs = tmp.runQuery();

            Vector data = new Vector();

	    if(rs!=null){
		while (rs.next())
		    {
			CEEndRoundResOutputObject cero = new CEEndRoundResOutputObject(rs);
			data.addElement(cero);
		    }
	    }
	    erData.put("Resource Data",data); // note - data is empty if rs is null
  
	    //nvm - retrieving externality here...
	    LoadDataResultsReq tmp2 = new LoadDataResultsReq("ccDB","CE_Externality_Data_T",bdi,wlsc,em);
	    ResultSet rs2 = tmp2.runQuery();
	    Vector extData = new Vector();
	    if(rs2!=null){
		while(rs2.next()){
		    CEExternalityOutputObject ceeoo = new CEExternalityOutputObject(rs2);
		    extData.add(ceeoo);
		}
	    }
	    erData.put("Externality Data", extData);
	   
            return erData;
            }
        catch(Exception e) 
            {
            wlsc.addToLog(e.getMessage());
            return new Hashtable();
            }
        }

    public void setExchanges(int value)
        {
        m_exchanges = value;
        }
    public void setNode(CENode n)
        {
        m_node = n;
        }
    public void setMax(int value)
        {
        m_max = value;
        }

    public void updateAvailableResources(CEResource gain, CEResource lost)
        {
        CEResource cer1 = getAvailableResources(gain.getLabel());
        cer1.setResource(gain.getResource() + cer1.getResource());

        CEResource cer2 = getAvailableResources(lost.getLabel());
        cer2.setResource(cer2.getResource() - lost.getResource());
        }
    public static void updateDBEntry(Connection con)
        {
        try
            { 
            // get a Statement object from the Connection
            //
            // Place new Simulant Type objects.
            Statement stmt = con.createStatement();
                    
            ResultSet rs = stmt.executeQuery("SELECT Object_ID_INT FROM Other_Objects_T WHERE Object_Name_VC = 'DB-CENodeResource'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Object_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Other_Objects_T SET Object_OBJ = ? WHERE Object_ID_INT = "+index);

                CENodeResource nr = new CENodeResource();

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

                CENodeResource nr = new CENodeResource();

System.err.println(nr);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Stores the resources a node has during a CENetworkAction.");

                cs.setString(1,"DB-CENodeResource");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,nr,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("CENodeResource Object ID: "+cs.getInt(4));
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
