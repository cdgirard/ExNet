package girard.sc.ce.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.ce.awt.CENetworkActionObserverWindow;
import girard.sc.ce.obj.CENetwork;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.expt.obj.ObserverExptInfo;
import girard.sc.expt.web.ExptOverlord;

/**
 * CENetworkActionStateMsg: Sends the present state of a CENetworkAction to
 * an observer that has just joined in the middle of an experiment.
 * <p>
 * <br> Started: 02-26-2003
 * <p>
 * @author Dudley Girard
 */

public class CENetworkActionStateMsg extends ExptMessage 
    { 
    public CENetworkActionStateMsg (Object args[])
        {
        super(args);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {  
// System.err.println("ESR: CE Network Action State Message");
// System.err.flush();

        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {                    
                    ec.addObserverMessage(new CENetworkActionStateMsg(this.getArgs()),(Integer)this.getArgs()[0]);
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment to get info on.");
            err_args[1] = new String("CENetworkActionStateMsg");
            return new ExptErrorMsg(err_args);
            }

        Object[] err_args = new Object[2];
        err_args[0] = new String("Should not be here.");
        err_args[1] = new String("CENetworkActionStateMsg");
        return new ExptErrorMsg(err_args);
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        Object[] args = this.getArgs();

        if (!(args[1] instanceof CENetwork))
            {
            new ErrorDialog("Wrong argument type. - CENetworkActionStateMsg");
            return;
            }

        ExptOverlord eo = ow.getEOApp();
        ObserverExptInfo oei = ow.getExpApp();
        ExptMessageListener ml = ow.getSML();

        oei.setReadyToStart(true);
        oei.setExptRunning(true);

        CENetwork cen = (CENetwork)args[1];
// This is also stored in the ObserverExptInfo object as m_observerID
        cen.setExtraData("Index",args[0]);  
        oei.setActiveAction(cen);
        ow.setWatcher(false);
        
        CENetworkActionObserverWindow tmpWin = new CENetworkActionObserverWindow(eo,oei,ml);

        if (cen.getCurrentPeriod() > -1)
            {
            tmpWin.setPeriodLabel(cen.getCurrentPeriod());
            tmpWin.setRoundLabel(cen.getActivePeriod().getCurrentRound());
            tmpWin.setTimeLabel(cen.getActivePeriod().getCurrentTime());
            }
        }
    }