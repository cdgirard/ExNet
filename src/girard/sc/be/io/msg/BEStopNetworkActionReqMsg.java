package girard.sc.be.io.msg;

/* BEStopNetworkActionReqMsg: Used to stop a BENetworkAction when its in the middle
   of being run.

Author: Dudley Girard
Started: 3-15-2001
Modified: 5-1-2001
Modified: 5-18-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.be.awt.BENetworkActionClientWindow;
import girard.sc.be.awt.BENetworkActionExperimenterWindow;
import girard.sc.be.awt.BENetworkActionObserverWindow;
import girard.sc.be.obj.BENetwork;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.EndExptReqMsg;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Vector;

public class BEStopNetworkActionReqMsg extends ExptMessage 
    { 
    public BEStopNetworkActionReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        cw.getExpApp().setExptStopping(true); // We are stopping the experiment.

        BENetworkActionClientWindow nacw = (BENetworkActionClientWindow)cw;
        BENetwork ben = (BENetwork)nacw.getNetwork();

        ben.setExtraData("RoundRunning",new Boolean(false));

      //  BEStopNetworkActionReqMsg tmp = new BEStopNetworkActionReqMsg(null);
      //  cw.getSML().sendMessage(tmp);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: Stop Network Action Request Message");
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    ec.sendToAllUsers(new BEStopNetworkActionReqMsg(args));
                    ec.sendToAllObservers(new BEStopNetworkActionReqMsg(args));
                    ec.addServerMessage(new BEStopNetworkActionReqMsg(args));
                    return null; 
                    }
                else
                    {
                  //  Object[] out_args = new Object[1];
                  //  out_args[0] = new Integer(index);
                  //  ec.addServerMessage(new BEStopNetworkActionReqMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BEStopNetworkActionReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
     //   Integer index = (Integer)this.getArgs()[0];
        
      //  if (ew.getExpApp().getExptRunning())
      //      ew.getExpApp().setReady(true,index.intValue());

     //   boolean flag = true;
     //   for (int x=0;x<ew.getExpApp().getNumUsers();x++)
     //       {
// If they aren't registered anymore they sure aren't going to be sending any responses back.
     //       if ((!ew.getExpApp().getReady(x)) && (ew.getExpApp().getRegistered(x)))
    //            flag = false;
     //       }
     //   if (flag)
     //       {
            ew.getExpApp().stopActiveSimActors();
System.err.println("Done stopping SimActors");
System.err.flush();

            BENetworkActionExperimenterWindow naew = (BENetworkActionExperimenterWindow)ew;
            BENetwork ben = (BENetwork)ew.getExpApp().getActiveAction().getAction();

         /* Save gathered output data here */
            if (naew.saveOutputResults("beDB",(Vector)ben.getExtraData("Data")))
                {
                Vector v = (Vector)ben.getExtraData("Data");
                v.removeAllElements();
                }
            else
                {
                /* Something bad happened. */
                }
            naew.savePayResults();
System.err.println("Saved output results");
System.err.flush();
            EndExptReqMsg tmp = new EndExptReqMsg(null);
            ew.getSML().sendMessage(tmp);
     //       }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof BENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - BEStopNetworkActionReqMsg");
            return;
            }    

        ow.getExpApp().setExptStopping(true);

        BENetworkActionObserverWindow naow = (BENetworkActionObserverWindow)ow;
        BENetwork ben = (BENetwork)naow.getNetwork();

        ben.setExtraData("RoundRunning",new Boolean(false));
        }
    }