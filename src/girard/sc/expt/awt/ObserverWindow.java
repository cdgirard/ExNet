package girard.sc.expt.awt;

import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.DisconnectReqMsg;
import girard.sc.expt.obj.ObserverExptInfo;
import girard.sc.expt.obj.ObserverWatcherThread;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.expt.web.JoinExperimentPage;
import girard.sc.web.WebPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

/**
 * The base window for display information to observers of an experiment.
 * <p>
 * <br>Started: 1-1-2001
 * <br>Modified: 4-24-2001
 * <br>Modified: 04-03-2003
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 */

public abstract class ObserverWindow extends JFrame implements ActionListener
    {
/**
 * Provides information needed by the observer station.
 */
    protected ObserverExptInfo m_ExpApp;
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 */
    protected ExptOverlord m_EOApp;
/**
 * Provides the means to send messages to the ExptServerConnection.
 * 
 * @see girard.sc.expt.io.ExptServerConnection
 */
    protected ExptMessageListener m_SML;
/**
 * Is the thread that waits for when to close down the window.
 */
    protected ObserverWatcherThread m_watcher;

/**
 * Provided as a convience for the programmer, can be used to restrict access
 * to the window.
 */
    private boolean m_EditMode = false;

/**
 * The constructor for the ObserverWindow.  Creates an ObserverWatcherTread for
 * the window.
 *
 * @param app1 The active ExptOverlord class object.
 * @param app2 The ObserverExptInfo class object.
 * @param app3 The ExptMessageListener class object.
 * @see girard.sc.expt.obj.ObserverWatcherThread
 */
    public ObserverWindow(ExptOverlord app1, ObserverExptInfo app2, ExptMessageListener app3)
        {
        m_EOApp = app1;
        m_ExpApp = app2;
        m_SML = app3;
        m_SML.addActionListener(this);
        m_watcher = new ObserverWatcherThread(this);
        }

/**
 * Used to process any ActionEvents.  Normally overridden as the one provided does
 * nothing.
 *
 * @param e The ActionEvent.
 */
    public void actionPerformed(ActionEvent e) {}

/**
 * Called when need to remove an ObserverWindow for one ExperimentAction before
 * creating the ObserverWindow for the next ExperimentAction.  Normally called by
 * finalCleanUp or removeWindow.
 *
 * @see girard.sc.expt.awt.ObserverWindow#finalCleanUp()
 * @see girard.sc.expt.awt.ObserverWindow#removeWindow()
 */
    public abstract void cleanUpWindow();

/**
 * Called when the experiment is finally over and it is time to close down
 * the presently active ObserverWindow. Calls cleanUpWindow to ensure the ObserverWindow
 * removes any extras such as additional Frames or Windows.  Normally called by
 * the ObserverWatcherThread attached to the ObserverWindow when the thread is stopped.
 *
 * @see girard.sc.expt.awt.ObserverWindow#cleanUpWindow()
 * @see girard.sc.expt.obj.ObserverWatcherThread
 */
    public final void finalCleanUp()
        {
        m_SML.removeAllListenRequests();
        DisconnectReqMsg tmp = new DisconnectReqMsg(null);
        m_SML.sendMessage(tmp);
        m_SML.finalize(0);

        cleanUpWindow();

        m_EOApp.setEditMode(false);

        WebPanel p = m_EOApp.getActivePanel();
        if (p instanceof JoinExperimentPage)
            {
            JoinExperimentPage jep = (JoinExperimentPage)p;
            jep.restartPage();
            }

        dispose();
        }

    public ObserverExptInfo getExpApp()
        {
        return m_ExpApp;
        }
    public ExptOverlord getEOApp()
        {
        return m_EOApp;
        }
    public ExptMessageListener getSML()
        {
        return m_SML;
        }

/**
 * Used to update the WebResourceBundle with any new entries for this window.
 * <p>
 * Example Code: m_EOApp.initializeLabels("girard/sc/expt/awt/dbaw.txt");
 * <p>
 * @see girard.sc.web.WebResourceBundle
 */
    public void initializeLabels() {}

/**
 * Used to update the WebResourceBundle by removing any entries for this window.
 * <p>
 * Example Code: m_EOApp.removeLabels("girard/sc/expt/awt/dbaw.txt");
 * <p>
 * @see girard.sc.web.WebResourceBundle
 */
    public void removeLabels() {}
/**
 * Removes all listen requests attached to the m_SML and removes this ObserverWindow
 * from its list of Listeners, then calls cleanUpWindow before removing the ObserverWindow.
 * Normally called by the ObserverWatcherThread attached to the ObserverWindow when the thread
 * is stopped.
 *
 * @see girard.sc.expt.awt.ObserverWindow#cleanUpWindow()
 * @see girard.sc.expt.obj.ObserverWatcherThread
 */
    public final void removeWindow()
        {
        m_SML.removeAllListenRequests();
        m_SML.removeActionListener(this);

        cleanUpWindow();

        dispose();
        }

/**
 * Tells when to stop the ObserverWatcherThread.
 *
 * @param value If true then the watcher thread keeps going, if false the thread stops.
 */
    public void setWatcher(boolean value)
        {
        m_watcher.setFlag(value);
        }

/**
 * The following function is a general function that is attached to all 
 * ObserverWindows.
 */
    public abstract void updateDisplay();
    }
