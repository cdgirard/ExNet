package girard.sc.tut.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.Experiment;
import girard.sc.expt.obj.ExptUserData;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.tut.obj.TutorialAction;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class TutorialActionExperimenterWindow extends ExperimenterWindow
    {
    protected Vector m_tutorialPages;
    protected Hashtable m_currentPage;

    MenuBar m_menuBar;
    Menu m_fileMenu;

    FixedList m_subjectList;
    FixedList m_observerList;

    long m_startTime;

    public TutorialActionExperimenterWindow(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        super(app1,app2,app3);

        m_tutorialPages = (Vector)m_ExpApp.getActiveAction().getAction();
        m_currentPage = ((TutorialAction)m_ExpApp.getActiveAction()).getCurrentPage();

        initializeLabels();
        m_startTime = Calendar.getInstance().getTime().getTime();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("taew_title"));
        getContentPane().setFont(m_EOApp.getMedWinFont());

  // Set up menu bar

        MenuItem tmpItem;

        m_menuBar = new MenuBar();

        m_menuBar.setFont(m_EOApp.getSmWinFont());
 
        m_fileMenu = new Menu(m_EOApp.getLabels().getObjectLabel("taew_file"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("taew_stop"));
        tmpItem.addActionListener(this);
        m_fileMenu.add(tmpItem);

        m_menuBar.add(m_fileMenu);

  // Setup for North Panel.
        GridBagPanel northPanel = new GridBagPanel();

        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("taew_observers")),1,2,4,1,GridBagConstraints.CENTER);

        m_observerList = new FixedList(3,false,1,16);
        m_observerList.setFont(m_EOApp.getMedWinFont());
        northPanel.constrain(m_observerList,1,3,4,4,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL);
  // End Setup for North Panel.

  // Setup Center Panel.
        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("taew_urp")),1,2,4,1,GridBagConstraints.CENTER);

        int[] tmpC = {7, 7, 5, 5};
        m_subjectList = new FixedList(4,false,4,tmpC);
        m_subjectList.setFont(m_EOApp.getMedWinFont());
        initializeSubjectList();
        centerPanel.constrain(m_subjectList,1,3,4,4,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL);
  // End Setup for Center Panel.

        setMenuBar(m_menuBar);
        getContentPane().add("North",northPanel);
        getContentPane().add("Center",centerPanel);

        pack();
        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("taew_stop")))
                {
                m_ExpApp.getActiveAction().stopAction(m_EOApp,m_ExpApp,m_SML);
                return;
                }
            }

        if (e.getSource() instanceof ExptMessage)
            {
// System.err.println("Message: "+e.getSource());
            synchronized(m_SML)  // Make sure we deal with only one message at a time.
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

    public int getPageIndex(int user)
        {
        int index = -1;

        String str = m_subjectList.getSubItem(user,2);

        try { index = Integer.valueOf(str).intValue(); }
        catch(NumberFormatException nfe) { ; }

        return index;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/tut/awt/taew.txt");
        }  
    public void initializeSubjectList()
        {
        for (int x=0;x<m_ExpApp.getNumUsers();x++)
            {
            String[] str = new String[4];
            if (m_ExpApp.getHumanUser(x))
                str[0] = new String("User"+x);
            else
                str[0] = new String("Comp"+x);
            str[1] = new String(""+m_ExpApp.getRegistered(x));
            str[2] = new String("0");
            Vector v = (Vector)m_tutorialPages.elementAt(x);
            str[3] = new String(""+v.size());

            m_subjectList.addItem(str);
            m_currentPage.put(new Integer(x),new Integer(0));
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/tut/awt/taew.txt");
        }

    public void savePayResults()
        {
        double[] payAmt = new double[m_ExpApp.getNumUsers()];

        for (int x=0;x<m_ExpApp.getNumUsers();x++)
            {
            payAmt[x] = 0;
            }

        savePayResults(payAmt);
        }

    public void updateUserPageIndex(int user, int page)
        {
        String[] str = new String[4];

        if (m_ExpApp.getHumanUser(user))
            str[0] = new String("User"+user);
        else
            str[0] = new String("Comp"+user);
        str[1] = new String(""+m_ExpApp.getRegistered(user));
        str[2] = new String(""+page);
        Vector v = (Vector)m_tutorialPages.elementAt(user);
        str[3] = new String(""+v.size());

        m_currentPage.put(new Integer(user),new Integer(page));

        m_subjectList.replaceItem(str,user);
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
        updateUserList();
        }
    public void updateUserList()
        {
        for (int x=0;x<m_ExpApp.getNumUsers();x++)
            {
            if (m_ExpApp.getRegistered(x) != Boolean.valueOf(m_subjectList.getSubItem(x,1)).booleanValue())
                {
                String[] str = new String[4];
                if (m_ExpApp.getHumanUser(x))
                    str[0] = new String("User"+x);
                else
                    str[0] = new String("Comp"+x);
                str[1] = new String(""+m_ExpApp.getRegistered(x));
                str[2] = m_subjectList.getSubItem(x,2);
                Vector v = (Vector)m_tutorialPages.elementAt(x);
                str[3] = new String(""+v.size());

                m_subjectList.replaceItem(str,x);
                }
            }
        }
    }
