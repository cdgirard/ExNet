package girard.sc.ce.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.awt.SortedFixedList;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENode;
import girard.sc.ce.obj.CENodeResource;
import girard.sc.ce.obj.CEResource;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
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
 * This allows a user to adjust the commodities avaialable for trading, how many
 * each node has, and how much they are worth.  Set whether each actor can see
 * the profits for the other actors.
 * <p>
 * <br> Started: 01-21-2003
 * <br> Modified: 03-18-2003
 * <p>
 * @author Dudley Girard
 */

public class CEEditResWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    CEFormatNetworkActionWindow m_FWApp;
    CENetwork m_activeNetwork;

  // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Help;

    CheckboxGroup m_profitInfoGrp = new CheckboxGroup();

    SortedFixedList m_NodeList;
    SortedFixedList m_CommoditiesList;
    FixedLabel m_NameLabel, m_LabelLabel;
    NumberTextField m_AmountField, m_UtilityField;
    Button m_CreateButton, m_EditButton, m_DeleteButton;
    Button m_UpdateButton;

    int m_NodeListIndex = -1;
    int m_CommoditiesListIndex = -1;
    CENode m_NodeIndex;
    CENodeResource m_ResourceIndex = null;

    boolean m_EditMode;

    public CEEditResWindow(ExptOverlord app1, CEFormatNetworkActionWindow app2, CENetwork app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_FWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("ceerw_title"));
        setFont(m_EOApp.getSmWinFont());
  
    // Start Setup for Menubar
       m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

    // File Menu

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("ceerw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ceerw_exit"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

     // Help Menu

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("ceerw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ceerw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
    // End Setup for Menubar.

   // Start setup for the North Panel

        GridBagPanel NorthPanel = new GridBagPanel();

        String pi = (String)m_activeNetwork.getExtraData("ProfitInfo");

        NorthPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceerw_dap")),1,1,4,1,GridBagConstraints.CENTER);
        if (pi.equals("All"))
            {
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("ceerw_yes"),m_profitInfoGrp,true),1,2,2,1,GridBagConstraints.CENTER);
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("ceerw_no"),m_profitInfoGrp,false),3,2,2,1,GridBagConstraints.CENTER);
            }
        else
            {
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("ceerw_yes"),m_profitInfoGrp,false),1,2,2,1,GridBagConstraints.CENTER);
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("ceerw_no"),m_profitInfoGrp,true),3,2,2,1,GridBagConstraints.CENTER);
            }
    // End setup for the North Panel

    // Start Setup for the Center Panel

        GridBagPanel centerPanel = new GridBagPanel();

    // List of Nodes

        Label tmpLabel = new Label(m_EOApp.getLabels().getObjectLabel("ceerw_nodes"));
        tmpLabel.setFont(m_EOApp.getMedWinFont());
        centerPanel.constrain(tmpLabel,1,1,4,1,GridBagConstraints.CENTER);

        int[] tmpIntArray = {5,5};
        m_NodeList = new SortedFixedList(6,false,2,tmpIntArray,FixedList.CENTER);
        m_NodeList.setFont(m_EOApp.getMedWinFont());

        Enumeration enm = m_activeNetwork.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CENode Ntemp = (CENode)enm.nextElement();
            m_NodeList.addItem(BuildNodeListEntry(Ntemp));
            }

        m_NodeList.addItemListener(this);
        centerPanel.constrain(m_NodeList,1,2,4,6,GridBagConstraints.CENTER); 

   // List of Commodities

        tmpLabel = new Label(m_EOApp.getLabels().getObjectLabel("ceerw_cau"));
        tmpLabel.setFont(m_EOApp.getMedWinFont());
        centerPanel.constrain(tmpLabel,5,1,6,1,GridBagConstraints.CENTER);

        int[] tmpIntArray2 = {5,7,7};
        m_CommoditiesList = new SortedFixedList(6,false,3,tmpIntArray2,FixedList.CENTER);
        m_CommoditiesList.setFont(m_EOApp.getMedWinFont());

        m_NodeList.select(0);
        m_NodeListIndex = 0;
        m_NodeIndex = (CENode)m_activeNetwork.getNode(new Integer(m_NodeList.getSelectedSubItem(1)));
        m_ResourceIndex = (CENodeResource)m_NodeIndex.getExptData("CENodeResource");
        enm = m_ResourceIndex.getInitialResources().elements();
        while (enm.hasMoreElements())
            {
            CEResource r = (CEResource)enm.nextElement();
            m_CommoditiesList.addItem(BuildCommoditiesListEntry(r));
            }

        m_CommoditiesList.addItemListener(this);
        centerPanel.constrain(m_CommoditiesList,5,2,6,6,GridBagConstraints.CENTER);

        m_CreateButton = new Button(m_EOApp.getLabels().getObjectLabel("ceerw_create"));
        m_CreateButton.addActionListener(this);
        centerPanel.constrain(m_CreateButton,5,8,2,1,GridBagConstraints.CENTER);

        m_EditButton = new Button(m_EOApp.getLabels().getObjectLabel("ceerw_edit"));
        m_EditButton.addActionListener(this);
        centerPanel.constrain(m_EditButton,7,8,2,1,GridBagConstraints.CENTER);

        m_DeleteButton = new Button(m_EOApp.getLabels().getObjectLabel("ceerw_delete"));
        m_DeleteButton.addActionListener(this);
        centerPanel.constrain(m_DeleteButton,9,8,2,1,GridBagConstraints.CENTER);
  // End Setup for Center Panel

  // Start Setup for the South Panel
        GridBagPanel southPanel = new GridBagPanel();

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceerw_name")),1,1,4,1,GridBagConstraints.EAST);
        m_NameLabel = new FixedLabel(10,"");
        southPanel.constrain(m_NameLabel,5,1,4,1,GridBagConstraints.WEST);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceerw_label")),1,2,4,1,GridBagConstraints.EAST);
        m_LabelLabel = new FixedLabel(10,"");
        southPanel.constrain(m_LabelLabel,5,2,4,1,GridBagConstraints.WEST);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceerw_amount")),1,3,4,1,GridBagConstraints.EAST);
        m_AmountField = new NumberTextField(5);
        m_AmountField.setAllowFloat(false);
        m_AmountField.setAllowNegative(false);
        southPanel.constrain(m_AmountField,5,3,4,1,GridBagConstraints.WEST);
 
        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceerw_utility")),1,4,4,1,GridBagConstraints.EAST);
        m_UtilityField = new NumberTextField(5);
        m_UtilityField.setAllowFloat(false);
        m_UtilityField.setAllowNegative(true);
        southPanel.constrain(m_UtilityField,5,4,4,1,GridBagConstraints.WEST);

        m_UpdateButton = new Button(m_EOApp.getLabels().getObjectLabel("ceerw_update"));
        m_UpdateButton.addActionListener(this);
        m_UpdateButton.setFont(m_EOApp.getMedWinFont());
        southPanel.constrain(m_UpdateButton,1,5,8,1,GridBagConstraints.CENTER);

        add("North",NorthPanel);
        add("Center",centerPanel);
        add("South",southPanel);
        pack();

        m_NodeList.setSize(m_NodeList.getPreferredSize());
        m_CommoditiesList.setSize(m_CommoditiesList.getPreferredSize());
        setSize(getPreferredSize());

        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        if (m_EditMode)
            return;

        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (theSource == m_CreateButton)
                {
                if (m_ResourceIndex.getInitialResources().size() < 4)
                    {
                    m_EditMode = true;
                    new CECreateCommodityWindow(m_EOApp,this,m_activeNetwork,m_ResourceIndex);
                    }
                else
                    {
                    new ErrorDialog(m_EOApp.getLabels().getObjectLabel("ceerw_error1"));
                    }
                }
            if (theSource == m_DeleteButton)
                {
                if (m_CommoditiesListIndex > -1)
                    {
                    m_EditMode = true;
                    CEResource r  = m_ResourceIndex.getInitialResources(m_CommoditiesList.getSelectedSubItem(0));
                    new CEDeleteCommodityWindow(m_EOApp,this,m_activeNetwork,r);
                    }
                else
                    {
                    new ErrorDialog(m_EOApp.getLabels().getObjectLabel("ceerw_error3"));
                    }
                }
            if (theSource == m_EditButton)
                {
                if (m_CommoditiesListIndex > -1)
                    {
                    m_EditMode = true;
                    CEResource r  = m_ResourceIndex.getInitialResources(m_CommoditiesList.getSelectedSubItem(0));
                    new CEEditCommodityWindow(m_EOApp,this,m_activeNetwork,m_ResourceIndex,r);
                    }
                else
                    {
                    new ErrorDialog(m_EOApp.getLabels().getObjectLabel("ceerw_error2"));
                    }
                }
            if (theSource == m_UpdateButton)
                {
                if (m_CommoditiesListIndex > -1)
                    {
                    CEResource r  = m_ResourceIndex.getInitialResources(m_CommoditiesList.getSelectedSubItem(0));
                    r.setResource(m_AmountField.getIntValue());
                    r.setValue(m_UtilityField.getIntValue());
                    m_CommoditiesList.replaceItem(BuildCommoditiesListEntry(r),m_CommoditiesListIndex);
                    m_CommoditiesList.select(m_CommoditiesListIndex);
                    }
                }
            }

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

       // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ceerw_exit")))
                {
                if (m_profitInfoGrp.getSelectedCheckbox().getLabel().equals(m_EOApp.getLabels().getObjectLabel("ceerw_yes")))
                    {
                    m_activeNetwork.setExtraData("ProfitInfo","All");
                    }
                else
                    {
                    m_activeNetwork.setExtraData("ProfitInfo","Limited");
                    }

                removeLabels();
                m_FWApp.setEditMode(false);
                dispose();
                return;
                }

         // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ceerw_help")))
                {
                m_EOApp.helpWindow("ehlp_ceerw");
                }
            }
        }
   
    public String[] BuildCommoditiesListEntry(CEResource r)
        {
        String[] str = new String[3];

        str[0] = new String(""+r.getLabel());
        str[1] = new String(""+r.getIntResource());
        str[2] = new String(""+r.getIntValue());

        return str;
        }
    public String[] BuildNodeListEntry(CENode Ntemp)
        {
        String[] str = new String[2];

        str[0] = new String(""+Ntemp.getLabel());
        str[1] = new String(""+Ntemp.getID());

        return str;
        }
    public boolean getEditMode()
        {
        return m_EditMode;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/ce/awt/ceerw.txt");
        }  

    public void itemStateChanged(ItemEvent e)
        {

        if (m_EditMode)
            return;

        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

            if ((theSource == m_CommoditiesList) && (theSource.getSelectedIndex() > -1))
                {
                m_CommoditiesListIndex = theSource.getSelectedIndex();
                CEResource r  = m_ResourceIndex.getInitialResources(m_CommoditiesList.getSelectedSubItem(0));
                m_NameLabel.setText(r.getName());
                m_LabelLabel.setText(r.getLabel());
                m_AmountField.setText(""+r.getIntResource());
                m_UtilityField.setText(""+r.getIntValue());
                }
            if ((theSource == m_NodeList) && (theSource.getSelectedIndex() > -1))
                {
                m_NodeListIndex = theSource.getSelectedIndex();
                m_NodeIndex = (CENode)m_activeNetwork.getNode(new Integer(m_NodeList.getSelectedSubItem(1)));
                m_ResourceIndex = (CENodeResource)m_NodeIndex.getExptData("CENodeResource");
                updateDisplay();
                
                }
            }
        }

    public void removeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/ce/awt/ceerw.txt");
        }

    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }

    public void updateDisplay()
        {
        String str = null;
        if (m_CommoditiesListIndex != -1)
            str = m_CommoditiesList.getSelectedSubItem(0);
        
        m_CommoditiesList.removeAll();
        m_CommoditiesListIndex = -1;

        Enumeration enm = m_ResourceIndex.getInitialResources().elements();
        while (enm.hasMoreElements())
            {
            CEResource r = (CEResource)enm.nextElement();
            m_CommoditiesList.addItem(BuildCommoditiesListEntry(r));
            }

        if (str != null)
            {
            for (int i=0;i<m_CommoditiesList.getItemCount();i++)
                {
                if (str.equals(m_CommoditiesList.getSubItem(i,0)))
                    {
                    m_CommoditiesListIndex = i;
                    m_CommoditiesList.select(i);
                    break;
                    }
                }
            }

        if (m_CommoditiesListIndex == -1)
            {
            m_NameLabel.setText("");
            m_LabelLabel.setText("");
            m_AmountField.setText("");
            m_UtilityField.setText("");
            }
        else
            {
            CEResource r  = m_ResourceIndex.getInitialResources(m_CommoditiesList.getSelectedSubItem(0));
            m_NameLabel.setText(r.getName());
            m_LabelLabel.setText(r.getLabel());
            m_AmountField.setText(""+r.getIntResource());
            m_UtilityField.setText(""+r.getIntValue());
            }
        }
    }
