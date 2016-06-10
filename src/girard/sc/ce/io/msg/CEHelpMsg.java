package girard.sc.ce.io.msg;

import girard.sc.ce.awt.CEHelpWindow;
import girard.sc.ce.awt.CENetworkActionExperimenterWindow;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

/**
 * Let the experimenter know that someone needs help.
 * <p>
 * <br> Started: 02-20-2003
 * <p>
 * @author Dudley Girard
 */

public class CEHelpMsg extends ExptMessage 
    { 
    public CEHelpMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CE Help Message");
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    // Should not go here.
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new CEHelpMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CEHelpMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];
        CENetworkActionExperimenterWindow naew = (CENetworkActionExperimenterWindow)ew;

        if ((!ew.getExpApp().getExptRunning()) || (ew.getExpApp().getExptStopping()))
            return;

        CEHelpWindow hw = new CEHelpWindow(naew,index.intValue());
        naew.addSubWindow(hw);
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }