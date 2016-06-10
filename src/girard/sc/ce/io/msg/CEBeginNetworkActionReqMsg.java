package girard.sc.ce.io.msg;

import girard.sc.ce.awt.CEExptStartWindow;
import girard.sc.ce.awt.CENetworkActionClientWindow;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.awt.Frame;
import java.util.Vector;

/**
 * The begin message for the CENetwork Action.  Gets the client screens going.
 * Sent only if the experimenter wanted to pause before starting the CENetworkAction.
 * <p>
 * <br> Started: 02-07-2003
 * <p>
 *
 * @author Dudley Girard
 */


public class CEBeginNetworkActionReqMsg extends ExptMessage 
    { 
    public CEBeginNetworkActionReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        CENetworkActionClientWindow nacw = (CENetworkActionClientWindow)cw;

        Vector v = nacw.getSubWindows();

        for (int i=0;i<v.size();i++)
            {
            Frame f = (Frame)v.elementAt(i);

            if (f instanceof CEExptStartWindow)
                {
                nacw.setMessageLabel("Please wait while others are reading.");
                CEStartNetworkActionReqMsg tmp = new CEStartNetworkActionReqMsg(null);
                nacw.getSML().sendMessage(tmp);
                nacw.removeSubWindow(f);
                return;
                }
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

 // System.err.println("ESR: CE Begin Network Action Request Message");
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    if (!ec.allRegistered())
                        {
                        Object[] err_args = new Object[2];
                        err_args[0] = new String("Least one user not registered.");
                        err_args[1] = new String("CEBeginNetworkActionReqMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CEBeginNetworkActionReqMsg(args));
                    ec.sendToAllObservers(new CEBeginNetworkActionReqMsg(args));
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
            err_args[1] = new String("CEBeginNetworkActionReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }