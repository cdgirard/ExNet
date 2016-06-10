package girard.sc.expt.awt;


import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.GetExptStateReqMsg;
import girard.sc.expt.io.msg.GetRegUsersMsg;
import girard.sc.expt.obj.ObserverExptInfo;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;

/**
 * The initial window displayed for any observers that connect to an experiment.
 * Initially sends a message to check what state the experiment is in to the 
 * experimenter station.
 * <p>
 * <br> Started: 01-01-2001
 * <br> Modified: 04-24-2001
 * <br> Modified: 04-03-2003
 * <p>
 * @author Dudley Girard
 * @version ExNet 3.0 v. 3.41
 * @since JDK1.4
 */

public class ObserverStartWindow extends ObserverWindow
    {
    MenuBar m_menuBar;
    Menu m_fileMenu;

    Label m_exptNameLabel;

    FixedList m_userList;

    int m_regListenIndex = -1;

    public ObserverStartWindow(ExptOverlord app1, ObserverExptInfo app2, ExptMessageListener app3)
        {
        super(app1,app2,app3);

        // m_SML.addActionListener(this);

        initializeLabels();

        getContentPane().setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("osw_title"));
        getContentPane().setFont(m_EOApp.getSmWinFont());

    // Set up menu bar

        MenuItem tmpItem;

        m_menuBar = new MenuBar();

        m_menuBar.setFont(m_EOApp.getSmWinFont());

        m_fileMenu = new Menu(m_EOApp.getLabels().getObjectLabel("osw_file"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("osw_exit"));
        tmpItem.addActionListener(this);
        m_fileMenu.add(tmpItem);

        m_menuBar.add(m_fileMenu);

    // North Panel

       GridBagPanel m_northPanel = new GridBagPanel();

       m_northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("osw_experiment")),1,1,2,1);
       m_northPanel.constrain(new Label(m_ExpApp.getExptName()),3,1,2,1);

    // Setup Central Panel

        GridBagPanel m_centerPanel = new GridBagPanel();

        m_centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("osw_ur")),1,9,4,1);
        m_userList = new FixedList(8,false,2,7);
        initializeUserList();

        m_centerPanel.constrain(m_userList,1,10,4,6);

        m_centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("osw_pwfets")),1,1,4,1);

    // End Setup for Central Panel

        setMenuBar(m_menuBar);
        getContentPane().add("North",m_northPanel);
        getContentPane().add("Center",m_centerPanel);
        
        pack();
        show();

        GetExptStateReqMsg tmp = new GetExptStateReqMsg(null);
        m_SML.sendMessage(tmp);

        GetRegUsersMsg tmpMsg = new GetRegUsersMsg(null);
        m_regListenIndex = m_SML.addListenRequest(tmpMsg,5000);
        }
 
    public void actionPerformed(ActionEvent e) 
        {
        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

            if ((theSource.getLabel().equals("Exit")) && (!m_ExpApp.getReadyToStart()))
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
        }

    public void cleanUpWindow()
        {
        removeLabels();
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/awt/osw.txt");
        }
    public void initializeUserList()
        {
        for (int x=0;x<m_ExpApp.getNumUsers();x++)
            {
            String[] str = new String[2];
            if (m_ExpApp.getHumanUser(x))
                str[0] = new String("User"+x);
            else
                str[0] = new String("Comp"+x);
            str[1] = new String(""+m_ExpApp.getRegistered(x));

            m_userList.addItem(str);
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/awt/osw.txt");
        }

    public void updateDisplay()
        {
        for (int x=0;x<m_ExpApp.getNumUsers();x++)
            {
            if (m_ExpApp.getRegistered(x) != Boolean.valueOf(m_userList.getSubItem(x,1)).booleanValue())
                {
                String[] str = new String[2];
                if (m_ExpApp.getHumanUser(x))
                    str[0] = new String("User"+x);
                else
                    str[0] = new String("Comp"+x);
                str[1] = new String(""+m_ExpApp.getRegistered(x));

                m_userList.replaceItem(str,x);
                }
            }
        }
    }