package girard.sc.ce.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.ce.awt.CEExptStartWindow;
import girard.sc.ce.awt.CENetworkActionClientWindow;
import girard.sc.ce.awt.CENetworkActionObserverWindow;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENetworkAction;
import girard.sc.ce.obj.CEStateAction;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.obj.ObserverExptInfo;
import girard.sc.expt.web.ExptOverlord;

/**
 * The start message for the CENetwork Action.
 * <p>
 * <br> Started: 01-31-2003
 * <p>
 * @author Dudley Girard
 */


public class CEStartNetworkActionReqMsg extends ExptMessage 
    { 
    public CEStartNetworkActionReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof CENetwork))
            {
            new ErrorDialog("Wrong argument type. - CEStartNetworkActionReqMsg");
            return;
            }

        ExptOverlord eo = cw.getEOApp();
        ClientExptInfo cei = cw.getExpApp();
        ExptMessageListener ml = cw.getSML();
        CENetwork cen = (CENetwork)args[0];

        cen.setCurrentPeriod(0);
        cen.getActivePeriod().setCurrentRound(0);
        cen.getActivePeriod().setCurrentTime(cen.getActivePeriod().getTime());
        cen.setExtraData("Index",new Integer(cei.getUserIndex()));
        cen.setExtraData("RoundRunning",new Boolean(false));
        cen.setExtraData("PntEarnedRound",new Double(0));
        cen.setExtraData("PntEarnedPeriod",new Double(0));
        cen.setExtraData("PntEarnedNetwork",new Double(0));
        cen.setExtraData("WindowType","Client");
        cei.setActiveAction(cen);  // In this case a CENetwork

        cw.setWatcher(false);
        
        CENetworkActionClientWindow nacw = new CENetworkActionClientWindow(eo,cei,ml);

        nacw.addSubWindow(new CEExptStartWindow(nacw));
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

 // System.err.println("ESR: CE Start Network Action Request Message");
    
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
                        err_args[1] = new String("CEStartNetworkActionReqMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CEStartNetworkActionReqMsg(args));
                    ec.sendToAllObservers(new CEStartNetworkActionReqMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new CEStartNetworkActionReqMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CEStartNetworkActionReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];
        
        if (ew.getExpApp().getExptRunning())
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
            CENetwork cen = (CENetwork)ew.getExpApp().getActiveAction().getAction();
            
            cen.setExtraData("CurrentState",new Double(0));  // What current place in the action is the experiment.
            cen.setExtraData("RoundRunning",new Boolean(false));  // Is the current round active flag.

            boolean[] tick = new boolean[ew.getExpApp().getNumUsers()];
            for (int x=0;x<tick.length;x++)
                {
                tick[x] = false;
                }
            cen.setExtraData("TimeReady",tick); // The flags for when to increment the tick time.
            
            cen.setCurrentPeriod(0);
            cen.getActivePeriod().setCurrentRound(0);

            CEStateAction cesa = ((CENetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            cesa.executeAction(ew);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        Object[] args = this.getArgs();

        if (!(args[0] instanceof CENetwork))
            {
            new ErrorDialog("Wrong argument type.");
            return;
            }

        ExptOverlord eo = ow.getEOApp();
        ObserverExptInfo oei = ow.getExpApp();
        ExptMessageListener ml = ow.getSML();
        CENetwork cen = (CENetwork)args[0];

        cen.setCurrentPeriod(0);
        cen.getActivePeriod().setCurrentRound(0);
        cen.getActivePeriod().setCurrentTime(cen.getActivePeriod().getTime());
        cen.setExtraData("Index",new Integer(oei.getObserverID()));
        cen.setExtraData("RoundRunning",new Boolean(false));

        double[] pen = new double[cen.getNumNodes()];
	double[] pren = new double[cen.getNumNodes()];
        for (int x=0;x<pen.length;x++)
            {
            pen[x] = 0;
	    pren[x] = 0;
            }
        cen.setExtraData("PntEarnedNetwork",pen);
        oei.setActiveAction(cen);

        ow.setWatcher(false);
        
        new CENetworkActionObserverWindow(eo,oei,ml);
        }
    }
