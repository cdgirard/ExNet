package girard.sc.likert.awt;

import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.likert.obj.LikertQuestion;
import girard.sc.qa.awt.FormatQuestionnaireWindow;
import girard.sc.ques.awt.TransitionWindow;
import girard.sc.ques.obj.BaseQuestion;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

public class LikertTransitionWindow extends TransitionWindow implements ActionListener,ItemListener
    {
    LikertQuestion m_LQ;

    FixedList m_questionList;

    FixedList m_gotoList;

    Button m_doneButton, m_helpButton;

    public LikertTransitionWindow(ExptOverlord app, FormatQuestionnaireWindow fqw, LikertQuestion lq)
        {
        super(app,fqw,(BaseQuestion)lq);

        m_LQ = (LikertQuestion)m_BQ;

        Vector trans = (Vector)m_FQWApp.getTransitions().elementAt(m_FQWApp.getUserListIndex());

        Vector quesTrans = (Vector)trans.elementAt(m_questionIndex);

        if (quesTrans.size() == 0)
            {
            for (int i=0;i<m_LQ.getRange();i++)
                {
// Have it default to go to the next question.
                quesTrans.addElement(new Integer(-1));
                }
            }
        m_quesTrans = quesTrans;

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("ltw_title"));
        setFont(m_EOApp.getMedWinFont());

     // Setup North Panel.
        GridBagPanel northPanel = new GridBagPanel();

        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ltw_Questionnaire")),1,1,2,1);
        m_questionList = new FixedList(8,false,1,20,FixedList.CENTER);
        m_questionList.setFont(m_EOApp.getSmLabelFont());
        m_questionList.addItemListener(this);

        fillQuestionList();

        northPanel.constrain(m_questionList,1,2,4,4);

    // End Setup for North Panel

    // Setup for Center Panel
        GridBagPanel centerPanel = new GridBagPanel();

        m_gotoList = new FixedList(5,false,2,10,FixedList.CENTER);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ltw_ag")),1,1,2,1);
        String[] str = new String[2];
        for (int i=1;i<=m_LQ.getRange();i++)
            {
            m_gotoList.addItem(BuildGotoListEntry(i));
            }
        centerPanel.constrain(m_gotoList,1,2,2,4);

    // End Setup for Center Panel

    // Start Setup for South Panel
        GridBagPanel southPanel = new GridBagPanel();

        m_doneButton = new Button(m_EOApp.getLabels().getObjectLabel("ltw_done"));
        m_doneButton.addActionListener(this);
        southPanel.constrain(m_doneButton,1,1,1,1);

        m_helpButton = new Button(m_EOApp.getLabels().getObjectLabel("ltw_help"));
        m_helpButton.addActionListener(this);
        southPanel.constrain(m_helpButton,2,1,1,1);
 
    // End Setup for South Panel

        add("North",northPanel);
        add("Center",centerPanel);
        add("South",southPanel);

        pack();
        show();
        }

    public void actionPerformed(ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (theSource == m_doneButton)
                {
                m_FQWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            if (theSource == m_helpButton)
                {
                m_EOApp.helpWindow("ehlp_ltw");
                }
            }
        }

    public String[] BuildGotoListEntry(int i)
        {
        String[] str = new String[2];

        str[0] = new String("("+i+")");

        str[1] = ((Integer)m_quesTrans.elementAt(i-1)).toString();

        return str;
        }
    public String[] BuildQuestionnaireListEntry(BaseQuestion bq)
        {
        String[] str = new String[1];

        str[0] = bq.getFileName();

        return str;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/likert/awt/ltw.txt");
        }

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)e.getSource();

            if (theSource == m_questionList)
                {
                if ((m_gotoList.getSelectedIndex() > -1) && (m_questionList.getSelectedIndex() > -1))
                    {
                    int gotoLi = m_gotoList.getSelectedIndex();
                    int quesLi = m_questionList.getSelectedIndex();
                    m_quesTrans.removeElementAt(gotoLi);
                    m_quesTrans.insertElementAt(new Integer(quesLi),gotoLi);
                    m_gotoList.replaceItem(BuildGotoListEntry(gotoLi+1),gotoLi);
                    m_gotoList.select(gotoLi);
                    }
                }
            }
        }

    public void fillQuestionList()
        {
        Vector qa = (Vector)m_FQWApp.getQuestionnaire().elementAt(m_FQWApp.getUserListIndex());
        Enumeration enm = qa.elements();
        while (enm.hasMoreElements())
            {
            BaseQuestion bq = (BaseQuestion)enm.nextElement();

            m_questionList.addItem(BuildQuestionnaireListEntry(bq));
            }
        String[] str = new String[1];

        str[0] = new String("-End Questionnaire-");
        m_questionList.addItem(str);
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/likert/awt/ltw.txt");
        }
    }