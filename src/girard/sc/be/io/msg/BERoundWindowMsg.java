package girard.sc.be.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.be.awt.BENetworkActionClientWindow;
import girard.sc.be.awt.BENetworkActionExperimenterWindow;
import girard.sc.be.awt.BERoundWindow;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENetworkAction;
import girard.sc.be.obj.BEPeriod;
import girard.sc.be.obj.BEStateAction;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

public class BERoundWindowMsg extends ExptMessage 
    { 
    public BERoundWindowMsg (Object args[])
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
            tmp.getNetwork().setExtraData("CurrentState",new Double(1.0));
            tmp.setMessageLabel("");
            tmp.addSubWindow(new BERoundWindow(tmp));
            }
        else
            {
            new ErrorDialog("Wrong Client Window. - BERoundWindowMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

 // System.err.println("ESR: Round Window Message");
    
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
                        err_args[1] = new String("BERoundWindowMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new BERoundWindowMsg(args));
                    ec.sendToAllObservers(new BERoundWindowMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new BERoundWindowMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BERoundWindowMsg");
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
            naew.setStartTime();
            BENetwork ben = (BENetwork)ew.getExpApp().getActiveAction().getAction();
            BEPeriod bep = ben.getActivePeriod();

            ben.setExtraData("RoundRunning",new Boolean(true));
            naew.setPeriodLabel(ben.getCurrentPeriod()+1);
            naew.setRoundLabel(bep.getCurrentRound()+1);

            ben.initializeNetwork();

            BEStartRoundMsg tmp1 = new BEStartRoundMsg(null);
            ew.getSML().sendMessage(tmp1);

            BETimeTckMsg tmp2 = new BETimeTckMsg(null);
            ew.getSML().sendMessage(tmp2);

            BEStateAction besa = ((BENetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            if (besa != null)
                besa.executeAction(ew);

            naew.repaint();
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }