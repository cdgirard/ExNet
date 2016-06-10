package girard.sc.be.awt;

/* Equivalent to minmax.c of the Xmotif Exnet version, now used to
   define subnetworks for each node based on work being done by
   Blaine Dobey.

 Writtien by Dudley Girard
 Started 8-11-1998
 Editted 4-7-2001
*/

import girard.sc.awt.BorderPanel;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeExchange;
import girard.sc.be.obj.BENodeSubnetwork;
import girard.sc.be.obj.BESubnetwork;
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

public class BESetSubnetworksWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    BEFormatNetworkActionWindow m_FNAWApp;
    BENetwork m_Network;
    Hashtable m_Subnetworks = new Hashtable();

    CheckboxGroup m_infoWindowGrp = new CheckboxGroup();

    FixedList m_NodeList;
    int m_NodeListIndex = -1;
    BENodeSubnetwork m_NodeSNIndex = null;

    NumberTextField m_MinField, m_MaxField;
    FixedLabel m_MinLabel, m_MaxLabel;
    
    FixedList m_SubnetworkList;
    int m_SubnetworkListIndex = -1;
    BESubnetwork m_ActiveSubnetwork = null;
    Button m_RegroupButton, m_BackButton, m_ExpandButton;

    Vector m_subnetworkTree = new Vector();

    FixedList m_AvailableList;
    boolean m_Regrouping = false;  // Are we in the middle of regrouping the subnetworks?
    Button m_GroupButton;
    
    Button m_OkButton, m_CancelButton, m_HelpButton;

    BENodeExchange m_ExchangeIndex = null;
    int m_rangeMax = 1;
    int[] m_tmpMin, m_tmpMax; 

    public BESetSubnetworksWindow(ExptOverlord app1, BEFormatNetworkActionWindow app2, BENetwork app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_FNAWApp = app2; /* Need so can unset edit mode */
        m_Network = app3; /* Makes referencing easier */

        initializeLabels();
        initializeSubnetworks();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("bessw_title"));
        setFont(m_EOApp.getSmWinFont());

    // Start setup for the North Panel

        GridBagPanel NorthPanel = new GridBagPanel();

        BENodeSubnetwork ns = null;
        Enumeration enm = m_Subnetworks.elements();
        if (enm.hasMoreElements())
            {
            ns = (BENodeSubnetwork)enm.nextElement();
            }

        NorthPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bessw_iw")),1,1,4,1,GridBagConstraints.CENTER);
        if (ns.getShowInfoWindow())
            {
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("bessw_yes"),m_infoWindowGrp,true),1,2,2,1,GridBagConstraints.CENTER);
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("bessw_no"),m_infoWindowGrp,false),3,2,2,1,GridBagConstraints.CENTER);
            }
        else
            {
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("bessw_yes"),m_infoWindowGrp,false),1,2,2,1,GridBagConstraints.CENTER);
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("bessw_no"),m_infoWindowGrp,true),3,2,2,1,GridBagConstraints.CENTER);
            }

    // End setup for the North Panel

    // Start setup for the West Panel

        GridBagPanel WestPanel = new GridBagPanel();

        WestPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bessw_mnl")),1,1,4,1,GridBagConstraints.CENTER);
        WestPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bessw_ninl")),1,2,4,1,GridBagConstraints.CENTER);

        int[] tmpIntArray = {7,10};
        m_NodeList = new FixedList(8,false,2,tmpIntArray,FixedList.CENTER);

        enm = m_Network.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode Ntemp = (BENode)enm.nextElement();
            m_NodeList.addItem(BuildNodeListEntry(Ntemp));
            }

        m_NodeList.addItemListener(this);
        WestPanel.constrain(m_NodeList,1,3,4,6,GridBagConstraints.CENTER); 

        WestPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bessw_maxv")),5,3,2,1);
        m_MaxLabel = new FixedLabel(4);
        WestPanel.constrain(m_MaxLabel,7,3,2,1);

        WestPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bessw_minv")),5,4,2,1);
        m_MinLabel = new FixedLabel(4);
        WestPanel.constrain(m_MinLabel,7,4,2,1);

        WestPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bessw_maxe")),5,5,2,1);
        m_MaxField = new NumberTextField(4);
        m_MaxField.addActionListener(this);
        WestPanel.constrain(m_MaxField,7,5,2,1);

        WestPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bessw_mine")),5,6,2,1);
        m_MinField = new NumberTextField(4);
        m_MinField.addActionListener(this);
        WestPanel.constrain(m_MinField,7,6,2,1);
    // End Setup for West panel.

    // Start Setup for Center Panel.
        GridBagPanel CenterPanel = new GridBagPanel();
   
        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bessw_snl")),1,1,4,1);
        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bessw_snn")),1,2,4,1);
        int[] tmpIntArray2 = {10,15};
        m_SubnetworkList = new FixedList(6,false,2,tmpIntArray2,FixedList.CENTER);
        m_SubnetworkList.addItemListener(this);
        CenterPanel.constrain(m_SubnetworkList,1,3,4,6,GridBagConstraints.CENTER);

        m_RegroupButton = new Button(m_EOApp.getLabels().getObjectLabel("bessw_regroup"));
        m_RegroupButton.addActionListener(this);
        CenterPanel.constrain(m_RegroupButton,1,9,4,1,GridBagConstraints.CENTER);

        m_BackButton = new Button(m_EOApp.getLabels().getObjectLabel("bessw_back"));
        m_BackButton.addActionListener(this);
        CenterPanel.constrain(m_BackButton,1,10,2,1,GridBagConstraints.CENTER);

        m_ExpandButton = new Button(m_EOApp.getLabels().getObjectLabel("bessw_expand"));
        m_ExpandButton.addActionListener(this);
        CenterPanel.constrain(m_ExpandButton,3,10,2,1,GridBagConstraints.CENTER);
    // End Setup for Center Panel.

    // Start Setup for East Panel.
        GridBagPanel EastPanel = new GridBagPanel();

        EastPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bessw_an")),1,1,4,1);
        EastPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bessw_ni")),1,2,4,1);
        m_AvailableList = new FixedList(7,true,1,10,FixedList.CENTER);
        EastPanel.constrain(m_AvailableList,1,3,4,6);

        m_GroupButton = new Button(m_EOApp.getLabels().getObjectLabel("bessw_group"));
        m_GroupButton.addActionListener(this);
        EastPanel.constrain(m_GroupButton,1,9,4,1,GridBagConstraints.CENTER);

    // End Setup for East Panel.

    // Ok and Cancel Buttons aka South Panel

        Panel SouthPanel = new Panel(new GridLayout(1,3));

        m_OkButton = new Button(m_EOApp.getLabels().getObjectLabel("bessw_ok"));
        m_OkButton.addActionListener(this);
        SouthPanel.add(m_OkButton);
        
        m_CancelButton = new Button(m_EOApp.getLabels().getObjectLabel("bessw_cancel"));
        m_CancelButton.addActionListener(this);
        SouthPanel.add(m_CancelButton);

        m_HelpButton = new Button(m_EOApp.getLabels().getObjectLabel("bessw_help"));
        m_HelpButton.addActionListener(this);
        SouthPanel.add(m_HelpButton);
    // End Setup for the South Panel

        add("North",new BorderPanel(NorthPanel,BorderPanel.ETCHED));
        add("West",new BorderPanel(WestPanel,BorderPanel.ETCHED));
        add("Center",new BorderPanel(CenterPanel,BorderPanel.ETCHED));
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

            if ((theSource == m_BackButton) && (!m_Regrouping) && (m_NodeListIndex != -1))
                {
                if (m_ActiveSubnetwork == null)
                    {
                    // We do nothing, can't back up the subnetwork tree any farther than this.
                    }
                else
                    {
                    Object obj = m_subnetworkTree.elementAt(m_subnetworkTree.size()-1);
                    if (obj instanceof BENodeSubnetwork)
                        {
                        m_subnetworkTree.removeElementAt(m_subnetworkTree.size()-1);
                        m_ActiveSubnetwork = null;
                        updateSubnetworkList(m_NodeSNIndex.getSubnetworks());
                        m_MinLabel.setText(""+m_NodeSNIndex.getMin());
                        m_MaxLabel.setText(""+m_NodeSNIndex.getMax());
                        }
                    else  // Better be a BESubnetwork class object
                        {
                        m_subnetworkTree.removeElementAt(m_subnetworkTree.size()-1);
                        m_ActiveSubnetwork = (BESubnetwork)obj;
                        updateSubnetworkList(m_ActiveSubnetwork.getSubnetworks());
                        m_MinLabel.setText(""+m_ActiveSubnetwork.getMin());
                        m_MaxLabel.setText(""+m_ActiveSubnetwork.getMax());
                        }
                    }
                }

            if ((theSource == m_ExpandButton) && (!m_Regrouping) && (m_SubnetworkListIndex != -1))
                {
                if (m_ActiveSubnetwork == null)
                    {
                    BESubnetwork sn = m_NodeSNIndex.getSubnetwork(m_SubnetworkListIndex);
                    if (sn.getNodes().length > 1) // Need at least 2 nodes to create a new valid subnetwork.
                        {
                        m_subnetworkTree.addElement(m_NodeSNIndex);
                        if (sn.getSubnetworks() == null)
                            sn.initializeSubnetworks();
                        m_ActiveSubnetwork = sn;
                        updateSubnetworkList(sn.getSubnetworks());
                        m_MinLabel.setText(""+m_ActiveSubnetwork.getMin());
                        m_MaxLabel.setText(""+m_ActiveSubnetwork.getMax());
                        }
                    }
                else
                    {
                    BESubnetwork sn = m_ActiveSubnetwork.getSubnetwork(m_SubnetworkListIndex);
                    if (sn.getNodes().length > 1) // Need at least 2 nodes to create a new valid subnetwork.
                        {
                        BESubnetwork sn2 = m_ActiveSubnetwork;
                        m_subnetworkTree.addElement(sn2);
                        if (sn.getSubnetworks() == null)
                            sn.initializeSubnetworks();
                        m_ActiveSubnetwork = sn;
                        updateSubnetworkList(sn.getSubnetworks());
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
                    }
                else
                    {
                    m_ActiveSubnetwork.addSubnetwork(nodes);
                    updateSubnetworkList(m_ActiveSubnetwork.getSubnetworks());
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
            if (theSource == m_HelpButton)
                {
                m_EOApp.helpWindow("ehlp_bessw");
                }
            if ((theSource == m_RegroupButton) && (!m_Regrouping) && (m_NodeListIndex != -1))
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
 
            if ((theSource == m_OkButton) && (!m_Regrouping))
                {
                // Handle OK
                Enumeration enm = m_Subnetworks.keys();
                while (enm.hasMoreElements())
                    {
                    Integer node = (Integer)enm.nextElement();
                    BENodeSubnetwork bens = (BENodeSubnetwork)m_Subnetworks.get(node);
                    if (m_infoWindowGrp.getSelectedCheckbox().getLabel().equals(m_EOApp.getLabels().getObjectLabel("bessw_yes")))
                        {
                        bens.setShowInfoWindow(true);
                        }
                    else
                        {
                        bens.setShowInfoWindow(false);
                        }
                    BENode ben = (BENode)m_Network.getNodeList().get(node);
                    BENodeExchange bene = (BENodeExchange)ben.getExptData("BENodeExchange");
                    if (bene instanceof BENodeSubnetwork)
                        {
                        BENodeSubnetwork bens2 = (BENodeSubnetwork)ben.getExptData("BENodeExchange");
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
                    BENodeSubnetwork bens = (BENodeSubnetwork)enm.nextElement();
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
    public String[] BuildSubnetworkListEntry(BESubnetwork sn)
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
        m_EOApp.initializeLabels("girard/sc/be/awt/bessw.txt");
        }
    private void initializeSubnetworks()
        {
        Enumeration enm = m_Network.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode node = (BENode)enm.nextElement();
            Object obj = node.getExptData("BENodeExchange");
            if (obj instanceof BENodeSubnetwork)
                {
                BENodeSubnetwork bens = (BENodeSubnetwork)obj;
                m_Subnetworks.put(new Integer(node.getID()),(BENodeSubnetwork)bens.clone());
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
                BENodeSubnetwork bens = new BENodeSubnetwork(node,nodes);
                m_Subnetworks.put(new Integer(node.getID()),bens);
                }
            }
        }

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)e.getSource();

            if ((theSource == m_NodeList) && (!m_Regrouping))
                {
                m_NodeListIndex = theSource.getSelectedIndex();
                if (m_NodeListIndex > -1)
                    {
                    Integer n = new Integer(theSource.getSelectedSubItem(0));
                    m_NodeSNIndex = (BENodeSubnetwork)m_Subnetworks.get(n);
                    m_MinLabel.setText(""+m_NodeSNIndex.getMin());
                    m_MaxLabel.setText(""+m_NodeSNIndex.getMax());
                    updateSubnetworkList(m_NodeSNIndex.getSubnetworks());
                    m_ActiveSubnetwork = null;
                    m_subnetworkTree.removeAllElements();
                    }
                }

            if ((theSource == m_SubnetworkList) && (m_NodeListIndex > -1) && (!m_Regrouping))
                {
                m_SubnetworkListIndex = m_SubnetworkList.getSelectedIndex();
                }
            }
        }


    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/bessw.txt");
        }

    public void updateAvailableList(Hashtable subnetworks)
        {
        m_Regrouping = true;
        m_SubnetworkList.removeAll();
        m_SubnetworkListIndex = -1;
        m_AvailableList.removeAll();
     
        Enumeration enm = subnetworks.keys();
        while (enm.hasMoreElements())
            {
            Object obj = enm.nextElement();
            if (obj instanceof Integer)
                {
                BESubnetwork sn = (BESubnetwork)subnetworks.get(obj);
                int[] nodes = sn.getNodes();
                for (int x=0;x<nodes.length;x++)
                    {
                    m_AvailableList.addItem(BuildAvailableListEntry(nodes[x]));
                    }
                }
            }
        }
    public void updateSubnetworkList(Hashtable subnetworks)
        {
        m_SubnetworkList.removeAll();
        m_SubnetworkListIndex = -1;

        for (int x=0;x<subnetworks.size();x++)
            {
            BESubnetwork sn = (BESubnetwork)subnetworks.get(new Integer(x));
                
            m_SubnetworkList.addItem(BuildSubnetworkListEntry(sn));
            }
        }
    }
