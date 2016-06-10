package girard.sc.be.io.msg;

/* BENetworkActionStateMsg: Sends the present state of a BENetworkAction to
   an observer that has just joined in the middle of an experiment.

Author: Dudley Girard
Started: 4-21-2001
Modified: 5-1-2001
Modified: 5-18-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.be.awt.BENetworkActionObserverWindow;
import girard.sc.be.obj.BENetwork;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.expt.obj.ObserverExptInfo;
import girard.sc.expt.web.ExptOverlord;

public class BENetworkActionStateMsg extends ExptMessage 
    { 
    public BENetworkActionStateMsg (Object args[])
        {
        super(args);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {  
// System.err.println("ESR: BE Network Action State Message");
// System.err.flush();

        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {                    
                    ec.addObserverMessage(new BENetworkActionStateMsg(this.getArgs()),(Integer)this.getArgs()[0]);
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment to get info on.");
            err_args[1] = new String("BENetworkActionStateMsg");
            return new ExptErrorMsg(err_args);
            }

        Object[] err_args = new Object[2];
        err_args[0] = new String("Should not be here.");
        err_args[1] = new String("BENetworkActionStateMsg");
        return new ExptErrorMsg(err_args);
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        Object[] args = this.getArgs();

        if (!(args[1] instanceof BENetwork))
            {
            new ErrorDialog("Wrong argument type. - BENetworkActionStateMsg");
            return;
            }

        ExptOverlord eo = ow.getEOApp();
        ObserverExptInfo oei = ow.getExpApp();
        ExptMessageListener ml = ow.getSML();

        oei.setReadyToStart(true);
        oei.setExptRunning(true);

        BENetwork ben = (BENetwork)args[1];
        ben.setExtraData("Index",args[0]);  // This is also stored in the ObserverExptInfo object as m_observerID
        oei.setActiveAction(ben);  // In this case a BENetwork
        ow.setWatcher(false);
        
        BENetworkActionObserverWindow tmpWin = new BENetworkActionObserverWindow(eo,oei,ml);

        if (ben.getCurrentPeriod() > -1)
            {
            tmpWin.setPeriodLabel(ben.getCurrentPeriod());
            tmpWin.setRoundLabel(ben.getActivePeriod().getCurrentRound());
            tmpWin.setTimeLabel(ben.getActivePeriod().getCurrentTime());
            }
        }
    }