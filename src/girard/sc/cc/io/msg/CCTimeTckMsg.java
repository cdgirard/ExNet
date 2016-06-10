package girard.sc.cc.io.msg;

/* Lets subjects and observers know that another time tick has
   passed. Subjects respond back so the server knows when to 
   move time foward again.

Author: Dudley Girard
Started: 1-1-2001
Modified: 4-26-2001
Modified: 5-18-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.cc.awt.CCNetworkActionClientWindow;
import girard.sc.cc.awt.CCNetworkActionExperimenterWindow;
import girard.sc.cc.awt.CCNetworkActionObserverWindow;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

public class CCTimeTckMsg extends ExptMessage 
    { 
    public CCTimeTckMsg (Object args[])
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

            CCNetwork ccn = (CCNetwork)nacw.getExpApp().getActiveAction();
            ccn.getPeriod().setCurrentTime(ccn.getPeriod().getCurrentTime() - 1);
            nacw.setTimeLabel(ccn.getPeriod().getCurrentTime());
            CCTimeTckMsg tmp = new CCTimeTckMsg(null);
            cw.getSML().sendMessage(tmp);
            }
        else
            {
            new ErrorDialog("Wrong Client Window. - CCRoundWindowMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CC Time Tck Message");
    
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
                        err_args[1] = new String("CCTimeTckMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CCTimeTckMsg(args));
                    ec.sendToAllObservers(new CCTimeTckMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new CCTimeTckMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CCTimeTckMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];
        CCNetworkActionExperimenterWindow naew = (CCNetworkActionExperimenterWindow)ew;
        boolean[] tick = (boolean[])naew.getNetwork().getExtraData("TimeReady");
        Boolean rr = (Boolean)naew.getNetwork().getExtraData("RoundRunning");

        if ((!rr.booleanValue()) || (!ew.getExpApp().getExptRunning()) || (ew.getExpApp().getExptStopping()))
            return;

        tick[index.intValue()] = true;

        boolean flag = true;

        for (int x=0;x<ew.getExpApp().getNumUsers();x++)
            {
            if (!tick[x])
                flag = false;
            }
        if (flag)
            {
            for (int x=0;x<ew.getExpApp().getNumUsers();x++)
                {
                tick[x] = false;
                }

            CCNetwork ccn = (CCNetwork)ew.getExpApp().getActiveAction().getAction();
            ccn.getPeriod().setCurrentTime(ccn.getPeriod().getCurrentTime() - 1);
            naew.setTimeLabel(ccn.getPeriod().getCurrentTime());

            if (ccn.getPeriod().getCurrentTime() > 0)
                {
                CCTimeTckMsg tmp = new CCTimeTckMsg(null);
                ew.getSML().sendMessage(tmp);
                }
            else
                {
                ccn.setExtraData("RoundRunning",new Boolean(false));
                CCStopRoundMsg tmp = new CCStopRoundMsg(null);
                ew.getSML().sendMessage(tmp);
                }
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CCNetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - BETimeTckMsg");
            return;
            }

        CCNetworkActionObserverWindow naow = (CCNetworkActionObserverWindow)ow;
        Boolean rr = (Boolean)naow.getNetwork().getExtraData("RoundRunning");

        if ((!rr.booleanValue()) || (!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
                return;

        CCNetwork ccn = naow.getNetwork();
        ccn.getPeriod().setCurrentTime(ccn.getPeriod().getCurrentTime() - 1);
        naow.setTimeLabel(ccn.getPeriod().getCurrentTime());
        }
    }