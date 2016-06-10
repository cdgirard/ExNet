package girard.sc.expt.obj;

import girard.sc.expt.awt.ExperimenterWindow;

/**
 * The object that watches for the end of an ExperimentAction or the actual Experiment
 * at which point it disposes of the window it is attached to.  Is started when an
 * ExperimenterWindow is first created.
 * <p>
 * Started: 1-24-2001
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 */

public class ExperimenterWatcherThread extends Thread
    {
/**
 * The ExperimenterWindow that the thread has control over.
 */
    protected ExperimenterWindow m_EWApp;
/**
 * Controls when to stop the thread.
 */
    protected boolean m_flag = true;

/**
 * Creates the ExperimenterWatcherThread.
 *
 * @param app1 The ExperimenterWindow that the ExperimenterWatcherThread is attached to.
 */
    public ExperimenterWatcherThread (ExperimenterWindow app1)
        {
        m_EWApp = app1;

        start();
        }

/**
 * The thread runs quietly until the m_flag variable is set to false.  Then if the
 * Experiment is still running it calls removeWindow, otherwise it calls finalCleanUp
 * on the ExperimenterWindow.
 */
    public void run()
        {
        while (m_flag)
            {
            try { sleep(500); }
            catch (InterruptedException ie) { ; }
            }
// System.err.println("Here EWT");
        if (m_EWApp.getExpApp().getExptRunning())
            m_EWApp.startAction();
        else
            m_EWApp.finalCleanUp();
        }

/**
 * Used to set the value of the m_flag.
 */
    public void setFlag(boolean value)
        {
        m_flag = value;
        }
    }
