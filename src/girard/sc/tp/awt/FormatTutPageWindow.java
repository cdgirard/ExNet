package girard.sc.tp.awt;

/* Based window used to format tutorial pages.

   Author: Dudley Girard
   Started: 10-30-2001
*/

import girard.sc.expt.awt.BaseActionFormatWindow;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.tp.obj.TutorialPage;

public class FormatTutPageWindow extends BaseActionFormatWindow
    {
    protected TutorialPageBuilderWindow m_TPBWApp;
    protected ExptOverlord m_EOApp;
    protected TutorialPage m_TPApp;

    protected boolean m_EditMode = false;

    public FormatTutPageWindow(ExptOverlord app1, TutorialPageBuilderWindow app2, TutorialPage app3)
        {
        super(app1,null,app3);

        m_EOApp = app1;
        m_TPBWApp = app2;
        m_TPApp = app3;
        }

    public boolean getEditMode()
        {
        return m_EditMode;
        }

    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }
    }
