package girard.sc.ce.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.ce.awt.CENetworkActionClientWindow;
import girard.sc.ce.awt.CENetworkActionExperimenterWindow;
import girard.sc.ce.awt.CENetworkActionObserverWindow;
import girard.sc.ce.obj.CENetwork;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.EndExptReqMsg;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Vector;

/**
 * CEStopNetworkActionReqMsg: Used to stop a CENetworkAction when its in the middle
 * of being run.
 * <p>
 * <br> Started: 02-26-2003
 * <p>
 * @author Dudley Girard
 */

public class CEStopNetworkActionReqMsg extends ExptMessage 
    { 
    public CEStopNetworkActionReqMsg (Object args[])
        {
        super(args);
        }

/**
 * Sets m_exptStopping to true and sets RoundRunning to false.
 */
    public void getClientResponse(ClientWindow cw)
        {
  // We are stopping the experiment.
        cw.getExpApp().setExptStopping(true); 

        CENetworkActionClientWindow nacw = (CENetworkActionClientWindow)cw;
        CENetwork cen = (CENetwork)nacw.getNetwork();

        cen.setExtraData("RoundRunning",new Boolean(false));
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: Stop CE Network Action Request Message");
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    ec.sendToAllUsers(new CEStopNetworkActionReqMsg(args));
                    ec.sendToAllObservers(new CEStopNetworkActionReqMsg(args));
                    ec.addServerMessage(new CEStopNetworkActionReqMsg(args));
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
            err_args[1] = new String("CEStopNetworkActionReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
         ew.getExpApp().stopActiveSimActors();

         CENetworkActionExperimenterWindow naew = (CENetworkActionExperimenterWindow)ew;
         CENetwork cen = (CENetwork)ew.getExpApp().getActiveAction().getAction();

         /* Save gathered output data here */
        if (naew.saveOutputResults("ccDB",(Vector)cen.getExtraData("Data")))
            {
            Vector v = (Vector)cen.getExtraData("Data");
            v.removeAllElements();
            }
        else
            {
            /* Something bad happened. */
            }
        naew.savePayResults();

        EndExptReqMsg tmp = new EndExptReqMsg(null);
        ew.getSML().sendMessage(tmp);
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CEStopNetworkActionReqMsg");
            return;
            }    

        ow.getExpApp().setExptStopping(true);

        CENetworkActionObserverWindow naow = (CENetworkActionObserverWindow)ow;
        CENetwork cen = (CENetwork)naow.getNetwork();

        cen.setExtraData("RoundRunning",new Boolean(false));
        }
    }