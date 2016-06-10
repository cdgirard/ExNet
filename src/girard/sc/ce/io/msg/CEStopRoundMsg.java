package girard.sc.ce.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.ce.awt.CENetworkActionClientWindow;
import girard.sc.ce.awt.CENetworkActionExperimenterWindow;
import girard.sc.ce.awt.CENetworkActionObserverWindow;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENetworkAction;
import girard.sc.ce.obj.CENode;
import girard.sc.ce.obj.CENodeResource;
import girard.sc.ce.obj.CEStateAction;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;

/**
 * Lets the subjects and obeservers know that a round has ended.
 * <p>
 * <br> Started: 02-20-2003
 * <p>
 * @author Dudley Girard
 */

public class CEStopRoundMsg extends ExptMessage 
    { 
    public CEStopRoundMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (cw instanceof CENetworkActionClientWindow)
            {
            CENetwork cen = (CENetwork)cw.getExpApp().getActiveAction();
            cen.setExtraData("RoundRunning",new Boolean(false));

            /* modifing earnings goes here */
            Enumeration enm = cen.getNodeList().elements();
            while (enm.hasMoreElements())
                {
                CENode node = (CENode)enm.nextElement();
                CENodeResource nr = (CENodeResource)node.getExptData("CENodeResource");
                nr.adjustEarnings();
                }

            CEStopRoundMsg tmp = new CEStopRoundMsg(null);
            cw.getSML().sendMessage(tmp);
            }
        else
            {
            new ErrorDialog("Wrong Client Window. - CEStopRoundMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CE Stop Round Message");
// System.err.flush();
    
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
                        err_args[1] = new String("CEStopRoundMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CEStopRoundMsg(args));
                    ec.sendToAllObservers(new CEStopRoundMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new CEStopRoundMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CEStartRoundMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];
        
        if ((!ew.getExpApp().getExptRunning()) || (ew.getExpApp().getExptStopping()))
            return;

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
            CENetwork cen = (CENetwork)ew.getExpApp().getActiveAction().getAction();
            CENetworkActionExperimenterWindow naew = (CENetworkActionExperimenterWindow)ew;
       
       /* Updating all the earnings based on the NodeExchange rules. */
            Enumeration enm = cen.getNodeList().elements();
            while (enm.hasMoreElements())
                {
                CENode node = (CENode)enm.nextElement();
                CENodeResource nr = (CENodeResource)node.getExptData("CENodeResource");
                nr.adjustEarnings();
                }

     // This would be where other things like zap voting could take place before the final end.
            CEStateAction cesa = ((CENetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            cesa.executeAction(ew);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CEStopRoundMsg");
            return;
            }

        if ((!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
            return;

        CENetwork cen = (CENetwork)ow.getExpApp().getActiveAction();
        cen.setExtraData("RoundRunning",new Boolean(false));

        /* modifing earnings goes here */
        Enumeration enm = cen.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CENode node = (CENode)enm.nextElement();
            CENodeResource nr = (CENodeResource)node.getExptData("CENodeResource");
            nr.adjustEarnings();
            }
        }
    }