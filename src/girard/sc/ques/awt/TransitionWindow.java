package girard.sc.ques.awt;

import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.qa.awt.FormatQuestionnaireWindow;
import girard.sc.ques.obj.BaseQuestion;

import java.awt.Frame;
import java.util.Hashtable;
import java.util.Vector;

public abstract class TransitionWindow extends Frame
    {
    protected ExptOverlord m_EOApp;
    protected FormatQuestionnaireWindow m_FQWApp;
    protected BaseQuestion m_BQ;

    protected int m_questionIndex;
    protected Vector m_quesTrans = new Vector();

    public TransitionWindow(ExptOverlord app, FormatQuestionnaireWindow fqw, BaseQuestion bq)
        {
        m_EOApp = app;
        m_FQWApp = fqw;
        m_BQ = loadQuestion(bq);

        m_questionIndex = m_FQWApp.getQuestionnaireListIndex();
        }

    public void initializeLabels()
        {
        }

    private final BaseQuestion loadQuestion(BaseQuestion bq)
        {
        Hashtable fileInfo = new Hashtable();
        fileInfo.put("FileName",bq.getFileName());
        fileInfo.put("UserID",new Integer(m_EOApp.getUserID()));
        if (bq.getAppID() != null)
            {
            fileInfo.put("App ID",bq.getAppID());
            fileInfo.put("App Name",bq.getAppName());
            }

        BaseAction ba = m_EOApp.loadBaseAction(fileInfo,(BaseAction)bq.clone());

        if (ba != null)
            {
            return (BaseQuestion)ba;
            }

        return null;
        }

    public void removeLabels()
        {
        }
    }