package girard.sc.expt.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.EndExptReqMsg;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.GetRegObserversMsg;
import girard.sc.expt.io.msg.GetRegUsersMsg;
import girard.sc.expt.io.msg.ResetExptStartReqMsg;
import girard.sc.expt.io.msg.StartExptReqMsg;
import girard.sc.expt.obj.Experiment;
import girard.sc.expt.obj.ExptUserData;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

public class ExperimenterStartWindow extends ExperimenterWindow implements ActionListener
    {
    MenuBar m_menuBar;
    Menu m_fileMenu;
    Menu m_experimentMenu;
    Menu m_helpMenu;

    Label m_exptNameLabel;

    FixedList m_observerList, m_userList;

    int m_regListenIndex = -1;
    int m_obvListenIndex = -1;

    public ExperimenterStartWindow(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        super(app1,app2,app3);

        // m_SML.addActionListener(this);
        m_ExpApp.initializeReady();

        initializeLabels();

        getContentPane().setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("esw_title"));
        getContentPane().setFont(m_EOApp.getSmWinFont());

    // Set up menu bar

        MenuItem tmpItem;

        m_menuBar = new MenuBar();

        m_menuBar.setFont(m_EOApp.getSmWinFont());

      // Setup File Menu
        m_fileMenu = new Menu(m_EOApp.getLabels().getObjectLabel("esw_file"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("esw_exit"));
        tmpItem.addActionListener(this);
        m_fileMenu.add(tmpItem);

      // Setup Experiment Menu
        m_experimentMenu = new Menu(m_EOApp.getLabels().getObjectLabel("esw_experiment"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("esw_start"));
        tmpItem.addActionListener(this);
        m_experimentMenu.add(tmpItem);

      // Setup Help Menu
        m_helpMenu = new Menu(m_EOApp.getLabels().getObjectLabel("esw_help"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("esw_help"));
        tmpItem.addActionListener(this);
        m_helpMenu.add(tmpItem);

        m_menuBar.add(m_fileMenu);
        m_menuBar.add(m_experimentMenu);
        m_menuBar.add(m_helpMenu);
    // End Setup of Menubar.

    // North Panel

       GridBagPanel m_northPanel = new GridBagPanel();

       m_exptNameLabel = new FixedLabel(25,m_ExpApp.getExptName());
       m_northPanel.constrain(new Label("Experiment:"),1,1,2,1);
       m_northPanel.constrain(m_exptNameLabel,3,1,10,1);

    // Setup Central Panel

        GridBagPanel m_centerPanel = new GridBagPanel();

        m_observerList = new FixedList(3,false,1,16,FixedList.CENTER);
        m_centerPanel.constrain(new Label("Observers"),1,4,4,1);
        m_centerPanel.constrain(m_observerList,1,5,4,3);

        m_centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("esw_ur")),1,9,4,1);
        m_userList = new FixedList(8,false,2,10,FixedList.CENTER);
        initializeUserList();
        m_centerPanel.constrain(m_userList,1,10,4,6);

        setMenuBar(m_menuBar);
        getContentPane().add("North",m_northPanel);
        getContentPane().add("Center",m_centerPanel);
        
        pack();
        show();

        GetRegUsersMsg tmpMsg = new GetRegUsersMsg(null);
        m_regListenIndex = m_SML.addListenRequest(tmpMsg,5000);

        GetRegObserversMsg tmpMsg2 = new GetRegObserversMsg(null);
        m_obvListenIndex = m_SML.addListenRequest(tmpMsg2,5000);
        }
 
    public void actionPerformed(ActionEvent e) 
        {
        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("esw_start")))
                {
                for (int x=0;x<m_ExpApp.getNumUsers();x++)
                    {
                    if (!m_ExpApp.getRegistered(x))
                        {
                        return;
                        }
                    }
                
                m_ExpApp.setReadyToStart(true);
                m_ExpApp.initializeReady();

                StartExptReqMsg tmp = new StartExptReqMsg(null);
                m_SML.sendMessage(tmp);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("esw_exit")))
                {
                m_SML.removeListenRequest(m_regListenIndex);
                m_ExpApp.stopActiveSimActors();
              // Sending out End Experiment Message.
                EndExptReqMsg tmp = new EndExptReqMsg(null);
                m_SML.sendMessage(tmp);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("esw_help")))
                {
                m_EOApp.helpWindow("ehlp_esw");
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
                    em.getExperimenterResponse(this);
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
        m_EOApp.initializeLabels("girard/sc/expt/awt/esw.txt");
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
        m_EOApp.removeLabels("girard/sc/expt/awt/esw.txt");
        }

    public void updateDisplay()
        {
        m_observerList.removeAll();
        Enumeration enm = m_ExpApp.getObservers().elements();
        while (enm.hasMoreElements())
            {
            ExptUserData wlud = (ExptUserData)enm.nextElement();
            String[] str = new String[1];
            str[0] = new String(wlud.getFirstName()+" "+wlud.getLastName());
            m_observerList.addItem(str);
            }

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
                if ((!m_ExpApp.getRegistered(x)) && (m_ExpApp.getReadyToStart()))
                    {
                    ResetExptStartReqMsg tmp = new ResetExptStartReqMsg(null);
                    m_EOApp.initializeWLMessage(tmp);
                    m_SML.sendMessage(tmp);
                    m_ExpApp.setReadyToStart(false);
                    m_ExpApp.initializeReady();
                    }

                m_userList.replaceItem(str,x);
                }
            }
        if (m_ExpApp.getExtraData("AutoStart") != null)
            {
            String str = (String)m_ExpApp.getExtraData("AutoStart");
            if (str.equals("true"))
                {
                boolean flag = true;
                for (int x=0;x<m_ExpApp.getNumUsers();x++)
                    {
                    if (!m_ExpApp.getRegistered(x))
                        {
                        flag = false;
                        break;
                        }
                    }
                if (flag)
                    {
                    m_ExpApp.setReadyToStart(true);
                    m_ExpApp.initializeReady();

                    StartExptReqMsg tmp = new StartExptReqMsg(null);
                    m_SML.sendMessage(tmp);
                    }
                }
            }        
        }
    }