package girard.sc.ques.awt;

import girard.sc.expt.awt.BaseActionFormatWindow;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.ques.obj.BaseQuestion;

/**
 * Based window used to format BaseQuestion objects.
 * <p>
 * Started: 7-29-2002
 * <p>
 * @author Dudley Girard
 */

public class FormatBaseQuestionWindow extends BaseActionFormatWindow
    {
    protected BaseQuestionBuilderWindow m_BQBWApp;
    protected ExptOverlord m_EOApp;
    protected BaseQuestion m_BQApp;

    protected boolean m_EditMode = false;

    public FormatBaseQuestionWindow(ExptOverlord app1, BaseQuestionBuilderWindow app2, BaseQuestion app3)
        {
        super(app1,null,app3);

        m_EOApp = app1;
        m_BQBWApp = app2;
        m_BQApp = app3;
        }

    public boolean getEditMode()
        {
        return m_EditMode;
        }
    public ExptOverlord getEOApp()
        {
        return m_EOApp;
        }

    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }
    public void setEOApp(ExptOverlord obj)
        {
        m_EOApp = obj;
        }
    }
