package girard.sc.cc.io.msg;

/* Lets the subject and observer interfaces know that the round has
   actually started.  Causes the subject stations to start sending tick
   messages.

   Author: Dudley Girard
   Started: 5-12-2001
   Last Modified: 7-24-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.cc.awt.CCNetworkActionClientWindow;
import girard.sc.cc.awt.CCNetworkActionObserverWindow;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeResource;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

public class CCStartRoundMsg extends ExptMessage 
    { 
    public CCStartRoundMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (cw instanceof CCNetworkActionClientWindow)
            {
            CCNetworkActionClientWindow nacw = (CCNetworkActionClientWindow)cw;

            CCNetwork ccn = (CCNetwork)cw.getExpApp().getActiveAction();
            ccn.setExtraData("RoundRunning",new Boolean(true));
            ccn.initializeNetwork();
            CCNode me = (CCNode)ccn.getExtraData("Me");
            CCNodeResource nr = (CCNodeResource)me.getExptData("CCNodeResource");
            nacw.setPointsLabel(nr.getPointPool());
            nacw.setBankLabel(nr.getActiveBank());
            CCNode node = nacw.getArrow().getToNode();
            nacw.getArrow().setToNode(node);
            nacw.repaint();
            }
        else
            {
            new ErrorDialog("Wrong ClientWindow. - CCStartRoundMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CC Start Round Message");
    
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
                        err_args[1] = new String("CCStartRoundMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CCStartRoundMsg(args));
                    ec.sendToAllObservers(new CCStartRoundMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new CCStartRoundMsg(out_args));
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

// Should not get called
    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CCNetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CCStartRoundMsg");
            }

        if ((!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
                return;
        
        CCNetworkActionObserverWindow naow = (CCNetworkActionObserverWindow)ow;

        CCNetwork ccn = (CCNetwork)ow.getExpApp().getActiveAction();
        ccn.setExtraData("RoundRunning",new Boolean(true));
        ccn.getPeriod().setCurrentRound(ccn.getPeriod().getCurrentRound() + 1);
        naow.setRoundLabel(ccn.getPeriod().getCurrentRound());
        ccn.initializeNetwork();
        naow.repaint();
        }
    }