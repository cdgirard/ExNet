package girard.sc.be.obj;

import girard.sc.exnet.obj.Edge;
import girard.sc.exnet.obj.Network;
import girard.sc.exnet.obj.Node;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.wl.io.WLGeneralServerConnection;

import java.awt.Font;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class BENetwork extends Network
    {
    protected Vector    m_periodList = new Vector();  // Should not be added till the experiment is built.
    protected int       m_currentPeriod = -1;
    protected Hashtable m_extraData = new Hashtable(); // Used to store end user specific data.
                                                       // Stores the index value for the client stations, under the key "Index"
    public BENetwork()
        {
        super("BENetwork","none","none");
        }
    public BENetwork(Network net)
        {
        super("BENetwork","none","none");

        m_userID = net.getUserID(); 
        m_fileName = net.getFileName();
        m_desc = net.getDesc();

        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            Node n = (Node)enm.nextElement();
            addNode(new BENode((Node)n.clone()));
            }
        enm = net.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            Edge e = (Edge)enm.nextElement();
            addEdge(new BEEdge((Edge)e.clone()));
            }
        }
  
    public void addEdge(BEEdge obj)
        {
        m_edgeList.addElement(obj);
        }
    public void addNode(BENode en)
        {
        m_nodeList.put(new Integer(en.getID()),en);
        }
    public void addPeriod(BEPeriod np)
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
            data.put("Objects",objects);
            data.put("Network",this);
            BENode ben = new BENode();
            ben.applySettings(data);
            addNode(ben);
            }

        Vector edges = (Vector)h.get("Edges");

        enm = edges.elements();
        while (enm.hasMoreElements())
            {
            Hashtable data = (Hashtable)enm.nextElement();
            data.put("Objects",objects);
            data.put("Network",this);
            BEEdge bee = new BEEdge();
            bee.applySettings(data);
            addEdge(bee);
            }

        Vector periods = (Vector)h.get("Periods");

        enm = periods.elements();
        while (enm.hasMoreElements())
            {
            Hashtable data = (Hashtable)enm.nextElement();
            BEPeriod bep = new BEPeriod();
            bep.applySettings(data);
            m_periodList.addElement(bep);  // The correct order should have been preserved by getSettings().
            }

        m_extraData = (Hashtable)h.get("ExtraData");

    // Old BENetworks won't have these so use the settings that they used.
        if (!m_extraData.containsKey("ExchangeMethod"))
            setExtraData("ExchangeMethod",new String("Consecutive"));
        if (!m_extraData.containsKey("InitialWindow"))
            {
            Hashtable iw = new Hashtable();
            iw.put("Message","A New Experiment is About to Start.\nPress the READY button to continue.");
            iw.put("FontName","Monospaced");
            iw.put("FontType",new Integer(Font.PLAIN));
            iw.put("FontSize",new Integer(18));
            iw.put("Loc",new Point(0,0));
            iw.put("Continue","Client");
            setExtraData("InitialWindow",iw);
            }

        m_currentPeriod = ((Integer)h.get("CurrentPeriod")).intValue();

        h.put("Nodes",new Vector()); // So we don't add more nodes when apply the Network applySettings method.
        h.put("Edges",new Vector()); // So we don't add more edges when apply the Network applySettings method.
        super.applySettings(h);
        h.put("Nodes",nodes); // Still might need for later.
        h.put("Edges",edges); // Still might need for later.
        }

    public Object clone()
        {
        BENetwork n = new BENetwork();

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
            BENode tmp = (BENode)((BENode)enm.nextElement()).clone();
            tmp.setComponentNetwork(n);
            n.addNode(tmp);
            }
        enm = m_edgeList.elements();
        while (enm.hasMoreElements())
            {
            BEEdge tmp = (BEEdge)((BEEdge)enm.nextElement()).clone();
            tmp.setComponentNetwork(n);
            n.addEdge(tmp);
            }
        enm = m_periodList.elements();
        while (enm.hasMoreElements())
            {
            BEPeriod tmp = (BEPeriod)((BEPeriod)enm.nextElement()).clone();
            tmp.setNetwork(n);
            n.addPeriod(tmp);
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

        if (m_extraData.containsKey("ExchangeMethod"))
            {
            String em = (String)getExtraData("ExchangeMethod");
            n.setExtraData("ExchangeMethod",new String(em)); // Can be Consecutive or Simultaneous
            }
        if (m_extraData.containsKey("InitialWindow"))
            {
            Hashtable h = (Hashtable)getExtraData("InitialWindow");
            Hashtable hNew = new Hashtable();
            hNew.put("Message",new String((String)h.get("Message")));
            hNew.put("FontName",new String((String)h.get("FontName")));
            hNew.put("FontSize",h.get("FontSize"));
            hNew.put("FontType",h.get("FontType"));
            hNew.put("Loc",new Point((Point)h.get("Loc")));
            hNew.put("Continue",new String((String)h.get("Continue")));
            n.setExtraData("InitialWindow",hNew);
            }

        return n;
        }

    public BEPeriod getActivePeriod()
        {
        if (m_currentPeriod > -1)
            return (BEPeriod)m_periodList.elementAt(m_currentPeriod);
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
        if (m_extraData.containsKey(str))
            return m_extraData.get(str);
        return null;
        }
    public BEStateAction getNextState()
        {
        BEStateAction besa = null;
        BEPeriod bep = getActivePeriod();
        BEEdge bee = (BEEdge)m_edgeList.elementAt(0);
        BENode ben = (BENode)m_nodeList.get(new Integer(bee.getNode1()));

        double ps1 = bep.getNextValidState(this);
        double ps2 = ben.getNextValidState(this);
        double ps3 = bee.getNextValidState(this);
        
        if ((ps1 < ps2) && (ps1 < ps3))
            {
            besa =  bep.getNextState(this);
            setExtraData("CurrentState",new Double(ps1));
            }
        if ((ps2 < ps1) && (ps2 < ps3))
            {
            besa = ben.getNextState(this);
            setExtraData("CurrentState",new Double(ps2));
            }
        if ((ps3 < ps1) && (ps3 < ps2))
            {
            besa = bee.getNextState(this);
            setExtraData("CurrentState",new Double(ps3));
            }

        return besa;
        }
    public int getNumPeriods()
        {
        return m_periodList.size();
        }
    public BEPeriod getPeriod(int value)
        {
        return (BEPeriod)m_periodList.elementAt(value);
        }
    public Hashtable getSettings()
        {
        Hashtable settings = super.getSettings();

        Vector types = new Vector();

    // Get the type info for the nodes.
        Enumeration enm = m_nodeList.elements();
        while(enm.hasMoreElements())
            {
            BENode ben = (BENode)enm.nextElement();
            Hashtable data = ben.getSettings();
            Vector nodeTypes = (Vector)data.get("Types");
            Enumeration enum2 = nodeTypes.elements();
            while (enum2.hasMoreElements())
                {
                String type = (String)enum2.nextElement();
                types.addElement(type);
                }
            }

   // Get the type info for the edges.
        enm = m_edgeList.elements();
        while(enm.hasMoreElements())
            {
            BEEdge bee = (BEEdge)enm.nextElement();
            Hashtable data = bee.getSettings();
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
            BEPeriod bep = (BEPeriod)enm.nextElement();
            periods.addElement(bep.getSettings());
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
            BEEdge edge = (BEEdge)enm.nextElement();
            edge.setActive(true);
            }
        enm = getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode node = (BENode)enm.nextElement();
            node.initializeNetwork();
            }
        enm = getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            edge.initializeNetwork();
            }
        }

    public void removeEdge(BEEdge ee)
        {
        m_edgeList.removeElement(ee);
        }
    public void removeNode(BENode en)
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
            BEPeriod np = (BEPeriod)e.nextElement();
            np.setPeriod(i);
            i++;
            }
        }

    public Hashtable retrieveData(WLGeneralServerConnection wlgsc, ExptMessage em, BaseDataInfo bdi)
        {
        Hashtable networkData = new Hashtable();

        BEPeriod bep = (BEPeriod)m_periodList.elementAt(0);
        BEEdge bee = (BEEdge)m_edgeList.elementAt(0);
        BENode ben = (BENode)m_nodeList.get(new Integer(bee.getNode1()));

        Hashtable periodData = bep.retrieveData(wlgsc,em,bdi);
        Hashtable nodeData = ben.retrieveData(wlgsc,em,bdi);
        Hashtable edgeData = bee.retrieveData(wlgsc,em,bdi);

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
            BEEdge ebe = (BEEdge)e.nextElement();
   
            BENode ebn = (BENode)m_nodeList.get(new Integer(ebe.getNode1()));
            ebe.setN1Anchor(ebn.getLoc().x+6,ebn.getLoc().y+6);

            ebn = (BENode)m_nodeList.get(new Integer(ebe.getNode2()));
            ebe.setN2Anchor(ebn.getLoc().x+6,ebn.getLoc().y+6);
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