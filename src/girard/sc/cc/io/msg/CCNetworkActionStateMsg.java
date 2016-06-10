package girard.sc.cc.io.msg;

/* CCNetworkActionStateMsg: Sends the present state of a CCNetworkAction to
   an observer that has just joined in the middle of an experiment.

Author: Dudley Girard
Started: 7-24-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.cc.awt.CCNetworkActionObserverWindow;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.expt.obj.ObserverExptInfo;
import girard.sc.expt.web.ExptOverlord;

public class CCNetworkActionStateMsg extends ExptMessage 
    { 
    public CCNetworkActionStateMsg (Object args[])
        {
        super(args);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {  
// System.err.println("ESR: CC Network Action State Message");

        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {                    
                    ec.addObserverMessage(new CCNetworkActionStateMsg(this.getArgs()),(Integer)this.getArgs()[0]);
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment to get info on.");
            err_args[1] = new String("CCNetworkActionStateMsg");
            return new ExptErrorMsg(err_args);
            }

        Object[] err_args = new Object[2];
        err_args[0] = new String("Should not be here.");
        err_args[1] = new String("CCNetworkActionStateMsg");
        return new ExptErrorMsg(err_args);
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        Object[] args = this.getArgs();

        if (!(args[1] instanceof CCNetwork))
            {
            new ErrorDialog("Wrong argument type. - CCNetworkActionStateMsg");
            return;
            }

        ExptOverlord eo = ow.getEOApp();
        ObserverExptInfo oei = ow.getExpApp();
        ExptMessageListener ml = ow.getSML();

        oei.setReadyToStart(true);
        oei.setExptRunning(true);

        CCNetwork ccn = (CCNetwork)args[1];
        ccn.setExtraData("Index",args[0]);  // This is also stored in the ObserverExptInfo object as m_observerID
        oei.setActiveAction(ccn);  // In this case a CCNetwork
        ow.setWatcher(false);
        
        CCNetworkActionObserverWindow tmpWin = new CCNetworkActionObserverWindow(eo,oei,ml);

        if (ccn.getPeriod().getCurrentRound() > -1)
            {
            tmpWin.setRoundLabel(ccn.getPeriod().getCurrentRound());
            tmpWin.setTimeLabel(ccn.getPeriod().getCurrentTime());
            }
        }
    }