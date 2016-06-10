package girard.sc.expt.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.event.ActionEvent;

public class ClientStartWindow extends ClientWindow
    {
    MenuBar m_menuBar;
    Menu m_fileMenu;

    Label m_exptNameLabel;

    public ClientStartWindow(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3)
        {
        super(app1,app2,app3);

        // m_SML.addActionListener(this);

        initializeLabels();

        getContentPane().setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("csw_title"));
        getContentPane().setFont(m_EOApp.getMedWinFont());

    // Set up menu bar

        MenuItem tmpItem;

        m_menuBar = new MenuBar();

        m_menuBar.setFont(m_EOApp.getSmWinFont());

        m_fileMenu = new Menu(m_EOApp.getLabels().getObjectLabel("csw_file"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("csw_exit"));
        tmpItem.addActionListener(this);
        m_fileMenu.add(tmpItem);

        m_menuBar.add(m_fileMenu);

    // North Panel
        Panel northPanel = new Panel(new GridLayout(1,1));
 
        northPanel.add(new Label(" "));
    // End North Panel

    // West Panel
        Panel westPanel = new Panel(new GridLayout(1,1));
 
        westPanel.add(new Label(" "));
    // End West Panel

    // Center Panel

        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("csw_experiment")),1,1,2,1);
        centerPanel.constrain(new Label(m_ExpApp.getExptName()),3,1,2,1);

        centerPanel.constrain(new Label(""),1,2,4,1);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("csw_pwfets")),1,3,4,1);
   // End Center Panel

   // East Panel
        Panel eastPanel = new Panel(new GridLayout(1,1));
 
        eastPanel.add(new Label(" "));
    // End East Panel

    // South Panel
        Panel southPanel = new Panel(new GridLayout(1,1));
 
        southPanel.add(new Label(" "));
    // End South Panel

        setMenuBar(m_menuBar);
        getContentPane().add("North",northPanel);
        getContentPane().add("West",westPanel);
        getContentPane().add("Center",centerPanel);
        getContentPane().add("East",eastPanel);
        getContentPane().add("South",southPanel);
        
        pack();

        setSize(getPreferredSize());

        show();
        }
 
    public void actionPerformed(ActionEvent e) 
        {

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

            if ((theSource.getLabel().equals("Exit")) && (!m_ExpApp.getReadyToStart()))
                {
                m_ExpApp.setExptRunning(false);
             //   m_EOApp.setEditMode(false);
             //   Panel p = m_EOApp.getWebpageBasePanel();
             //   if (p instanceof JoinExperimentPage)
             //       {
             //       JoinExperimentPage jep = (JoinExperimentPage)p;
            //        jep.restartPage();
            //        }
                setWatcher(false);
                }
            }

        if (e.getSource() instanceof ExptMessage)
            {
            synchronized(m_SML)
                {
                ExptMessage em = (ExptMessage)e.getSource();

                if (em instanceof ExptErrorMsg)
                    {
                    String str = (String)em.getArgs()[0];
                    new ErrorDialog(str);
                    }
                else
                    {
                    em.getClientResponse(this);
                    }
                }
            }
        }

    public void cleanUpWindow()
        {
        removeLabels();
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/awt/csw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/awt/csw.txt");
        }
    }
