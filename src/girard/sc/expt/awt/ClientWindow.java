package girard.sc.expt.awt;

import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.DisconnectReqMsg;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.obj.ClientWatcherThread;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.expt.web.JoinExperimentPage;
import girard.sc.web.WebPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

/**
 * Is the base window class for all client displays.  An ExperimentAction's client 
 * display must extend this class.
 * <p>
 * <br> Modified: 04-03-2003
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.41
 * @since JDK1.4
 */

public abstract class ClientWindow extends JFrame implements ActionListener
    {
/**
 * The class object that keeps track of any experiment information needed by
 * the client.
 */
    protected ClientExptInfo m_ExpApp;
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 */ 
    protected ExptOverlord m_EOApp;
/**
 * Provides the means to send messages to the ExptComptroller.
 * 
 * @see girard.sc.expt.io.obj.ExptComptroller
 */
    protected ExptMessageListener m_SML;
/**
 * Is the thread that waits for when to close down the window.
 */
    protected ClientWatcherThread m_watcher;
/**
 * Provided as a convience for the programmer, can be used to restrict access
 * to the window.
 */
    protected boolean m_EditMode = false;

/**
 * The constructor for the ClientWindow.  Creates a ClientWatcherTread for the
 * window.
 *
 * @param app1 The active ExptOverlord class object.
 * @param app2 The ClientExptInfo class object.
 * @param app3 The ExptMessageListener class object.
 * @see girard.sc.expt.obj.ClientWatcherThread
 */
    public ClientWindow(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3)
        {
        m_EOApp = app1;
        m_ExpApp = app2;
        m_SML = app3;
        m_SML.addActionListener(this);
        m_watcher = new ClientWatcherThread(this);
        }

/**
 * Used to process any ActionEvents.  Normally overridden as the one provided does
 * nothing.
 *
 * @param e The ActionEvent.
 */
    public void actionPerformed(ActionEvent e) { }

/**
 * Called when need to remove a ClientWindow for one ExperimentAction before
 * creating the ClientWindow for the next ExperimentAction.  Normally called by
 * finalCleanUp or removeWindow.
 *
 * @see girard.sc.expt.awt.ClientWindow#finalCleanUp()
 * @see girard.sc.expt.awt.ClientWindow#removeWindow()
 */
    public abstract void cleanUpWindow();

/**
 * Called when the experiment is finally over and it is time to close down
 * the presently active ClientWindow. Calls cleanUpWindow to ensure the ClientWindow
 * removes any extras such as additional Frames or Windows.  Normally called by
 * the ClientWatcherThread attached to the ClientWindow when the thread is stopped.
 *
 * @see girard.sc.expt.awt.ClientWindow#cleanUpWindow()
 * @see girard.sc.expt.obj.ClientWatcherThread
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

/**
 * @return Returns the ClientExptInfo, m_ExpApp.
 */
    public ClientExptInfo getExpApp()
        {
        return m_ExpApp;
        }
/**
 * @return Returns the ExptOverlord, m_EOApp.
 */
    public ExptOverlord getEOApp()
        {
        return m_EOApp;
        }
/**
 * @return Returns the ExptMessageListener, m_SML.
 */
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
 * Removes all listen requests attached to the m_SML and removes this ClientWindow
 * from its list of Listeners, then calls cleanUpWindow before removing the ClientWindow.
 * Normally called by the ClientWatcherThread attached to the ClientWindow when the thread
 * is stopped.
 *
 * @see girard.sc.expt.awt.ClientWindow#cleanUpWindow()
 * @see girard.sc.expt.obj.ClientWatcherThread
 */
    public final void removeWindow()
        {
        m_SML.removeAllListenRequests();
        m_SML.removeActionListener(this);
 
        cleanUpWindow();

        dispose();
        }

/**
 * Tells when to stop the ClientWatcherThread.
 *
 * @param value If true then the watcher thread keeps going, if false the thread stops.
 */
    public void setWatcher(boolean value)
        {
        m_watcher.setFlag(value);
        }
    }
