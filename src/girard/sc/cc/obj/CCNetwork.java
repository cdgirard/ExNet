package girard.sc.cc.obj;

import girard.sc.exnet.obj.Edge;
import girard.sc.exnet.obj.Network;
import girard.sc.exnet.obj.Node;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.wl.io.WLGeneralServerConnection;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class CCNetwork extends Network
    {
    protected CCPeriod  m_period = null;  // Should not be added till the experiment is built.
                                          // Probably should have placed this in the m_extraData as well,
                                          // but easier to deal with it out here with the way the code is
                                          // already setup.
    protected Hashtable m_extraData = new Hashtable(); // Used to store end user specific data.
                                                       // Stores the index value for the client stations, under the key "Index"
    public CCNetwork()
        {
        super("CCNetwork","none","none");
        }
    public CCNetwork(Network net)
        {
        super("CCNetwork","none","none");

        m_userID = net.getUserID(); 
        m_fileName = net.getFileName();
        m_desc = net.getDesc();

        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            Node n = (Node)enm.nextElement();
            addNode(new CCNode((Node)n.clone()));
            }
        enm = net.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            Edge e = (Edge)enm.nextElement();
            addEdge(new CCEdge((Edge)e.clone()));
            }
        }
  
    public void addEdge(CCEdge obj)
        {
        m_edgeList.addElement(obj);
        }
    public void addNode(CCNode en)
        {
        m_nodeList.put(new Integer(en.getID()),en);
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
            CCNode ccn = new CCNode();
            ccn.applySettings(data);
            addNode(ccn);
            }

        Vector edges = (Vector)h.get("Edges");
        enm = edges.elements();
        while (enm.hasMoreElements())
            {
            Hashtable data = (Hashtable)enm.nextElement();
            data.put("Network",this);
            data.put("Objects",objects);
            CCEdge cce = new CCEdge();
            cce.applySettings(data);
            addEdge(cce);
            }

        Hashtable periodData = (Hashtable)h.get("Period");
        CCPeriod ccp = new CCPeriod();
        ccp.applySettings(periodData);
        m_period = ccp;

        m_extraData = (Hashtable)h.get("ExtraData");

        h.put("Nodes",new Vector()); // So we don't add more nodes when apply the Network applySettings method.
        h.put("Edges",new Vector()); // So we don't add more edges when apply the Network applySettings method.
        super.applySettings(h);
        h.put("Nodes",nodes); // Still might need for later.
        h.put("Edges",edges); // Still might need for later.
        }

    public Object clone()
        {
        CCNetwork n = new CCNetwork();

        n.setUserID(m_userID);
        n.setActionType(m_ActionType);
        n.setFileName(new String(m_fileName));
        n.setCounter(m_counter);
        n.setPeriod((CCPeriod)m_period.clone());
        n.setDesc(new String(m_desc));
        n.setDBTable(new String(m_dbTable));
        n.setDB(new String(m_db));
        n.setName(new String(m_name));

        Enumeration enm = m_nodeList.elements();
        while (enm.hasMoreElements())
            {
            CCNode tmp = (CCNode)((CCNode)enm.nextElement()).clone();
            tmp.setComponentNetwork(n);
            n.addNode(tmp);
            }
        enm = m_edgeList.elements();
        while (enm.hasMoreElements())
            {
            CCEdge tmp = (CCEdge)((CCEdge)enm.nextElement()).clone();
            tmp.setComponentNetwork(n);
            n.addEdge(tmp);
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

    public Hashtable getExtraData()
        {
        return m_extraData;
        }
    public Object getExtraData(String str)
        {
        return m_extraData.get(str);
        }
// We may have some changes to make since not all nodes will have tokens or fuzzies.
    public CCStateAction getNextState()
        {
        CCStateAction ccsa = null;
        CCPeriod ccp = m_period;
        CCEdge cce = (CCEdge)m_edgeList.elementAt(0);
        CCNode ccn = (CCNode)m_nodeList.get(new Integer(cce.getNode1()));

        double ps1 = ccp.getNextValidState(this);
        double ps2 = ccn.getNextValidState(this);
        double ps3 = cce.getNextValidState(this);
        
        if ((ps1 < ps2) && (ps1 < ps3) && (ps1 != 100))
            {
            ccsa =  ccp.getNextState(this);
            setExtraData("CurrentState",new Double(ps1));
            }
        if ((ps2 < ps1) && (ps2 < ps3) && (ps2 != 100))
            {
            ccsa = ccn.getNextState(this);
            setExtraData("CurrentState",new Double(ps2));
            }
        if ((ps3 < ps1) && (ps3 < ps2) && (ps3 != 100))
            {
            ccsa = cce.getNextState(this);
            setExtraData("CurrentState",new Double(ps3));
            }

        return ccsa;
        }
    public CCPeriod getPeriod()
        {
        return m_period;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = super.getSettings();

        Vector types = new Vector();  // A list of all the objects that will needed to be
                                      // loaded in when this network is retreived later.

        Enumeration enm = m_nodeList.elements();
        while(enm.hasMoreElements())
            {
            CCNode ccn = (CCNode)enm.nextElement();
            Hashtable data = ccn.getSettings();
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
            CCEdge cce = (CCEdge)enm.nextElement();
            Hashtable data = cce.getSettings();
            Vector edgeTypes = (Vector)data.get("Types");
            Enumeration enum2 = edgeTypes.elements();
            while (enum2.hasMoreElements())
                {
                String type = (String)enum2.nextElement();
                types.addElement(type);
                }
            }

        settings.put("Period",m_period.getSettings());
        settings.put("Types",types);
        settings.put("ExtraData",m_extraData);

        return settings;
        }

    public void initializeNetwork()
        {

   // We do this first so then any components that might disable an edge can
   // do so without fear of the edge going back later and renabling itself.
        Enumeration enm = getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge edge = (CCEdge)enm.nextElement();
            edge.setActive(true);
            }
        enm = getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CCNode node = (CCNode)enm.nextElement();
            node.initializeNetwork();
            }
        enm = getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge edge = (CCEdge)enm.nextElement();
            edge.initializeNetwork();
            }
        }

 // Why do we have removeEdge and removeNode in here?
    public void removeEdge(CCEdge ee)
        {
        m_edgeList.removeElement(ee);
        }
    public void removeNode(CCNode en)
        {
        m_nodeList.remove(new Integer(en.getID()));
        }

    public Hashtable retrieveData(WLGeneralServerConnection wlgsc, ExptMessage em, BaseDataInfo bdi)
        {
        Hashtable networkData = new Hashtable();

        CCPeriod ccp = m_period;
        CCEdge cce = (CCEdge)m_edgeList.elementAt(0);
        CCNode ccn = (CCNode)m_nodeList.get(new Integer(cce.getNode1()));

        Hashtable periodData = ccp.retrieveData(wlgsc,em,bdi);
        Hashtable nodeData = ccn.retrieveData(wlgsc,em,bdi);
        Hashtable edgeData = cce.retrieveData(wlgsc,em,bdi);

        networkData.put("Period Data",periodData);
        networkData.put("Node Data",nodeData);
        networkData.put("Edge Data",edgeData);

        return networkData;
        }

    public void setEdgeAnchorPoints()
        {
        Enumeration e = m_edgeList.elements();
        while (e.hasMoreElements())
            {
            CCEdge cce = (CCEdge)e.nextElement();
   
            CCNode ccn = (CCNode)m_nodeList.get(new Integer(cce.getNode1()));
            cce.setN1Anchor(ccn.getLoc().x+6,ccn.getLoc().y+6);

            ccn = (CCNode)m_nodeList.get(new Integer(cce.getNode2()));
            cce.setN2Anchor(ccn.getLoc().x+6,ccn.getLoc().y+6);
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
    public void setPeriod(CCPeriod np)
        {
        m_period = np;
        }
    }