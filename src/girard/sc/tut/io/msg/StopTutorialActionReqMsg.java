package girard.sc.tut.io.msg;

/* StopTutorialActionReqMsg: Used to stop a BENetworkAction when its in the middle
   of being run.

Author: Dudley Girard
Started: 01-15-2002
*/

import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.EndExptReqMsg;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.tut.awt.TutorialActionExperimenterWindow;

public class StopTutorialActionReqMsg extends ExptMessage 
    { 
    public StopTutorialActionReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        cw.getExpApp().setExptStopping(true); // We are stopping the experiment.
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: Stop Tutorial Action Request Message");
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    ec.sendToAllUsers(new StopTutorialActionReqMsg(args));
                    ec.sendToAllObservers(new StopTutorialActionReqMsg(args));
                    ec.addServerMessage(new StopTutorialActionReqMsg(args));
                    return null; 
                    }
                else
                    {
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("StopTutorialActionReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        ew.getExpApp().stopActiveSimActors();
System.err.println("Done stopping SimActors");
System.err.flush();

        TutorialActionExperimenterWindow taew = (TutorialActionExperimenterWindow)ew;

         /* Save gathered output data here */
            // naew.saveOutputResults();
        taew.savePayResults();

        EndExptReqMsg tmp = new EndExptReqMsg(null);
        ew.getSML().sendMessage(tmp);
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

     //   if (!(ow instanceof TutorialActionObserverWindow))
      //      {
      //      new ErrorDialog("Wrong Observer Window. - StopTutorialActionReqMsg");
     //       return;
     //       }    

        ow.getExpApp().setExptStopping(true);
        }
    }