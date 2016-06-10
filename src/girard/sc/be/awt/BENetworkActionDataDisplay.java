package girard.sc.be.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENetworkAction;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeExchange;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/**
 * Used to display the data from a BENetworkAction to an experimenter.
 * <p>
 * <br> Started: 9-10-2001
 * <br> Modified: 09-20-2002
 * <p>
 * @author Dudley Girard
 */

public class BENetworkActionDataDisplay extends Frame implements ActionListener
    {
    BaseDataInfo m_bdi;
    BENetworkAction m_NApp;
    ExptOverlord m_EOApp;

    MenuBar m_menuBar;
    Menu m_fileMenu, m_optionsMenu, m_helpMenu;

    boolean m_editMode = false;

    public BENetworkActionDataDisplay(ExptOverlord app1, BaseDataInfo bdi, BENetworkAction app2)
        {
        super();
        m_bdi = bdi;
        m_NApp = app2;
        m_EOApp =app1;

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("benadd_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel MainPanel = new GridBagPanel();

     // Setup Menu options
        MenuItem tmpItem;

        m_menuBar = new MenuBar();

        m_menuBar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_menuBar);

    // File Menu
        m_fileMenu = new Menu(m_EOApp.getLabels().getObjectLabel("benadd_file"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("benadd_exit"));
        tmpItem.addActionListener(this);
        m_fileMenu.add(tmpItem);

        m_menuBar.add(m_fileMenu);

   // Options Menu
        m_optionsMenu = new Menu(m_EOApp.getLabels().getObjectLabel("benadd_options"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("benadd_vod"));
        tmpItem.addActionListener(this);
        m_optionsMenu.add(tmpItem);

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("benadd_vsd"));
        tmpItem.addActionListener(this);
        m_optionsMenu.add(tmpItem);

        BENetwork ben = (BENetwork)m_NApp.getAction();
        Enumeration enm = ben.getNodeList().elements();
        BENode node = (BENode)enm.nextElement();
        BENodeExchange be = (BENodeExchange)node.getExptData("BENodeExchange");
        if (be instanceof BENodeOrSubNet)
            {
            tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("benadd_scd"));
            tmpItem.addActionListener(this);
            m_optionsMenu.add(tmpItem);
            }

        m_menuBar.add(m_optionsMenu);

    // Help Menu
        m_helpMenu = new Menu(m_EOApp.getLabels().getObjectLabel("benadd_help"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("benadd_help"));
        tmpItem.addActionListener(this);
        m_helpMenu.add(tmpItem);

        m_menuBar.add(m_helpMenu);
    // End setup for Menu options.

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benadd_name")+m_bdi.getActionDetailName()),1,1,4,1);
        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benadd_description")),1,2,4,1);
        TextArea desc = new TextArea(m_NApp.getDesc());
        desc.setEditable(false);
        MainPanel.constrain(desc,1,3,4,4);
        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benadd_rb")+m_bdi.getUserName()),1,7,4,1);
        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benadd_date")+m_bdi.getDateRun().toString()),1,8,4,1);

        add(MainPanel);
        pack();
        show();
        }

/********************************************************************************
Callback for the Ready button on the Ready Message Window
*********************************************************************************/
    public void actionPerformed(ActionEvent e)
        {
        if (m_editMode)
            return;

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("benadd_exit")))
                {
                m_EOApp.setEditMode(false);
                this.dispose();
                return;
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("benadd_scd")))
                {
                m_editMode = true;
                new BEStaticCoalitionDataWindow(m_NApp,m_EOApp,this,m_bdi);
                return;
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("benadd_vod")))
                {
                m_editMode = true;
                new BEOffersDataWindow(m_NApp,m_EOApp,this,m_bdi);
                return;
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("benadd_vsd")))
                {
                m_editMode = true;
                new BESanctionDataWindow(m_NApp,m_EOApp,this,m_bdi);
                return;
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("benadd_help")))
                {
                m_EOApp.helpWindow("ehlp_benadd");
                return;
                }
            }
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }
    
    public boolean getEditMode()
        {
        return m_editMode;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/benadd.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/benadd.txt");
        }

    public void setEditMode(boolean value)
        {
        m_editMode = value;
        }
    }
