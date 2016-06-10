package girard.sc.be.io.msg;

/* Lets the subjects and observers know that the round has
   actually started.

Author: Dudley Girard
Started: 3-12-2001
Last Modified: 4-26-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.be.awt.BENetworkActionClientWindow;
import girard.sc.be.awt.BENetworkActionObserverWindow;
import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BEPeriod;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

public class BEStartRoundMsg extends ExptMessage 
    { 
    public BEStartRoundMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (cw instanceof BENetworkActionClientWindow)
            {
            BENetworkActionClientWindow nacw = (BENetworkActionClientWindow)cw;

            BENetwork ben = (BENetwork)cw.getExpApp().getActiveAction();
            ben.setExtraData("CurrentState",new Double(0));
            ben.setExtraData("RoundRunning",new Boolean(true));
            ben.initializeNetwork();
            nacw.setMessageLabel("");
            nacw.getArrow().setTmpKeep(-1);
            nacw.getArrow().setTmpGive(-1);
            BEEdge edge = nacw.getArrow().getEdge();
            nacw.getArrow().setEdge(edge);
            nacw.repaint();
            }
        else
            {
            new ErrorDialog("Wrong ClientWindow. - BEStartRoundMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: Start Round Message");
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
                        err_args[1] = new String("BEStartRoundMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new BEStartRoundMsg(args));
                    ec.sendToAllObservers(new BEStartRoundMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new BEStartRoundMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BEStartRoundMsg");
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

        if (!(ow instanceof BENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - BEStartRoundMsg");
            }

        if ((!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
                return;
        
        BENetworkActionObserverWindow naow = (BENetworkActionObserverWindow)ow;

        BENetwork ben = (BENetwork)ow.getExpApp().getActiveAction();
        ben.setExtraData("RoundRunning",new Boolean(true));
        BEPeriod bep = ben.getActivePeriod();
        bep.setCurrentRound(bep.getCurrentRound() + 1);
        bep.setCurrentTime(bep.getTime());
        naow.setPeriodLabel(ben.getCurrentPeriod());
        naow.setRoundLabel(bep.getCurrentRound());
        ben.initializeNetwork();
        naow.repaint();
        }
    }