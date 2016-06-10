package girard.sc.cc.awt;

/* Allows you to modify the period setup for a CCNetworkAction.

   Author: Dudley Girard
   Started: 7-24-2001
*/

import girard.sc.awt.BorderPanel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCPeriod;
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

public class CCEditPeriodWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    CCFormatNetworkActionWindow m_FNAWApp;
    CCNetwork m_activeNetwork;

    // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_Help;

  /* GUI Variables For Time Panel */
    GridBagPanel m_TimePanel;
    NumberTextField m_RoundField, m_TimeField;

 /* GUI Variables For User Panel */
    GridBagPanel m_NodeUserPanel;
    FixedList m_UserOrderList;
    int m_UserOrderListIndex;
    Checkbox m_ChangeOrder;
    
 /* GUI Variables For Bottom Panel */
    GridBagPanel m_BottomPanel;
    Button m_PeriodOK;

 /* Class Variables */
    CCPeriod m_activePeriod = null;

    public CCEditPeriodWindow(ExptOverlord app1, CCFormatNetworkActionWindow app2, CCNetwork app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_FNAWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("apw_title"));
        setFont(m_EOApp.getMedWinFont());

        m_activePeriod = m_activeNetwork.getPeriod();
           
    // Start Setup for Menubar
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

     // Help Menu
        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("apw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("apw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
   // End Setup for Menubar
    
// Setup the Time Panel part of the Window.

        m_TimePanel = new GridBagPanel();
        m_TimePanel.setFont(m_EOApp.getMedWinFont());

        m_TimePanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("apw_time")),1,1,3,1,GridBagConstraints.CENTER);

        m_TimeField = new NumberTextField(""+m_activePeriod.getTime());
        m_TimePanel.constrain(m_TimeField,4,1,3,1,GridBagConstraints.CENTER);

        m_TimePanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("apw_rounds")),1,2,3,1);

        m_RoundField = new NumberTextField(Integer.toString(m_activePeriod.getRounds()));
        m_TimePanel.constrain(m_RoundField,4,2,3,1,GridBagConstraints.CENTER); 

// Setup NodeUser Panel area.

        m_NodeUserPanel = new GridBagPanel();

        m_NodeUserPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("apw_unl")),1,1,4,1,GridBagConstraints.CENTER);

        int[] tmpIntArray = {10,10,10};
        m_UserOrderList = new FixedList(8,false,3,tmpIntArray,FixedList.CENTER);

        for (int i=0;i<m_activeNetwork.getNodeList().size();i++)
            {
            m_UserOrderList.addItem(BuildUserOrderListEntry(i));
            }
        m_UserOrderList.addItemListener(this);
        m_UserOrderListIndex = -1;
        m_NodeUserPanel.constrain(m_UserOrderList,1,2,4,4,GridBagConstraints.CENTER); 

        m_ChangeOrder = new Checkbox(m_EOApp.getLabels().getObjectLabel("apw_co"),false);
        m_NodeUserPanel.constrain(m_ChangeOrder,1,7,4,1,GridBagConstraints.CENTER);

// Setup Bottom Panel

        m_BottomPanel = new GridBagPanel();

        m_PeriodOK = new Button(m_EOApp.getLabels().getObjectLabel("apw_done"));
        m_PeriodOK.addActionListener(this);
        m_BottomPanel.constrain(m_PeriodOK,1,2,4,1,GridBagConstraints.CENTER);

        add("West",new BorderPanel(m_TimePanel,BorderPanel.FRAME));
        add("East",new BorderPanel(m_NodeUserPanel,BorderPanel.FRAME));
        add("South",new BorderPanel(m_BottomPanel,BorderPanel.FRAME));

        pack();
        show();

        m_UserOrderList.setSize(m_UserOrderList.getPreferredSize());
        setSize(getPreferredSize());
        }

    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
       
            if (theSource == m_PeriodOK)
                {
                // Handle Save
                updatePresentPeriod();
                m_FNAWApp.setEditMode(false);
                removeLabels();
                dispose();
                }
            }

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

         // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("apw_help")))
                {
                m_EOApp.helpWindow("ehlp_ccepw");
                }
            }
        }

    public String[] BuildUserOrderListEntry(int user)
        {
        String[] str = new String[3];

        int node = m_activePeriod.getUserNode(user);
        CCNode Ntemp = (CCNode)m_activeNetwork.getNode(node);

        str[0] = new String("User"+user);
        str[1] = new String(""+Ntemp.getID());
        str[2] = new String(""+Ntemp.getLabel());

        return str;
        }

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedList)
            {
            int OldIndex, i;
            FixedList theSource = (FixedList)e.getSource();

        // if clicked on an item in NodeList determine which item then
        // update the other lists.

            if ((theSource == m_UserOrderList) && (e.getStateChange() == ItemEvent.SELECTED))
                {
                if (m_ChangeOrder.getState())
                    {
                    OldIndex = m_UserOrderListIndex;
                    m_UserOrderListIndex = theSource.getSelectedIndex();

                    Integer node1 = new Integer(m_UserOrderList.getSubItem(OldIndex,1));
                    Integer node2 = new Integer(m_UserOrderList.getSubItem(m_UserOrderListIndex,1));

                    m_activePeriod.swapNodes(OldIndex,m_UserOrderListIndex);
                    m_UserOrderList.replaceItem(BuildUserOrderListEntry(OldIndex),OldIndex);
                    m_UserOrderList.replaceItem(BuildUserOrderListEntry(m_UserOrderListIndex),m_UserOrderListIndex);
                    // m_ChangeOrder.setState(false);   See how it works when we don't reset the change state flag.
                    m_UserOrderList.select(m_UserOrderListIndex);
                    }
                else
                    {
                    m_UserOrderListIndex = theSource.getSelectedIndex();
                    }
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/cc/awt/ccepw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/cc/awt/ccepw.txt");
        }

    public void updatePresentPeriod()
        {
        m_activePeriod.setTime(Integer.valueOf(m_TimeField.getText()).intValue());
        m_activePeriod.setRounds(Integer.valueOf(m_RoundField.getText()).intValue());
        }
    }
