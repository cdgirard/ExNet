package girard.sc.be.awt;


import girard.sc.awt.BorderPanel;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.awt.SortedFixedList;
import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeSanction;
import girard.sc.be.obj.BENodeSanctions;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;

/**
 * Allows you to set the sanctions for nodes
 * that are in a BENetwork.
 * <p>
 * <br> Started: 09-19-2002
 * <p>
 * @author Dudley Girard
 */

public class BESetSanctionsWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    BEFormatNetworkActionWindow m_FNAWApp;
    BENetwork m_activeNetwork;

  // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Help; 
    
    NumberTextField m_SanctionField, m_RewardField;

    SortedFixedList m_NodeList, m_AdjNodeList;
    int m_NodeListIndex, m_AdjNodeListIndex;

    Checkbox m_Sanction;

    public BESetSanctionsWindow(ExptOverlord app1, BEFormatNetworkActionWindow app2, BENetwork app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_FNAWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */

        initializeLabels();
        initializeNetwork();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("bessw2_title"));
        setFont(m_EOApp.getMedWinFont());
               
//  Start Setup for Menus
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;
     // File Menu

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("bessw2_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("bessw2_done"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

     // Help Menu

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("bessw2_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("bessw2_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
    // End Setup for Menubar.

// Setup the North Panel part of the Window.

        GridBagPanel NorthPanel = new GridBagPanel();

        NorthPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bessw2_nodes")),1,1,3,1,GridBagConstraints.CENTER);

        m_NodeList = new SortedFixedList(8,false,2,5,SortedFixedList.CENTER);
        Enumeration enm = m_activeNetwork.getNodeList().elements();
        while(enm.hasMoreElements())
            {
            BENode ben = (BENode)enm.nextElement();
            m_NodeList.addItem(BuildNodeListEntry(ben));
            }
        m_NodeList.addItemListener(this);
        m_NodeListIndex = -1;
        NorthPanel.constrain(m_NodeList,1,2,3,6,GridBagConstraints.CENTER);

        NorthPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bessw2_an")),4,1,3,1,GridBagConstraints.CENTER);

        m_AdjNodeList = new SortedFixedList(8,false,2,5,SortedFixedList.CENTER);
        m_AdjNodeList.addItemListener(this);
        m_AdjNodeListIndex = -1;
        NorthPanel.constrain(m_AdjNodeList,4,2,3,6,GridBagConstraints.CENTER);

// Setup Center Panel area.

        GridBagPanel CenterPanel = new GridBagPanel();
     
     // Start Sanction display setup.
        m_Sanction = new Checkbox(m_EOApp.getLabels().getObjectLabel("bessw2_crop"),false);
        m_Sanction.addItemListener(this);
        CenterPanel.constrain(m_Sanction,1,1,4,1,GridBagConstraints.CENTER);
       
        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bessw2_ra")),1,2,2,1,GridBagConstraints.CENTER);
        m_RewardField = new NumberTextField("",4);
        m_RewardField.setEditMode(false);
        m_RewardField.setAllowFloat(false);
        CenterPanel.constrain(m_RewardField,3,2,2,1,GridBagConstraints.CENTER);

        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bessw2_pa")),1,3,2,1,GridBagConstraints.CENTER);
        m_SanctionField = new NumberTextField("",4);
        m_SanctionField.setEditMode(false);
        m_SanctionField.setAllowFloat(false);
        CenterPanel.constrain(m_SanctionField,3,3,2,1,GridBagConstraints.CENTER);
     // End Sanction dislay setup.

        add("North",new BorderPanel(NorthPanel,BorderPanel.FRAME));
        add("Center",new BorderPanel(CenterPanel,BorderPanel.FRAME));

        pack();
        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();
       
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("bessw2_done")))
                {
                if ((m_NodeListIndex > -1) && (m_AdjNodeListIndex > -1))
                    UpdateNodeResourceSettings();
                m_FNAWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("bessw2_help")))
                {
                m_EOApp.helpWindow("ehlp_bessw2");
                }
            }
        }

    public String[] BuildAdjNodeListEntry(BENode ben)
        {
        String[] str = new String[2];

        str[0] = new String(""+ben.getID());
        str[1] = new String(""+ben.getLabel());

        return str;
        }
    public String[] BuildNodeListEntry(BENode ben)
        {
        String[] str = new String[2];

        str[0] = new String(""+ben.getID());
        str[1] = new String(""+ben.getLabel());

        return str;
        }

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

            if ((theSource == m_NodeList) && (theSource.getSelectedIndex() > -1))
                {
                if (m_AdjNodeListIndex > -1)
                    {
                    UpdateNodeResourceSettings();
                    }
                else if (m_NodeListIndex > -1)
                    {
                    int node = Integer.valueOf(m_NodeList.getSubItem(m_NodeListIndex,0)).intValue();

                    BENode n = (BENode)m_activeNetwork.getNode(node);
                    }
                m_NodeListIndex = theSource.getSelectedIndex();
                UpdateNodeDisplay();
                }

            if ((theSource == m_AdjNodeList) && (theSource.getSelectedIndex() > -1))
                {
                if (m_AdjNodeListIndex > -1)
                    {
                    UpdateNodeResourceSettings();
                    }
                m_AdjNodeListIndex = theSource.getSelectedIndex();
                UpdateAdjDisplay();
                }
            }
        if (e.getSource() instanceof Checkbox)
            {
            Checkbox theSource = (Checkbox)e.getSource();

            if (theSource == m_Sanction)
                {
                if (m_AdjNodeListIndex == -1)
                    m_Sanction.setState(false);
                else
                    {
                    if (m_Sanction.getState())
                        {
                        m_SanctionField.setEditMode(true);
                        m_SanctionField.setText("0");
                        m_RewardField.setEditMode(true);
                        m_RewardField.setText("0");
                        }
                    else
                        {
                        m_SanctionField.setText("");
                        m_SanctionField.setEditMode(false);
                        m_RewardField.setText("");
                        m_RewardField.setEditMode(false);
                        }
                    }
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/bessw2.txt");
        }
    public void initializeNetwork()
        {
        Enumeration enm = m_activeNetwork.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode ben = (BENode)enm.nextElement();
            if (ben.getExptData("BENodeSanctions") == null)
                {
                BENodeSanctions ns = new BENodeSanctions(ben,m_activeNetwork);
                ben.addExptData(ns);
                }
            }
        } 

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/bessw2.txt");
        }

    public void UpdateNodeDisplay()
        {
        if (m_NodeListIndex > -1)
            {
            int node = Integer.valueOf(m_NodeList.getSubItem(m_NodeListIndex,0)).intValue();
            BENode n = (BENode)m_activeNetwork.getNode(node);

            m_AdjNodeList.removeAll();

            Enumeration enm = m_activeNetwork.getEdgeList().elements();
            while (enm.hasMoreElements())
                {
                BEEdge edge = (BEEdge)enm.nextElement();
                if (edge.getNode1() == node)
                    {
                    BENode n2 = (BENode)m_activeNetwork.getNode(edge.getNode2());
                    m_AdjNodeList.addItem(BuildAdjNodeListEntry(n2));
                    }
                if (edge.getNode2() == node)
                    {
                    BENode n2 = (BENode)m_activeNetwork.getNode(edge.getNode1());
                    m_AdjNodeList.addItem(BuildAdjNodeListEntry(n2));
                    }
                }
            m_AdjNodeListIndex = -1;
            }
    }

    public void UpdateAdjDisplay()
        {
        if (m_AdjNodeListIndex > -1)
            {
            int node = Integer.valueOf(m_NodeList.getSubItem(m_NodeListIndex,0)).intValue();
            int adjNode = Integer.valueOf(m_AdjNodeList.getSubItem(m_AdjNodeListIndex,0)).intValue();

            BENode n = (BENode)m_activeNetwork.getNode(node);
            BENode an = (BENode)m_activeNetwork.getNode(adjNode);

    // Update Settings for Node Sanction 
            BENodeSanctions ns = (BENodeSanctions)n.getExptData("BENodeSanctions");

            if (ns.hasSanction(adjNode))
                {
                m_Sanction.setState(true);
                BENodeSanction tmp = ns.getSanction(adjNode);
                m_SanctionField.setText(""+tmp.getSanctionValue());
                m_SanctionField.setEditMode(true);
                m_RewardField.setText(""+tmp.getRewardValue());
                m_RewardField.setEditMode(true);
                }
            else
                {
                m_Sanction.setState(false);
                m_SanctionField.setText("");
                m_SanctionField.setEditMode(false);
                m_RewardField.setText("");
                m_RewardField.setEditMode(false);
                }
            }
        }

    public void UpdateNodeResourceSettings()
        {
        int node = Integer.valueOf(m_NodeList.getSubItem(m_NodeListIndex,0)).intValue();
        int adjNode = Integer.valueOf(m_AdjNodeList.getSubItem(m_AdjNodeListIndex,0)).intValue();

        BENode n = (BENode)m_activeNetwork.getNode(node);
        BENode an = (BENode)m_activeNetwork.getNode(adjNode);

    // Update Sanction Information
        BENodeSanctions ns = (BENodeSanctions)n.getExptData("BENodeSanctions");

        if (m_Sanction.getState())
            {
            if (ns.hasSanction(adjNode))
                {
                BENodeSanction tmp = ns.getSanction(adjNode);

                int s = Integer.valueOf(m_SanctionField.getText()).intValue();
                int r = Integer.valueOf(m_RewardField.getText()).intValue();

                tmp.setRewardValue(r);
                tmp.setSanctionValue(s);
                }
            else
                {
                int s = Integer.valueOf(m_SanctionField.getText()).intValue();
                int r = Integer.valueOf(m_RewardField.getText()).intValue();

                BENodeSanction sanction = new BENodeSanction(adjNode,r,s);
                ns.addSanction(sanction);
                }
            }
        else
            {
            if (ns.hasSanction(adjNode))
                {
                ns.removeSanction(adjNode);
                }
            }
        m_Sanction.setState(false);
        m_SanctionField.setEditMode(false);
        m_SanctionField.setText("");
        m_RewardField.setEditMode(false);
        m_RewardField.setText("");
        }
    }
