package girard.sc.expt.io.msg;

import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.obj.ExptComptroller;

/**
 * Let's everyone know that the experiment is starting.
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1 
 */

public class StartExptReqMsg extends ExptMessage 
    {
/**
 * The constructor.  Sometimes the m_args will store which client the StartExptReqMsg
 * is from.
 *
 * @param args[] The arguments attached to the message.
 */
    public StartExptReqMsg (Object args[])
        {
        super(args);
        }

/**
 * Has the client set variables in ClientExptInfo to the experiment running state.
 * <br> m_readyToStart -> true
 * <br> m_exptRunning -> true
 * <br> Returns back a StartExptReqMsg once the client has readied itself to begin.
 *
 * @param cw The active ClientWindow, normally the ClientStartWindow.
 * @see girard.sc.expt.obj.ClientExptInfo
 * @see girard.sc.expt.awt.ClientStartWindow
 */
    public void getClientResponse(ClientWindow cw)
        {
        cw.getExpApp().setReadyToStart(true);
        cw.getExpApp().setExptRunning(true);

        StartExptReqMsg tmp = new StartExptReqMsg(null);
        cw.getSML().sendMessage(tmp);
        }

/**
 * Fowards StartExptReqMsg from an Experimenter to the Clients and Observers.  Fowards
 * StartExptReqMsgs from Clients to the Experimenter.  All message forwarding is handled
 * via the ExptComptroller.
 *
 * @param esc The ExptServerConnection that this message is attached to.
 * @return Returns an ExptErrorMsg if something wrong happens, otherwise returns null.
 * @see girard.sc.expt.io.obj.ExptComptroller
 */
    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {

// System.err.println("ESR: Start Experiment Request Message");
// System.err.flush();

        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index > -1)
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new StartExptReqMsg(out_args));
                    return null;
                    }
                else
                    {
                    if (!ec.allRegistered())
                        {
                        Object[] err_args = new Object[2];
                        err_args[0] = new String("Least one user not registered.");
                        err_args[1] = new String("StartExptReqMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new StartExptReqMsg(null));
                    ec.sendToAllObservers(new StartExptReqMsg(null));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment to start.");
            err_args[1] = new String("StartExptReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

/**
 * Waits to get responses from all the clients then:
 * <br> calls the newExptStarted() function in ExperimenterWindow.
 * <br> calls the newExptUserReferenceInfo() function in ExperimenterWindow.
 * <br> lastly starts up the first ExperimentAction.
 *
 * @param ew The active ExperimenterWindow, normally the ExperimenterStartWindow.
 * @see girard.sc.expt.awt.ExperimenterWindow
 * @see girard.sc.expt.awt.ExperimenterStartWindow
 */
    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];
        
        if (ew.getExpApp().getReadyToStart())
            ew.getExpApp().setReady(true,index.intValue());

        boolean flag = true;
        for (int x=0;x<ew.getExpApp().getNumUsers();x++)
            {
            if (!ew.getExpApp().getReady(x))
                flag = false;
            }
        if (flag)
            {
            ew.getExpApp().initializeReady();
 
            ew.newExptStarted(); // Sends out a message to the database adding the experiment to it.

            ew.newExptUserReferenceInfo();

            ew.getExpApp().setExptRunning(true);
            ew.getExpApp().setActionIndex(0);
            ew.setWatcher(false);
            }

        return;
        }

/**
 * Has the observer set variables in ObserverExptInfo to the experiment running state.
 * <br> m_readyToStart -> true
 * <br> m_exptRunning -> true
 * <br> Does not return a message as it doesn't matter if an Observer is there or not.
 *
 * @param ow The active ObserverWindow, normally the ObserverStartWindow.
 * @see girard.sc.expt.obj.ObserverExptInfo
 * @see girard.sc.expt.awt.ObserverStartWindow
 */
    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        ow.getExpApp().setReadyToStart(true);
        ow.getExpApp().setExptRunning(true);
        }
    }