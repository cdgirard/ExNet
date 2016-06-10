package girard.sc.expt.io.msg;

/* If the experiment fails to get started due to a user disconnecting
   then this message is sent out to reset everyone back to just before
   the start message is sent.

   Author: Dudley Girard
   Started: 01-01-2000
*/

import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.obj.ExptComptroller;

public class ResetExptStartReqMsg extends ExptMessage 
    { 
    public ResetExptStartReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        cw.getExpApp().setReadyToStart(false);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
// System.err.println("ESR: Reset Start Experiment Request Message");
// System.err.flush();
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == -1)
                    {
                    ec.sendToAllUsers(new ResetExptStartReqMsg(null));
                    ec.sendToAllObservers(new ResetExptStartReqMsg(null));
                    return null;
                    }
                else
                    {
                    // Should never go here.
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment to disconnect from.");
            err_args[1] = new String("ResetExptStartReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        ow.getExpApp().setReadyToStart(false);
        }
    }
