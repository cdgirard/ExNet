package girard.sc.expt.io.msg;

/* EndExptReqMsg: Signifies the end of an experiment.  Causes the subject and
   observer stations to close down, while the experimenter station is sent the
   amount to pay each subject.

Author: Dudley Girard
Started: ?????-2000
Modified: 4-30-2001
Modified: 5-28-2001
*/

import girard.sc.expt.awt.ClientEndExptWindow;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Hashtable;

public class EndExptReqMsg extends ExptMessage 
    { 
    public EndExptReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if (getArgs() == null)
            {
            cw.getExpApp().setExptRunning(false);
            cw.setWatcher(false);
            }
        else
            {
            cw.getExpApp().setExptRunning(false);
            Hashtable endWindowDetails = (Hashtable)(getArgs()[0]);

            new ClientEndExptWindow(cw,endWindowDetails);
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
 // System.err.println("ESR: End Experiment Request Message: "+getArgs());
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
                    ec.sendToAllObservers(new EndExptReqMsg(getArgs()));
                    ec.sendToAllUsers(new EndExptReqMsg(getArgs()));
                    ec.addServerMessage(new EndExptReqMsg(getArgs()));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment to start.");
            err_args[1] = new String("EndExptReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        ew.getExpApp().setExptRunning(false);
        ew.getExpApp().cleanUpSimSMLs();
        
        if (ew.getExpApp().getExptOutputID() == 0)
            {
            ew.setWatcher(false); // Experiment never got started.
            }
        else
            {
            if (ew.getExpApp().getExtraData("AutoStart") != null)
                {
                String str = (String)ew.getExpApp().getExtraData("AutoStart");
                if (str.equals("true"))
                    {
                    ew.setWatcher(false);
                    return;
                    }
                }
            Object[] out_args = new Object[1];
            out_args[0] = new Integer(ew.getExpApp().getExptOutputID());
            GetPayResultsReqMsg tmp = new GetPayResultsReqMsg(out_args);
	    System.err.println("calling the get pay results req message");
            ew.getSML().sendMessage(tmp);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        ow.getExpApp().setExptRunning(false);
        ow.getExpApp().setJoined(false);
        ow.setWatcher(false);
        }
    }
