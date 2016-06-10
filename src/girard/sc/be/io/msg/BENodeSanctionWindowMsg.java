package girard.sc.be.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.be.awt.BENetworkActionClientWindow;
import girard.sc.be.awt.BENodeSanctionWindow;
import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeSanctions;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;

/**
 * This message informs nodes that now is the time to send sanctions and pops
 * up a window to faciltate the sending.  If the node has no sanctions to send
 * then the node has to wait.
 * <p>
 * <br> Started: 09-19-2002
 * <p>
 * @author Dudley Girard
 */

public class BENodeSanctionWindowMsg extends ExptMessage 
    { 
    public BENodeSanctionWindowMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (!(cw instanceof BENetworkActionClientWindow))
            {
            new ErrorDialog("Wrong Client Window. - BESanctionWindowMsg");
            return;
            }

        BENetworkActionClientWindow nacw = (BENetworkActionClientWindow)cw;

        Enumeration enm = nacw.getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge tmpEdge = (BEEdge)enm.nextElement();

            BENode n1 = (BENode)nacw.getNetwork().getNode(tmpEdge.getNode1());
            BENode n2 = (BENode)nacw.getNetwork().getNode(tmpEdge.getNode2());

            BENodeSanctions ns1 = (BENodeSanctions)n1.getExptData("BENodeSanctions");
            BENodeSanctions ns2 = (BENodeSanctions)n2.getExptData("BENodeSanctions");

            boolean tmpFlag = tmpEdge.getCompleted();
            tmpEdge.setCompleted(false);

            if ((ns1.isEdgeActive(tmpEdge)) && (ns2.isEdgeActive(tmpEdge)))
                {
                tmpEdge.setActive(true);
                }
            else
                {
                tmpEdge.setActive(false);
                }
            tmpEdge.setCompleted(tmpFlag);
            }


        BENode node = (BENode)nacw.getNetwork().getExtraData("Me");
        BENodeSanctions ns = (BENodeSanctions)node.getExptData("BENodeSanctions");
        if (ns.getSanctions().size() > 0)
            {
            nacw.addSubWindow(new BENodeSanctionWindow(nacw));
            }
        nacw.repaint();
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: BE Node Sanction Window Message");
    
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
                        err_args[1] = new String("BENodeSanctionWindowMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new BENodeSanctionWindowMsg(args));
                    ec.sendToAllObservers(new BENodeSanctionWindowMsg(args));
                    }
                return null; 
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BENodeSanctionWindowMsg");
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