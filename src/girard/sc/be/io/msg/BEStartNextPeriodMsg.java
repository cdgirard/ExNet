package girard.sc.be.io.msg;

/* Lets the subjects and observers know that it is ready to
   start the next period.

Author: Dudley Girard
Started: 3-12-2001
Last Modified: 4-26-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.be.awt.BENetworkActionClientWindow;
import girard.sc.be.awt.BENetworkActionExperimenterWindow;
import girard.sc.be.awt.BENetworkActionObserverWindow;
import girard.sc.be.awt.BEPeriodWindow;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENetworkAction;
import girard.sc.be.obj.BEStateAction;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

public class BEStartNextPeriodMsg extends ExptMessage 
    { 
    public BEStartNextPeriodMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (cw instanceof BENetworkActionClientWindow)
            {
            BENetworkActionClientWindow tmp = (BENetworkActionClientWindow)cw;
            tmp.getNetwork().setExtraData("CurrentState",new Double(0));
            tmp.addSubWindow(new BEPeriodWindow(tmp));
            }
        else
            {
            new ErrorDialog("Wrong Client Window. - BEStartNextPeriodMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: Start Next Period Message");
// System.err.flush();
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    for (int x=0;x<ec.getNumUsers();x++)
                        {
                        if (!ec.getRegistered(x))
                            {
                            Object[] err_args = new Object[2];
                            err_args[0] = new String("Least one user not registered.");
                            err_args[1] = new String("BEStartNextPeriodMsg");
                            return new ExptErrorMsg(err_args);
                            }
                        }
                    ec.sendToAllUsers(new BEStartNextPeriodMsg(args));
                    ec.sendToAllObservers(new BEStartNextPeriodMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new BEStartNextPeriodMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BEStartNextPeriodMsg");
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
            BENetworkActionExperimenterWindow naew = (BENetworkActionExperimenterWindow)ew;
            BENetwork ben = (BENetwork)ew.getExpApp().getActiveAction().getAction();

            ben.getActivePeriod().setCurrentRound(0);
            ben.setExtraData("CurrentState",new Double(0));
            naew.setPeriodLabel(ben.getCurrentPeriod());

            BEStateAction besa = ((BENetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            besa.executeAction(ew);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof BENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - BEStartNextPeriodMsg");
            return;
            }

        if (!ow.getExpApp().getExptRunning() || (ow.getExpApp().getExptStopping()))
            return;

        BENetworkActionObserverWindow naow = (BENetworkActionObserverWindow)ow;
        BENetwork ben = naow.getNetwork();

        ben.setCurrentPeriod(ben.getCurrentPeriod()+1);
        ben.getActivePeriod().setCurrentRound(0);
        naow.setPeriodLabel(ben.getCurrentPeriod());
        }
    }