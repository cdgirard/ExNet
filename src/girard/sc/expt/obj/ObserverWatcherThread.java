package girard.sc.expt.obj;

import girard.sc.expt.awt.ObserverWindow;

/**
 * The object that watches for the end of the round at which point it disposes
 * of the window it is attached to.
 * <p>
 * Started: 1-24-2001
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1  
 */

public class ObserverWatcherThread extends Thread
    {
/**
 * The ObserverWindow that the thread has control over.
 */
    protected ObserverWindow m_OWApp;
/**
 * Controls when to stop the thread.
 */
    protected boolean m_flag = true;

/**
 * Creates the ObserverWatcherThread.
 *
 * @param app1 The ObserverWindow that the ObserverWatcherThread is attached to.
 */
    public ObserverWatcherThread (ObserverWindow app1)
        {
        m_OWApp = app1;

        start();
        }

/**
 * The thread runs quietly until the m_flag variable is set to false.  Then if the
 * Experiment is still running it calls removeWindow, otherwise it calls finalCleanUp
 * on the ObserverWindow.
 */
    public void run()
        {
        while (m_flag)
            {
            try { sleep(500); }
            catch (InterruptedException ie) { ; }
            }
        if (m_OWApp.getExpApp().getExptRunning())
            m_OWApp.removeWindow();
        else
            m_OWApp.finalCleanUp();
        }

/**
 * Used to set the value of the m_flag.
 */
    public void setFlag(boolean value)
        {
        m_flag = value;
        }
    }
