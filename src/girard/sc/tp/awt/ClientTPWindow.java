package girard.sc.tp.awt;

import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.gtp.obj.GenericTutorialPage;
import girard.sc.tp.obj.TutorialPage;
import girard.sc.tut.io.msg.NextTutorialPageReqMsg;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Used as the base window for tutorial page displays for subjects.
 * <p>
 * <br> Started: 08-07-2002
 * <br> Modified: 04-03-2003
 * <p>
 * @author Dudley Girard
 * 
 */

public class ClientTPWindow extends ClientWindow
    {
    protected TutorialPage m_tp;

    public ClientTPWindow(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3)
        {
        super(app1,app2,app3);

        Hashtable h = (Hashtable)m_ExpApp.getActiveAction();
        Vector pages = (Vector)h.get("TutPages");
        Integer currentPage = (Integer)h.get("CurrentPage");

        m_tp = (GenericTutorialPage)pages.elementAt(currentPage.intValue());
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
    public void removeTutWindow(int value)
        {
        Hashtable h = (Hashtable)m_ExpApp.getActiveAction();
        Vector pages = (Vector)h.get("TutPages");
        int currentPage = ((Integer)h.get("CurrentPage")).intValue() + value;
 
        h.put("CurrentPage",new Integer(currentPage));

        if (pages.size() > currentPage)
            {
            TutorialPage tp = (TutorialPage)pages.elementAt(currentPage);
            
            m_SML.removeActionListener(this);

            cleanUpWindow();

            tp.startPage(m_EOApp,m_ExpApp,m_SML);
 
            Object[] out_args = new Object[1];
            out_args[0] = new Integer(currentPage + 1);
            NextTutorialPageReqMsg tmp = new NextTutorialPageReqMsg(out_args);
            m_SML.sendMessage(tmp);
            }
        else
            {
            // Popup a please wait while others finish window.

            m_SML.removeActionListener(this);

            m_tp.createWaitWindow(m_EOApp,m_ExpApp,m_SML);

            cleanUpWindow();
            }
        }
    }
