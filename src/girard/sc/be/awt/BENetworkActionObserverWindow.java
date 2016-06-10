package girard.sc.be.awt;


import girard.sc.awt.BorderPanel;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.be.obj.BENetwork;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.GetRegUsersMsg;
import girard.sc.expt.obj.ObserverExptInfo;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.event.ActionEvent;

/**
 * BENetworkActionObserverWindow: Is this window displayed to observers during
 * the running of a BENetworkAction.
 * <p>
 * <br> Started: 2000
 * <br> Modified: 05-01-2001
 * <br> Modified: 05-18-2001
 * <br> Modified: 10-31-2001
 * <br> Modified: 04-03-2003
 * <p>
 * @author Dudley Girard
 */

public class BENetworkActionObserverWindow extends ObserverWindow
    {
    BENetwork m_network;

    MenuBar m_menuBar;
    Menu m_fileMenu;

    FixedList m_subjectList = null;
    Button m_subjectDetailsButton;

    FixedLabel m_exptNameLabel;
    // FixedLabel m_activeActionLabel;
    FixedLabel m_periodLabel;
    FixedLabel m_roundLabel;
    FixedLabel m_timeLabel;

    BEObserverDisplayCanvas m_displayArea;
    BEObserverMiniDisplayCanvas m_miniDisplayArea;
    Button m_zoomInButton;
    Button m_zoomOutButton;
    int m_zoomLevel = 4;  /* Min of 0 Max of 4, higher value more area shown */

    int m_regListenIndex = -1;

    public BENetworkActionObserverWindow(ExptOverlord app1, ObserverExptInfo app2, ExptMessageListener app3)
        {
        super(app1,app2,app3);

        m_network = (BENetwork)m_ExpApp.getActiveAction();

        initializeLabels();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("benaow_title"));
        setSize(m_EOApp.getWidth(),m_EOApp.getHeight());
    
        m_displayArea = new BEObserverDisplayCanvas(m_network);
        m_miniDisplayArea = new BEObserverMiniDisplayCanvas(m_network,m_displayArea);

    // Set up menu bar
        setMenuBar(m_menuBar);

        MenuItem tmpItem;

        m_menuBar = new MenuBar();

        m_menuBar.setFont(m_EOApp.getSmWinFont());

        m_fileMenu = new Menu(m_EOApp.getLabels().getObjectLabel("benaow_file"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("benaow_exit"));
        tmpItem.addActionListener(this);
        m_fileMenu.add(tmpItem);

        m_menuBar.add(m_fileMenu);
    // End setup for the menu bar.

    // North Panel

        GridBagPanel northPanel = new GridBagPanel();

        m_exptNameLabel = new FixedLabel(25,m_ExpApp.getExptName());
        northPanel.constrain(new Label("Experiment:"),1,1,2,1);
        northPanel.constrain(m_exptNameLabel,3,1,4,1);

        // m_activeActionLabel = new FixedLabel(25,"NONE");
        // northPanel.constrain(new Label("Active Action:"),1,2,2,1);
        // northPanel.constrain(m_activeActionLabel,3,2,4,1);

        m_zoomInButton = new Button(m_EOApp.getLabels().getObjectLabel("benaow_zi"));
        m_zoomInButton.addActionListener(this);
        northPanel.constrain(m_zoomInButton,7,1,2,1);

        m_zoomOutButton = new Button(m_EOApp.getLabels().getObjectLabel("benaow_zo"));
        m_zoomOutButton.addActionListener(this);
        northPanel.constrain(m_zoomOutButton,7,2,2,1);
        
        northPanel.constrain(new Label("                               "),1,3,8,1); // Just a place holder.

        Panel tmpPanel = new Panel(new GridLayout(1,1));
        tmpPanel.add(m_miniDisplayArea);
        northPanel.constrain(new BEMiniDisplayPanel(tmpPanel,BorderPanel.FRAME),9,1,3,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
    // End Setup North Panel

    // Start Setup West Panel

        GridBagPanel westPanel = new GridBagPanel();

        m_timeLabel = new FixedLabel(3,"-");
        westPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benaow_timer")),1,1,2,1);
        westPanel.constrain(m_timeLabel,3,1,2,1);

        m_roundLabel = new FixedLabel(3,"-");
        westPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benaow_round")),1,2,2,1);
        westPanel.constrain(m_roundLabel,3,2,2,1);

        m_periodLabel = new FixedLabel(3,"-");
        westPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benaow_period")),1,3,2,1);
        westPanel.constrain(m_periodLabel,3,3,2,1);

        westPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benaow_sr")),1,10,4,1);
        m_subjectList = new FixedList(4,false,2,7);
        initializeSubjectList();
        westPanel.constrain(m_subjectList,1,11,4,4);

        m_subjectDetailsButton = new Button(m_EOApp.getLabels().getObjectLabel("benaow_details"));
        m_subjectDetailsButton.addActionListener(this);
        westPanel.constrain(m_subjectDetailsButton,1,16,4,1);
        
    // End Setup for West Panel

    // Setup Center Panel
        Panel centerPanel = new Panel(new GridLayout(1,1));

        centerPanel.add(m_displayArea);
    // End Setup for Center Panel

        
        setMenuBar(m_menuBar);
        getContentPane().add("North",northPanel);
        getContentPane().add("West",westPanel);
        getContentPane().add("Center",new BorderPanel(centerPanel,BorderPanel.FRAME));

        show();

        GetRegUsersMsg tmpMsg = new GetRegUsersMsg(null);
        m_regListenIndex = m_SML.addListenRequest(tmpMsg,5000);
        }
 
    public void actionPerformed(ActionEvent e) 
        {
        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

            if (theSource.getLabel().equals("Exit"))
                {
                m_ExpApp.setExptRunning(false);
                m_ExpApp.setJoined(false);
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
                    em.getObserverResponse(this);
                    }
                }
            }
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (theSource == m_subjectDetailsButton)
                {
                }
            if (theSource == m_zoomInButton)
                {
                if (m_zoomLevel != 0)
                    m_zoomLevel--;
                m_miniDisplayArea.zoomAdjust(m_zoomLevel);
                }
            if (theSource == m_zoomOutButton)
                {
                if (m_zoomLevel != 4)
                    m_zoomLevel++;
                m_miniDisplayArea.zoomAdjust(m_zoomLevel);
                }
            }
        }

    public void cleanUpWindow()
        {
        removeLabels();
        }

    public BENetwork getNetwork()
        {
        return m_network;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/benaow.txt");
        }
    public void initializeSubjectList()
        {
        for (int x=0;x<m_ExpApp.getNumUsers();x++)
            {
            String[] str = new String[2];
            if (m_ExpApp.getHumanUser(x))
                str[0] = new String("User"+x);
            else
                str[0] = new String("Comp"+x);
            str[1] = new String(""+m_ExpApp.getRegistered(x));

            m_subjectList.addItem(str);
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/benaow.txt");
        }

    public void repaint()
        {
        m_displayArea.repaint();
        }

   // public void setActiveActionLabel(int value)
   //     {
   //     m_activeActionLabel.setText(""+value);
  //      }
    public void setPeriodLabel(int value)
        {
        m_periodLabel.setText(""+value);
        }
    public void setRoundLabel(int value)
        {
        m_roundLabel.setText(""+value);
        }
    public void setTimeLabel(int value)
        {
        m_timeLabel.setText(""+value);
        }

    public void updateDisplay()
        {
        if (m_subjectList == null)
            return;
        updateUserList();
        }
    public void updateUserList()
        {
        for (int x=0;x<m_ExpApp.getNumUsers();x++)
            {
            if (m_ExpApp.getRegistered(x) != Boolean.valueOf(m_subjectList.getSubItem(x,1)).booleanValue())
                {
                String[] str = new String[2];
                if (m_ExpApp.getHumanUser(x))
                    str[0] = new String("User"+x);
                else
                    str[0] = new String("Comp"+x);
                str[1] = new String(""+m_ExpApp.getRegistered(x));

                m_subjectList.replaceItem(str,x);
                }
            }
        }
    }
