package girard.sc.ce.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.ce.awt.CENetworkActionClientWindow;
import girard.sc.ce.awt.CENetworkActionExperimenterWindow;
import girard.sc.ce.awt.CENetworkActionObserverWindow;
import girard.sc.ce.awt.CEPeriodWindow;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENetworkAction;
import girard.sc.ce.obj.CEStateAction;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

/**
 * Lets the subjects and observers know that it is ready to
 * start the next period.  Creates a CEPeriodWindow for the subjects.
 * <p>
 * <br> Started: 02-20-2003
 * <p>
 * @author Dudley Girard
 */

public class CEStartNextPeriodMsg extends ExptMessage 
    { 
    public CEStartNextPeriodMsg (Object args[])
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
            tmp.addSubWindow(new CEPeriodWindow(tmp));
            }
        else
            {
            new ErrorDialog("Wrong Client Window. - CEStartNextPeriodMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CE Start Next Period Message");
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
                            err_args[1] = new String("CEStartNextPeriodMsg");
                            return new ExptErrorMsg(err_args);
                            }
                        }
                    ec.sendToAllUsers(new CEStartNextPeriodMsg(args));
                    ec.sendToAllObservers(new CEStartNextPeriodMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new CEStartNextPeriodMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CEStartNextPeriodMsg");
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
            CENetwork cen = (CENetwork)ew.getExpApp().getActiveAction().getAction();

            cen.getActivePeriod().setCurrentRound(0);
            cen.setExtraData("CurrentState",new Double(0));
            naew.setPeriodLabel(cen.getCurrentPeriod());

            CEStateAction cesa = ((CENetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            cesa.executeAction(ew);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CEStartNextPeriodMsg");
            return;
            }

        if (!ow.getExpApp().getExptRunning() || (ow.getExpApp().getExptStopping()))
            return;

        CENetworkActionObserverWindow naow = (CENetworkActionObserverWindow)ow;
        CENetwork cen = naow.getNetwork();

        cen.setCurrentPeriod(cen.getCurrentPeriod()+1);
        cen.getActivePeriod().setCurrentRound(0);
        naow.setPeriodLabel(cen.getCurrentPeriod());
        }
    }