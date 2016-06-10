package girard.sc.expt.io.msg;

import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.obj.ExptComptroller;

/**
 * ShutdownExptReqMsg: Shutsdown any remaining client, observer, or experimenter windows.
 * Is only used if the End window is being used and the experimenter has to shut them down.
 * <p>
 * <br> Started: 10-15-2002
 * <p>
 *
 * @author Dudley Girard
 */

public class ShutdownExptReqMsg extends ExptMessage 
    { 
    public ShutdownExptReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
// System.err.println("ESR: Shutdown Experiment Request Message");
// System.err.flush();

        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index > -1)
                    {
                    // Should not got here.
                    return null;
                    }
                else
                    {
                    ec.sendToAllObservers(new ShutdownExptReqMsg(null));
                    ec.sendToAllUsers(new ShutdownExptReqMsg(null));
                    ec.addServerMessage(new ShutdownExptReqMsg(null));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment to start.");
            err_args[1] = new String("ShutdownExptReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        ew.setWatcher(false);
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }