package girard.sc.cc.awt;

/* Allows you to set the resources, tokens, and sanctions for nodes
   that are in a cc network.

   Author: Dudley Girard
   Startd: 5-25-2001
   Modified: 7-11-2001
   Modified: 7-31-2001
*/

import girard.sc.awt.BorderPanel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.cc.obj.CCEdge;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeFuzzies;
import girard.sc.cc.obj.CCNodeFuzzy;
import girard.sc.cc.obj.CCNodeResource;
import girard.sc.cc.obj.CCNodeSanction;
import girard.sc.cc.obj.CCNodeSanctions;
import girard.sc.cc.obj.CCNodeToken;
import girard.sc.cc.obj.CCNodeTokens;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
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
import java.util.Vector;

public class CCEditNodeResourceWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    CCFormatNetworkActionWindow m_FNAWApp;
    CCNetwork m_activeNetwork;

  // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Help;

    NumberTextField m_BankField, m_PointsField;
    NumberTextField m_TokenYesField, m_TokenNoField;
    NumberTextField m_SanctionField, m_RewardField;
    NumberTextField m_TokenPercent;

    FixedList m_NodeList, m_AdjNodeList, m_AboutNodeList;
    int m_NodeListIndex, m_AdjNodeListIndex, m_AboutNodeListIndex;

    Checkbox m_OfferPoints, m_OfferToken, m_OfferFuzzy, m_Sanction;
    Label m_PeriodLabel;
    
    Button m_DoneButton, m_HelpButton;

    public CCEditNodeResourceWindow(ExptOverlord app1, CCFormatNetworkActionWindow app2, CCNetwork app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_FNAWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */

        initializeLabels();
        initializeNetwork();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("ccenrw_title"));
        setFont(m_EOApp.getMedWinFont());
               
//  Start Setup for Menus
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;
     // File Menu

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("ccenrw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccenrw_done"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

     // Help Menu

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("ccenrw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccenrw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
    // End Setup for Menubar.

// Setup the North Panel part of the Window.

        GridBagPanel NorthPanel = new GridBagPanel();

        NorthPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccenrw_nodes")),1,1,3,1,GridBagConstraints.CENTER);

        m_NodeList = new FixedList(8,false,2,5,FixedList.CENTER);
        Enumeration enm = m_activeNetwork.getNodeList().elements();
        while(enm.hasMoreElements())
            {
            CCNode ccn = (CCNode)enm.nextElement();
            m_NodeList.addItem(BuildNodeListEntry(ccn));
            }
        m_NodeList.addItemListener(this);
        m_NodeListIndex = -1;
        NorthPanel.constrain(m_NodeList,1,2,3,6,GridBagConstraints.CENTER); 

        NorthPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccenrw_ba")),4,2,3,1,GridBagConstraints.CENTER);
        m_BankField = new NumberTextField("0",5);
        m_BankField.setAllowFloat(false);
        NorthPanel.constrain(m_BankField,4,3,3,1,GridBagConstraints.CENTER);

        NorthPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccenrw_pp")),4,4,3,1,GridBagConstraints.CENTER);
        m_PointsField = new NumberTextField("0",5);
        m_PointsField.setAllowFloat(false);
        NorthPanel.constrain(m_PointsField,4,5,3,1,GridBagConstraints.CENTER);

        NorthPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccenrw_an")),7,1,3,1,GridBagConstraints.CENTER);

        m_AdjNodeList = new FixedList(8,false,2,5,FixedList.CENTER);
        m_AdjNodeList.addItemListener(this);
        m_AdjNodeListIndex = -1;
        NorthPanel.constrain(m_AdjNodeList,7,2,3,6,GridBagConstraints.CENTER);

// Setup Center Panel area.

        GridBagPanel CenterPanel = new GridBagPanel();

        m_OfferPoints = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccenrw_cop"),false);
        CenterPanel.constrain(m_OfferPoints,1,1,4,1,GridBagConstraints.CENTER);

      // Start Token display setup.
        m_OfferToken = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccenrw_csat"),false);
        m_OfferToken.addItemListener(this);
        CenterPanel.constrain(m_OfferToken,1,2,4,1,GridBagConstraints.CENTER);

        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccenrw_percent")),1,3,2,1,GridBagConstraints.CENTER);
        m_TokenPercent = new NumberTextField("",4);
        m_TokenPercent.setEditMode(false);
        m_TokenPercent.setAllowFloat(false);
        m_TokenPercent.setAllowNegative(false);
        CenterPanel.constrain(m_TokenPercent,3,3,2,1,GridBagConstraints.CENTER);
       
        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccenrw_ra")),5,3,2,1,GridBagConstraints.CENTER);
        m_TokenYesField = new NumberTextField("",4);
        m_TokenYesField.setEditMode(false);
        m_TokenYesField.setAllowFloat(false);
        CenterPanel.constrain(m_TokenYesField,7,3,2,1,GridBagConstraints.CENTER);

        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccenrw_fa")),5,4,2,1,GridBagConstraints.CENTER);
        m_TokenNoField = new NumberTextField("",4);
        m_TokenNoField.setEditMode(false);
        m_TokenNoField.setAllowFloat(false);
        CenterPanel.constrain(m_TokenNoField,7,4,2,1,GridBagConstraints.CENTER);
     // End Token dislay setup.

    // Start Fuzzy Display Setup
        m_OfferFuzzy = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccenrw_csm"),false);
        m_OfferFuzzy.addItemListener(this);
        CenterPanel.constrain(m_OfferFuzzy,1,5,4,1,GridBagConstraints.CENTER);

        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccenrw_aboutnode")),1,6,4,1,GridBagConstraints.CENTER);

        m_AboutNodeList = new FixedList(6,true,2,5,FixedList.CENTER);
        enm = m_activeNetwork.getNodeList().elements();
        while(enm.hasMoreElements())
            {
            CCNode ccn = (CCNode)enm.nextElement();
            m_AboutNodeList.addItem(BuildAboutNodeListEntry(ccn));
            }
        m_AboutNodeList.addItemListener(this);
        m_AboutNodeListIndex = -1;
        CenterPanel.constrain(m_AboutNodeList,1,7,4,6,GridBagConstraints.CENTER);
     // End Fuzzy Display Setup
     
     // Start Sanction display setup.
        m_Sanction = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccenrw_crof"),false);
        m_Sanction.addItemListener(this);
        CenterPanel.constrain(m_Sanction,1,13,4,1,GridBagConstraints.CENTER);
       
        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccenrw_ra")),1,14,2,1,GridBagConstraints.CENTER);
        m_RewardField = new NumberTextField("",4);
        m_RewardField.setEditMode(false);
        m_RewardField.setAllowFloat(false);
        CenterPanel.constrain(m_RewardField,3,14,2,1,GridBagConstraints.CENTER);

        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccenrw_fa")),1,15,2,1,GridBagConstraints.CENTER);
        m_SanctionField = new NumberTextField("",4);
        m_SanctionField.setEditMode(false);
        m_SanctionField.setAllowFloat(false);
        CenterPanel.constrain(m_SanctionField,3,15,2,1,GridBagConstraints.CENTER);

        
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
       
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccenrw_done")))
                {
                if ((m_NodeListIndex > -1) && (m_AdjNodeListIndex > -1))
                    UpdateNodeResourceSettings();
                m_FNAWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccenrw_help")))
                {
                m_EOApp.helpWindow("ehlp_ccenrw");
                }
            }
        }

    public String[] BuildAboutNodeListEntry(CCNode ccn)
        {
        String[] str = new String[2];

        str[0] = new String(""+ccn.getID());
        str[1] = new String(""+ccn.getLabel());

        return str;
        }
    public String[] BuildAdjNodeListEntry(CCNode ccn)
        {
        String[] str = new String[2];

        str[0] = new String(""+ccn.getID());
        str[1] = new String(""+ccn.getLabel());

        return str;
        }
    public String[] BuildNodeListEntry(CCNode ccn)
        {
        String[] str = new String[2];

        str[0] = new String(""+ccn.getID());
        str[1] = new String(""+ccn.getLabel());

        return str;
        }

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)e.getSource();

            if ((theSource == m_NodeList) && (theSource.getSelectedIndex() > -1))
                {
                if (m_AdjNodeListIndex > -1)
                    {
                    UpdateNodeResourceSettings();
                    }
                else if (m_NodeListIndex > -1)
                    {
                    int node = Integer.valueOf(m_NodeList.getSubItem(m_NodeListIndex,0)).intValue();

                    CCNode n = (CCNode)m_activeNetwork.getNode(node);

                 // Update the Node Resource Information.
                    CCNodeResource nr = (CCNodeResource)n.getExptData("CCNodeResource");
                    nr.setBank(Integer.valueOf(m_BankField.getText()).intValue());
                    nr.setPointPool(Integer.valueOf(m_PointsField.getText()).intValue());
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
            
            if ((theSource == m_AboutNodeList) && (theSource.getSelectedIndex() > -1))
                {
                if (!m_OfferFuzzy.getState())
                    {
                    int[] indexes = m_AboutNodeList.getSelectedIndexes();
                    for (int i=0;i<indexes.length;i++)
                        {
                        m_AboutNodeList.deselect(indexes[i]);
                        }
                    }
                }
            }
        if (e.getSource() instanceof Checkbox)
            {
            Checkbox theSource = (Checkbox)e.getSource();

            if (theSource == m_OfferToken)
                {
                if (m_AdjNodeListIndex == -1)
                    m_OfferToken.setState(false);
                else
                    {
                    if (m_OfferToken.getState())
                        {
                        m_TokenPercent.setEditMode(true);
                        m_TokenPercent.setText("100");
                        m_TokenYesField.setEditMode(true);
                        m_TokenYesField.setText("0");
                        m_TokenNoField.setEditMode(true);
                        m_TokenNoField.setText("0");
                        }
                    else
                        {
                        m_TokenPercent.setText("");
                        m_TokenPercent.setEditMode(false);
                        m_TokenYesField.setText("");
                        m_TokenYesField.setEditMode(false);
                        m_TokenNoField.setText("");
                        m_TokenNoField.setEditMode(false);
                        }
                    }
                }
            if (theSource == m_OfferFuzzy)
                {
                if (m_AdjNodeListIndex == -1)
                    m_OfferFuzzy.setState(false); 
                else
                    {
                    if (!m_OfferFuzzy.getState())
                        {
                        int[] indexes = m_AboutNodeList.getSelectedIndexes();
                        for (int i=0;i<indexes.length;i++)
                            {
                            m_AboutNodeList.deselect(indexes[i]);
                           }
                        }
                    }
                }
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
        m_EOApp.initializeLabels("girard/sc/cc/awt/ccenrw.txt");
        }
    public void initializeNetwork()
        {
        Enumeration enm = m_activeNetwork.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CCNode ccn = (CCNode)enm.nextElement();
            if (ccn.getExptData("CCNodeTokens") == null)
                {
                CCNodeTokens nt = new CCNodeTokens(ccn,m_activeNetwork);
                ccn.addExptData(nt);
                }
            if (ccn.getExptData("CCNodeFuzzies") == null)
                {
                CCNodeFuzzies nf = new CCNodeFuzzies(ccn,m_activeNetwork);
                ccn.addExptData(nf);
                }
            if (ccn.getExptData("CCNodeSanctions") == null)
                {
                CCNodeSanctions ns = new CCNodeSanctions(ccn,m_activeNetwork);
                ccn.addExptData(ns);
                }
            }
        } 

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/cc/awt/ccenrw.txt");
        }

    public void UpdateNodeDisplay()
        {
        if (m_NodeListIndex > -1)
            {
            int node = Integer.valueOf(m_NodeList.getSubItem(m_NodeListIndex,0)).intValue();
            CCNode n = (CCNode)m_activeNetwork.getNode(node);
            CCNodeResource nr = (CCNodeResource)n.getExptData("CCNodeResource");

            m_BankField.setText(""+nr.getBank());
            m_PointsField.setText(""+nr.getPointPool());

            m_AdjNodeList.removeAll();

            Enumeration enm = m_activeNetwork.getEdgeList().elements();
            while (enm.hasMoreElements())
                {
                CCEdge edge = (CCEdge)enm.nextElement();
                if (edge.getNode1() == node)
                    {
                    CCNode n2 = (CCNode)m_activeNetwork.getNode(edge.getNode2());
                    m_AdjNodeList.addItem(BuildAdjNodeListEntry(n2));
                    }
                if (edge.getNode2() == node)
                    {
                    CCNode n2 = (CCNode)m_activeNetwork.getNode(edge.getNode1());
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

            CCNode n = (CCNode)m_activeNetwork.getNode(node);
            CCNode an = (CCNode)m_activeNetwork.getNode(adjNode);

            CCNodeResource nr = (CCNodeResource)n.getExptData("CCNodeResource");
            if (nr.canTradeWith(adjNode))
                {
                m_OfferPoints.setState(true);
                }
            else
                {
                m_OfferPoints.setState(false);
                }

    // Update Node Token Settings.
            CCNodeTokens nt = (CCNodeTokens)n.getExptData("CCNodeTokens");
            if (nt.hasToken(adjNode))
                {
                m_OfferToken.setState(true);

                CCNodeToken tmp = nt.getToken(adjNode);
                double p = tmp.getPercent();
                int percent = (int)(p*100);

                m_TokenPercent.setText(""+percent);
                m_TokenPercent.setEditMode(true);
                m_TokenYesField.setText(""+tmp.getYesValue());
                m_TokenYesField.setEditMode(true);
                m_TokenNoField.setText(""+tmp.getNoValue());
                m_TokenNoField.setEditMode(true);
                }
            else
                {
                m_OfferToken.setState(false);
                m_TokenPercent.setText("");
                m_TokenPercent.setEditMode(false);
                m_TokenYesField.setText("");
                m_TokenYesField.setEditMode(false);
                m_TokenNoField.setText("");
                m_TokenNoField.setEditMode(false);
                }

      // Update the settings for the Node Fuzzy.
            CCNodeFuzzies nf = (CCNodeFuzzies)n.getExptData("CCNodeFuzzies");

            if (nf.hasFuzzy(adjNode))
                {
                Vector v = nf.getFuzzyList(adjNode);
                m_OfferFuzzy.setState(true);

                int[] indexes = m_AboutNodeList.getSelectedIndexes();
                for (int i=0;i<indexes.length;i++)
                    {
                    m_AboutNodeList.deselect(indexes[i]);
                    }

                Enumeration enm = v.elements();
                while (enm.hasMoreElements())
                    {
                    CCNodeFuzzy fuzzy = (CCNodeFuzzy)enm.nextElement();
                    for (int i=0;i<m_AboutNodeList.getItemCount();i++)
                        {
                        if (fuzzy.getAboutNode() == Integer.valueOf(m_AboutNodeList.getSubItem(i,0)).intValue())
                            {
                            m_AboutNodeList.select(i);
                            break;
                            }
                        }
                    }
                }
            else
                {
                m_OfferFuzzy.setState(false);
                }


    // Update Settings for Node Sanction 
            CCNodeSanctions ns = (CCNodeSanctions)n.getExptData("CCNodeSanctions");

            if (ns.hasSanction(adjNode))
                {
                m_Sanction.setState(true);
                CCNodeSanction tmp = ns.getSanction(adjNode);
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

        CCNode n = (CCNode)m_activeNetwork.getNode(node);
        CCNode an = (CCNode)m_activeNetwork.getNode(adjNode);

 // Update the Node Resource Information.
        CCNodeResource nr = (CCNodeResource)n.getExptData("CCNodeResource");
        CCNodeResource anNr = (CCNodeResource)an.getExptData("CCNodeResource");
        nr.setBank(Integer.valueOf(m_BankField.getText()).intValue());
        nr.setPointPool(Integer.valueOf(m_PointsField.getText()).intValue());
        if ((nr.getPointPool() > 0) && (anNr.getPointPool() > 0))
            {
            if (nr.canTradeWith(adjNode))
                {
                nr.removeTradePartener(adjNode);
                anNr.removeTradePartener(node);
                }
            }
        else if ((nr.getPointPool() == 0) && (anNr.getPointPool() == 0))
            {
            if (nr.canTradeWith(adjNode))
                {
                nr.removeTradePartener(adjNode);
                anNr.removeTradePartener(node);
                }
            }
        else
            {
            if ((nr.canTradeWith(adjNode)) && (!m_OfferPoints.getState()))
                {
                nr.removeTradePartener(adjNode);
                anNr.removeTradePartener(node);
                }
            if ((!nr.canTradeWith(adjNode)) && (m_OfferPoints.getState()))
                {
                nr.addTradePartener(adjNode);
                anNr.addTradePartener(node);
                }
            }
        m_OfferPoints.setState(false);

  // Update the Node Token Information
        CCNodeTokens nt = (CCNodeTokens)n.getExptData("CCNodeTokens");
        if (m_OfferToken.getState())
            {
            if (nt.hasToken(adjNode))
                {
                CCNodeToken tmp = nt.getToken(adjNode);

                int percent = Integer.valueOf(m_TokenPercent.getText()).intValue();
                double p = (percent*1.0)/100.0;
                int yes = Integer.valueOf(m_TokenYesField.getText()).intValue();
                int no = Integer.valueOf(m_TokenNoField.getText()).intValue();

                tmp.setPercent(p);
                tmp.setYesValue(yes);
                tmp.setNoValue(no);
                }
            else
                {
                int percent = Integer.valueOf(m_TokenPercent.getText()).intValue();
                double p = (percent*1.0)/100.0;
                int yes = Integer.valueOf(m_TokenYesField.getText()).intValue();
                int no = Integer.valueOf(m_TokenNoField.getText()).intValue();

                CCNodeToken token = new CCNodeToken(adjNode,p,yes,no);
                nt.addToken(token);
                }
            }
        else
            {
            if (nt.hasToken(adjNode))
                {
                nt.removeToken(adjNode);
                }
            }
       m_OfferToken.setState(false);
       m_TokenYesField.setEditMode(false);
       m_TokenYesField.setText("");
       m_TokenNoField.setEditMode(false);
       m_TokenNoField.setText("");

  // Update the Node Fuzzy Information
        CCNodeFuzzies nf = (CCNodeFuzzies)n.getExptData("CCNodeFuzzies");
        nf.removeFuzzies(adjNode);

        if (m_OfferFuzzy.getState())
            {
            int[] aboutNodes = m_AboutNodeList.getSelectedIndexes();
            for (int i=0;i<aboutNodes.length;i++)
                {
                int aboutNode = Integer.valueOf(m_AboutNodeList.getSubItem(aboutNodes[i],0)).intValue();

                CCNodeFuzzy fuzzy = new CCNodeFuzzy(aboutNode,adjNode);
                nf.addFuzzy(fuzzy);
                }
            }
        m_OfferFuzzy.setState(false);
        int[] indexes = m_AboutNodeList.getSelectedIndexes();
        for (int i=0;i<indexes.length;i++)
            {
            m_AboutNodeList.deselect(indexes[i]);
            }


    // Update Sanction Information
        CCNodeSanctions ns = (CCNodeSanctions)n.getExptData("CCNodeSanctions");

        if (m_Sanction.getState())
            {
            if (ns.hasSanction(adjNode))
                {
                CCNodeSanction tmp = ns.getSanction(adjNode);

                int s = Integer.valueOf(m_SanctionField.getText()).intValue();
                int r = Integer.valueOf(m_RewardField.getText()).intValue();

                tmp.setRewardValue(r);
                tmp.setSanctionValue(s);
                }
            else
                {
                int s = Integer.valueOf(m_SanctionField.getText()).intValue();
                int r = Integer.valueOf(m_RewardField.getText()).intValue();

                CCNodeSanction sanction = new CCNodeSanction(adjNode,r,s);
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
