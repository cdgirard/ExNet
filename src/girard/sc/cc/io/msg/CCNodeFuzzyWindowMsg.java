package girard.sc.cc.io.msg;

/* This message informs nodes that now is the time to send fuzzies and pops
   up a window to faciltate the sending.  If the node has no fuzzies to send
   the message simply sends a response back to the experimenter that the node
   is ready to continue.

   Author: Dudley Girard
   Started: 6-28-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.cc.awt.CCNetworkActionClientWindow;
import girard.sc.cc.awt.CCNodeFuzzyWindow;
import girard.sc.cc.obj.CCEdge;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeFuzzies;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;

public class CCNodeFuzzyWindowMsg extends ExptMessage 
    { 
    public CCNodeFuzzyWindowMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (!(cw instanceof CCNetworkActionClientWindow))
            {
            new ErrorDialog("Wrong Client Window. - CCRoundWindowMsg");
            return;
            }

        CCNetworkActionClientWindow nacw = (CCNetworkActionClientWindow)cw;

        Enumeration enm = nacw.getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge tmpEdge = (CCEdge)enm.nextElement();

            CCNode n1 = (CCNode)nacw.getNetwork().getNode(tmpEdge.getNode1());
            CCNode n2 = (CCNode)nacw.getNetwork().getNode(tmpEdge.getNode2());

            CCNodeFuzzies nf1 = (CCNodeFuzzies)n1.getExptData("CCNodeFuzzies");
            CCNodeFuzzies nf2 = (CCNodeFuzzies)n2.getExptData("CCNodeFuzzies");

            tmpEdge.setCompleted(false);

            if ((nf1.isEdgeActive(tmpEdge)) && (nf2.isEdgeActive(tmpEdge)))
                {
                tmpEdge.setActive(true);
                }
            else
                {
                tmpEdge.setActive(false);
                }
            }

        CCNode node = (CCNode)nacw.getNetwork().getExtraData("Me");
        CCNodeFuzzies nf = (CCNodeFuzzies)node.getExptData("CCNodeFuzzies");
        if (nf.getFuzzies().size() > 0)
            {
            nacw.addSubWindow(new CCNodeFuzzyWindow(nacw));
            }
        nacw.repaint();
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CC Node Fuzzy Window Message");
    
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
                        err_args[1] = new String("CCNodeFuzzyWindowMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CCNodeFuzzyWindowMsg(args));
                    ec.sendToAllObservers(new CCNodeFuzzyWindowMsg(args));
                    }
                return null; 
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CCNodeFuzzyWindowMsg");
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