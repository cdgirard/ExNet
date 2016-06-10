package girard.sc.qa.awt;

import girard.sc.awt.FixedLabel;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.qa.obj.Questionnaire;
import girard.sc.ques.obj.AnswerOutputObject;
import girard.sc.ques.obj.BaseQuestion;

import java.awt.Button;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Used to display the data from a Questionnaire to an experimenter.
 * <p>
 * Started: 08-20-2002
 * <p>
 * @author Dudley Girard
 */

public class QuestionnaireDataDisplay extends Frame implements ActionListener
    {
    BaseDataInfo m_bdi;
    Questionnaire m_QApp;
    ExptOverlord m_EOApp;

    int m_numUsers = 0;
    Vector m_userQA = null;
    int m_userIndex = -1;

    MenuBar m_menuBar;
    Menu m_fileMenu, m_userMenu, m_viewMenu, m_helpMenu;

    SortedFixedList m_answerList;
    FixedLabel m_userLabel;

    Button m_viewQuestionButton;

    boolean m_editMode = false;

    public QuestionnaireDataDisplay(ExptOverlord app1, BaseDataInfo bdi, Questionnaire app2)
        {
        super();
        m_bdi = bdi;
        m_QApp = app2;
        m_EOApp = app1;

        m_numUsers = ((Vector)m_QApp.getAction()).size();

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("qdd_title"));
        setFont(new Font("Monospaced",Font.PLAIN,14));
        setBackground(m_EOApp.getWinBkgColor());

     // Setup Menu options
        MenuItem tmpItem;

        m_menuBar = new MenuBar();

        m_menuBar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_menuBar);

    // File Menu
        m_fileMenu = new Menu(m_EOApp.getLabels().getObjectLabel("qdd_file"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("qdd_exit"));
        tmpItem.addActionListener(this);
        m_fileMenu.add(tmpItem);

        m_menuBar.add(m_fileMenu);

    // User Menu
        m_userMenu = new Menu(m_EOApp.getLabels().getObjectLabel("qdd_user"));

        for (int i=0;i<m_numUsers;i++)
            {
            tmpItem = new MenuItem("  "+i+"  ");
            tmpItem.addActionListener(this);
            m_userMenu.add(tmpItem);
            }

        m_menuBar.add(m_userMenu);

    // View Menu
        m_viewMenu = new Menu(m_EOApp.getLabels().getObjectLabel("qdd_view"));

        
        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("qdd_answers"));
        tmpItem.addActionListener(this);
        m_viewMenu.add(tmpItem);

        m_menuBar.add(m_viewMenu);

    // Help Menu
        m_helpMenu = new Menu(m_EOApp.getLabels().getObjectLabel("qdd_help"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("qdd_help"));
        tmpItem.addActionListener(this);
        m_helpMenu.add(tmpItem);

        m_menuBar.add(m_helpMenu);
    // End setup for Menu options.

        GridBagPanel MainPanel = new GridBagPanel();

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("qdd_name")+m_bdi.getActionDetailName()),1,1,4,1);
        
        m_userLabel = new FixedLabel(10);
        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("qdd_user:")),1,2,1,1);
        MainPanel.constrain(m_userLabel,2,2,1,1,GridBagConstraints.WEST);

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("qdd_eaqttat")),1,3,10,1);
        int[] elCol = { 7, 5, 5, 15, 5, 20, 7};
        m_answerList = new SortedFixedList(10,false,7,elCol,SortedFixedList.CENTER);

        MainPanel.constrain(m_answerList,1,4,10,10);

        m_viewQuestionButton = new Button(m_EOApp.getLabels().getObjectLabel("qdd_vq"));
        m_viewQuestionButton.addActionListener(this);
        MainPanel.constrain(m_viewQuestionButton,1,14,10,1,GridBagConstraints.CENTER);

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("qdd_rb")+m_bdi.getUserName()),1,15,4,1);
        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("qdd_date")+m_bdi.getDateRun().toString()),1,16,4,1);

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("qdd_description")),5,15,4,1);
        TextArea desc = new TextArea(m_QApp.getDesc(),5,30,TextArea.SCROLLBARS_NONE);
        desc.setEditable(false);
        MainPanel.constrain(desc,5,16,4,4);

        add(MainPanel);
        pack();
        show();
        }

    public void actionPerformed(ActionEvent e)
        {
        if (m_editMode)
            return;

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("qdd_answers")))
                {
                m_editMode = true;
                new AnswersDataWindow(m_QApp,m_EOApp,this,m_bdi);
                return;
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("qdd_exit")))
                {
                m_EOApp.setEditMode(false);
                this.dispose();
                return;
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("qdd_help")))
                {
                m_EOApp.helpWindow("ehlp_qdd");
                return;
                }
            for (int i=0;i<m_numUsers;i++)
                {
                if (theSource.getLabel().equals("  "+i+"  "))
                    {
                    m_userLabel.setText(m_EOApp.getLabels().getObjectLabel("qdd_user")+" - "+i);
                    m_userQA = (Vector)((Vector)m_QApp.getAction()).elementAt(i);
                    m_userIndex = i;
                    fillAnswerList();
                    }
                }
            }
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if ((theSource == m_viewQuestionButton) && (m_answerList.getSelectedIndex() > -1))
                {
                String str = m_answerList.getSelectedSubItem(2);
                int ques = Integer.valueOf(str).intValue();
                BaseQuestion bq = (BaseQuestion)m_userQA.elementAt(ques);
                bq.displayQuestion();
                }
            }
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }
    
    public void fillAnswerList()
        {
        m_answerList.removeAll();
        Vector ques = (Vector)m_bdi.getActionData().get(""+m_userIndex);
        Enumeration enm = ques.elements();
        while (enm.hasMoreElements())
            {
            AnswerOutputObject aoo = (AnswerOutputObject)enm.nextElement();

            if (m_userIndex == aoo.getUserIndex())
                {
                BaseQuestion bq = (BaseQuestion)m_userQA.elementAt(aoo.getQuestionIndex());
                String[] str = new String[7];
                str[0] = new String(""+m_bdi.getExptOutID());
                str[1] = new String(""+m_bdi.getActionIndex());
                str[2] = new String(""+aoo.getQuestionIndex());
                str[3] = bq.getName();
                str[4] = new String(""+aoo.getTransitionIndex());
                str[5] = aoo.getAnswer();
                str[6] = new String(""+aoo.getRealTime());
                m_answerList.addItem(str);
                }
            }
        }

    public boolean getEditMode()
        {
        return m_editMode;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/qa/awt/qdd.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/qa/awt/qdd.txt");
        }

    public void setEditMode(boolean value)
        {
        m_editMode = value;
        }
    }
