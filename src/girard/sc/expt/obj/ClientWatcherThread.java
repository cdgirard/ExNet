package girard.sc.expt.obj;

import girard.sc.expt.awt.ClientWindow;

/**
 * The object that watches for the end of an ExperimentAction or the actual Experiment
 * at which point it disposes of the window it is attached to.  Is started when a ClientWindow
 * is first created.
 * <p>
 * Started: 1-24-2001
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 */

public class ClientWatcherThread extends Thread
    {
/**
 * The ClientWindow that the thread has control over.
 */
    protected ClientWindow m_CWApp;
/**
 * Controls when to stop the thread.
 */
    protected boolean m_flag = true;

/**
 * Creates the ClientWatcherThread.
 *
 * @param app1 The ClientWindow that the ClientWatcherThread is attached to.
 */
    public ClientWatcherThread (ClientWindow app1)
        {
        m_CWApp = app1;

        start();
        }

/**
 * The thread runs quietly until the m_flag variable is set to false.  Then if the
 * Experiment is still running it calls removeWindow, otherwise it calls finalCleanUp
 * on the ClientWindow.
 */
    public void run()
        {
        while (m_flag)
            {
            try { sleep(500); }
            catch (InterruptedException ie) { ; }
            }
        if (m_CWApp.getExpApp().getExptRunning())
            m_CWApp.removeWindow();
        else
            m_CWApp.finalCleanUp();
        }

/**
 * Used to set the value of the m_flag.
 */
    public void setFlag(boolean value)
        {
        m_flag = value;
        }
    }
