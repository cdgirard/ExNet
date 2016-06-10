package girard.sc.be.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.be.awt.BENetworkActionClientWindow;
import girard.sc.be.awt.BEStaticCoalZapResWindow;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
/**
 * This message informs the clients who zapped and who didn't.  And Therefore
 * how to reduce earnings.
 * 
 * <p>
 * <br> Started: 07-25-2003
 * <p>
 * @author Dudley Girard
 */

public class BEVoteZapResultMsg extends ExptMessage 
    { 
    public BEVoteZapResultMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (!(cw instanceof BENetworkActionClientWindow))
            {
            new ErrorDialog("Wrong Client Window. - BEVoteZapResultMsg");
            return;
            }

        BENetworkActionClientWindow nacw = (BENetworkActionClientWindow)cw;

        Hashtable theVotes = (Hashtable)getArgs()[0];

        Enumeration enm = nacw.getNetwork().getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode tmpNode = (BENode)enm.nextElement();

            BENodeOrSubNet nos = (BENodeOrSubNet)tmpNode.getExptData("BENodeExchange");

            Boolean vote = (Boolean)theVotes.get(new Integer(tmpNode.getID()));
            nos.getCoalition().setZapped(vote.booleanValue());
            }

        BENode node = (BENode)nacw.getNetwork().getExtraData("Me");
        BENodeOrSubNet nos = (BENodeOrSubNet)node.getExptData("BENodeExchange");
        boolean windowFlag = false;
        if (nos.getCoalition().getCoalitionType().equals("Dynamic"))
            {
            
            }
        else if (nos.getCoalition().getCoalitionType().equals("Static"))
            {
System.err.println("FORMED: "+nos.getCoalition().getFormed()+" ZF: "+nos.getCoalition().areZappableFreeRiders(nos,nacw.getNetwork()));
            if ((nos.getCoalition().areZappableFreeRiders(nos,nacw.getNetwork())) && (nos.getCoalition().getFormed()))
                {
                nacw.addSubWindow(new BEStaticCoalZapResWindow(nacw));
                windowFlag = true;
                }
            else
                {
                windowFlag = false;
                nacw.setMessageLabel("Please wait while others are reading.");
                }
            }
        else
            {
            windowFlag = false;
            nacw.setMessageLabel("Please wait while others are reading.");
            }

        if (!windowFlag)
            {
            Object[] out_args = new Object[3];
            out_args[0] = new Integer(nos.getCoalition().getCoalition());
            out_args[1] = new String(nos.getCoalition().getCoalitionType());
            out_args[2] = new Vector();
            BECoalZapAckMsg tmp = new BECoalZapAckMsg(out_args);
            nacw.getSML().sendMessage(tmp);
            }

        nacw.repaint();
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: BE Vote Zap Result Message");
    
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
                        err_args[1] = new String("BEVoteZapResultMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new BEVoteZapResultMsg(args));
                    ec.sendToAllObservers(new BEVoteZapResultMsg(args));
                    }
                else
                    {
                    // Do nothing.
                    }
                return null; 
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BEVoteZapResultMsg");
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