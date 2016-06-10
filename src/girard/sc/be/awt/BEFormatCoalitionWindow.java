package girard.sc.be.awt;

import girard.sc.awt.FixedJList;
import girard.sc.awt.JGridBagPanel;
import girard.sc.be.obj.BECoalition;
import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class BEFormatCoalitionWindow extends JFrame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    BEFormatNetworkActionWindow m_EBWApp;
    BENetwork m_activeNetwork;

    JMenuBar m_mbar;
    JMenu m_File, m_Help;

    Vector m_availableColors = new Vector();

 // Assign Coalition Pane
    FixedJList m_nodeList;
    JLabel m_nodeLabel;
    JComboBox m_typeBox = new JComboBox();
    JComboBox m_coalitionBox = new JComboBox();
    JComboBox m_infoLevelBox = new JComboBox();
    FixedJList m_edgeList;

 // Static Coalition Pane
    Hashtable m_staticCoalitions = new Hashtable();
    int m_activeStaticCoal = 1;
    JComboBox m_staticCoalBox = new JComboBox();
    JComboBox m_staticColorBox = new JComboBox();
    JComboBox m_staticShareBox = new JComboBox();
    JComboBox m_staticCritMassBox = new JComboBox();
    JComboBox m_staticReportMethBox = new JComboBox();
    JComboBox m_staticZapBox = new JComboBox();
    BENumberJTextField m_staticZapCostField;
    BENumberJTextField m_staticZapAmtField;
    JComboBox m_staticVotesNeededBox = new JComboBox();
    JComboBox m_staticNumZapBox = new JComboBox();
    
 // Dynamic Coalition Pane
    Hashtable m_dynamicCoalitions = new Hashtable();
    int m_activeDynamicCoal = 1;
    JComboBox m_dynamicCoalBox = new JComboBox();
    JComboBox m_dynamicShareBox = new JComboBox();
    JComboBox m_dynamicZapBox = new JComboBox();
    BENumberJTextField m_dynamicZapCostField;
    BENumberJTextField m_dynamicZapAmtField;
    JComboBox m_dynamicVotesNeededBox = new JComboBox();
    JComboBox m_dynamicNumZapBox = new JComboBox();

    boolean m_updateFlag = false;

    public BEFormatCoalitionWindow(ExptOverlord app1, BEFormatNetworkActionWindow app2, BENetwork app3)
        {
        m_EOApp = app1; /* Need to make pretty buttons. */
        m_EBWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */

        initializeLabels();

        BECoalition cTmp = new BECoalition();

        m_availableColors.addElement(cTmp.getDisplayColor(0));
        m_availableColors.addElement(cTmp.getDisplayColor(1));
        m_availableColors.addElement(cTmp.getDisplayColor(2));
        m_availableColors.addElement(cTmp.getDisplayColor(3));
        m_availableColors.addElement(cTmp.getDisplayColor(4));
        m_availableColors.addElement(cTmp.getDisplayColor(5));
        m_availableColors.addElement(cTmp.getDisplayColor(6));
        m_availableColors.addElement(cTmp.getDisplayColor(7));

        initializeCoalitions();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Color.lightGray);
        setTitle(m_EOApp.getLabels().getObjectLabel("befcw_title"));
        // setSize(250,250);

    // Start Setup for Menubar
        m_mbar = new JMenuBar();

        setJMenuBar(m_mbar);
    
        JMenuItem tmpMI;
     // File Menu
        m_File = new JMenu("File");

        tmpMI = new JMenuItem("Done");
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

     // Help Menu
        m_Help = new JMenu("Help");

        tmpMI = new JMenuItem("Help");
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
  // End Setup of Menus


    // Setup the Central Panel
        JTabbedPane centralPane = new JTabbedPane();

        centralPane.addTab("Assign",createAssignCoalitionPanel());
        centralPane.addTab("Static",createStaticCoalitionPanel());
        centralPane.addTab("Dynamic",createDynamicCoalitionPanel());

   // End Setup Centeral Panel

        getContentPane().add("Center",centralPane);
        
        pack();
        show();
        }

    public void actionPerformed (ActionEvent e)
        {
// System.err.println(""+e.getSource());
        if (e.getSource() instanceof JButton)
            {
            JButton theSource = (JButton)e.getSource();
            }
        if (e.getSource() instanceof JComboBox)
            {
            JComboBox theSource = (JComboBox)e.getSource();

            if (m_updateFlag)
                return;

    // Dynamic GUI Boxes.        

            if ((theSource == m_dynamicCoalBox) && (m_dynamicCoalBox.getSelectedIndex() > -1))
                {
                Integer coal = Integer.valueOf((String)m_dynamicCoalBox.getSelectedItem());
                updateDynamicPane(coal);
                return;
                }
            if (theSource == m_dynamicShareBox)
                {
                Integer coal = Integer.valueOf((String)m_dynamicCoalBox.getSelectedItem());
                updateDynamicCoal(coal);
                return;
                }
           if ((theSource == m_dynamicZapBox) || (theSource == m_dynamicVotesNeededBox) || (theSource == m_dynamicNumZapBox))
                {
                Integer coal = Integer.valueOf((String)m_dynamicCoalBox.getSelectedItem());
                updateDynamicCoal(coal);
                return;
                }

    // Main Panel GUI Boxes
            if ((theSource == m_coalitionBox) && (m_nodeList.getSelectedIndex() > -1))
                {
                updateActiveNodeCoal();
                }
            if ((theSource == m_infoLevelBox) && (m_nodeList.getSelectedIndex() > -1))
                {
                updateActiveNodeCoal();
                }
            if ((theSource == m_typeBox) && (m_nodeList.getSelectedIndex() > -1))
                {
                updateActiveNodeCoal();
                }

    // Static GUI Boxes

            if (theSource == m_staticCoalBox)
                {
                Integer coal = Integer.valueOf((String)m_staticCoalBox.getSelectedItem());
                updateStaticPane(coal);
                return;
                }
            if (theSource == m_staticColorBox)
                {
                Integer coal = Integer.valueOf((String)m_staticCoalBox.getSelectedItem());
                updateStaticCoal(coal);
                return;
                }
            if ((theSource == m_staticShareBox) || (theSource == m_staticCritMassBox) || (theSource == m_staticReportMethBox))
                {
                Integer coal = Integer.valueOf((String)m_staticCoalBox.getSelectedItem());
		//		System.err.println("selected the value:"+coal.intValue());
                updateStaticCoal(coal);
                return;
                }
            if ((theSource == m_staticZapBox) || (theSource == m_staticVotesNeededBox) || (theSource == m_staticNumZapBox))
                {
                Integer coal = Integer.valueOf((String)m_staticCoalBox.getSelectedItem());
                updateStaticCoal(coal);
                return;
                }
            }
        if (e.getSource() instanceof JMenuItem)
            {
            JMenuItem theSource = (JMenuItem)e.getSource();
       
            if (theSource.getText().equals("Done"))
                {
                removeLabels();
                dispose();
                m_EBWApp.setEditMode(false);
                return;
                }
            if (theSource.getText().equals("Help"))
                {
                // Handle Help.
                m_EOApp.helpWindow("ehlp_befcw");
                }
            }
        if (e.getSource() instanceof BENumberJTextField)
            {
            BENumberJTextField theSource = (BENumberJTextField)e.getSource();
 
            if (m_updateFlag)
                return;

            if ((theSource == m_dynamicZapCostField) || (theSource == m_dynamicZapAmtField))
                {
                Integer coal = Integer.valueOf((String)m_dynamicCoalBox.getSelectedItem());
                updateDynamicCoal(coal);
                return;
                }
            if ((theSource == m_staticZapCostField) || (theSource == m_staticZapAmtField))
                {
                Integer coal = Integer.valueOf((String)m_staticCoalBox.getSelectedItem());
                updateStaticCoal(coal);
                return;
                }
            }
        }

    private String[] buildEdgeListEntry(BENode Ntemp, int id)
        {
        BENode resN = (BENode)m_activeNetwork.getNode(id);
        BENodeOrSubNet exch = (BENodeOrSubNet)Ntemp.getExptData("BENodeExchange");
        BECoalition bec = exch.getCoalition();

        boolean shareRes = bec.getShareList(id);
        String[] str = new String[3];

        str[0] = resN.getLabel();
        str[1] = new String(""+resN.getID());
        

        str[2] = new String(""+shareRes);

        return str;
        }
    private String[] buildNodeListEntry(BENode Ntemp)
        {
        String[] str = new String[5];

        str[0] = Ntemp.getLabel();
        str[1] = new String(""+Ntemp.getID());
        
        BENodeOrSubNet exch = (BENodeOrSubNet)Ntemp.getExptData("BENodeExchange");
        if (exch.getCoalition().getCoalitionType().equals("Static"))
            {
            str[2] = new String("S");
            }
        else if (exch.getCoalition().getCoalitionType().equals("Dynamic"))
            {
            str[2] = new String("D");
            }
        else
            {
            str[2] = new String("-");
            }

        str[3] = new String(""+exch.getCoalition().getCoalition());

        str[4] = new String(""+exch.getCoalition().getCoalitionInfoLevel());

        return str;
        }

/**
 * Copies key vales from the coal into the target.
 */
    private void copyBaseCoalitionSettings(BECoalition coal, BECoalition target)
        {
  // Coalition
        target.setCoalition(coal.getCoalition());
  // Type
        target.setCoalitionType(coal.getCoalitionType());
  // ID Color
        target.setIDColor(coal.getIDColor());
  // Sharing
        target.setSharing(coal.getSharing());
  // Number needed to join
        target.setNumNeededToJoin(coal.getNumNeededToJoin());
  // Report Method
        target.setReportMethod(coal.getReportMethod());
  // Zapping
        target.setZapping(coal.getZapping());
  // Zap Cost
        target.setZapCost(coal.getZapCost());
  // Zap Amount
        target.setZapAmount(coal.getZapAmount());
  // Num Needed per Zap
        target.setNumNeededToZap(coal.getNumNeededToZap());
  // Num Zap per Zap
        target.setNumZapped(coal.getNumZapped());
        }

    public JGridBagPanel createAssignCoalitionPanel()
        {
        JGridBagPanel assignPanel = new JGridBagPanel();
        JScrollPane tmpPane;

        assignPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_ntc")),1,1,4,1);
        int[] colWidths = { 3, 5, 5, 5, 5 };
        m_nodeList = new FixedJList(5,colWidths,FixedJList.CENTER);
        m_nodeList.setVisibleRowCount(5);
        m_nodeList.setSorted(true);
        m_nodeList.setSize(m_nodeList.getPreferredSize());
        m_nodeList.setBackground(Color.white);
        m_nodeList.addItemListener(this);
        fillNodeList();
        assignPanel.constrain(m_nodeList,1,2,4,10,GridBagConstraints.CENTER,GridBagConstraints.BOTH);

        assignPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_node")),5,1,2,1,GridBagConstraints.CENTER);
        m_nodeLabel = new JLabel("   ");
        assignPanel.constrain(m_nodeLabel,7,1,2,1);

        assignPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_type")),5,2,2,1,GridBagConstraints.CENTER);
        m_typeBox.addItem("None");
        m_typeBox.addItem("Static");
        // m_typeBox.addItem("Dynamic");
        m_typeBox.setSelectedIndex(0);
        m_typeBox.addActionListener(this);
        assignPanel.constrain(m_typeBox,7,2,2,1);

        assignPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_coalition")),5,3,2,1,GridBagConstraints.CENTER);
        for (int x=1;x<7;x++)
            {
            m_coalitionBox.addItem(""+x);
            }
        m_coalitionBox.addActionListener(this);
        assignPanel.constrain(m_coalitionBox,7,3,2,1);

        assignPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_il")),5,4,2,1,GridBagConstraints.CENTER);
        m_infoLevelBox.addItem("0");
        m_infoLevelBox.addItem("1");
        m_infoLevelBox.addItem("2");
        m_infoLevelBox.setSelectedIndex(0);
        m_infoLevelBox.addActionListener(this);
        assignPanel.constrain(m_infoLevelBox,7,4,2,1);

        assignPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_es")),5,5,4,1);
        int[] colWidths2 = { 3, 5, 5 };
        m_edgeList = new FixedJList(3,colWidths2,FixedJList.CENTER);
        m_edgeList.setVisibleRowCount(4);
        m_edgeList.setSorted(true);
        m_edgeList.setSize(m_edgeList.getPreferredSize());
        m_edgeList.setBackground(Color.white);
        m_edgeList.addItemListener(this);
        assignPanel.constrain(m_edgeList,5,6,4,4,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        
        return assignPanel;
        }
/**
 * Creates the layout and initializes the pane to edit the settings for the
 * various static coalitions.
 */
    public JGridBagPanel createStaticCoalitionPanel()
        {
        JGridBagPanel staticPanel = new JGridBagPanel();

        staticPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_coalition")),1,1,2,1,GridBagConstraints.CENTER);
        for (int x=1;x<7;x++)
            {
            m_staticCoalBox.addItem(""+x);
            }
        m_staticCoalBox.setSelectedIndex(0);
        m_staticCoalBox.addActionListener(this);
        staticPanel.constrain(m_staticCoalBox,3,1,2,1);

        BECoalition tmpCoal = (BECoalition)m_staticCoalitions.get(new Integer(1));

        staticPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_ic")),1,2,2,1,GridBagConstraints.CENTER);
        fillBoxWithColors(m_staticColorBox);
        m_staticColorBox.setSelectedIndex(tmpCoal.getIDColor());
        m_staticColorBox.addActionListener(this);
        staticPanel.constrain(m_staticColorBox,3,2,2,1);

        staticPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_sharing")),1,3,2,1,GridBagConstraints.CENTER);
        m_staticShareBox.addItem("Yes");
        m_staticShareBox.addItem("No");
        if (tmpCoal.getSharing())
            m_staticShareBox.setSelectedIndex(0);
        else
            m_staticShareBox.setSelectedIndex(1);
        m_staticShareBox.addActionListener(this);
        staticPanel.constrain(m_staticShareBox,3,3,2,1);

        staticPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_cm")),1,4,2,1,GridBagConstraints.CENTER);
        m_staticCritMassBox.addItem("All");
        for (int x=1;x<10;x++)
            {
            m_staticCritMassBox.addItem(""+x);
            }
        m_staticCritMassBox.setSelectedIndex(tmpCoal.getNumNeededToJoin());
        m_staticCritMassBox.addActionListener(this);
        staticPanel.constrain(m_staticCritMassBox,3,4,2,1,GridBagConstraints.CENTER);

        staticPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_rm")),1,5,2,1,GridBagConstraints.CENTER);
        m_staticReportMethBox.addItem("Actual");
        for (int x=1;x<10;x++)
            {
            m_staticReportMethBox.addItem(""+x);
            }
        m_staticReportMethBox.setSelectedIndex(tmpCoal.getReportMethod());
	m_staticReportMethBox.addActionListener(this);
        staticPanel.constrain(m_staticReportMethBox,3,5,2,1,GridBagConstraints.CENTER);

    // Zapping section

        staticPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_zapping")),5,1,2,1,GridBagConstraints.CENTER);
        m_staticZapBox.addItem("Yes");
        m_staticZapBox.addItem("No");
        if (tmpCoal.getZapping())
            m_staticZapBox.setSelectedIndex(0);
        else
            m_staticZapBox.setSelectedIndex(1);
        m_staticZapBox.addActionListener(this);
        staticPanel.constrain(m_staticZapBox,7,1,2,1);

        staticPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_zc")),5,2,2,1,GridBagConstraints.CENTER);
        int tmpInt = (int)(tmpCoal.getZapCost()*100);
        m_staticZapCostField= new BENumberJTextField(""+tmpInt,5);
        m_staticZapCostField.setAllowFloat(false);
        m_staticZapCostField.setAllowNegative(false);
        m_staticZapCostField.addActionListener(this);
        staticPanel.constrain(m_staticZapCostField,7,2,2,1,GridBagConstraints.CENTER);

        staticPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_za")),5,3,2,1,GridBagConstraints.CENTER);
        tmpInt = (int)(tmpCoal.getZapAmount()*100);
        m_staticZapAmtField= new BENumberJTextField(""+tmpInt,5);
        m_staticZapAmtField.setAllowFloat(false);
        m_staticZapAmtField.setAllowNegative(false);
        m_staticZapAmtField.addActionListener(this);
        staticPanel.constrain(m_staticZapAmtField,7,3,2,1,GridBagConstraints.CENTER);
        
        staticPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_vn")),5,4,2,1,GridBagConstraints.CENTER);
        m_staticVotesNeededBox.addItem("All");
        for (int x=1;x<9;x++)
            {
            m_staticVotesNeededBox.addItem(""+x);
            }
        m_staticVotesNeededBox.setSelectedIndex(tmpCoal.getNumNeededToZap());
        m_staticVotesNeededBox.addActionListener(this);
        staticPanel.constrain(m_staticVotesNeededBox,7,4,2,1);

        staticPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_nz")),5,5,2,1,GridBagConstraints.CENTER);
        m_staticNumZapBox.addItem("All");
        for (int x=1;x<9;x++)
            {
            m_staticNumZapBox.addItem(""+x);
            }
        m_staticNumZapBox.setSelectedIndex(tmpCoal.getNumZapped());
        m_staticNumZapBox.addActionListener(this);
        staticPanel.constrain(m_staticNumZapBox,7,5,2,1);

        return staticPanel;
        }

    public JGridBagPanel createDynamicCoalitionPanel()
        {
        JGridBagPanel dynamicPanel = new JGridBagPanel();

        dynamicPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_coalition")),1,1,2,1,GridBagConstraints.CENTER);
        for (int x=1;x<7;x++)
            {
            m_dynamicCoalBox.addItem(""+x);
            }
        m_dynamicCoalBox.setSelectedIndex(0);
        m_dynamicCoalBox.addActionListener(this);
        dynamicPanel.constrain(m_dynamicCoalBox,3,1,2,1);

        BECoalition tmpCoal = (BECoalition)m_staticCoalitions.get(new Integer(1));

        dynamicPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_sharing")),1,2,2,1,GridBagConstraints.CENTER);
        m_dynamicShareBox.addItem("Yes");
        m_dynamicShareBox.addItem("No");
        if (tmpCoal.getSharing())
            m_dynamicShareBox.setSelectedIndex(0);
        else
            m_dynamicShareBox.setSelectedIndex(1);
        m_dynamicShareBox.addActionListener(this);
        dynamicPanel.constrain(m_dynamicShareBox,3,2,2,1);

    // Zapping section

        dynamicPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_zapping")),5,1,2,1,GridBagConstraints.CENTER);
        m_dynamicZapBox.addItem("Yes");
        m_dynamicZapBox.addItem("No");
        if (tmpCoal.getZapping())
            m_dynamicZapBox.setSelectedIndex(0);
        else
            m_dynamicZapBox.setSelectedIndex(1);
        m_dynamicZapBox.addActionListener(this);
        dynamicPanel.constrain(m_dynamicZapBox,7,1,2,1);

        dynamicPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_zc")),5,2,2,1,GridBagConstraints.CENTER);
        int tmpInt = (int)(tmpCoal.getZapCost()*100);
        m_dynamicZapCostField = new BENumberJTextField(""+tmpInt,5);
        m_dynamicZapCostField.setAllowFloat(false);
        m_dynamicZapCostField.setAllowNegative(false);
        m_dynamicZapCostField.addActionListener(this);
        dynamicPanel.constrain(m_dynamicZapCostField,7,2,2,1,GridBagConstraints.CENTER);

        dynamicPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_za")),5,3,2,1,GridBagConstraints.CENTER);
        tmpInt = (int)(tmpCoal.getZapAmount()*100);
        m_dynamicZapAmtField = new BENumberJTextField(""+tmpInt,5);
        m_dynamicZapAmtField.setAllowFloat(false);
        m_dynamicZapAmtField.setAllowNegative(false);
        m_dynamicZapAmtField.addActionListener(this);
        dynamicPanel.constrain(m_dynamicZapAmtField,7,3,2,1,GridBagConstraints.CENTER);
        
        dynamicPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_vn")),5,4,2,1,GridBagConstraints.CENTER);
        m_dynamicVotesNeededBox.addItem("All");
        for (int x=1;x<9;x++)
            {
            m_dynamicVotesNeededBox.addItem(""+x);
            }
        m_dynamicVotesNeededBox.setSelectedIndex(tmpCoal.getNumNeededToZap());
        m_dynamicVotesNeededBox.addActionListener(this);
        dynamicPanel.constrain(m_dynamicVotesNeededBox,7,4,2,1);

        dynamicPanel.constrain(new JLabel(m_EOApp.getLabels().getObjectLabel("befcw_nz")),5,5,2,1,GridBagConstraints.CENTER);
        m_dynamicNumZapBox.addItem("All");
        for (int x=1;x<9;x++)
            {
            m_dynamicNumZapBox.addItem(""+x);
            }
        m_dynamicNumZapBox.setSelectedIndex(tmpCoal.getNumZapped());
        m_dynamicNumZapBox.addActionListener(this);
        dynamicPanel.constrain(m_dynamicNumZapBox,7,5,2,1);


        return dynamicPanel;
        }

    private void fillBoxWithColors(JComboBox jcb)
        {
        Enumeration enm = m_availableColors.elements();
        while (enm.hasMoreElements())
            {
            Image img = m_EOApp.createImage(50,15);
            Graphics g = img.getGraphics();
            g.setColor((Color)enm.nextElement());
            g.fillRect(0,0,50,15);
            ImageIcon ii = new ImageIcon(img);
            jcb.addItem(ii);
            }
        }
    private void fillEdgeList(BENode node)
        {
        BENodeOrSubNet exch = (BENodeOrSubNet)node.getExptData("BENodeExchange");
        BECoalition bec = exch.getCoalition();

        Enumeration enm = bec.getShareList().keys();
        while (enm.hasMoreElements())
            {
            Integer tmpID = (Integer)enm.nextElement();
            m_edgeList.addItem(buildEdgeListEntry(node, tmpID.intValue()));
            }
        }
    private void fillNodeList()
        {
        Enumeration enm = m_activeNetwork.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode Ntemp = (BENode)enm.nextElement();
            m_nodeList.addItem(buildNodeListEntry(Ntemp));
            }
        }

    private void initializeCoalitions()
        {
        for (int x=1;x<7;x++)
            {
            BECoalition tmpCoal = new BECoalition(x);
            tmpCoal.setCoalitionType("Static");
            tmpCoal.setCoalition(x);
            m_staticCoalitions.put(new Integer(x),tmpCoal);
            }
        for (int x=1;x<7;x++)
            {
            BECoalition tmpCoal = new BECoalition(x);
            tmpCoal.setCoalitionType("Dynamic");
            tmpCoal.setCoalition(x);
            m_dynamicCoalitions.put(new Integer(x),tmpCoal);
            }
        Enumeration enm = m_activeNetwork.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode Ntemp = (BENode)enm.nextElement();
            BENodeOrSubNet exch = (BENodeOrSubNet)Ntemp.getExptData("BENodeExchange");
            BECoalition bec = exch.getCoalition();
            if (bec.getCoalitionType().equals("Static"))
                {
                BECoalition tmpCoal = (BECoalition)m_staticCoalitions.get(new Integer(bec.getCoalition()));
                copyBaseCoalitionSettings(bec,tmpCoal);
                }
            else if (bec.getCoalitionType().equals("Dynamic"))
                {
                BECoalition tmpCoal = (BECoalition)m_dynamicCoalitions.get(new Integer(bec.getCoalition()));
                copyBaseCoalitionSettings(bec,tmpCoal);
                }
            else if ((bec.getCoalitionType().equals("None")) && (bec.getShareList().size() == 0))
                {
  // Is part of an old BENodeSubOrNet and we need to update it.
                Enumeration enum2 = m_activeNetwork.getEdgeList().elements();
                while (enum2.hasMoreElements())
                    {
                    BEEdge edge = (BEEdge)enum2.nextElement();
                    if (edge.getNode1() == Ntemp.getID())
                        bec.setShareList(edge.getNode2(),false);
                    if (edge.getNode2() == Ntemp.getID())
                        bec.setShareList(edge.getNode1(),false);
                    }
                }
            }
        }
    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/befcw.txt");
        }

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedJList)
            {
            FixedJList theSource = (FixedJList)e.getSource();

            if (m_updateFlag)
                return;

            if (theSource == m_nodeList)
                {
                m_updateFlag = true;
                int nodeID = (Integer.valueOf(m_nodeList.getSelectedSubItem(1))).intValue();
                BENode node = (BENode)m_activeNetwork.getNode(nodeID);
                BENodeOrSubNet exch = (BENodeOrSubNet)node.getExptData("BENodeExchange");
                BECoalition bec = exch.getCoalition();
                m_nodeLabel.setText(node.getLabel()+" "+node.getID());
                if (bec.getCoalitionType().equals("None"))
                    {
                    m_typeBox.setSelectedIndex(0);
                    }
                else if (bec.getCoalitionType().equals("Static"))
                    {
                    m_typeBox.setSelectedIndex(1);
                    }
                else if (bec.getCoalitionType().equals("Dynamic"))
                    {
                    m_typeBox.setSelectedIndex(2);
                    }
                m_coalitionBox.setSelectedIndex(bec.getCoalition()-1);
                m_infoLevelBox.setSelectedIndex(bec.getCoalitionInfoLevel());
                m_edgeList.removeAll();
                fillEdgeList(node);
                m_updateFlag = false;
                }
// Have it toggle the sharing of those resources.
            if ((theSource == m_edgeList) && (m_nodeList.getSelectedIndex() > -1))
                {
                m_updateFlag = true;
                Integer nodeID = Integer.valueOf(m_nodeList.getSelectedSubItem(1));
                Integer resID = Integer.valueOf(m_edgeList.getSelectedSubItem(1));
                BENode node = (BENode)m_activeNetwork.getNode(Integer.valueOf(m_nodeList.getSelectedSubItem(1)));
                BENodeOrSubNet exch = (BENodeOrSubNet)node.getExptData("BENodeExchange");
                BECoalition bec = exch.getCoalition();
                bec.toggleShareListItem(resID.intValue());
                m_edgeList.replaceItem(buildEdgeListEntry(node,resID.intValue()),m_edgeList.getSelectedIndex());
                m_updateFlag = false;
                }
            }
        }

    
    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/befcw.txt");
        }

/**
 *
 */
    private void updateActiveNodeCoal()
        {
        String coalType = (String)m_typeBox.getSelectedItem();
        Integer infoLevel = Integer.valueOf((String)m_infoLevelBox.getSelectedItem());
        Integer coalitionID = Integer.valueOf((String)m_coalitionBox.getSelectedItem());

        int nodeID = (Integer.valueOf(m_nodeList.getSelectedSubItem(1))).intValue();
         
        BENode node = (BENode)m_activeNetwork.getNode(nodeID);
        BENodeOrSubNet exch = (BENodeOrSubNet)node.getExptData("BENodeExchange");
        BECoalition bec = exch.getCoalition();

        if (!bec.getCoalitionType().equals(coalType))
            {
            Integer coalID = Integer.valueOf((String)m_coalitionBox.getSelectedItem());
            bec.setCoalitionType(coalType);
            if (coalType.equals("None"))
                {
                bec.setSharing(false);
                m_nodeList.replaceItem(buildNodeListEntry(node),m_nodeList.getSelectedIndex());
                return;
                }
            if (coalType.equals("Static"))
                {
                BECoalition staticCoal = (BECoalition)m_staticCoalitions.get(coalID);
                copyBaseCoalitionSettings(staticCoal,bec);
                m_nodeList.replaceItem(buildNodeListEntry(node),m_nodeList.getSelectedIndex());
                return;
                }
            if (coalType.equals("Dynamic"))
                {
                BECoalition dynamicCoal = (BECoalition)m_dynamicCoalitions.get(coalID);
                copyBaseCoalitionSettings(dynamicCoal,bec);
                m_nodeList.replaceItem(buildNodeListEntry(node),m_nodeList.getSelectedIndex());
                return;
                }
            }
        bec.setCoalitionInfoLevel(infoLevel.intValue());
        bec.setCoalition(coalitionID.intValue());
        m_nodeList.replaceItem(buildNodeListEntry(node),m_nodeList.getSelectedIndex());
        }
/**
 *
 */
    private void updateDynamicCoal(Integer coal)
        {
        BECoalition tmpCoal = (BECoalition)m_dynamicCoalitions.get(coal);

        // Sharing
        if (m_dynamicShareBox.getSelectedIndex() == 0)
            tmpCoal.setSharing(true);
        else
            tmpCoal.setSharing(false);
  // Zapping
        if (m_dynamicZapBox.getSelectedIndex() == 0)
            tmpCoal.setZapping(true);
        else
            tmpCoal.setZapping(false);
  // Zap Cost
        double tmpDouble = m_dynamicZapCostField.getDoubleValue();
        tmpCoal.setZapCost(tmpDouble/100.0);
  // Zap Amount
        tmpDouble = m_dynamicZapAmtField.getDoubleValue();
        tmpCoal.setZapAmount(tmpDouble/100.0);
  // Num Needed per Zap
        tmpCoal.setNumNeededToZap(m_dynamicVotesNeededBox.getSelectedIndex());
  // Num Zap per Zap
        tmpCoal.setNumZapped(m_dynamicNumZapBox.getSelectedIndex());

  // Update the BeNodeOrSubNets.
        Enumeration enm = m_activeNetwork.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode Ntemp = (BENode)enm.nextElement();
            BENodeOrSubNet exch = (BENodeOrSubNet)Ntemp.getExptData("BENodeExchange");
            BECoalition bec = exch.getCoalition();
            if ((bec.getCoalitionType().equals("Dynamic")) && (bec.getCoalition() == tmpCoal.getCoalition()))
                {
                copyBaseCoalitionSettings(tmpCoal,bec);
                }
            }
        }
/**
 *
 */
    private void updateDynamicPane(Integer coal)
        {
        if (coal.intValue() == m_activeDynamicCoal)
            {
            return;
            }

        m_updateFlag = true;

        m_activeDynamicCoal = coal.intValue();

        BECoalition tmpCoal = (BECoalition)m_dynamicCoalitions.get(coal);
  // Sharing
        if (tmpCoal.getSharing())
            m_dynamicShareBox.setSelectedIndex(0);
        else
            m_dynamicShareBox.setSelectedIndex(1);
  // Zapping
        if (tmpCoal.getZapping())
            m_dynamicZapBox.setSelectedIndex(0);
        else
            m_dynamicZapBox.setSelectedIndex(1);
  // Zap Cost
        int tmpInt = (int)(tmpCoal.getZapCost()*100);
        m_dynamicZapCostField.setText(""+tmpInt);
  // Zap Amount
        tmpInt = (int)(tmpCoal.getZapAmount()*100);
        m_dynamicZapAmtField.setText(""+tmpInt);
  // Num Needed per Zap
        m_dynamicVotesNeededBox.setSelectedIndex(tmpCoal.getNumNeededToZap());
  // Num Zap per Zap
        m_dynamicNumZapBox.setSelectedIndex(tmpCoal.getNumZapped());
        validate();
        m_updateFlag = false;
        }
/**
 * 
 */
    private void updateStaticCoal(Integer coal)
        {
        BECoalition tmpCoal = (BECoalition)m_staticCoalitions.get(coal);

        // Coalition ID Color
        tmpCoal.setIDColor(m_staticColorBox.getSelectedIndex());

  // Sharing
        if (m_staticShareBox.getSelectedIndex() == 0)
            tmpCoal.setSharing(true);
        else
            tmpCoal.setSharing(false);
  // Number needed to join
        tmpCoal.setNumNeededToJoin(m_staticCritMassBox.getSelectedIndex());
  // Report Method
        tmpCoal.setReportMethod(m_staticReportMethBox.getSelectedIndex());
	// System.err.println("setting:"+m_staticReportMethBox.getSelectedIndex());
  // Zapping
        if (m_staticZapBox.getSelectedIndex() == 0)
            tmpCoal.setZapping(true);
        else
            tmpCoal.setZapping(false);
  // Zap Cost
        double tmpDouble = m_staticZapCostField.getDoubleValue();
        tmpCoal.setZapCost(tmpDouble/100.0);
  // Zap Amount
        tmpDouble = m_staticZapAmtField.getDoubleValue();
        tmpCoal.setZapAmount(tmpDouble/100.0);
  // Num Needed per Zap
        tmpCoal.setNumNeededToZap(m_staticVotesNeededBox.getSelectedIndex());
  // Num Zap per Zap
        tmpCoal.setNumZapped(m_staticNumZapBox.getSelectedIndex());

  // Update the BeNodeOrSubNets.
        Enumeration enm = m_activeNetwork.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode Ntemp = (BENode)enm.nextElement();
            BENodeOrSubNet exch = (BENodeOrSubNet)Ntemp.getExptData("BENodeExchange");
            BECoalition bec = exch.getCoalition();
            if ((bec.getCoalitionType().equals("Static")) && (bec.getCoalition() == tmpCoal.getCoalition()))
                {
                copyBaseCoalitionSettings(tmpCoal,bec);
                }
            }
        }
/**
 *
 */
    private void updateStaticPane(Integer coal)
        {
        if (coal.intValue() == m_activeStaticCoal)
            {
            return;
            }

        m_updateFlag = true;

        m_activeStaticCoal = coal.intValue();

        BECoalition tmpCoal = (BECoalition)m_staticCoalitions.get(coal);

  // Coalition ID Color
        m_staticColorBox.setSelectedIndex(tmpCoal.getIDColor());

  // Sharing
        if (tmpCoal.getSharing())
            m_staticShareBox.setSelectedIndex(0);
        else
            m_staticShareBox.setSelectedIndex(1);
  // Number needed to join
        m_staticCritMassBox.setSelectedIndex(tmpCoal.getNumNeededToJoin());
  // Report Method
        m_staticReportMethBox.setSelectedIndex(tmpCoal.getReportMethod());
  // Zapping
        if (tmpCoal.getZapping())
            m_staticZapBox.setSelectedIndex(0);
        else
            m_staticZapBox.setSelectedIndex(1);
  // Zap Cost
        int tmpInt = (int)(tmpCoal.getZapCost()*100);
        m_staticZapCostField.setText(""+tmpInt);
  // Zap Amount
        tmpInt = (int)(tmpCoal.getZapAmount()*100);
        m_staticZapAmtField.setText(""+tmpInt);
  // Num Needed per Zap
        m_staticVotesNeededBox.setSelectedIndex(tmpCoal.getNumNeededToZap());
  // Num Zap per Zap
        m_staticNumZapBox.setSelectedIndex(tmpCoal.getNumZapped());

        validate();

        m_updateFlag = false;
        }
    }
