package girard.sc.cc.awt;

/* Used to display the data from a CCNetworkAction to an experimenter.

   Author: Dudley Girard
   Started: 9-13-2001
*/

import girard.sc.awt.GridBagPanel;
import girard.sc.cc.obj.CCNetworkAction;
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

public class CCNetworkActionDataDisplay extends Frame implements ActionListener
    {
    BaseDataInfo m_bdi;
    CCNetworkAction m_NApp;
    ExptOverlord m_EOApp;

    MenuBar m_menuBar;
    Menu m_fileMenu, m_optionsMenu, m_helpMenu;

    boolean m_editMode = false;

    public CCNetworkActionDataDisplay(ExptOverlord app1, BaseDataInfo bdi, CCNetworkAction app2)
        {
        super();
        m_bdi = bdi;
        m_NApp = app2;
        m_EOApp =app1;

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("ccnadd_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel MainPanel = new GridBagPanel();

     // Setup Menu options
        MenuItem tmpItem;

        m_menuBar = new MenuBar();

        m_menuBar.setFont(m_EOApp.getSmWinFont());

    // File Menu
        m_fileMenu = new Menu(m_EOApp.getLabels().getObjectLabel("ccnadd_file"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccnadd_exit"));
        tmpItem.addActionListener(this);
        m_fileMenu.add(tmpItem);

        m_menuBar.add(m_fileMenu);

   // Options Menu
        m_optionsMenu = new Menu(m_EOApp.getLabels().getObjectLabel("ccnadd_options"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccnadd_vmd"));
        tmpItem.addActionListener(this);
        m_optionsMenu.add(tmpItem);

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccnadd_vod"));
        tmpItem.addActionListener(this);
        m_optionsMenu.add(tmpItem);

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccnadd_vsd"));
        tmpItem.addActionListener(this);
        m_optionsMenu.add(tmpItem);

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccnadd_vtd"));
        tmpItem.addActionListener(this);
        m_optionsMenu.add(tmpItem);

        m_menuBar.add(m_optionsMenu);

    // Help Menu
        m_helpMenu = new Menu(m_EOApp.getLabels().getObjectLabel("ccnadd_help"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccnadd_help"));
        tmpItem.addActionListener(this);
        m_helpMenu.add(tmpItem);

        m_menuBar.add(m_helpMenu);

        setMenuBar(m_menuBar);
     // End setup for Menu options.

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnadd_name")+m_bdi.getActionDetailName()),1,1,4,1);
        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnadd_description")),1,2,4,1);
        TextArea desc = new TextArea(m_NApp.getDesc());
        desc.setEditable(false);
        MainPanel.constrain(desc,1,3,4,4);
        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnadd_rb")+m_bdi.getUserName()),1,7,4,1);
        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnadd_date")+m_bdi.getDateRun().toString()),1,8,4,1);

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

       // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccnadd_exit")))
                {
                m_EOApp.setEditMode(false);
                this.dispose();
                return;
                }
       // Options Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccnadd_vmd")))
                {
                m_editMode = true;
                new CCMessageDataWindow(m_NApp,m_EOApp,this,m_bdi);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccnadd_vod")))
                {
                m_editMode = true;
                new CCOfferDataWindow(m_NApp,m_EOApp,this,m_bdi);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccnadd_vsd")))
                {
                m_editMode = true;
                new CCSanctionDataWindow(m_NApp,m_EOApp,this,m_bdi);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccnadd_vtd")))
                {
                m_editMode = true;
                new CCTokenDataWindow(m_NApp,m_EOApp,this,m_bdi);
                }
        // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccnadd_help")))
                {
                m_EOApp.helpWindow("ehlp_ccnadd");
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
        m_EOApp.initializeLabels("girard/sc/cc/awt/ccnadd.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/cc/awt/ccnadd.txt");
        }

    public void setEditMode(boolean value)
        {
        m_editMode = value;
        }
    }
