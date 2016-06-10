package girard.sc.be.awt;

import girard.sc.awt.BorderPanel;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.be.io.msg.BEEndRoundMsg;
import girard.sc.be.io.msg.BEStopNetworkActionReqMsg;
import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BENetwork;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.GetRegObserversMsg;
import girard.sc.expt.io.msg.GetRegUsersMsg;
import girard.sc.expt.obj.Experiment;
import girard.sc.expt.obj.ExptUserData;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

public class BENetworkActionExperimenterWindow extends ExperimenterWindow
    {
    protected BENetwork m_network;

    MenuBar m_menuBar;
    Menu m_fileMenu;

    FixedList m_subjectList = null;
    Button m_subjectDetailsButton;
    FixedList m_observerList = null;
    Button m_observerDetailsButton;
    FixedList m_edgeList;
    Button m_edgeDetailsButton;
    int m_edgeIndex = -1;

    FixedLabel m_exptNameLabel;
    FixedLabel m_activeActionLabel;
    FixedLabel m_periodLabel;
    FixedLabel m_roundLabel;
    FixedLabel m_timeLabel;

    BEExperimenterDisplayCanvas m_displayArea;
    BEExperimenterMiniDisplayCanvas m_miniDisplayArea;
    Button m_zoomInButton;
    Button m_zoomOutButton;
    int m_zoomLevel = 4;  /* Min of 0 Max of 4, higher value more area shown */

    Vector m_subWindows = new Vector();

    boolean m_pauseFlag = false;
    boolean m_paused = false;
    Vector m_heldMessages = new Vector();

    int m_regListenIndex = -1;
    int m_obvListenIndex = -1;
    long m_startTime;

    public BENetworkActionExperimenterWindow(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        super(app1,app2,app3);
        
        m_network = (BENetwork)m_ExpApp.getActiveAction().getAction();

        initializeLabels();
        m_startTime = Calendar.getInstance().getTime().getTime();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("benaew_title"));
        setSize(m_EOApp.getWidth(),m_EOApp.getHeight());
    
        m_displayArea = new BEExperimenterDisplayCanvas(m_network);
        m_miniDisplayArea = new BEExperimenterMiniDisplayCanvas(m_network,m_displayArea);

    // Set up menu bar

        MenuItem tmpItem;

        m_menuBar = new MenuBar();

        m_menuBar.setFont(m_EOApp.getSmWinFont());

        m_fileMenu = new Menu(m_EOApp.getLabels().getObjectLabel("benaew_file"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("benaew_stop"));
        tmpItem.addActionListener(this);
        m_fileMenu.add(tmpItem);

        m_fileMenu.addSeparator();

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("benaew_pause"));
        tmpItem.addActionListener(this);
        m_fileMenu.add(tmpItem);

        m_menuBar.add(m_fileMenu);

    // North Panel

        GridBagPanel northPanel = new GridBagPanel();

        m_timeLabel = new FixedLabel(3,"-");
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benaew_timer")),1,1,2,1);
        northPanel.constrain(m_timeLabel,3,1,2,1);

        m_roundLabel = new FixedLabel(3,"-");
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benaew_round")),1,2,2,1);
        northPanel.constrain(m_roundLabel,3,2,2,1);

        m_periodLabel = new FixedLabel(3,"-");
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benaew_period")),1,3,2,1);
        northPanel.constrain(m_periodLabel,3,3,2,1);

        m_exptNameLabel = new FixedLabel(25,m_ExpApp.getExptName());
        northPanel.constrain(new Label("Experiment:"),5,1,2,1);
        northPanel.constrain(m_exptNameLabel,7,1,4,1);

        m_activeActionLabel = new FixedLabel(25,""+m_ExpApp.getActionIndex());
        northPanel.constrain(new Label("Active Action:"),5,2,2,1);
        northPanel.constrain(m_activeActionLabel,7,2,4,1);

        m_zoomInButton = new Button(m_EOApp.getLabels().getObjectLabel("benaew_zi"));
        m_zoomInButton.addActionListener(this);
        northPanel.constrain(m_zoomInButton,11,1,4,1);

        m_zoomOutButton = new Button(m_EOApp.getLabels().getObjectLabel("benaew_zo"));
        m_zoomOutButton.addActionListener(this);
        northPanel.constrain(m_zoomOutButton,11,2,4,1);

       Panel tmpPanel = new Panel(new GridLayout(1,1));
       tmpPanel.add(m_miniDisplayArea);
       northPanel.constrain(new BorderPanel(tmpPanel,BorderPanel.FRAME),15,1,4,4,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
 
    // End Setup North Panel

    // Start Setup West Panel

        GridBagPanel westPanel = new GridBagPanel();

        westPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benaew_ei")),1,1,4,1);
        m_edgeList = new FixedList(3,false,2,8);
        initializeEdgeList();
        westPanel.constrain(m_edgeList,1,2,4,4);

        m_edgeDetailsButton = new Button(m_EOApp.getLabels().getObjectLabel("benaew_details"));
        m_edgeDetailsButton.addActionListener(this);
        westPanel.constrain(m_edgeDetailsButton,1,6,4,1);

        westPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benaew_observers")),1,7,4,1);
        m_observerList = new FixedList(3,false,1,16);
        initializeObserverList();
        westPanel.constrain(m_observerList,1,8,4,3);

        m_observerDetailsButton = new Button(m_EOApp.getLabels().getObjectLabel("benaew_details"));
        m_observerDetailsButton.addActionListener(this);
        westPanel.constrain(m_observerDetailsButton,1,11,4,1);

        westPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("benaew_srs")),1,12,4,1);
        int[] tmpHold = {7, 3, 3};
        m_subjectList = new FixedList(4,false,3,tmpHold);
        initializeSubjectList();
        westPanel.constrain(m_subjectList,1,13,4,4);

        m_subjectDetailsButton = new Button(m_EOApp.getLabels().getObjectLabel("benaew_details"));
        m_subjectDetailsButton.addActionListener(this);
        westPanel.constrain(m_subjectDetailsButton,1,17,4,1);
        
    // End Setup for West Panel


    // Setup Center Panel
        Panel centerPanel = new Panel(new GridLayout(1,1));

        centerPanel.add(m_displayArea);

        setMenuBar(m_menuBar);
        getContentPane().add("North",northPanel);
        getContentPane().add("West",westPanel);
        getContentPane().add("Center",new BorderPanel(centerPanel,BorderPanel.FRAME));

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

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("benaew_stop")))
                {
                m_ExpApp.setExptStopping(true);
                m_network.setExtraData("RoundRunning",new Boolean(false));
                m_ExpApp.initializeReady();
                BEStopNetworkActionReqMsg tmp = new BEStopNetworkActionReqMsg(null);
                m_SML.sendMessage(tmp);
                return;
                }

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("benaew_pause")))
                {
                m_pauseFlag = true;
                return;
                }
            }

        if (e.getSource() instanceof ExptMessage)
            {
            synchronized(m_SML)  // Make sure we deal with only one message at a time.
                {
                ExptMessage em = (ExptMessage)e.getSource();

                if ((m_pauseFlag) && (em instanceof BEEndRoundMsg))
                    {
                    m_heldMessages.addElement(em);
                    if (!m_paused)
                        {
                        BEPauseResumeWindow prw = new BEPauseResumeWindow(this);
                        addSubWindow(prw);
                        m_paused = true;
                        }
                    }
                else if (em instanceof ExptErrorMsg)
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

        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (theSource == m_edgeDetailsButton)
                {
                }
            if (theSource == m_observerDetailsButton)
                {
                }
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

    public void addSubWindow(Window w)
        {
        m_subWindows.addElement(w);
        }

    public void cleanUpWindow()
        {
        Enumeration enm = m_subWindows.elements();
        while (enm.hasMoreElements())
            {
            Frame f = (Frame)enm.nextElement();
            f.dispose();
            }

        m_subWindows.removeAllElements();  // Remove any references to the subwindows.

        removeLabels();
        }

    public Vector getHeldMessages()
        {
        return m_heldMessages;
        }
    public BENetwork getNetwork()
        {
        return m_network;
        }
    public long getPresentTime()
        {
        long pt = Calendar.getInstance().getTime().getTime() - m_startTime;
        return pt/1000;
        }

    public void initializeEdgeList()
        {
        Enumeration enm = m_network.getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            BEEdge bee = (BEEdge)enm.nextElement();
            String[] str = new String[2];
            str[0] = bee.toString();
            str[1] = new String("Info Level");// bee.getInfoLevel().toString();
            m_edgeList.addItem(str);
            }
        }
    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/benaew.txt");
        }
    public void initializeObserverList()
        {
        for (int x=0;x<m_ExpApp.getNumObservers();x++)
            {
            String[] str = new String[1];
            str[0] = new String(m_ExpApp.getObserver(x).getLastName());
            m_observerList.addItem(str);
            }
        }
    public void initializeSubjectList()
        {
        for (int x=0;x<m_ExpApp.getNumUsers();x++)
            {
            String[] str = new String[3];
            if (m_ExpApp.getHumanUser(x))
                str[0] = new String("User"+x);
            else
                str[0] = new String("Comp"+x);

            if (m_ExpApp.getRegistered(x))
                str[1] = new String("Y");
            else
                str[1] = new String("N");

            if (m_ExpApp.getReady(x))
                str[2] = new String("Y");
            else
                str[2] = new String("N");

            m_subjectList.addItem(str);
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/benaew.txt");
        }
    public void removeSubWindow(Window w)
        {
        m_subWindows.removeElement(w);
        w.dispose();
        }

    public void repaint()
        {
        m_displayArea.repaint();
        }    

    public void savePayResults()
        {
        double[] payRate = (double[])m_network.getExtraData("Pay");
        double[] payAmt = new double[m_network.getNumNodes()];
        double[] pen = (double[])m_network.getExtraData("PntEarnedNetwork");

        for (int x=0;x<m_network.getNumNodes();x++)
            {
            payAmt[x] = payRate[x]*pen[x];
            }

        savePayResults(payAmt);
        }

    public void setPaused(boolean value)
        {
        m_paused = value;
        }
    public void setPauseFlag(boolean value)
        {
        m_pauseFlag = value;
        }
    public void setPeriodLabel(int value)
        {
        m_periodLabel.setText(""+value);
        }
    public void setRoundLabel(int value)
        {
        m_roundLabel.setText(""+value);
        }
    public void setStartTime()
        {
        m_startTime = Calendar.getInstance().getTime().getTime();
        }
    public void setTimeLabel(int value)
        {
        m_timeLabel.setText(""+value);
        }

    public void updateDisplay()
        {
        if ((m_observerList == null) || (m_subjectList == null))
            return;
        m_observerList.removeAll();
        Enumeration enm = m_ExpApp.getObservers().elements();
        while (enm.hasMoreElements())
            {
            ExptUserData wlud = (ExptUserData)enm.nextElement();
            String[] str = new String[1];
            str[0] = new String(wlud.getFirstName()+" "+wlud.getLastName());
            m_observerList.addItem(str);
            }
        updateUserList();
        }
    public void updateUserList()
        {
        for (int x=0;x<m_ExpApp.getNumUsers();x++)
            {
            boolean reg = false;
            boolean rdy = false;

            if (m_subjectList.getSubItem(x,1) == "Y")
                reg = true;
            if (m_subjectList.getSubItem(x,2) == "Y")
                rdy = true;

            if ((m_ExpApp.getRegistered(x) != reg) || (m_ExpApp.getReady(x) != rdy))
                {
                String[] str = new String[3];
                if (m_ExpApp.getHumanUser(x))
                    str[0] = new String("User"+x);
                else
                    str[0] = new String("Comp"+x);

                if (m_ExpApp.getRegistered(x))
                str[1] = new String("Y");
                    else
                str[1] = new String("N");

                if (m_ExpApp.getReady(x))
                    str[2] = new String("Y");
                else
                    str[2] = new String("N");

                m_subjectList.replaceItem(str,x);
                }
            }
        }
    }