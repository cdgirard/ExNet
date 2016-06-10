package girard.sc.tut.awt;

import girard.sc.awt.DescriptionDialog;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.expt.awt.ExptBuilderWindow;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.tp.io.msg.BaseTutorialPageTypesListReqMsg;
import girard.sc.tp.obj.TutorialPage;
import girard.sc.tut.obj.TutorialAction;

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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Used to format the Tutorial Action.
 * <p>
 * <br> Started: 12-01-2001
 * <br> Modified: 7-23-2002
 * <br> Modified: 7-23-2002
 * <p>
 *
 * @author Dudley Girard
 */

public class FormatTutorialActionWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    TutorialAction m_TApp;
    ExptBuilderWindow m_EBWApp;
    TutorialPage m_basePage = new TutorialPage();
    TutorialPage m_activePage = null;

    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Edit, m_Help;

    FixedList m_userList;
    int m_userListIndex = -1;
    Checkbox m_copyBox;

    SortedFixedList m_tutPageTypeList;
    int m_typePageIndex = -1;
    Vector m_availableTypes = new Vector();
    Vector m_typeDescriptions = new Vector();
    Button m_typeDescButton;

    SortedFixedList m_accessGroupList;
    Vector m_accessGroups = new Vector();
    Button m_agDescButton;

    SortedFixedList m_availPageList;
    Vector m_availablePages = new Vector();
    Vector m_listedPages = new Vector();
    Button m_availDescButton;

    FixedList m_tutorialList;
    Vector m_tutorial = new Vector();
    Button m_addButton, m_insertButton, m_removeButton;

    boolean m_editMode = false;

    public FormatTutorialActionWindow(ExptOverlord app1, ExptBuilderWindow app2,TutorialAction app3)
        {
        m_EOApp = app1;
        m_EBWApp = app2;
        m_TApp = app3;

        for (int x=0;x<m_EBWApp.getExpApp().getNumUsers();x++)
            {
            Vector tmpVec = new Vector();
            m_tutorial.addElement(tmpVec);
            }

        if (m_TApp.getAction() == null)
            {
            m_TApp.setAction(m_tutorial);
            }
        else
            {
            m_tutorial = (Vector)m_TApp.getAction();
            }

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("ftaw_title"));
        setFont(m_EOApp.getMedWinFont());

   // Start setup for menubar.
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;
    
    // File Menu Options

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("ftaw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ftaw_exit"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

        m_Edit = new Menu(m_EOApp.getLabels().getObjectLabel("ftaw_edit"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ftaw_description"));
        tmpMI.addActionListener(this);
        m_Edit.add(tmpMI);

        m_mbar.add(m_Edit);

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("ftaw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ftaw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
    // End setup for menu bar.

    // Setup North Panel
        GridBagPanel northGBPanel = new GridBagPanel();

        northGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ftaw_users")),1,1,4,1);
        m_userList = new FixedList(8,false,1,10,FixedList.CENTER);
        m_userList.setFont(m_EOApp.getSmLabelFont());
        m_userList.addItemListener(this);

        fillUserList();

        northGBPanel.constrain(m_userList,1,2,4,4);

        m_copyBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("ftaw_ct"),false);
        northGBPanel.constrain(m_copyBox,1,6,4,1);

        northGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ftaw_types")),5,1,4,1);
        m_tutPageTypeList = new SortedFixedList(8,false,1,15,FixedList.CENTER);
        m_tutPageTypeList.setFont(m_EOApp.getSmLabelFont());
        m_tutPageTypeList.addActionListener(this);

        loadAvailableTypes();

        northGBPanel.constrain(m_tutPageTypeList,5,2,4,4);

        m_typeDescButton = new Button(m_EOApp.getLabels().getObjectLabel("ftaw_vd"));
        m_typeDescButton.addActionListener(this);
        northGBPanel.constrain(m_typeDescButton,5,6,1,4,GridBagConstraints.CENTER);

  // Access groups List

        northGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ftaw_ag")),9,1,4,1);
        m_accessGroupList = new SortedFixedList(8,false,1,20,FixedList.CENTER);
        m_accessGroupList.setFont(m_EOApp.getSmLabelFont());
        m_accessGroupList.addItemListener(this);

        String[] str = new String[1];
        str[0] = "<NONE>";
        m_accessGroupList.addItem(str);
        Hashtable h = new Hashtable();
        h.put("Name","<NONE>");
        h.put("Desc","Only this user may access this file.");
        m_accessGroups.addElement(h);

        CreateAccessGroupList();

        northGBPanel.constrain(m_accessGroupList,9,2,4,4);

        m_agDescButton = new Button(m_EOApp.getLabels().getObjectLabel("ftaw_vd"));
        m_agDescButton.addActionListener(this);
        northGBPanel.constrain(m_agDescButton,9,6,1,4,GridBagConstraints.CENTER);
    // End North Panel Setup

    // Start setup for center panel.
        GridBagPanel centerGBPanel = new GridBagPanel();

        centerGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ftaw_available")),1,1,4,1);
        m_availPageList = new SortedFixedList(8,false,1,20,FixedList.CENTER);
        m_availPageList.setFont(m_EOApp.getSmLabelFont());

        centerGBPanel.constrain(m_availPageList,1,2,4,4);

        m_availDescButton = new Button(m_EOApp.getLabels().getObjectLabel("ftaw_vd"));
        m_availDescButton.addActionListener(this);
        centerGBPanel.constrain(m_availDescButton,1,6,4,1);

        m_addButton = new Button(m_EOApp.getLabels().getObjectLabel("ftaw_add"));
        m_addButton.addActionListener(this);
        centerGBPanel.constrain(m_addButton,5,3,2,1);

        m_insertButton = new Button(m_EOApp.getLabels().getObjectLabel("ftaw_insert"));
        m_insertButton.addActionListener(this);
        centerGBPanel.constrain(m_insertButton,5,4,2,1);

        m_removeButton = new Button(m_EOApp.getLabels().getObjectLabel("ftaw_remove"));
        m_removeButton.addActionListener(this);
        centerGBPanel.constrain(m_removeButton,5,5,2,1);
    
        centerGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ftaw_tutorial")),7,1,4,1);
        m_tutorialList = new FixedList(8,false,1,20,FixedList.CENTER);
        m_tutorialList.setFont(m_EOApp.getSmLabelFont());

        centerGBPanel.constrain(m_tutorialList,7,2,4,4);
    // End setup for center panel.

        add("North",northGBPanel);
        add("Center",centerGBPanel);

        pack();
        show();

        m_availPageList.setSize(m_availPageList.getPreferredSize());
        m_tutorialList.setSize(m_tutorialList.getPreferredSize());
        setSize(getPreferredSize());
        validate();
        }

    public void actionPerformed (ActionEvent e)
        {
        if (getEditMode())
            return;

        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if ((theSource == m_addButton) && (m_availPageList.getSelectedIndex() > -1) && (m_userListIndex > -1))
                {
                TutorialPage tp = (TutorialPage)((TutorialPage)m_availableTypes.elementAt(m_typePageIndex)).clone();
                Hashtable h = (Hashtable)m_listedPages.elementAt(m_availPageList.getSelectedIndex());
                tp.setFileName((String)h.get("FileName"));
                tp.setUserID(((Integer)h.get("UserID")).intValue());
                if (h.containsKey("App ID"))
                    {
                    tp.setAppID((String)h.get("App ID"));
                    tp.setAppName((String)h.get("App Name"));
                    }
                Vector tut = (Vector)m_tutorial.elementAt(m_userListIndex);
                tut.addElement(tp);
                m_tutorialList.addItem(BuildTutorialListEntry(tp));
                }
            if ((theSource == m_agDescButton) && (m_accessGroupList.getSelectedIndex() > -1))
                {
                Hashtable h = (Hashtable)m_accessGroups.elementAt(m_accessGroupList.getSelectedIndex());
                if (h.containsKey("App Desc"))
                    { 
                    String str = (String)h.get("App Desc");
                    new DescriptionDialog(str);
                    }
                }
            if ((theSource == m_availDescButton) && (m_availPageList.getSelectedIndex() > -1))
                {
                Hashtable h = (Hashtable)m_listedPages.elementAt(m_availPageList.getSelectedIndex());
                new DescriptionDialog((String)h.get("Desc"));
                }
            if ((theSource == m_insertButton) && (m_availPageList.getSelectedIndex() > -1) && (m_tutorialList.getSelectedIndex() > -1))
                {
                int index = m_tutorialList.getSelectedIndex();
                TutorialPage tp = (TutorialPage)((TutorialPage)m_availableTypes.elementAt(m_typePageIndex)).clone();
                Hashtable h = (Hashtable)m_listedPages.elementAt(m_availPageList.getSelectedIndex());
                tp.setFileName((String)h.get("FileName"));
                tp.setUserID(((Integer)h.get("UserID")).intValue());
                if (h.containsKey("App ID"))
                    {
                    tp.setAppID((String)h.get("App ID"));
                    tp.setAppName((String)h.get("App Name"));
                    }
                Vector tut = (Vector)m_tutorial.elementAt(m_userListIndex);
                tut.insertElementAt(tp,index);
                m_tutorialList.addItem(BuildTutorialListEntry(tp),index);
                }
            if ((theSource == m_removeButton) && (m_tutorialList.getSelectedIndex() > -1))
                {
                int index = m_tutorialList.getSelectedIndex();
                Vector tut = (Vector)m_tutorial.elementAt(m_userListIndex);
                tut.removeElementAt(index);
                m_tutorialList.remove(index);
                }
            if ((theSource == m_typeDescButton) && (m_tutPageTypeList.getSelectedIndex() > -1))
                {
                String str = (String)m_typeDescriptions.elementAt(m_tutPageTypeList.getSelectedIndex());
                new DescriptionDialog(str);
                }
            }

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ftaw_description")))
                {
                setEditMode(true);
                new TutEditDescriptionWindow(m_EOApp,this,m_TApp);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ftaw_exit")))
                {
                m_EBWApp.updateDisplay();
                m_EBWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ftaw_help")))
                {
                m_EOApp.helpWindow("ehlp_ftaw");
                }
            }
        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

            if ((theSource == m_tutPageTypeList) && (m_tutPageTypeList.getSelectedIndex() > -1))
                {
                m_typePageIndex = m_tutPageTypeList.getSelectedIndex();
                TutorialPage tp = (TutorialPage)m_availableTypes.elementAt(m_typePageIndex);
                
                m_availablePages = m_EOApp.loadBaseActionFileList(tp.getDB(),tp.getDBTable(),m_accessGroups);
                if (m_accessGroupList.getSelectedIndex() > -1)
                    {
                    CreateAvailPagesList(m_accessGroupList.getSelectedIndex());
                    }
                }
            }
        }

    public String[] BuildTutorialListEntry(TutorialPage tp)
        {
        String[] str = new String[1];

        str[0] = tp.getFileName();

        return str;
        }

/**
 * Requests a list of Access Groups that can be accessed by the user for granting
 * access rights to the file being saved. Does this by sending a WLAccessGroupListReqMsg.
 * 
 * @see girard.sc.wl.io.msg.WLAccessGroupListReqMsg
 */
    public void CreateAccessGroupList() 
        {
        Vector accessGroups = m_EOApp.loadAccessGroupList();
        Enumeration enm = accessGroups.elements();
        while (enm.hasMoreElements())
            {
            Hashtable h = (Hashtable)enm.nextElement();
            String[] str = new String[1];
            str[0] = (String)h.get("App Name");
            m_accessGroupList.addItem(str);
            m_accessGroups.insertElementAt(h,m_accessGroupList.last);
            }
        }
/**
 * Requests a list of BaseAction files that can be loaded by the user of the type 
 * the m_BApp variable is.  Does this by sending a SavedBaseActionFileListReqMsg.
 * 
 * @see girard.sc.expt.io.msg.SavedBaseActionFileListReqMsg
 * @see girard.sc.expt.obj.BaseAction#getDB()
 * @see girard.sc.expt.obj.BaseAction#getDBTable()
 */
    public void CreateAvailPagesList(int loc) 
        {   
        m_listedPages.removeAllElements();
        m_availPageList.removeAll();

        Hashtable h = (Hashtable)m_accessGroups.elementAt(loc);
        String uid = new String("-");
        if (h.containsKey("App ID"))
            uid = (String)h.get("App ID");

        Enumeration enm = m_availablePages.elements();
        while (enm.hasMoreElements())
            {
            Hashtable h2 = (Hashtable)enm.nextElement();

            String uid2 = new String("-");
            if (h2.containsKey("App ID"))
                uid2 = (String)h2.get("App ID");

            if (uid.equals(uid2))
                {
                String[] str = new String[1];
                str[0] = (String)h2.get("FileName");
                m_availPageList.addItem(str);

                m_listedPages.insertElementAt(h2,m_availPageList.last);
                }
            }
        }

    private void copyTutorial(int from, int to)
        {
        Vector fromTut = (Vector)m_tutorial.elementAt(from);
        Vector toTut = (Vector)m_tutorial.elementAt(to);
        toTut.removeAllElements();

        Enumeration enm = fromTut.elements();
        while (enm.hasMoreElements())
            {
            TutorialPage tp = (TutorialPage)enm.nextElement();

            toTut.addElement(tp.clone());
            }
        }

    public void fillTutorialList()
        {
        Vector tut = (Vector)m_tutorial.elementAt(m_userListIndex);
        Enumeration enm = tut.elements();
        while (enm.hasMoreElements())
            {
            TutorialPage tp = (TutorialPage)enm.nextElement();

            m_tutorialList.addItem(BuildTutorialListEntry(tp));
            }
        }
    public void fillUserList()
        {
        int counter = 0;
        Enumeration enm = m_tutorial.elements();
        while (enm.hasMoreElements())
            {
            Vector userTut = (Vector)enm.nextElement();

            String[] str = new String[1];
            str[0] = new String("User "+counter);
            m_userList.addItem(str);
            counter++;
            }
        }

    public boolean getEditMode()
        {
        return m_editMode;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/tut/awt/ftaw.txt");
        }  

    public void itemStateChanged(ItemEvent ie)
        {
        if (ie.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)ie.getSource();

            if (theSource == m_userList)
                {
                int index = theSource.getSelectedIndex();
                if (index >= 0)
                    {
                    if (m_copyBox.getState())
                        {
                        m_copyBox.setState(false);

                        if (m_userListIndex > -1)
                            {
                            copyTutorial(m_userListIndex,index);
                            }
                        }

                    m_userListIndex = index;
                    m_tutorialList.removeAll();
                    fillTutorialList();
                    }
                }
            }
        if (ie.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)ie.getSource();

            if ((theSource == m_accessGroupList) && (m_accessGroupList.getSelectedIndex() >= 0))
                {
                int index = m_accessGroupList.getSelectedIndex();
                CreateAvailPagesList(index);
                }
            }
        }

    public void loadAvailableTypes()
        {
        BaseTutorialPageTypesListReqMsg tmp = new BaseTutorialPageTypesListReqMsg(null);
        ExptMessage em = m_EOApp.sendExptMessage(tmp);

        if (em instanceof BaseTutorialPageTypesListReqMsg)
            {
            Object[] in_args = em.getArgs();
            Vector availableTypes = (Vector)in_args[0];
            Vector typeDescriptions = (Vector)in_args[1];

            Enumeration enm = availableTypes.elements();
            Enumeration enum2 = typeDescriptions.elements();
            while (enm.hasMoreElements())
                {
                String[] str = new String[1];
                TutorialPage tp = (TutorialPage)enm.nextElement();
                Object obj = enum2.nextElement();

                str[0] = tp.getName();
                m_tutPageTypeList.addItem(str);

                m_availableTypes.insertElementAt(tp,m_tutPageTypeList.last);
                m_typeDescriptions.insertElementAt(obj,m_tutPageTypeList.last);
                }
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/tut/awt/ftaw.txt");
        }

    public void setEditMode(boolean value)
        {
        m_editMode = value;
        }
    }
