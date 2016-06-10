package girard.sc.be.awt;

import girard.sc.awt.BorderPanel;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.awt.SortedFixedList;
import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeExchange;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.be.obj.BEOrSubnetwork;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Used to define ordered subnetworks for each node.
 * <p>
 * <br> Started: 09-17-2002
 * <p>
 * @author Dudley Girard
 */


public class BESetOrdSubNetsWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    BEFormatNetworkActionWindow m_FNAWApp;
    BENetwork m_Network;
    Hashtable m_Subnetworks = new Hashtable();

    CheckboxGroup m_infoWindowGrp = new CheckboxGroup();

    SortedFixedList m_NodeList;
    int m_NodeListIndex = -1;
    BENodeOrSubNet m_NodeSNIndex = null;

    NumberTextField m_MinField, m_MaxField;
    FixedLabel m_MinLabel, m_MaxLabel;
    
    FixedList m_SubnetworkList;
    int m_SubnetworkListIndex = -1;
    BEOrSubnetwork m_ActiveSubnetwork = null;
    Button m_RegroupSubNetButton, m_RegroupOrdButton, m_BackButton, m_ExpandButton;

    Vector m_subnetworkTree = new Vector();

    SortedFixedList m_AvailableList;
    boolean m_Regrouping = false;  // Are we in the middle of regrouping the subnetworks?
    boolean m_Reordering = false;  // Are we in the middle of reordering the subnetworks?
    Button m_GroupButton;
    
    FixedList m_OrderingList;

    Button m_OkButton, m_CancelButton, m_HelpButton;

    BENodeExchange m_ExchangeIndex = null;
    int m_rangeMax = 1;
    int[] m_tmpMin, m_tmpMax; 

    public BESetOrdSubNetsWindow(ExptOverlord app1, BEFormatNetworkActionWindow app2, BENetwork app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_FNAWApp = app2; /* Need so can unset edit mode */
        m_Network = app3; /* Makes referencing easier */

        initializeLabels();
        initializeSubnetworks();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("besosnw_title"));
        setFont(m_EOApp.getSmWinFont());

    // Start setup for the North Panel

        GridBagPanel NorthPanel = new GridBagPanel();

        BENodeOrSubNet ns = null;
        Enumeration enm = m_Subnetworks.elements();
        if (enm.hasMoreElements())
            {
            ns = (BENodeOrSubNet)enm.nextElement();
            }

        NorthPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besosnw_iw")),1,1,4,1,GridBagConstraints.CENTER);
        if (ns.getShowInfoWindow())
            {
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("besosnw_yes"),m_infoWindowGrp,true),1,2,2,1,GridBagConstraints.CENTER);
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("besosnw_no"),m_infoWindowGrp,false),3,2,2,1,GridBagConstraints.CENTER);
            }
        else
            {
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("besosnw_yes"),m_infoWindowGrp,false),1,2,2,1,GridBagConstraints.CENTER);
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("besosnw_no"),m_infoWindowGrp,true),3,2,2,1,GridBagConstraints.CENTER);
            }

    // End setup for the North Panel

    // Start setup for the West Panel

        GridBagPanel WestPanel = new GridBagPanel();

        WestPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besosnw_mnl")),1,1,4,1,GridBagConstraints.CENTER);
        WestPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besosnw_ninl")),1,2,4,1,GridBagConstraints.CENTER);

        int[] tmpIntArray = {7,10};
        m_NodeList = new SortedFixedList(6,false,2,tmpIntArray,SortedFixedList.CENTER);

        enm = m_Network.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode Ntemp = (BENode)enm.nextElement();
            m_NodeList.addItem(BuildNodeListEntry(Ntemp));
            }

        m_NodeList.addItemListener(this);
        WestPanel.constrain(m_NodeList,1,3,4,6,GridBagConstraints.CENTER); 

        WestPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besosnw_maxv")),5,3,2,1);
        m_MaxLabel = new FixedLabel(4);
        WestPanel.constrain(m_MaxLabel,7,3,2,1);

        WestPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besosnw_minv")),5,4,2,1);
        m_MinLabel = new FixedLabel(4);
        WestPanel.constrain(m_MinLabel,7,4,2,1);

        WestPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besosnw_maxe")),5,5,2,1);
        m_MaxField = new NumberTextField(4);
        m_MaxField.addActionListener(this);
        WestPanel.constrain(m_MaxField,7,5,2,1);

        WestPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besosnw_mine")),5,6,2,1);
        m_MinField = new NumberTextField(4);
        m_MinField.addActionListener(this);
        WestPanel.constrain(m_MinField,7,6,2,1);
    // End Setup for West panel.

    // Start Setup for East Panel.
        GridBagPanel EastPanel = new GridBagPanel();

        EastPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besosnw_an")),1,1,4,1);
        EastPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besosnw_ni")),1,2,4,1);
        m_AvailableList = new SortedFixedList(6,true,1,10,FixedList.CENTER);
        EastPanel.constrain(m_AvailableList,1,3,4,6);

        m_GroupButton = new Button(m_EOApp.getLabels().getObjectLabel("besosnw_group"));
        m_GroupButton.addActionListener(this);
        EastPanel.constrain(m_GroupButton,1,9,4,1,GridBagConstraints.CENTER);
    // End Setup for East Panel.

    // Start Setup for South Panel.
        Panel SouthPanel = new Panel(new BorderLayout());

      // Setup West South Panel
        GridBagPanel SouthWestPanel = new GridBagPanel();
    
        SouthWestPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besosnw_snl")),1,1,4,1);
        SouthWestPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besosnw_snn")),1,2,4,1);
        int[] tmpIntArray2 = {10,15};
        m_SubnetworkList = new FixedList(6,false,2,tmpIntArray2,FixedList.CENTER);
        m_SubnetworkList.addItemListener(this);
        SouthWestPanel.constrain(m_SubnetworkList,1,3,4,6,GridBagConstraints.CENTER);

        m_RegroupSubNetButton = new Button(m_EOApp.getLabels().getObjectLabel("besosnw_regroup"));
        m_RegroupSubNetButton.addActionListener(this);
        SouthWestPanel.constrain(m_RegroupSubNetButton,1,9,4,1,GridBagConstraints.CENTER);

        m_BackButton = new Button(m_EOApp.getLabels().getObjectLabel("besosnw_back"));
        m_BackButton.addActionListener(this);
        SouthWestPanel.constrain(m_BackButton,1,10,2,1,GridBagConstraints.CENTER);

        m_ExpandButton = new Button(m_EOApp.getLabels().getObjectLabel("besosnw_expand"));
        m_ExpandButton.addActionListener(this);
        SouthWestPanel.constrain(m_ExpandButton,3,10,2,1,GridBagConstraints.CENTER);
      // End Setup for West South Panel.

      // Setup East South Panel
        GridBagPanel SouthEastPanel = new GridBagPanel();
    
        SouthEastPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besosnw_ol")),1,1,4,1);
        SouthEastPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besosnw_on")),1,2,4,1);
        int[] tmpIntArray3 = {10,15};
        m_OrderingList = new FixedList(6,false,2,tmpIntArray3,FixedList.CENTER);
        m_OrderingList.addItemListener(this);
        SouthEastPanel.constrain(m_OrderingList,1,3,4,6,GridBagConstraints.CENTER);

        m_RegroupOrdButton = new Button(m_EOApp.getLabels().getObjectLabel("besosnw_reorder"));
        m_RegroupOrdButton.addActionListener(this);
        SouthEastPanel.constrain(m_RegroupOrdButton,1,9,4,1,GridBagConstraints.CENTER);

        SouthEastPanel.constrain(new Label(""),1,10,4,1,GridBagConstraints.CENTER);
      // End Setup for West South Panel.

      // Setup Ok and Cancel Buttons aka South South Panel
        Panel SouthSouthPanel = new Panel(new GridLayout(1,3));

        m_OkButton = new Button(m_EOApp.getLabels().getObjectLabel("besosnw_ok"));
        m_OkButton.addActionListener(this);
        SouthSouthPanel.add(m_OkButton);
        
        m_CancelButton = new Button(m_EOApp.getLabels().getObjectLabel("besosnw_cancel"));
        m_CancelButton.addActionListener(this);
        SouthSouthPanel.add(m_CancelButton);

        m_HelpButton = new Button(m_EOApp.getLabels().getObjectLabel("besosnw_help"));
        m_HelpButton.addActionListener(this);
        SouthSouthPanel.add(m_HelpButton);
      // End Setup for the South South Panel.

        SouthPanel.add("West",new BorderPanel(SouthWestPanel,BorderPanel.ETCHED));
        SouthPanel.add("Center",new BorderPanel(SouthEastPanel,BorderPanel.ETCHED));
        SouthPanel.add("South",SouthSouthPanel);
    // End Setup for the South Panel

        add("North",new BorderPanel(NorthPanel,BorderPanel.ETCHED));
        add("West",new BorderPanel(WestPanel,BorderPanel.ETCHED));
        add("East",new BorderPanel(EastPanel,BorderPanel.ETCHED)); 
        add("South",SouthPanel);
        pack();
        show();
        }

    
    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if ((theSource == m_BackButton) && (!m_Regrouping) && (!m_Reordering) && (m_NodeListIndex != -1))
                {
                if (m_ActiveSubnetwork == null)
                    {
                    // We do nothing, can't back up the subnetwork tree any farther than this.
                    }
                else
                    {
                    Object obj = m_subnetworkTree.elementAt(m_subnetworkTree.size()-1);
                    if (obj instanceof BENodeOrSubNet)
                        {
                        m_subnetworkTree.removeElementAt(m_subnetworkTree.size()-1);
                        m_ActiveSubnetwork = null;
                        updateSubnetworkList(m_NodeSNIndex.getSubnetworks());
                        updateOrderingList(m_NodeSNIndex.getOrdering());
                        m_MinLabel.setText(""+m_NodeSNIndex.getMin());
                        m_MaxLabel.setText(""+m_NodeSNIndex.getMax());
                        }
                    else  // Better be a BEOrSubnetwork class object
                        {
                        m_subnetworkTree.removeElementAt(m_subnetworkTree.size()-1);
                        m_ActiveSubnetwork = (BEOrSubnetwork)obj;
                        updateSubnetworkList(m_ActiveSubnetwork.getSubnetworks());
                        updateOrderingList(m_ActiveSubnetwork.getOrdering());
                        m_MinLabel.setText(""+m_ActiveSubnetwork.getMin());
                        m_MaxLabel.setText(""+m_ActiveSubnetwork.getMax());
                        }
                    }
                }

            if ((theSource == m_ExpandButton) && (!m_Regrouping) && (!m_Reordering) && (m_SubnetworkListIndex != -1))
                {
                if (m_ActiveSubnetwork == null)
                    {
                    BEOrSubnetwork sn = m_NodeSNIndex.getSubnetwork(m_SubnetworkListIndex);
                    if (sn.getNodes().length > 1) // Need at least 2 nodes to create a new valid subnetwork.
                        {
                        m_subnetworkTree.addElement(m_NodeSNIndex);
                        if (sn.getSubnetworks() == null)
                            sn.initializeSubnetworks();
                        m_ActiveSubnetwork = sn;
                        updateSubnetworkList(sn.getSubnetworks());
                        updateOrderingList(sn.getOrdering());
                        m_MinLabel.setText(""+m_ActiveSubnetwork.getMin());
                        m_MaxLabel.setText(""+m_ActiveSubnetwork.getMax());
                        }
                    }
                else
                    {
                    BEOrSubnetwork sn = m_ActiveSubnetwork.getSubnetwork(m_SubnetworkListIndex);
                    if (sn.getNodes().length > 1) // Need at least 2 nodes to create a new valid subnetwork.
                        {
                        BEOrSubnetwork sn2 = m_ActiveSubnetwork;
                        m_subnetworkTree.addElement(sn2);
                        if (sn.getSubnetworks() == null)
                            sn.initializeSubnetworks();
                        m_ActiveSubnetwork = sn;
                        updateSubnetworkList(sn.getSubnetworks());
                        updateOrderingList(sn.getOrdering());
                        m_MinLabel.setText(""+m_ActiveSubnetwork.getMin());
                        m_MaxLabel.setText(""+m_ActiveSubnetwork.getMax());
                        }
                    }
                }
            if ((theSource == m_GroupButton) && (m_Regrouping) && (m_AvailableList.getSelectedIndexes().length > 0))
                {
              // Need to make sure we haven't selected all the nodes to be in one large subnetwork.
                if (m_ActiveSubnetwork == null)
                    {
                    if ((m_NodeSNIndex.getSubnetworks().size() == 0) && (m_AvailableList.getSelectedIndexes().length == m_AvailableList.getItemCount()))
                        return;
                    }
                else
                    {
                    if ((m_ActiveSubnetwork.getSubnetworks() == null) && (m_AvailableList.getSelectedIndexes().length == m_AvailableList.getItemCount()))
                        return;
                    }

                int[] indexes = m_AvailableList.getSelectedIndexes();
                int[] nodes = new int[indexes.length];
                for (int x=0;x<indexes.length;x++)
                    {
                    String node = m_AvailableList.getSubItem(indexes[x],0);
                    nodes[x] = Integer.valueOf(node).intValue();
                    }
                if (m_ActiveSubnetwork == null)
                    {
                    m_NodeSNIndex.addSubnetwork(nodes);
                    updateSubnetworkList(m_NodeSNIndex.getSubnetworks());
                    updateOrderingList(m_NodeSNIndex.getOrdering());
                    }
                else
                    {
                    m_ActiveSubnetwork.addSubnetwork(nodes);
                    updateSubnetworkList(m_ActiveSubnetwork.getSubnetworks());
                    updateOrderingList(m_ActiveSubnetwork.getOrdering());
                    }
                for (int x=indexes.length-1;x>-1;x--)
                    {
                    m_AvailableList.remove(indexes[x]);
                    }
                if (m_AvailableList.getItemCount() == 0)
                    {
                    m_Regrouping = false;
                    }
                }
            if ((theSource == m_GroupButton) && (m_Reordering) && (m_AvailableList.getSelectedIndexes().length > 0))
                {
                int[] indexes = m_AvailableList.getSelectedIndexes();
                Vector v = new Vector();
                if (indexes.length == m_AvailableList.getItemCount())
                    {
                    v.addElement(new Integer(-1));
                    }
                else
                    {
                    for (int x=0;x<indexes.length;x++)
                        {
                        String node = m_AvailableList.getSubItem(indexes[x],0);
                        v.addElement(Integer.valueOf(node));
                        }
                    }
                if (m_ActiveSubnetwork == null)
                    {
                    m_NodeSNIndex.addOrdering(v);
                    updateOrderingList(m_NodeSNIndex.getOrdering());
                    if (m_NodeSNIndex.getSubnetworks().size() == m_NodeSNIndex.getOrdering().size())
                        {
                        m_Reordering = false;
                        m_AvailableList.removeAll();
                        }
                    }
                else
                    {
                    m_ActiveSubnetwork.addOrdering(v);
                    updateOrderingList(m_ActiveSubnetwork.getOrdering());
                    if (m_ActiveSubnetwork.getSubnetworks().size() == m_ActiveSubnetwork.getOrdering().size())
                        {
                        m_Reordering = false;
                        m_AvailableList.removeAll();
                        }
                    }
                }
            if (theSource == m_HelpButton)
                {
                m_EOApp.helpWindow("ehlp_besosnw");
                }
            if ((theSource == m_RegroupSubNetButton) && (!m_Regrouping) && (!m_Reordering) && (m_NodeListIndex != -1))
                {
                if (m_ActiveSubnetwork == null)
                    {
                    updateAvailableList(m_NodeSNIndex.getSubnetworks());
                    m_NodeSNIndex.cleanUp();
                    m_MinLabel.setText(""+m_NodeSNIndex.getMin());
                    m_MaxLabel.setText(""+m_NodeSNIndex.getMax());
                    }
                else
                    {
                    updateAvailableList(m_ActiveSubnetwork.getSubnetworks());
                    m_ActiveSubnetwork.cleanUp();
                    m_ActiveSubnetwork.setMax(1);
                    m_MinLabel.setText(""+m_ActiveSubnetwork.getMin());
                    m_MaxLabel.setText(""+m_ActiveSubnetwork.getMax());
                    }
                }
            if ((theSource == m_RegroupOrdButton) && (!m_Regrouping) && (!m_Reordering) && (m_NodeListIndex != -1))
                {
                if (m_ActiveSubnetwork == null)
                    {
                    updateAvailable2List(m_NodeSNIndex.getSubnetworks());
                    m_NodeSNIndex.getOrdering().removeAllElements();
                    }
                else
                    {
                    updateAvailable2List(m_ActiveSubnetwork.getSubnetworks());
                    m_ActiveSubnetwork.getOrdering().removeAllElements();
                    }
                }
 
            if ((theSource == m_OkButton) && (!m_Regrouping) && (!m_Reordering))
                {
                // Handle OK
                Enumeration enm = m_Subnetworks.keys();
                while (enm.hasMoreElements())
                    {
                    Integer node = (Integer)enm.nextElement();
                    BENodeOrSubNet bens = (BENodeOrSubNet)m_Subnetworks.get(node);
                    if (m_infoWindowGrp.getSelectedCheckbox().getLabel().equals(m_EOApp.getLabels().getObjectLabel("besosnw_yes")))
                        {
                        bens.setShowInfoWindow(true);
                        }
                    else
                        {
                        bens.setShowInfoWindow(false);
                        }
                    BENode ben = (BENode)m_Network.getNodeList().get(node);
                    BENodeExchange bene = (BENodeExchange)ben.getExptData("BENodeExchange");
                    if (bene instanceof BENodeOrSubNet)
                        {
                        BENodeOrSubNet bens2 = (BENodeOrSubNet)ben.getExptData("BENodeExchange");
                        ben.removeExptData("BENodeExchange");
                        bens2.cleanUp();
                        ben.addExptData(bens);
                        }
                    else
                        {
                        ben.removeExptData("BENodeExchange");
                        ben.addExptData(bens);
                        }
                    }
                m_Subnetworks.clear();
                m_FNAWApp.setEditMode(false);
                removeLabels();
                dispose();
                }
            if (theSource == m_CancelButton)
                {
                // Handle Cancel
                Enumeration enm = m_Subnetworks.elements();
                while (enm.hasMoreElements())
                    {
                    BENodeOrSubNet bens = (BENodeOrSubNet)enm.nextElement();
                    bens.cleanUp();
                    }
                m_Subnetworks.clear();
                m_FNAWApp.setEditMode(false);
                removeLabels();
                dispose();
                }
            }

        if (e.getSource() instanceof NumberTextField)
            {
            NumberTextField theSource = (NumberTextField)e.getSource();

            if ((theSource == m_MinField) && (m_MinField.getText().length() > 0))
                {
                int min = Integer.valueOf(m_MinField.getText()).intValue();
                if (m_ActiveSubnetwork == null)
                    {
                    if ((min <= m_NodeSNIndex.getMax()) && (min > 0))
                        {
                        m_NodeSNIndex.setMin(min);
                        m_MinLabel.setText(""+min);
                        }
                    }
                else
                    {
                    if ((min <= m_ActiveSubnetwork.getMax()) && (min > 0))
                        {
                        m_ActiveSubnetwork.setMin(min);
                        m_MinLabel.setText(""+min);
                        }
                    }
                }
            if ((theSource == m_MaxField) && (m_MaxField.getText().length() > 0))
                {
                int max = Integer.valueOf(m_MaxField.getText()).intValue();
                if (m_ActiveSubnetwork == null)
                    {
                    if ((max <= m_NodeSNIndex.getSubnetworks().size()) && (max >= m_NodeSNIndex.getMin()))
                        {
                        m_NodeSNIndex.setMax(max);
                        m_MaxLabel.setText(""+max);
                        }
                    }
                else
                    {
                    if ((max <= m_ActiveSubnetwork.getSubnetworks().size()) && (max >= m_ActiveSubnetwork.getMin()))
                        {
                        m_ActiveSubnetwork.setMax(max);
                        m_MaxLabel.setText(""+max);
                        }
                    }
                }
            }
        }
   
    public String[] BuildAvailableListEntry(int node)
        {
        String[] str = new String[1];

        str[0] = new String(""+node);

        return str;
        }
    public String[] BuildNodeListEntry(BENode Ntemp)
        {
        String[] str = new String[2];

        str[0] = new String(""+Ntemp.getID());
        str[1] = Ntemp.getLabel();

        return str;
        }
    public String[] BuildOrderingListEntry(int loc, Vector v)
        {
        StringBuffer str = new StringBuffer("");
        Integer ord = (Integer)v.elementAt(0);
        if (ord.intValue() == -1)
            {
            str.append("Any");
            }
        else
            {
            str.append("[");
            for (int x=0;x<v.size()-1;x++)
                {
                ord = (Integer)v.elementAt(x);
                str.append(""+ord+", ");
                }
            ord = (Integer)v.elementAt(v.size()-1);
            str.append(""+ord+"]");
            }

        String[] entry = new String[2];
        entry[0] = new String(""+loc);
        entry[1] = str.toString();

        return entry;
        }
    public String[] BuildSubnetworkListEntry(BEOrSubnetwork sn)
        {
        int[] nodes = sn.getNodes();
        StringBuffer str = new StringBuffer("[");
        for (int x=0;x<nodes.length-1;x++)
            {
            str.append(""+nodes[x]+", ");
            }
        str.append(""+nodes[nodes.length-1]+"]");

        String[] entry = new String[2];
        entry[0] = new String(""+sn.getSubnetwork());
        entry[1] = str.toString();

        return entry;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/besosnw.txt");
        }
    private void initializeSubnetworks()
        {
        Enumeration enm = m_Network.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode node = (BENode)enm.nextElement();
            Object obj = node.getExptData("BENodeExchange");
            if (obj instanceof BENodeOrSubNet)
                {
                BENodeOrSubNet bens = (BENodeOrSubNet)obj;
                m_Subnetworks.put(new Integer(node.getID()),(BENodeOrSubNet)bens.clone());
                }
            else
                {
                Vector nodes = new Vector();
                Enumeration enum2 = m_Network.getEdgeList().elements();
                while (enum2.hasMoreElements())
                    {
                    BEEdge edge = (BEEdge)enum2.nextElement();
                    if (edge.getNode1() == node.getID())
                        {
                        nodes.addElement(new Integer(edge.getNode2()));
                        }
                    if (edge.getNode2() == node.getID())
                        {
                        nodes.addElement(new Integer(edge.getNode1()));
                        }
                    }
                BENodeOrSubNet bens = new BENodeOrSubNet(node,nodes);
                m_Subnetworks.put(new Integer(node.getID()),bens);
                }
            }
        }

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)e.getSource();

            if ((theSource == m_SubnetworkList) && (m_NodeListIndex > -1) && (!m_Regrouping))
                {
                m_SubnetworkListIndex = m_SubnetworkList.getSelectedIndex();
                }
            }
        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

            if ((theSource == m_NodeList) && (!m_Regrouping))
                {
                m_NodeListIndex = theSource.getSelectedIndex();
                if (m_NodeListIndex > -1)
                    {
                    Integer n = new Integer(theSource.getSelectedSubItem(0));
                    m_NodeSNIndex = (BENodeOrSubNet)m_Subnetworks.get(n);
                    m_MinLabel.setText(""+m_NodeSNIndex.getMin());
                    m_MaxLabel.setText(""+m_NodeSNIndex.getMax());
                    updateSubnetworkList(m_NodeSNIndex.getSubnetworks());
                    updateOrderingList(m_NodeSNIndex.getOrdering());
                    m_ActiveSubnetwork = null;
                    m_subnetworkTree.removeAllElements();
                    }
                }
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/besosnw.txt");
        }

    public void updateAvailableList(Hashtable subnetworks)
        {
        m_Regrouping = true;
        m_SubnetworkList.removeAll();
        m_SubnetworkListIndex = -1;
        m_OrderingList.removeAll();
        m_AvailableList.removeAll();
     
        Enumeration enm = subnetworks.keys();
        while (enm.hasMoreElements())
            {
            Object obj = enm.nextElement();
            if (obj instanceof Integer)
                {
                BEOrSubnetwork sn = (BEOrSubnetwork)subnetworks.get(obj);
                int[] nodes = sn.getNodes();
                for (int x=0;x<nodes.length;x++)
                    {
                    m_AvailableList.addItem(BuildAvailableListEntry(nodes[x]));
                    }
                }
            }
        }
    public void updateAvailable2List(Hashtable subnetworks)
        {
        m_Reordering = true;
        m_OrderingList.removeAll();
        m_AvailableList.removeAll();
     
        Enumeration enm = subnetworks.keys();
        while (enm.hasMoreElements())
            {
            Object obj = enm.nextElement();
            if (obj instanceof Integer)
                {
                BEOrSubnetwork sn = (BEOrSubnetwork)subnetworks.get(obj);
                int[] nodes = sn.getNodes();
                for (int x=0;x<nodes.length;x++)
                    {
                    m_AvailableList.addItem(BuildAvailableListEntry(nodes[x]));
                    }
                }
            }
        }
    public void updateOrderingList(Vector ordering)
        {
        m_OrderingList.removeAll();

        for (int x=0;x<ordering.size();x++)
            {
            Vector v = (Vector)ordering.elementAt(x);
                
            m_OrderingList.addItem(BuildOrderingListEntry(x,v));
            }
        }
    public void updateSubnetworkList(Hashtable subnetworks)
        {
        m_SubnetworkList.removeAll();
        m_SubnetworkListIndex = -1;

        for (int x=0;x<subnetworks.size();x++)
            {
            BEOrSubnetwork sn = (BEOrSubnetwork)subnetworks.get(new Integer(x));
                
            m_SubnetworkList.addItem(BuildSubnetworkListEntry(sn));
            }
        }
    }
