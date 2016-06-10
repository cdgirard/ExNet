package girard.sc.cc.io.msg;

/* This message informs nodes that now is the time to send tokens and pops
   up a window to faciltate the sending.  If the node has no tokens to send
   then the node has to wait.

   Author: Dudley Girard
   Started: 7-6-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.cc.awt.CCNetworkActionClientWindow;
import girard.sc.cc.awt.CCNodeTokenWindow;
import girard.sc.cc.obj.CCEdge;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeTokens;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;

public class CCNodeTokenWindowMsg extends ExptMessage 
    { 
    public CCNodeTokenWindowMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (!(cw instanceof CCNetworkActionClientWindow))
            {
            new ErrorDialog("Wrong Client Window. - CCTokenWindowMsg");
            return;
            }

        CCNetworkActionClientWindow nacw = (CCNetworkActionClientWindow)cw;

        Enumeration enm = nacw.getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge tmpEdge = (CCEdge)enm.nextElement();

            CCNode n1 = (CCNode)nacw.getNetwork().getNode(tmpEdge.getNode1());
            CCNode n2 = (CCNode)nacw.getNetwork().getNode(tmpEdge.getNode2());

            CCNodeTokens nt1 = (CCNodeTokens)n1.getExptData("CCNodeTokens");
            CCNodeTokens nt2 = (CCNodeTokens)n2.getExptData("CCNodeTokens");

            tmpEdge.setCompleted(false);

            if ((nt1.isEdgeActive(tmpEdge)) && (nt2.isEdgeActive(tmpEdge)))
                {
                tmpEdge.setActive(true);
                }
            else
                {
                tmpEdge.setActive(false);
                }
            }

        CCNode node = (CCNode)nacw.getNetwork().getExtraData("Me");
        CCNodeTokens nt = (CCNodeTokens)node.getExptData("CCNodeTokens");
        if (nt.getTokens().size() > 0)
            {
            nacw.addSubWindow(new CCNodeTokenWindow(nacw));
            }
        nacw.repaint();
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CC Node Token Window Message");
    
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
                        err_args[1] = new String("CCNodeTokenWindowMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CCNodeTokenWindowMsg(args));
                    ec.sendToAllObservers(new CCNodeTokenWindowMsg(args));
                    }
                return null; 
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CCNodeTokenWindowMsg");
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