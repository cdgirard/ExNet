package girard.sc.be.awt;

import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeExchange;
import girard.sc.be.obj.BENodeSubnetwork;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Used to adjust the minimum and maximum number of exchanges a position
 * can make.
 * <p>
 *  Started: 4-12-2002
 *  Modified: 8-7-2002
 * <p>
 *
 * @author Dudley Girard
 *
 */

public class BENumExchWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    BEFormatNetworkActionWindow m_FNAWApp;
    BENetwork m_activeNetwork;

    Hashtable m_nodeExchanges = new Hashtable();

    GridBagPanel m_ExchangeWindowPanel = new GridBagPanel();
    SortedFixedList m_NodeList;
    FixedLabel m_MaxLabel;
    Button m_MaxInc, m_MaxDec;
    FixedLabel m_MinLabel;
    Button m_MinInc, m_MinDec;
    Button m_NumExchangeOK, m_NumExchangeCancel;

    int m_ListIndex = -1;
    BENode m_NodeIndex;
    BENodeExchange m_ExchangeIndex = null;
    int m_rangeMax = 1;

  // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_Help;

    public BENumExchWindow(ExptOverlord app1, BEFormatNetworkActionWindow app2, BENetwork app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_FNAWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */

        initializeLabels();
        initializeNodeExchanges();

        setLayout(new GridLayout(1,1));
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("benew_title"));
        setFont(m_EOApp.getMedWinFont());

    // Start Setup for Menubar
        // m_mbar.setFont(m_EOApp.getMedWinFont());

        setMenuBar(m_mbar);

    // Help Menu
        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("benew_help"));

        MenuItem tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("benew_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
   // End Setup for Menubar

    // List of Nodes and their Min-Max Exchange

        m_ExchangeWindowPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benew_nimm")),1,1,4,1,GridBagConstraints.CENTER);

        int[] tmpIntArray = {10,5,5,5};
        m_NodeList = new SortedFixedList(6,false,4,tmpIntArray,FixedList.CENTER);

        Enumeration enm = m_activeNetwork.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode Ntemp = (BENode)enm.nextElement();
            m_NodeList.addItem(BuildExchangeListEntry(Ntemp));
            }

        m_NodeList.addItemListener(this);
        m_ExchangeWindowPanel.constrain(m_NodeList,1,2,4,4,GridBagConstraints.CENTER); 

    //Maximum Exchange Buttons

        m_ExchangeWindowPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benew_maxe")),1,7,2,1);

        m_MaxInc = new Button("+");
        m_MaxInc.addActionListener(this);
        m_ExchangeWindowPanel.constrain(m_MaxInc,1,8,2,1,GridBagConstraints.CENTER);

        m_MaxLabel = new FixedLabel(2,"-");
        m_ExchangeWindowPanel.constrain(m_MaxLabel,1,9,2,1,GridBagConstraints.CENTER);

        m_MaxDec = new Button("-");
        m_MaxDec.addActionListener(this);
        m_ExchangeWindowPanel.constrain(m_MaxDec,1,10,2,1,GridBagConstraints.CENTER);

    // Minimum Exchange Buttons

        m_ExchangeWindowPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benew_mine")),3,7,2,1);

        m_MinInc = new Button("+");
        m_MinInc.addActionListener(this);
        m_ExchangeWindowPanel.constrain(m_MinInc,3,8,2,1,GridBagConstraints.CENTER);

        m_MinLabel = new FixedLabel(2,"-");
        m_ExchangeWindowPanel.constrain(m_MinLabel,3,9,2,1,GridBagConstraints.CENTER);

        m_MinDec = new Button("-");
        m_MinDec.addActionListener(this);
        m_ExchangeWindowPanel.constrain(m_MinDec,3,10,2,1,GridBagConstraints.CENTER);

    // Ok and Cancel Buttons

        m_NumExchangeOK = new Button(m_EOApp.getLabels().getObjectLabel("benew_ok"));
        m_NumExchangeOK.addActionListener(this);
        m_ExchangeWindowPanel.constrain(m_NumExchangeOK,1,11,2,1,GridBagConstraints.CENTER);
        
        m_NumExchangeCancel = new Button(m_EOApp.getLabels().getObjectLabel("benew_cancel"));
        m_NumExchangeCancel.addActionListener(this);
        m_ExchangeWindowPanel.constrain(m_NumExchangeCancel,3,11,2,1,GridBagConstraints.CENTER);

        add(m_ExchangeWindowPanel);
        pack();
        show();

        m_NodeList.setSize(m_NodeList.getPreferredSize());
        setSize(getPreferredSize());
        }

    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (m_ListIndex != -1) 
                {
                if (theSource == m_MaxInc)
                    {
                    if (m_ExchangeIndex.getMax() < m_rangeMax)
                        {
                        m_ExchangeIndex.setMax(m_ExchangeIndex.getMax() + 1);
                        m_NodeList.replaceItem(BuildExchangeListEntry(m_NodeIndex),m_ListIndex);
                        m_NodeList.select(m_ListIndex);
                        m_MaxLabel.setText(""+m_ExchangeIndex.getMax());
                        }
                    }
                if (theSource == m_MaxDec)
                    {
                    if (m_ExchangeIndex.getMax() > 1)
                        {
                        m_ExchangeIndex.setMax(m_ExchangeIndex.getMax() - 1);

                        if (m_ExchangeIndex.getMin() > m_ExchangeIndex.getMax())
                            {
                            m_ExchangeIndex.setMin(m_ExchangeIndex.getMax());
                            }

                        m_NodeList.replaceItem(BuildExchangeListEntry(m_NodeIndex),m_ListIndex);
                        m_NodeList.select(m_ListIndex);
                        m_MaxLabel.setText(""+m_ExchangeIndex.getMax());
                        m_MinLabel.setText(""+m_ExchangeIndex.getMin());
                        }
                    }

                if (theSource == m_MinInc)
                    {
                    if (m_ExchangeIndex.getMin() < m_ExchangeIndex.getMax())
                        {
                        m_ExchangeIndex.setMin(m_ExchangeIndex.getMin() + 1);
                        m_NodeList.replaceItem(BuildExchangeListEntry(m_NodeIndex),m_ListIndex);
                        m_NodeList.select(m_ListIndex);
                        m_MinLabel.setText(""+m_ExchangeIndex.getMin());
                        }
                    }
                if (theSource == m_MinDec)
                    {
                    if (m_ExchangeIndex.getMin() > 1)
                        {
                        m_ExchangeIndex.setMin(m_ExchangeIndex.getMin() - 1);
                        m_NodeList.replaceItem(BuildExchangeListEntry(m_NodeIndex),m_ListIndex);
                        m_NodeList.select(m_ListIndex);
                        m_MinLabel.setText(""+m_ExchangeIndex.getMin());
                        }
                    }
                }
 
            if (theSource == m_NumExchangeOK)
                {
                // Handle OK
                Enumeration enm = m_nodeExchanges.keys();
                while (enm.hasMoreElements())
                    {
                    Integer node = (Integer)enm.nextElement();
                    BENodeExchange bens = (BENodeExchange)m_nodeExchanges.get(node); 
                    BENode ben = (BENode)m_activeNetwork.getNodeList().get(node);
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
                m_nodeExchanges.clear();
                m_FNAWApp.setEditMode(false);
                removeLabels();
                dispose();
                }
            if (theSource == m_NumExchangeCancel)
                {
                // Handle Cancel
                m_nodeExchanges.clear();
                m_FNAWApp.setEditMode(false);
                removeLabels();
                dispose();
                }
            }

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

         // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("benew_help")))
                {
                m_EOApp.helpWindow("ehlp_benew");
                }
            }
        }

    public String[] BuildExchangeListEntry(BENode Ntemp)
        {
        BENodeExchange ne = (BENodeExchange)m_nodeExchanges.get(new Integer(Ntemp.getID()));
        String[] str = new String[4];

        str[0] = Ntemp.getLabel();
        str[1] = new String(""+Ntemp.getID());
        str[2] = new String(""+ne.getMax());
        str[3] = new String(""+ne.getMin());

        return str;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/benew.txt");
        }
    private void initializeNodeExchanges()
        {
        Enumeration enm = m_activeNetwork.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode node = (BENode)enm.nextElement();
            Object obj = node.getExptData("BENodeExchange");
            if (obj instanceof BENodeSubnetwork)
                {
                BENodeExchange bens = new BENodeExchange(node);
                m_nodeExchanges.put(new Integer(node.getID()),bens);
                }
            else
                {
                BENodeExchange bens = (BENodeExchange)obj;
                m_nodeExchanges.put(new Integer(node.getID()),(BENodeExchange)bens.clone());
                }
            }
        } 

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

            if ((theSource == m_NodeList) && (theSource.getSelectedIndex() > -1))
                {
                m_ListIndex = theSource.getSelectedIndex();
                Integer n = new Integer(theSource.getSelectedSubItem(1));

                m_NodeIndex = (BENode)m_activeNetwork.getNode(n);

                m_ExchangeIndex = (BENodeExchange)m_nodeExchanges.get(n);

                m_MaxLabel.setText(""+m_ExchangeIndex.getMax());

                m_MinLabel.setText(""+m_ExchangeIndex.getMin());
 
                m_rangeMax = 0;
                Enumeration enm = m_activeNetwork.getEdgeList().elements();
                while (enm.hasMoreElements())
                    {
                    BEEdge Etemp = (BEEdge)enm.nextElement();
                    if (Etemp.getNode1() == m_NodeIndex.getID())
                        {
                        m_rangeMax++;
                        }
                    else if (Etemp.getNode2() == m_NodeIndex.getID())
                        {
                        m_rangeMax++;
                        }
                    }
                }
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/benew.txt");
        }
    }