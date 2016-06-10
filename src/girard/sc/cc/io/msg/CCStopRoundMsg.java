package girard.sc.cc.io.msg;

/* Lets the subjects and obeservers know that a round has ended.

Author: Dudley Girard
Started: 6-27-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.cc.awt.CCNetworkActionClientWindow;
import girard.sc.cc.awt.CCNetworkActionObserverWindow;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNetworkAction;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeResource;
import girard.sc.cc.obj.CCStateAction;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;

public class CCStopRoundMsg extends ExptMessage 
    { 
    public CCStopRoundMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (!(cw instanceof CCNetworkActionClientWindow))
            {
            new ErrorDialog("Wrong Client Window. - CCStopRoundMsg");
            return;
            }

        CCNetworkActionClientWindow nacw = (CCNetworkActionClientWindow)cw;

        CCNetwork ccn = (CCNetwork)cw.getExpApp().getActiveAction();
        ccn.setExtraData("RoundRunning",new Boolean(false));

        /* modifing earnings goes here */
        Enumeration enm = ccn.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CCNode node = (CCNode)enm.nextElement();
            CCNodeResource ccnr = (CCNodeResource)node.getExptData("CCNodeResource");
            ccnr.adjustEarnings();
            }

        CCNode me = (CCNode)ccn.getExtraData("Me");
        CCNodeResource nr = (CCNodeResource)me.getExptData("CCNodeResource");
        nacw.setBankLabel(nr.getActiveBank());
        nacw.setPointsLabel(nr.getAvailablePoints());

        CCStopRoundMsg tmp = new CCStopRoundMsg(null);
        cw.getSML().sendMessage(tmp);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CC Stop Round Message");
    
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
                        err_args[1] = new String("CCStopRoundMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CCStopRoundMsg(args));
                    ec.sendToAllObservers(new CCStopRoundMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new CCStopRoundMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CCStartRoundMsg");
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
            CCNetwork ccn = (CCNetwork)ew.getExpApp().getActiveAction().getAction();

        /* modifing earnings goes here */
            Enumeration enm = ccn.getNodeList().elements();
            while (enm.hasMoreElements())
                {
                CCNode node = (CCNode)enm.nextElement();
                CCNodeResource ccnr = (CCNodeResource)node.getExptData("CCNodeResource");
                ccnr.adjustEarnings();
                }

     // This would be where other things like zap voting could take place before the final end or could be the final end.
            CCStateAction ccsa = ((CCNetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            ccsa.executeAction(ew);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CCNetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CCStopRoundMsg");
            return;
            }

        if ((!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
            return;

        CCNetwork ccn = (CCNetwork)ow.getExpApp().getActiveAction();
        ccn.setExtraData("RoundRunning",new Boolean(false));

        /* modifing earnings goes here */
        Enumeration enm = ccn.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CCNode node = (CCNode)enm.nextElement();
            CCNodeResource ccnr = (CCNodeResource)node.getExptData("CCNodeResource");
            ccnr.adjustEarnings();
            }
        }
    }