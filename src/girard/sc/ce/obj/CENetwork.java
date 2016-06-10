package girard.sc.ce.obj;

import girard.sc.exnet.obj.Edge;
import girard.sc.exnet.obj.Network;
import girard.sc.exnet.obj.Node;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.wl.io.WLGeneralServerConnection;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Commodities Exchange Network
 * <p>
 * <br> Started: 7-23-2002
 * <br> Modified: 1-16-2003
 * <p>
 * @author Dudley Girard
 */

public class CENetwork extends Network
    {
    protected Vector    m_periodList = new Vector();  // Should not be added till the experiment is built.
    protected int       m_currentPeriod = -1;
    protected Hashtable m_extraData = new Hashtable(); // Used to store end user specific data.
                                                       // Stores the index value for the client stations, under the key "Index"
    public CENetwork()
        {
        super("CENetwork","none","none");
        }
    public CENetwork(Network net)
        {
        super("CENetwork","none","none");

        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            Node n = (Node)enm.nextElement();
            addNode(new CENode((Node)n.clone()));
            }
        enm = net.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            Edge e = (Edge)enm.nextElement();
            addEdge(new CEEdge((Edge)e.clone()));
            }
        m_fileName = net.getFileName();
        m_userID = net.getUserID();
        }
  
    public void addEdge(CEEdge obj)
        {
        m_edgeList.addElement(obj);
        }
    public void addNode(CENode en)
        {
        m_nodeList.put(new Integer(en.getID()),en);
        }
    public void addPeriod(CEPeriod np)
        {
        m_periodList.addElement(np);
        }

    public void applySettings(Hashtable h)
        {
        Hashtable objects = (Hashtable)h.get("Objects");

        Vector nodes = (Vector)h.get("Nodes");
        Enumeration enm = nodes.elements();
        while (enm.hasMoreElements())
            {
            Hashtable data = (Hashtable)enm.nextElement();
            data.put("Network",this);
            data.put("Objects",objects);
            CENode cen = new CENode();
            cen.applySettings(data);
            addNode(cen);
            }

        Vector edges = (Vector)h.get("Edges");
        enm = edges.elements();
        while (enm.hasMoreElements())
            {
            Hashtable data = (Hashtable)enm.nextElement();
            data.put("Network",this);
            data.put("Objects",objects);
            CEEdge cee = new CEEdge();
            cee.applySettings(data);
            addEdge(cee);
            }

        Vector periods = (Vector)h.get("Periods");

        enm = periods.elements();
        while (enm.hasMoreElements())
            {
            Hashtable data = (Hashtable)enm.nextElement();
            CEPeriod cep = new CEPeriod();
            cep.applySettings(data);
            m_periodList.addElement(cep);  // The correct order should have been preserved by getSettings().
            }

        m_extraData = (Hashtable)h.get("ExtraData");
        m_fileName = (String)h.get("FileName");
        m_userID = ((Integer)h.get("UserID")).intValue();
        }

/**
 * Does not clone the values held in m_extraData.
 */
    public Object clone()
        {
        CENetwork n = new CENetwork();

        n.setUserID(m_userID);
        n.setActionType(m_ActionType);
        n.setFileName(new String(m_fileName));
        n.setCounter(m_counter);
        n.setCurrentPeriod(m_currentPeriod);
        n.setDesc(new String(m_desc));
        n.setDBTable(new String(m_dbTable));
        n.setDB(new String(m_db));
        n.setName(new String(m_name));

        Enumeration enm = m_nodeList.elements();
        while (enm.hasMoreElements())
            {
            CENode tmp = (CENode)enm.nextElement();
            tmp.setComponentNetwork(n);
            n.addNode((CENode)tmp.clone());
            }
        enm = m_edgeList.elements();
        while (enm.hasMoreElements())
            {
            CEEdge tmp = (CEEdge)enm.nextElement();
            tmp.setComponentNetwork(n);
            n.addEdge((CEEdge)tmp.clone());
            }
        enm = m_periodList.elements();
        while (enm.hasMoreElements())
            {
            CEPeriod tmp = (CEPeriod)enm.nextElement();
            tmp.setNetwork(n);
            n.addPeriod((CEPeriod)tmp.clone());
            }

        if (getExtraData("Pay") != null)
            {
            double[] pay = (double[])getExtraData("Pay");
            double[] newPay = new double[pay.length];
            for (int x=0;x<pay.length;x++)
                {
                newPay[x] = pay[x];
                }
            n.setExtraData("Pay",newPay);
            }

        return n;
        }

    public CEPeriod getActivePeriod()
        {
        if (m_currentPeriod > -1)
            return (CEPeriod)m_periodList.elementAt(m_currentPeriod);
        else
            return null;
        }
    public int getCurrentPeriod()
        {
        return m_currentPeriod;
        }
    public Hashtable getExtraData()
        {
        return m_extraData;
        }
    public Object getExtraData(String str)
        {
        return m_extraData.get(str);
        }
    /**
     * utility method to print the extra data
     */
    public void printExtraData(){
/*
    	Enumeration enm = m_extraData.keys();
    	while(enm.hasMoreElements()){
    		String s = (String)enm.nextElement();
    		System.err.print(s+":");
    		if(s.equals("PntEarnedRound")){
    			System.err.print("="+((Double)this.getExtraData("PntEarnedRound")).intValue());
    		}
    		else if(s.equals("Data")){
    			Hashtable h42= (Hashtable)m_extraData.get("Data");
    			Enumeration enum2 = h42.keys();
    			while(enum2.hasMoreElements()){
    				String dataStr = (String)enum2.nextElement();
    				System.err.println("Data element---"+dataStr);
    			}
    		}
    		System.err.println();
    	}
*/	
    }
    public CEStateAction getNextState()
        {
        CEStateAction cesa = null;
        CEPeriod cep = getActivePeriod();
        CEEdge cee = (CEEdge)m_edgeList.elementAt(0);
        CENode cen = (CENode)m_nodeList.get(new Integer(cee.getNode1()));

        double ps1 = cep.getNextValidState(this);
        double ps2 = cen.getNextValidState(this);
        double ps3 = cee.getNextValidState(this);
        
        if ((ps1 < ps2) && (ps1 < ps3) && (ps1 != 100))
            {
            cesa =  cep.getNextState(this);
            setExtraData("CurrentState",new Double(ps1));
            }
        if ((ps2 < ps1) && (ps2 < ps3) && (ps2 != 100))
            {
            cesa = cen.getNextState(this);
            setExtraData("CurrentState",new Double(ps2));
            }
        if ((ps3 < ps1) && (ps3 < ps2) && (ps3 != 100))
            {
            cesa = cee.getNextState(this);
            setExtraData("CurrentState",new Double(ps3));
            }

        return cesa;
        }
    public int getNumPeriods()
        {
        return m_periodList.size();
        }
    public CEPeriod getPeriod(int value)
        {
        return (CEPeriod)m_periodList.elementAt(value);
        }
    public Hashtable getSettings()
        {
        Hashtable settings = super.getSettings();

        Vector types = new Vector();  // A list of all the objects that will needed to be
                                      // loaded in when this network is retreived later.

        Enumeration enm = m_nodeList.elements();
        while(enm.hasMoreElements())
            {
            CENode cen = (CENode)enm.nextElement();
            Hashtable data = cen.getSettings();
            Vector nodeTypes = (Vector)data.get("Types");
            Enumeration enum2 = nodeTypes.elements();
            while (enum2.hasMoreElements())
                {
                String type = (String)enum2.nextElement();
                types.addElement(type);
                }
            }

        enm = m_edgeList.elements();
        while(enm.hasMoreElements())
            {
            CEEdge cee = (CEEdge)enm.nextElement();
            Hashtable data = cee.getSettings();
            Vector edgeTypes = (Vector)data.get("Types");
            Enumeration enum2 = edgeTypes.elements();
            while (enum2.hasMoreElements())
                {
                String type = (String)enum2.nextElement();
                types.addElement(type);
                }
            }

        Vector periods = new Vector();
        enm = m_periodList.elements();
        while(enm.hasMoreElements())
            {
            CEPeriod cep = (CEPeriod)enm.nextElement();
            periods.addElement(cep.getSettings());
            }
        settings.put("Periods",periods);
        settings.put("Types",types);
        settings.put("ExtraData",m_extraData);
        settings.put("CurrentPeriod",new Integer(m_currentPeriod));

        return settings;
        }

    public void initializeNetwork()
        {
   // We do this first so then any components that might disable an edge can
   // do so without fear of the edge going back later and renabling itself.
        Enumeration enm = getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CEEdge edge = (CEEdge)enm.nextElement();
            edge.setCompleted(false);
            edge.setActive(true);
            }
        enm = getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CENode node = (CENode)enm.nextElement();
            node.initializeNetwork();
            }
        enm = getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CEEdge edge = (CEEdge)enm.nextElement();
            edge.initializeNetwork();
            }
        }

 // Why do we have removeEdge and removeNode in here?
    public void removeEdge(CEEdge ee)
        {
        m_edgeList.removeElement(ee);
        }
    public void removeNode(CENode en)
        {
        m_nodeList.remove(new Integer(en.getID()));
        }
    public void removePeriod(int index)
        {
        m_periodList.removeElementAt(index);
        Enumeration e = m_periodList.elements();
        int i = 1;
        while (e.hasMoreElements())
            {
            CEPeriod np = (CEPeriod)e.nextElement();
            np.setPeriod(i);
            i++;
            }
        }

    public Hashtable retrieveData(WLGeneralServerConnection wlgsc, ExptMessage em, BaseDataInfo bdi)
        {
        Hashtable networkData = new Hashtable();


	//// get the externality data here!!!
        CEPeriod cep = (CEPeriod)m_periodList.elementAt(0);
        CEEdge cee = (CEEdge)m_edgeList.elementAt(0);
        CENode cen = (CENode)m_nodeList.get(new Integer(cee.getNode1()));

        Hashtable periodData = cep.retrieveData(wlgsc,em,bdi);
        Hashtable nodeData = cen.retrieveData(wlgsc,em,bdi);
        Hashtable edgeData = cee.retrieveData(wlgsc,em,bdi);

        networkData.put("Period Data",periodData);
        networkData.put("Node Data",nodeData);
        networkData.put("Edge Data",edgeData);
	
        return networkData;
        }

    public void setCurrentPeriod(int value)
        {
        m_currentPeriod = value;
        }
    public void setEdgeAnchorPoints()
        {
        Enumeration e = m_edgeList.elements();
        while (e.hasMoreElements())
            {
            CEEdge cee = (CEEdge)e.nextElement();
   
            CENode cen = (CENode)m_nodeList.get(new Integer(cee.getNode1()));
            cee.setN1Anchor(cen.getLoc().x+6,cen.getLoc().y+6);

            cen = (CENode)m_nodeList.get(new Integer(cee.getNode2()));
            cee.setN2Anchor(cen.getLoc().x+6,cen.getLoc().y+6);
            }
        }
    public void setExtraData(Hashtable h)
        {
        m_extraData = h;
        }
    public void setExtraData(String key, Object obj)
        {
        if (m_extraData.containsKey(key))
            {
            m_extraData.remove(key);
            m_extraData.put(key,obj);
            }
        else
            {
            m_extraData.put(key,obj);
            }
        }
    }
