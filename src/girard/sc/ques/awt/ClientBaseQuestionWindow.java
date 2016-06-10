package girard.sc.ques.awt;

import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.qa.io.msg.NextQuestionNoticeMsg;
import girard.sc.qa.io.msg.QuestionAnswerNoticeMsg;
import girard.sc.ques.obj.BaseQuestion;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Used to display BaseQuestions to the subjects.
 * <p>
 * <br> Started: 08-04-2002
 * <br> Modified: 04-03-2003
 * <p>
 * @author Dudley Girard
 */

public class ClientBaseQuestionWindow extends ClientWindow
    {
    protected BaseQuestion m_bq;
    protected Vector m_trans;
    protected int m_question;

    public ClientBaseQuestionWindow(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3, int question)
        {
        super(app1,app2,app3);

        m_question = question;

        Hashtable h = (Hashtable)m_ExpApp.getActiveAction();
        Vector qa = (Vector)h.get("Questionaire");
        Vector trans = (Vector)h.get("Transitions");

        m_bq = (BaseQuestion)qa.elementAt(m_question);
        m_trans = (Vector)trans.elementAt(m_question);
        }

    public void cleanUpWindow()
        {
        removeLabels();
        }

    public void initializeLabels()
        {
        }

    public void removeLabels()
        {
        }

    public void removeQuestionWindow(String ans, int t)
        {
        Hashtable h = (Hashtable)m_ExpApp.getActiveAction();
        Vector qa = (Vector)h.get("Questionaire");
        Integer trans = new Integer(0);

        if (m_trans.size() == 0)
            trans = new Integer(m_question + 1);
        else
            trans = (Integer)m_trans.elementAt(t);

        if (trans.intValue() == -1)
            trans = new Integer(m_question + 1);

        Object[] out_args = new Object[3];
        out_args[0] = new Integer(m_question);
        out_args[1] = trans;
        out_args[2] = ans;
        QuestionAnswerNoticeMsg tmp = new QuestionAnswerNoticeMsg(out_args);
        m_SML.sendMessage(tmp);

        if (trans.intValue() < qa.size())
            {
            BaseQuestion bq = (BaseQuestion)qa.elementAt(trans.intValue());
            
            m_SML.removeActionListener(this);

            removeLabels();
            dispose();

            bq.showQuestion(m_EOApp,m_ExpApp,m_SML,trans.intValue());

            Object[] out_args2 = new Object[1];
            out_args2[0] = new Integer(trans.intValue() + 1);
            NextQuestionNoticeMsg tmp2 = new NextQuestionNoticeMsg(out_args2);
            m_SML.sendMessage(tmp2);
            }
        else
            {
            // Popup a please wait while others finish window.
            m_SML.removeActionListener(this);

            removeLabels();
            dispose();

            m_bq.createWaitWindow(m_EOApp,m_ExpApp,m_SML);
            }
        }
    }
