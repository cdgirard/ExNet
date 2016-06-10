package girard.sc.expt.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.DisconnectReqMsg;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.NewExptOutputReqMsg;
import girard.sc.expt.io.msg.NewExptUserReferenceReqMsg;
import girard.sc.expt.io.msg.SaveExptActionReferenceReqMsg;
import girard.sc.expt.io.msg.SaveExptPayResultsReqMsg;
import girard.sc.expt.io.msg.SaveOutputResultsReqMsg;
import girard.sc.expt.obj.Experiment;
import girard.sc.expt.obj.ExperimentAction;
import girard.sc.expt.obj.ExperimenterWatcherThread;
import girard.sc.expt.obj.SimActor;
import girard.sc.expt.web.ExptOverlord;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JFrame;

/**
 * The base window class for the experimenter.  The main experimenter display must
 * extend this class.
 * <p>
 * <br>Started: 1-1-2001
 * <br>Last Modified: 4-30-2001
 * <br>Last Modified: 04-03-2003
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.41
 * @since JDK1.4
 */

public abstract class ExperimenterWindow extends JFrame implements ActionListener
    {
/**
 * The Experiment being run, provides information needed by the experimenter station.
 */
    protected Experiment m_ExpApp;
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 */
    protected ExptOverlord m_EOApp;
/**
 * Provides the means to send(receive) messages to(from) the ExptServerConnection.
 * 
 * @see girard.sc.expt.io.ExptServerConnection
 */
    protected ExptMessageListener m_SML;
/**
 * Is the thread that waits for when to close down the window.
 */
    protected ExperimenterWatcherThread m_watcher;
/**
 * Provided as a convience for the programmer, can be used as a flag to restrict access
 * to the window.
 */
    protected boolean m_EditMode = false;

/**
 * The constructor for the ExperimenterWindow.  Creates an ExperimenterWatcherTread for
 * the window.
 *
 * @param app1 The active ExptOverlord class object.
 * @param app2 The ClientExptInfo class object.
 * @param app3 The ExptMessageListener class object.
 * @see girard.sc.expt.obj.ExperimenterWatcherThread
 */
    public ExperimenterWindow(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        m_EOApp = app1;
        m_ExpApp = app2;
        m_SML = app3;
        m_SML.addActionListener(this);
        m_watcher = new ExperimenterWatcherThread(this);
        }

/**
 * Used to process any ActionEvents.  Normally overridden as the one provided does
 * nothing.
 *
 * @param e The ActionEvent.
 */
    public void actionPerformed(ActionEvent e) {}

/**
 * Called when need to remove an ExperimenterWindow for one ExperimentAction before
 * creating the ExperimenterWindow for the next ExperimentAction.  Normally called by
 * finalCleanUp or startAction.
 *
 * @see girard.sc.expt.awt.ExperimenterWindow#finalCleanUp()
 * @see girard.sc.expt.awt.ExperimenterWindow#startAction()
 */
    public abstract void cleanUpWindow();

/**
 * Called when the experiment is finally over and it is time to close down
 * the presently active ExperimenterWindow. Calls cleanUpWindow to ensure the ExperimenterWindow
 * removes any extras such as additional Frames or Windows.  Normally called by
 * the ExperimenterWatcherThread attached to the ExperimenterWindow when the thread is stopped.
 *
 * @see girard.sc.expt.awt.ExperimenterWindow#cleanUpWindow()
 * @see girard.sc.expt.obj.ExperimenterWatcherThread
 */
    public final void finalCleanUp()
        {
        m_SML.removeAllListenRequests();
        DisconnectReqMsg tmp = new DisconnectReqMsg(null);
        m_SML.sendMessage(tmp);
        m_SML.finalize(0);

        cleanUpWindow();

        m_EOApp.setEditMode(false);

        dispose();
        }

/**
 * @return Returns the Experiment pointed to by m_ExpApp.
 */
    public Experiment getExpApp()
        {
        return m_ExpApp;
        }
/**
 * @return Returns the ExptOverlord accessible by m_EOApp.
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
 * Called when the experiment is actually started.  Should only be called once, is
 * called by StartExptReqMsg.  Sends a NewExptOutputReqMsg to the
 * WLServer to create an entry in the database for saving off any information.
 * The return message provides the output identifier for the experiment that
 * is running.
 * <br> NOTE: This function should only be called once per experiment.
 *
 * @see girard.sc.expt.io.msg.NewExptOutputReqMsg
 * @see girard.sc.expt.awt.ExperimenterStartWindow
 * @see girard.sc.expt.io.msg.StartExptReqMsg
 */
    public final void newExptStarted()
        {
        Object[] out_args = new Object[5];
        out_args[0] = m_ExpApp.getExptName();
        out_args[1] = m_ExpApp.getExptDesc();
        out_args[2] = m_ExpApp.getAppID();
        out_args[3] = m_ExpApp.getAppName();
        out_args[4] = m_ExpApp.getExtraData();
        NewExptOutputReqMsg tmp = new NewExptOutputReqMsg(out_args);
        ExptMessage em = null;
        int counter = 0;
        while (em == null)
            {
            em = m_EOApp.sendExptMessage(tmp);
            counter ++;
            if (counter > 5)
                break;
            }

        if (em instanceof NewExptOutputReqMsg)
            {
            m_ExpApp.setExptOutputID(((Integer)em.getArgs()[0]).intValue());
            }
        else if (em != null)
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        else 
            {
            new ErrorDialog("NULL NEOR: Failed to send message.");
            }
        }
/**
 * This stores basic information on the subjects in the experiment.  Stores which were
 * human and which were computer, if they were a computer then is stores the SimActor
 * class object for that subject.  It packages this information up and sends it off
 * to the database via a NewExptUserReferenceReqMsg.
 * <br> NOTE: This function should only be called once per experiment (Is called 
 * by StartExptReqMsg.).
 *
 * @see girard.sc.expt.io.msg.NewExptUserReferenceReqMsg
 */ 
    public final void newExptUserReferenceInfo()
        {
        Object[] out_args = new Object[3];
        out_args[0] = new Integer(m_ExpApp.getExptOutputID());
        out_args[1] = new Integer(m_ExpApp.getNumUsers());

        Hashtable h = new Hashtable();
        int actionCounter = 0;
        Enumeration enm = m_ExpApp.getActions().elements();
        while (enm.hasMoreElements())
            {
            ExperimentAction ea = (ExperimentAction)enm.nextElement();
            Hashtable actionUserInfo = new Hashtable();
            Enumeration enum2 = ea.getActors().keys();
            while (enum2.hasMoreElements())
                {
                Integer userNum = (Integer)enum2.nextElement();
                SimActor sa = ea.getActor(userNum.intValue());
                Object[] data = new Object[4];
                data[0] = new Integer(sa.getActorTypeID());
                data[1] = sa.getActorName();
                data[2] = sa.getActorDesc();
                data[3] = sa.getSettings();
                actionUserInfo.put(userNum,data);
                }
            h.put(new Integer(actionCounter),actionUserInfo);
            actionCounter++;
            }
        out_args[2] = h;
        NewExptUserReferenceReqMsg tmp = new NewExptUserReferenceReqMsg(out_args);

        ExptMessage em = null;
        int counter = 0;
        while (em == null)
            {
            em = m_EOApp.sendExptMessage(tmp);
            counter ++;
            if (counter > 5)
                break;
            }

        if (em instanceof ExptErrorMsg)
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        else if (em == null)
            {
            new ErrorDialog("NULL NUR: Failed to send message.");
            }
        }

/**
 * When this function is called, a copy of the active ExperimentAction class object is
 * stored in the database.  It is attached to the presently running experiment and
 * is used later on in data retrieval.  It does this by using a
 * SaveExptActionReferenceReqMsg. The database in which the data for this 
 * ExperimentAction class object is gotten from the ExperimentAction class object.
 * <br> Called from the startAction() function, so it is handled automatically.
 *
 * @see girard.sc.expt.io.msg.SaveExptActionReferenceReqMsg
 */
    public final void saveExptActionReference()
        {
        Object[] out_args = new Object[7];
        out_args[0] = new Integer(m_ExpApp.getExptOutputID());
        out_args[1] = new Integer(m_ExpApp.getActionIndex());
        out_args[2] = new Integer(m_ExpApp.getActiveAction().getActionType());
        out_args[3] = m_ExpApp.getActiveAction().getSettings();
        out_args[4] = m_ExpApp.getActiveAction().getDataBD();
        out_args[5] = m_ExpApp.getActiveAction().getDetailName();
        out_args[6] = m_ExpApp.getActiveAction().getDesc();

        SaveExptActionReferenceReqMsg tmp = new SaveExptActionReferenceReqMsg(out_args);

        ExptMessage em = null;
        int counter = 0;
        while (em == null)
            {
            em = m_EOApp.sendExptMessage(tmp);
            counter ++;
            if (counter > 5)
                break;
            }

        if (em instanceof ExptErrorMsg)
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        else if (em == null)
            {
            new ErrorDialog("NULL SAR: Failed to send message.");
            }
        }
/**
 * Used to send data to be stored in the database.  You must provide the String 
 * indentifier for the database to place the data and a Vector of DataOutputObjects.
 * Confirs this information to the TheServer via a SaveOutputResultsReqMsg.
 *
 * @param db The String identifier used by the TheServer class object to identify the
 * database.
 * @param v The Vector of DataOutputObjects that will facilitate the writing of the data
 * to the database given.
 * @return Returns true if successful, false otherwise.
 */
    public final boolean saveOutputResults(String db, Vector v)
        {
        Object[] out_args = new Object[2];
        out_args[0] = db;
        out_args[1] = v;
        SaveOutputResultsReqMsg tmp = new SaveOutputResultsReqMsg(out_args);
	//nvm
	System.err.println("Using the Db:"+db);
        ExptMessage em = null;
        int counter = 0;
        while (em == null)
            {
            em = m_EOApp.sendExptMessage(tmp);
            counter ++;
            if (counter > 5)
                break;
            }

        if (em instanceof SaveOutputResultsReqMsg)
            {
            return true;
            }
        else if (em != null)
            {
            new ErrorDialog((String)em.getArgs()[0]);
            return false;
            }
        else
            {
            new ErrorDialog("NULL SOR: Failed to send message.");
            return false;
            }
        }

/**
 * This function is used to save subject pay information to the database.  Confirs the
 * information to the database in a SaveExptPayResultsReqMsg.
 *
 * @param payAmt The array of doubles that represents the amount to pay the subjects.
 * @see girard.sc.expt.io.msg.SaveExptPayResultsReqMsg
 * @see girard.sc.expt.web.ExptOverlord#sendExptMessage(ExptMessage em)
 */
    public void savePayResults(double[] payAmt)
        {
        if (payAmt.length != m_ExpApp.getNumUsers())
            return;

        Object[] out_args = new Object[3];
        out_args[0] = new Integer(m_ExpApp.getExptOutputID());
        out_args[1] = new Integer(m_ExpApp.getActionIndex());
        out_args[2] = payAmt;
        SaveExptPayResultsReqMsg tmp = new SaveExptPayResultsReqMsg(out_args);

        ExptMessage em = null;
        int counter = 0;
        while (em == null)
            {
            em = m_EOApp.sendExptMessage(tmp);
            counter ++;
            if (counter > 5)
                break;
            }


        if (em instanceof ExptErrorMsg)
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        else if (em == null)
            {
            new ErrorDialog("NULL SPR: Failed to send message.");
            }
        }

/**
 * Used to start the next ExperimentAction for an experiment. Called from the
 * ExperimenterWatcherThread.  Gets the next ExperimentAction, resets the
 * ExptMessageListener, finishes cleaning up the present ExperimenterWindow by
 * calling cleanUpWindow(), calls the startAction for the ExperimentAction,
 * calls saveExptActionReference(),  then disposes of the old ExperimenterWindow.
 *
 * @see girard.sc.expt.obj.Experiment#getActiveAction()
 * @see girard.sc.expt.io.ExptMessageListener
 * @see girard.sc.expt.obj.ExperimentAction#startAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
 */
    public final void startAction()
        {
        ExperimentAction ea = m_ExpApp.getActiveAction();

        m_SML.removeAllListenRequests();
        m_SML.removeActionListener(this);

        cleanUpWindow();

        ea.initializeAction(m_EOApp,m_ExpApp,m_SML);

        saveExptActionReference();

        ea.startAction(m_EOApp,m_ExpApp,m_SML);

        dispose();
        }

/**
 * The following function is a general function that is attached to all 
 * ExperimenterWindows.  It is called by GetRegUsersMsg and GetRegObserversMsg 
 * when they have updated the registration lists of the subjects and observers 
 * respectively.  A programmer can have their own messages make use of it as
 * well if they want.
 *
 * @see girard.sc.expt.io.msg.GetRegUsersMsg
 * @see girard.sc.expt.io.msg.GetRegObserversMsg
 */
    public abstract void updateDisplay();

/**
 * Used to stop the ExperimenterWatcherThread.  By sending in a false value it will cause
 * the thread to stop.
 *
 * @param value If true the thread keeps going, if false the thread will stop.
 * @see girard.sc.expt.obj.ExperimenterWatcherThread
 */
    public void setWatcher(boolean value)
        {
        m_watcher.setFlag(value);
        }
    }
