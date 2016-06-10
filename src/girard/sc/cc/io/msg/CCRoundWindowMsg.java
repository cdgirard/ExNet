package girard.sc.cc.io.msg;

/* This message tells the clients to pop up a round window giving subjects
   information on the network.

   Author: Dudley Girard
   Started: 6-10-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.cc.awt.CCNetworkActionClientWindow;
import girard.sc.cc.awt.CCNetworkActionExperimenterWindow;
import girard.sc.cc.awt.CCRoundWindow;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNetworkAction;
import girard.sc.cc.obj.CCPeriod;
import girard.sc.cc.obj.CCStateAction;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

public class CCRoundWindowMsg extends ExptMessage 
    { 
    public CCRoundWindowMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (cw instanceof CCNetworkActionClientWindow)
            {
            CCNetworkActionClientWindow tmp = (CCNetworkActionClientWindow)cw;
            tmp.addSubWindow(new CCRoundWindow(tmp));
            }
        else
            {
            new ErrorDialog("Wrong Client Window. - CCRoundWindowMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CC Round Window Message");
    
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
                        err_args[1] = new String("CCRoundWindowMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CCRoundWindowMsg(args));
                    ec.sendToAllObservers(new CCRoundWindowMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new CCRoundWindowMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CCRoundWindowMsg");
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
            CCNetworkActionExperimenterWindow naew = (CCNetworkActionExperimenterWindow)ew;
            naew.setStartTime();
            CCNetwork ccn = (CCNetwork)ew.getExpApp().getActiveAction().getAction();
            CCPeriod ccp = ccn.getPeriod();

            ccn.setExtraData("RoundRunning",new Boolean(true));
            naew.setRoundLabel(ccp.getCurrentRound());

            ccn.initializeNetwork();

            CCStartRoundMsg tmp1 = new CCStartRoundMsg(null);
            ew.getSML().sendMessage(tmp1);

            CCTimeTckMsg tmp2 = new CCTimeTckMsg(null);
            ew.getSML().sendMessage(tmp2);

            CCStateAction ccsa = ((CCNetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            if (ccsa != null)
                ccsa.executeAction(ew);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }