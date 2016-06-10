package girard.sc.be.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.be.awt.BEAfterSanctionWindow;
import girard.sc.be.awt.BENetworkActionClientWindow;
import girard.sc.be.obj.BENetworkAction;
import girard.sc.be.obj.BEStateAction;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

/**
 * This message informs nodes the outcome of all the sanctions that
 * were sent.
 * <p>
 * <br>Started: 09-15-2002
 * <p>
 * @author Dudley Girard
 */

public class BEAfterSanctionWindowMsg extends ExptMessage 
    { 
    public BEAfterSanctionWindowMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (!(cw instanceof BENetworkActionClientWindow))
            {
            new ErrorDialog("Wrong Client Window. - BEAfterSanctionWindowMsg");
            return;
            }

        BENetworkActionClientWindow nacw = (BENetworkActionClientWindow)cw;

        nacw.addSubWindow(new BEAfterSanctionWindow(nacw));
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: BE After Sanction Window Message");
    
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
                        err_args[1] = new String("BEAfterSanctionWindowMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new BEAfterSanctionWindowMsg(args));
                    ec.sendToAllObservers(new BEAfterSanctionWindowMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new BEAfterSanctionWindowMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BEAfterSanctionWindowMsg");
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
            BEStateAction besa = ((BENetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            besa.executeAction(ew);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }