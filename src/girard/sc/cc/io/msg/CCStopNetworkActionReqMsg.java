package girard.sc.cc.io.msg;

/* CCStopNetworkActionReqMsg: Used to stop a CCNetworkAction when its in the middle
   of being run.

   Author: Dudley Girard
   Started: 7-24-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.cc.awt.CCNetworkActionClientWindow;
import girard.sc.cc.awt.CCNetworkActionExperimenterWindow;
import girard.sc.cc.awt.CCNetworkActionObserverWindow;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.EndExptReqMsg;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Vector;

public class CCStopNetworkActionReqMsg extends ExptMessage 
    { 
    public CCStopNetworkActionReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        cw.getExpApp().setExptStopping(true); // We are stopping the experiment.

        CCNetworkActionClientWindow nacw = (CCNetworkActionClientWindow)cw;
        CCNetwork ccn = (CCNetwork)nacw.getNetwork();

        ccn.setExtraData("RoundRunning",new Boolean(false));

        CCStopNetworkActionReqMsg tmp = new CCStopNetworkActionReqMsg(null);
        cw.getSML().sendMessage(tmp);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CC Stop Network Action Request Message");
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    ec.sendToAllUsers(new CCStopNetworkActionReqMsg(args));
                    ec.sendToAllObservers(new CCStopNetworkActionReqMsg(args));
                    return null; 
                    }
                else
                    {
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new CCStopNetworkActionReqMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CCStopNetworkActionReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];
        
        if (ew.getExpApp().getExptRunning())
            ew.getExpApp().setReady(true,index.intValue());

        boolean flag = true;
        for (int x=0;x<ew.getExpApp().getNumUsers();x++)
            {
// If they aren't registered anymore they sure aren't going to be sending any responses back.
            if ((!ew.getExpApp().getReady(x)) && (ew.getExpApp().getRegistered(x)))
                flag = false;
            }
        if (flag)
            {
            ew.getExpApp().stopActiveSimActors();
System.err.println("Done stopping SimActors");

            CCNetworkActionExperimenterWindow naew = (CCNetworkActionExperimenterWindow)ew;
            CCNetwork ccn = (CCNetwork)ew.getExpApp().getActiveAction().getAction();

         /* Save gathered output data here */
            if (naew.saveOutputResults("ccDB",(Vector)ccn.getExtraData("Data")))
                {
                Vector v = (Vector)ccn.getExtraData("Data");
                v.removeAllElements();
                }
            else
                {
                /* Something bad happened. */
                }

            naew.savePayResults();
System.err.println("Saved output results");
            EndExptReqMsg tmp = new EndExptReqMsg(null);
            ew.getSML().sendMessage(tmp);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CCNetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CCStopNetworkActionReqMsg");
            return;
            }    

        ow.getExpApp().setExptStopping(true);

        CCNetworkActionObserverWindow naow = (CCNetworkActionObserverWindow)ow;
        CCNetwork ccn = (CCNetwork)naow.getNetwork();

        ccn.setExtraData("RoundRunning",new Boolean(false));
        }
    }