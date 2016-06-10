package girard.sc.qa.awt;

import girard.sc.awt.DescriptionDialog;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.expt.awt.ExptBuilderWindow;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.qa.obj.Questionnaire;
import girard.sc.ques.io.msg.BaseQuestionTypesListReqMsg;
import girard.sc.ques.obj.BaseQuestion;

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
 * Used to format the Questionnaire.
 * <p>
 * <br> Started: 7-30-2002
 * <br> Modified: 10-30-2002
 * <p>
 * @author Dudley Girard
 */

public class FormatQuestionnaireWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    Questionnaire m_QApp;
    ExptBuilderWindow m_EBWApp;

// Why do we have these two variables?
  //  BaseQuestion m_basePage = new BaseQuestion();
  //  BaseQuestion m_activePage = null;

    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Edit, m_Help;

    FixedList m_userList;
    int m_userListIndex = -1;
    Checkbox m_copyBox;

    SortedFixedList m_baseQuestionTypeList;
    int m_typeQuestionIndex = -1;
    Vector m_availableTypes = new Vector();
    Vector m_typeDescriptions = new Vector();
    Button m_typeDescButton;

    SortedFixedList m_accessGroupList;
    Vector m_accessGroups = new Vector();
    Button m_agDescButton;

    SortedFixedList m_availQuestionList;
    Vector m_availQuestions = new Vector();
    Vector m_listedQuestions = new Vector();
    Button m_availDescButton;

    FixedList m_QuestionnaireList;
    Vector m_Questionnaire = new Vector();
    Vector m_transitions = new Vector();
    Button m_addButton, m_insertButton, m_removeButton, m_transitionButton;

    boolean m_editMode = false;

    public FormatQuestionnaireWindow(ExptOverlord app1, ExptBuilderWindow app2,Questionnaire app3)
        {
        m_EOApp = app1;
        m_EBWApp = app2;
        m_QApp = app3;

        m_Questionnaire = (Vector)m_QApp.getAction();
        m_transitions = (Vector)m_QApp.getTransitions();

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("fqw_title"));
        setFont(m_EOApp.getMedWinFont());

   // Start setup for menubar.
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;
    
    // File Menu Options

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("fqw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("fqw_exit"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

        m_Edit = new Menu(m_EOApp.getLabels().getObjectLabel("fqw_edit"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("fqw_description"));
        tmpMI.addActionListener(this);
        m_Edit.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("fqw_iw"));
        tmpMI.addActionListener(this);
        m_Edit.add(tmpMI);

        m_mbar.add(m_Edit);

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("fqw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("fqw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
    // End setup for menu bar.

    // North Panel
        GridBagPanel northGBPanel = new GridBagPanel();

 // User List
        northGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fqw_users")),1,1,4,1);
        m_userList = new FixedList(8,false,1,10,FixedList.CENTER);
        m_userList.setFont(m_EOApp.getSmLabelFont());
        m_userList.addItemListener(this);

        fillUserList();

        northGBPanel.constrain(m_userList,1,2,4,4);

        m_copyBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("fqw_cq"),false);
        northGBPanel.constrain(m_copyBox,1,6,4,1);

 // Types List

        northGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fqw_types")),5,1,4,1);
        m_baseQuestionTypeList = new SortedFixedList(8,false,1,15,FixedList.CENTER);
        m_baseQuestionTypeList.setFont(m_EOApp.getSmLabelFont());
        m_baseQuestionTypeList.addActionListener(this);

        loadAvailableTypes();

        northGBPanel.constrain(m_baseQuestionTypeList,5,2,4,4);

        m_typeDescButton = new Button(m_EOApp.getLabels().getObjectLabel("fqw_vd"));
        m_typeDescButton.addActionListener(this);
        northGBPanel.constrain(m_typeDescButton,5,6,1,4,GridBagConstraints.CENTER);

  // Access groups List

        northGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fqw_ag")),9,1,4,1);
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

        m_agDescButton = new Button(m_EOApp.getLabels().getObjectLabel("fqw_vd"));
        m_agDescButton.addActionListener(this);
        northGBPanel.constrain(m_agDescButton,9,6,1,4,GridBagConstraints.CENTER);
    // End North Panel Setup

    // Start setup for south panel.
        GridBagPanel southGBPanel = new GridBagPanel();

        southGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fqw_available")),1,1,4,1);
        m_availQuestionList = new SortedFixedList(8,false,1,15,FixedList.CENTER);
        m_availQuestionList.setFont(m_EOApp.getSmLabelFont());

        southGBPanel.constrain(m_availQuestionList,1,2,4,4);

        m_availDescButton = new Button(m_EOApp.getLabels().getObjectLabel("fqw_view"));
        m_availDescButton.addActionListener(this);
        southGBPanel.constrain(m_availDescButton,1,6,4,1);

        m_addButton = new Button(m_EOApp.getLabels().getObjectLabel("fqw_add"));
        m_addButton.addActionListener(this);
        southGBPanel.constrain(m_addButton,5,3,2,1);

        m_insertButton = new Button(m_EOApp.getLabels().getObjectLabel("fqw_insert"));
        m_insertButton.addActionListener(this);
        southGBPanel.constrain(m_insertButton,5,4,2,1);

        m_removeButton = new Button(m_EOApp.getLabels().getObjectLabel("fqw_remove"));
        m_removeButton.addActionListener(this);
        southGBPanel.constrain(m_removeButton,5,5,2,1);

        m_transitionButton = new Button(m_EOApp.getLabels().getObjectLabel("fqw_transition"));
        m_transitionButton.addActionListener(this);
        southGBPanel.constrain(m_transitionButton,5,6,2,1);

        southGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fqw_Questionnaire")),7,1,4,1);
        m_QuestionnaireList = new FixedList(8,false,1,15,FixedList.CENTER);
        m_QuestionnaireList.setFont(m_EOApp.getSmLabelFont());

        southGBPanel.constrain(m_QuestionnaireList,7,2,4,4);
    // End Setup for South Panel.

        add("North",northGBPanel);
        add("South",southGBPanel);

        pack();
        show();

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

            if ((theSource == m_addButton) && (m_availQuestionList.getSelectedIndex() > -1) && (m_userListIndex > -1))
                {
                BaseQuestion bq = (BaseQuestion)((BaseQuestion)m_availableTypes.elementAt(m_typeQuestionIndex)).clone();
                Hashtable h = (Hashtable)m_listedQuestions.elementAt(m_availQuestionList.getSelectedIndex());
                bq.setFileName((String)h.get("FileName"));
                bq.setUserID(((Integer)h.get("UserID")).intValue());
                if (h.containsKey("App ID"))
                    {
                    bq.setAppID((String)h.get("App ID"));
                    bq.setAppName((String)h.get("App Name"));
                    }
                Vector qa = (Vector)m_Questionnaire.elementAt(m_userListIndex);
                Vector trans = (Vector)m_transitions.elementAt(m_userListIndex);
                qa.addElement(bq);
                trans.addElement(bq.initializeTransitions());

                m_QuestionnaireList.addItem(BuildQuestionnaireListEntry(bq));
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
            if ((theSource == m_availDescButton) && (m_availQuestionList.getSelectedIndex() > -1))
                {
                BaseQuestion bq = (BaseQuestion)((BaseQuestion)m_availableTypes.elementAt(m_typeQuestionIndex)).clone();
                Hashtable h = (Hashtable)m_listedQuestions.elementAt(m_availQuestionList.getSelectedIndex());
                ((BaseQuestion)m_EOApp.loadBaseAction(h,bq)).displayQuestion();
                }
            if ((theSource == m_insertButton) && (m_availQuestionList.getSelectedIndex() > -1) && (m_QuestionnaireList.getSelectedIndex() > -1))
                {
                int index = m_QuestionnaireList.getSelectedIndex();
                BaseQuestion bq = (BaseQuestion)((BaseQuestion)m_availableTypes.elementAt(m_typeQuestionIndex)).clone();
                Hashtable h = (Hashtable)m_listedQuestions.elementAt(m_availQuestionList.getSelectedIndex());
                bq.setFileName((String)h.get("FileName"));
                bq.setUserID(((Integer)h.get("UserID")).intValue());
                if (h.containsKey("App ID"))
                    {
                    bq.setAppID((String)h.get("App ID"));
                    bq.setAppName((String)h.get("App Name"));
                    }
                Vector qa = (Vector)m_Questionnaire.elementAt(m_userListIndex);
                Vector trans = (Vector)m_transitions.elementAt(m_userListIndex);
                qa.insertElementAt(bq,index);
                trans.insertElementAt(bq.initializeTransitions(),index);
                m_QuestionnaireList.addItem(BuildQuestionnaireListEntry(bq),index);
                }
            if ((theSource == m_removeButton) && (m_QuestionnaireList.getSelectedIndex() > -1))
                {
                int index = m_QuestionnaireList.getSelectedIndex();
                Vector qa = (Vector)m_Questionnaire.elementAt(m_userListIndex);
                Vector trans = (Vector)m_transitions.elementAt(m_userListIndex);
                qa.removeElementAt(index);
                trans.removeAllElements();
                m_QuestionnaireList.remove(index);
                for(int i=0;i<qa.size();i++)
                    {
                    BaseQuestion bq = (BaseQuestion)qa.elementAt(i);
                    trans.addElement(bq.initializeTransitions());
                    }
                }
            if ((theSource == m_transitionButton) && (m_QuestionnaireList.getSelectedIndex() > -1))
                {
                setEditMode(true);
                int index = m_QuestionnaireList.getSelectedIndex();
                Vector qa = (Vector)m_Questionnaire.elementAt(m_userListIndex);
                BaseQuestion ba = (BaseQuestion)qa.elementAt(index);
                ba.formatTransition(m_EOApp,this);
                }
            if ((theSource == m_typeDescButton) && (m_baseQuestionTypeList.getSelectedIndex() > -1))
                {
                String str = (String)m_typeDescriptions.elementAt(m_baseQuestionTypeList.getSelectedIndex());
                new DescriptionDialog(str);
                }
            }

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("fqw_description")))
                {
                setEditMode(true);
                new EditQuestionnaireDescriptionWindow(m_EOApp,this,m_QApp);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("fqw_iw")))
                {
                setEditMode(true);
                new QAEditInitialWindow(m_EOApp,this,m_QApp);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("fqw_exit")))
                {
                m_EBWApp.updateDisplay();
                m_EBWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("fqw_help")))
                {
                m_EOApp.helpWindow("ehlp_fqw");
                }
            }
        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

            if ((theSource == m_baseQuestionTypeList) && (m_baseQuestionTypeList.getSelectedIndex() > -1))
                {
                m_typeQuestionIndex = m_baseQuestionTypeList.getSelectedIndex();
                BaseQuestion bq = (BaseQuestion)m_availableTypes.elementAt(m_typeQuestionIndex);

                m_availQuestions = m_EOApp.loadBaseActionFileList(bq.getDB(),bq.getDBTable(),m_accessGroups);
                if (m_accessGroupList.getSelectedIndex() > -1)
                    {
                    CreateAvailQuestionList(m_accessGroupList.getSelectedIndex());
                    }
                }
            }
        }

    public String[] BuildQuestionnaireListEntry(BaseQuestion bq)
        {
        String[] str = new String[1];

        str[0] = bq.getFileName();

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
    public void CreateAvailQuestionList(int loc) 
        {   
        m_listedQuestions.removeAllElements();
        m_availQuestionList.removeAll();

        Hashtable h = (Hashtable)m_accessGroups.elementAt(loc);
        String uid = new String("-");
        if (h.containsKey("App ID"))
            uid = (String)h.get("App ID");

        Enumeration enm = m_availQuestions.elements();
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
                m_availQuestionList.addItem(str);

                m_listedQuestions.insertElementAt(h2,m_availQuestionList.last);
                }
            }
        }

    private void copyQuestionnaire(int from, int to)
        {
        Vector fromQA = (Vector)m_Questionnaire.elementAt(from);
        Vector toQA = (Vector)m_Questionnaire.elementAt(to);
        toQA.removeAllElements();

        Enumeration enm = fromQA.elements();
        while (enm.hasMoreElements())
            {
            BaseQuestion bq = (BaseQuestion)enm.nextElement();

            toQA.addElement(bq.clone());
            }
        }

    public void fillQuestionnaireList()
        {
        Vector qa = (Vector)m_Questionnaire.elementAt(m_userListIndex);
        Enumeration enm = qa.elements();
        while (enm.hasMoreElements())
            {
            BaseQuestion bq = (BaseQuestion)enm.nextElement();

            m_QuestionnaireList.addItem(BuildQuestionnaireListEntry(bq));
            }
        }
    public void fillUserList()
        {
        for (int i=0;i<m_Questionnaire.size();i++)
            {
            String[] str = new String[1];
            str[0] = new String("User "+i);
            m_userList.addItem(str);
            }
        }

    public boolean getEditMode()
        {
        return m_editMode;
        }
    public Vector getQuestionnaire()
        {
        return m_Questionnaire;
        }
    public int getQuestionnaireListIndex()
        {
        return m_QuestionnaireList.getSelectedIndex();
        }
    public Vector getTransitions()
        {
        return m_transitions;
        }
    public int getUserListIndex()
        {
        return m_userListIndex;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/qa/awt/fqw.txt");
        }  

    public void itemStateChanged(ItemEvent ie)
        {
        if (getEditMode())
            return;

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
                            copyQuestionnaire(m_userListIndex,index);
                            }
                        }

                    m_userListIndex = index;
                    m_QuestionnaireList.removeAll();
                    fillQuestionnaireList();
                    }
                }
            }

        if (ie.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)ie.getSource();

            if ((theSource == m_accessGroupList) && (m_accessGroupList.getSelectedIndex() >= 0))
                {
                int index = m_accessGroupList.getSelectedIndex();
                CreateAvailQuestionList(index);
                }
            }
        }

    public void loadAvailableTypes()
        {
        BaseQuestionTypesListReqMsg tmp = new BaseQuestionTypesListReqMsg(null);
        ExptMessage em = m_EOApp.sendExptMessage(tmp);

        if (em instanceof BaseQuestionTypesListReqMsg)
            {
            Object[] in_args = em.getArgs();
            Vector availableTypes = (Vector)in_args[0];
            Vector typeDescriptions = (Vector)in_args[1];

            for (int i=0;i<availableTypes.size();i++)
                {
                String[] str = new String[1];
                BaseQuestion bq = (BaseQuestion)availableTypes.elementAt(i);

                str[0] = bq.getName();
                m_baseQuestionTypeList.addItem(str);
                m_availableTypes.insertElementAt(bq,m_baseQuestionTypeList.last);
                m_typeDescriptions.insertElementAt(typeDescriptions.elementAt(i),m_baseQuestionTypeList.last);
                }
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/qa/awt/fqw.txt");
        }

    public void setEditMode(boolean value)
        {
        m_editMode = value;
        }
    }
