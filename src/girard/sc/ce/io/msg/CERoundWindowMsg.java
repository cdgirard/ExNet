package girard.sc.ce.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.ce.awt.CENetworkActionClientWindow;
import girard.sc.ce.awt.CENetworkActionExperimenterWindow;
import girard.sc.ce.awt.CERoundWindow;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENetworkAction;
import girard.sc.ce.obj.CEPeriod;
import girard.sc.ce.obj.CEStateAction;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

/**
 * Message used to inform the clients to display the start round window
 * for a CE Network Action.
 * <p>
 * <br> Started: 02-10-2003
 * <p>
 *
 * @author Dudley Girard
 */

public class CERoundWindowMsg extends ExptMessage 
    { 
    public CERoundWindowMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (cw instanceof CENetworkActionClientWindow)
            {
            CENetworkActionClientWindow tmp = (CENetworkActionClientWindow)cw;
            tmp.setMessageLabel("");
            tmp.addSubWindow(new CERoundWindow(tmp));
            }
        else
            {
            new ErrorDialog("Wrong Client Window. - BERoundWindowMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

 // System.err.println("ESR: CE Round Window Message");
    
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
                        err_args[1] = new String("CERoundWindowMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CERoundWindowMsg(args));
                    ec.sendToAllObservers(new CERoundWindowMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new CERoundWindowMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CERoundWindowMsg");
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
            CENetworkActionExperimenterWindow naew = (CENetworkActionExperimenterWindow)ew;
            naew.setStartTime();
            CENetwork cen = (CENetwork)ew.getExpApp().getActiveAction().getAction();
            CEPeriod cep = cen.getActivePeriod();

            cen.setExtraData("RoundRunning",new Boolean(true));
            naew.setPeriodLabel(cen.getCurrentPeriod()+1);
            naew.setRoundLabel(cep.getCurrentRound()+1);

            cen.initializeNetwork();

            CEStartRoundMsg tmp1 = new CEStartRoundMsg(null);
            ew.getSML().sendMessage(tmp1);

            CETimeTckMsg tmp2 = new CETimeTckMsg(null);
            ew.getSML().sendMessage(tmp2);

            CEStateAction cesa = ((CENetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            if (cesa != null)
                cesa.executeAction(ew);

            naew.repaint();
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }