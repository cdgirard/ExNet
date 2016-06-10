package girard.sc.expt.io.msg;

import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.expt.obj.ExperimentAction;

/**
 * GetExptStateReqMsg: Is used by observers to request the present state the
 * experiment is in.  Thus allowing observers to start watching an experiment
 * that is in progress.
 * <p>
 * Started: 4-21-2001
 * Last Modified: 4-30-2001
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.2
 * @since JDK1.1
 */


public class GetExptStateReqMsg extends ExptMessage 
    {
/**
 * The constructor function.  The Object array passed in should have one object.
 * The object is an Integer giving the id of the observer that the message should
 * go to.
 *
 * @param args[] The Object array of information needed.
 */
    public GetExptStateReqMsg (Object args[])
        {
        super(args);
        }

/**
 * Used to foward the message to the proper user based on who sent it.  If an observer
 * sent the message then the message is fowarded to the experimenter.  If the experimenter
 * sent the message then the message is fowarded to proper observer.
 *
 * @param esc The ExptServerConnection that received the message.
 * @return Returns an ExptErrorMsg if something wrong happens, otherwise returns null.
 */
    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {  
// System.err.println("ESR: Get Experiment State Request Message");
// System.err.flush();

        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {                    
                    ec.addObserverMessage(new GetExptStateReqMsg(this.getArgs()),(Integer)this.getArgs()[0]);
                    return null;
                    }
                if (index == ExptComptroller.OBSERVER)
                    {
                    Object[] out_args = new Object[1];
                    out_args[0] = esc.getData();
                    ec.addServerMessage(new GetExptStateReqMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment to get info on.");
            err_args[1] = new String("GetExptStateReqMsg");
            return new ExptErrorMsg(err_args);
            }

        Object[] err_args = new Object[2];
        err_args[0] = new String("This should not be sent.");
        err_args[1] = new String("GetExptStateReqMsg");
        return new ExptErrorMsg(err_args);
        }

/**
 * When received by the experimenter, the observer id is pulled from the message and
 * then the sendPresentState function is called via the active ExperimentAction.
 *
 * @param ew The ExperimenterWindow that received the message.
 * @see girard.sc.expt.obj.ExperimentAction
 */
    public void getExperimenterResponse(ExperimenterWindow ew)
        {
// Need to account for when the experiment hasn't started or is in the middle of starting.
        if (ew.getExpApp().getExptRunning())
            {
            ExperimentAction ea = ew.getExpApp().getActiveAction();
        
            Integer obv = (Integer)this.getArgs()[0];

            ea.sendPresentState(obv,ew);
            }
        else
            {
            Object[] out_args = new Object[1];
            out_args[0] = this.getArgs()[0];

            GetExptStateReqMsg tmp = new GetExptStateReqMsg(out_args);
            ew.getSML().sendMessage(tmp);

            if (ew.getExpApp().getReadyToStart())
                {
                ExptStartingMsg tmp2 = new ExptStartingMsg(out_args);
                ew.getSML().sendMessage(tmp2);
                }
            }
        }

/**
 * What the ObserverWindow calls when it gets this message.  All it does is set
 * the ExptObserverInfo m_joined variable to true.  This lets any messages sent to
 * the Observer know that it is now connected to the Experiment.
 *
 * @param ow The ObserverWindow that received the message.
 */
    public void getObserverResponse(ObserverWindow ow)
        {
        ow.getExpApp().setJoined(true);
        }
    }