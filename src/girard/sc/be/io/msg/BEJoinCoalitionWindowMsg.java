package girard.sc.be.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.be.awt.BEJoinStaticCoalitionWindow;
import girard.sc.be.awt.BENetworkActionClientWindow;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

/**
 * This message informs nodes that now is the time to send sanctions and pops
 * up a window to faciltate the sending.  If the node has no sanctions to send
 * then the node has to wait.
 * <p>
 * <br> Started: 09-19-2002
 * <p>
 * @author Dudley Girard
 */

public class BEJoinCoalitionWindowMsg extends ExptMessage 
    { 
    public BEJoinCoalitionWindowMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (!(cw instanceof BENetworkActionClientWindow))
            {
            new ErrorDialog("Wrong Client Window. - BEJoinCoalitionWindowMsg");
            return;
            }

        BENetworkActionClientWindow nacw = (BENetworkActionClientWindow)cw;

        nacw.getNetwork().setExtraData("CurrentState",new Double(0.5));

        BENode node = (BENode)nacw.getNetwork().getExtraData("Me");
        BENodeOrSubNet nos = (BENodeOrSubNet)node.getExptData("BENodeExchange");
        if (nos.getCoalition().getCoalitionType().equals("Static"))
            {
            nacw.addSubWindow(new BEJoinStaticCoalitionWindow(nacw));
            }
        else if (nos.getCoalition().getCoalitionType().equals("Dynamic"))
            {
            }
        else
            {
            Object[] out_args = new Object[2];
            out_args[0] = new Integer(node.getID());
            out_args[1] = new Boolean(false);
            BEVoteJoinMsg tmp = new BEVoteJoinMsg(out_args);
            nacw.getSML().sendMessage(tmp);
            nacw.setMessageLabel("Please wait while others are deciding.");
            }
        nacw.repaint();
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: BE Join Coalition Window Message");
    
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
                        err_args[1] = new String("BEJoinCoalitionWindowMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new BEJoinCoalitionWindowMsg(args));
                    ec.sendToAllObservers(new BEJoinCoalitionWindowMsg(args));
                    }
                return null; 
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BEJoinCoalitionWindowMsg");
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