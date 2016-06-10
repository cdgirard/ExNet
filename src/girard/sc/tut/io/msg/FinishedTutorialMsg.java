package girard.sc.tut.io.msg;

/* The sends the done with tutorial message to the experimenter
   for a client in the Tutorial Action.

Author: Dudley Girard
Started: 1-14-2002
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.tut.awt.TutorialActionExperimenterWindow;

public class FinishedTutorialMsg extends ExptMessage 
    { 
    public FinishedTutorialMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

 // System.err.println("ESR: Finished Tutorial Message");
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    return null;
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new FinishedTutorialMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("FinishedTutorialMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];
        
        if (!(ew instanceof TutorialActionExperimenterWindow))
            {
            new ErrorDialog("Wrong Experimenter Window.");
            return;
            }
  System.err.println("FTM");
System.err.flush();
        if (ew.getExpApp().getExptRunning())
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

            TutorialActionExperimenterWindow taew = (TutorialActionExperimenterWindow)ew;
            /* Possibly write some data to a temporary table for summing up earnings later */
            taew.savePayResults();
                    
            ew.getExpApp().startNextAction(ew);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }