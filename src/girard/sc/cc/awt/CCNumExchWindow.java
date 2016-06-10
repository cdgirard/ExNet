package girard.sc.cc.awt;

/* Used to adjust the minimum and maximum number of exchanges a position
   can make.

   Author: Dudley Girard
   Started: 4-12-2002
*/

import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.cc.obj.CCEdge;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeResource;
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

public class CCNumExchWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    CCFormatNetworkActionWindow m_FNAWApp;
    CCNetwork m_activeNetwork;

    GridBagPanel m_ExchangeWindowPanel = new GridBagPanel();
    SortedFixedList m_NodeList;
    Label m_MaxLabel;
    Button m_MaxInc, m_MaxDec;
    Button m_NumExchangeOK, m_NumExchangeCancel;

    int m_ListIndex = -1;
    CCNode m_NodeIndex;
    CCNodeResource m_ExchangeIndex = null;
    int m_rangeMax = 1;
    int[] m_tmpMax; 

 // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_Help;

    public CCNumExchWindow(ExptOverlord app1, CCFormatNetworkActionWindow app2, CCNetwork app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_FNAWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("ccnew_title"));
        setFont(m_EOApp.getMedWinFont());

        m_tmpMax = new int[m_activeNetwork.getNumNodes()];

        int i = 0;
        Enumeration enm = m_activeNetwork.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CCNode Ntemp = (CCNode)enm.nextElement();
            CCNodeResource ne = (CCNodeResource)Ntemp.getExptData("CCNodeResource");
            m_tmpMax[i] = ne.getMax();
            i++;
            }
    // Start Setup for Menubar
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);

    // Help Menu
        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("ccnew_help"));

        MenuItem tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccnew_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
   // End Setup for Menubar

    // List of Nodes and their Min-Max Exchange

        m_ExchangeWindowPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnew_nimm")),1,1,4,1,GridBagConstraints.CENTER);

        int[] tmpIntArray = {10,5,6};
        m_NodeList = new SortedFixedList(6,false,3,tmpIntArray,FixedList.CENTER);

        enm = m_activeNetwork.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CCNode Ntemp = (CCNode)enm.nextElement();
            m_NodeList.addItem(BuildExchangeListEntry(Ntemp));
            }

        m_NodeList.addItemListener(this);
        m_ExchangeWindowPanel.constrain(m_NodeList,1,2,4,4,GridBagConstraints.CENTER); 

    //Maximum Exchange Buttons

        m_ExchangeWindowPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnew_maxe")),1,7,4,1,GridBagConstraints.CENTER);

        m_MaxInc = new Button("+");
        m_MaxInc.addActionListener(this);
        m_ExchangeWindowPanel.constrain(m_MaxInc,1,8,4,1,GridBagConstraints.CENTER);

        m_MaxLabel = new Label("-");
        m_ExchangeWindowPanel.constrain(m_MaxLabel,1,9,4,1,GridBagConstraints.CENTER);

        m_MaxDec = new Button("-");
        m_MaxDec.addActionListener(this);
        m_ExchangeWindowPanel.constrain(m_MaxDec,1,10,4,1,GridBagConstraints.CENTER);

    // Ok and Cancel Buttons

        m_NumExchangeOK = new Button(m_EOApp.getLabels().getObjectLabel("ccnew_ok"));
        m_NumExchangeOK.addActionListener(this);
        m_ExchangeWindowPanel.constrain(m_NumExchangeOK,1,11,2,1,GridBagConstraints.CENTER);
        
        m_NumExchangeCancel = new Button(m_EOApp.getLabels().getObjectLabel("ccnew_cancel"));
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
                        m_NodeList.replaceItem(BuildExchangeListEntry(m_NodeIndex),m_ListIndex);
                        m_NodeList.select(m_ListIndex);
                        m_MaxLabel.setText(""+m_ExchangeIndex.getMax());
                        }
                    }
                }
 
            if (theSource == m_NumExchangeOK)
                {
                // Handle OK
                // ECApp.repaint();
                m_FNAWApp.setEditMode(false);
                removeLabels();
                dispose();
                }
            if (theSource == m_NumExchangeCancel)
                {
                // Handle Cancel
                int i = 0;
                Enumeration enm = m_activeNetwork.getNodeList().elements();
                while (enm.hasMoreElements())
                    {
                    CCNode Ntemp = (CCNode)enm.nextElement();
                    CCNodeResource ne = (CCNodeResource)Ntemp.getExptData("CCNodeResource");
                    ne.setMax(m_tmpMax[i]);
                    i++;
                    }

                m_FNAWApp.setEditMode(false);
                removeLabels();
                dispose();
                }
            }

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

         // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccnew_help")))
                {
                m_EOApp.helpWindow("ehlp_ccnew");
                }
            }
        }
   
    public String[] BuildExchangeListEntry(CCNode Ntemp)
        {
        CCNodeResource ne = (CCNodeResource)Ntemp.getExptData("CCNodeResource");
        String[] str = new String[3];

        str[0] = Ntemp.getLabel();
        str[1] = new String(""+Ntemp.getID());
        str[2] = new String(""+ne.getMax());

        return str;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/cc/awt/ccnew.txt");
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

                m_NodeIndex = (CCNode)m_activeNetwork.getNode(n);
// System.err.println("Node: "+n+" "+m_NodeIndex);
                m_ExchangeIndex = (CCNodeResource)m_NodeIndex.getExptData("CCNodeResource");

                m_MaxLabel.setText(""+m_ExchangeIndex.getMax());
 
                m_rangeMax = 0;
                Enumeration enm = m_activeNetwork.getEdgeList().elements();
                while (enm.hasMoreElements())
                    {
                    CCEdge Etemp = (CCEdge)enm.nextElement();
                    if (Etemp.getNode1() == m_NodeIndex.getID())
                        {
                        CCNode tmpNode = (CCNode)m_activeNetwork.getNode(Etemp.getNode2());
                        CCNodeResource tmpRes = (CCNodeResource)tmpNode.getExptData("CCNodeResource");
                        if ((tmpRes.canTradeWith(m_NodeIndex.getID())) || (m_ExchangeIndex.canTradeWith(tmpNode.getID())))
                            {
                            m_rangeMax++;
                            }
                        }
                    else if (Etemp.getNode2() == m_NodeIndex.getID())
                        {
                        CCNode tmpNode = (CCNode)m_activeNetwork.getNode(Etemp.getNode1());
                        CCNodeResource tmpRes = (CCNodeResource)tmpNode.getExptData("CCNodeResource");
                        if ((tmpRes.canTradeWith(m_NodeIndex.getID())) || (m_ExchangeIndex.canTradeWith(tmpNode.getID())))
                            {
                            m_rangeMax++;
                            }
                        }
                    }
                }
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/cc/awt/ccnew.txt");
        }
    }
