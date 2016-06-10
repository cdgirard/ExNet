package girard.sc.cc.io.msg;

/* This message informs nodes that now is the time to send sanctions and pops
   up a window to faciltate the sending.  If the node has no sanctions to send
   then the node has to wait.

   Author: Dudley Girard
   Started: 7-9-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.cc.awt.CCNetworkActionClientWindow;
import girard.sc.cc.awt.CCNodeSanctionWindow;
import girard.sc.cc.obj.CCEdge;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeSanctions;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;

public class CCNodeSanctionWindowMsg extends ExptMessage 
    { 
    public CCNodeSanctionWindowMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (!(cw instanceof CCNetworkActionClientWindow))
            {
            new ErrorDialog("Wrong Client Window. - CCSanctionWindowMsg");
            return;
            }

        CCNetworkActionClientWindow nacw = (CCNetworkActionClientWindow)cw;

        Enumeration enm = nacw.getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge tmpEdge = (CCEdge)enm.nextElement();

            CCNode n1 = (CCNode)nacw.getNetwork().getNode(tmpEdge.getNode1());
            CCNode n2 = (CCNode)nacw.getNetwork().getNode(tmpEdge.getNode2());

            CCNodeSanctions ns1 = (CCNodeSanctions)n1.getExptData("CCNodeSanctions");
            CCNodeSanctions ns2 = (CCNodeSanctions)n2.getExptData("CCNodeSanctions");

            tmpEdge.setCompleted(false);

            if ((ns1.isEdgeActive(tmpEdge)) && (ns2.isEdgeActive(tmpEdge)))
                {
                tmpEdge.setActive(true);
                }
            else
                {
                tmpEdge.setActive(false);
                }
            }


        CCNode node = (CCNode)nacw.getNetwork().getExtraData("Me");
        CCNodeSanctions ns = (CCNodeSanctions)node.getExptData("CCNodeSanctions");
        if (ns.getSanctions().size() > 0)
            {
            nacw.addSubWindow(new CCNodeSanctionWindow(nacw));
            }
        nacw.repaint();
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CC Node Sanction Window Message");
    
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
                        err_args[1] = new String("CCNodeSanctionWindowMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CCNodeSanctionWindowMsg(args));
                    ec.sendToAllObservers(new CCNodeSanctionWindowMsg(args));
                    }
                return null; 
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CCNodeSanctionWindowMsg");
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