package girard.sc.expt.io.msg;

/* ExptStartingMsg: Is sent to observers to let them know an experiment is about
   to start.  This occurs only when the Observers have just missed the StartExptReqMsg.

Author: Dudley Girard
Started: 5-1-2001
*/


import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.obj.ExptComptroller;

public class ExptStartingMsg extends ExptMessage 
    { 
    public ExptStartingMsg (Object args[])
        {
        super(args);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {

// System.err.println("ESR: Experiment Starting Message");
// System.err.flush();

        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    ec.addObserverMessage(new ExptStartingMsg(null),(Integer)getArgs()[0]);
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment to start.");
            err_args[1] = new String("ExptStartingMsg");
            return new ExptErrorMsg(err_args);
            }

        Object[] err_args = new Object[2];
        err_args[0] = new String("This should not have happened.");
        err_args[1] = new String("ExptStartingMsg");
        return new ExptErrorMsg(err_args);
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        ow.getExpApp().setReadyToStart(true);
        ow.getExpApp().setExptRunning(true);
        }
    }